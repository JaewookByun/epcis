package org.oliot.epcis.service.query.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr
 */
@Controller
@RequestMapping("/rest")
public class QueryService {

	/**
	 * Return the resource indicating {epc} Time range can be adjustable
	 * 
	 * @param epc
	 *            Name of Resource
	 * @param from
	 *            Refer to Graphite URL API model
	 * 
	 *            RELATIVE_TIME or ABSOLUTE_TIME
	 * 
	 *            RELATIVE_TIME
	 * 
	 *            s: Seconds
	 * 
	 *            min: Minutes
	 * 
	 *            h: Hours
	 * 
	 *            d: Days
	 * 
	 *            w: Weeks mon: 30 Days(month)
	 * 
	 *            y: 365 Days (year)
	 * 
	 *            ABSOLUTE_TIME FORMAT SimpleDateFormat sdf = new
	 *            SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss"); GregorianCalendar
	 *            eventTimeCalendar = new GregorianCalendar(); Date date =
	 *            sdf.parse("time");
	 * @param until
	 *            examples: &from=-8d&until=-7d
	 *            &from=2007-12-02T21:32:52&until=2007-12-02T21:35:55
	 * @return
	 */
	@RequestMapping(value = "/{epc}", method = RequestMethod.GET)
	@ResponseBody
	public String getResource(@PathVariable String epc,
			@RequestParam(required = false) String from,
			@RequestParam(required = false) String until) {

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");
		
		long fromTime = 0;
		long untilTime = 0;
		if (from != null) {
			from = from.trim();
			if (from.startsWith("-"))
				fromTime = getRelativeMiliTimes(from);
			else
			{
				fromTime = getAbsoluteMiliTimes(from);
			}
		}
		if (until != null){
			until = until.trim();
			if (until.startsWith("-"))
				untilTime = getRelativeMiliTimes(until);
			else
			{
				untilTime = getAbsoluteMiliTimes(until);
			}
		}
		// TODO:
		
		((AbstractApplicationContext) ctx).close();
		return "1.1";
	}
	
	public long getAbsoluteMiliTimes(String absString){
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			Date date = sdf.parse(absString);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public long getRelativeMiliTimes(String relString) {
		try {
			int periodNumber;
			GregorianCalendar currentCalendar = new GregorianCalendar();
			DatatypeFactory df;

			df = DatatypeFactory.newInstance();
			XMLGregorianCalendar currentTime = df
					.newXMLGregorianCalendar(currentCalendar);
			if (relString.endsWith("s")) {
				// second
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-PT" + periodNumber + "S");
				currentTime.add(duration);

			} else if (relString.endsWith("min")) {
				// minute
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 3));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-PT" + periodNumber + "M");
				currentTime.add(duration);

			} else if (relString.endsWith("h")) {
				// hour
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-PT" + periodNumber + "H");
				currentTime.add(duration);

			} else if (relString.endsWith("d")) {
				// days
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-P" + periodNumber + "D");
				currentTime.add(duration);

			} else if (relString.endsWith("w")) {
				// weeks mon
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-P" + periodNumber + "M");
				currentTime.add(duration);

			} else if (relString.endsWith("y")) {
				// year
				periodNumber = Integer.parseInt(relString.substring(1,
						relString.length() - 1));

				Duration duration = DatatypeFactory.newInstance().newDuration(
						"-P" + periodNumber + "Y");
				currentTime.add(duration);
			}
			long timeMil = currentTime.toGregorianCalendar().getTimeInMillis();

			return timeMil;
		} catch (DatatypeConfigurationException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
		return 0;
	}
}
