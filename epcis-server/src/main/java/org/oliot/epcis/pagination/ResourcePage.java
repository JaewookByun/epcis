package org.oliot.epcis.pagination;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
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

import org.oliot.epcis.model.BusinessLocationIDListType;
import org.oliot.epcis.model.BusinessStepListType;
import org.oliot.epcis.model.DispositionListType;
import org.oliot.epcis.model.EPC;
import org.oliot.epcis.model.EPCListType;
import org.oliot.epcis.model.EventTypeListType;
import org.oliot.epcis.model.ReadPointIDListType;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.BusinessStep;
import org.oliot.epcis.model.cbv.Disposition;
import org.oliot.epcis.tdt.TagDataTranslationEngine;
import org.oliot.epcis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.xml.bind.JAXBException;

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
		if (tag.equals("epc")) {
			EPCListType epcList = new EPCListType();
			List<EPC> epcs = epcList.getEpc();
			int cnt = 0;
			boolean needPagination = false;
			for (; cursor < resources.size(); cursor++) {
				cnt++;
				EPC epc = new EPC(resources.get(cursor));
				epcs.add(epc);
				if (cnt == perPage) {
					needPagination = true;
					cursor++;
					break;
				}
			}
			if (needPagination == false)
				isClosed = true;
			try {
				return XMLUtil.toString(epcList, EPCListType.class);
			} catch (ParserConfigurationException | JAXBException | TransformerException e) {
				return null;
			}
		} else if (tag.equals("bizStep")) {
			BusinessStepListType bizStepList = new BusinessStepListType();
			List<String> bizSteps = bizStepList.getBizStep();
			int cnt = 0;
			boolean needPagination = false;
			for (; cursor < resources.size(); cursor++) {
				cnt++;
				bizSteps.add(resources.get(cursor));
				if (cnt == perPage) {
					needPagination = true;
					cursor++;
					break;
				}
			}
			if (needPagination == false)
				isClosed = true;
			try {
				return XMLUtil.toString(bizStepList, BusinessStepListType.class);
			} catch (ParserConfigurationException | JAXBException | TransformerException e) {
				return null;
			}
		} else if (tag.equals("disposition")) {
			DispositionListType dispositionList = new DispositionListType();
			List<String> dispositions = dispositionList.getDisposition();
			int cnt = 0;
			boolean needPagination = false;
			for (; cursor < resources.size(); cursor++) {
				cnt++;
				dispositions.add(resources.get(cursor));
				if (cnt == perPage) {
					needPagination = true;
					cursor++;
					break;
				}
			}
			if (needPagination == false)
				isClosed = true;
			try {
				return XMLUtil.toString(dispositionList, DispositionListType.class);
			} catch (ParserConfigurationException | JAXBException | TransformerException e) {
				return null;
			}
		} else if (tag.equals("bizLocation")) {
			BusinessLocationIDListType bizLocationList = new BusinessLocationIDListType();
			List<String> bizLocations = bizLocationList.getBizLocation();
			int cnt = 0;
			boolean needPagination = false;
			for (; cursor < resources.size(); cursor++) {
				cnt++;
				bizLocations.add(resources.get(cursor));
				if (cnt == perPage) {
					needPagination = true;
					cursor++;
					break;
				}
			}
			if (needPagination == false)
				isClosed = true;
			try {
				return XMLUtil.toString(bizLocationList, BusinessLocationIDListType.class);
			} catch (ParserConfigurationException | JAXBException | TransformerException e) {
				return null;
			}
		} else if (tag.equals("readPoint")) {
			ReadPointIDListType readPointList = new ReadPointIDListType();
			List<String> readPoints = readPointList.getReadPoint();
			int cnt = 0;
			boolean needPagination = false;
			for (; cursor < resources.size(); cursor++) {
				cnt++;
				readPoints.add(resources.get(cursor));
				if (cnt == perPage) {
					needPagination = true;
					cursor++;
					break;
				}
			}
			if (needPagination == false)
				isClosed = true;
			try {
				return XMLUtil.toString(readPointList, ReadPointIDListType.class);
			} catch (ParserConfigurationException | JAXBException | TransformerException e) {
				return null;
			}
		} else {
			EventTypeListType eventTypeList = new EventTypeListType();
			List<String> eventTypes = eventTypeList.getEventType();
			int cnt = 0;
			boolean needPagination = false;
			for (; cursor < resources.size(); cursor++) {
				cnt++;
				eventTypes.add(resources.get(cursor));
				if (cnt == perPage) {
					needPagination = true;
					cursor++;
					break;
				}
			}
			if (needPagination == false)
				isClosed = true;
			try {
				return XMLUtil.toString(eventTypeList, EventTypeListType.class);
			} catch (ParserConfigurationException | JAXBException | TransformerException e) {
				return null;
			}
		}
	}

	public static String getQueryNamesResults(List<String> queries) {
		Document message = null;
		try {
			message = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		Element resources;
		resources = message.createElement("Resource");
		message.appendChild(resources);

		for (String query : queries) {
			Element r = message.createElement("queryName");
			r.setTextContent(query);
			resources.appendChild(r);
		}

		return getXMLString(message);
	}

	/**
	 * "example": { "@context":
	 * "https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld", "type":
	 * "Collection", "member": [
	 * "urn:jaif:id:obj:37SUN321456789A111222333AB+123456789012",
	 * "urn:epc:id:sgtin:0614141.107346.2018",
	 * "https://example.com/01/04012345123456/21/abc234",
	 * "urn:epc:id:sgtin:0614141.107346.2017" ] }
	 * 
	 * @param perPage
	 * @param type
	 * @return
	 */
	public synchronized String getJSONNextPage(int perPage, String type) {
		// null means end of page
		if (isClosed) {
			return null;
		}

		JsonObject jsonResource = new JsonObject();
		jsonResource.put("@context", "https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");
		jsonResource.put("type", "Collection");
		JsonArray member = new JsonArray();

		int cnt = 0;
		boolean needPagination = false;
		for (; cursor < resources.size(); cursor++) {
			cnt++;

			String resource = resources.get(cursor);
			if (type.equals("epc") || type.equals("readPoint") || type.equals("bizLocation")) {
				try {
					member.add(TagDataTranslationEngine.toDL(resource));
				} catch (ValidationException e) {
					member.add(resource);
				}
			} else if (type.equals("bizStep")) {
				try {
					member.add(BusinessStep.getShortVocabularyName(resource));
				} catch (Exception e) {
					member.add(resource);
				}
			} else if (type.equals("disposition")) {
				try {
					member.add(Disposition.getShortVocabularyName(resource));
				} catch (Exception e) {
					member.add(resource);
				}
			} else {
				member.add(resource);
			}

			if (cnt == perPage) {
				needPagination = true;
				cursor++;
				break;
			}
		}
		if (needPagination == false)
			isClosed = true;

		jsonResource.put("member", member);

		return jsonResource.toString();
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
