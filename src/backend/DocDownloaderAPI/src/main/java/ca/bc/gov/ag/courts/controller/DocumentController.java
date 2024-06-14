package ca.bc.gov.ag.courts.controller;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.Utils.InetUtils;
import ca.bc.gov.ag.courts.api.DocumentApi;
import ca.bc.gov.ag.courts.api.model.FiletransferRequest;
import ca.bc.gov.ag.courts.api.model.FiletransferResponse;
import ca.bc.gov.ag.courts.api.model.FiletransferstatusResponse;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.service.JobService;
import ca.bc.gov.ag.courts.service.RedisCacheClientService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@RestController
public class DocumentController implements DocumentApi {
	
	Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
	private final JobService jService; 
	private final AppProperties props;
	private final RedisCacheClientService rService;
	
	public DocumentController(JobService jService, RedisCacheClientService rService, AppProperties props) {
		this.jService = jService;
		this.rService = rService; 
		this.props = props; 
	}
	
	@Override
	public ResponseEntity<FiletransferResponse> documentUploadPost(
	        @Parameter(name = "FiletransferRequest", description = "File transfer request", required = true) @Valid @RequestBody FiletransferRequest filetransferRequest
	    ) {
		
		FiletransferResponse resp = new FiletransferResponse();
		
		logger.info("Heard a call to the document upload endpoint. ");
		
		// Create transferId for request. 
		String transferId = InetUtils.getGuidWODash();
		
		Job job = new Job();
		job.setId(transferId); // note mapping here. Job Id is transfer Id. Job status may be found using transfer Id.   
		job.setGuid(Base64.getEncoder().encodeToString(filetransferRequest.getObjGuid())); // guid sent as b64 and mapped to byte[] in request object. 
		job.setApplicationId(props.getOrdsApplicationId());
		job.setGraphSessionUrl(null);
		job.setEmail(filetransferRequest.getEmail());
		job.setError(false);
		job.setLastErrorMessage(null);
		job.setStartDeliveryDtm(null);
		job.setEndDeliveryDtm(null);
		job.setPercentageComplete(0); 
		job.setFilePath(filetransferRequest.getFilePath());
		job.setFileName(null); 
		job.setMimeType(null); 
		
		jService.processDocRequest(job); // trip of the processing in this async thread. 
		
		resp.setObjGuid(Base64.getEncoder().encodeToString(filetransferRequest.getObjGuid()));
		resp.setTransferId(transferId);
		
		return new ResponseEntity<FiletransferResponse>(resp, HttpStatus.ACCEPTED);
	}
	
	@Override
	public ResponseEntity<FiletransferstatusResponse> documentStatusTransferIdGet(
			@Parameter(name = "transferId", description = "The document transfer Id", required = true, in = ParameterIn.PATH) @PathVariable("transferId") String transferId) {

		MDC.put("transferid", transferId + "_poll");
		
		logger.info("Heard a call to the document status endpoint for transferId: " + transferId);

		try {
		
			ResponseEntity<Job> response = null;
			try {
				CompletableFuture<ResponseEntity<Job>> _job = rService.getJob(transferId);
				response = _job.get();
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
				
			// If the response is a 404, just return the same (Occurs when transferId not found or expired). 
			if (HttpStatus.NOT_FOUND == response.getStatusCode()) {
				return new ResponseEntity<FiletransferstatusResponse>(HttpStatus.NOT_FOUND);
			}
		
			Job job = response.getBody();
	
			FiletransferstatusResponse resp = new FiletransferstatusResponse();
			resp.setPercentTransfered(job.getPercentageComplete());
			resp.setStartDeliveryDtm(job.getStartDeliveryDtm());
			resp.setEndDeliveryDtm(job.getEndDeliveryDtm());
			resp.setFileName(job.getFileName());
			resp.setFilePath(job.getFilePath());
			resp.fileSize(job.getFileSize());
			resp.setMime(job.getMimeType());
			resp.setError(job.getError());
			resp.setLastErrorMessage(job.getLastErrorMessage());
			
			
			return new ResponseEntity<FiletransferstatusResponse>(resp, HttpStatus.OK);
		
		
		} finally {
			MDC.remove("transferid");
		}

	}

}
