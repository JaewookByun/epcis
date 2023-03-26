package org.oliot.epcis.converter.data.bson_to_pojo;

import javax.xml.datatype.DatatypeConfigurationException;

import org.bson.Document;
import org.oliot.epcis.model.BusinessLocationType;
import org.oliot.epcis.model.PersistentDispositionType;
import org.oliot.epcis.model.ReadPointType;
import org.oliot.epcis.model.TransformationEventType;
import org.oliot.epcis.util.SOAPMessage;

import static org.oliot.epcis.util.BSONReadUtil.*;

import java.util.ArrayList;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * The class converts TransformationEvent from a POJO to a storage unit, BSON.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class TransformationEventConverter {

	public TransformationEventType convert(Document obj, SOAPMessage message,
			ArrayList<String> nsList) throws DatatypeConfigurationException {

		TransformationEventType event = new TransformationEventType();

		EPCISEventConverter.putCommonEventFields(event, obj, message, nsList);

		// inputEPCList
		if (obj.containsKey("inputEPCList"))
			event.setInputEPCList(getEPCListType(obj.getList("inputEPCList", String.class)));
		// outputEPCList
		if (obj.containsKey("outputEPCList"))
			event.setOutputEPCList(getEPCListType(obj.getList("outputEPCList", String.class)));
		// transformationID
		if (obj.containsKey("transformationID"))
			event.setTransformationID(obj.getString("transformationID"));

		// bizStep
		if (obj.containsKey("bizStep")) {
			event.setBizStep(obj.getString("bizStep"));
		}
		// disposition
		if (obj.containsKey("disposition")) {
			event.setDisposition(obj.getString("disposition"));
		}
		// readPoint
		if (obj.containsKey("readPoint")) {
			event.setReadPoint(new ReadPointType(obj.getString("readPoint")));
		}
		// bizLocation
		if (obj.containsKey("bizLocation")) {
			event.setBizLocation(new BusinessLocationType(obj.getString("bizLocation")));
		}
		// bizTransactionList
		if (obj.containsKey("bizTransactionList")) {
			event.setBizTransactionList(
					getBusinessTransactionListType(obj.getList("bizTransactionList", Document.class)));
		}

		// inputQuantityList
		if (obj.containsKey("inputQuantityList")) {
			event.setInputQuantityList(getQuantityListType(obj.getList("inputQuantityList", Document.class)));
		}
		// outputQuantityList
		if (obj.containsKey("outputQuantityList")) {
			event.setOutputQuantityList(getQuantityListType(obj.getList("outputQuantityList", Document.class)));
		}

		// sourceList
		if (obj.containsKey("sourceList")) {
			event.setSourceList(getSourceListType(obj.getList("sourceList", Document.class)));
		}
		// destinationList
		if (obj.containsKey("destinationList")) {
			event.setDestinationList(getDestinationListType(obj.getList("destinationList", Document.class)));
		}

		// ilmd
		if (obj.containsKey("ilmd")) {
			event.setIlmd(getILMD(obj.get("ilmd", Document.class), message, nsList));
		}

		// Vendor Extension
		if (obj.containsKey("extension")) {
			event.setAny(getAny(obj.get("extension", Document.class), message, nsList));
		}

		// sensorElementList
		if (obj.containsKey("sensorElementList")) {
			event.setSensorElementList(
					getSensorElementListType(obj.getList("sensorElementList", Document.class), message, nsList));
		}
		// Persistent Disposition
		if (obj.containsKey("persistentDisposition")) {
			PersistentDispositionType pdt = getPersistentDispositionType(
					obj.get("persistentDisposition", Document.class));
			if (pdt != null)
				event.setPersistentDisposition(pdt);
		}

		return event;
	}

}
