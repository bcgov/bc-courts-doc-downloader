package ca.bc.gov.ag.courts.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.courts.config.AppProperties;

@Component
public class AppStartupListener implements ApplicationListener<ApplicationReadyEvent> {
	
	static final Logger logger = LoggerFactory.getLogger(AppStartupListener.class);
	
	@Autowired
	AppProperties props; 
	
	/**
	 * This event is executed as late as conceivably possible to indicate that the
	 * application is ready to service requests.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		
		logger.info("BC Courts Doc Downloader API starting up. Version: " + props.getApplicationVersion());
		return;
	}
}