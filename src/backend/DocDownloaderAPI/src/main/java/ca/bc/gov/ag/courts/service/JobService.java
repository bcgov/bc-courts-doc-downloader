/**
 * 
 */
package ca.bc.gov.ag.courts.service;

import ca.bc.gov.ag.courts.model.Job;



/**
 * Job Service Interface
 * 
 * @author 176899
 *
 */
public interface JobService {
	
	public void processDocRequest(Job job);
	public String getDocSessionStatus(String docSessionId);
	
}
