package ca.bc.gov.ag.courts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.PingApi;

@RestController
public class PingController implements PingApi {

	@Override
	public ResponseEntity<String> pingGet() {
		//TODO - Add ORDS ping to response
		return new ResponseEntity<String>("Pong", HttpStatus.OK);
	}
}
