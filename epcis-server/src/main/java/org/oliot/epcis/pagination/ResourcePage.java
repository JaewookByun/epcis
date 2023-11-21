package org.oliot.epcis.pagination;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.tdt.TagDataTranslationEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ResourcePage {

	private ArrayList<String> eventResources = new ArrayList<String>();
	private ArrayList<String> vocResources = new ArrayList<String>();
	private ArrayList<String> resources = new ArrayList<String>();
	private String tag;
	private int cursor;
	private boolean isClosed;
	private Timer timer;

	public ResourcePage(String tag, ConcurrentHashSet<String> eventResources, ConcurrentHashSet<String> vocResources) {
		HashSet<String> set = new HashSet<String>();
		HashSet<String> eset = new HashSet<String>();
		synchronized (eventResources) {
			for (String v : eventResources) {
				eset.add(v);
				set.add(v);
			}
		}
		HashSet<String> vset = new HashSet<String>();
		synchronized (vocResources) {
			for (String v : vocResources) {
				vset.add(v);
				set.add(v);
			}
		}
		this.eventResources.addAll(eset);
		this.vocResources.addAll(eset);
		this.resources.addAll(set);
		this.tag = tag;
		cursor = 0;
		isClosed = false;
	}

	public synchronized String getXMLNextPage(int perPage) {
		// null means end of page
		if (isClosed) {
			return null;
		}
		Document message = null;
		try {
			message = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		Element epcs;
		epcs = message.createElement("Resource");
		message.appendChild(epcs);
		int cnt = 0;
		boolean needPagination = false;
		for (; cursor < resources.size(); cursor++) {
			cnt++;
			Element r = message.createElement(tag);
			r.setTextContent(resources.get(cursor));
			epcs.appendChild(r);
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

	public synchronized String getJSONNextPage(int perPage) {
		// null means end of page
		if (isClosed) {
			return null;
		}

		JsonArray jsonResource = new JsonArray();

		int cnt = 0;
		boolean needPagination = false;
		for (; cursor < resources.size(); cursor++) {
			cnt++;
			try {
				jsonResource.add(TagDataTranslationEngine.toDL(resources.get(cursor)));
			} catch (ValidationException e) {
				jsonResource.add(resources.get(cursor));
			}
			if (cnt == perPage) {
				needPagination = true;
				cursor++;
				break;
			}
		}
		if (needPagination == false)
			isClosed = true;

		JsonObject result = new JsonObject();
		result.put("@set", jsonResource);

		return result.toString();
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

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

}
