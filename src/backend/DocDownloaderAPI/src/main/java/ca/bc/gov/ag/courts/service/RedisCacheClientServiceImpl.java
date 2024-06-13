package ca.bc.gov.ag.courts.service;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
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
	
	private final RestTemplate restTemplate;
	private final AppProperties props; 
	private String redisUrl;
	
	public RedisCacheClientServiceImpl(RestTemplateBuilder restTemplateBuilder, AppProperties props) {
		this.restTemplate = restTemplateBuilder
				.errorHandler(new GenericErrorHandler()) 
				.build(); 
		this.props = props; 
	}
	
	@PostConstruct
	public void init() throws UnknownHostException {
		redisUrl = "http://" + InetUtils.getIPForHostname(props.getRedisClientHost()) + ":" + props.getRedisClientPort() + "/";
		logger.info("Resolved redis client as " + redisUrl);
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
	
	///TODO - This needs protection from retrying when the transferId is not found (404) otherwise it will retry the max retries. 
	@Override
	@Retryable(retryFor = Exception.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<Job>> getJob(String jobId) throws Exception {
		
		logger.debug("Redis Service: Calling getJob... retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		
		URI uri = new URI(this.redisUrl + "jobs/" + jobId);

		HttpEntity<String> entity = new HttpEntity<String>(
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<Job> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, Job.class);

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

		ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

		return CompletableFuture.completedFuture(responseEntity);
	}

	@Override
	@Retryable(retryFor = RestClientException.class, maxAttemptsExpression = "${application.net.max.retries}", backoff = @Backoff(delayExpression = "${application.net.delay}"))
	public CompletableFuture<ResponseEntity<String>> deleteJob(String jobId) throws Exception {
		
		/**
		 * Returns 404 NOT FOUND if jobId doesn't exist. 
		 */
		
		logger.debug("Redis Service: Calling deleteJob... retry count " + RetrySynchronizationManager.getContext().getRetryCount());
		URI uri = new URI(this.redisUrl + "job/" + jobId);

		HttpEntity<Job> entity = new HttpEntity<Job>(
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

		return CompletableFuture.completedFuture(responseEntity);
		
	}

	/**
	 * 
	 * Test to determine if jobId already exists
	 *  
	 */
	@Override
	public boolean jobExists(String jobId) {
		
		ResponseEntity<Job> jobTest;
		try {
			CompletableFuture<ResponseEntity<Job>> j = this.getJob(jobId);
			jobTest = j.get();
			if (jobTest.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false; 
			} else {
				return true;
			}
		} catch (Exception e) {
			return false; 
		}
	}
}
