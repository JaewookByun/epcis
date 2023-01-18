package org.oliot.epcis.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	public static String getDateTimeStamp(long timemillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return sdf.format(new Date(timemillis));
	}
}
