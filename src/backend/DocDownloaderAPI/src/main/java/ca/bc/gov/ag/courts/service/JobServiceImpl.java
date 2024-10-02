package ca.bc.gov.ag.courts.service;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import ca.bc.gov.ag.courts.Utils.AuthHelper;
import ca.bc.gov.ag.courts.Utils.TimeHelper;
import ca.bc.gov.ag.courts.api.model.FileterminateRequest;
import ca.bc.gov.ag.courts.api.model.OrdsPushResponse;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.listener.JobEventListener;
import ca.bc.gov.ag.courts.model.Job;
import jakarta.annotation.PostConstruct; 

/**
 * 
 * Main Job Processing service. 
 * 
 * This service provides generates an Async process to service 1 document push request to OneDrive.
 * 
 * Steps: 
 * 
 * 	1.) ORDS call is made for document to be placed on NFS.
 *  2.) (ISB) 'Bucket Pumper' pushed file to the S3 storage bucket.  
 *  2.) Use the S3PollerService to watch for the file to appear on the intermediate S3 storage. 
 *  3.) Request a file upload session URL from MS Graph.
 *  4.) Sequentially pull from the S3 input stream and push to the MS Graph OneDrive location until the stream is empty.  
 *  
 * During the above, percentage complete, and or errors, are written to Redis by way of the Redis Client.  
 *  
 * As single access token is required to initiate the MS Graph Upload session only.  
 * 
 * @author 176899
 *
 */

@Service
public class JobServiceImpl implements JobService, JobEventListener {
	
	Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
	 
	private final RedisCacheClientService rService; 
	private final OrdsDocumentService oService;
	private final AuthHelper aService; 
	private final MSGraphService mService; 
	private final S3PollerService pService; 
	private final S3Service sService;
	private final AppProperties props; 
	
	public JobServiceImpl(RedisCacheClientService rService, 
			OrdsDocumentService oService, 
			AuthHelper aService, 
			MSGraphService mService,
			S3PollerService pService,
			S3Service sService,
			AppProperties props) {
		this.rService = rService; 
		this.oService = oService;
		this.aService = aService; 
		this.mService = mService;
		this.pService = pService;
		this.sService = sService; 
		this.props = props;
	}
	
