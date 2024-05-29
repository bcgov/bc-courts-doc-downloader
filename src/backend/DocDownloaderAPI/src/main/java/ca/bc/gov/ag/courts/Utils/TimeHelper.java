package ca.bc.gov.ag.courts.Utils;

import java.util.Date;
import java.util.TimeZone;

import com.nimbusds.jose.shaded.gson.internal.bind.util.ISO8601Utils;


/**
 * 
 * A collection of time utility functions. 
 * 
 * @author 176899
 *
 */
public class TimeHelper {
	
	private static final String TIME_ZONE_PACIFIC = "US/Pacific";
	
	/**
	 * 
	 * Get the present time as 2024-01-19T19:00:00-07:00 (UTC + offset). 
	 * 
	 * @param date
	 * @return
	 */
	public static String getISO8601NowDtm(Date date) {
		return ISO8601Utils.format(new Date(), false, TimeZone.getTimeZone(TIME_ZONE_PACIFIC));
	}

}
