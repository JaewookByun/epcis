package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.oliot.epcglobal.EPC;
import org.oliot.epcis.ActionType;
import org.oliot.epcis.BusinessLocationType;
import org.oliot.epcis.BusinessTransactionType;
import org.oliot.epcis.EPCISEventExtensionType;
import org.oliot.epcis.ObjectEventExtension2Type;
import org.oliot.epcis.ObjectEventExtensionType;
import org.oliot.epcis.ObjectEventType;

import org.oliot.epcis.ReadPointType;
import org.oliot.epcis.SourceDestType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class ALECaptureServlet
 */
public class ALECaptureServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ALECaptureServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			// Identifying what the event type is
			String eventType = request.getParameter("eventType");
			// Default Event Type
			if (eventType == null)
				eventType = "ObjectEvent";

			// Get ECReport
			InputStream is = request.getInputStream();

			String xmlDocumentString = getXMLDocumentString(is);
			InputStream validateStream = getXMLDocumentInputStream(xmlDocumentString);
			// Parsing and Validating data
			ServletConfig conf = getServletConfig();
			String xsdPath = conf.getServletContext().getRealPath("/wsdl");
			xsdPath += "/EPCglobal-ale-1_1-ale.xsd";
			boolean isValidated = validateECReport(validateStream, xsdPath);
			if (isValidated == false) {
				return;
			}
			// Event Type branch
			if (eventType.equals("AggregationEvent")) {
				// TODO:
			} else if (eventType.equals("ObjectEvent")) {
				JSONArray epcisArray = makeObjectEvent(xmlDocumentString, request);
				for (int i = 0; i < epcisArray.length(); i++) {
					JSONObject epcisObject = epcisArray.getJSONObject(i);
					CaptureService capture = new CaptureService();
					capture.capture(epcisObject);
				}
			} else if (eventType.equals("QuantityEvent")) {
				// TODO:
			} else if (eventType.equals("TransactionEvent")) {
				// TODO:
			} else if (eventType.equals("TransformationEvent")) {
				// TODO:
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean validateECReport(InputStream is, String xsdPath) {
		try {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			File xsdFile = new File(xsdPath);
			Schema schema = schemaFactory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			StreamSource xmlSource = new StreamSource(is);
			validator.validate(xmlSource);
			return true;
		} catch (SAXException e) {
			System.out.println("Invalid ECReport: " + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private JSONObject makeObjectEventBase(String ecReportString,
			HttpServletRequest request) {
		try {
			InputStream ecReportStream = getXMLDocumentInputStream(ecReportString);
			Document doc = getXMLDocument(ecReportStream);
			// get extra param : action
			String actionType = request.getParameter("action");
			// Mandatory Field : Default - OBSERVE
			if (actionType == null)
				actionType = "OBSERVE";
			// Optional Field
			String bizStep = request.getParameter("bizStep");
			// Optional Field
			String disposition = request.getParameter("disposition");
			// Optional Field
			String readPoint = request.getParameter("readPoint");
			// Optional Field
			String bizLocation = request.getParameter("bizLocation");
			// Optional Field : Comma Separated List , ~,~,~
			String bizTransactionListStr = request
					.getParameter("bizTransactionList");
			String[] bizTransactionList = null;
			if (bizTransactionListStr != null) {
				bizTransactionList = bizTransactionListStr.split(",");
				for (int i = 0; i < bizTransactionList.length; i++) {
					bizTransactionList[i] = bizTransactionList[i].trim();
				}
			}
			// Optional Field : Comma Separated List , ~,~,~
			String sourceListStr = request.getParameter("sourceList");
			String[] sourceList = null;
			if (sourceListStr != null) {
				sourceList = sourceListStr.split(",");
				for (int i = 0; i < sourceList.length; i++) {
					sourceList[i] = sourceList[i].trim();
				}
			}
			// Optional Field : Comma Separated List , ~,~,~
			String destinationListStr = request.getParameter("destinationList");
			String[] destinationList = null;
			if (destinationListStr != null) {
				destinationList = destinationListStr.split(",");
				for (int i = 0; i < destinationList.length; i++) {
					destinationList[i] = destinationList[i].trim();
				}
			}
			Calendar eventTime = getEventTime(doc);
			Calendar recordTime = new GregorianCalendar();
			String eventTimeZoneOffset = eventTime.getTimeZone().toString();

			JSONObject ecReportObject = XML.toJSONObject(ecReportString);

			// Null Reports Check
			boolean isNull = isReportNull(ecReportObject);
			if (isNull == true) {
				return null;
			}

			// Start to make EPCIS Object
			JSONObject epcisObject = new JSONObject();
			if (eventTime != null) {
				Date eventDate = eventTime.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				String eventTimeStr = sdf.format(eventDate);
				epcisObject.put("eventTime", eventTimeStr);
			}
			if (eventTimeZoneOffset != null) {
				Date eventDate = eventTime.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("XXX");
				String eventTimeZoneOffsetStr = sdf.format(eventDate);
				epcisObject.put("eventTimeZoneOffset", eventTimeZoneOffsetStr);
			}
			if (recordTime != null) {
				Date recordDate = recordTime.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				String recordTimeStr = sdf.format(recordDate);
				epcisObject.put("recordTime", recordTimeStr);
			}
			if (actionType != null)
				epcisObject.put("action", actionType);
			if (bizStep != null)
				epcisObject.put("bizStep", bizStep);
			if (bizLocation != null)
				epcisObject.put("bizLocation", bizLocation);
			if (bizTransactionList != null) {
				JSONArray bizTranArr = new JSONArray();
				for (int i = 0; i < bizTransactionList.length; i++) {
					bizTranArr.put(bizTransactionList[i]);
				}
				epcisObject.put("bizTransactionList", bizTranArr);
			}
			if (disposition != null)
				epcisObject.put("disposition", disposition);
			if (readPoint != null)
				epcisObject.put("readPoint", readPoint);
			if (sourceList != null) {
				JSONArray sourceJSON = new JSONArray();
				for (int i = 0; i < sourceList.length; i++) {
					sourceJSON.put(sourceList[i]);
				}
				epcisObject.put("sourceList", sourceJSON);
			}

			if (destinationList != null) {
				JSONArray destJSON = new JSONArray();
				for (int i = 0; i < destinationList.length; i++) {
					destJSON.put(destinationList[i]);
				}
				epcisObject.put("destinationList", destJSON);
			}
			return epcisObject;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private JSONArray makeEPCISArray(JSONObject epcisObject, JSONObject reportsObj) {
		String reportKey = getJSONKey(reportsObj, "report");
		JSONObject report = reportsObj.getJSONObject(reportKey);
		String groupKey = getJSONKey(report, "group");
		JSONObject group = report.getJSONObject(groupKey);
		String groupListKey = getJSONKey(group, "groupList");
		JSONObject groupList = group.getJSONObject(groupListKey);
		String memberKey = getJSONKey(groupList, "member");
		Object member = groupList.get(memberKey);
		JSONArray epcisArray = new JSONArray();
		// Each member would be one EPCIS Report
		if (member instanceof JSONObject) {
			JSONObject memberObject = (JSONObject) member;
			epcisObject = getEPCISObject(epcisObject, memberObject);
			epcisArray.put(epcisObject);
		} else if (member instanceof JSONArray) {
			JSONArray memberArray = (JSONArray) member;
			for (int i = 0; i < memberArray.length(); i++) {
				JSONObject memberObject = (JSONObject) memberArray
						.getJSONObject(i);
				epcisObject = getEPCISObject(epcisObject, memberObject);
				epcisArray.put(epcisObject);
			}
		}
		return epcisArray;
	}

	private JSONObject getEPCISObject(JSONObject base, JSONObject memberObject) {
		// Make epcList
		String epcKey = getJSONKey(memberObject, "epc");
		String epc = memberObject.getString(epcKey);
		JSONArray epcJSONArr = new JSONArray();
		epcJSONArr.put(epc);
		base.put("epcList", epcJSONArr);

		// Make Extension
		String extensionKey = getJSONKey(memberObject, "extension");
		// Extension
		if (extensionKey != null) {
			JSONObject extension = memberObject.getJSONObject(extensionKey);
			JSONArray extensionArr = getExtensionArray(extension);
			base.put("extension", extensionArr);
		}
		return base;
	}

	private JSONArray getExtensionArray(JSONObject extension) {
		String fieldListKey = getJSONKey(extension, "fieldList");
		JSONObject fieldList = extension.getJSONObject(fieldListKey);
		String fieldKey = getJSONKey(fieldList, "field");
		Object field = fieldList.get(fieldKey);
		JSONArray extensionArr = new JSONArray();
		// Single extension field
		if (field instanceof JSONObject) {
			JSONObject fieldObject = (JSONObject) field;
			JSONObject extensionObj = getExtensionObject(fieldObject);
			extensionArr.put(extensionObj);
		} else if (field instanceof JSONArray) {
			JSONArray fieldArray = (JSONArray) field;
			for (int i = 0; i < fieldArray.length(); i++) {
				JSONObject fieldObject = (JSONObject) fieldArray.get(i);
				JSONObject extensionObj = getExtensionObject(fieldObject);
				extensionArr.put(extensionObj);
			}
		}
		return extensionArr;
	}

	private JSONObject getExtensionObject(JSONObject fieldObject) {
		String nameKey = getJSONKey(fieldObject, "name");
		String name = fieldObject.getString(nameKey);
		String valueKey = getJSONKey(fieldObject, "value");
		Object value = fieldObject.get(valueKey);
		JSONObject extensionObj = new JSONObject();
		extensionObj.put(name, value.toString());
		return extensionObj;
	}

	private JSONArray makeObjectEvent(String ecReportString,
			HttpServletRequest request) {

		// Make Object Event Base
		JSONObject epcisObject = makeObjectEventBase(ecReportString, request);
		if (epcisObject == null)
			return null;

		JSONObject ecReportObject = XML.toJSONObject(ecReportString);

		String ecReportsKey = getJSONKey(ecReportObject, "ECReports");
		JSONObject ecReports = ecReportObject.getJSONObject(ecReportsKey);
		String reportsKey = getJSONKey(ecReports, "reports");
		Object reports = ecReports.get(reportsKey);

		JSONArray epcisArray = new JSONArray();
		// Single Reports
		if (reports instanceof JSONObject) {
			JSONObject reportsObj = (JSONObject) reports;
			epcisArray = makeEPCISArray(epcisObject, reportsObj);
		} else if (reports instanceof JSONArray) // Multiple Reports
		{
			JSONArray reportsArr = (JSONArray) reports;
			for (int i = 0; i < reportsArr.length(); i++) {
				JSONObject reportsObj = reportsArr.getJSONObject(i);
				epcisArray = makeEPCISArray(epcisObject, reportsObj);
			}
		}
		return epcisArray;
	}

	public String getJSONKey(JSONObject jObj, String contain) {
		String[] names = JSONObject.getNames(jObj);
		for (int i = 0; i < names.length; i++) {
			if (names[i].contains(contain)) {
				return names[i];
			}
		}
		return null;
	}

	private boolean isReportNull(JSONObject ecReportObject) {

		// Cannot Be Null
		String ecReportsKey = getJSONKey(ecReportObject, "ECReports");
		JSONObject ecReports = ecReportObject.getJSONObject(ecReportsKey);
		String reportsKey = getJSONKey(ecReports, "reports");
		Object e = ecReports.get(reportsKey);
		if (e.toString().equals("")) {
			return true;
		}
		return false;
	}

	private ObjectEventType[] makeObjectEvents(Document doc,
			HttpServletRequest request) throws ParseException,
			MalformedURIException {

		// get extra param : action
		String actionType = request.getParameter("action");
		// Mandatory Field : Default - OBSERVE
		if (actionType == null)
			actionType = "OBSERVE";
		// Optional Field
		String bizStep = request.getParameter("bizStep");
		// Optional Field
		String disposition = request.getParameter("disposition");
		// Optional Field
		String readPoint = request.getParameter("readPoint");
		// Optional Field
		String bizLocation = request.getParameter("bizLocation");
		// Optional Field : Comma Separated List , ~,~,~
		String bizTransactionListStr = request
				.getParameter("bizTransactionList");
		String[] bizTransactionList = null;
		if (bizTransactionListStr != null) {
			bizTransactionList = bizTransactionListStr.split(",");
			for (int i = 0; i < bizTransactionList.length; i++) {
				bizTransactionList[i] = bizTransactionList[i].trim();
			}
		}
		// Optional Field : Comma Separated List , ~,~,~
		String sourceListStr = request.getParameter("sourceList");
		String[] sourceList = null;
		if (sourceListStr != null) {
			sourceList = sourceListStr.split(",");
			for (int i = 0; i < sourceList.length; i++) {
				sourceList[i] = sourceList[i].trim();
			}
		}
		// Optional Field : Comma Separated List , ~,~,~
		String destinationListStr = request.getParameter("destinationList");
		String[] destinationList = null;
		if (destinationListStr != null) {
			destinationList = destinationListStr.split(",");
			for (int i = 0; i < destinationList.length; i++) {
				destinationList[i] = destinationList[i].trim();
			}
		}

		Calendar eventTime = getEventTime(doc);
		Calendar recordTime = new GregorianCalendar();
		String eventTimeZoneOffset = eventTime.getTimeZone().toString();
		Element root = doc.getDocumentElement();
		NodeList memberList = root.getElementsByTagName("member");
		ObjectEventType[] oetArr = new ObjectEventType[memberList.getLength()];
		for (int i = 0; i < memberList.getLength(); i++) {
			ObjectEventType oet = new ObjectEventType();
			oet.setEventTime(eventTime);
			oet.setRecordTime(recordTime);
			oet.setEventTimeZoneOffset(eventTimeZoneOffset);
			List<EPC> epcs = new ArrayList<EPC>();
			Node member = memberList.item(i);
			NodeList children = member.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);
				String nodeName = child.getNodeName();
				if (nodeName.equals("epc")) {
					epcs.add(new EPC(child.getTextContent()));
				} else if (nodeName.equals("extension")) {
					ObjectEventExtensionType extension = getObjectEventExtensionType(child);
					oet.setExtension(extension);
				}
			}
			if (epcs != null) {
				EPC[] epcArr = new EPC[epcs.size()];
				for (int j = 0; j < epcs.size(); j++) {
					epcArr[j] = epcs.get(i);
				}
				oet.setEpcList(epcArr);
			}
			ActionType action = new ActionType(actionType);
			oet.setAction(action);
			if (bizStep != null)
				oet.setBizStep(new URI(bizStep));
			if (disposition != null)
				oet.setDisposition(new URI(disposition));
			if (readPoint != null)
				oet.setReadPoint(new ReadPointType(new URI(readPoint), null,
						null));
			if (bizLocation != null)
				oet.setBizLocation(new BusinessLocationType(
						new URI(bizLocation), null, null));
			if (bizTransactionList != null) {
				BusinessTransactionType[] bizTranTypeArr = new BusinessTransactionType[bizTransactionList.length];
				for (int j = 0; j < bizTransactionList.length; j++) {
					BusinessTransactionType bizTran = new BusinessTransactionType();
					bizTran.setType(new URI(bizTransactionList[j]));
					bizTranTypeArr[j] = bizTran;
				}
				oet.setBizTransactionList(bizTranTypeArr);
			}

			ObjectEventExtensionType extension = oet.getExtension();
			if (extension == null)
				extension = new ObjectEventExtensionType();

			if (sourceList != null) {
				SourceDestType[] sdTypeArr = new SourceDestType[sourceList.length];
				for (int j = 0; j < sourceList.length; j++) {
					SourceDestType sd = new SourceDestType();
					sd.setType(new URI(sourceList[j]));
					sdTypeArr[j] = sd;
				}
				extension.setSourceList(sdTypeArr);
				oet.setExtension(extension);
			}

			extension = oet.getExtension();
			if (extension == null)
				extension = new ObjectEventExtensionType();

			if (destinationList != null) {
				SourceDestType[] sdTypeArr = new SourceDestType[destinationList.length];
				for (int j = 0; j < destinationList.length; j++) {
					SourceDestType sd = new SourceDestType();
					sd.setType(new URI(destinationList[j]));
					sdTypeArr[j] = sd;
				}
				extension.setDestinationList(sdTypeArr);
				oet.setExtension(extension);
			}
			oetArr[i] = oet;
		}

		return oetArr;
	}

	private ObjectEventExtensionType getObjectEventExtensionType(Node extension) {
		ObjectEventExtensionType eet = new ObjectEventExtensionType();
		NodeList fieldList = extension.getChildNodes();
		Node fieldNode = null;
		for (int i = 0; i < fieldList.getLength(); i++) {
			if (fieldList.item(i).getNodeName().equals("fieldList")) {
				fieldNode = fieldList.item(i);
				break;
			}
		}
		NodeList fields = fieldNode.getChildNodes();
		MessageElement[] meArr = new MessageElement[1];
		MessageElement me = new MessageElement();
		for (int i = 0; i < fields.getLength(); i++) {
			Node field = fields.item(i);
			if (!field.getNodeName().equals("field"))
				continue;
			String name = null;
			String value = null;

			NodeList nodes = field.getChildNodes();
			for (int j = 0; j < nodes.getLength(); j++) {
				if (nodes.item(j).getNodeName().equals("name")) {
					name = nodes.item(j).getTextContent();
				} else if (nodes.item(j).getNodeName().equals("value")) {
					value = nodes.item(j).getTextContent();
				}
			}

			if (name != null & value != null) {
				me.setAttribute(name, value);
			}
		}
		meArr[0] = me;
		ObjectEventExtension2Type extension2 = new ObjectEventExtension2Type();
		extension2.set_any(meArr);
		eet.setExtension(extension2);
		return eet;
	}

	@SuppressWarnings("unused")
	private EPCISEventExtensionType getEPCISEventExtensionType(Document doc) {
		NodeList fieldList = doc.getElementsByTagName("fieldList");
		if (fieldList.getLength() == 0)
			return null;

		EPCISEventExtensionType eet = new EPCISEventExtensionType();
		MessageElement[] meArr = new MessageElement[1];
		MessageElement me = new MessageElement();
		// fieldList
		Node fieldNode = fieldList.item(0);
		// field(s)
		NodeList fields = fieldNode.getChildNodes();
		for (int i = 0; i < fields.getLength(); i++) {
			Node field = fields.item(i);
			Node first = field.getFirstChild();
			Node second = first.getNextSibling();
			String name;
			String value;
			if (first.getNodeName().equals("name")) {
				name = first.getTextContent();
				value = second.getTextContent();
				me.setAttribute(value, name);
			} else if (first.getNodeName().equals("value")) {
				name = second.getTextContent();
				value = first.getTextContent();
				me.setAttribute(value, name);
			}
		}
		meArr[0] = me;
		eet.set_any(meArr);
		return eet;
	}

	private Calendar getEventTime(Document doc) throws ParseException {
		Element root = doc.getDocumentElement();
		String date = root.getAttribute("date");
		if (date == null)
			return null;
		Calendar eventTime = Calendar.getInstance();
		// Example: 2014-08-11T19:57:59.717+09:00
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		eventTime.setTime(sdf.parse(date));
		return eventTime;
	}

	private InputStream getXMLDocumentInputStream(String xmlString) {
		InputStream stream = new ByteArrayInputStream(
				xmlString.getBytes(StandardCharsets.UTF_8));
		return stream;
	}

	private String getXMLDocumentString(InputStream is) {
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			String xmlString = writer.toString();
			return xmlString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Document getXMLDocument(InputStream is) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (SAXException e) {
			System.out.println("Invalid ECReport: " + e.getLocalizedMessage());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

	}

	@SuppressWarnings("unused")
	private Document getXMLDocument(InputStream is, String xsdPath) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			File xsdFile = new File(xsdPath);
			Schema schema = schemaFactory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			Source xmlSource = new StreamSource(is);
			validator.validate(xmlSource);
			dbFactory.setSchema(schemaFactory
					.newSchema(new Source[] { new StreamSource(xsdFile) }));
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (SAXException e) {
			System.out.println("Invalid ECReport: " + e.getLocalizedMessage());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

	}

	public String getDataFromInputStream(ServletInputStream is)
			throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		String data = writer.toString();
		return data;
	}

}
