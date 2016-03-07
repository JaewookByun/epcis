package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonValue;

import org.oliot.epcis.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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

@Controller
@RequestMapping("/BsonDocumentCapture")
public class BsonDocumentCapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String post(@RequestBody byte[] inputByteArray) {
		Configuration.logger.info(" EPCIS Bson Document Capture Started.... ");

		try {
			ByteArrayInputStream is = new ByteArrayInputStream(inputByteArray);
			ObjectInput oi = new ObjectInputStream(is);
			BsonDocument inputDocument = (BsonDocument) oi.readObject();
			
			MongoClient dbClient = new MongoClient();
			MongoDatabase db = dbClient.getDatabase("epcis");
			
			for (String collectionKey : inputDocument.keySet()) {
				MongoCollection<BsonDocument> collection = db.getCollection(collectionKey, BsonDocument.class);
				BsonArray bsonCollection = inputDocument.getArray(collectionKey);
				Iterator<BsonValue> docIterator = bsonCollection.iterator();
				while(docIterator.hasNext()){
					BsonDocument docElement = docIterator.next().asDocument();
					docElement.put("recordTime", new BsonInt64(System.currentTimeMillis()));
					collection.insertOne(docElement);
				}
			}
			dbClient.close();
		} catch (IOException e) {
			Configuration.logger.error(e);
		} catch (ClassNotFoundException e) {
			Configuration.logger.error(e);
		}

		return "EPCIS Document : Captured ";

	}
}
