package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.oliot.epcis.service.ConfigurationServlet;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class CaptureServlet
 */
public class CaptureServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CaptureServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			ConfigurationServlet.logger.info(" EPCISDocument Capture Started.... ");
			
			// Get EPCIS Document
			InputStream is = request.getInputStream();

			String xmlDocumentString = getXMLDocumentString(is);
			InputStream validateStream = getXMLDocumentInputStream(xmlDocumentString);
			// Parsing and Validating data
			ServletConfig conf = getServletConfig();
			String xsdPath = conf.getServletContext().getRealPath("/wsdl");
			xsdPath += "/EPCglobal-epcis-1_1.xsd";
			boolean isValidated = validateECReport(validateStream, xsdPath);
			if (isValidated == false) {
				ConfigurationServlet.logger.log(Level.ERROR, " Incoming EPCISDocument is not validated ");
				return;
			}
			ConfigurationServlet.logger.log(Level.INFO, " Incoming EPCISDocument is validated ");
			
			JSONObject epcisObject = XML.toJSONObject(xmlDocumentString);
			String epcisDocumentKey = getJSONKey(epcisObject, "EPCISDocument");
			JSONObject epcisDocument = epcisObject.getJSONObject(epcisDocumentKey);
			Object epcisBody = epcisDocument.get("EPCISBody");
			if( epcisBody instanceof String )
			{
				ConfigurationServlet.logger.log(Level.INFO, " Null Report ");
				return;
			}
			JSONObject epcisBodyObject = (JSONObject) epcisBody;
			String eventListKey = getJSONKey(epcisBodyObject, "EventList");
			if( eventListKey == null )
			{
				ConfigurationServlet.logger.log(Level.INFO, " Null Report ");
				return;
			}
			JSONObject eventList = epcisBodyObject.getJSONObject("EventList");
			
			String[] eventNames = JSONObject.getNames(eventList);
			
			for( int i = 0 ; i < eventNames.length ; i++)
			{
				String eventName = eventNames[i];
				switch(eventName)
				{
					case "ObjectEvent":
										doObjectEventCapture(eventList.get(eventName));
										break;
					case "AggregationEvent":
										doAggregationEventCapture(eventList.get(eventName));
										break;
					case "QuantityEvent":
										doQuantityEventCapture(eventList.get(eventName));
										break;
					case "TransactionEvent":
										doTransactionEventCapture(eventList.get(eventName));
										break;
				}
			}
			System.out.println();
		} catch (IOException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
	}
	
	public void updateObjectList(JSONObject jObj, String key)
	{
		// jObj e.g. : epcList
		// objList e.g.: epc
		Object objList = jObj.get(key);
		// Need to be updated 
		if( !(objList instanceof JSONArray) )
		{
			JSONArray jArr = new JSONArray();
			jArr.put(objList);
			jObj.put(key, jArr);
		
		}
		return;
	}
	
	public JSONObject prepareObjectEvent(JSONObject objectEventObj)
	{
		JSONObject epcList = objectEventObj.getJSONObject("epcList");
		updateObjectList(epcList, "epc");
		if( !objectEventObj.isNull("bizTransactionList"))
		{
			JSONObject bizTransactionList = objectEventObj.getJSONObject("bizTransactionList");
			updateObjectList(bizTransactionList, "bizTransactionList");
		}
		if( !objectEventObj.isNull("extension"))	//Extension Field exists
		{
			JSONObject extension = objectEventObj.getJSONObject("extension");
			// QuantityList update
			if( !extension.isNull("quantityList"))
			{
				JSONObject quantityList = extension.getJSONObject("quantityList");
				updateObjectList(quantityList, "quantityElement");
			}
			// SourceList update
			if( !extension.isNull("sourceList"))
			{
				JSONObject sourceList = extension.getJSONObject("sourceList");
				updateObjectList(sourceList, "source");
			}
			// DestinationList update
			if( !extension.isNull("destinationList"))
			{
				JSONObject destinationList = extension.getJSONObject("destinationList");
				updateObjectList(destinationList, "destination");
			}
		}
		return objectEventObj;
	}
	public void doObjectEventCapture(Object objectEvent)
	{
		if( objectEvent instanceof JSONObject )
		{
			JSONObject objectEventObj = (JSONObject) objectEvent;
			objectEventObj = prepareObjectEvent(objectEventObj);
			CaptureService captureService = new CaptureService();
			captureService.capture(objectEventObj);
		}
		else if( objectEvent instanceof JSONArray )
		{
			JSONArray objectEventArr = (JSONArray) objectEvent;
			for( int i = 0 ; i < objectEventArr.length() ; i++ )
			{
				JSONObject objectEventObj = objectEventArr.getJSONObject(i);
				objectEventObj = prepareObjectEvent(objectEventObj);
				CaptureService captureService = new CaptureService();
				captureService.capture(objectEventObj);
			}
		}
	}
	public void doAggregationEventCapture(Object aggregationEvent)
	{
		
	}
	public void doQuantityEventCapture(Object quantityEvent)
	{
		
	}
	public void doTransactionEventCapture(Object transactionEvent)
	{
		
	}
	
	public void updateBaseExtensionMap(JSONObject parent, Map<String, String> extensionMap)
	{
		String extensionKey = getJSONKey(parent, "baseExtension");
		if( extensionKey == null )
		{
			return;
		}
		JSONObject extension = parent.getJSONObject(extensionKey);
		String[] extensionNames = JSONObject.getNames(extension);
		
		for( int i = 0 ; i < extensionNames.length ; i++ )
		{
			String name = extensionNames[i];
			String value = extension.get(name).toString();
			extensionMap.put(name, value);
		}
		parent.remove(extensionKey);
	}
	
	public void updateExtensionMap(JSONObject parent, Map<String, String> extensionMap)
	{
		String extensionKey = getJSONKey(parent, "extension");
		if( extensionKey == null )
		{
			return;
		}
		JSONObject extension = parent.getJSONObject(extensionKey);
		String[] extensionNames = JSONObject.getNames(extension);
		
		for( int i = 0 ; i < extensionNames.length ; i++ )
		{
			String name = extensionNames[i];
			String value = extension.get(name).toString();
			extensionMap.put(name, value);
		}
		parent.remove(extensionKey);
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

	private InputStream getXMLDocumentInputStream(String xmlString) {
		InputStream stream = new ByteArrayInputStream(
				xmlString.getBytes(StandardCharsets.UTF_8));
		return stream;
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
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return false;
		} catch (IOException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return false;
		}
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
}
