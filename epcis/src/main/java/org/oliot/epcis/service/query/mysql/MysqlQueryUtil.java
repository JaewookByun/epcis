package org.oliot.epcis.service.query.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MysqlQueryUtil {

	static DBObject getINQueryObject(String field, String csv) {
		String[] eqArr = csv.split(",");
		BasicDBList subStringList = new BasicDBList();
		for (int i = 0; i < eqArr.length; i++) {
			String eqString = eqArr[i].trim();
			subStringList.add(eqString);
		}
		if (subStringList.isEmpty() == false) {
			DBObject query = new BasicDBObject();
			query.put(field, new BasicDBObject("$in", subStringList));
			return query;
		}
		return null;
	}
	
	static  List<String> getStringList(String csv){
		String[] eqArr = csv.split(",");
		List<String> subStringList=new ArrayList<String>();
		for (int i = 0; i < eqArr.length; i++){
			String eqString = eqArr[i].trim();
			subStringList.add(eqString);
		}
		return subStringList;
	}
	static  List<String> getepcList(String csv){
		String[] eqArr = csv.split(",");
		List<String> subStringList=new ArrayList<String>();
		for (int i = 0; i < eqArr.length; i++){
			String eqString = eqArr[i].trim();
			String[] pat=eqString.split(":");
			if(pat[3].equals("idpat")){
				String[] pat2=pat[4].split("\\.");
				String conc=pat[0]+":"+pat[1]+":id"+":"+pat[3]+":"+pat2[0]+".%";
				subStringList.add(conc);
				subStringList.add(eqString);
			}else{
				subStringList.add(eqString);
			}
			
		}
		return subStringList;
	}

	static DBObject getINFamilyQueryObject(String type, String field, String csv) {
		String[] paramValueArr = csv.split(",");
		BasicDBList subObjectList = new BasicDBList();
		for (int i = 0; i < paramValueArr.length; i++) {
			String val = paramValueArr[i].trim();
			DBObject dbo = new BasicDBObject();
			dbo.put(type, val);
			subObjectList.add(dbo);
		}
		if (subObjectList.isEmpty() == false) {
			DBObject query = new BasicDBObject();
			query.put(field, new BasicDBObject("$in", subObjectList));
			return query;
		}
		return null;
	}

	static DBObject getINExtensionQueryObject(String type, String[] fields,
			String csv) {
		String[] paramValueArr = csv.split(",");
		BasicDBList subStringList = new BasicDBList();
		for (int i = 0; i < paramValueArr.length; i++) {
			String val = paramValueArr[i].trim();
			subStringList.add(val);
		}
		if (subStringList.isEmpty() == false) {
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$in", subStringList));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		}
		return null;
	}

	static DBObject getCompExtensionQueryObject(String type, String[] fields,
			String value, String comp) {
		if( comp.equals("GT"))
		{
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$gt", value));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		}else if( comp.equals("GE"))
		{
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$gte", value));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		}else if( comp.equals("LT"))
		{
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$lt", value));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		}else if( comp.equals("LE"))
		{
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$lte", value));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		}
		return null;		
	}

	static DBObject getINQueryObject(String[] fields, String csv) {
		String[] eqArr = csv.split(",");
		BasicDBList subStringList = new BasicDBList();
		for (int i = 0; i < eqArr.length; i++) {
			String eqString = eqArr[i].trim();
			subStringList.add(eqString);
		}
		if (subStringList.isEmpty() == false) {
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$in", subStringList));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		}
		return null;
	}

	static DBObject getRegexQueryObject(String field, String csv) {
		String[] wdArr = csv.split(",");
		BasicDBList subPatternList = new BasicDBList();
		for (int i = 0; i < wdArr.length; i++) {
			String wdString = wdArr[i].trim();
			DBObject subRegex = new BasicDBObject();
			subRegex.put("$regex", Pattern.compile("^" + wdString + ".*"));
			subPatternList.add(new BasicDBObject(field, subRegex));
		}
		DBObject subBase = new BasicDBObject();
		subBase.put("$or", subPatternList);
		return subBase;
	}

}
