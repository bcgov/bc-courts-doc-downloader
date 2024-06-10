package ca.bc.gov.ag.courts.service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
	@Async
    public void processDocRequest(Job job) {
		
		MDC.put("transferid", job.getId());
        
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
			
			// Update Redis after sync ORDS push to intermediate NFS storage. 
			rService.updateJob(job); 
		   
			// TODO - Check for the presence of the file on the NFS. (requires connectivity - See SCV-456)  
			// TODO - Initiate MS Graph upload process. (requires connectivity - See SCV-457). 
			
            Thread.sleep(5000); // Simulating job processing time
            
            this.onCompletion(job); // success callback
            
        } catch (Exception e) {
        	
        	this.onError(job, e);  // error callback
            Thread.currentThread().interrupt();
            
        } finally {
        	MDC.remove("transferid");
        }
    }
	

	@Override
	public void onCompletion(Job job) {
		logger.info("Job completed.");
	}

	@Override
	public void onError(Job job, Throwable ex) {
		
    	job.setError(true); 
    	job.setLastErrorMessage(ex.getMessage());
    	
    	try {
			rService.updateJob(job);
		} catch (Exception e1) {
			logger.error("Failed to update Job after encountering error.");
			e1.printStackTrace();
		}
	}
}
