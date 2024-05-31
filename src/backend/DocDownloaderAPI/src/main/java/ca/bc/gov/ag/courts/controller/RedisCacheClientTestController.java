package ca.bc.gov.ag.courts.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.service.RedisCacheClientService;
import org.springframework.http.MediaType; 

/**
 * Created for testing Redis Cache Client service only. 
 * 
 * To be removed for prod release. 
 * 
 * @author 176899
 *
 */
@RestController
public class RedisCacheClientTestController {

	//TODO - Remove me for production. 
	
	
	private RedisCacheClientService service;

	public RedisCacheClientTestController(RedisCacheClientService service) {
		this.service = service;
	}

	@GetMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Job[]> getJobs() throws Exception {

		CompletableFuture<ResponseEntity<Job[]>> _resp = service.getJobs();
		return _resp.get();

	}
	
	@GetMapping(value = "/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Job> getJobs(@PathVariable String jobId) throws Exception {

		CompletableFuture<ResponseEntity<Job>> _resp = service.getJob(jobId);
		return _resp.get();

	}
	
	@GetMapping(value = "/createjob", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> createJob() throws Exception {
		
		Job job = new Job();
		job.setId("123456");
		job.setGuid("79278979237492");
		job.setApplicationId("CeisCso");
		job.setOrdsTimeout(false);
		job.setGraphTimeout(false);
		job.setGraphSessionUrl("8623846823");
		job.setError(false);
		job.setLastErrorMessage(null);
		job.setStartDeliveryDtm("2024-01-19T19:00-07:00");
		job.setEndDeliveryDtm("2024-01-19T20:00-07:00");
		job.setPercentageComplete(23);
		job.setFileName("test.pdf"); 
		job.setMimeType("application/pdf");

		CompletableFuture<ResponseEntity<String>> _resp = service.createJob(job);
		return _resp.get();
	}
	
	@GetMapping(value = "/updatejob", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> updateJob() throws Exception {
		
		Job job = new Job();
		job.setId("123456");
		job.setGuid("79278979237492");
		job.setApplicationId("CeisCso");
		job.setOrdsTimeout(false);
		job.setGraphTimeout(false);
		job.setGraphSessionUrl("8623846823");
		job.setError(false);
		job.setLastErrorMessage(null);
		job.setStartDeliveryDtm("2024-01-19T19:00-07:00");
		job.setEndDeliveryDtm("2024-01-19T20:00-07:00");
		job.setPercentageComplete(23);
		job.setFileName("test.pdf"); 
		job.setMimeType("application/pdf");

		CompletableFuture<ResponseEntity<String>> _resp = service.updateJob(job);
		return _resp.get();
	}
	
	@GetMapping(value = "/deletejob/{jobId}", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> deleteJob(@PathVariable String jobId) throws Exception {

		CompletableFuture<ResponseEntity<String>> _resp = service.deleteJob(jobId);
		return _resp.get();

	}
}
