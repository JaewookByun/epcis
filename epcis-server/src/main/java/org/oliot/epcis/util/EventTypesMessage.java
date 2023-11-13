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

public class EventTypesMessage {

	private Document message;
	private Element eventTypes;

	public EventTypesMessage() {
		try {
			message = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		eventTypes = message.createElement("EventTypes");
		message.appendChild(eventTypes);
		for (String type : DynamicResource.availableEventTypes) {
			eventTypes.appendChild(message.createElement(type));
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
