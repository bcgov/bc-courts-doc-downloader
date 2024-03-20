package ca.bc.gov.ag.courts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.FetchApi;
import ca.bc.gov.ag.courts.api.model.FiletransferRequest;
import ca.bc.gov.ag.courts.api.model.FiletransferResponse;

@RestController
public class DocumentTransferController implements FetchApi {
	
	@Override
	public ResponseEntity<FiletransferResponse> fetchPost(FiletransferRequest req) {
		//TODO 
		FiletransferResponse resp = new FiletransferResponse();
		resp.setAcknowledge(true);
		resp.setDetail("tobedone");
		return new ResponseEntity<FiletransferResponse>(resp, HttpStatus.ACCEPTED);
	}
}
