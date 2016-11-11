package org.oliot.epcis.serde.mysql;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.oliot.ExtensionMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReaderUtility {
	
	

	
	

static Element putAny(Map<Integer, ExtensionMap> map, int left,int[] level, Element element, List<Object> elementList,Document doc) throws ParserConfigurationException{ //, Element element, Boolean parent
		
		
		
		
		if(map.get(left).getRightNodeNumber()==map.get(left).getLeftNodeNumber()+1){
			String leafQname=map.get(left).getqName();
			//Configuration.logger.info(leafQname);
			WriteUtility.rightNodeNumber=WriteUtility.leftNodeNumber+1;
			WriteUtility.leftNodeNumber=WriteUtility.rightNodeNumber;
			String leafnamespaceURI="";
			String leafLocalName="";
			if(leafQname.split("#").length>0)
				leafnamespaceURI=leafQname.split("#")[0];
			if(leafQname.split("#").length>1)
				leafLocalName=leafQname.split("#")[1];
			String leafPrefix=map.get(left).getPrefixValue();
			if(leafPrefix==null){
				leafPrefix="sample";
			}
			String leafValue=ReaderUtility.getValueStringFormat(map.get(left));
			Element leafElement=doc.createElement(leafPrefix+":"+leafLocalName);
			if(leafPrefix !=null && leafnamespaceURI != null){
				leafElement.setAttribute("xmlns:"+leafPrefix, leafnamespaceURI);
				//leafElement.setAttribute("xsi:type:"+leafPrefix, leafnamespaceURI);
			}
			if(leafValue != null){
				leafElement.setTextContent(leafValue);
			}
			
			if(level[0]==0){
				//Configuration.logger.info(map.get(left).getqName()+ "  added to object List");
				elementList.add(leafElement);
			}else{
				//Configuration.logger.info(map.get(left).getqName()+ "  added to parent element");
				level[0]--;
				element.appendChild(leafElement);
			}
			
		}else{
			String parentQname=map.get(left).getqName();			
			String parentNamespaceURI=parentQname.split("#")[0];
			String parentLocalName="";
			if(map.get(left).getqName().equals("parent")){
				//do nothing
			}else{
				parentLocalName=parentQname.split("#")[1];
			}
			String parentPrefix="myexample";
			Element parentElement=doc.createElement(parentPrefix+":"+parentLocalName);
			if(parentPrefix !=null && parentNamespaceURI != null){
				parentElement.setAttribute("xmlns:"+parentPrefix, parentNamespaceURI);
			}
			int node=map.get(left).getRightNodeNumber();
			if(map.get(left).getqName().equals("parent")){
				//do nothing
			}else{				
				//Configuration.logger.info("Parent: "+map.get(left).getqName());
				level[0]++;
				//Element parentElement
			}
			do{
				//int next=left+1;
				if(level[0]==0 && !map.get(left).getqName().equals("parent")){
					level[0]++;
				}
				WriteUtility.leftNodeNumber++;
				int next=WriteUtility.leftNodeNumber;
				putAny( map, next,level,parentElement,elementList,doc);
			}while(WriteUtility.leftNodeNumber+1<node);
			
			//Configuration.logger.info("After the sequence"+map.get(left).getqName());
			//Configuration.logger.info("int value"+ level[0]);
			WriteUtility.rightNodeNumber=WriteUtility.leftNodeNumber+1;
			WriteUtility.leftNodeNumber=WriteUtility.rightNodeNumber;
			
			if(level[0]==0 && !map.get(left).getqName().equals("parent")){
				//Configuration.logger.info(map.get(left).getqName()+ "  added to object List");
				elementList.add(parentElement);
			}else if(!map.get(left).getqName().equals("parent")){
				//Configuration.logger.info(map.get(left).getqName()+ "  added to parent element");
				level[0]--;
				element.appendChild(parentElement);
			}
		}
		return null;
	}

	static String getValueStringFormat(ExtensionMap extensionMap){
		String value="";
		String type=extensionMap.getDataType();
		try{
			if(type!=null){
				if(type.equals("int")){
					value=Integer.toString(extensionMap.getIntValue());
				}else if(type.equals("float")){
					value=Float.toString(extensionMap.getFloatValue());
				}else if(type.equals("boolean")){
					value=Boolean.toString(extensionMap.isBoleanValue());
				}else if(type.equals("dateTime")){
					value=extensionMap.getTimeValue().toString();
				}else{
					value=extensionMap.getStringValue();
				}
			}
		}catch(Exception e){
			value=extensionMap.getStringValue();
		}

		return value;
	}
	
	public static void getValueTypeList(List<PairType> ValueTypeList, List<String> valueList){
		if(valueList!=null){
			for(int i=0; i<valueList.size(); i++){
				String[] valueArr=valueList.get(i).split("\\^");
				PairType pairType=new PairType();
				if(valueArr.length>1){
					String value=valueArr[0].trim();
					String type=valueArr[1].trim();
					
					if(type.equals("int")){
						pairType.setType("int");
						pairType.setValue(value);
						ValueTypeList.add(pairType);
					}else if(type.equals("long")){
						pairType.setType("int");
						pairType.setValue(value);
						ValueTypeList.add(pairType);
					}else if(type.equals("float")){
						pairType.setType("float");
						pairType.setValue(value);
						ValueTypeList.add(pairType);
					}else if(type.equals("double")){
						pairType.setType("float");
						pairType.setValue(value);
						ValueTypeList.add(pairType);
					}else if(type.equals("boolean")){
						pairType.setType("boolean");
						pairType.setValue(value);
						ValueTypeList.add(pairType);
					}else if(type.equals("dateTime")){
						pairType.setType("dateTime");
						pairType.setValue(value);
						ValueTypeList.add(pairType);
					}else if(type.equals("time")){
						pairType.setType("dateTime");
						pairType.setValue(value);
						ValueTypeList.add(pairType);
					}else {
						pairType.setType("String");
						pairType.setValue(value);
						ValueTypeList.add(pairType);
					}
					
				}else{
					pairType.setType("String");
					pairType.setValue("value");
					ValueTypeList.add(pairType);
				}
			}
		}
		
	}
	
	public static void extensionHierarchCriteria_EQ_qName(Criteria criteria, String qName, String alias, boolean isInner){
		try{
			criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
					Restrictions.eq(alias+".innerValue",isInner)));
		}catch(Exception e ){
			Configuration.logger.log(null, e.getMessage());
		}
	}
	
	
	public static void extensionHierarchCriteria_EQ(Criteria criteria, List<PairType> ValueTypeList, String qName, String alias, boolean isInner){
		try{
			 for(int i=0; i<ValueTypeList.size(); i++){
			    	String dataType=ValueTypeList.get(i).getType();
			    	if(dataType.equals("int")){
			    		int intValue=Integer.parseInt(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".intValue", intValue))));

			    	}else if(dataType.equals("float")){
			    		float floatValue=Float.parseFloat(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".floatValue", floatValue))));
			    	}else if(dataType.equals("boolean")){
			    		Boolean booleanValue=Boolean.parseBoolean(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".boleanValue", booleanValue))));
			    			
			    	}else if(dataType.equals("dateTime")){
			    		Date dateValue=WriteUtility.getDateTimeAny(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".timeValue", dateValue))));
			    	}else{
			    		String stringValue=ValueTypeList.get(i).getValue();
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".stringValue", stringValue))));
			    	}
			    	
			    }
		}catch(Exception e ){
			Configuration.logger.log(null, e.getMessage());
		}
	}
	
	public static void extensionHierarchCriteria_GT(Criteria criteria, List<PairType> ValueTypeList, String qName, String alias, boolean isInner){
		try{
			 for(int i=0; i<ValueTypeList.size(); i++){
			    	String dataType=ValueTypeList.get(i).getType();
			    	if(dataType.equals("int")){
			    		int intValue=Integer.parseInt(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.gt(alias+".intValue", intValue))));

			    	}else if(dataType.equals("float")){
			    		float floatValue=Float.parseFloat(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.gt(alias+".floatValue", floatValue))));
			    	}else if(dataType.equals("dateTime")){
			    		Date dateValue=WriteUtility.getDateTimeAny(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.gt(alias+".timeValue", dateValue))));
			    	}else{
			    		String stringValue=ValueTypeList.get(i).getValue();
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".stringValue", stringValue))));
			    	}
			    	
			    }
		}catch(Exception e ){
			Configuration.logger.log(null, e.getMessage());
		}
	}
	
	public static void extensionHierarchCriteria_LT(Criteria criteria, List<PairType> ValueTypeList, String qName, String alias, boolean isInner){
		try{
			 for(int i=0; i<ValueTypeList.size(); i++){
			    	String dataType=ValueTypeList.get(i).getType();
			    	if(dataType.equals("int")){
			    		int intValue=Integer.parseInt(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.lt(alias+".intValue", intValue))));

			    	}else if(dataType.equals("float")){
			    		float floatValue=Float.parseFloat(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.lt(alias+".floatValue", floatValue))));
			    	}else if(dataType.equals("dateTime")){
			    		Date dateValue=WriteUtility.getDateTimeAny(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.lt(alias+".timeValue", dateValue))));
			    	}else{
			    		String stringValue=ValueTypeList.get(i).getValue();
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".stringValue", stringValue))));
			    	}
			    	
			    }
		}catch(Exception e ){
			Configuration.logger.log(null, e.getMessage());
		}
	}
	
	public static void extensionHierarchCriteria_GE(Criteria criteria, List<PairType> ValueTypeList, String qName, String alias, boolean isInner){
		try{
			 for(int i=0; i<ValueTypeList.size(); i++){
			    	String dataType=ValueTypeList.get(i).getType();
			    	if(dataType.equals("int")){
			    		int intValue=Integer.parseInt(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.ge(alias+".intValue", intValue))));

			    	}else if(dataType.equals("float")){
			    		float floatValue=Float.parseFloat(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.ge(alias+".floatValue", floatValue))));
			    	}else if(dataType.equals("dateTime")){
			    		Date dateValue=WriteUtility.getDateTimeAny(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.ge(alias+".timeValue", dateValue))));
			    	}else{
			    		String stringValue=ValueTypeList.get(i).getValue();
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".stringValue", stringValue))));
			    	}
			    	
			    }
		}catch(Exception e ){
			Configuration.logger.log(null, e.getMessage());
		}
	}
	
	public static void extensionHierarchCriteria_LE(Criteria criteria, List<PairType> ValueTypeList, String qName, String alias, boolean isInner){
		try{
			 for(int i=0; i<ValueTypeList.size(); i++){
			    	String dataType=ValueTypeList.get(i).getType();
			    	if(dataType.equals("int")){
			    		int intValue=Integer.parseInt(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.le(alias+".intValue", intValue))));

			    	}else if(dataType.equals("float")){
			    		float floatValue=Float.parseFloat(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.le(alias+".floatValue", floatValue))));
			    	}else if(dataType.equals("dateTime")){
			    		Date dateValue=WriteUtility.getDateTimeAny(ValueTypeList.get(i).getValue());
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.le(alias+".timeValue", dateValue))));
			    	}else{
			    		String stringValue=ValueTypeList.get(i).getValue();
			    		criteria.add(Restrictions.and(Restrictions.eq(alias+".qName", qName),
			    				Restrictions.and(Restrictions.eq(alias+".innerValue",isInner),
					    				Restrictions.eq(alias+".stringValue", stringValue))));
			    	}
			    	
			    }
		}catch(Exception e ){
			Configuration.logger.log(null, e.getMessage());
		}
	}
}
