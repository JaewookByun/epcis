package org.oliot.epcis.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLUtil {
	public static String toString(Document xmlDoc) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter sw = new StringWriter();
		trans.transform(new DOMSource(xmlDoc), new StreamResult(sw));
		return sw.toString();
	}
	
	public static byte[] getByteArray(String xmlString) {
		return xmlString.getBytes(StandardCharsets.UTF_8);
	}

	public static InputStream getXMLDocumentInputStream(byte[] xmlByteArray) {
		InputStream stream = new ByteArrayInputStream(xmlByteArray);
		return stream;
	}

	public static String toString(Node node) {
		try {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			StringWriter sw = new StringWriter();
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(new DOMSource(node), new StreamResult(sw));
			return sw.toString();
		} catch (TransformerException e) {
			// Never happen or should not happen
			e.printStackTrace();
			return null;
		}
	}

	public static String getCaptureInputType(InputStream is) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			Element root = doc.getDocumentElement();
			String nodeName = root.getNodeName();
			if (nodeName.contains("AggregationEvent"))
				return "AggregationEvent";
			else if (nodeName.contains("ObjectEvent"))
				return "ObjectEvent";
			else if (nodeName.contains("TransactionEvent"))
				return "TransactionEvent";
			else if (nodeName.contains("TransformationEvent"))
				return "TransformationEvent";
			else if (nodeName.contains("AssociationEvent"))
				return "AssociationEvent";
		} catch (Exception e) {

		}
		// Not happened
		return null;
	}
}
