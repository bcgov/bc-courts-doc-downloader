package ca.bc.gov.ag.courts.listener;

import ca.bc.gov.ag.courts.model.Job;

/**
 * Callback interface for Job Service. 
 * 
 * @author 176899
 *
 */
public interface JobEventListener {

    public void onCompletion(Job job);
    
    public void onError(Job job, Throwable exception);
}
