package org.oliot.epcis.service.query.mongodb;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
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
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class MongoQueryUtil {

	static BsonDocument getFamilyQueryObject(String type, String[] fieldArr, String csv) {

		BsonArray orQueries = new BsonArray();
		for (String field : fieldArr) {
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
				orQueries.add(query);
			}
		}
		if (orQueries.size() != 0) {
			BsonDocument queryObject = new BsonDocument();
			queryObject.put("$or", orQueries);
			return queryObject;
		} else {
			return null;
		}
	}

	static BsonArray getWDParamBsonArray(String csv) {
		BsonArray paramArray = new BsonArray();
		String[] paramValueArr = csv.split(",");
		for (String paramValue : paramValueArr) {
			String param = paramValue.trim();
			if (param.split(",").length == 1 && param.split("\\^").length == 2
					&& param.split("\\^")[1].trim().equals("regex")) {
				String regex = param.split("\\^")[0].trim();
				BsonRegularExpression regexValue = new BsonRegularExpression("^" + regex + "$");
				paramArray.add(regexValue);
			} else {
				paramArray.add(new BsonString(param));
			}
		}
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("MasterData",
				BsonDocument.class);

		Iterator<BsonValue> paramIterator = paramArray.iterator();
		Set<BsonValue> childParamSet = new HashSet<BsonValue>();
		while (paramIterator.hasNext()) {
			BsonValue param = paramIterator.next();
			BsonDocument vocObject = null;
			if (param instanceof BsonRegularExpression) {
				BsonRegularExpression regexParam = param.asRegularExpression();
				vocObject = collection.find(new BsonDocument("id", regexParam)).first();

			} else {
				BsonString stringParam = param.asString();
				vocObject = collection.find(new BsonDocument("id", stringParam)).first();
			}
			if (vocObject != null) {
				BsonArray childObject = vocObject.get("children").asArray();
				Iterator<BsonValue> childIterator = childObject.iterator();
				while (childIterator.hasNext()) {
					BsonString child = childIterator.next().asString();
					childParamSet.add(child);
				}
			}
		}
		Iterator<BsonValue> childParamIterator = childParamSet.iterator();
		while (childParamIterator.hasNext()) {
			paramArray.add(childParamIterator.next().asString());
		}

		return paramArray;
	}

	static BsonArray getParamBsonArray(String csv) {
		BsonArray paramArray = new BsonArray();
		String[] paramValueArr = csv.split(",");
		for (String paramValue : paramValueArr) {
			String param = paramValue.trim();
			paramArray.add(converseType(param));
		}
		return paramArray;
	}

	static BsonValue converseType(String value) {
		String[] valArr = value.split("\\^");
		if (valArr.length != 2) {
			return new BsonString(value);
		}
		try {
			String type = valArr[1].trim();
			if (type.equals("int")) {
				return new BsonInt32(Integer.parseInt(valArr[0]));
			} else if (type.equals("long")) {
				return new BsonInt64(Long.parseLong(valArr[0]));
			} else if (type.equals("double")) {
				return new BsonDouble(Double.parseDouble(valArr[0]));
			} else if (type.equals("boolean")) {
				return new BsonBoolean(Boolean.parseBoolean(valArr[0]));
			} else if (type.equals("regex")) {
				return new BsonRegularExpression("^" + valArr[0] + "$");
			} else if (type.equals("float")) {
				return new BsonDouble(Double.parseDouble(valArr[0]));
			} else if (type.equals("dateTime")) {
				BsonDateTime time = MongoQueryService.getTimeMillis(valArr[0]);
				if (time != null)
					return time;
				return new BsonString(value);
			} else {
				return new BsonString(value);
			}
		} catch (NumberFormatException e) {
			return new BsonString(value);
		}
	}

	static BsonDocument getQueryObject(String[] fieldArr, BsonArray paramArray) {

		BsonArray orQueries = new BsonArray();
		for (String field : fieldArr) {
			Iterator<BsonValue> paramIterator = paramArray.iterator();
			BsonArray pureStringParamArray = new BsonArray();
			while (paramIterator.hasNext()) {
				BsonValue param = paramIterator.next();
				if (param instanceof BsonRegularExpression) {
					BsonDocument regexQuery = new BsonDocument(field, new BsonDocument("$regex", param));
					orQueries.add(regexQuery);
				} else {
					pureStringParamArray.add(param);
				}
			}
			if (pureStringParamArray.size() != 0) {
				BsonDocument stringInQueries = new BsonDocument(field, new BsonDocument("$in", pureStringParamArray));
				orQueries.add(stringInQueries);
			}
		}
		if (orQueries.size() != 0) {
			BsonDocument queryObject = new BsonDocument();
			queryObject.put("$or", orQueries);
			return queryObject;
		} else {
			return null;
		}
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

	static BsonDocument getExistsQueryObject(String field, String str, BsonBoolean isExist) {
		BsonDocument query = new BsonDocument();
		if (str != null) {
			str = encodeMongoObjectKey(str);
			query.put(field + "." + str, new BsonDocument("$exists", isExist));
		} else {
			query.put(field, new BsonDocument("$exists", isExist));
		}
		return query;
	}

	static BsonDocument getExistsQueryObject(String[] fieldArr, String str, BsonBoolean isExist) {
		BsonArray conjQueries = new BsonArray();
		for (String field : fieldArr) {
			BsonDocument query = new BsonDocument();
			if (str != null) {
				str = encodeMongoObjectKey(str);
				query.put(field + "." + str, new BsonDocument("$exists", isExist));
			} else {
				query.put(field, new BsonDocument("$exists", isExist));
			}
			conjQueries.add(query);
		}
		if (conjQueries.size() != 0) {
			BsonDocument queryObject = new BsonDocument();
			if (isExist.equals(BsonBoolean.TRUE))
				queryObject.put("$or", conjQueries);
			else{
				queryObject.put("$and", conjQueries);
			}
			return queryObject;
		} else {
			return null;
		}
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
