package org.oliot.epcis.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
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
	
	public static InputStream getXMLDocumentInputStream(String xmlString) {
		InputStream stream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
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
}
