package org.oliot.epcis.service.capture;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.message.MessageElement;
import org.apache.commons.io.IOUtils;
import org.oliot.epcglobal.EPC;
import org.oliot.epcis.EPCISEventExtensionType;
import org.oliot.epcis.ObjectEventExtension2Type;
import org.oliot.epcis.ObjectEventExtensionType;
import org.oliot.epcis.ObjectEventType;
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
			if( eventType.equals("AggregationEvent"))
			{
				//TODO: 	
			}else if( eventType.equals("ObjectEvent"))
			{
				ObjectEventType[] events = makeObjectEvents(doc);
				CaptureService captureService = new CaptureService();
				for( int i = 0 ; i < events.length ; i++ )
				{
					captureService.capture(events[i]);
				}
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
	private ObjectEventType[] makeObjectEvents(Document doc) throws ParseException {

		Calendar eventTime = getEventTime(doc);
		Calendar recordTime = new GregorianCalendar();
		String eventTimeZoneOffset = eventTime.getTimeZone().toString();
		Element root = doc.getDocumentElement();
		NodeList memberList = root.getElementsByTagName("member");
		ObjectEventType[] oetArr = new ObjectEventType[memberList.getLength()];
		for(int i = 0 ; i < memberList.getLength() ; i++ )
		{
			ObjectEventType oet = new ObjectEventType();
			oet.setEventTime(eventTime);
			oet.setRecordTime(recordTime);
			oet.setEventTimeZoneOffset(eventTimeZoneOffset);
			List<EPC> epcs = new ArrayList<EPC>();
			Node member = memberList.item(i);
			NodeList children = member.getChildNodes();
			for(int j = 0 ; j < children.getLength() ; j ++ )
			{
				Node child = children.item(j);
				String nodeName = child.getNodeName();
				if( nodeName.equals("epc"))
				{
					epcs.add(new EPC(child.getTextContent()));
				}
				else if( nodeName.equals("extension"))
				{
					ObjectEventExtensionType extension = getObjectEventExtensionType(child);
					oet.setExtension(extension);
				}
			}
			if( epcs != null )
			{
				EPC[] epcArr = new EPC[epcs.size()];
				for( int j = 0 ; j < epcs.size() ; j ++ )
				{
					epcArr[j] = epcs.get(i);
				}
				oet.setEpcList(epcArr);
			}
			oetArr[i] = oet;
		}

		return oetArr;
	}





	private ObjectEventExtensionType getObjectEventExtensionType(Node extension) {
		ObjectEventExtensionType eet = new ObjectEventExtensionType();
		NodeList fieldList = extension.getChildNodes();
		Node fieldNode = null;
		for(int i = 0 ; i < fieldList.getLength() ; i++ )
		{
			if(fieldList.item(i).getNodeName().equals("fieldList"))
			{
				fieldNode = fieldList.item(i);
				break;
			}
		}
		NodeList fields = fieldNode.getChildNodes();
		MessageElement[] meArr = new MessageElement[1];
		MessageElement me = new MessageElement();
		for( int i = 0 ; i < fields.getLength() ; i++ )
		{
			Node field = fields.item(i);
			if( !field.getNodeName().equals("field")) continue;
			String name = null;
			String value = null;
			
			NodeList nodes = field.getChildNodes();
			for(int j = 0 ; j < nodes.getLength() ; j++)
			{
				if(nodes.item(j).getNodeName().equals("name"))
				{
					name = nodes.item(j).getTextContent();
				}
				else if(nodes.item(j).getNodeName().equals("value"))
				{
					value = nodes.item(j).getTextContent();
				}
			}
			
			if( name != null & value != null )
			{
				me.setAttribute(name, value);
			}
		}
		meArr[0] = me;
		ObjectEventExtension2Type extension2 = new ObjectEventExtension2Type();
		extension2.set_any(meArr);
		eet.setExtension(extension2);
		return eet;
	}

	private ObjectEventExtensionType getObjectEventExtensionType(Document doc) {
		NodeList fieldList = doc.getElementsByTagName("fieldList");
		if( fieldList.getLength() == 0 ) return null;

		ObjectEventExtensionType eet = new ObjectEventExtensionType();
		MessageElement[] meArr = new MessageElement[1];
		MessageElement me = new MessageElement();
		//fieldList
		Node fieldNode = fieldList.item(0);
		//field(s)
		NodeList fields = fieldNode.getChildNodes();
		for(int i = 0 ; i < fields.getLength() ; i++ )
		{
			Node field = fields.item(i);
			String name = null;
			String value = null;
			
			NodeList nodes = field.getChildNodes();
			for(int j = 0 ; j < nodes.getLength() ; j++)
			{
				if(nodes.item(j).getNodeName().equals("name"))
				{
					name = nodes.item(j).getTextContent();
				}
				else if(nodes.item(j).getNodeName().equals("value"))
				{
					value = nodes.item(j).getTextContent();
				}
			}
			
			if( name != null & value != null )
			{
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
	private EPCISEventExtensionType getEPCISEventExtensionType(Document doc)
	{
		NodeList fieldList = doc.getElementsByTagName("fieldList");
		if( fieldList.getLength() == 0 ) return null;

		EPCISEventExtensionType eet = new EPCISEventExtensionType();
		MessageElement[] meArr = new MessageElement[1];
		MessageElement me = new MessageElement();
		//fieldList
		Node fieldNode = fieldList.item(0);
		//field(s)
		NodeList fields = fieldNode.getChildNodes();
		for(int i = 0 ; i < fields.getLength() ; i++ )
		{
			Node field = fields.item(i);
			Node first = field.getFirstChild();
			Node second = first.getNextSibling();
			String name;
			String value;
			if( first.getNodeName().equals("name"))
			{
				name = first.getTextContent();
				value = second.getTextContent();
				me.setAttribute(value, name);
			}
			else if( first.getNodeName().equals("value"))
			{
				name = second.getTextContent();
				value = first.getTextContent();
				me.setAttribute(value, name);
			}
		}
		meArr[0] = me;
		eet.set_any(meArr);
		return eet;
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
