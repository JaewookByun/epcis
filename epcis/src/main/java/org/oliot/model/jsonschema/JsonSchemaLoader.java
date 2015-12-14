package org.oliot.model.jsonschema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Copyright (C) 2015 Jaewook Jack Byun
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
 *         
 * @author Sungpil Woo, Master student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         woosungpil@kaist.ac.kr, woosungpil7@gmail.com
 */

public class JsonSchemaLoader {
	
	JSONObject schema_json;
	JSONObject schema_json_md;
	JSONObject objecteventschema_json;
	JSONObject aggregationeventschema_json;
	JSONObject tranformationeventschema_json;
	JSONObject transactioneventschema_json;
	
	public JsonSchemaLoader(){
		JSONParser parser = new JSONParser();
		Object g_schemaobj,g_schemaobj_md,objecteventobj,aggregationeventobj,tranformationeventobj,transactioneventobj;
		
		URL g_url = getClass().getResource("general_schema.json");
		URL g_url_md = getClass().getResource("general_schema_md.json");
		URL objecteventurl = getClass().getResource("obejctevent_schema.json");
		URL aggregationeventurl = getClass().getResource("aggregationevent_schema.json");
		URL tranformationeventurl = getClass().getResource("transformationevent_schema.json");
		URL transactioneventurl = getClass().getResource("transactionevent_schema.json");
		
		File g_schema_file = new File(g_url.getPath());
		File g_schema_md_file = new File(g_url_md.getPath());
		File objecteventfile = new File(objecteventurl.getPath());
		File aggregationeventfile = new File(aggregationeventurl.getPath());
		File tranformationeventfile = new File(tranformationeventurl.getPath());
		File transactioneventfile = new File(transactioneventurl.getPath());
		
		
		try {
			g_schemaobj = parser.parse(new FileReader(g_schema_file));
			g_schemaobj_md = parser.parse(new FileReader(g_schema_md_file));
			objecteventobj = parser.parse(new FileReader(objecteventfile));
			aggregationeventobj = parser.parse(new FileReader(aggregationeventfile));
			tranformationeventobj = parser.parse(new FileReader(tranformationeventfile));
			transactioneventobj = parser.parse(new FileReader(transactioneventfile));
			
			schema_json = new JSONObject(g_schemaobj.toString());
			schema_json_md = new JSONObject(g_schemaobj_md.toString());
			objecteventschema_json = new JSONObject(objecteventobj.toString());
			aggregationeventschema_json = new JSONObject(aggregationeventobj.toString());
			tranformationeventschema_json = new JSONObject(tranformationeventobj.toString());
			transactioneventschema_json = new JSONObject(transactioneventobj.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	public JSONObject getGeneralschema(){
		
		return this.schema_json;
	}
	
	public JSONObject getGeneralschema_md(){
		
		return this.schema_json_md;
	}
	
	
	public JSONObject getObjectEventschema(){
		
		return this.objecteventschema_json;
	}
	
	public JSONObject getAggregationEventschema(){
		
		return this.aggregationeventschema_json;
	}
	
	public JSONObject getTransformationEventschema(){
		
		return this.transactioneventschema_json;
	}
	
	public JSONObject getTransactionEventschema(){
		
		return this.transactioneventschema_json;
	}
	
	

}
