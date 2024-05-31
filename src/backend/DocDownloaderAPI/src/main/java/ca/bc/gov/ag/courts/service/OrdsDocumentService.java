/**
 * 
 */
package ca.bc.gov.ag.courts.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import ca.bc.gov.ag.courts.api.model.OrdsPushResponse;
import ca.bc.gov.ag.courts.exception.DownloaderException;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.model.OrdsHealthResponse;



/**
 * Ords Document Service Interface
 * 
 * @author 176899
 *
 */
public interface OrdsDocumentService {
	
	public CompletableFuture<ResponseEntity<OrdsHealthResponse>> getOrdsHealth() throws HttpClientErrorException;
	public CompletableFuture<ResponseEntity<OrdsPushResponse>> pushFile(Job job) throws DownloaderException;
	
}
