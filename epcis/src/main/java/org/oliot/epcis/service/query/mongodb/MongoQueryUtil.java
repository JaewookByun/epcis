package org.oliot.epcis.service.query.mongodb;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class MongoQueryUtil {

	static DBObject getINQueryObject(String field, Set<String> set) {

		BasicDBList subStringList = new BasicDBList();
		Iterator<String> setIter = set.iterator();
		while (setIter.hasNext()) {
			String str = setIter.next();
			subStringList.add(str);
		}
		if (subStringList.isEmpty() == false) {
			DBObject query = new BasicDBObject();
			query.put(field, new BasicDBObject("$in", subStringList));
			return query;
		}
		return null;
	}

	static DBObject getExistsQueryObject(String field, String str) {
		DBObject query = new BasicDBObject();
		str = encodeMongoObjectKey(str);
		query.put(field + "." + str, new BasicDBObject("$exists", true));
		return query;
	}

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

	static DBObject getVocFamilyQueryObject(String type, String field, String csv) {
		String[] paramValueArr = csv.split(",");
		BasicDBList subObjectList = new BasicDBList();
		for (int i = 0; i < paramValueArr.length; i++) {
			String val = paramValueArr[i].trim();
			DBObject dbo = new BasicDBObject();
			type = encodeMongoObjectKey(type);
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

	@Deprecated
	static DBObject getVocFamilyQueryObjectLegacy(String type, String field, String csv) {
		String[] paramValueArr = csv.split(",");
		BasicDBList subObjectList = new BasicDBList();
		for (int i = 0; i < paramValueArr.length; i++) {
			String val = paramValueArr[i].trim();
			DBObject dbo = new BasicDBObject();
			dbo.put("id", type);
			dbo.put("value", val);
			subObjectList.add(dbo);
		}
		if (subObjectList.isEmpty() == false) {
			DBObject query = new BasicDBObject();
			query.put(field, new BasicDBObject("$in", subObjectList));
			return query;
		}
		return null;
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

	static DBObject getINExtensionQueryObject(String type, String[] fields, String csv) {
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

	static DBObject getCompExtensionQueryObject(String type, String[] fields, String value, String comp) {
		if (comp.equals("GT")) {
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$gt", value));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		} else if (comp.equals("GE")) {
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$gte", value));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		} else if (comp.equals("LT")) {
			BasicDBList subList = new BasicDBList();
			for (int i = 0; i < fields.length; i++) {
				DBObject sub = new BasicDBObject();
				sub.put(fields[i], new BasicDBObject("$lt", value));
				subList.add(sub);
			}
			DBObject subBase = new BasicDBObject();
			subBase.put("$or", subList);
			return subBase;
		} else if (comp.equals("LE")) {
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

	static Set<String> getWDList(Set<String> idSet, String id) {

		ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		DBCollection collection = mongoOperation.getCollection("MasterData");
		// Invoke vocabulary query with EQ_name and includeChildren
		DBObject vocObject = collection.findOne(new BasicDBObject("id", id));
		if (vocObject == null){
			((AbstractApplicationContext) ctx).close();
			return null;
		}			
		idSet.add(id);
		Object childObject = vocObject.get("children");
		if (childObject != null) {
			BasicDBList childList = (BasicDBList) childObject;
			@SuppressWarnings("rawtypes")
			Iterator childIter = childList.iterator();
			while (childIter.hasNext()) {
				Object childObj = childIter.next();
				if (childObj != null) {
					idSet.add(childObj.toString());
				}
			}
		}
		((AbstractApplicationContext) ctx).close();
		return idSet;
	}

	static String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}

	static String decodeMongoObjectKey(String key) {
		key = key.replace("\uff0e", ".");
		return key;
	}
}
