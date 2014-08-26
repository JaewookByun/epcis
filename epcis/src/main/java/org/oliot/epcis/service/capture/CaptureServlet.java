package org.oliot.epcis.service.capture;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.oliot.epcglobal.EPC;
import org.oliot.epcis.EPCISEventExtensionType;
import org.oliot.epcis.EPCISEventType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
			// Identifying what the event type is
			String eventType = request.getParameter("eventType");
			String backendType = request.getParameter("backendType");
		
		
			// Get ECReport
			InputStream is = request.getInputStream();
					
			// Parsing data
			Document doc = getXMLDocument(is);
			
			// Check whether data is available
			boolean hasContents = isDataAvailable(doc);
			if( hasContents == false ) return;
			
			// Event Type branch
			if( eventType.equals("EPCISEvent"))
			{
				EPCISEventType epcisEvent = makeEPCISEvent(doc);
			}else if( eventType.equals("AggregationEvent"))
			{
				//TODO: 	
			}else if( eventType.equals("ObjectEvent"))
			{
				//TODO: 
			}else if( eventType.equals("QuantityEvent"))
			{
				//TODO: 
			}else if( eventType.equals("TransactionEvent"))
			{
				//TODO: 
			}else if( eventType.equals("TransformationEvent"))
			{
				//TODO: 
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	private EPCISEventType makeEPCISEvent(Document doc) throws ParseException {
		
		//Calendar eventTime,Calendar recordTime,String eventTimeZoneOffset,EPCISEventExtensionType baseExtension
		Calendar eventTime = getEventTime(doc);
		Calendar recordTime = new GregorianCalendar();
		String eventTimeZoneOffset = eventTime.getTimeZone().toString();
		
		
		return null;
	}
	
	private EPCISEventExtensionType getEPCISEventExtensionType(Document doc)
	{
		NodeList fieldList = doc.getElementsByTagName("fieldList");
		
		
		
		
		return null;
	}

	private Calendar getEventTime(Document doc) throws ParseException
	{
		Element root = doc.getDocumentElement();
		String date = root.getAttribute("date");
		if( date == null ) return null;
		Calendar eventTime = Calendar.getInstance();
		// Example: 2014-08-11T19:57:59.717+09:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		eventTime.setTime(sdf.parse(date));
		return eventTime;
	}
	
	
	
	private boolean isDataAvailable(Document doc)
	{
		Element root = doc.getDocumentElement();
		String termination = root.getAttribute("terminationCondition");
		if( !termination.equals("WhenDataAvailable"))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private Document getXMLDocument(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();
		return doc;
	}

	public String getDataFromInputStream(ServletInputStream is) throws IOException
	{
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		String data = writer.toString();
		return data;
	}
	
	
}
