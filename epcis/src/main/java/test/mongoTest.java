package test;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.oliot.epcis.model.AggregationEventExtension2Type;
import org.oliot.epcis.model.AggregationEventExtensionType;
import org.oliot.epcis.model.QuantityElementType;
import org.oliot.epcis.model.SourceDestType;
import org.oliot.epcis.service.SpringMongoConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/mongoTest")
public class mongoTest {

	@SuppressWarnings("resource")
	@RequestMapping(method=RequestMethod.GET)
	public void MongoTest() throws MalformedURIException
	{
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		
//		ObjectEventType oe = new ObjectEventType();
//		EPC epc = new EPC();
//		epc.set_value("urn:id");
//		EPC[] epcs = new EPC[]{epc};
//		oe.setEpcList(epcs);
//		oe.setAction(ActionType.OBSERVE);
//		oe.setEventTime(new GregorianCalendar());
//		oe.setRecordTime(new GregorianCalendar());
		
		AggregationEventExtension2Type ae = new AggregationEventExtension2Type();
		
		MessageElement me = new MessageElement();
		me.setAttribute("hoho", "hihi");
		MessageElement[] me1 = new MessageElement[]{me};
		ae.set_any(me1);
//		QuantityElementType test = new QuantityElementType();
//		test.setQuantity(1.5f);
//		SourceDestType test = new SourceDestType();
		URI uri = new URI();
		uri.setHost("143.248.53.35");
		uri.setPort(80);
//		test.setType(uri);
		
		SourceDestType sd = new SourceDestType();
		sd.setType(uri);
		SourceDestType[] sdl = new SourceDestType[]{sd};
		
		QuantityElementType qe = new QuantityElementType();
		qe.setEpcClass(uri);
		qe.setQuantity(1.5f);
		qe.setUom(uri);
		
		QuantityElementType[] qel = new QuantityElementType[]{qe};
		AggregationEventExtensionType test = new AggregationEventExtensionType();
		test.setChildQuantityList(qel);
		test.setExtension(ae);
		test.setSourceList(sdl);
		test.setDestinationList(sdl);
		
		
		mongoOperation.save(test);
	}	
}
