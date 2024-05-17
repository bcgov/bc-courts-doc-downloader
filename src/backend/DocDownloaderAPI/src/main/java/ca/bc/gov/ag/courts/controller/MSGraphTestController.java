package ca.bc.gov.ag.courts.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.MsgtestApi;
import ca.bc.gov.ag.courts.api.model.MsgtestRequest;
import ca.bc.gov.ag.courts.api.model.TestResponse;
import ca.bc.gov.ag.courts.service.MSGraphServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class MSGraphTestController implements MsgtestApi {
	
	@Autowired 
	MSGraphServiceImpl msgService; 
	
	static String fileName = "testfile.txt";
	static String fileFolder = "MSGraphTestFolder";
	
	Logger logger = LoggerFactory.getLogger(MSGraphTestController.class);

	@Override
	public ResponseEntity<TestResponse> msgtestPost(
			@Parameter(name = "MsgtestRequest", description = "") @Valid @RequestBody(required = false) MsgtestRequest msgtestRequest) {

		TestResponse resp = new TestResponse();
		resp.setResult("Success");
		
		try {
			
			logger.info("Initiating MS Graph file upload test for : " + fileName);

			CompletableFuture<String> sessionResponse  = msgService.createUploadSession(msgtestRequest.getAccessToken(), fileFolder, fileName);
			String uploadUrl = sessionResponse.get();
			
			// Load file from resources folder.
			File file = ResourceUtils.getFile("classpath:" + fileName);
			
			long fileSize = file.length(); 
			
			CompletableFuture<JSONObject> uploadResponse = uploadFileInChunks(file, uploadUrl, fileSize);
			
			uploadResponse.get();
			logger.info("Completed file upload for : " + fileName);
			logger.info("Resp: " + uploadResponse.get().toString());
			
			return new ResponseEntity<TestResponse>(resp, HttpStatus.OK);
			

		} catch (Exception ex) {
			resp.setResult("Error");
			resp.setError(ex.getMessage());
			return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	/**
	 * 
	 * Upload in chunks. 
	 * 
	 * @param file
	 * @param uploadUrl
	 * @param fileSize
	 * @return
	 * @throws Exception
	 */
	private CompletableFuture<JSONObject> uploadFileInChunks(File file, String uploadUrl, long fileSize) throws Exception {
		
		URI uri = new URI(uploadUrl);
		
		// Upload file in chunks
	    int fragSize = 320 * 1024;
	    int numFragments = (int) ((fileSize / fragSize) + 1);    
	    byte[] buffer = new byte[fragSize];
	    
	    logger.info("FileSize being uploaded: " + fileSize);
	    logger.info("Number of fragments: " + numFragments);
	    
	    int bytesRead;
	    
	    JSONObject lastResponseObject = null; 
	    
	    try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
	    	
	    	long bytesRemaining = fileSize; 
	    	int count = 0;
	    	 
	        while ((bytesRead = bis.read(buffer)) != -1) {
	        	
	        	 int chunkSize = fragSize;
	             
	             if (bytesRemaining < chunkSize) {
	                 chunkSize = (int) bytesRemaining;
	             }
	             
	             byte[] chunk = new byte[bytesRead];
	             System.arraycopy(buffer, 0, chunk, 0, bytesRead);
	             
	             CompletableFuture<JSONObject> chunkResponse = msgService.uploadChunk(uploadUrl, count, fileSize, chunk, fragSize, chunkSize);
	             lastResponseObject = chunkResponse.get();
	             
	             logger.debug("Chunk " + count + " uploaded."); 
	           	 
	           	 bytesRemaining = bytesRemaining - chunkSize;
	           	 count++;
	        }
	    }
	    
	    return CompletableFuture.completedFuture(lastResponseObject);
	   
	}
	
	
	
	/**
	 * Load file from Springboot resources folder. 
	 * 
	 * @return
	 */
    public static File fetchFileResource(){
    	File file = null; 
        try {
            file = ResourceUtils.getFile("classpath:" + fileName);
            return file; 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file; 
    }
}
