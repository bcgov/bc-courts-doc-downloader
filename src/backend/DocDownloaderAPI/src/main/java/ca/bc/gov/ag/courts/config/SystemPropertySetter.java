package ca.bc.gov.ag.courts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 
 * This component is only invoked when the spring profile is 'splunk'. 
 * 
 * Its provides the necessary proxy settings to the underlying JVM at runtime. 
 * 
 * This component 'must' be provided in the OpenShift, 'Emerald' cluster for 
 * accessing MS Graph API related endpoints: 
 * 
 * e.g.,
 * 
 *  	https://login.microsoftonline.com/
 *  	https://graph.microsoft.com/ 
 * 
 * It also assumes that appropriate fire wall rules and security classifications have 
 * been set in place in the hosting environment.   
 * 
 * Some references may be found at SCV-466. 
 * 
 */

@Profile("splunk")
@Component
public class SystemPropertySetter {
	
	Logger logger = LoggerFactory.getLogger(SystemPropertySetter.class);
	
	@Value("${application.proxy.host}")
	private String appProxyHost;
	
	@Value("${application.proxy.port}")
	private String appProxyPort;
	
	// boolean
	@Value("${application.use.proxy}")
	private String appUseProxy;
	
	@Value("${application.no.proxy}")
	private String appNoProxy;

	@PostConstruct
	public void setProperty() {
		
		logger.info("Setting JVM System proxy values.");
		
		System.setProperty("javax.net.debug", "all");
		System.setProperty("https.proxyHost", appProxyHost);
		System.setProperty("https.proxyPort", appProxyPort);
		System.setProperty("java.net.useSystemProxies", appUseProxy);
		System.setProperty("http.nonProxyHosts", appNoProxy);

		logger.info("javax.net.debug",System.getProperty("javax.net.debug"));
		logger.info("https.proxyHost",System.getProperty("https.proxyHost"));
		logger.info("https.proxyPort",System.getProperty("https.proxyPort"));
		logger.info("java.net.useSystemProxies",System.getProperty("java.net.useSystemProxies"));
		logger.info("http.nonProxyHosts",System.getProperty("http.nonProxyHosts"));
	}

}
