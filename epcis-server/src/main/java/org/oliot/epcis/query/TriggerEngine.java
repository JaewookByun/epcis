package org.oliot.epcis.query;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.eventbus.EventBus;

import org.bson.Document;
import org.oliot.epcis.converter.data.bson_to_pojo.AggregationEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.AssociationEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.ObjectEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.TransactionEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.TransformationEventConverter;
import org.oliot.epcis.model.EventListType;
import org.oliot.epcis.model.QueryResults;
import org.oliot.epcis.model.QueryResultsBody;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

public class TriggerEngine {

	public static ConcurrentHashMap<TriggerDescription, HashSet<URI>> triggerSubscription;

	public TriggerEngine() {
		triggerSubscription = new ConcurrentHashMap<TriggerDescription, HashSet<URI>>();
	}

	public void addSubscription(TriggerDescription description, URI uri) {
		if (triggerSubscription.containsKey(description)) {
			triggerSubscription.get(description).add(uri);
		} else {
			HashSet<URI> set = new HashSet<URI>();
			set.add(uri);
			triggerSubscription.put(description, set);
		}
	}

	public static void registerTransactionStartHandler(EventBus eventBus) {
		eventBus.consumer("trigger", msg -> {
			for (java.util.Map.Entry<TriggerDescription, HashSet<URI>> entry : TriggerEngine.triggerSubscription
					.entrySet()) {
				TriggerDescription desc = entry.getKey();
				HashSet<URI> uris = entry.getValue();
				if (desc.isPass((Document) msg.body())) {
					for (URI uri : uris) {
						sendQueryResult(desc, (Document) msg.body(), uri);
					}
				}
			}
		});
	}

	private static Object getConvertedEvent(SOAPMessage message, Document doc, ArrayList<String> nsList) {
		String type = doc.getString("type");
		try {
			if (type.equals("AggregationEvent")) {
				return new AggregationEventConverter().convert(doc, message, nsList);
			} else if (type.equals("ObjectEvent")) {
				return new ObjectEventConverter().convert(doc, message, nsList);
			} else if (type.equals("TransactionEvent")) {
				return new TransactionEventConverter().convert(doc, message, nsList);
			} else if (type.equals("TransformationEvent")) {
				return new TransformationEventConverter().convert(doc, message, nsList);
			} else if (type.equals("AssociationEvent")) {
				return new AssociationEventConverter().convert(doc, message, nsList);
			}
			return null;
		} catch (Exception e) {
			// not happen
			e.printStackTrace();
			return null;
		}
	}

	private static void sendQueryResult(TriggerDescription desc, Document doc, URI uri) {
		SOAPMessage message = new SOAPMessage();
		ArrayList<String> nsList = new ArrayList<String>();
		QueryResults queryResults = new QueryResults();
		queryResults.setQueryName("SimpleEventQuery");
		queryResults.setSubscriptionID(desc.getSubscriptionID());

		QueryResultsBody resultsBody = new QueryResultsBody();

		EventListType elt = new EventListType();
		elt.setObjectEventOrAggregationEventOrTransformationEvent(List.of(getConvertedEvent(message, doc, nsList)));
		resultsBody.setEventList(elt);
		queryResults.setResultsBody(resultsBody);

		HTTPUtil.sendQueryResults(EPCISServer.clientForSubscriptionCallback, uri, EPCISServer.logger, message,
				queryResults, QueryResults.class);
	}

}
