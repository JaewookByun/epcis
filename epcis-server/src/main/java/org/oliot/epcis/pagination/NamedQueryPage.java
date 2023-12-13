package org.oliot.epcis.pagination;

import java.io.StringWriter;
import java.util.List;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.oliot.epcis.query.QueryDescription;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class NamedQueryPage {

	private List<org.bson.Document> namedQueries;
	private int cursor;
	private boolean isClosed;
	private Timer timer;

	public NamedQueryPage(List<org.bson.Document> namedQueries) {

		this.namedQueries = namedQueries;
		cursor = 0;
		isClosed = false;
	}

	public synchronized String getXMLNextPage(int perPage) throws ParserConfigurationException {
		// null means end of page
		if (isClosed) {
			return null;
		}
		Document message = null;

		message = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element createQueries = message.createElement("CreateQueries");
		message.appendChild(createQueries);
		int cnt = 0;
		boolean needPagination = false;
		for (; cursor < namedQueries.size(); cursor++) {
			cnt++;
			org.bson.Document namedQueryDocument = namedQueries.get(cursor);
			Element createQuery = QueryDescription.getXMLCreateQuery(message, namedQueryDocument);
			createQueries.appendChild(createQuery);
			if (cnt == perPage) {
				needPagination = true;
				cursor++;
				break;
			}
		}
		if (needPagination == false)
			isClosed = true;

		return toXMLString(message);
	}

	public static String getCreateQueriesResult(List<org.bson.Document> queries) {
		Document message = null;
		try {
			message = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		Element createQueries = message.createElement("CreateQueries");
		message.appendChild(createQueries);

		for (org.bson.Document namedQueryDocument : queries) {
			Element createQuery = QueryDescription.getXMLCreateQuery(message, namedQueryDocument);
			createQueries.appendChild(createQuery);
		}

		return getXMLString(message);
	}

	public synchronized String getJSONNextPage(int perPage) {
		// null means end of page
		if (isClosed) {
			return null;
		}

		int cnt = 0;
		boolean needPagination = false;
		JsonArray createQueries = new JsonArray();
		for (; cursor < namedQueries.size(); cursor++) {
			cnt++;

			org.bson.Document namedQueryDocument = namedQueries.get(cursor);
			JsonObject createQuery = QueryDescription.toJSONCreateQuery(namedQueryDocument);
			createQueries.add(createQuery);

			if (cnt == perPage) {
				needPagination = true;
				cursor++;
				break;
			}
		}
		if (needPagination == false)
			isClosed = true;

		return createQueries.toString();
	}

	public boolean isClosed() {
		return isClosed;
	}

	public String toXMLString(Document message) {
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

	public static String getXMLString(Document message) {
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

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

}
