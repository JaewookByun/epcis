package org.oliot.epcis.resource;

import java.util.HashMap;
import java.util.HashSet;

import org.oliot.epcis.converter.unit.UnitConverter;
import org.oliot.epcis.query.converter.SimpleEventQueryFactory;
import org.oliot.epcis.query.converter.SimpleMasterDataQueryFactory;
import org.oliot.epcis.util.TimeUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class StaticResource {
	public static HashSet<String> unitOfMeasure;
	public static HashSet<String> eventTypes;
	public static HashSet<String> actions;
	public static HashSet<String> bizSteps;
	public static HashSet<String> dispositions;
	public static HashSet<String> bizTransactionTypes;
	public static HashSet<String> sourceDestinationTypes;
	public static HashSet<String> errorReasons;
	public static HashSet<String> fao3;
	public static HashSet<String> vocabularyTypes;
	public static HashMap<String, Integer> gcpLength = new HashMap<String, Integer>();
	public static HashSet<String> measurements;

	public static UnitConverter unitConverter = new UnitConverter();

	public static SimpleEventQueryFactory simpleEventQueryFactory = new SimpleEventQueryFactory();
	public static SimpleMasterDataQueryFactory simpleMasterDataQueryFactory = new SimpleMasterDataQueryFactory();

	public static JsonObject simpleEventQueryResults = new JsonObject().put("type", "EPCISQueryDocument")
			.put("schemaVersion", "2.0").put("creationDate", TimeUtil.getDateTimeStamp(System.currentTimeMillis()))
			.put("epcisBody", new JsonObject().put("queryResults", new JsonObject().put("queryName", "SimpleEventQuery")
					.put("resultsBody", new JsonObject().put("eventList", new JsonArray()))));

	public static JsonObject simpleMasterDataQueryResults = new JsonObject().put("type", "EPCISQueryDocument")
			.put("schemaVersion", "2.0").put("creationDate", TimeUtil.getDateTimeStamp(System.currentTimeMillis()))
			.put("epcisBody",
					new JsonObject().put("queryResults", new JsonObject().put("queryName", "SimpleMasterDataQuery")
							.put("resultsBody", new JsonObject().put("vocabularyList", new JsonArray()))));

}
