package ca.bc.gov.ag.courts.model;

/**
 * Maintains attributes specific to each document retrieval job (thread)
 * 
 * @author 176899
 *
 */
public class Job {
	
	private String docGuid; 
	private String label;
	private String threadId;
	private boolean error; 
	private String errorMessage; 
	
	private long starttime; 
	private long endInitTime;
	private long endGetDocTime;  
	private String percentageComplete = "0";
	
	private String fileName; 
	private String mimeType; 
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getDocGuid() {
		return docGuid;
	}
	public void setDocGuid(String docGuid) {
		this.docGuid = docGuid;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public long getStarttime() {
		return starttime;
	}
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	public String getDurations() {
		String resp = "";
		
		if ( this.starttime > 0 ) { 
			if ( this.endInitTime > 0 ) {
				resp = resp + Long.toString(this.endInitTime - this.starttime); 
			}
			if ( this.endGetDocTime > 0 ) {
				resp = resp + " : " + Long.toString(this.endGetDocTime - this.starttime) + " ms";
			} else {
				resp = resp + " ms";
			}
		}
		return resp; 
	}
	public void setEndInitTime(long endInitTime) {
		this.endInitTime = endInitTime;
	}
	public void setEndGetDocTime(long endGetDocTime) {
		this.endGetDocTime = endGetDocTime;
	}
	public String getPercentageComplete() {
		return percentageComplete;
	}
	public void setPercentageComplete(String percentageComplete) {
		this.percentageComplete = percentageComplete;
	}
	public void setErrorMessage(String msg) {
		this.errorMessage = msg; 
	}
	public String getErrorMessage() {
		return errorMessage;
	}
}
