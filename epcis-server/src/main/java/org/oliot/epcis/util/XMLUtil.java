package org.oliot.epcis.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

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
import org.w3c.dom.Node;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class XMLUtil {

	public static String toString(Object object, Class<?> clazz)
			throws ParserConfigurationException, JAXBException, TransformerException {
		org.w3c.dom.Document retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.marshal(object, retDoc);
		String result = "";
		String resultString = XMLUtil.toString(retDoc, true);
		String[] lines = resultString.split("\n");
		for (int i = 1; i < lines.length; i++) {
			result += lines[i] + "\n";
		}
		return result;
	}

	public static String toString(Document xmlDoc, boolean needIndentation) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		if (needIndentation)
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
		else
			trans.setOutputProperty(OutputKeys.INDENT, "no");
		StringWriter sw = new StringWriter();
		trans.transform(new DOMSource(xmlDoc), new StreamResult(sw));
		return sw.toString();
	}

	public static byte[] toByteArray(Document xmlDoc) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.setOutputProperty(OutputKeys.INDENT, "no");
		StringWriter sw = new StringWriter();
		trans.transform(new DOMSource(xmlDoc), new StreamResult(sw));
		return sw.toString().getBytes(StandardCharsets.UTF_8);
	}

	public static InputStream getXMLDocumentInputStream(byte[] xmlByteArray) {
		InputStream stream = new ByteArrayInputStream(xmlByteArray);
		return stream;
	}

	public static String toString(Node node) {
		try {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			StringWriter sw = new StringWriter();
			tf.setOutputProperty(OutputKeys.INDENT, "no");
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
