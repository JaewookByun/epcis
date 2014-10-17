package org.oliot.example.bridge;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.oliot.epcis_client.CaptureClient;

public class BridgeExample {

	@Test
	public void sendBridgeData1()
	{
		String v1 = "";
		String v2 = "";
		String v3 = "";
		String v4 = "";
		String v5 = "";
		String v6 = "";
				
		CaptureClient cc = new CaptureClient("rest_capture");
		
		BufferedReader br = null;
		String line = "";

		long pTime = 0;
		
		try {
			System.out.println(new GregorianCalendar().toString() + " client started ");
			br = new BufferedReader(new FileReader(
					"./Data/maintower-girder-strain.csv"));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("time"))
					continue;
				String[] strArray = line.split(",");
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm");
				Date date = sdf.parse(strArray[0]);
				long cTime = date.getTime();	// min level
				
				if( pTime == 0 )
				{
					pTime = cTime;
					v1 = strArray[1];
					v2 = strArray[2];
					v3 = strArray[3];
					v4 = strArray[4];
					v5 = strArray[5];
					v6 = strArray[6];
					continue;
				}
				if( pTime == cTime)
				{
					v1 += "," + strArray[1];
					v2 += "," + strArray[2];
					v3 += "," + strArray[3];
					v4 += "," + strArray[4];
					v5 += "," + strArray[5];
					v6 += "," + strArray[6];
					continue;
				}
				if( pTime < cTime)
				{
					pTime = cTime;
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("eventTime", cTime);
					map.put("YJ-3A-DSG-05", v1);
					map.put("YJ-3A-DSG-01", v2);
					map.put("YJ-3A-DSG-06", v3);
					map.put("YJ-3A-DSG-04", v4);
					map.put("YJ-3A-DSG-03", v5);
					map.put("YJ-3A-DSG-02", v6);
					cc.send("urn:epc:id:sgtin:111111111112.1.1", map);
					System.out.println(new GregorianCalendar().getTime().toString() + " published ");
					pTime = 0;
				}	
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {

		}
		
		cc.close();
	}
	
	@Test
	public void sendBridgeData2()
	{
		String[] stringArr = new String[66];
		
		CaptureClient cc = new CaptureClient("rest_capture");
		
		BufferedReader br = null;
		String line = "";

		long pTime = 0;
		
		try {
			System.out.println(new GregorianCalendar().toString() + " client started ");
			br = new BufferedReader(new FileReader(
					"./Data/GPS.csv"));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("time") || line.startsWith(","))
					continue;
				String[] strArray = line.split(",");
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm");
				Date date = sdf.parse(strArray[0]);
				long cTime = date.getTime();	// min level
				
				if( pTime == 0 )
				{
					pTime = cTime;
					for(int i = 0 ; i < stringArr.length ; i++ )
					{
						stringArr[i] = strArray[i+1];
					}
					continue;
				}
				if( pTime == cTime)
				{
					for(int i = 0 ; i < stringArr.length ; i++ )
					{
						stringArr[i] += "," + strArray[i+1];
					}
					continue;
				}
				if( pTime < cTime)
				{
					pTime = cTime;
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("eventTime", cTime);
					
					for(int i = 0 ; i < stringArr.length ; i++ )
					{
						map.put(""+i, stringArr[i]);
					}
					cc.send("urn:epc:id:sgtin:111111111113.1.1", map);
					System.out.println(new GregorianCalendar().getTime().toString() + " published ");
					pTime = 0;
				}	
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {

		}
		
		cc.close();
	}
}
