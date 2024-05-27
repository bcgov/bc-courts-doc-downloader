package ca.bc.gov.ag.courts.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.courts.Utils.HttpClientHelper;
import ca.bc.gov.ag.courts.config.AppProperties;
import jakarta.annotation.PostConstruct;


/**
 * 
 * MS Graph Service methods 
 * 
 * @author 176899
 * 
 */
@Service
public class MSGraphServiceImpl implements MSGraphService {

	private static final Logger logger = LoggerFactory.getLogger(MSGraphServiceImpl.class);

	private AppProperties props;

	private final String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
			+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

	private Pattern emailPattern;
	
	public MSGraphServiceImpl (AppProperties props) {
		this.props = props;
	}

	@PostConstruct
	public void init() {
		emailPattern = Pattern.compile(emailRegex);
	}
	
	/**
	 * 
	 * Create upload session. Only works with a token derived using OAuth2 Authorization Code flow.  (e.g., token created 
	 * on behalf of the user - aka 'Delegated').  
	 * 
	 * @param accessToken
	 * @param fileFolder
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@Async
	@Retryable(retryFor = Exception.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<String> createUploadSession(String accessToken, String fileFolder, String fileName) throws Exception {
		
		logger.debug("Calling createUploadSession...retry count: " + RetrySynchronizationManager.getContext().getRetryCount());
		logger.debug("Processing createUploadSession asynchronously with Thread {}", Thread.currentThread().getName());
    	
        URI uri = new URI(props.getMsgEndpointHost() + "v1.0/me/drive/root:/" + fileFolder + "/" + fileName + ":/createUploadSession");
        
        RestTemplate restTemplate = new RestTemplate();
        
        // ref: https://learn.microsoft.com/en-us/graph/api/resources/driveitem?view=graph-rest-1.0 regarding behavior 
        String jsonBody = "{\"item\":{\"@microsoft.graph.conflictBehavior\": \"replace\",\"name\": \"" + fileName + "\"}}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");   
        headers.set("Content-Length", Integer.toString(jsonBody.length()));   
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/json");
        
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
        
        ResponseEntity<String> response = null;
        try {
        	response = restTemplate.postForEntity(uri, request, String.class); 
        } catch (RestClientException rce) {
        	logger.error(rce.getMessage());
        	throw new Exception(rce);
        }
        
        HttpStatusCode statusCode = response.getStatusCode();
        
        if(statusCode.is2xxSuccessful()) {  
          
	        JSONObject responseObject = HttpClientHelper.processResponse(statusCode.value(), response.getBody());
	        return CompletableFuture.completedFuture(responseObject.getJSONObject("responseMsg").getString("uploadUrl"));
        
        } else {
        	
        	 JSONObject responseObject = HttpClientHelper.processResponse(statusCode.value(), response.getBody());
        	 String errorMessage = "Error creating upload session: " + responseObject.getJSONObject("responseMsg").getString("message");
        	 logger.error(errorMessage);
        	 throw new Exception(errorMessage);
        }
		
	}
	
	/**
	 * 
	 * Create upload session with User Id. Only works with a token derived using OAuth2 Client Credentials flow.  (e.g., token created 
	 * on behalf of the application (e.g., none delegated)). 
	 * 
	 * @param accessToken
	 * @param userId
	 * @param fileFolder
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@Async
	@Retryable(retryFor = Exception.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<String> createUploadSessionFromUserId(String accessToken, String userId, String fileFolder,
				String fileName) throws Exception {	

		logger.debug("Calling createUploadSessionFromUserId...retry count: "
				+ RetrySynchronizationManager.getContext().getRetryCount());
		logger.debug("Processing createUploadSessionFromUserId asynchronously with Thread {}",
				Thread.currentThread().getName());

		URI uri = new URI(props.getMsgEndpointHost() + "v1.0/users/" + userId + "/drive/root:/" + fileFolder + "/"
				+ fileName + ":/createUploadSession");

		RestTemplate restTemplate = new RestTemplate();

		// ref: https://learn.microsoft.com/en-us/graph/api/resources/driveitem?view=graph-rest-1.0 regarding behavior
		String jsonBody = "{\"item\":{\"@microsoft.graph.conflictBehavior\": \"replace\",\"name\": \"" + fileName + "\"}}";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Content-Length", Integer.toString(jsonBody.length()));
		headers.set("Authorization", "Bearer " + accessToken);
		headers.set("Accept", "application/json");

		HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

		ResponseEntity<String> response = null;
		try {
			response = restTemplate.postForEntity(uri, request, String.class);
		} catch (RestClientException rce) {
			logger.error(rce.getMessage());
			throw new Exception(rce);
		}

		HttpStatusCode statusCode = response.getStatusCode();

		if (statusCode.is2xxSuccessful()) {

			JSONObject responseObject = HttpClientHelper.processResponse(statusCode.value(), response.getBody());
			return CompletableFuture
					.completedFuture(responseObject.getJSONObject("responseMsg").getString("uploadUrl"));

		} else {

			JSONObject responseObject = HttpClientHelper.processResponse(statusCode.value(), response.getBody());
			String errorMessage = "Error creating upload session from email: " + responseObject.getJSONObject("responseMsg").getString("message");
			logger.error(errorMessage);
			throw new Exception(errorMessage);
		}
	}
		
	/**
	 * 
	 * Upload a Chunk
	 * 
	 * @param uploadUrl
	 * @param count
	 * @param fileSize
	 * @param chunk
	 * @param fragSize
	 * @param chunkSize
	 * @return
	 * @throws Exception
	 * 
	 * Note: Presently this method can not use Spring RestTemplate for uploading file content. 
	 */
	@Async
	@Retryable(retryFor = Exception.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<JSONObject> uploadChunk(String uploadUrl, int count, long fileSize, byte[] chunk, int fragSize, int chunkSize) throws Exception {

		logger.debug("Processing uploadChunk asynchronously with Thread {}", Thread.currentThread().getName());
		logger.debug("Calling uploadChunk...retry count: " + RetrySynchronizationManager.getContext().getRetryCount());
		
		try {

			HttpURLConnection uploadConnection = (HttpURLConnection) new URL(uploadUrl).openConnection();
			uploadConnection.setRequestMethod("PUT");
			uploadConnection.setRequestProperty("Accept", "application/json"); // a must otherwise 400 bad requests will occur.
			uploadConnection.setRequestProperty("Content-Length", Integer.toString(chunk.length));

			// The Content-Range header must be set for each fragment. 
			// For example: Content-Range: bytes 0-25/128 for bytes 0 through 25 out of 128
			String range = "bytes start-end/fileSize";

			range = StringUtils.replace(range, "start", Integer.toString(count * fragSize));
			range = StringUtils.replace(range, "end", Integer.toString(count * fragSize + chunkSize - 1));
			range = StringUtils.replace(range, "fileSize", Long.toString(fileSize));

			logger.debug("Uploading content-range: " + range);

			uploadConnection.setRequestProperty("Content-Range", range);
			uploadConnection.setDoOutput(true);
			OutputStream outputStream = uploadConnection.getOutputStream();
			outputStream.write(chunk);
			outputStream.flush();
			outputStream.close();

			String response = HttpClientHelper.getResponseStringFromConn(uploadConnection);
			int responseCode = uploadConnection.getResponseCode();

			if (!HttpStatus.valueOf(responseCode).is2xxSuccessful()) {
				logger.error("File upload chunk failure. Response: " + response);
				throw new Exception("Unexpected http status code received when uploading chunk: " + responseCode); 
			} else {
				logger.debug("Chunk response: " + response);
			}
			
			return CompletableFuture.completedFuture(HttpClientHelper.processResponse(responseCode, response));

		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}

	}
	
	/**
	 * getUserId from email address. 
	 * 
	 * App must have the following role(s) set to execute the 'users' search operation: User.Read.All.  
	 * 
	 * @param accessToken
	 * @param email
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 */
	@Async
	public CompletableFuture<JSONObject> GetUserId(String accessToken, String email) throws MalformedURLException, IOException, JSONException {
	
		if (!validateEmail(email)) {
			
			String jError = "{\"error\": {\"code\": \"Invalid Email format\",\"message\": \"Invalid Email format\"}}"; 
			
			return CompletableFuture.completedFuture(HttpClientHelper.processResponse(HttpStatus.BAD_REQUEST.value(), jError)); 
		}

		String useridQuery = props.getMsgEndpointHost() + "v1.0/users('" + email + "')";

		HttpURLConnection connection = null;
		URL url = new URL(useridQuery);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET"); 
	    connection.setRequestProperty("Authorization", "Bearer " + accessToken);
	    connection.setRequestProperty("Accept", "*/*");
	    
	    int responseCode = connection.getResponseCode();
		String response = HttpClientHelper.getResponseStringFromConn(connection);
		
		if (!HttpStatus.valueOf(responseCode).is2xxSuccessful()) {
			logger.error("GetUserId failure. Response: " + response);
			return CompletableFuture.completedFuture(HttpClientHelper.processResponse(responseCode, response)); 
		} else {
			logger.debug("GetUserId response: " + response);
		}

		return CompletableFuture.completedFuture(HttpClientHelper.processResponse(responseCode, response));

	}
	
	/**
	 * 
	 * Utility function to validate email format. 
	 * 
	 * @param email
	 * @return
	 */
	private boolean validateEmail(String email) {
		Matcher matcher = emailPattern.matcher(email);
	    return matcher.matches();
	}

}
