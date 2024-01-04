package org.oliot.epcis.query;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;

import org.bson.Document;
import org.oliot.epcis.converter.data.bson_to_pojo.AggregationEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.AssociationEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.ObjectEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.TransactionEventConverter;
import org.oliot.epcis.converter.data.bson_to_pojo.TransformationEventConverter;
import org.oliot.epcis.model.EventListType;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.QueryResults;
import org.oliot.epcis.model.QueryResultsBody;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

public class TriggerEngine {

	public static ConcurrentHashMap<TriggerDescription, HashSet<Object>> triggerSubscription;

	public TriggerEngine() {
		triggerSubscription = new ConcurrentHashMap<TriggerDescription, HashSet<Object>>();
	}

	private static void updateLastNotifiedAt(String subscriptionID, long lastNotifiedAt)
			throws ImplementationException {

		try {
			EPCISServer.mSubscriptionCollection.findOneAndUpdate(new org.bson.Document("_id", subscriptionID),
					new org.bson.Document("$set", new org.bson.Document("lastNotifiedAt", lastNotifiedAt)));
			EPCISServer.logger.debug("lastNotifiedAt updated to " + lastNotifiedAt);
		} catch (Throwable e2) {
			ImplementationException e = new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null,
					e2.getMessage());
			EPCISServer.logger.error(e.getMessage());
			throw e;
		}
	}

	public void addSubscription(TriggerDescription description, URI uri) {
		if (triggerSubscription.containsKey(description)) {
			triggerSubscription.get(description).add(uri);
		} else {
			HashSet<Object> set = new HashSet<Object>();
			set.add(uri);
			triggerSubscription.put(description, set);
		}
	}

	public void addSubscription(TriggerDescription description, ServerWebSocket ws) {
		if (triggerSubscription.containsKey(description)) {
			triggerSubscription.get(description).add(ws);
		} else {
			HashSet<Object> set = new HashSet<Object>();
			set.add(ws);
			triggerSubscription.put(description, set);
		}
	}

	public static void registerTransactionStartHandler(EventBus eventBus) {
		eventBus.consumer("trigger", msg -> {
			for (java.util.Map.Entry<TriggerDescription, HashSet<Object>> entry : TriggerEngine.triggerSubscription
					.entrySet()) {
				TriggerDescription desc = entry.getKey();
				HashSet<Object> dests = entry.getValue();
				if (desc.isPass((Document) msg.body())) {
					long cur = System.currentTimeMillis();
					try {
						updateLastNotifiedAt(desc.getSubscriptionID(), cur);
						for (Object dest : dests) {
							if (dest instanceof URI) {
								sendQueryResult(desc, (Document) msg.body(), (URI) dest);
							} else if (dest instanceof ServerWebSocket) {
								sendQueryResult(desc, (Document) msg.body(), (ServerWebSocket) dest);
							}

						}
					} catch (ImplementationException e) {

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
		;
		HTTPUtil.sendQueryResults(EPCISServer.clientForSubscriptionCallback, uri, EPCISServer.logger, message,
				queryResults, QueryResults.class);
	}

	private static void sendQueryResult(TriggerDescription desc, Document doc, ServerWebSocket ws) {
		ws.writeTextMessage(doc.toJson());
	}
}
