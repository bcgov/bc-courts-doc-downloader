package ca.bc.gov.ag.courts.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.Utils.AuthHelper;
import ca.bc.gov.ag.courts.api.MsgtestApi;
import ca.bc.gov.ag.courts.api.model.MsgtestRequest;
import ca.bc.gov.ag.courts.api.model.TestResponse;
import ca.bc.gov.ag.courts.service.MSGraphServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class MSGraphTestController implements MsgtestApi {
	
	Logger logger = LoggerFactory.getLogger(MSGraphTestController.class);

	private final AuthHelper authService; 
	private final MSGraphServiceImpl msgService;
	
	public MSGraphTestController(AuthHelper authService, MSGraphServiceImpl msgService) {
		this.authService = authService;
		this.msgService = msgService; 
	}	

	@Override
	public ResponseEntity<TestResponse> msgtestPost(
			@Parameter(name = "MsgtestRequest", description = "") @Valid @RequestBody(required = false) MsgtestRequest msgtestRequest) {

		TestResponse resp = new TestResponse();
		resp.setResult("Success");
		
		try {
			
			logger.debug("Initiating MS Graph file upload test for : " + msgtestRequest.getFileName());
			
			String token; 
			
			JSONObject jResponse = null; 
			CompletableFuture<JSONObject> at = authService.GetAccessToken();
			jResponse = at.get();
			if (jResponse.getInt("responseCode") == HttpStatus.OK.value())  
					token = jResponse.getJSONObject("responseMsg").getString("access_token");
			else 
				throw new Exception(jResponse.getJSONObject("responseMsg").getJSONObject("error").getString("message")); 
			
			logger.debug("token: " + token);
			
			String userId; 
			CompletableFuture<JSONObject> ui = msgService.GetUserId(token, msgtestRequest.getEmail());
			jResponse = ui.get();
			if (jResponse.getInt("responseCode") == HttpStatus.OK.value())  
				userId = jResponse.getJSONObject("responseMsg").getString("id");
			else 
				throw new Exception(jResponse.getJSONObject("responseMsg").getJSONObject("error").getString("message")); 

			CompletableFuture<String> sessionResponse  = msgService.createUploadSessionFromUserId(token, userId, msgtestRequest.getFolder(), msgtestRequest.getFileName());
			String uploadUrl = sessionResponse.get();
			
			long fileSize = msgtestRequest.getData().length;
			
			CompletableFuture<JSONObject> uploadResponse = uploadFileInChunks(msgtestRequest.getData(), uploadUrl, fileSize);
			
			uploadResponse.get();
			logger.info("Completed file upload for : " + msgtestRequest.getFileName());
			logger.info("Resp: " + uploadResponse.get().toString());
			
			return new ResponseEntity<TestResponse>(resp, HttpStatus.CREATED);
			

		} catch (Exception ex) {
			resp.setResult("Error");
			resp.setError(ex.getMessage());
			return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	/**
	 * 
	 * Upload file content in chunks
	 * 
	 * @param content
	 * @param uploadUrl
	 * @param fileSize
	 * @return
	 * @throws Exception
	 */
	private CompletableFuture<JSONObject> uploadFileInChunks(byte[] content, String uploadUrl, long fileSize) throws Exception {

		// Upload file in chunks
		int fragSize = 320 * 1024;
		int numFragments = (int) ((fileSize / fragSize) + 1);
		byte[] buffer = new byte[fragSize];

		logger.info("FileSize being uploaded: " + fileSize);
		logger.info("Number of fragments: " + numFragments);

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

				CompletableFuture<JSONObject> chunkResponse = msgService.uploadChunk(uploadUrl, count, fileSize, chunk,
						fragSize, chunkSize);
				lastResponseObject = chunkResponse.get();

				logger.debug("Chunk " + count + " uploaded.");

				bytesRemaining = bytesRemaining - chunkSize;
				count++;
			}
		}

		return CompletableFuture.completedFuture(lastResponseObject);

	}
	
}
