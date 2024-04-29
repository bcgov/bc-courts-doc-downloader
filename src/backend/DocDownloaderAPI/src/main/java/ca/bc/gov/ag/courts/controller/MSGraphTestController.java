package ca.bc.gov.ag.courts.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.api.MsgtestApi;
import ca.bc.gov.ag.courts.api.model.TestResponse;
import ca.bc.gov.ag.courts.service.MSGraphServiceImpl;

@RestController
public class MSGraphTestController implements MsgtestApi {
	
	@Autowired 
	MSGraphServiceImpl service; 

	@Override
	public ResponseEntity<TestResponse> msgtestGet() {
		
		File file = fetchFileResource();

		final String clientId = "c85283ca-1ebd-49ba-bad0-c189c2811c07"; // app created for shaunmillaris@gmail.com
		final String tenantId = "7029ed61-6fd4-4648-82ed-e33d7f2d7f2c"; // or "common" for multi-tenant apps
		//final String tenantId = "common"; // or "common" for multi-tenant apps

		final String[] scopes = new String[] { "Files.ReadWrite.All" }; // necessary for writing a file.

		TestResponse resp = service.UploadFile(clientId, tenantId, scopes, file);
		
		return new ResponseEntity<TestResponse>(resp, HttpStatus.OK);
	}
	
	
	/**
	 * Load file from Springboot resources folder. 
	 * 
	 * @return
	 */
    public static File fetchFileResource(){
    	File file = null; 
        try {
            file = ResourceUtils.getFile("classpath:testfile.txt");
            return file; 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file; 
    }
}
