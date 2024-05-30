package ca.bc.gov.ag.courts.service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.courts.Utils.TimeHelper;
import ca.bc.gov.ag.courts.api.model.OrdsPushResponse;
import ca.bc.gov.ag.courts.listener.JobEventListener;
import ca.bc.gov.ag.courts.model.Job; 

@Service
public class JobServiceImpl implements JobService, JobEventListener {
	
	Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
	 
	private final RedisCacheClientService rService; 
	
	private final OrdsDocumentService oService;
	
	public JobServiceImpl(RedisCacheClientService rService, OrdsDocumentService oService) {
		this.rService = rService; 
		this.oService = oService; 
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
		
			// set time of job creation
			job.setStartDeliveryDtm(TimeHelper.getISO8601Dtm(new Date()));
			
			// create the initial entry in Redis.
			rService.createJob(job); 
			
			CompletableFuture<ResponseEntity<OrdsPushResponse>> _resp = oService.pushFile(job); 
			ResponseEntity<OrdsPushResponse> resp =  _resp.get();
			
			job.setPercentageComplete(10);
			job.setFileName(resp.getBody().getFilename()); // available after ORDS call 
			job.setMimeType(resp.getBody().getMimetype()); // available after ORDS call
			
			// update after sync ORDS push file to intermediate NFS storage. 
			rService.updateJob(job); 
		   
			// TODO - Create ORDS call with (Sync) and once it completes, initiate MS Graph upload process (void return type).
			// TODO - At every stage update Reddis with state.
			
            Thread.sleep(5000); // Simulating job processing time
            
            this.onCompletion(job); // success callback
            
        } catch (Exception e) {
        	
        	this.onError(job, e);  // error callback
        	
        	//TODO - set generic error flags in the job object and write to Redis. 
        	
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
		System.out.println("Job completed for correlationId: " + job.getId());
	}

	@Override
	public void onError(Job job, Throwable ex) {
		System.out.println("Job errored for correlationId: " + job.getId() + ", Error: " + ex.getMessage());
		
		job.setOrdsTimeout(true);
		job.setError(true);
		job.setLastErrorMessage(ex.getMessage());
		job.setEndDeliveryDtm(null);
		job.setPercentageComplete(10);
		job.setFileName(null); // available after ORDS call 
		job.setMimeType(null); // available after ORDS call	
	}
}
