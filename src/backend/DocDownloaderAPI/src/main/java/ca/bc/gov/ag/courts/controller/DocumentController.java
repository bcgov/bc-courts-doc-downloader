package ca.bc.gov.ag.courts.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.DocumentApi;
import ca.bc.gov.ag.courts.api.model.FiletransferRequest;
import ca.bc.gov.ag.courts.api.model.FiletransferResponse;
import ca.bc.gov.ag.courts.api.model.FiletransferstatusResponse;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.service.JobService;
import ca.bc.gov.ag.courts.service.JobServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@RestController
public class DocumentController implements DocumentApi {
	
	Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
	private final JobService jService; 
	private final AppProperties props; 
	
	public DocumentController(JobServiceImpl jService, AppProperties props) {
		this.jService = jService;
		this.props = props; 
	}
	
	@Override
	public ResponseEntity<FiletransferResponse> documentUploadPost(
	        @Parameter(name = "X-Correlation-Id", description = "", in = ParameterIn.HEADER) @RequestHeader(value = "X-Correlation-Id", required = false) String xCorrelationId,
	        @Parameter(name = "FiletransferRequest", description = "") @Valid @RequestBody(required = false) FiletransferRequest filetransferRequest
	    ) {
		
		MDC.put("correlationid", xCorrelationId);
		
		logger.info("Heard a call to the document upload endpoint. ");
		
		Job job = new Job();
		job.setId(xCorrelationId);
		job.setGuid(new String(filetransferRequest.getObjGuid())); // guid sent as b64 and mapped to byte[] in request object. 
		job.setApplicationId(props.getOrdsApplicationId());
		job.setOrdsTimeout(false);
		job.setGraphTimeout(false);
		job.setGraphSessionUrl(null);
		job.setError(false);
		job.setLastErrorMessage(null);
		job.setStartDeliveryDtm(null);
		job.setEndDeliveryDtm(null);
		job.setPercentageComplete(0);
		job.setFileName(null); // available after ORDS call 
		job.setMimeType(null); // available after ORDS call
		jService.processDocRequest(job);
		
		//TODO - To be completed 
		FiletransferResponse resp = new FiletransferResponse();
		resp.setAcknowledge(true);
		resp.setDetail("tobedone");
		
		MDC.remove("correlationid");
		
		return new ResponseEntity<FiletransferResponse>(resp, HttpStatus.ACCEPTED);
	}
	
	@Override
	public ResponseEntity<FiletransferstatusResponse> documentStatusDocIdGet(
	        @Min(1) @Parameter(name = "docId", description = "The document Id", required = true, in = ParameterIn.PATH) @PathVariable("docId") Integer docId) {
		
		logger.info("Heard a call to the document status endpoint for docId: " + docId);
		
		// TODO - Response data will come from Redis Cache. 
		// TODO - Needs input params defined.
		// TODO - Needs to return Job object if it exists. 
		FiletransferstatusResponse resp = new FiletransferstatusResponse();
		resp.setPercentTransfered(75);
		resp.setFileRequestedDtm("2013-09-15T05:53:00-08:00");
		resp.setFileDeliveredDtm("2013-09-15T05:55:00-08:00");
		resp.setFileName("test1.txt");
		resp.setFilePath("/me/court/0122202/files");
		resp.fileSize(123L);
		resp.setMime("application/pdf");
		
		return new ResponseEntity<FiletransferstatusResponse>(resp, HttpStatus.OK);
	}

}
