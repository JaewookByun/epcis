package org.oliot.epcis.util;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SOAPMessage {

	private Document message;
	private Element envelope;
	private Element body;

	public SOAPMessage() {
		try {
			message = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		envelope = message.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
		envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
		body = message.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");

	}

	public Document getMessage() {
		return message;
	}

	public Element getEnvelope() {
		return envelope;
	}

	public Element getBody() {
		return body;
	}

	public void putResult(Object result, Class<?> resultType) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(resultType);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(result, body);
			envelope.appendChild(body);
			envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			message.appendChild(envelope);
			message.normalize();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
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

	/*
	 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
	 * <soap:Body> <ns3:QueryResults xmlns:ns4="urn:epcglobal:epcis:xsd:1"
	 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
	 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">
	 * <queryName>SimpleEventQuery</queryName> <resultsBody> <EventList> ...
	 */
}
