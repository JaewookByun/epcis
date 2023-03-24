package org.oliot.epcis.converter.xml.read;

import javax.xml.datatype.DatatypeConfigurationException;

import org.bson.Document;
import org.oliot.epcis.model.BusinessLocationType;
import org.oliot.epcis.model.PersistentDispositionType;
import org.oliot.epcis.model.ReadPointType;
import org.oliot.epcis.model.TransformationEventType;
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
public class XMLTransformationEventReadConverter {

	public TransformationEventType convert(Document obj, org.w3c.dom.Document doc, Element envelope,
			ArrayList<String> nsList) throws DatatypeConfigurationException {

		TransformationEventType event = new TransformationEventType();

		XMLEPCISEventReadConverter.putCommonEventFields(event, obj, doc, envelope, nsList);

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
			event.setIlmd(getILMD(obj.get("ilmd", Document.class), doc, envelope, nsList));
		}

		// Vendor Extension
		if (obj.containsKey("extension")) {
			event.setAny(getAny(obj.get("extension", Document.class), doc, envelope, nsList));
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
