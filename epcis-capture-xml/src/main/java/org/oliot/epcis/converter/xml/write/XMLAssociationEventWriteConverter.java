package org.oliot.epcis.converter.xml.write;

import static org.oliot.epcis.util.BSONWriteUtil.*;

import org.bson.Document;
import org.oliot.epcis.capture.XMLCaptureServer;
import org.oliot.epcis.model.AssociationEventType;
import org.oliot.epcis.model.exception.ValidationException;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class XMLAssociationEventWriteConverter {

	public static Document convert(AssociationEventType obj) throws ValidationException {
		
		// validate(obj);
		
		Document dbo = XMLEPCISEventWriteConverter.convert(obj);
		
		// Event Type
		dbo.put("type", "AssociationEvent");
		// parentID
		putEPC(dbo, "parentID", obj.getParentID());
		// Child EPCs - using EPCList for query efficiency
		putEPCList(dbo, "epcList", obj.getChildEPCs());
		// Child QuantityList = using QuantityList for query efficiency
		putQuantityList(dbo, "quantityList", obj.getChildQuantityList());
		// Action
		putAction(dbo, obj.getAction());
		// Biz Step
		putBizStep(dbo, obj.getBizStep());
		// Disposition
		putDisposition(dbo, obj.getDisposition());
		// ReadPoint
		putReadPoint(dbo, obj.getReadPoint());
		// BizLocation
		putBizLocation(dbo, obj.getBizLocation());
		// BizTransactionList
		putBizTransactionList(dbo, obj.getBizTransactionList());
		// Source List
		putSourceList(dbo, obj.getSourceList());
		// Dest List
		putDestinationList(dbo, obj.getDestinationList());

		// SensorElementList
		putSensorElementList(dbo, obj.getSensorElementList(), XMLCaptureServer.unitConverter);
		// PersistentDisposition
		putPersistentDisposition(dbo, obj.getPersistentDisposition());

		// Vendor Extension
		Document extension = putAny(dbo, "extension", obj.getAny(), false);
		if (extension != null)
			putFlatten(dbo, "extf", extension);

		// put event id
		if (!dbo.containsKey("eventID")) {
			putEventHashID(dbo);
		}
		
		return dbo;
	}
	
	public void validate(AssociationEventType obj) throws ValidationException {
		if (obj.getAction().value().equals("ADD") && obj.getParentID() == null
				|| (obj.getChildEPCs() == null && obj.getChildQuantityList() == null)) {
			ValidationException e = new ValidationException();
			e.setReason("ADD AssociationEvent requires parentID and instance or class-level child EPCs");
			e.setStackTrace(new StackTraceElement[0]);
			throw e;
		} else if (obj.getAction().value().equals("DELETE") && obj.getParentID() == null) {
			ValidationException e = new ValidationException();
			e.setReason("DELETE AssociationEvent requires parentID");
			e.setStackTrace(new StackTraceElement[0]);
			throw e;
		} else if (obj.getAction().value().equals("OBSERVE")
				&& (obj.getChildEPCs() == null && obj.getChildQuantityList() == null)) {
			ValidationException e = new ValidationException();
			e.setReason("OBSERVE AssociationEvent requires instance or class-level child EPCs");
			e.setStackTrace(new StackTraceElement[0]);
			throw e;
		}
	}
}
