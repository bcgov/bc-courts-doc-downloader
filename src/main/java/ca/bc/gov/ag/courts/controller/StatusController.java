package ca.bc.gov.ag.courts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.StatusApi;
import ca.bc.gov.ag.courts.api.model.FiletransferstatusResponse; 

@RestController
public class StatusController implements StatusApi {

	@Override
	public ResponseEntity<FiletransferstatusResponse> statusGet() {
		
		// TODO - This will come from Reddis
		// TODO - Needs input params defined. 
		FiletransferstatusResponse resp = new FiletransferstatusResponse();
		resp.setPercentTransfered(75);
		resp.setFileRequestedDtm("2013-09-15T05:53:00-08:00");
		resp.setFileDeliveredDtm("2013-09-15T05:55:00-08:00");
		resp.setFileName("test1.txt");
		resp.setFilePath("/me/court/0122202/files");
		resp.fileSize(123L);
		resp.setMime("applicationn/pdf");
		
		return new ResponseEntity<FiletransferstatusResponse>(resp, HttpStatus.OK);
	}

}
