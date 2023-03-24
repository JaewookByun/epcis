package org.oliot.epcis.converter.xml.read;

import javax.xml.datatype.DatatypeConfigurationException;

import org.bson.Document;
import org.oliot.epcis.model.ActionType;
import org.oliot.epcis.model.BusinessLocationType;
import org.oliot.epcis.model.PersistentDispositionType;
import org.oliot.epcis.model.ReadPointType;
import org.oliot.epcis.model.TransactionEventType;
import org.w3c.dom.Element;

import static org.oliot.epcis.util.BSONReadUtil.*;

import java.util.ArrayList;

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
public class XMLTransactionEventReadConverter {

	public TransactionEventType convert(Document obj, org.w3c.dom.Document doc, Element envelope,
			ArrayList<String> nsList) throws DatatypeConfigurationException {

		TransactionEventType event = new TransactionEventType();

		XMLEPCISEventReadConverter.putCommonEventFields(event, obj, doc, envelope, nsList);

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
			event.setAny(getAny(obj.get("extension", Document.class), doc, envelope, nsList));
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
					getSensorElementListType(obj.getList("sensorElementList", Document.class), doc, envelope, nsList));
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
