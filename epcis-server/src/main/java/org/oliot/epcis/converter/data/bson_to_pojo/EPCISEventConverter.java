package org.oliot.epcis.converter.data.bson_to_pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.bson.Document;
import org.oliot.epcis.model.CorrectiveEventIDsType;
import org.oliot.epcis.model.EPCISEventType;
import org.oliot.epcis.model.ErrorDeclarationType;
import org.oliot.epcis.util.SOAPMessage;

import jakarta.xml.bind.JAXBElement;

import static org.oliot.epcis.util.BSONReadUtil.*;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * The class converts ObjectEvent (common class) from a POJO to a storage unit, BSON.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class EPCISEventConverter {

	@SuppressWarnings("unchecked")
	public static void putCommonEventFields(EPCISEventType event, Document obj, SOAPMessage message,
			ArrayList<String> nsList) throws DatatypeConfigurationException {

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

		// Certification Info
		String certificationInfo = obj.getString("certificationInfo");
		if (certificationInfo != null)
			event.setCertificationInfo(certificationInfo);

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
				jaxbList.addAll(getAny(innerObj.get("extension", Document.class), message, nsList));
			}
			event.setErrorDeclaration(err);
		}
	}
}
