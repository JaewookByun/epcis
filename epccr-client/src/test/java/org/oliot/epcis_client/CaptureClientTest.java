package org.oliot.epcis_client;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr
 */
public class CaptureClientTest {

	/**
	 * Default Initialization
	 * Map Data Send
	 */
	@Test
	public void test1()
	{
		CaptureClient cc = new CaptureClient("rest_capture");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("DummyInt", 36);
		map.put("DummyFloat", 36.132);
		map.put("DummyLong", 23431223123123l);
		map.put("DummyString", "hello");
		cc.send("Object", "urn:epc:id:sgtin:111111111111.1.1", map);
		cc.close();
	}
	
	/**
	 * Initialization with URL and port
	 * Map Data Send
	 */
	@Test
	public void test2()
	{
		CaptureClient cc = new CaptureClient("127.0.0.1", 5672, "rest_capture");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("DummyInt", 36);
		map.put("DummyFloat", 36.132);
		map.put("DummyLong", 23431223123123l);
		map.put("DummyString", "hello");
		cc.send("Object", "urn:epc:id:sgtin:111111111111.1.1", map);
		cc.close();
	}
	
	/**
	 * Initialization with URL and port
	 * Map Data Send with specified time
	 */
	@Test
	public void test3()
	{
		CaptureClient cc = new CaptureClient("127.0.0.1", 5672, "rest_capture");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("DummyInt", 36);
		map.put("DummyFloat", 36.132);
		map.put("DummyLong", 23431223123123l);
		map.put("DummyString", "hello");
		GregorianCalendar cal = new GregorianCalendar();
		cc.send("Object", "urn:epc:id:sgtin:111111111111.1.1", cal.getTimeInMillis(), map);
		cc.close();
	}
	
	/**
	 * Initialization with URL and port
	 * Map Data Send with specified time range
	 */
	@Test
	public void test4()
	{
		CaptureClient cc = new CaptureClient("127.0.0.1", 5672, "rest_capture");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("DummyInt", 36);
		map.put("DummyFloat", 36.132);
		map.put("DummyLong", 23431223123123l);
		map.put("DummyString", "hello");
		GregorianCalendar cal = new GregorianCalendar();
		cc.send("Object", "urn:epc:id:sgtin:111111111111.1.1", cal.getTimeInMillis(), cal.getTimeInMillis()+5, map);
		cc.close();
	}
	
	/**
	 * eventTime should be less than or equal to finishTime
	 */
	@Test
	public void test5()
	{
		CaptureClient cc = new CaptureClient("127.0.0.1", 5672, "rest_capture");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("DummyInt", 36);
		map.put("DummyFloat", 36.132);
		map.put("DummyLong", 23431223123123l);
		map.put("DummyString", "hello");
		GregorianCalendar cal = new GregorianCalendar();
		cc.send("Object", "urn:epc:id:sgtin:111111111111.1.1", cal.getTimeInMillis(), cal.getTimeInMillis()-5, map);
		cc.close();
	}
	
	/**
	 * Reconnect test
	 */
	@Test
	public void test6()
	{
		CaptureClient cc = new CaptureClient("rest_capture");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("DummyInt", 36);
		map.put("DummyFloat", 36.132);
		map.put("DummyLong", 23431223123123l);
		map.put("DummyString", "hello");
		cc.send("Object", "urn:epc:id:sgtin:111111111111.1.1", map);
		cc.close();
		cc.reconnect();
		cc.send("Object", "urn:epc:id:sgtin:111111111111.1.1", map);
		cc.close();		
	}
	
	/**
	 * Stress Test with 10000 loop
	 * about 50s , with 10 work queues
	 */
	@Test
	public void test7()
	{
		CaptureClient cc = new CaptureClient("rest_capture");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("DummyString", "hello");
		for( int i = 0 ; i < 10000 ; i++ )
		{
			//cc.send("Object", "urn:epc:id:sgtin:111111111111.1.1", map);
		}
		cc.close();
	}
		
}
