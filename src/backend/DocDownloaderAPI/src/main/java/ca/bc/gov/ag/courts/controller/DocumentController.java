package ca.bc.gov.ag.courts.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.DocumentApi;
import ca.bc.gov.ag.courts.api.model.FiletransferRequest;
import ca.bc.gov.ag.courts.api.model.FiletransferResponse;
import ca.bc.gov.ag.courts.api.model.FiletransferstatusResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@RestController
public class DocumentController implements DocumentApi {
	
	Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
	@Override
	public ResponseEntity<FiletransferResponse> documentUploadPost(
	        @Parameter(name = "FiletransferRequest", description = "") @Valid @RequestBody(required = true) FiletransferRequest filetransferRequest) {
		
		logger.info("Heard a call to the document upload endpoint for docId: " + new String(filetransferRequest.getObjGuid()));
		
		//TODO 
		FiletransferResponse resp = new FiletransferResponse();
		resp.setAcknowledge(true);
		resp.setDetail("tobedone");
		return new ResponseEntity<FiletransferResponse>(resp, HttpStatus.ACCEPTED);
	}
	
	@Override
	public ResponseEntity<FiletransferstatusResponse> documentStatusDocIdGet(
	        @Min(1) @Parameter(name = "docId", description = "The document Id", required = true, in = ParameterIn.PATH) @PathVariable("docId") Integer docId) {
		
		logger.info("Heard a call to the document status endpoint for docId: " + docId);
		
		// TODO - This will come from Reddis
		// TODO - Needs input params defined.
		// TODO - Needs to return Job object. 
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
