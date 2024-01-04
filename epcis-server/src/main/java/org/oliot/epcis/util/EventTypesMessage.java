package org.oliot.epcis.util;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.oliot.epcis.resource.DynamicResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class EventTypesMessage {

	private Document message;
	private Element eventTypes;

	public EventTypesMessage() {
		try {
			message = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		eventTypes = message.createElement("Resource");
		message.appendChild(eventTypes);
		for (String type : DynamicResource.availableEventTypes) {
			Element eventType = message.createElement("eventType");
			eventType.setTextContent(type);
			eventTypes.appendChild(eventType);
		}
		message.normalize();
	}

	public Document getMessage() {
		return message;
	}

	public String toString() {
		try {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			StringWriter sw = new StringWriter();
			tf.setOutputProperty(OutputKeys.INDENT, "no");
			tf.transform(new DOMSource(message), new StreamResult(sw));
			return sw.toString();
		} catch (TransformerException e) {
			// Never happen or should not happen
			e.printStackTrace();
			return null;
		}
	}

}
