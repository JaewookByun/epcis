package org.oliot.epcis.service.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;

import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.converter.mongodb.MongoWriterUtil;
import org.oliot.epcis.security.OAuthUtil;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.model.epcis.PollParameters;
import org.oliot.model.epcis.QueryParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.types.User;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

@Controller
public class NamedQueryRegistration implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@SuppressWarnings("unused")
	@Autowired
	private HttpServletRequest request;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;

	}

	/**
	 * Provide existing Named Event Queries
	 */
	@RequestMapping(value = "/Admin/NamedEventQuery", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getNamedEventQueries() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("NamedEventQuery",
				BsonDocument.class);

		MongoCursor<BsonDocument> cursor = collection.find().iterator();
		JSONArray jarray = new JSONArray();
		while (cursor.hasNext()) {
			BsonDocument doc = cursor.next();
			JSONObject json = new JSONObject(doc.toJson());
			jarray.put(json);
		}

		return new ResponseEntity<>(jarray.toString(1), responseHeaders, HttpStatus.OK);
	}

	/**
	 * Provide existing Named Event Queries
	 */
	@RequestMapping(value = "/Admin/NamedEventQuery/{name}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> putNamedEventQuery(@PathVariable String name, @RequestParam String description,
			@RequestParam(required = false) String eventType, @RequestParam(required = false) String GE_eventTime,
			@RequestParam(required = false) String LT_eventTime, @RequestParam(required = false) String GE_recordTime,
			@RequestParam(required = false) String LT_recordTime, @RequestParam(required = false) String EQ_action,
			@RequestParam(required = false) String EQ_bizStep, @RequestParam(required = false) String EQ_disposition,
			@RequestParam(required = false) String EQ_readPoint, @RequestParam(required = false) String WD_readPoint,
			@RequestParam(required = false) String EQ_bizLocation,
			@RequestParam(required = false) String WD_bizLocation,
			@RequestParam(required = false) String EQ_transformationID,
			@RequestParam(required = false) String MATCH_epc, @RequestParam(required = false) String MATCH_parentID,
			@RequestParam(required = false) String MATCH_inputEPC,
			@RequestParam(required = false) String MATCH_outputEPC, @RequestParam(required = false) String MATCH_anyEPC,
			@RequestParam(required = false) String MATCH_epcClass,
			@RequestParam(required = false) String MATCH_inputEPCClass,
			@RequestParam(required = false) String MATCH_outputEPCClass,
			@RequestParam(required = false) String MATCH_anyEPCClass,
			@RequestParam(required = false) Integer EQ_quantity, @RequestParam(required = false) Integer GT_quantity,
			@RequestParam(required = false) Integer GE_quantity, @RequestParam(required = false) Integer LT_quantity,
			@RequestParam(required = false) Integer LE_quantity,

			@RequestParam(required = false) String EQ_eventID,
			@RequestParam(required = false) Boolean EXISTS_errorDeclaration,
			@RequestParam(required = false) String GE_errorDeclarationTime,
			@RequestParam(required = false) String LT_errorDeclarationTime,
			@RequestParam(required = false) String EQ_errorReason,
			@RequestParam(required = false) String EQ_correctiveEventID,

			@RequestParam(required = false) String orderBy, @RequestParam(required = false) String orderDirection,
			@RequestParam(required = false) Integer eventCountLimit,
			@RequestParam(required = false) Integer maxEventCount,

			@RequestParam(required = false) String vocabularyName,
			@RequestParam(required = false) Boolean includeAttributes,
			@RequestParam(required = false) Boolean includeChildren,
			@RequestParam(required = false) String attributeNames, @RequestParam(required = false) String EQ_name,
			@RequestParam(required = false) String WD_name, @RequestParam(required = false) String HASATTR,
			@RequestParam(required = false) Integer maxElementCount,

			@RequestParam(required = false) String format, @RequestParam String userID,
			@RequestParam String accessToken,

			@RequestParam Map<String, String> params) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		// Access Control is not mandatory
		// However, if fid and accessToken provided, more information provided
		FacebookClient fc = null;
		List<String> friendList = null;
		if (userID != null) {
			// Check accessToken
			fc = OAuthUtil.isValidatedFacebookClient(accessToken, userID);
			if (fc == null) {
				return new ResponseEntity<>(new String("Unauthorized Token"), responseHeaders, HttpStatus.UNAUTHORIZED);
			}
			friendList = new ArrayList<String>();

			Connection<User> friendConnection = fc.fetchConnection("me/friends", User.class);
			for (User friend : friendConnection.getData()) {
				friendList.add(friend.getId());
			}
		}

		// OAuth Fails
		if (!OAuthUtil.isAdministratable(userID, friendList)) {
			Configuration.logger.log(Level.INFO, " No right to administration ");
			return new ResponseEntity<>(new String("No right to administration"), responseHeaders,
					HttpStatus.BAD_REQUEST);

		}

		try {
			PollParameters p = new PollParameters("SimpleEventQuery", eventType, GE_eventTime, LT_eventTime,
					GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint,
					EQ_bizLocation, WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
					MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
					MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, EQ_eventID,
					EXISTS_errorDeclaration, GE_errorDeclarationTime, LT_errorDeclarationTime, EQ_errorReason,
					EQ_correctiveEventID, orderBy, orderDirection, eventCountLimit, maxEventCount, vocabularyName,
					includeAttributes, includeChildren, attributeNames, EQ_name, WD_name, HASATTR, maxElementCount,
					format, params);
			MongoQueryService mqs = new MongoQueryService();
			// null means no error
			String reason = mqs.checkConstraintSimpleEventQuery(p);
			if (reason != null)
				return new ResponseEntity<>(reason, responseHeaders, HttpStatus.BAD_REQUEST);

			boolean isSuccess = addNamedEventQueryToDB(name, description, p);
			if (isSuccess == false) {
				return new ResponseEntity<>(new String("Existing NamedEventQuery, Use another name"), responseHeaders,
						HttpStatus.BAD_REQUEST);
			}

		} catch (QueryParameterException e) {
			return new ResponseEntity<>(e.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
		}

		Configuration.logger.log(Level.INFO, "NamedEventQuery: " + name + " is registered");
		return new ResponseEntity<>(new String("NamedEventQuery " + name + " is registered"), responseHeaders, HttpStatus.OK);
	}

	/**
	 * Provide existing Named Event Queries
	 */
	@RequestMapping(value = "/Admin/NamedEventQuery/{name}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteNamedEventQuery(@PathVariable String name, @RequestParam String userID,
			@RequestParam String accessToken) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		// Access Control is not mandatory
		// However, if fid and accessToken provided, more information provided
		FacebookClient fc = null;
		List<String> friendList = null;
		if (userID != null) {
			// Check accessToken
			fc = OAuthUtil.isValidatedFacebookClient(accessToken, userID);
			if (fc == null) {
				return new ResponseEntity<>(new String("Unauthorized Token"), responseHeaders, HttpStatus.UNAUTHORIZED);
			}
			friendList = new ArrayList<String>();

			Connection<User> friendConnection = fc.fetchConnection("me/friends", User.class);
			for (User friend : friendConnection.getData()) {
				friendList.add(friend.getId());
			}
		}

		// OAuth Fails
		if (!OAuthUtil.isAdministratable(userID, friendList)) {
			Configuration.logger.log(Level.INFO, " No right to administration ");
			return new ResponseEntity<>(new String("No right to administration"), responseHeaders,
					HttpStatus.BAD_REQUEST);

		}

		if (deleteNamedEventQueryFromDB(name)) {
			Configuration.logger.log(Level.INFO, "NamedEventQuery: " + name + " is removed");
			return new ResponseEntity<>(new String("NamedEventQuery: " + name + " is removed"), responseHeaders,
					HttpStatus.OK);
		} else {
			Configuration.logger.log(Level.INFO, "NamedEventQuery: " + name + " does not exist");
			return new ResponseEntity<>(new String("NamedEventQuery: " + name + " does not exist"), responseHeaders,
					HttpStatus.OK);
		}
	}

	private boolean deleteNamedEventQueryFromDB(String name) {
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("NamedEventQuery",
				BsonDocument.class);

		BsonDocument s = collection.findOneAndDelete(new BsonDocument("name", new BsonString(name)));

		if (s == null) {
			return false;
		} else {		
			MongoCollection<BsonDocument> eventDataCollection = Configuration.mongoDatabase.getCollection("EventData",
					BsonDocument.class);
			eventDataCollection.dropIndex(name);
			return true;
		}
	}

	private boolean addNamedEventQueryToDB(String name, String description, PollParameters p) {
		MongoCollection<BsonDocument> namedEventQueryCollection = Configuration.mongoDatabase.getCollection("NamedEventQuery",
				BsonDocument.class);
		MongoCollection<BsonDocument> eventDataCollection = Configuration.mongoDatabase.getCollection("EventData",
				BsonDocument.class);
		
		BsonDocument existingDoc = namedEventQueryCollection.find(new BsonDocument("name", new BsonString(name))).first();

		if (existingDoc == null) {
			BsonDocument bson = PollParameters.asBsonDocument(p);
			bson.put("name", new BsonString(name));
			bson.put("description", new BsonString(description));
			namedEventQueryCollection.insertOne(bson);
		} else {
			return false;
		}

		// Create Index with the given NamedEventQuery name and background option
		IndexOptions indexOptions = new IndexOptions().name(name).background(true);
		BsonDocument indexDocument = makeIndexObject(p);
		eventDataCollection.createIndex(indexDocument, indexOptions);
		
		Configuration.logger.log(Level.INFO, "NamedEventQuery: " + name + " is added to DB. ");
		return true;
	}
	
	private BsonDocument makeIndexObject(PollParameters p) {

		BsonDocument indexDocument = new BsonDocument();

		if (p.getEventType() != null) {
			indexDocument.put("eventType", new BsonInt32(1));
		}

		if (p.getGE_eventTime() != null) {
			indexDocument.put("eventTime", new BsonInt32(1));
		}
		
		if (p.getLT_eventTime() != null) {
			indexDocument.put("eventTime", new BsonInt32(1));
		}
		
		if (p.getGE_recordTime() != null) {
			indexDocument.put("recordTime", new BsonInt32(1));
		}
		
		if (p.getLT_recordTime() != null) {
			indexDocument.put("recordTime", new BsonInt32(1));
		}

		if (p.getGE_errorDeclarationTime() != null) {
			indexDocument.put("errorDeclaration.declarationTime", new BsonInt32(1));
		}

		if (p.getLT_errorDeclarationTime() != null) {
			indexDocument.put("errorDeclaration.declarationTime", new BsonInt32(1));
		}

		if (p.getEQ_action() != null) {
			indexDocument.put("action", new BsonInt32(1));
		}
		
		if (p.getEQ_bizStep() != null) {
			indexDocument.put("bizStep", new BsonInt32(1));
		}
		
		if (p.getEQ_disposition() != null) {
			indexDocument.put("disposition", new BsonInt32(1));
		}
		
		if (p.getEQ_readPoint() != null) {
			indexDocument.put("readPoint.id", new BsonInt32(1));
		}

		if (p.getWD_readPoint() != null) {
			indexDocument.put("readPoint.id", new BsonInt32(1));
		}

		if (p.getEQ_bizLocation() != null) {
			indexDocument.put("bizLocation.id", new BsonInt32(1));
		}

		if (p.getWD_bizLocation() != null) {
			indexDocument.put("bizLocation.id", new BsonInt32(1));
		}

		if (p.getEQ_transformationID() != null) {
			indexDocument.put("transformationID", new BsonInt32(1));
		}

		if (p.getMATCH_epc() != null) {
			indexDocument.put("epcList.epc", new BsonInt32(1));
			indexDocument.put("childEPCs.epc", new BsonInt32(1));
		}

		if (p.getMATCH_parentID() != null) {
			indexDocument.put("parentID", new BsonInt32(1));
		}

		if (p.getMATCH_inputEPC() != null) {
			indexDocument.put("inputEPCList.epc", new BsonInt32(1));
		}

		if (p.getMATCH_outputEPC() != null) {
			indexDocument.put("outputEPCList.epc", new BsonInt32(1));
		}

		if (p.getMATCH_anyEPC() != null) {
			indexDocument.put("epcList.epc", new BsonInt32(1));
			indexDocument.put("childEPCs.epc", new BsonInt32(1));
			indexDocument.put("inputEPCList.epc", new BsonInt32(1));
			indexDocument.put("outputEPCList.epc", new BsonInt32(1));
			indexDocument.put("parentID", new BsonInt32(1));
		}

		if (p.getMATCH_epcClass() != null) {
			indexDocument.put("extension.quantityList.epcClass", new BsonInt32(1));
			indexDocument.put("extension.childQuantityList.epcClass", new BsonInt32(1));
		}

		if (p.getMATCH_inputEPCClass() != null) {
			indexDocument.put("inputQuantityList.epcClass", new BsonInt32(1));
		}

		if (p.getMATCH_outputEPCClass() != null) {
			indexDocument.put("outputQuantityList.epcClass", new BsonInt32(1));
		}

		if (p.getMATCH_anyEPCClass() != null) {
			indexDocument.put("extension.quantityList.epcClass", new BsonInt32(1));
			indexDocument.put("extension.childQuantityList.epcClass", new BsonInt32(1));
			indexDocument.put("inputQuantityList.epcClass", new BsonInt32(1));
			indexDocument.put("outputQuantityList.epcClass", new BsonInt32(1));
		}

		if (p.getEQ_eventID() != null) {
			indexDocument.put("eventID", new BsonInt32(1));
		}

		if (p.getEQ_errorReason() != null) {
			indexDocument.put("errorDeclaration.reason", new BsonInt32(1));
		}

		if (p.getEQ_correctiveEventID() != null) {
			indexDocument.put("errorDeclaration.correctiveEventIDs", new BsonInt32(1));
		}

		if (p.getEXISTS_errorDeclaration() != null) {
			indexDocument.put("errorDeclaration", new BsonInt32(1));
		}

		if (p.getParams() != null) {
			Iterator<String> paramIter = p.getParams().keySet().iterator();
			while (paramIter.hasNext()) {
				String paramName = paramIter.next();

				if (paramName.contains("EQ_bizTransaction_")) {
					indexDocument.put("bizTransactionList", new BsonInt32(1));
				}

				if (paramName.contains("EQ_source_")) {
					indexDocument.put("extension.sourceList", new BsonInt32(1));
					indexDocument.put("sourceList", new BsonInt32(1));
				}

				
				if (paramName.contains("EQ_destination_")) {
					indexDocument.put("extension.destinationList", new BsonInt32(1));
					indexDocument.put("destinationList", new BsonInt32(1));
				}

				if (paramName.startsWith("EQ_ILMD_")) {
					String type = paramName.substring(8, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					indexDocument.put("extension.ilmd.any." + type, new BsonInt32(1));
					indexDocument.put("ilmd.any." + type, new BsonInt32(1));
				}

				if (paramName.startsWith("GT_ILMD_") || paramName.startsWith("GE_ILMD_")
						|| paramName.startsWith("LT_ILMD_") || paramName.startsWith("LE_ILMD_")) {
					String type = paramName.substring(8, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					indexDocument.put("extension.ilmd.any." + type, new BsonInt32(1));
					indexDocument.put("ilmd.any." + type, new BsonInt32(1));
				}

				if (paramName.startsWith("EXISTS_ILMD_")) {
					String field = paramName.substring(12, paramName.length());
					field = MongoWriterUtil.encodeMongoObjectKey(field);
					indexDocument.put("extension.ilmd.any." + field, new BsonInt32(1));
					indexDocument.put("ilmd.any." + field, new BsonInt32(1));
				}

				if (paramName.startsWith("EQ_ERROR_DECLARATION_")) {
					String type = paramName.substring(21, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					indexDocument.put("errorDeclaration.any." + type , new BsonInt32(1));
				}

				if (paramName.startsWith("GT_ERROR_DECLARATION_") || paramName.startsWith("GE_ERROR_DECLARATION_")
						|| paramName.startsWith("LT_ERROR_DECLARATION_")
						|| paramName.startsWith("LE_ERROR_DECLARATION_")) {
					String type = paramName.substring(21, paramName.length());
					type = MongoWriterUtil.encodeMongoObjectKey(type);
					indexDocument.put("errorDeclaration.any." + type , new BsonInt32(1));
				}

				boolean isExtraParam = isExtraParameter(paramName);

				if (isExtraParam == true) {

					if (paramName.startsWith("EQ_")) {
						String type = paramName.substring(3, paramName.length());
						type = MongoWriterUtil.encodeMongoObjectKey(type);
						indexDocument.put("any." + type , new BsonInt32(1));
					}

					if (paramName.startsWith("GT_") || paramName.startsWith("GE_") || paramName.startsWith("LT_")
							|| paramName.startsWith("LE_")) {
						String type = paramName.substring(3, paramName.length());
						type = MongoWriterUtil.encodeMongoObjectKey(type);
						indexDocument.put("any." + type , new BsonInt32(1));
					}

					if (paramName.startsWith("EXISTS_")) {
						String type = paramName.substring(3, paramName.length());
						type = MongoWriterUtil.encodeMongoObjectKey(type);
						indexDocument.put("any." + type , new BsonInt32(1));
					}
				}
			}
		}

		// Update Query with ORDER and LIMIT
		if (p.getOrderBy() != null) {
			String orderBy = MongoWriterUtil.encodeMongoObjectKey(p.getOrderBy());
			// Currently only eventTime, recordTime can be used
			if (orderBy.trim().equals("eventTime")) {
				indexDocument.put("eventTime" , new BsonInt32(1));
			} else if (orderBy.trim().equals("recordTime")) {
				indexDocument.put("recordTime" , new BsonInt32(1));
			} else {
				indexDocument.put("any." + orderBy , new BsonInt32(1));
			}
		}
		
		return indexDocument;
	}
	
	boolean isExtraParameter(String paramName) {

		if (paramName.contains("eventTime"))
			return false;
		if (paramName.contains("recordTime"))
			return false;
		if (paramName.contains("errorDeclarationTime"))
			return false;
		if (paramName.contains("action"))
			return false;
		if (paramName.contains("bizStep"))
			return false;
		if (paramName.contains("disposition"))
			return false;
		if (paramName.contains("readPoint"))
			return false;
		if (paramName.contains("bizLocation"))
			return false;
		if (paramName.contains("bizTransaction"))
			return false;
		if (paramName.contains("source"))
			return false;
		if (paramName.contains("destination"))
			return false;
		if (paramName.contains("transformationID"))
			return false;
		if (paramName.contains("ILMD"))
			return false;
		if (paramName.contains("eventID"))
			return false;
		if (paramName.contains("errorReason"))
			return false;
		if (paramName.contains("correctiveEventID"))
			return false;
		if (paramName.contains("errorDeclaration"))
			return false;
		if (paramName.contains("ERROR_DECLARATION"))
			return false;
		if (paramName.contains("INNER"))
			return false;

		return true;
	}
}
