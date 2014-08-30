/**
 * Copyright (C) 2014 KAIST RESL 
 *
 * This file is part of Oliot (oliot.org).

 * @author Jack Jaewook Byun, Ph.D student
 * Korea Advanced Institute of Science and Technology
 * Real-time Embedded System Laboratory(RESL)
 * bjw0829@kaist.ac.kr
 */

package org.oliot.epcis.service.capture;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

public class CaptureService implements CoreCaptureService {

	@SuppressWarnings({ "resource" })
	@Override
	public void capture(AggregationEventType event) {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void capture(ObjectEventType event) {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}

	@SuppressWarnings({ "resource" })
	@Override
	public void capture(QuantityEventType event) {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}

	@SuppressWarnings({ "resource" })
	@Override
	public void capture(TransactionEventType event) {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}

	@SuppressWarnings({ "resource" })
	@Override
	public void capture(TransformationEventType event) {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			ApplicationContext ctx = new GenericXmlApplicationContext(
					"classpath:MongoConfig.xml");
			MongoOperations mongoOperation = (MongoOperations) ctx
					.getBean("mongoTemplate");
			mongoOperation.save(event);
			ConfigurationServlet.logger.info(" Event Saved ");
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void capture(EPCISDocumentType epcisDocument) {
		if (epcisDocument.getEPCISBody() == null) {
			ConfigurationServlet.logger.info(" There is no DocumentBody ");
			return;
		}
		if (epcisDocument.getEPCISBody().getEventList() == null) {
			ConfigurationServlet.logger.info(" There is no EventList ");
			return;
		}
		EventListType eventListType = epcisDocument.getEPCISBody()
				.getEventList();
		List<Object> eventList = eventListType
				.getObjectEventOrAggregationEventOrQuantityEvent();
		/*
		 * JAXBElement<EPCISEventListExtensionType>
		 * JAXBElement<TransactionEventType> Object
		 * JAXBElement<QuantityEventType> JAXBElement<ObjectEventType>
		 * JAXBElement<AggregationEventType> Element
		 */

		for (int i = 0; i < eventList.size(); i++) {
			JAXBElement eventElement = (JAXBElement) eventList.get(i);
			Object event = eventElement.getValue();
			if (event instanceof ObjectEventType) {
				capture((ObjectEventType) event);
			} else if (event instanceof AggregationEventType) {
				capture((AggregationEventType) event);
			} else if (event instanceof TransactionEventType) {
				capture((TransactionEventType) event);
			} else if (event instanceof TransformationEventType) {
				capture((TransformationEventType) event);
			} else if (event instanceof QuantityEventType) {
				capture((QuantityEventType) event);
			}
		}
	}
}
