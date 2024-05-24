package ca.bc.gov.ag.courts.controller;

import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.OrdstestApi;
import ca.bc.gov.ag.courts.api.model.FiletransferRequest;
import ca.bc.gov.ag.courts.api.model.OrdsPushResponse;
import ca.bc.gov.ag.courts.exception.DownloaderException;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.service.OrdsDocumentLookupService;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class OrdsTestController implements OrdstestApi {

	private OrdsDocumentLookupService service;

	public OrdsTestController(OrdsDocumentLookupService service) {
		this.service = service;
	}

	@Override
	public ResponseEntity<OrdsPushResponse> ordstestPost(
			@Parameter(name = "FiletransferRequest") @Valid @RequestBody(required = true) FiletransferRequest filetransferRequest) {

		// Create a new Job from the request
		Job job = new Job();
		job.setGuid(new String(filetransferRequest.getObjGuid())); // object guid decoded from b64 at this point.

		ResponseEntity<OrdsPushResponse> resp = null;
		try {
			resp = service.getFile(job).get();
		} catch (DownloaderException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return resp;

	}
}
