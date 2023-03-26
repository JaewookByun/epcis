package org.oliot.epcis.query.response;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StaticResponseBuilder {
	public static String getStandardVersion() throws ParserConfigurationException, TransformerException {
		/*
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		 * <soap:Body> <ns3:GetStandardVersionResponse
		 * xmlns:ns4="urn:epcglobal:epcis:xsd:1"
		 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
		 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"> 1.2
		 * </ns3:GetStandardVersionResponse> </soap:Body> </soap:Envelope>
		 */

//		EPCISQueryDocumentType retDoc = new EPCISQueryDocumentType();
//		EPCISQueryBodyType retBody = new EPCISQueryBodyType();
//		retBody.setGetStandardVersionResult("2.0");
//		retDoc.setEPCISBody(retBody);
//		StringWriter sw = new StringWriter();
//		JAXB.marshal(retDoc, sw);
//		

		Document retDoc;

		retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
		Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
		Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1", "query:GetStandardVersionResult");
		response.setTextContent(Metadata.GS1_EPCIS_Version);
		envelope.appendChild(body);
		body.appendChild(response);
		retDoc.appendChild(envelope);
		return XMLUtil.toString(retDoc);

		// SecurityException, ValidationException, ImplementationException;
	}

	public static String getVendorVersion() throws ParserConfigurationException, TransformerException {
		/*
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		 * <soap:Body> <ns3:GetVendorVersionResponse
		 * xmlns:ns4="urn:epcglobal:epcis:xsd:1"
		 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
		 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">org.
		 * oliot.epcis-1.2.10</ns3:GetVendorVersionResponse> </soap:Body>
		 * </soap:Envelope>
		 */
		Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
		Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
		Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1", "query:GetVendorVersionResult");
		response.setTextContent(Metadata.GS1_Vendor_Version);
		envelope.appendChild(body);
		body.appendChild(response);
		retDoc.appendChild(envelope);
		return XMLUtil.toString(retDoc);

		// SecurityException, ValidationException, ImplementationException;
	}

	public static String getQueryNames() throws ParserConfigurationException, TransformerException {
		/*
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		 * <soap:Body> <ns3:GetQueryNamesResponse xmlns:ns4="urn:epcglobal:epcis:xsd:1"
		 * xmlns:ns3="urn:epcglobal:epcis-query:xsd:1" xmlns:ns2=
		 * "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">
		 * <item>SimpleEventQuery</item> <item>SimpleMasterDataQuery</item>
		 * </ns3:GetQueryNamesResponse> </soap:Body> </soap:Envelope>
		 */

		Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
		Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
		Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1", "query:GetQueryNamesResult");
		Element item1 = retDoc.createElement("string");
		item1.setTextContent("SimpleEventQuery");
		Element item2 = retDoc.createElement("string");
		item2.setTextContent("SimpleMasterDataQuery");
		response.appendChild(item1);
		response.appendChild(item2);
		envelope.appendChild(body);
		body.appendChild(response);
		retDoc.appendChild(envelope);
		return XMLUtil.toString(retDoc);

		// public List<String> getQueryNames() throws SecurityException,
		// ValidationException, ImplementationException;
	}
	
	public static String subscribe() throws ParserConfigurationException, TransformerException {
		
		Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
		Element body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
		Element response = retDoc.createElementNS("urn:epcglobal:epcis-query:xsd:1", "query:SubscribeResult");
		
		body.appendChild(response);
		envelope.appendChild(body);
		body.appendChild(response);
		retDoc.appendChild(envelope);

		return XMLUtil.toString(retDoc);

	}
}
