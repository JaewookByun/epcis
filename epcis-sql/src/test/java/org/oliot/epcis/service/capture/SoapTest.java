package org.oliot.epcis.service.capture;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/soaptest")
public class SoapTest {

	@SuppressWarnings({ "resource", "unused" })
	@RequestMapping(value = "/EventCapture", method = RequestMethod.POST)
	@ResponseBody
	public String test1()
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapCaptureClient.xml");
		CoreCaptureService client = (CoreCaptureService)context.getBean("client");
		//client.capture(epcisDocument);
		return "OK";
		
	}
	@SuppressWarnings({ "resource", "unused" })
	@RequestMapping(value = "/VocabularyCapture", method = RequestMethod.POST)
	@ResponseBody
	public String test2()
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapCaptureClient.xml");
		CoreCaptureService client = (CoreCaptureService)context.getBean("client");
		//client.capture(epcisDocument);
		return "OK";		
	}
}
