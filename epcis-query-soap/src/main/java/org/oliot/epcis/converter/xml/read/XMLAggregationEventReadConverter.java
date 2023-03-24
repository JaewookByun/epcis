package org.oliot.epcis.converter.xml.read;

import javax.xml.datatype.DatatypeConfigurationException;

import org.bson.Document;
import org.oliot.epcis.model.ActionType;
import org.oliot.epcis.model.AggregationEventType;
import org.oliot.epcis.model.BusinessLocationType;
import org.oliot.epcis.model.PersistentDispositionType;
import org.oliot.epcis.model.ReadPointType;
import org.oliot.epcis.util.BSONReadUtil;

import static org.oliot.epcis.util.BSONReadUtil.*;

import java.util.ArrayList;

/**
 * Copyright (C) 2020-2021. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-query-soap acts as a server to receive queries
 * to provide filtered, sorted, limited events or masterdata of interest inside EPCIS
 * repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class XMLAggregationEventReadConverter {

	public AggregationEventType convert(Document obj, org.w3c.dom.Document doc, org.w3c.dom.Element envelope,
			ArrayList<String> nsList) throws DatatypeConfigurationException {

		AggregationEventType event = new AggregationEventType();

		XMLEPCISEventReadConverter.putCommonEventFields(event, obj, doc, envelope, nsList);

		// Parent ID
		if (obj.containsKey("parentID"))
			event.setParentID(obj.getString("parentID"));

		// Child EPCs - using EPCList for query efficiency
		if (obj.containsKey("epcList"))
			event.setChildEPCs(BSONReadUtil.getEPCListType(obj.getList("epcList", String.class)));

		// action
		if (obj.containsKey("action")) {
			event.setAction(ActionType.fromValue(obj.getString("action")));
		}

		// bizStep
		if (obj.containsKey("bizStep")) {
			// urn:epcglobal:cbv:bizstep:
			// https://gs1.org/voc/Bizstep-
			event.setBizStep(obj.getString("bizStep"));
		}
		// disposition
		if (obj.containsKey("disposition")) {
			// urn:epcglobal:cbv:disp:
			// https://gs1.org/voc/Disp-
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
			// urn:epcglobal:cbv:btt:
			// https://gs1.org/voc/BTT-
			event.setBizTransactionList(
					getBusinessTransactionListType(obj.getList("bizTransactionList", Document.class)));
		}

		// Vendor Extension
		if (obj.containsKey("extension")) {
			event.setAny(getAny(obj.get("extension", Document.class), doc, envelope, nsList));
		}

		// ChildQuantityList - using QuantityList for query efficiency
		// quantityList
		if (obj.containsKey("quantityList")) {
			event.setChildQuantityList(getQuantityListType(obj.getList("quantityList", Document.class)));
		}
		// sourceList
		if (obj.containsKey("sourceList")) {
			// urn:epcglobal:cbv:sdt:
			// https://gs1.org/voc/SDT-
			event.setSourceList(getSourceListType(obj.getList("sourceList", Document.class)));
		}
		// destinationList
		if (obj.containsKey("destinationList")) {
			event.setDestinationList(getDestinationListType(obj.getList("destinationList", Document.class)));
		}
		// sensorElementList
		if (obj.containsKey("sensorElementList")) {
			// gs1:MeasurementType-
			// https://gs1.org/voc/MeasurementType-
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
