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
import org.bson.BsonString;
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
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

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
		Configuration.logger.info(" EPCIS Bson Document Capture Not yet Implemented.... ");

/*		try {
			ByteArrayInputStream is = new ByteArrayInputStream(inputByteArray);
			ObjectInput oi = new ObjectInputStream(is);
			BsonDocument inputDocument = (BsonDocument) oi.readObject();

			MongoClient dbClient = new MongoClient();
			MongoDatabase db = dbClient.getDatabase("epcis");

			for (String collectionKey : inputDocument.keySet()) {
				MongoCollection<BsonDocument> collection = db.getCollection(collectionKey, BsonDocument.class);
				BsonArray bsonCollection = inputDocument.getArray(collectionKey);
				Iterator<BsonValue> docIterator = bsonCollection.iterator();
				while (docIterator.hasNext()) {
					if (!collectionKey.equals("MasterData")) {
						BsonDocument docElement = docIterator.next().asDocument();
						docElement.put("recordTime", new BsonInt64(System.currentTimeMillis()));
						collection.insertOne(docElement);
					} else {
						BsonDocument docElement = docIterator.next().asDocument();
						BsonString vocID = docElement.get("id").asString();
						// each id should have one document
						MongoCursor<BsonDocument> bsonDocumentIterator = collection.find(new BsonDocument("id", vocID))
								.iterator();
						if (bsonDocumentIterator.hasNext()) {
							BsonDocument existingDocument = bsonDocumentIterator.next();
							BsonDocument existingAttributes = existingDocument.getDocument("attributes");
							BsonDocument currentAttributes = docElement.getDocument("attributes");
							for (String key : currentAttributes.keySet()) {
								BsonString value = currentAttributes.getString(key);
								existingAttributes.put(key, value);
							}
							existingAttributes.put("lastUpdated", new BsonInt64(System.currentTimeMillis()));
							existingDocument.put("attributes", existingAttributes);
							if (docElement.containsKey("children")
									&& docElement.getArray("children").isEmpty() == false) {
								existingDocument.put("children", docElement.getArray("children"));
							}
							collection.findOneAndReplace(new BsonDocument("id", vocID), existingDocument);
						} else {
							if (!docElement.containsKey("attributes")
									|| docElement.getDocument("attributes").isEmpty()) {
								BsonDocument attributes = new BsonDocument("lastUpdated",
										new BsonInt64(System.currentTimeMillis()));
								docElement.put("attributes", attributes);
							} else {
								docElement.put("attributes", docElement.getDocument("attributes").append("lastUpdated",
										new BsonInt64(System.currentTimeMillis())));
							}
							collection.insertOne(docElement);
						}
					}
				}
			}
			dbClient.close();
		} catch (IOException e) {
			Configuration.logger.error(e);
		} catch (ClassNotFoundException e) {
			Configuration.logger.error(e);
		}
*/
		return "EPCIS Document : Captured ";

	}
}
