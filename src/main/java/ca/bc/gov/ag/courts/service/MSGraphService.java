/**
 * 
 */
package ca.bc.gov.ag.courts.service;

import java.io.File;

import ca.bc.gov.ag.courts.api.model.TestResponse;



/**
 * @author 176899
 *
 */
public interface MSGraphService {
	
	public TestResponse UploadFile(String clientId, String tenantId, String[] scopes, File file);

}
