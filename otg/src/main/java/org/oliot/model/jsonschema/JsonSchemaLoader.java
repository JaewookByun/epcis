package org.oliot.model.jsonschema;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
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

	JSONObject eventSchema;
	JSONObject masterDataSchema;
	JSONObject objectEventSchema;
	JSONObject aggregationEventSchema;
	JSONObject transformationEventSchema;
	JSONObject transactionEventSchema;

	public JsonSchemaLoader() {
		try {
			eventSchema = new JSONObject(new String(
					Files.readAllBytes(Paths.get(getClass().getResource("GeneralEventSchema.json").toURI()))));
			masterDataSchema = new JSONObject(
					new String(Files.readAllBytes(Paths.get(getClass().getResource("GeneralMDSchema.json").toURI()))));
			objectEventSchema = new JSONObject(new String(
					Files.readAllBytes(Paths.get(getClass().getResource("ObjectEventSchema.json").toURI()))));
			aggregationEventSchema = new JSONObject(new String(
					Files.readAllBytes(Paths.get(getClass().getResource("AggregationEventSchema.json").toURI()))));
			transformationEventSchema = new JSONObject(new String(
					Files.readAllBytes(Paths.get(getClass().getResource("TransformationEventSchema.json").toURI()))));
			transactionEventSchema = new JSONObject(new String(
					Files.readAllBytes(Paths.get(getClass().getResource("TransactionEventSchema.json").toURI()))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getEventSchema() {
		return eventSchema;
	}

	public void setEventSchema(JSONObject eventSchema) {
		this.eventSchema = eventSchema;
	}

	public JSONObject getMasterDataSchema() {
		return masterDataSchema;
	}

	public void setMasterDataSchema(JSONObject masterDataSchema) {
		this.masterDataSchema = masterDataSchema;
	}

	public JSONObject getObjectEventSchema() {
		return objectEventSchema;
	}

	public void setObjectEventSchema(JSONObject objectEventSchema) {
		this.objectEventSchema = objectEventSchema;
	}

	public JSONObject getAggregationEventSchema() {
		return aggregationEventSchema;
	}

	public void setAggregationEventSchema(JSONObject aggregationEventSchema) {
		this.aggregationEventSchema = aggregationEventSchema;
	}

	public JSONObject getTransformationEventSchema() {
		return transformationEventSchema;
	}

	public void setTransformationEventSchema(JSONObject transformationEventSchema) {
		this.transformationEventSchema = transformationEventSchema;
	}

	public JSONObject getTransactionEventSchema() {
		return transactionEventSchema;
	}

	public void setTransactionEventSchema(JSONObject transactionEventSchema) {
		this.transactionEventSchema = transactionEventSchema;
	}

}
