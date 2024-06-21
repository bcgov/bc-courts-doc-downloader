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

import ca.bc.gov.ag.courts.Utils.InetUtils;
import ca.bc.gov.ag.courts.config.AppProperties;
import jakarta.annotation.PostConstruct;

@Service
public class S3PollerService {
	
	private static final Logger logger = LoggerFactory.getLogger(S3PollerService.class);
	
	private S3Service sService;
	private AppProperties props; 
	
	public S3PollerService(AppProperties props, S3Service sService) {
		this.sService = sService; 
	}
	
	@PostConstruct
	public void init() throws UnknownHostException {
		logger.info("S3 Poller Service started.");
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
	public void PollS3ForFile(String fileName, JobService service) throws InterruptedException, ExecutionException {
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(new Poll(fileName));
		
		try {
            logger.debug("Polling started for fileName: " + fileName);
            future.get(10, TimeUnit.MINUTES); //TODO - externalize. 
            service.onS3DocumentArrival("Document found");
            
        } catch (TimeoutException e) {
            future.cancel(true);
            service.onS3DocumentTimeout("Timeout reached");
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
	 * Queries the S3 bucket for the given file nam
	 * 
	 * @param fileName
	 * @return
	 * @throws InterruptedException
	 */
	private boolean queryS3(String fileName) {
		
		logger.debug("S3 Query for fileName " + fileName);
		
		try {
			return sService.objectExists(props.getS3AccessBucket(), fileName);
		} catch (Exception e) {
			logger.error("S3PollerService: Error while querying S3 object store: " + e.getMessage());
			e.printStackTrace();
			return false; 
		}
	}
}
