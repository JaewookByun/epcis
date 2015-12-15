package org.oliot.epcis.service.capture;

import javax.servlet.ServletContext;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.serde.mongodb.MasterDataWriteConverter;
import org.oliot.model.jsonschema.JsonSchemaLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

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

@Controller
@RequestMapping("/JsonVocabularyCapture")
public class JsonVocabularyCapture implements ServletContextAware {
	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ResponseEntity<?> asyncPost(String inputString) {
		ResponseEntity<?> result = post(inputString);
		return result;
	}

	@SuppressWarnings({ "unused", "resource" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@RequestBody String inputString) {
		Configuration.logger.info(" EPCIS Masterdata Document Capture Started.... ");

		if (Configuration.isCaptureVerfificationOn == true) {

			// JSONParser parser = new JSONParser();
			JsonSchemaLoader schemaloader = new JsonSchemaLoader();

			ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");

			try {

				JSONObject json = new JSONObject(inputString);
				JSONObject schema_json = schemaloader.getGeneralschema_md();

				if (!CaptureUtil.validate(json, schema_json)) {
					Configuration.logger.info("Json Document is invalid" + " about master_data_validcheck");
					((AbstractApplicationContext) ctx).close();

					return new ResponseEntity<>(new String("Error: EPCIS Masterdata Document is not validated"),
							HttpStatus.BAD_REQUEST);
				}
				JSONObject json2 = json.getJSONObject("epcismd");
				JSONObject json3 = json2.getJSONObject("EPCISBody");
				JSONArray json4 = json3.getJSONArray("VocabularyList");

				for (int i = 0; i < json4.length(); i++) {

					if (json4.getJSONObject(i).has("Vocabulary") == true) {

						JSONArray json5 = json4.getJSONObject(i).getJSONArray("Vocabulary");
						
						for(int j = 0; j < json5.length(); j++){
							
							if (Configuration.backend.equals("MongoDB")) {
								MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
								mdConverter.json_capture(json5.getJSONObject(j));
								Configuration.logger.info(" EPCIS Masterdata Document : Captured ");
							}
							
						}
						/* startpoint of validation logic for Vocabulary */
					}
				}
			} catch (JSONException e) {
				Configuration.logger.info(" Json Document is not valid ");
			} catch (Exception e) {
				Configuration.logger.log(Level.ERROR, e.toString());
			}


		} else {
			JSONObject json = new JSONObject(inputString);
			JSONObject json2 = json.getJSONObject("epcismd");
			JSONObject json3 = json2.getJSONObject("EPCISBody");
			JSONArray json4 = json3.getJSONArray("VocabularyList");
			
			ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
			
			for (int i = 0; i < json4.length(); i++) {

				if (json4.getJSONObject(i).has("Vocabulary") == true) {

					JSONArray json5 = json4.getJSONObject(i).getJSONArray("Vocabulary");
					
					for(int j = 0; j < json5.length(); j++){
						
						if (Configuration.backend.equals("MongoDB")) {
							MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
							mdConverter.json_capture(json5.getJSONObject(i));
							Configuration.logger.info(" EPCIS Masterdata Document : Captured ");
						}
						
					}
					/* startpoint of validation logic for Vocabulary */
				}
			}
			
		}
		return new ResponseEntity<>(new String("EPCIS Masterdata Document : Captured"), HttpStatus.OK);
	}
}

