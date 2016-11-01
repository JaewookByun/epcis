package org.oliot.epcis.service.admin;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
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
	public ResponseEntity<?> getNamedEventQuery() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("NamedEventQuery",
				BsonDocument.class);

		MongoCursor<BsonDocument> cursor = collection.find().iterator();
		JSONArray jarray = new JSONArray();
		while(cursor.hasNext()){
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
	public ResponseEntity<?> putNamedEventQuery(@PathVariable String name,
			@RequestParam String description, @RequestParam(required = false) String eventType,
			@RequestParam(required = false) String GE_eventTime, @RequestParam(required = false) String LT_eventTime,
			@RequestParam(required = false) String GE_recordTime, @RequestParam(required = false) String LT_recordTime,
			@RequestParam(required = false) String EQ_action, @RequestParam(required = false) String EQ_bizStep,
			@RequestParam(required = false) String EQ_disposition, @RequestParam(required = false) String EQ_readPoint,
			@RequestParam(required = false) String WD_readPoint, @RequestParam(required = false) String EQ_bizLocation,
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

			@RequestParam(required = false) String format, @RequestParam(required = false) String userID,
			@RequestParam(required = false) String accessToken,

			@RequestParam Map<String, String> params) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

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
			if( isSuccess == false ){
				return new ResponseEntity<>(new String("Existing NamedEventQuery, Use another name"), responseHeaders, HttpStatus.BAD_REQUEST);
			}

		} catch (QueryParameterException e) {
			return new ResponseEntity<>(e.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
		}

		Configuration.logger.log(Level.INFO, "NamedEventQuery: " + name + " is registered");
		return new ResponseEntity<>(new String("PUT NamedEventQuery"), responseHeaders, HttpStatus.OK);
	}

	/**
	 * Provide existing Named Event Queries
	 */
	@RequestMapping(value = "/Admin/NamedEventQuery/{name}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteNamedEventQuery(@PathVariable String name) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		if( deleteNamedEventQueryFromDB(name)) {
			Configuration.logger.log(Level.INFO, "NamedEventQuery: " + name + " is removed");
			return new ResponseEntity<>(new String("NamedEventQuery: " + name + " is removed"), responseHeaders, HttpStatus.OK);
		}else{
			Configuration.logger.log(Level.INFO, "NamedEventQuery: " + name + " does not exist");
			return new ResponseEntity<>(new String("NamedEventQuery: " + name + " does not exist"), responseHeaders, HttpStatus.OK);
		}
	}

	private boolean deleteNamedEventQueryFromDB(String name){
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("NamedEventQuery",
				BsonDocument.class);

		BsonDocument s = collection
				.findOneAndDelete(new BsonDocument("name", new BsonString(name)));
		
		if( s == null ){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean addNamedEventQueryToDB(String name, String description, PollParameters p) {
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("NamedEventQuery",
				BsonDocument.class);

		BsonDocument existingDoc = collection.find(new BsonDocument("name", new BsonString(name))).first();

		if (existingDoc == null) {
			BsonDocument bson = PollParameters.asBsonDocument(p);
			bson.put("name", new BsonString(name));
			bson.put("description", new BsonString(description));
			collection.insertOne(bson);
		} else {
			return false;
		}

		Configuration.logger.log(Level.INFO, "NamedEventQuery: " + name + " is added to DB. ");
		return true;
	}
}
