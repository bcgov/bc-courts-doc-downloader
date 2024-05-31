package ca.bc.gov.ag.courts.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;


/**
 * Helper methods for converting host names to IPs, etc. 
 */
@Component
public class InetUtils {
	
	public static String getIPForHostname(String hostname) throws UnknownHostException {
		return InetAddress.getByName(hostname).getHostAddress();
	}

}
