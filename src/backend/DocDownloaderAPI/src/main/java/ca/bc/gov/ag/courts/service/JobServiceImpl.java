package ca.bc.gov.ag.courts.service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.courts.Utils.TimeHelper;
import ca.bc.gov.ag.courts.listener.JobEventListener;
import ca.bc.gov.ag.courts.model.Job; 

@Service
public class JobServiceImpl implements JobService, JobEventListener {
	
	Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
	 
	private final RedisCacheClientService rService; 
	
	public JobServiceImpl(RedisCacheClientService rService) {
		this.rService = rService; 
	}
	
	/**
	 * 
	 * Process Job (Doc Request) 
	 * 
	 * @param job
	 * @param docSessionId
	 */
    public void processDocRequest(Job job) {
        
		logger.info("Heard a call to processDocRequest.");
		
		try {
		
			// set time creation
			job.setStartDeliveryDtm(TimeHelper.getISO8601NowDtm(new Date()));
			
			rService.createJob(job);
		
			// TODO - Update Redis Cache Client with latest job model.   
			// TODO - Create ORDS call with (Sync) and once it completes, initiate MS Graph upload process (void return type).
			// TODO - At every stage update Reddis with state.
			
            Thread.sleep(5000); // Simulating job processing time
            this.onCompletion(job); // callback
            
        } catch (Exception e) {
        	
        	this.onError(job, e);
            Thread.currentThread().interrupt();
        }
    }
	

	/**
	 * 
	 * This call will be made to the Redis Cache Client service. 
	 * 
	 * @param docSessionId
	 * @return
	 */	
	public String getDocSessionStatus(String docSessionId) {

		RestTemplate restTemplate = new RestTemplate();
	    String url = "https://httpbin.org/get";
	    String response = "Started";
	    System.out.println("Process Started");

	    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {     
	        restTemplate.getForObject(url, String.class);
	        return "success";
	    });
	    
	    try {
	        response = future.get();
	        System.out.println("Call completed. Response was " + response);
	    }
	    catch(Exception e){
	        response = e.toString();
	    }

	    return response;

	}

	@Override
	public void onCompletion(Job job) {
		System.out.println("Job completed for correlationId: " + job.getCorrelationId());
	}

	@Override
	public void onError(Job job, Throwable ex) {
		System.out.println("Job errored for correlationId: " + job.getCorrelationId() + ", Error: " + ex.getMessage());
	}
}
