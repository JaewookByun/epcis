package org.oliot.epcis.converter.data.pojo_to_bson;

import static org.oliot.epcis.converter.data.pojo_to_bson.POJOtoBSONUtil.*;

import org.bson.Document;
import org.oliot.epcis.model.TransactionEventType;
import org.oliot.epcis.model.ValidationException;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * The class converts TransactionEvent from a storage unit to POJO.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class TransactionEventConverter {

	public static Document toBson(TransactionEventType obj) throws ValidationException {

		Document dbo = EPCISEventConverter.toBson(obj);

		// Event Type
		dbo.put("type", "TransactionEvent");
		// Parent ID
		putEPC(dbo, "parentID", obj.getParentID());
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

		// SensorElementList
		putSensorElementList(dbo, obj.getSensorElementList());
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
