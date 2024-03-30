package ca.bc.gov.ag.courts.service;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.model.GetFileResponse;
import ca.bc.gov.ag.courts.model.InitializeRequest;
import ca.bc.gov.ag.courts.model.InitializeResponse;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.model.OrdsHealthResponse;

@Service
public class OrdsDocumentLookupService {

	private static final Logger logger = LoggerFactory.getLogger(OrdsDocumentLookupService.class);

	private final RestTemplate restTemplate;
	
	@Autowired
	private AppProperties props; 

	public OrdsDocumentLookupService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder
				.build();
	}

	
	/**
	 * 
	 * Call to PUT document on NFS
	 * 
	 * @param documentGuid
	 * @param appTicket
	 * @return
	 */
	private CompletableFuture<ResponseEntity<GetFileResponse>> getFile(Job job, String appTicket) {
		
		ResponseEntity<GetFileResponse> results = null; 
		
		try {
		
			// TODO - discuss this endpoint with Bron. 
			String getEndpoint = props.getOrdsEndpoint() + "/getFilePoc?AppTicket=%s" + 
												"&ObjectGuid=%s" + 
												"&TicketLifeTime=%s" + 
												"&PutId=SCVPOC";
			
			// The base64 document guid has to be additionally URL escaped as it's sent as a param to a RESTful ORDS operation.  
			String htmlEscapedBase64Guid = null; 
			try {
				htmlEscapedBase64Guid = encodeValue(job.getDocGuid());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			getEndpoint = String.format(getEndpoint, appTicket, htmlEscapedBase64Guid, props.getTicLifeTime());
			
			logger.info("Calling ORDS getFile...");
			
			results = restTemplate.exchange(getEndpoint, HttpMethod.GET, 
						new HttpEntity<GetFileResponse>(createHeaders()), GetFileResponse.class);				
	    
			logger.info("Success.");
			
			return CompletableFuture.completedFuture(results);

		} catch (Exception ex) {
			logger.error("GetFile call resulted in an error. Message: " + ex.getMessage());
			throw ex;
		} 
	}
	
	/**
	 * Utility function to apply url encoding to a string. 
	 * 
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String encodeValue(String value) throws UnsupportedEncodingException {
	    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	}
	
	/**
	 * 
	 * Initializes the next POC putFile call.  
	 * 
	 * @return
	 */
	@Retryable(retryFor = RestClientException.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	private CompletableFuture<ResponseEntity<InitializeResponse>> initializeNFSDocument(Job job) throws InterruptedException {
		
		ResponseEntity<InitializeResponse> resp = null;
		try {
			
			HttpEntity<InitializeRequest> body = new HttpEntity<InitializeRequest>(
								new InitializeRequest(props.getAppId(), props.getAppPwd(), props.getTicLifeTime()), createHeaders());
			
			logger.info("Calling ORDS initialize...");
			
			resp = restTemplate.exchange(props.getOrdsEndpoint() + "/initialize",
										HttpMethod.POST,
										body,
										InitializeResponse.class);
			
			logger.info("Success.");
			
			return CompletableFuture.completedFuture(resp);
		
		} catch (HttpStatusCodeException ex) {
			logger.error("Initialize call resulted in an HTTP Status code response of " + ex.getStatusText());
			throw ex;
		} 

	}

	@Async
	@Retryable(retryFor = RestClientException.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void SendOrdsGetDocumentRequests(List<Job> jobs) throws URISyntaxException {
		
		String appTicket = null; 
		
		// Calls initialize first for the upcoming document request, async. 
		for(Job job: jobs) {
			try {
				
				MDC.put("threadId", job.getThreadId());
				
				job.setStarttime(System.currentTimeMillis());
				CompletableFuture<ResponseEntity<InitializeResponse>> future = this.initializeNFSDocument(job);
				future.get(); // wait for the thread to complete.
				appTicket = future.get().getBody().getAppTicket();
				job.setPercentageComplete("50");
				job.setEndInitTime(System.currentTimeMillis());
				DispatchOrdsResponse(job); // dispatch first half of request (init). 
				
				try { 
					CompletableFuture<ResponseEntity<GetFileResponse>> future2 = this.getFile(job, appTicket);
					ResponseEntity<GetFileResponse> _resp = future2.get();
					job.setEndGetDocTime(System.currentTimeMillis());
					job.setFileName(_resp.getBody().getFilename());
					job.setMimeType(_resp.getBody().getMimeType());
					logger.info("File name returned was " + _resp.getBody().getFilename());
					job.setPercentageComplete("100");
	
				} catch (Exception e) {	
					job.setEndGetDocTime(System.currentTimeMillis());
					job.setError(true); // triggers error progress indicator bar.
					job.setPercentageComplete("100");
					String msg = "Error received when sending get document request. Error: " + e.getMessage();
					job.setErrorMessage(msg);
					logger.error(msg);
					e.printStackTrace();
				}
				
			} catch (Exception e) {	
				job.setEndInitTime(System.currentTimeMillis());
				job.setError(true); // triggers error progress indicator bar.
				job.setPercentageComplete("100");
				String msg = "Error received when sending initialize. Error: " + e.getMessage();
				job.setErrorMessage(msg);
				logger.error(msg);
				e.printStackTrace();
			} finally {
				MDC.clear();
			}
			
			DispatchOrdsResponse(job); // dispatch second half of request (getPOCFile).
		}
		
	}
	
	@Async
	@Retryable(retryFor = RestClientException.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public CompletableFuture<ResponseEntity<OrdsHealthResponse>> GetOrdsHealth() throws HttpClientErrorException {

		logger.info("Calling ORDS Health...retry count " + RetrySynchronizationManager.getContext().getRetryCount());

		ResponseEntity<OrdsHealthResponse> resp = null;

		resp = restTemplate.exchange(props.getOrdsEndpoint() + "/health", HttpMethod.GET,
				new HttpEntity<OrdsHealthResponse>(createHeaders()), OrdsHealthResponse.class);

		return CompletableFuture.completedFuture(resp);

	}
	
	/**
	 * 
	 * Dispatch data to front end.
	 * 
	 * @param threadId
	 * @throws URISyntaxException
	 */
	private void DispatchOrdsResponse(Job job) throws URISyntaxException {
		
		//TODO - This method needs to dispatch back to the main controller to 
		// initiate the MS Graph calls and push the file to OneDrive. 
		
	}
	
	/**
	 * Generates Basic Auth Header. 
	 * 
	 * @return
	 */
	private HttpHeaders createHeaders() {
		return new HttpHeaders() {
			private static final long serialVersionUID = -9217317753759432107L;
			{
				String auth = props.getOrdsUserName() + ":" + props.getOrdsPassword();
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(), false);
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}
}
