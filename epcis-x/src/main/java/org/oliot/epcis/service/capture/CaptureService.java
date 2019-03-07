package org.oliot.epcis.service.capture;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;

import org.oliot.epcis.converter.mongodb.AggregationEventWriteConverter;
import org.oliot.epcis.converter.mongodb.ObjectEventWriteConverter;
import org.oliot.epcis.converter.mongodb.TransactionEventWriteConverter;
import org.oliot.epcis.converter.mongodb.TransformationEventWriteConverter;
import org.oliot.epcis.service.EPCISServer;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISEventListExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.TransactionEventType;

import io.vertx.core.json.JsonObject;

public class CaptureService {

	public void post(String inputString) {
		InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
		EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream, EPCISDocumentType.class);
		captureEvents(epcisDocument);
		return;
	}

	private void captureEvents(EPCISDocumentType epcisDocument) {
		try {
			List<Object> eventList = epcisDocument.getEPCISBody().getEventList()
					.getObjectEventOrAggregationEventOrQuantityEvent();
			eventList.parallelStream().parallel().map(jaxbEvent -> prepareEvent(jaxbEvent)).filter(doc -> doc != null)
					.forEach(doc -> {
						EPCISServer.mClient.insert("EventData", doc, res -> {
							if (res.succeeded()) {
								String id = res.result();
								System.out.println("Inserted event with id " + id);
							} else {
								res.cause().printStackTrace();
							}
						});
					});
		} catch (NullPointerException ex) {
			// No Event
		}
	}

	@SuppressWarnings("rawtypes")
	private JsonObject prepareEvent(Object jaxbEvent) {
		JAXBElement eventElement = (JAXBElement) jaxbEvent;
		Object event = eventElement.getValue();
		JsonObject doc = convert(event);
		return doc;
	}

	public JsonObject convert(Object event) {
		JsonObject object2Save = null;
		if (event instanceof AggregationEventType) {
			AggregationEventWriteConverter wc = new AggregationEventWriteConverter();
			object2Save = wc.convert((AggregationEventType) event);
		} else if (event instanceof ObjectEventType) {
			ObjectEventWriteConverter wc = new ObjectEventWriteConverter();
			object2Save = wc.convert((ObjectEventType) event);
		} else if (event instanceof TransactionEventType) {
			TransactionEventWriteConverter wc = new TransactionEventWriteConverter();
			object2Save = wc.convert((TransactionEventType) event);
		} else if (event instanceof EPCISEventListExtensionType) {
			TransformationEventWriteConverter wc = new TransformationEventWriteConverter();
			object2Save = wc.convert(((EPCISEventListExtensionType) event).getTransformationEvent());
		}

		if (object2Save == null)
			return null;

		return object2Save;
	}
}
