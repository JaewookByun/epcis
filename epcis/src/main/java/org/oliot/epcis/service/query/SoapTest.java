package org.oliot.epcis.service.query;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.oliot.model.epcis.QueryParam;
import org.oliot.model.epcis.QueryParams;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.SubscriptionControls;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/soaptest")
public class SoapTest {

	@RequestMapping(value = "/getStandardVersion", method = RequestMethod.GET)
	@ResponseBody
	public String test1()
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapClient.xml");
		CoreQueryService client = (CoreQueryService)context.getBean("client");
		String ret = client.getStandardVersion();
		context.close();
		return ret;
		
	}
	@RequestMapping(value = "/getVenderVersion", method = RequestMethod.GET)
	@ResponseBody
	public String test2()
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapClient.xml");
		CoreQueryService client = (CoreQueryService)context.getBean("client");
		String ret = client.getVendorVersion();
		context.close();
		return ret;
		
	}
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/getQueryNames", method = RequestMethod.GET)
	@ResponseBody
	public String test3()
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapClient.xml");
		CoreQueryService client = (CoreQueryService)context.getBean("client");
		List<String> x = client.getQueryNames();
		context.close();		
		return "OK";
		
	}
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/getSubscriptionIDs", method = RequestMethod.GET)
	@ResponseBody
	public String test4()
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapClient.xml");
		CoreQueryService client = (CoreQueryService)context.getBean("client");
		List<String> x = client.getSubscriptionIDs("SimpleEventQuery");
		context.close();		
		return "OK";
		
	}
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/poll", method = RequestMethod.GET)
	@ResponseBody
	public String test5()
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapClient.xml");
		CoreQueryService client = (CoreQueryService)context.getBean("client");
		QueryParams queryParams = new QueryParams();
		List<QueryParam> queryParamList = queryParams.getParam();
		QueryParam qp = new QueryParam();
		qp.setName("MATCH_epc");
		qp.setValue("epc");
		queryParamList.add(qp);
		QueryResults queryResults = client.poll("SimpleEventQuery", queryParams);
		context.close();		
		return "OK";
		
	}
	
	@RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	@ResponseBody
	public String test6()
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapClient.xml");
		CoreQueryService client = (CoreQueryService)context.getBean("client");
		client.unsubscribe("1");
		context.close();		
		return "OK";
	}
	
	@RequestMapping(value = "/subscribe", method = RequestMethod.GET)
	@ResponseBody
	public String test7() throws URISyntaxException
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:SoapClient.xml");
		CoreQueryService client = (CoreQueryService)context.getBean("client");
		QueryParams queryParams = new QueryParams();
		List<QueryParam> queryParamList = queryParams.getParam();
		QueryParam qp = new QueryParam();
		qp.setName("MATCH_epc");
		qp.setValue("epc");
		queryParamList.add(qp);
		QueryParam qp2 = new QueryParam();
		qp2.setName("cronExpression");
		qp2.setValue("0/10 * * * * ?");
		queryParamList.add(qp2);
		SubscriptionControls sc = new SubscriptionControls();
		client.subscribe("SimpleEventQuery", queryParams, new URI("http://localhost:8888/epcis/SubscriptionServlet"), sc, "1");
		
		context.close();		
		return "OK";
	}
}
