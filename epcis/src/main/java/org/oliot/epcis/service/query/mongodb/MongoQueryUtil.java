package org.oliot.epcis.service.query.mongodb;

import java.util.Iterator;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.oliot.epcis.configuration.Configuration;

import com.mongodb.client.MongoCollection;

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

	static BsonDocument getINQueryObject(String field, Set<String> set) {

		BsonArray subStringList = new BsonArray();
		Iterator<String> setIter = set.iterator();
		while (setIter.hasNext()) {
			String str = setIter.next();
			subStringList.add(new BsonString(str));
		}
		if (subStringList.isEmpty() == false) {
			BsonDocument query = new BsonDocument();
			query.put(field, new BsonDocument("$in", subStringList));
			return query;
		}
		return null;
	}

	static BsonDocument getExistsQueryObject(String field, String str) {
		BsonDocument query = new BsonDocument();
		str = encodeMongoObjectKey(str);
		query.put(field + "." + str, new BsonDocument("$exists", new BsonBoolean(true)));
		return query;
	}

	static BsonDocument getINQueryObject(String field, String csv) {
		String[] eqArr = csv.split(",");
		BsonArray subStringList = new BsonArray();
		for (int i = 0; i < eqArr.length; i++) {
			String eqString = eqArr[i].trim();
			subStringList.add(new BsonString(eqString));
		}
		if (subStringList.isEmpty() == false) {
			BsonDocument query = new BsonDocument();
			query.put(field, new BsonDocument("$in", subStringList));
			return query;
		}
		return null;
	}

	static BsonDocument getVocFamilyQueryObject(String type, String field, String csv) {
		String[] paramValueArr = csv.split(",");
		BsonArray subObjectList = new BsonArray();
		for (int i = 0; i < paramValueArr.length; i++) {
			String val = paramValueArr[i].trim();
			BsonDocument dbo = new BsonDocument();
			type = encodeMongoObjectKey(type);
			dbo.put(type, new BsonString(val));
			subObjectList.add(dbo);
		}
		if (subObjectList.isEmpty() == false) {
			BsonDocument query = new BsonDocument();
			query.put(field, new BsonDocument("$in", subObjectList));
			return query;
		}
		return null;
	}

	@Deprecated
	static BsonDocument getVocFamilyQueryObjectLegacy(String type, String field, String csv) {
		String[] paramValueArr = csv.split(",");
		BsonArray subObjectList = new BsonArray();
		for (int i = 0; i < paramValueArr.length; i++) {
			String val = paramValueArr[i].trim();
			BsonDocument dbo = new BsonDocument();
			dbo.put("id", new BsonString(type));
			dbo.put("value", new BsonString(val));
			subObjectList.add(dbo);
		}
		if (subObjectList.isEmpty() == false) {
			BsonDocument query = new BsonDocument();
			query.put(field, new BsonDocument("$in", subObjectList));
			return query;
		}
		return null;
	}

	static BsonDocument getINFamilyQueryObject(String type, String field, String csv) {
		String[] paramValueArr = csv.split(",");
		BsonArray subObjectList = new BsonArray();
		for (int i = 0; i < paramValueArr.length; i++) {
			String val = paramValueArr[i].trim();
			BsonDocument dbo = new BsonDocument();
			dbo.put(type, new BsonString(val));
			subObjectList.add(dbo);
		}
		if (subObjectList.isEmpty() == false) {
			BsonDocument query = new BsonDocument();
			query.put(field, new BsonDocument("$in", subObjectList));
			return query;
		}
		return null;
	}

	static BsonDocument getINExtensionQueryObject(String type, String[] fields, String csv) {
		String[] paramValueArr = csv.split(",");
		BsonArray subStringList = new BsonArray();
		for (int i = 0; i < paramValueArr.length; i++) {
			String val = paramValueArr[i].trim();
			subStringList.add(converseType(val));
		}
		if (subStringList.isEmpty() == false) {
			BsonArray subList = new BsonArray();
			for (int i = 0; i < fields.length; i++) {
				BsonDocument sub = new BsonDocument();
				sub.put(fields[i], new BsonDocument("$in", subStringList));
				subList.add(sub);
			}
			BsonDocument subBase = new BsonDocument();
			subBase.put("$or", subList);
			return subBase;
		}
		return null;
	}

	static BsonDocument getCompExtensionQueryObject(String type, String[] fields, String value, String comp) {
		if (comp.equals("GT")) {
			BsonArray subList = new BsonArray();
			for (int i = 0; i < fields.length; i++) {
				BsonDocument sub = new BsonDocument();
				sub.put(fields[i], new BsonDocument("$gt", converseType(value)));
				subList.add(sub);
			}
			BsonDocument subBase = new BsonDocument();
			subBase.put("$or", subList);
			return subBase;
		} else if (comp.equals("GE")) {
			BsonArray subList = new BsonArray();
			for (int i = 0; i < fields.length; i++) {
				BsonDocument sub = new BsonDocument();
				sub.put(fields[i], new BsonDocument("$gte", converseType(value)));
				subList.add(sub);
			}
			BsonDocument subBase = new BsonDocument();
			subBase.put("$or", subList);
			return subBase;
		} else if (comp.equals("LT")) {
			BsonArray subList = new BsonArray();
			for (int i = 0; i < fields.length; i++) {
				BsonDocument sub = new BsonDocument();
				sub.put(fields[i], new BsonDocument("$lt", converseType(value)));
				subList.add(sub);
			}
			BsonDocument subBase = new BsonDocument();
			subBase.put("$or", subList);
			return subBase;
		} else if (comp.equals("LE")) {
			BsonArray subList = new BsonArray();
			for (int i = 0; i < fields.length; i++) {
				BsonDocument sub = new BsonDocument();
				sub.put(fields[i], new BsonDocument("$lte", converseType(value)));
				subList.add(sub);
			}
			BsonDocument subBase = new BsonDocument();
			subBase.put("$or", subList);
			return subBase;
		}
		return null;
	}

	static BsonDocument getINQueryObject(String[] fields, String csv) {
		String[] eqArr = csv.split(",");
		BsonArray subStringList = new BsonArray();
		for (int i = 0; i < eqArr.length; i++) {
			String eqString = eqArr[i].trim();
			subStringList.add(new BsonString(eqString));
		}
		if (subStringList.isEmpty() == false) {
			BsonArray subList = new BsonArray();
			for (int i = 0; i < fields.length; i++) {
				BsonDocument sub = new BsonDocument();
				sub.put(fields[i], new BsonDocument("$in", subStringList));
				subList.add(sub);
			}
			BsonDocument subBase = new BsonDocument();
			subBase.put("$or", subList);
			return subBase;
		}
		return null;
	}

	static BsonDocument getRegexQueryObject(String field, String csv) {
		String[] wdArr = csv.split(",");
		BsonArray subPatternList = new BsonArray();
		for (int i = 0; i < wdArr.length; i++) {
			String wdString = wdArr[i].trim();
			BsonDocument subRegex = new BsonDocument();
			subRegex.put("$regex", new BsonRegularExpression("^" + wdString + ".*"));
			subPatternList.add(new BsonDocument(field, subRegex));
		}
		BsonDocument subBase = new BsonDocument();
		subBase.put("$or", subPatternList);
		return subBase;
	}

	static Set<String> getWDList(Set<String> idSet, String id) {

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("MasterData", BsonDocument.class);
		// Invoke vocabulary query with EQ_name and includeChildren
		BsonDocument vocObject = collection.find(new BsonDocument("id", new BsonString(id))).first();
		if (vocObject == null) {
			return null;
		}
		idSet.add(id);
		BsonArray childObject = vocObject.get("children").asArray();
		if (childObject != null) {
			@SuppressWarnings("rawtypes")
			Iterator childIter = childObject.iterator();
			while (childIter.hasNext()) {
				Object childObj = childIter.next();
				if (childObj != null) {
					idSet.add(childObj.toString());
				}
			}
		}
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

	static BsonValue converseType(String value) {
		String[] valArr = value.split("\\^");
		if (valArr.length != 2) {
			return new BsonString(value);
		}
		try {
			String type = valArr[1];
			if (type.equals("int")) {
				return new BsonInt32(Integer.parseInt(valArr[0]));
			} else if (type.equals("long")) {
				return new BsonInt64(Long.parseLong(valArr[0]));
			} else if (type.equals("double")) {
				return new BsonDouble(Double.parseDouble(valArr[0]));
			} else if (type.equals("boolean")) {
				return new BsonBoolean(Boolean.parseBoolean(valArr[0]));
			} else {
				return new BsonString(value);
			}
		} catch (NumberFormatException e) {
			return new BsonString(value);
		}
	}
}
