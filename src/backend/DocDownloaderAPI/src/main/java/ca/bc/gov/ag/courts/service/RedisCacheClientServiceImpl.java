package ca.bc.gov.ag.courts.service;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import ca.bc.gov.ag.courts.Utils.InetUtils;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.handler.GenericErrorHandler;
import ca.bc.gov.ag.courts.model.Job;
import jakarta.annotation.PostConstruct;


/**
 * Redis Cache Client Service Interface
 * 
 * @author 176899
 *
 */
@Service
public class RedisCacheClientServiceImpl implements RedisCacheClientService {
	
	Logger logger = LoggerFactory.getLogger(RedisCacheClientServiceImpl.class);
	
	private final Environment environment;
	private final RestTemplate restTemplate;
	private final AppProperties props; 
	private String redisUrl;
	
	public RedisCacheClientServiceImpl(Environment environment, RestTemplateBuilder restTemplateBuilder, AppProperties props) {
		this.environment = environment; 
		this.restTemplate = restTemplateBuilder
				.errorHandler(new GenericErrorHandler()) 
				.build(); 
		this.props = props; 
		
	}
	
	@PostConstruct
	public void init() throws UnknownHostException {
		
		boolean unitTesting = Arrays.asList(this.environment.getActiveProfiles()).contains("test");
		
		if (!unitTesting) {
			redisUrl = "http://" + InetUtils.getIPForHostname(props.getRedisClientHost()) + ":" + props.getRedisClientPort() + "/";
			logger.info("Redis server DNS set for Redis Client.");
		} else { 
			// Set the redisUrl to a dummy host and port. unlike the above, in this case we don't want to resolve the IP for the host name as it will error. 
			redisUrl = "http://localhost:6379/";
			logger.info("Set Redis server to dummy value while profile is 'test'.");
		}
	}
	
	@Override
	@Retryable(retryFor = RestClientException.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<Job[]>> getJobs() throws Exception {
		
		logger.debug("Redis Service: Calling getJobs... retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		URI uri = new URI(this.redisUrl + "jobs");

		HttpEntity<String> entity = new HttpEntity<String>(
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<Job[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, Job[].class);

		return CompletableFuture.completedFuture(responseEntity);
	}

	@Override
	@Retryable(retryFor = RestClientException.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<String>> createJob(Job job) throws Exception {
		
		logger.debug("Redis Service: Calling createJob... retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		URI uri = new URI(this.redisUrl + "job");

		HttpEntity<Job> entity = new HttpEntity<Job>(job,
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, entity, String.class);

		return CompletableFuture.completedFuture(responseEntity);
	}

	@Override
	@Retryable(retryFor = RestClientException.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<String>> updateJob(Job job) throws Exception {
		
		logger.debug("Redis Service: Calling updateJob... retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		URI uri = new URI(this.redisUrl + "job");

		HttpEntity<Job> entity = new HttpEntity<Job>(job,
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		
		ResponseEntity<String> responseEntity = null;
		try {
			
			responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
			
		} catch (HttpClientErrorException ce) {
			
			// If the response is a 404, or 400 from Redis Client, just return and don't continue to retry. 
			// This will occur when a download is terminated while still running.  
			if (HttpStatus.NOT_FOUND == ce.getStatusCode() || HttpStatus.BAD_REQUEST == ce.getStatusCode()) {
				return CompletableFuture.completedFuture(
						new ResponseEntity<String>("Not found", HttpStatus.NOT_FOUND));
			} else {
				throw ce;
			}
		}	

		return CompletableFuture.completedFuture(responseEntity);
	}

	@Override
	@Retryable(retryFor = RestClientException.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<String>> deleteJob(String jobId) throws Exception {
		
		logger.debug("Redis Service: Calling deleteJob... retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		URI uri = new URI(this.redisUrl + "job/" + jobId);

		HttpEntity<Job> entity = new HttpEntity<Job>(
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<String> responseEntity = null;
		try {
			
			responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);
		
		} catch (HttpClientErrorException ce) {
				
			// If the response is a 404 from Redis Client, just return and don't continue to retry.
			// This will occur when a download is terminated while still running. 
			if (HttpStatus.NOT_FOUND == ce.getStatusCode()) {
				return CompletableFuture.completedFuture(
						new ResponseEntity<String>("Not found", HttpStatus.NOT_FOUND));
			} else {
				throw ce;
			}
		}

		return CompletableFuture.completedFuture(responseEntity);
		
	}
	
	/**
	 * The following is the only Async method as it is called directly from the document controller. All other methods in this service are called from the 
	 * Async Job processor which makes them Async automatically. 
	 */
	@Override
	@Async			
	@Retryable(retryFor = Exception.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<Job>> getJob(String jobId) throws Exception {
		
		logger.debug("Redis Service: Calling getJob... retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		
		URI uri = new URI(this.redisUrl + "jobs/" + jobId);

		HttpEntity<String> entity = new HttpEntity<String>(
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<Job> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, Job.class);
		} catch (Exception ex) {
			
			// This block watches for 404 response - We don't want this status code to cause the multiple retries and 
			// hold up the response to the client. Simply return a 404 and do no further processing. 
			if (ex instanceof HttpClientErrorException) {
				HttpClientErrorException _ex = (HttpClientErrorException)ex;
				if (HttpStatus.NOT_FOUND == _ex.getStatusCode()) {
					return CompletableFuture.completedFuture(new ResponseEntity<Job>(HttpStatus.NOT_FOUND));
				}
			} else {
				throw ex; 
			}
		}

		return CompletableFuture.completedFuture(responseEntity);
	}

}
