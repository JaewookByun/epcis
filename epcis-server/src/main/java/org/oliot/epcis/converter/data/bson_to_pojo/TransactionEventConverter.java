package org.oliot.epcis.converter.data.bson_to_pojo;

import javax.xml.datatype.DatatypeConfigurationException;

import org.bson.Document;
import org.oliot.epcis.model.ActionType;
import org.oliot.epcis.model.BusinessLocationType;
import org.oliot.epcis.model.PersistentDispositionType;
import org.oliot.epcis.model.ReadPointType;
import org.oliot.epcis.model.TransactionEventType;
import org.oliot.epcis.util.SOAPMessage;

import static org.oliot.epcis.util.BSONReadUtil.*;

import java.util.ArrayList;

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
public class TransactionEventConverter {

	public TransactionEventType convert(Document obj, SOAPMessage message, ArrayList<String> nsList)
			throws DatatypeConfigurationException {

		TransactionEventType event = new TransactionEventType();

		EPCISEventConverter.putCommonEventFields(event, obj, message, nsList);

		// Parent ID
		if (obj.containsKey("parentID"))
			event.setParentID(obj.getString("parentID"));

		// Child EPCs - using EPCList for query efficiency
		if (obj.containsKey("epcList"))
			event.setEpcList(getEPCListType(obj.getList("epcList", String.class)));

		// action
		if (obj.containsKey("action")) {
			event.setAction(ActionType.fromValue(obj.getString("action")));
		}

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

		// Vendor Extension
		if (obj.containsKey("extension")) {
			event.setAny(getAny(obj.get("extension", Document.class), message, nsList));
		}

		// sourceList
		if (obj.containsKey("sourceList")) {
			event.setSourceList(getSourceListType(obj.getList("sourceList", Document.class)));
		}
		// destinationList
		if (obj.containsKey("destinationList")) {
			event.setDestinationList(getDestinationListType(obj.getList("destinationList", Document.class)));
		}
		// quantityList
		if (obj.containsKey("quantityList")) {
			event.setQuantityList(getQuantityListType(obj.getList("quantityList", Document.class)));
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
