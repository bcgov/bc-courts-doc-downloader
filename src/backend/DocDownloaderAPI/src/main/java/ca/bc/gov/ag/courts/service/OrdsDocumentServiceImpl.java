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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.courts.Utils.AuthHelper;
import ca.bc.gov.ag.courts.api.model.OrdsPushResponse;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.exception.DownloaderException;
import ca.bc.gov.ag.courts.handler.GenericErrorHandler;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.model.OrdsHealthResponse;

/**
 * 
 * Ords Document Service 
 * 
 * @author 176899
 * 
 */
@Service
public class OrdsDocumentServiceImpl implements OrdsDocumentService {

	private static final Logger logger = LoggerFactory.getLogger(OrdsDocumentServiceImpl.class);

	private final RestTemplate restTemplate;
	
	private AppProperties props; 

	public OrdsDocumentServiceImpl(RestTemplateBuilder restTemplateBuilder, AppProperties props) {
		this.restTemplate = restTemplateBuilder
				.errorHandler(new GenericErrorHandler()) 
				.build(); 
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
	public CompletableFuture<ResponseEntity<OrdsPushResponse>> pushFile(Job job) throws DownloaderException {

		logger.info("ORDS Service: Calling ORDS getFile...retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		
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
	
	@Retryable(retryFor = RestClientException.class, maxAttemptsExpression = "${application.net.max.retries}", 
		backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<OrdsHealthResponse>> getOrdsHealth() throws HttpClientErrorException {

		logger.info("ORDS Service: Calling ORDS (Health Ping)...retry count " + RetrySynchronizationManager.getContext().getRetryCount());

		ResponseEntity<OrdsHealthResponse> resp = null;

		resp = restTemplate.exchange(props.getOrdsSsgBaseUrl() + "/ping", HttpMethod.GET,
				new HttpEntity<OrdsHealthResponse>(AuthHelper.createBasicAuthHeaders(
							props.getOrdsSsgUsername(), props.getOrdsSsgPassword())),
					OrdsHealthResponse.class);

		return CompletableFuture.completedFuture(resp);

	}

}
