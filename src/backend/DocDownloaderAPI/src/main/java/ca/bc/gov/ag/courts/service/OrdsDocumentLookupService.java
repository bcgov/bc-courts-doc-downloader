package ca.bc.gov.ag.courts.service;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.courts.Utils.AuthHelper;
import ca.bc.gov.ag.courts.api.model.OrdsPushResponse;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.exception.DownloaderException;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.model.OrdsHealthResponse;


@Service
public class OrdsDocumentLookupService {

	private static final Logger logger = LoggerFactory.getLogger(OrdsDocumentLookupService.class);

	private final RestTemplate restTemplate;
	
	private AppProperties props; 

	public OrdsDocumentLookupService(RestTemplateBuilder restTemplateBuilder, AppProperties props) {
		this.restTemplate = restTemplateBuilder.build();
		this.props = props; 
	}
	
	/**
	 * Call to PUT document on NFS
	 * 
	 * @param job
	 * @return
	 * @throws DownloaderException
	 */
	@Retryable(retryFor = RestClientException.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<OrdsPushResponse>> getFile(Job job) throws DownloaderException {

		logger.info("Calling ORDS getFile...retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		
		URIBuilder builder;
		try {
			builder = new URIBuilder(props.getOrdsSsgBaseUrl() + "/doc/push-to-server")
				    .addParameter("document_guid", new String(job.getGuid()))
				    .addParameter("application_id", props.getOrdsApplicationId())
				    .addParameter("app_password", props.getOrdsPassword())
				    .addParameter("user_name", props.getOrdsUsername())
				    .addParameter("database_id", props.getOrdsDatabaseId())
				    .addParameter("ticket_lifetime", props.getOrdsTicketLifetime())
				    .addParameter("putid",  props.getOrdsPutId())
				    .addParameter("server", props.getOrdsServer());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new DownloaderException("Error when composing ORDS push to server endpoint." + e.getMessage());
		}
		
		String url = builder.toString();
		
		logger.debug("Requesting file push to server with url: " + url);
		
		ResponseEntity<OrdsPushResponse> results = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<OrdsPushResponse>(AuthHelper.createBasicAuthHeaders(
						props.getOrdsSsgUsername(), props.getOrdsSsgPassword())), OrdsPushResponse.class);

		return CompletableFuture.completedFuture(results);

	}

// Keep this code in the event we need to batch process calls from the front end. 	
//	@Async
//	public void SendOrdsGetDocumentRequests(List<Job> jobs) throws URISyntaxException {
//		
//		String appTicket = null; 
//		
//		// Calls initialize first for the upcoming document request, async. 
//		for(Job job: jobs) {
//			try {
//				
//				MDC.put("threadId", job.getThreadId());
//				
//				job.setStarttime(System.currentTimeMillis());
//				CompletableFuture<ResponseEntity<InitializeResponse>> future = this.initializeNFSDocument(job);
//				future.get(); // wait for the thread to complete.
//				appTicket = future.get().getBody().getAppTicket();
//				job.setPercentageComplete("50");
//				job.setEndInitTime(System.currentTimeMillis());
//				DispatchOrdsResponse(job); // dispatch first half of request (init). 
//				
//				try { 
//					CompletableFuture<ResponseEntity<GetFileResponse>> future2 = this.getFile(job, appTicket);
//					ResponseEntity<GetFileResponse> _resp = future2.get();
//					job.setEndGetDocTime(System.currentTimeMillis());
//					job.setFileName(_resp.getBody().getFilename());
//					job.setMimeType(_resp.getBody().getMimeType());
//					logger.info("File name returned was " + _resp.getBody().getFilename());
//					job.setPercentageComplete("100");
//	
//				} catch (Exception e) {	
//					job.setEndGetDocTime(System.currentTimeMillis());
//					job.setError(true); // triggers error progress indicator bar.
//					job.setPercentageComplete("100");
//					String msg = "Error received when sending get document request. Error: " + e.getMessage();
//					job.setErrorMessage(msg);
//					logger.error(msg);
//					e.printStackTrace();
//				}
//				
//			} catch (Exception e) {	
//				job.setEndInitTime(System.currentTimeMillis());
//				job.setError(true); // triggers error progress indicator bar.
//				job.setPercentageComplete("100");
//				String msg = "Error received when sending initialize. Error: " + e.getMessage();
//				job.setErrorMessage(msg);
//				logger.error(msg);
//				e.printStackTrace();
//			} finally {
//				MDC.clear();
//			}
//			
//			DispatchOrdsResponse(job); // dispatch second half of request (getPOCFile).
//		}
//		
//	}
	
	@Async
	@Retryable(retryFor = RestClientException.class, maxAttemptsExpression = "${application.net.max.retries}", 
		backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<OrdsHealthResponse>> GetOrdsHealth() throws HttpClientErrorException {

		logger.info("Calling ORDS (Health Ping)...retry count " + RetrySynchronizationManager.getContext().getRetryCount());

		ResponseEntity<OrdsHealthResponse> resp = null;

		resp = restTemplate.exchange(props.getOrdsSsgBaseUrl() + "/ping", HttpMethod.GET,
				new HttpEntity<OrdsHealthResponse>(AuthHelper.createBasicAuthHeaders(
							props.getOrdsSsgUsername(), props.getOrdsSsgPassword())),
					OrdsHealthResponse.class);

		return CompletableFuture.completedFuture(resp);

	}

}
