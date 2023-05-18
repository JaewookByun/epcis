package org.oliot.epcis.resource;

import java.util.HashMap;
import java.util.HashSet;

import org.oliot.epcis.converter.unit.UnitConverter;
import org.oliot.epcis.query.converter.SimpleEventQueryFactory;
import org.oliot.epcis.query.converter.SimpleMasterDataQueryFactory;

public class Resource {
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

}
