package ca.bc.gov.ag.courts.exception;

/**
 * Custom Exception for Downloader API
 * 
 */
public class DownloaderException extends Exception {

	private static final long serialVersionUID = 5873442413088571528L;

	public DownloaderException(String message) {
		super(message);
	}

	public DownloaderException(String message, Throwable cause) {
		super(message, cause);
	}

}
