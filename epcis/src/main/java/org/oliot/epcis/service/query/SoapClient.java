package org.oliot.epcis.service.query;

import java.net.URI;
import java.net.URL;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.epcis.service.query.mysql.MysqlQueryService;
import org.oliot.epcis.service.query.mysql.QueryOprationBackend;
import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryParam;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.QuerySchedule;
import org.oliot.model.epcis.SubscriptionControls;
import org.oliot.model.oliot.Attribute;
import org.oliot.model.oliot.TransformationEvent;
import org.oliot.model.oliot.Vocabulary;
import org.oliot.model.oliot.VocabularyElement;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class SoapClient {
	
	private static final QName SERVICE_NAME =new QName("http://query.service.epcis.oliot.org/", "SoapQueryServiceService");
    private static final QName PORT_NAME =	new QName("http://query.service.epcis.oliot.org/", "SoapQueryServicePort");
	private static final String WSDL_LOCATION =	"http://localhost:8080/epcis/webservice/QueryService?wsdl";
	//private static final String WSDL_LOCATION =	"http://localhost:8080/epcis/QueryService?wsdl";
	
	public static void main(String[] args) throws Exception{
	

		//URL wsdlURL = new URL(WSDL_LOCATION);
		//Service service = Service.create(wsdlURL, SERVICE_NAME);
		//CoreQueryService port = service.getPort(PORT_NAME,	CoreQueryService.class);
		//CoreQueryService port= new SoapQueryService();
		MysqlQueryService port = new MysqlQueryService();
		QueryParam QueryParam=new QueryParam();
		//QueryParam.setName("eventType");
		//QueryParam.setValue("AggregationEvent");
		//QueryParam.setName("GE_eventTime");
		//QueryParam.setValue("2013-06-08T23:58:56.591-09:00");
		//QueryParam.setName("LT_eventTime");
		//QueryParam.setValue("2013-06-08T23:58:56.591-09:00");
		//QueryParam.setName("EQ_action");
		//QueryParam.setValue("OBSERVE"); 
		//QueryParam.setName("MATCH_parentID");
		//QueryParam.setValue("urn:epc:id:sscc:0614141.1234567890");
		//QueryParam.setName("EQ_source_urn:epcglobal:cbv:sdt:possessing_party");
		//QueryParam.setValue("urn:epc:id:sgln:4012345.00001.0"); 
		QueryParam.setName("eventCountLimit");
		QueryParam.setValue("6");
		QueryParams QueryParams=new QueryParams();
		QueryParams.getParam().add(QueryParam);
		String queryName="SimpleEventQuery";
		
		SubscriptionControls Controls=new SubscriptionControls();
		XMLGregorianCalendar initialRecordTime = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar();
		QuerySchedule querySchedule=new QuerySchedule();
		querySchedule.setSecond("");
		querySchedule.setMinute("2");
		querySchedule.setHour("2");
		querySchedule.setDayOfWeek("2");
		querySchedule.setMonth("2");
		querySchedule.setDayOfMonth("2");
		
		Controls.setSchedule(querySchedule);
		
		URI uri=new URI("test");
		//System.out.println(port.getVendorVersion());
		//System.out.println(port.getStandardVersion());
		
		String queryResults=port.poll(queryName, QueryParams);
		System.out.println(queryResults);
	}

}
