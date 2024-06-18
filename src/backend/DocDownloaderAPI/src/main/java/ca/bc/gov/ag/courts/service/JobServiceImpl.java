package ca.bc.gov.ag.courts.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import ca.bc.gov.ag.courts.Utils.AuthHelper;
import ca.bc.gov.ag.courts.Utils.TestHelper;
import ca.bc.gov.ag.courts.Utils.TimeHelper;
import ca.bc.gov.ag.courts.api.model.OrdsPushResponse;
import ca.bc.gov.ag.courts.listener.JobEventListener;
import ca.bc.gov.ag.courts.model.Job; 

/**
 * 
 * Main Job Processing service. 
 * 
 * This service provides generates an Async process to completely service 1 document push request to OneDrive.
 * 
 * Steps: 
 * 
 * 	1.) ORDS call is made for the document which returns synchronously. 
 *  2.) Validate the file has arrived on the intermediate drive location. 
 *  3.) Request the file upload session URL from MS Graph.
 *  4.) Sequentially upload the file in chunks until complete. 
 * 
 * As single access token is required to initiate the upload session only.  
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
	
	public JobServiceImpl(RedisCacheClientService rService, 
			OrdsDocumentService oService, 
			AuthHelper aService, 
			MSGraphService mService) {
		this.rService = rService; 
		this.oService = oService;
		this.aService = aService; 
		this.mService = mService; 
	}

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
			
			job.setPercentageComplete(10);
			job.setFileName(resp.getBody().getFilename());  
			job.setMimeType(resp.getBody().getMimetype());
			job.setFileSize(Long.parseLong(resp.getBody().getSizeval()));
			
			// Update Redis after sync ORDS push to intermediate NFS storage. 
			rService.updateJob(job); 
		   
			// TODO - Check for the presence of the file on the NFS. (requires connectivity - See SCV-456)  
			
			// Initiate MS Graph upload process by acquiring the session URL. (requires connectivity for O/S - See SCV-457). 
			String token = aService.GetAccessToken();
			
			String sessionUrl = mService.createUploadSessionFromUserId(
					token, mService.GetUserId(token, job.getEmail()), job.getFilePath(), job.getFileName()
			);
			
			//TODO - Remove me for prod - Loads a dummy file instead of the one pulled from the object store.  
			//byte[] bytes = TestHelper.fetchFileResourceAsBytes("test.pdf");
			byte[] bytes = TestHelper.fetchFileResourceAsBytes("15394_3M.pdf");
			
			//TODO - remove this next line once the NFS solution has been implemented 
			job.setFileSize(bytes.length);
			
			CompletableFuture<JSONObject> uploadResponse = uploadFileInChunks(job, bytes, sessionUrl);
			JSONObject mResp = uploadResponse.get();
			logger.debug(mResp.toString());
            
            this.onCompletion(job); // success callback
            
        } catch (Exception e) {
        	
        	this.onError(job, e);  // error callback
            Thread.currentThread().interrupt();
            
        } finally {
        	MDC.remove("transferid");
        }
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
	private CompletableFuture<JSONObject> uploadFileInChunks(Job job, byte[] content, String uploadUrl) throws Exception {

		int fragSize = 320 * 1024;
		long fileSize = content.length;
		int numFragments = (int) ((fileSize / fragSize) + 1);
		byte[] buffer = new byte[fragSize];
		
		// Maintains percentage complete interval for each chunk.
		int uploadTick = 90 / numFragments;  

		logger.debug("FileSize being uploaded: " + fileSize);
		logger.debug("Number of fragments: " + numFragments);
		logger.debug("Upload chunk percentage increase: " + uploadTick);

		int bytesRead;

		JSONObject lastResponseObject = null;

		InputStream fileStream = new ByteArrayInputStream(content);

		try (BufferedInputStream bis = new BufferedInputStream(fileStream)) {

			long bytesRemaining = fileSize;
			int count = 0;

			while ((bytesRead = bis.read(buffer)) != -1) {

				int chunkSize = fragSize;

				if (bytesRemaining < chunkSize) {
					chunkSize = (int) bytesRemaining;
				}

				byte[] chunk = new byte[bytesRead];
				System.arraycopy(buffer, 0, chunk, 0, bytesRead);

				CompletableFuture<JSONObject> chunkResponse = mService.uploadChunk(uploadUrl, count, fileSize, chunk,
						fragSize, chunkSize);
				lastResponseObject = chunkResponse.get();
				
				bytesRemaining = bytesRemaining - chunkSize;
				
				// Report latest upload to Redis. 
				job.setPercentageComplete( job.getPercentageComplete() + uploadTick );
				if (job.getPercentageComplete() == 100) {
					job.setEndDeliveryDtm(TimeHelper.getISO8601Dtm(new Date()));
					job.setBytesDelivered(fileSize - bytesRemaining);
				}
				this.rService.updateJob(job);

				logger.debug("Chunk " + count + " uploaded.");
				logger.debug("Bytes remaining to be delivered = " + bytesRemaining); // here
				
				count++;
			}
		}

		return CompletableFuture.completedFuture(lastResponseObject);

	}
	
	@Override
	public void onCompletion(Job job) {
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
}
