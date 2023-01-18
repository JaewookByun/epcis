package org.oliot.epcis.converter.pojo_to_bson;

import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.*;

import org.bson.Document;
import org.oliot.epcis.model.TransformationEventType;
import org.oliot.epcis.model.ValidationException;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * The class converts TransformationEvent from a format to another format.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class TransformationEventConverter {

	public static Document toBson(TransformationEventType obj) throws ValidationException {

		Document dbo = EPCISEventConverter.toBson(obj);

		// Event Type
		dbo.put("type", "TransformationEvent");

		// Input EPCList
		putEPCList(dbo, "inputEPCList", obj.getInputEPCList());
		// Output EPCList
		putEPCList(dbo, "outputEPCList", obj.getOutputEPCList());
		// TransformationID
		putTransformationID(dbo, obj.getTransformationID());
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

		// Input Quantity List
		putQuantityList(dbo, "inputQuantityList", obj.getInputQuantityList());
		// Output Quantity List
		putQuantityList(dbo, "outputQuantityList", obj.getOutputQuantityList());
		// Source List
		putSourceList(dbo, obj.getSourceList());
		// Dest List
		putDestinationList(dbo, obj.getDestinationList());
		// ILMD
		Document ilmdExtension = null;
		if (obj.getIlmd() != null) {
			ilmdExtension = putAny(dbo, "ilmd", obj.getIlmd().getAny(), true);
		}
		if (ilmdExtension != null)
			putFlatten(dbo, "ilmdf", ilmdExtension);

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
