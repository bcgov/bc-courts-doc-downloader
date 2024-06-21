/**
 * 
 */
package ca.bc.gov.ag.courts.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;

import ca.bc.gov.ag.courts.model.Job;



/**
 * Redis Cache Client Service Interface
 * 
 * @author 176899
 *
 */
public interface RedisCacheClientService {
	
	public CompletableFuture<ResponseEntity<Job[]>> getJobs() throws Exception;
	public CompletableFuture<ResponseEntity<Job>> getJob(String jobId) throws Exception;
	public CompletableFuture<ResponseEntity<String>> createJob(Job job) throws Exception;
	public CompletableFuture<ResponseEntity<String>> updateJob(Job job) throws Exception;
	public CompletableFuture<ResponseEntity<String>> deleteJob(String jobId) throws Exception;

}
