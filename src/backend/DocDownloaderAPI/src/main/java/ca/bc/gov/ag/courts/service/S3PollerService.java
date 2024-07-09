package ca.bc.gov.ag.courts.service;

import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.model.Job;
import jakarta.annotation.PostConstruct;

@Service
public class S3PollerService {
	
	private static final Logger logger = LoggerFactory.getLogger(S3PollerService.class);
	
	private S3Service sService;
	private AppProperties props; 
	
	public S3PollerService(AppProperties props, S3Service sService) {
		this.sService = sService; 
		this.props = props; 
	}
	
	@PostConstruct
	public void init() throws UnknownHostException {
		logger.info("S3 Poller Service started. Timeout: " + props.getS3PollerTimeoutMinutes() + " minutes.");
	}
	
	/**
	 * 
	 * Uses Executor service to wait on the requested file to arrive at the S3 storage. 
	 * 
	 * Note the use of the JobService callback triggered on file arrival or timeout. 
	 * 
	 * @param fileName
	 * @param service
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void PollS3ForFile(String fileName, JobService service, Job job) throws InterruptedException, ExecutionException {
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(new Poll(fileName));
		
		try {
			
            logger.debug("Polling started for fileName: " + fileName);
            
            future.get(Long.valueOf(props.getS3PollerTimeoutMinutes()), TimeUnit.MINUTES);    
            
            service.onS3DocumentArrival("Document found", job);
            
        } catch (TimeoutException e) {
            future.cancel(true);
            service.onS3DocumentTimeout("Timeout reached", job);
        }

        executor.shutdownNow();
		
	}
	
	/**
	 * This method will invoke the S3 service to determine if the requested file 
	 * has been placed into the bucket. 
	 * 
	 * @author 176899
	 *
	 */
	private class Poll implements Callable<String> {
		
		private String fileName; 
		
		public Poll(String fileName) {
			this.fileName = fileName; 
		}
		
	    @Override
	    public String call() throws InterruptedException {
	 
	    	boolean found = false;
	    	
	    	while(!found) {
	    		found = queryS3(fileName);
	    	}
	    	return "File Found";
	    }
	}

	/**
	 * 
	 * Queries the S3 bucket for the given file name
	 * 
	 * @param fileName
	 * @return
	 * @throws InterruptedException
	 */
	private boolean queryS3(String fileName) {
		
		logger.debug("S3 Query for fileName " + fileName);
		
		try {
			Thread.sleep(4000);
			return sService.objectExists(props.getS3AccessBucket(), fileName);
		} catch (Exception e) {
			logger.error("Error while querying S3 object store: " + e.getMessage());
			return false; 
		}
	}
}
