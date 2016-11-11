package org.oliot.epcis.serde.mysql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Level;
import org.bson.BsonDateTime;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.oliot.ExtensionMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class WriteUtility {
	

	static void getAnyObject(List<Object> objectList,List<ExtensionMap> extensionMapList){
		WriteUtility.leftNodeNumber=1;
		WriteUtility.rightNodeNumber=1;
		ExtensionMap parentExtensionMap=new ExtensionMap();
		parentExtensionMap.setqName("parent");
		parentExtensionMap.setLeftNodeNumber(WriteUtility.leftNodeNumber);
		for(int i=0; i<objectList.size();i++){
			Object object=objectList.get(i);
			if(object instanceof Element){
				
				Element element=(Element)object;
				int[] level= new int[1];
				//level[0]=0;
				WriteUtility.getAnyMap(element, extensionMapList,1,true);
				
			}
			
		}
		WriteUtility.rightNodeNumber=WriteUtility.leftNodeNumber+1;
		parentExtensionMap.setRightNodeNumber(WriteUtility.rightNodeNumber);
		extensionMapList.add(parentExtensionMap);
	}
	static Boolean getAnyMap(Element element, List<ExtensionMap> extensionMaps, int level, Boolean parent){
		Boolean hirarch=false; 
		
		ExtensionMap extensionMap=new ExtensionMap();
		String nodeNameParent = element.getNodeName();
		String[] checkArrParent =nodeNameParent.split(":");
		if(checkArrParent.length!=2)
			return null;
		String qnameParent=element.getNamespaceURI()+"#"+checkArrParent[1];
		WriteUtility.leftNodeNumber=WriteUtility.rightNodeNumber+1;
		WriteUtility.rightNodeNumber=WriteUtility.leftNodeNumber;
		extensionMap.setqName(qnameParent);
		extensionMap.setPrefixValue(checkArrParent[0]);
		extensionMap.setLeftNodeNumber(WriteUtility.leftNodeNumber);
		

				
		Node firstChild=element.getFirstChild();
		if(firstChild != null){
			if(firstChild instanceof Text){
				
								
				WriteUtility.rightNodeNumber=WriteUtility.leftNodeNumber+1;
				WriteUtility.leftNodeNumber=WriteUtility.rightNodeNumber;
				
				String[] data=new String[2];
				String value=firstChild.getTextContent();
				data=reflectXsi(value,element);
				String dataType=data[1];
				String dataValue=data[0];
				setExteMap(extensionMap,dataType, dataValue);
				if(level==1){
					Configuration.logger.info("Top Level :- "+ qnameParent);
					extensionMap.setInnerValue(false);
				}else{
					Configuration.logger.info("Inner Level :- "+ qnameParent);
					extensionMap.setInnerValue(true);
				}
				
				extensionMap.setRightNodeNumber(WriteUtility.rightNodeNumber);
				extensionMaps.add(extensionMap);
				
			}else if(firstChild instanceof Element){
				hirarch=true;
				Element childNode=null;
				do{
					
					Configuration.logger.info("Do i");
					if(firstChild instanceof Element){
						childNode=(Element)firstChild;
						
						
						getAnyMap(childNode,extensionMaps,0,false);
						


					}
					
					
				}while((firstChild=firstChild.getNextSibling())!=null);
				
				WriteUtility.rightNodeNumber=WriteUtility.leftNodeNumber+1;
				WriteUtility.leftNodeNumber=WriteUtility.rightNodeNumber;
				
				extensionMap.setRightNodeNumber(WriteUtility.rightNodeNumber);
				if(level==1){
					Configuration.logger.info("Top Level :- "+ qnameParent);
					extensionMap.setInnerValue(false);
				}else{
					Configuration.logger.info("Inner Level :- "+ qnameParent);
					extensionMap.setInnerValue(true);
				}
				extensionMaps.add(extensionMap);
				
			}

		}
		return hirarch;
	}
	static void setExteMap(ExtensionMap extensionMap, String dataType, String dataValue){
		//data[0] value, data[1],type
		if(dataType!=null){
			if (dataType.contains("int")) {
				extensionMap.setDataType("int");
				extensionMap.setIntValue(Integer.parseInt(dataValue)); //need conversion here
			} else if (dataType.contains("long")) {
				extensionMap.setDataType("int");
				extensionMap.setIntValue(Integer.parseInt(dataValue));
			} else if (dataType.contains("float")) {
				extensionMap.setDataType("float");
				extensionMap.setFloatValue(Float.parseFloat(dataValue));
			} else if (dataType.contains("double")) {
				extensionMap.setDataType("float");
				extensionMap.setFloatValue(Float.parseFloat(dataValue));
			} else if (dataType.contains("boolean")) {
				extensionMap.setDataType("boolean");
				extensionMap.setBoleanValue(Boolean.parseBoolean(dataValue));
			} else if (dataType.contains("dateTime")) {
				extensionMap.setDataType("dateTime");
				extensionMap.setTimeValue(getDateTimeAny(dataValue));
			}else{
				extensionMap.setDataType("String");
				extensionMap.setStringValue(dataValue);
			}
		}
		
	}
	static String[] reflectXsi(String value, Element element){
		// xsi: int, long, float, double, boolean, dateTime
				// Wedge is evaluated before xsi
		String[] data=new String[2]; //[0] value, [1],type
		
				String type = element.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
				if ( value.indexOf('^') > -1) {
					if (value.contains("int")) {
						String[] restValue=value.split("\\^");
						data[0]=restValue[0].trim();
						data[1]=restValue[1].trim();
					} else if (value.contains("long")) {
						String[] restValue=value.split("\\^");
						data[0]=restValue[0].trim();
						data[1]=restValue[1].trim();
					} else if (value.contains("float")) {
						String[] restValue=value.split("\\^");
						data[0]=restValue[0].trim();
						data[1]=restValue[1].trim();
					} else if (value.contains("double")) {
						String[] restValue=value.split("\\^");
						data[0]=restValue[0].trim();
						data[1]=restValue[1].trim();
					} else if (value.contains("boolean")) {
						String[] restValue=value.split("\\^");
						data[0]=restValue[0].trim();
						data[1]=restValue[1].trim();
					} else if (value.contains("dateTime")) {
						String[] restValue=value.split("\\^");
						data[0]=restValue[0].trim();
						data[1]=restValue[1].trim();
					}
					if(value.contains("time")){
						String[] restValue=value.split("\\^");
						data[0]=restValue[0].trim();
						data[1]="dateTime";
					}
				}else {
					data[0]=value;
					data[1]=type;
				}
		return data;
	}
	
	static String refletXsiType(String value, Element element){
		// xsi: int, long, float, double, boolean, dateTime
				// Wedge is evaluated before xsi
				String type = element.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
				Configuration.logger.info("Type  -------" +type);
				if (type != null && !type.isEmpty() && value.indexOf('^') == -1) {
					if (type.contains("int")) {
						value = value.trim();
						value += "^int";
					} else if (type.contains("long")) {
						value = value.trim();
						value += "^long";
					} else if (type.contains("float")) {
						value = value.trim();
						value += "^float";
					} else if (type.contains("double")) {
						value = value.trim();
						value += "^double";
					} else if (type.contains("boolean")) {
						value = value.trim();
						value += "^boolean";
					} else if (type.contains("dateTime")) {
						value = value.trim();
						value += "^dateTime";
					}
				}
		return value;
	}
	
	static Date getDateTimeAny(String standardDateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			GregorianCalendar eventTimeCalendar = new GregorianCalendar();
			eventTimeCalendar.setTime(sdf.parse(standardDateString));
			return new Date(eventTimeCalendar.getTimeInMillis());
		} catch (ParseException e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				GregorianCalendar eventTimeCalendar = new GregorianCalendar();
				eventTimeCalendar.setTime(sdf.parse(standardDateString));
				return new Date(eventTimeCalendar.getTimeInMillis());
			} catch (ParseException e1) {
				Configuration.logger.log(Level.ERROR, e1.toString());
			}
		}
		// Never Happened
		return null;
	}
	
	public static int leftNodeNumber=1;
	public static int rightNodeNumber=1;
	

}