	@PostConstruct
	private void postConstruct() {
		logger.info("JobService service started.");
	}
	

// TODO - Restore this code to revert to MS Graph usage	
// Original - Replace this method once the connection to MS Graph has been restored. 		
	/**
	 * 
	 * Main processor  
	 * 
	 * @param job
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
			
			logger.debug("Filename received from ORDS: " + resp.getBody().getFilename());
			
			job.setPercentageComplete(10); 
			
			// TODO - Uncomment this when bucket pumper working. Remove next line. 
	        job.setOrdsFileName(resp.getBody().getFilename());
			//job.setOrdsFileName("pZuu5fgHrtr98jekhew.pdf");
			
			job.setMimeType(resp.getBody().getMimetype());
			job.setFileSize(Long.parseLong(resp.getBody().getSizeval()));
			
			// Update Redis after sync ORDS push to intermediate NFS storage. 
			rService.updateJob(job);
			
			// Once the file has been found on the S3 storage, the onS3DocumentArrival method is called to initiate the MS Graph push. 
			pService.PollS3ForFile(job.getOrdsFileName(), this, job);
			
            
        } catch (Exception e) {
        	
        	this.onError(job, e);  // error callback
            Thread.currentThread().interrupt();
            
        } finally {
        	MDC.remove("transferid");
        }
    }
	
	
	/**
	 * 
	 * Main processor  
	 * 
	 * @param job
	 */
// Stuart version. 	
//	@Async
//    public void processDocRequest(Job job) {
//		
//		MDC.put("transferid", job.getId());
//        
//		logger.info("Heard a call to processDocRequest.");
//		
//		try {
//		
//			// set time of job creation
//			job.setStartDeliveryDtm(TimeHelper.getISO8601Dtm(new Date()));
//			
//			// create the initial entry in Redis.
//			rService.createJob(job); 
//			
//			CompletableFuture<ResponseEntity<OrdsPushResponse>> _resp = oService.pushFile(job); 
//			ResponseEntity<OrdsPushResponse> resp =  _resp.get();
//			
//			logger.debug("Filename received from ORDS: " + resp.getBody().getFilename());
//			
//			job.setPercentageComplete(10); 
//			job.setOrdsFileName(resp.getBody().getFilename());
//			job.setMimeType(resp.getBody().getMimetype());
//			job.setFileSize(Long.parseLong(resp.getBody().getSizeval()));
//			
//			// Update Redis after sync ORDS push to intermediate NFS storage. 
//			rService.updateJob(job); 
//		   
//			//Fake out the MS Graph portion of this job.
//			uploadFileInChunks(job); 
//
//            this.onCompletion(job); // success callback
//            
//        } catch (Exception e) {
//        	
//        	this.onError(job, e);  // error callback
//            Thread.currentThread().interrupt();
//            
//        } finally {
//        	MDC.remove("transferid");
//        }
//    }

// Original version - Replace this method once the connection to MS Graph has been restored. 	
	/**
	 * 
	 * Upload file content in chunks
	 * 
	 * Note: This method must live outside of the MSGraphService class as it calls 'mService.uploadChunk'. If this method lives
	 * within the MSGraphService, the 'Retryable' uploadChunk fails to remain 'Retryable'.  
	 * 
 	 * 
 	 * @param job
 	 * @param fileStream
 	 * @param uploadUrl
 	 * @return
 	 * @throws Exception
 	 */
	private CompletableFuture<JSONObject> uploadFileInChunks(Job job, InputStream fileStream, String uploadUrl) throws Exception {

		int fragSize = 320 * 1024;
		long fileSize = job.getFileSize();
		int numFragments = (int) ((fileSize / fragSize) + 1);
		byte[] buffer = new byte[fragSize];
		
		// Percentage complete increment for each chunk.
		int uploadTick = 90 / numFragments;  

		logger.debug("FileSize being uploaded: " + fileSize);
		logger.debug("Number of fragments: " + numFragments);
		logger.debug("Upload chunk percentage increase: " + uploadTick);

		JSONObject lastResponseObject = null;

		// DataInputstream is used below as it provides the ability to completely 
		// load the buffer with data each time it reads from the incoming stream. If a 
		// conventional InputStream was used, the buffer is not guaranteed to be filled on 
		// each Read() resulting in many more chunks being uploaded. 
		try (DataInputStream dis = new DataInputStream(fileStream)) {

			long bytesRemaining = fileSize;
			int count = 0;

			while (bytesRemaining > 0) {

				int chunkSize = fragSize;

				// Accounts for possible remainder of dividing 
				// the file size by the fragsize. 
				if (bytesRemaining < chunkSize) {
					chunkSize = (int) bytesRemaining;
				}
				
				try {
					dis.readFully(buffer, 0, chunkSize); // See description of this line above. 
				} catch (EOFException eof) {
					 System.out.println("End of file reached");
				}

				byte[] chunk = new byte[chunkSize];
				System.arraycopy(buffer, 0, chunk, 0, chunkSize);

				CompletableFuture<JSONObject> chunkResponse = mService.uploadChunk(uploadUrl, count, fileSize, chunk,
						fragSize, chunkSize);
				lastResponseObject = chunkResponse.get();
				
				bytesRemaining = bytesRemaining - chunkSize;
				
				// If transferId still exists (and hasn't been cancelled by the user), continue processing, otherwise
				// cancel upload session and break while loop. 
				if (jobExists(job.getId())) {
				
					// Report latest upload to Redis. 
					job.setPercentageComplete( job.getPercentageComplete() + uploadTick );
					if (job.getPercentageComplete() == 100) {
						job.setEndDeliveryDtm(TimeHelper.getISO8601Dtm(new Date()));
						job.setBytesDelivered(fileSize - bytesRemaining);
					}
					this.rService.updateJob(job);
	
					logger.debug("Chunk " + count + " uploaded.");
					logger.debug("Bytes remaining to be delivered = " + bytesRemaining);
					
					count++;
					
				} else {
					
					logger.warn("Cancelling MS Graph upload session for transferId, " + job.getId());
					
					mService.deleteUploadSession(uploadUrl, job.getId());
					break;
					
				}
			}
		}

		return CompletableFuture.completedFuture(lastResponseObject);

	}
	
	
	/**
	 * 
	 * Upload file content in chunks
	 * 
	 * Note: This method must live outside of the MSGraphService class as it calls 'mService.uploadChunk'. If this method lives
	 * within the MSGraphService, the 'Retryable' uploadChunk fails to remain 'Retryable'.  
	 * @param job 
	 * 
	 * @param content
	 * @param uploadUrl
	 * @param fileSize
	 * @return
	 * @throws Exception
	 */
// Stuart version	
//	private void uploadFileInChunks(Job job) throws Exception {
//
//		int fragSize = 320 * 1024;
//		//long fileSize = content.length;
//		long fileSize = job.getFileSize();
//		int numFragments = (int) ((fileSize / fragSize) + 1);
//		//byte[] buffer = new byte[fragSize];
//		
//		// determines percentage complete increment for each chunk.
//		int uploadTick = 90 / numFragments;  
//
//		logger.debug("FileSize being uploaded: " + fileSize);
//		logger.debug("Number of fragments: " + numFragments);
//		logger.debug("Upload chunk percentage increase: " + uploadTick);
//
//		int count = numFragments; 
//		long bytesRemaining = fileSize;
//
//		while (count > 0) {
//
//			int chunkSize = fragSize;
//			
//			if (bytesRemaining < chunkSize) {
//				chunkSize = (int) bytesRemaining;
//			}
//			
//			// Pause
//			try {
//			  Thread.sleep(5000);
//			} catch (InterruptedException e) {
//			  Thread.currentThread().interrupt();
//			}
//			
//			bytesRemaining = bytesRemaining - chunkSize;
//				
//			// Report latest upload to Redis. 
//			job.setPercentageComplete( job.getPercentageComplete() + uploadTick );
//			if (job.getPercentageComplete() == 100) {
//				job.setEndDeliveryDtm(TimeHelper.getISO8601Dtm(new Date()));
//				job.setBytesDelivered(fileSize - bytesRemaining);
//			}
//			
//			this.rService.updateJob(job);
//	
//			logger.debug("Chunk " + count + " uploaded.");
//			logger.debug("Bytes remaining to be delivered = " + bytesRemaining); // here
//			
//			count--;
//			
//		}
//
//		//return CompletableFuture.completedFuture(lastResponseObject);
//
//	}	
	

