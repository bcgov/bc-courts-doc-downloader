/**
 * 
 */
package ca.bc.gov.ag.courts.service;

import java.util.List;

import javax.validation.Valid;

import ca.bc.gov.ag.courts.api.model.FileterminateRequestInner;
import ca.bc.gov.ag.courts.model.Job;


/**
 * Job Service Interface
 * 
 * @author 176899
 *
 */
public interface JobService {
	
	public void processDocRequest(Job job);
	public void onS3DocumentArrival(String msg);
	public void onS3DocumentTimeout(String msg);
	public void processTerminate(List<@Valid FileterminateRequestInner> request);
	
}
