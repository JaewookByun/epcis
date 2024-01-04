package org.oliot.epcis.converter.data.pojo_to_bson;

import static org.oliot.epcis.converter.data.pojo_to_bson.POJOtoBSONUtil.*;

import org.bson.Document;
import org.oliot.epcis.model.ActionType;
import org.oliot.epcis.model.ObjectEventType;
import org.oliot.epcis.model.ValidationException;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * TransactionManager holds event-bus handlers for processing capture jobs.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class ObjectEventConverter {

	public static Document toBson(ObjectEventType obj) throws ValidationException {

		Document dbo = EPCISEventConverter.toBson(obj);

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
		// BizTransactionList
		putBizTransactionList(dbo, obj.getBizTransactionList());
		// SourceList
		putSourceList(dbo, obj.getSourceList());
		// DestinationList
		putDestinationList(dbo, obj.getDestinationList());
		// QuantityList
		putQuantityList(dbo, "quantityList", obj.getQuantityList());
		// PersistentDisposition
		putPersistentDisposition(dbo, obj.getPersistentDisposition());
		// ReadPoint
		putReadPoint(dbo, obj.getReadPoint());
		// BizLocation
		putBizLocation(dbo, obj.getBizLocation());
		// SensorElementList
		putSensorElementList(dbo, obj.getSensorElementList());

		// ILMD
		Document ilmdExtension = null;
		if (obj.getIlmd() != null) {
			if (!obj.getAction().equals(ActionType.ADD)) {
				throw new ValidationException("action should be ADD if ilmd is provided.");
			}
			ilmdExtension = putAny(dbo, "ilmd", obj.getIlmd().getAny(), true);
			if (ilmdExtension != null)
				putFlatten(dbo, "ilmdf", ilmdExtension);
		}

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
