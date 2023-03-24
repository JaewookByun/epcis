package org.oliot.epcis.converter.xml.write;

import static org.oliot.epcis.util.BSONWriteUtil.*;

import org.bson.Document;
import org.oliot.epcis.capture.XMLCaptureServer;
import org.oliot.epcis.model.ObjectEventType;
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
public class XMLObjectEventWriteConverter {

	public static Document convert(ObjectEventType obj) throws ValidationException {

		Document dbo = XMLEPCISEventWriteConverter.convert(obj);

		// Event Type
		dbo.put("type", "ObjectEvent");
		// EPC List
		putEPCList(dbo, "epcList", obj.getEpcList());
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
		// SourceList
		putSourceList(dbo, obj.getSourceList());
		// DestinationList
		putDestinationList(dbo, obj.getDestinationList());
		// QuantityList
		putQuantityList(dbo, "quantityList", obj.getQuantityList());
		// ILMD
		Document ilmdExtension = null;
		if (obj.getIlmd() != null) {
			ilmdExtension = putAny(dbo, "ilmd", obj.getIlmd().getAny(), true);
		}
		if (ilmdExtension != null)
			putFlatten(dbo, "ilmdf", ilmdExtension);
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

}
