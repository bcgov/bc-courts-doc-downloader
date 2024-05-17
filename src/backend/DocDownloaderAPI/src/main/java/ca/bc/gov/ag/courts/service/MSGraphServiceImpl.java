
package ca.bc.gov.ag.courts.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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


/**
 * @author 176899
 * 
 * https://learn.microsoft.com/en-us/graph/sdks/large-file-upload?tabs=java#upload-large-file-to-onedrive
 * https://learn.microsoft.com/en-us/graph/sdks/create-client?tabs=java
 * https://stackoverflow.com/questions/45609432/how-do-i-resolve-the-error-aadsts7000218-the-request-body-must-contain-the-foll
 * 
 */
@Service
public class MSGraphServiceImpl implements MSGraphService {

	private static final Logger logger = LoggerFactory.getLogger(MSGraphServiceImpl.class);
	
	@Autowired
    AppProperties props; 
	
	/**
	 * 
	 * Create upload session
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
		
		//ref: https://learn.microsoft.com/en-us/onedrive/developer/rest-api/api/driveitem_createuploadsession?view=odsp-graph-online#create-an-upload-session
		
		logger.debug("Calling createUploadSession...retry count: " + RetrySynchronizationManager.getContext().getRetryCount());
		logger.debug("Processing createUploadSession asynchronously with Thread {}", Thread.currentThread().getName());
    	
    	// Microsoft Graph user upload location
        URI uri = new URI(props.getMsgEndpointHost() + "v1.0/me/drive/root:/" + fileFolder + "/" + fileName + ":/createUploadSession");
        
        RestTemplate restTemplate = new RestTemplate();
        
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
			// For example: Content-Range: bytes 0-25/128
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

			if (responseCode >= 400) {
				logger.error("File upload chunk failure. Response: " + response);
				throw new Exception("Unexpected 40x error."); 
			} else {
				logger.debug("Chunk response: " + response);
			}
			
			return CompletableFuture.completedFuture(HttpClientHelper.processResponse(responseCode, response));

		} catch (Exception ex) {
			logger.error("Unexpected error at uploadChunk: " + ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}

	}

}
