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
		
		TestResponse resp = new TestResponse();
		resp.setResult("Not Implemented");
		
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
