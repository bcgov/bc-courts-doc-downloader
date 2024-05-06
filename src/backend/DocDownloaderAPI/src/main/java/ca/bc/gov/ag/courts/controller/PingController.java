package ca.bc.gov.ag.courts.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.PingApi;
import ca.bc.gov.ag.courts.model.OrdsHealthResponse;
import ca.bc.gov.ag.courts.service.OrdsDocumentLookupService;

@RestController
public class PingController implements PingApi {
	
	Logger logger = LoggerFactory.getLogger(PingController.class);
	
	@Autowired 
	OrdsDocumentLookupService service; 

	@Override
	public ResponseEntity<String> pingGet() {
		
		logger.info("Heard a call to the Ping controller.");

		CompletableFuture<ResponseEntity<OrdsHealthResponse>> future = service.GetOrdsHealth();

		String response = "Unknown Response";
		ResponseEntity<OrdsHealthResponse> _resp;
		try {
			_resp = future.get();
			response = _resp.getBody().getStatus();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
}
