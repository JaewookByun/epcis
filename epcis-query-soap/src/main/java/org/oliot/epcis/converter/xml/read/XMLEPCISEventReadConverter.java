package org.oliot.epcis.converter.xml.read;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.bson.Document;
import org.oliot.epcis.model.CorrectiveEventIDsType;
import org.oliot.epcis.model.EPCISEventType;
import org.oliot.epcis.model.ErrorDeclarationType;

import static org.oliot.epcis.util.BSONReadUtil.*;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-soap acts as a server to receive queries
 * to provide filtered, sorted, limited events or masterdata of interest inside
 * EPCIS repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class XMLEPCISEventReadConverter {

	@SuppressWarnings("unchecked")
	public static void putCommonEventFields(EPCISEventType event, Document obj, org.w3c.dom.Document doc,
			org.w3c.dom.Element envelope, ArrayList<String> nsList) throws DatatypeConfigurationException {

		// Event Time
		XMLGregorianCalendar eventTime = getGregorianCalendar(obj.getLong("eventTime"));
		if (eventTime != null)
			event.setEventTime(eventTime);

		// Event Time Zone
		String eventTimeZoneOffset = obj.getString("eventTimeZoneOffset");
		if (eventTimeZoneOffset != null)
			event.setEventTimeZoneOffset(eventTimeZoneOffset);

		// Record Time
		XMLGregorianCalendar recordTime = getGregorianCalendar(obj.getLong("recordTime"));
		if (recordTime != null)
			event.setRecordTime(recordTime);

		// Event ID
		event.setEventID(obj.getString("eventID"));

		// Error Declaration
		Document innerObj = obj.get("errorDeclaration", Document.class);
		if (innerObj != null && !innerObj.isEmpty()) {
			ErrorDeclarationType err = new ErrorDeclarationType();
			List<Object> jaxbList = err.getDeclarationTimeOrReasonOrCorrectiveEventIDs();
			if (innerObj.containsKey("declarationTime")) {
				XMLGregorianCalendar calendar = getGregorianCalendar((Long) innerObj.remove("declarationTime"));
				JAXBElement<XMLGregorianCalendar> element = new JAXBElement<XMLGregorianCalendar>(
						new QName("declarationTime"), XMLGregorianCalendar.class, calendar);
				jaxbList.add(element);
			}
			if (innerObj.containsKey("reason")) {
				JAXBElement<String> element = new JAXBElement<String>(new QName("reason"), String.class,
						(String) innerObj.remove("reason"));
				jaxbList.add(element);
			}
			if (innerObj.containsKey("correctiveEventIDs")) {
				CorrectiveEventIDsType ceIDs = new CorrectiveEventIDsType(
						(List<String>) innerObj.remove("correctiveEventIDs"));
				JAXBElement<CorrectiveEventIDsType> element = new JAXBElement<CorrectiveEventIDsType>(
						new QName("correctiveEventIDs"), CorrectiveEventIDsType.class, ceIDs);
				jaxbList.add(element);
			}
			if (innerObj.containsKey("extension")) {
				jaxbList.addAll(getAny(innerObj.get("extension", Document.class), doc, envelope, nsList));
			}
			event.setErrorDeclaration(err);
		}
	}
}
