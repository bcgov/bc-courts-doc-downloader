/**
 * 
 */
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.courts.Utils.AuthHelper;
import ca.bc.gov.ag.courts.Utils.InetUtils;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.handler.RedisClientResponseErrorHandler;
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
				.errorHandler(new RedisClientResponseErrorHandler()) 
				.build(); 
		this.props = props; 
	}
	
	@PostConstruct
	public void init() throws UnknownHostException {
		redisUrl = "http://" + InetUtils.getIPForHostname(props.getRedisClientHost()) + ":" + props.getRedisClientPort() + "/";
		logger.info("Resolved redis client as " + redisUrl);
	}
	
	
	@Override
	public CompletableFuture<ResponseEntity<Job[]>> getJobs() throws Exception {

		logger.info("RCC Calling getJobs...");
		URI uri = new URI(this.redisUrl + "jobs");

		HttpEntity<String> entity = new HttpEntity<String>(
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<Job[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, Job[].class);

		return CompletableFuture.completedFuture(responseEntity);
	}
	
	@Override
	public CompletableFuture<ResponseEntity<Job>> getJob(String jobId) throws Exception {
		
		logger.info("RCC Calling getJob for jobId: " + jobId);
		URI uri = new URI(this.redisUrl + "jobs/" + jobId);

		HttpEntity<String> entity = new HttpEntity<String>(
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<Job> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, Job.class);

		return CompletableFuture.completedFuture(responseEntity);
	}

	@Override
	public CompletableFuture<ResponseEntity<String>> createJob(Job job) throws Exception {
		
		logger.info("RCC: Calling createJob...");
		System.out.println(job);
		URI uri = new URI(this.redisUrl + "job");

		HttpEntity<Job> entity = new HttpEntity<Job>(job,
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, entity, String.class);

		return CompletableFuture.completedFuture(responseEntity);
	}

	@Override
	public CompletableFuture<ResponseEntity<String>> updateJob(Job job) throws Exception {
		
		logger.info("RCC: Calling updateJob...");
		URI uri = new URI(this.redisUrl + "job");

		HttpEntity<Job> entity = new HttpEntity<Job>(job,
				AuthHelper.createBasicAuthHeaders(props.getRedisClientUsername(), props.getRedisClientPassword()));

		ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

		return CompletableFuture.completedFuture(responseEntity);
	}

	@Override
	public CompletableFuture<ResponseEntity<String>> deleteJob(String jobId) throws Exception {
		
		/**
		 * Returns 400 BAD REQUEST if jobId doesn't exist. 
		 */
		
		logger.info("RCC: Calling deleteJob...");
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
