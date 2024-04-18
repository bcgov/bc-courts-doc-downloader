package ca.bc.gov.ag.courts.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.PingApi;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.model.OrdsHealthResponse;
import ca.bc.gov.ag.courts.service.OrdsDocumentLookupService;

@RestController
public class PingController implements PingApi {
	
	@Autowired 
	OrdsDocumentLookupService service; 
	
	@Autowired
	private AppProperties props;

	@Override
	public ResponseEntity<String> pingGet() {
		
		String ordsMsg = "Object Store ORDS ";  
		CompletableFuture<ResponseEntity<OrdsHealthResponse>> future = service.GetOrdsHealth();
		
			ResponseEntity<OrdsHealthResponse> _resp;
			try {
				_resp = future.get();
				ordsMsg += _resp.getBody().toString();
			} catch (InterruptedException | ExecutionException e) {
				ordsMsg += e.getMessage();
				e.printStackTrace();
			}
		
		return new ResponseEntity<String>(props.getAppName() + "\n" + ordsMsg, HttpStatus.OK);
	}
}