	@Override
	public void onCompletion(Job job) {
		
		try {
			logger.info("Deleting S3 object: " + job.getOrdsFileName());
			this.sService.removeObject(props.getS3AccessBucket(), job.getOrdsFileName());
		} catch (Exception ex) {
			logger.error("Unable to remove S3 filename: " + job.getOrdsFileName() + ", Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		
		logger.info("Job completed.");
	}

	@Override
	public void onError(Job job, Throwable ex) {
		
    	job.setError(true); 
    	
    	if (ex instanceof HttpClientErrorException) {
    		HttpClientErrorException _ex = (HttpClientErrorException)ex;
    		job.setLastErrorMessage(_ex.getLocalizedMessage());
    	} else {
    		job.setLastErrorMessage(ex.toString());
    	}
    	
    	try {
			rService.updateJob(job);
		} catch (Exception e1) {
			logger.error("Failed to update Job after encountering error.");
			e1.printStackTrace();
		}
	}
	
	/**
	 * 
	 * S3 delivery callback - Initiates the MS Graph upload. 
	 * 
	 * @param msg
	 */
	//TODO - Original Version
	public void onS3DocumentArrival(String msg, Job job) {
		
		logger.debug("Received a message on S3DocumentArrival: " + msg);
		logger.debug("Initiating MS Graph push");
		
		try {
			
			// Initiate MS Graph upload process by acquiring the session URL. (requires connectivity for O/S - See SCV-457). 
			String token = aService.GetAccessToken();
			
			String sessionUrl = mService.createUploadSessionFromUserId(
					token, mService.GetUserId(token, job.getEmail()), job.getFilePath(), job.getFileName()
			);
			
			//TODO - Remove me for prod - Loads a dummy file instead of the one pulled from the object store.  
			//byte[] bytes = TestHelper.fetchFileResourceAsBytes("test.pdf");
			//byte[] bytes = TestHelper.fetchFileResourceAsBytes("15394_3M.pdf");
			
			// Fetch the file from the S3 store. 
			InputStream fileStream = sService.downloadObject(props.getS3AccessBucket(), job.getOrdsFileName());
			
			CompletableFuture<JSONObject> uploadResponse = uploadFileInChunks(job, fileStream, sessionUrl);
			JSONObject mResp = uploadResponse.get();
			logger.debug(mResp.toString());
			
	        this.onCompletion(job); // success callback
        
        
		} catch (Exception ex) {
			logger.error("Caught error at onS3DocumentArrival: " + ex.getMessage());
			this.onError(job, ex);
            Thread.currentThread().interrupt();
		}  finally {
			MDC.remove(job.getId());
		}
		
	}
	
	/**
	 * 
	 * S3 delivery callback - Initiates the MS Graph upload. 
	 * 
	 * @param msg
	 */
	// Stuart Version
//	public void onS3DocumentArrival(String msg, Job job) {
//		
//		logger.debug("Received a message on S3DocumentArrival: " + msg);
//		logger.debug("Initiating MS Graph push");
//		
//		try {
//			
//			uploadFileInChunks(job);
//			
//	        this.onCompletion(job); // success callback
//        
//        
//		} catch (Exception ex) {
//			this.onError(job, ex);
//            Thread.currentThread().interrupt();
//		}  finally {
//			MDC.remove(job.getId());
//		}
//		
//	}
	
	/**
	 * 
	 * S3 timeout callback
	 * 
	 * @param msg
	 * @throws Exception 
	 */
	public void onS3DocumentTimeout(String msg, Job job) {
		
		logger.error("Received a timeout message on S3DocumentTimeout: " + msg);
		
		// Report timeout error to Redis. 
		job.setError(true);
		job.setLastErrorMessage("File transfer failure. S3 Document Storage time out error.");
		try {
			this.rService.updateJob(job);
			MDC.remove(job.getId());
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * processTerminate
	 * 
	 * @param request
	 */
	@Override
	@Async
	public void processTerminate(@Valid FileterminateRequest request) {
		
		for (String element: request.getTransferIds()) {
		
			try {
				logger.debug("Cancelling transferId, " + element);
				rService.deleteJob(element);
			} catch (Exception e) {
				logger.error("Unable to cancel transferId, " + element + ". Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * Test to determine if jobId already exists
	 * 
	 * @param jobId
	 * @return
	 */
	private boolean jobExists(String jobId) {
		
		ResponseEntity<Job> jobTest;
		try {
			CompletableFuture<ResponseEntity<Job>> j = rService.getJob(jobId);
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