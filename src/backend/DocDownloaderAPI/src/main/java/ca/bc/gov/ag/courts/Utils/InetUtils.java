package ca.bc.gov.ag.courts.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.springframework.stereotype.Component;


/**
 * Helper methods for converting host names to IPs, etc. 
 */
@Component
public class InetUtils {
	
	public static String getIPForHostname(String hostname) throws UnknownHostException {
		return InetAddress.getByName(hostname).getHostAddress();
	}
	
	public static String getGuidWODash() {
	    return UUID.randomUUID().toString().replace("-", "");
	}

}
