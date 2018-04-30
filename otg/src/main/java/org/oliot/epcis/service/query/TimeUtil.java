package org.oliot.epcis.service.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is incubating project named Electronic Product Code Context
 * Repository (EPCCR). This project pursues Resource Oriented Architecture (ROA)
 * for EPC-based event
 * 
 * Commonality with EPCIS Getting powered with EPC's global uniqueness
 * 
 * Differences Resource Oriented, not Service Oriented Resource(EPC)-driven URL
 * scheme Best efforts to comply RESTful principle Exploit flexibility rather
 * than formal verification JSON vs. XML NOSQL vs. SQL Focus on the Internet of
 * Things beyond Supply Chain Management
 * 
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@{kaist.ac.kr,gmail.com}
 */
public class TimeUtil {
	public static long getAbsoluteMiliTimes(String absString) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date date = sdf.parse(absString);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getRelativeMiliTimes(String relString) {
		try {
			int periodNumber;
			GregorianCalendar currentCalendar = new GregorianCalendar();
			DatatypeFactory df;

			df = DatatypeFactory.newInstance();
			XMLGregorianCalendar currentTime = df.newXMLGregorianCalendar(currentCalendar);
			if (relString.endsWith("s")) {
				// second
				periodNumber = Integer.parseInt(relString.substring(1, relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration("-PT" + periodNumber + "S");
				currentTime.add(duration);

			} else if (relString.endsWith("min")) {
				// minute
				periodNumber = Integer.parseInt(relString.substring(1, relString.length() - 3));

				Duration duration = DatatypeFactory.newInstance().newDuration("-PT" + periodNumber + "M");
				currentTime.add(duration);

			} else if (relString.endsWith("h")) {
				// hour
				periodNumber = Integer.parseInt(relString.substring(1, relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration("-PT" + periodNumber + "H");
				currentTime.add(duration);

			} else if (relString.endsWith("d")) {
				// days
				periodNumber = Integer.parseInt(relString.substring(1, relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration("-P" + periodNumber + "D");
				currentTime.add(duration);

			} else if (relString.endsWith("w")) {
				// weeks mon
				periodNumber = Integer.parseInt(relString.substring(1, relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration("-P" + periodNumber + "M");
				currentTime.add(duration);

			} else if (relString.endsWith("y")) {
				// year
				periodNumber = Integer.parseInt(relString.substring(1, relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration("-P" + periodNumber + "Y");
				currentTime.add(duration);
			}
			long timeMil = currentTime.toGregorianCalendar().getTimeInMillis();

			return timeMil;
		} catch (DatatypeConfigurationException e) {

		}
		return 0;
	}

	public static boolean isCorrectTimeZone(String timeZone) {

		boolean isMatch = timeZone.matches("^(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])$");

		return isMatch;
	}

	public static long getTimeMil(String time) {

		long timeMil = 0;
		if (time != null) {
			time = time.trim();
			if (time.startsWith("-")) {
				timeMil = TimeUtil.getRelativeMiliTimes(time);
			} else {
				timeMil = TimeUtil.getAbsoluteMiliTimes(time);
			}
		}
		return timeMil;
	}

	public static String makeTimeZoneString(int timeZone) {
		String retString = "";
		timeZone = timeZone / 60;

		if (timeZone >= 0) {
			retString = String.format("+%02d:00", timeZone);
		} else {
			timeZone = Math.abs(timeZone);
			retString = String.format("-%02d:00", timeZone);
		}
		return retString;
	}
}
