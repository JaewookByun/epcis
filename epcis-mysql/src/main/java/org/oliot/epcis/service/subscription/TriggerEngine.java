package org.oliot.epcis.service.subscription;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.log4j.Level;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.query.mysql.MysqlQueryService;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.PollParameters;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.SubscriptionType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;

public class TriggerEngine {
	private static Map<String, SubscriptionType> triggerSubscriptionMap = new HashMap<String, SubscriptionType>();
	private static final Object syncObject = new Object();

	public static Map<String, SubscriptionType> getTriggerSubscriptionMap() {
		synchronized (syncObject) {
			return triggerSubscriptionMap;
		}
	}

	public static void setTriggerSubscriptionMap(Map<String, SubscriptionType> triggerSubscriptionMap) {
		synchronized (syncObject) {
			TriggerEngine.triggerSubscriptionMap = triggerSubscriptionMap;
		}
	}

	public static void addTriggerSubscription(String subscriptionID, SubscriptionType subscription) {
		synchronized (syncObject) {
			triggerSubscriptionMap.put(subscriptionID, subscription);
		}
	}

	public static void removeTriggerSubscription(String subscriptionID) {
		synchronized (syncObject) {
			triggerSubscriptionMap.remove(subscriptionID);
		}
	}

	/**
	 * 
	 * @param subscription
	 * @return if true, the event is triggered
	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static boolean examineAndFire(String eventType, BsonDocument bsonDocument) {
//		synchronized (syncObject) {
//			for (String subID : triggerSubscriptionMap.keySet()) {
//				SubscriptionType sub = triggerSubscriptionMap.get(subID);
//				try {
//					if (isPassed(eventType, bsonDocument, sub)) {
//						if (sub.getPollParameters().getFormat() == null
//								|| sub.getPollParameters().getFormat().equals("XML")) {
//							EPCISQueryDocumentType epcisQueryDocumentType = null;
//							MysqlQueryService qs=new MysqlQueryService();
//							//MongoQueryService qs = new MongoQueryService();
//							epcisQueryDocumentType = qs.makeBaseResultDocument("SimpleEventQuery", sub.getSubscriptionID());
//							
//							List<Object> eventObjects = epcisQueryDocumentType.getEPCISBody().getQueryResults()
//									.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent();
//							
//							if (eventType.equals("AggregationEvent")) {
//								AggregationEventReadConverter con = new AggregationEventReadConverter();
//								
//								JAXBElement element = new JAXBElement(new QName("AggregationEvent"),
//										AggregationEventType.class, con.convert(bsonDocument));
//								eventObjects.add(element);
//							} else if (eventType.equals("ObjectEvent")) {
//								ObjectEventReadConverter con = new ObjectEventReadConverter();
//								JAXBElement element = new JAXBElement(new QName("ObjectEvent"), ObjectEventType.class,
//										con.convert(bsonDocument));
//								eventObjects.add(element);
//							} else if (eventType.equals("QuantityEvent")) {
//								QuantityEventReadConverter con = new QuantityEventReadConverter();
//								JAXBElement element = new JAXBElement(new QName("QuantityEvent"),
//										QuantityEventType.class, con.convert(bsonDocument));
//								eventObjects.add(element);
//							} else if (eventType.equals("TransactionEvent")) {
//								TransactionEventReadConverter con = new TransactionEventReadConverter();
//								JAXBElement element = new JAXBElement(new QName("TransactionEvent"),
//										TransactionEventType.class, con.convert(bsonDocument));
//								eventObjects.add(element);
//							} else if (eventType.equals("TransformationEvent")) {
//								TransformationEventReadConverter con = new TransformationEventReadConverter();
//								JAXBElement element = new JAXBElement(new QName("TransformationEvent"),
//										TransformationEventType.class, con.convert(bsonDocument));
//								eventObjects.add(element);
//							}
//							StringWriter sw = new StringWriter();
//							JAXB.marshal(epcisQueryDocumentType, sw);
//							sendPost(new URL(sub.getDest()), sw.toString().getBytes());
//						} else {
//							sendPost(new URL(sub.getDest()), bsonDocument.toJson().getBytes());
//						}
//					}else{
//						// failed to pass
//						if( sub.getReportIfEmpty() == true ){
//							if (sub.getPollParameters().getFormat() == null
//									|| sub.getPollParameters().getFormat().equals("XML")) {
//								EPCISQueryDocumentType epcisQueryDocumentType = null;
//								MongoQueryService qs = new MongoQueryService();
//								epcisQueryDocumentType = qs.makeBaseResultDocument("SimpleEventQuery",sub.getSubscriptionID());
//								StringWriter sw = new StringWriter();
//								JAXB.marshal(epcisQueryDocumentType, sw);
//								sendPost(new URL(sub.getDest()), sw.toString().getBytes());
//							}else{
//								sendPost(new URL(sub.getDest()), new BsonDocument().toJson().getBytes());
//							}
//						}
//					}
//				} catch (MalformedURLException e) {
//					e.printStackTrace();
//				}
//			}
//			return false;
//		}
//	}

	public static boolean isPassed(String eventType, BsonDocument bsonDocument, SubscriptionType subscription) {
		PollParameters p = subscription.getPollParameters();
		if (p.getEventType() != null) {
			if (!eventType.equals(p.getEventType()))
				return false;
		}
		if (p.getGE_eventTime() != null) {
			if (!bsonDocument.containsKey("eventTime"))
				return false;
			long eventTime = bsonDocument.getInt64("eventTime").getValue();
			long cond = getTimeMillis(p.getGE_eventTime());
			if (eventTime < cond)
				return false;
		}
		if (p.getLT_eventTime() != null) {
			if (!bsonDocument.containsKey("eventTime"))
				return false;
			long eventTime = bsonDocument.getInt64("eventTime").getValue();
			long cond = getTimeMillis(p.getLT_eventTime());
			if (eventTime >= cond)
				return false;
		}

		if (p.getGE_recordTime() != null) {
			if (!bsonDocument.containsKey("recordTime"))
				return false;
			long recordTime = bsonDocument.getInt64("recordTime").getValue();
			long cond = getTimeMillis(p.getGE_recordTime());
			if (recordTime < cond)
				return false;
		}
		if (p.getLT_recordTime() != null) {
			if (!bsonDocument.containsKey("recordTime"))
				return false;
			long recordTime = bsonDocument.getInt64("recordTime").getValue();
			long cond = getTimeMillis(p.getLT_recordTime());
			if (recordTime >= cond)
				return false;
		}
		if (p.getEQ_action() != null) {
			if (!bsonDocument.containsKey("action"))
				return false;
			if (!bsonDocument.getString("action").getValue().equals(p.getEQ_action()))
				return false;
		}
		if (p.getEQ_disposition() != null) {
			if (!bsonDocument.containsKey("disposition"))
				return false;
			if (!bsonDocument.getString("disposition").getValue().equals(p.getEQ_disposition()))
				return false;
		}
		if (p.getEQ_bizStep() != null) {
			if (!bsonDocument.containsKey("bizStep"))
				return false;
			if (!bsonDocument.getString("bizStep").getValue().equals(p.getEQ_bizStep()))
				return false;
		}
		if (p.getEQ_readPoint() != null) {
			if (!bsonDocument.containsKey("readPoint"))
				return false;
			BsonDocument readPoint = bsonDocument.getDocument("readPoint");
			if (!readPoint.containsKey("id"))
				return false;
			if (!readPoint.getString("id").getValue().equals(p.getEQ_readPoint()))
				return false;
		}
		if (p.getEQ_bizLocation() != null) {
			if (!bsonDocument.containsKey("bizLocation"))
				return false;
			BsonDocument bizLocation = bsonDocument.getDocument("bizLocation");
			if (!bizLocation.containsKey("id"))
				return false;
			if (!bizLocation.getString("id").getValue().equals(p.getEQ_bizLocation()))
				return false;
		}
		if (p.getEQ_transformationID() != null) {
			if (!bsonDocument.containsKey("transformationID"))
				return false;
			if (!bsonDocument.getString("transformationID").getValue().equals(p.getEQ_transformationID()))
				return false;
		}

		if (p.getMATCH_epc() != null) {
			if (!bsonDocument.containsKey("epcList") && !bsonDocument.containsKey("childEPCs"))
				return false;

			Set<String> epcSet = new HashSet<String>();
			if (bsonDocument.containsKey("epcList")) {
				epcSet = addEPCtoSet(epcSet, bsonDocument.getArray("epcList"));
			}
			if (bsonDocument.containsKey("childEPCs")) {
				epcSet = addEPCtoSet(epcSet, bsonDocument.getArray("childEPCs"));
			}

			if (!epcSet.contains(p.getMATCH_epc()))
				return false;
		}

		if (p.getMATCH_parentID() != null) {
			if (!bsonDocument.containsKey("parentID"))
				return false;
			if (!bsonDocument.getString("parentID").getValue().equals(p.getMATCH_parentID()))
				return false;
		}

		if (p.getMATCH_inputEPC() != null) {
			if (!bsonDocument.containsKey("inputEPCList"))
				return false;

			Set<String> epcSet = new HashSet<String>();
			if (bsonDocument.containsKey("inputEPCList")) {
				epcSet = addEPCtoSet(epcSet, bsonDocument.getArray("inputEPCList"));
			}

			if (!epcSet.contains(p.getMATCH_inputEPC()))
				return false;
		}

		if (p.getMATCH_outputEPC() != null) {
			if (!bsonDocument.containsKey("outputEPCList"))
				return false;

			Set<String> epcSet = new HashSet<String>();
			if (bsonDocument.containsKey("outputEPCList")) {
				epcSet = addEPCtoSet(epcSet, bsonDocument.getArray("outputEPCList"));
			}

			if (!epcSet.contains(p.getMATCH_outputEPC()))
				return false;
		}

		if (p.getMATCH_anyEPC() != null) {
			if (!bsonDocument.containsKey("epcList") && !bsonDocument.containsKey("childEPCs")
					&& !bsonDocument.containsKey("inputEPCList") && !bsonDocument.containsKey("outputEPCList")
					&& !bsonDocument.containsKey("parentID"))
				return false;

			Set<String> epcSet = new HashSet<String>();
			if (bsonDocument.containsKey("epcList")) {
				epcSet = addEPCtoSet(epcSet, bsonDocument.getArray("epcList"));
			}
			if (bsonDocument.containsKey("childEPCs")) {
				epcSet = addEPCtoSet(epcSet, bsonDocument.getArray("childEPCs"));
			}
			if (bsonDocument.containsKey("inputEPCList")) {
				epcSet = addEPCtoSet(epcSet, bsonDocument.getArray("inputEPCList"));
			}
			if (bsonDocument.containsKey("outputEPCList")) {
				epcSet = addEPCtoSet(epcSet, bsonDocument.getArray("outputEPCList"));
			}
			if (bsonDocument.containsKey("parentID")) {
				epcSet.add(bsonDocument.getString("parentID").getValue());
			}

			if (!epcSet.contains(p.getMATCH_anyEPC()))
				return false;
		}

		if (p.getMATCH_epcClass() != null) {
			if (!bsonDocument.containsKey("extension")) {
				BsonDocument extension = bsonDocument.getDocument("extension");
				if (!extension.containsKey("quantityList") && !extension.containsKey("childQuantityList"))
					return false;
				Set<String> epcSet = new HashSet<String>();
				if (extension.containsKey("quantityList")) {
					epcSet = addEPCClasstoSet(epcSet, extension.getArray("quantityList"));
				}
				if (extension.containsKey("childQuantityList")) {
					epcSet = addEPCClasstoSet(epcSet, extension.getArray("childQuantityList"));
				}
				if (!epcSet.contains(p.getMATCH_epcClass()))
					return false;
			}
		}

		if (p.getMATCH_inputEPCClass() != null) {
			if (!bsonDocument.containsKey("inputQuantityList"))
				return false;

			Set<String> epcSet = new HashSet<String>();
			if (bsonDocument.containsKey("inputQuantityList")) {
				epcSet = addEPCClasstoSet(epcSet, bsonDocument.getArray("inputQuantityList"));
			}

			if (!epcSet.contains(p.getMATCH_inputEPCClass()))
				return false;
		}

		if (p.getMATCH_outputEPCClass() != null) {
			if (!bsonDocument.containsKey("outputQuantityList"))
				return false;

			Set<String> epcSet = new HashSet<String>();
			if (bsonDocument.containsKey("outputQuantityList")) {
				epcSet = addEPCClasstoSet(epcSet, bsonDocument.getArray("outputQuantityList"));
			}

			if (!epcSet.contains(p.getMATCH_outputEPCClass()))
				return false;
		}

		if (p.getMATCH_epcClass() != null) {
			if (!bsonDocument.containsKey("inputQuantityList") && !bsonDocument.containsKey("outputQuantityList")
					&& !bsonDocument.containsKey("extension"))
				return false;

			Set<String> epcSet = new HashSet<String>();
			if (bsonDocument.containsKey("inputQuantityList")) {
				epcSet = addEPCClasstoSet(epcSet, bsonDocument.getArray("inputQuantityList"));
			}
			if (bsonDocument.containsKey("outputQuantityList")) {
				epcSet = addEPCClasstoSet(epcSet, bsonDocument.getArray("outputQuantityList"));
			}
			BsonDocument extension = bsonDocument.getDocument("extension");
			if (!extension.containsKey("quantityList") && !extension.containsKey("childQuantityList")
					&& epcSet.isEmpty() == true)
				return false;

			if (extension.containsKey("quantityList")) {
				epcSet = addEPCClasstoSet(epcSet, extension.getArray("quantityList"));
			}
			if (extension.containsKey("childQuantityList")) {
				epcSet = addEPCClasstoSet(epcSet, extension.getArray("childQuantityList"));
			}
			if (!epcSet.contains(p.getMATCH_anyEPCClass()))
				return false;
		}

		if (p.getParams() != null) {
			Map<String, String> paramMap = p.getParams();
			Iterator<String> paramIter = paramMap.keySet().iterator();
			while (paramIter.hasNext()) {
				String paramName = paramIter.next();
				String paramValues = paramMap.get(paramName);

				if (paramName.contains("EQ_bizTransaction_")) {
					if (!bsonDocument.containsKey("bizTransactionList"))
						return false;
					BsonArray bizTransactionList = bsonDocument.getArray("bizTransactionList");
					String type = paramName.substring(18, paramName.length());
					boolean contains = false;
					Iterator<BsonValue> bizTransactionIterator = bizTransactionList.iterator();
					while (bizTransactionIterator.hasNext()) {
						BsonDocument bizTransaction = bizTransactionIterator.next().asDocument();
						if (bizTransaction.containsKey(type)) {
							if (bizTransaction.getString(type).getValue().equals(paramValues))
								contains = true;
						}
					}
					if (contains == false) {
						return false;
					}
				}

				if (paramName.contains("EQ_source_")) {
					String type = paramName.substring(10, paramName.length());
					if (eventType.equals("AggregationEvent") || eventType.equals("ObjectEvent")
							|| eventType.equals("TransactionEvent")) {
						if (!bsonDocument.containsKey("extension")
								|| !bsonDocument.getDocument("extension").containsKey("sourceList"))
							return false;
						BsonArray sourceList = bsonDocument.getDocument("extension").getArray("sourceList");
						boolean contains = false;
						Iterator<BsonValue> sourceIterator = sourceList.iterator();
						while (sourceIterator.hasNext()) {
							BsonDocument source = sourceIterator.next().asDocument();
							if (source.containsKey(type)) {
								if (source.getString(type).getValue().equals(paramValues))
									contains = true;
							}
						}
						if (contains == false) {
							return false;
						}
					}
					if (eventType.equals("TransformationEvent")) {
						if (!bsonDocument.containsKey("sourceList"))
							return false;

						BsonArray sourceList = bsonDocument.getArray("sourceList");
						boolean contains = false;
						Iterator<BsonValue> sourceIterator = sourceList.iterator();
						while (sourceIterator.hasNext()) {
							BsonDocument source = sourceIterator.next().asDocument();
							if (source.containsKey(type)) {
								if (source.getString(type).getValue().equals(paramValues))
									contains = true;
							}
						}
						if (contains == false) {
							return false;
						}
					}
				}

				if (paramName.contains("EQ_destination_")) {
					String type = paramName.substring(15, paramName.length());

					if (eventType.equals("AggregationEvent") || eventType.equals("ObjectEvent")
							|| eventType.equals("TransactionEvent")) {
						if (!bsonDocument.containsKey("extension")
								|| !bsonDocument.getDocument("extension").containsKey("destinationList"))
							return false;
						BsonArray destinationList = bsonDocument.getDocument("extension").getArray("destinationList");
						boolean contains = false;
						Iterator<BsonValue> destinationIterator = destinationList.iterator();
						while (destinationIterator.hasNext()) {
							BsonDocument destination = destinationIterator.next().asDocument();
							if (destination.containsKey(type)) {
								if (destination.getString(type).getValue().equals(paramValues))
									contains = true;
							}
						}
						if (contains == false) {
							return false;
						}
					}
					if (eventType.equals("TransformationEvent")) {
						if (!bsonDocument.containsKey("sourceList"))
							return false;

						BsonArray destinationList = bsonDocument.getArray("destinationList");
						boolean contains = false;
						Iterator<BsonValue> destinationIterator = destinationList.iterator();
						while (destinationIterator.hasNext()) {
							BsonDocument destination = destinationIterator.next().asDocument();
							if (destination.containsKey(type)) {
								if (destination.getString(type).getValue().equals(paramValues))
									contains = true;
							}
						}
						if (contains == false) {
							return false;
						}
					}
				}

				boolean isExtraParam = isExtraParameter(paramName);

				if (isExtraParam == true) {

					if (paramName.startsWith("EQ_")) {
						String type = paramName.substring(3, paramName.length());

						if (!bsonDocument.containsKey("any"))
							return false;
						BsonDocument extensionField = bsonDocument.getDocument("any");
						boolean contains = false;
						Object conversedParamValue = converseType(paramValues);
						for (String key : extensionField.keySet()) {
							if (type.equals(key)) {
								if (conversedParamValue instanceof String
										&& extensionField.get(key).getBsonType().equals(BsonType.STRING)) {
									String stringValue = (String) conversedParamValue;
									String compValue = extensionField.getString(key).getValue();
									if (stringValue.equals(compValue))
										contains = true;
								} else if (conversedParamValue instanceof Integer
										&& extensionField.get(key).getBsonType().equals(BsonType.INT32)) {
									int intValue = (Integer) conversedParamValue;
									int compValue = extensionField.getInt32(key).getValue();
									if (intValue == compValue)
										contains = true;
								} else if (conversedParamValue instanceof Long
										&& extensionField.get(key).getBsonType().equals(BsonType.INT64)) {
									long longValue = (Long) conversedParamValue;
									long compValue = extensionField.getInt64(key).getValue();
									if (longValue == compValue)
										contains = true;
								} else if (conversedParamValue instanceof Double
										&& extensionField.get(key).getBsonType().equals(BsonType.DOUBLE)) {
									double doubleValue = (Double) conversedParamValue;
									double compValue = extensionField.getDouble(key).getValue();
									if (doubleValue == compValue)
										contains = true;
								} else if (conversedParamValue instanceof Boolean
										&& extensionField.get(key).getBsonType().equals(BsonType.BOOLEAN)) {
									boolean boolValue = (Boolean) conversedParamValue;
									boolean compValue = extensionField.getBoolean(key).getValue();
									if (boolValue == compValue)
										contains = true;
								}
							}
						}
						if (contains == false)
							return false;
					}

					if (paramName.startsWith("GT_") || paramName.startsWith("GE_") || paramName.startsWith("LT_")
							|| paramName.startsWith("LE_")) {
						String type = paramName.substring(3, paramName.length());

						if (!bsonDocument.containsKey("any"))
							return false;
						BsonDocument extensionField = bsonDocument.getDocument("any");
						boolean contains = false;
						Object conversedParamValue = converseType(paramValues);
						for (String key : extensionField.keySet()) {
							if (type.equals(key)) {
								if (conversedParamValue instanceof String
										&& extensionField.get(key).getBsonType().equals(BsonType.STRING)) {
									String stringValue = (String) conversedParamValue;
									String compValue = extensionField.getString(key).getValue();
									if (paramName.startsWith("GT_")) {
										if (compValue.compareTo(stringValue) > 0)
											contains = true;
									} else if (paramName.startsWith("GE_")) {
										if (compValue.compareTo(stringValue) >= 0)
											contains = true;
									} else if (paramName.startsWith("LT_")) {
										if (compValue.compareTo(stringValue) < 0)
											contains = true;
									} else if (paramName.startsWith("LE_")) {
										if (compValue.compareTo(stringValue) <= 0)
											contains = true;
									}
								} else if (conversedParamValue instanceof Integer
										&& extensionField.get(key).getBsonType().equals(BsonType.INT32)) {
									int intValue = (Integer) conversedParamValue;
									int compValue = extensionField.getInt32(key).getValue();
									if (paramName.startsWith("GT_")) {
										if (compValue > intValue)
											contains = true;
									} else if (paramName.startsWith("GE_")) {
										if (compValue >= intValue)
											contains = true;
									} else if (paramName.startsWith("LT_")) {
										if (compValue < intValue)
											contains = true;
									} else if (paramName.startsWith("LE_")) {
										if (compValue <= intValue)
											contains = true;
									}
								} else if (conversedParamValue instanceof Long
										&& extensionField.get(key).getBsonType().equals(BsonType.INT64)) {
									long longValue = (Long) conversedParamValue;
									long compValue = extensionField.getInt64(key).getValue();
									if (paramName.startsWith("GT_")) {
										if (compValue > longValue)
											contains = true;
									} else if (paramName.startsWith("GE_")) {
										if (compValue >= longValue)
											contains = true;
									} else if (paramName.startsWith("LT_")) {
										if (compValue < longValue)
											contains = true;
									} else if (paramName.startsWith("LE_")) {
										if (compValue <= longValue)
											contains = true;
									}
								} else if (conversedParamValue instanceof Double
										&& extensionField.get(key).getBsonType().equals(BsonType.DOUBLE)) {
									double doubleValue = (Double) conversedParamValue;
									double compValue = extensionField.getDouble(key).getValue();
									if (paramName.startsWith("GT_")) {
										if (compValue > doubleValue)
											contains = true;
									} else if (paramName.startsWith("GE_")) {
										if (compValue >= doubleValue)
											contains = true;
									} else if (paramName.startsWith("LT_")) {
										if (compValue < doubleValue)
											contains = true;
									} else if (paramName.startsWith("LE_")) {
										if (compValue <= doubleValue)
											contains = true;
									}
								} else if (conversedParamValue instanceof Boolean
										&& extensionField.get(key).getBsonType().equals(BsonType.BOOLEAN)) {
									boolean boolValue = (Boolean) conversedParamValue;
									boolean compValue = extensionField.getBoolean(key).getValue();
									if (paramName.startsWith("GT_")) {
										if (compValue != boolValue && compValue == true)
											contains = true;
									} else if (paramName.startsWith("GE_")) {
										if (compValue == true)
											contains = true;
									} else if (paramName.startsWith("LT_")) {
										if (compValue != boolValue && compValue == false)
											contains = true;
									} else if (paramName.startsWith("LE_")) {
										if (compValue == false)
											contains = true;
									}
								}
							}
						}
						if (contains == false)
							return false;

					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param address
	 * @param port
	 * @param remainingURL
	 *            start with /
	 * @param message
	 * @throws IOException
	 */
	private static void sendPost(URL captureURL, byte[] bytes) {
		HttpProcessor httpproc = HttpProcessorBuilder.create().add(new RequestContent()).add(new RequestTargetHost())
				.add(new RequestConnControl()).add(new RequestUserAgent("Test/1.1"))
				.add(new RequestExpectContinue(true)).build();

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost(captureURL.getHost(), captureURL.getPort());
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(bytes);
			baos.flush();
			baos.close();
			HttpEntity[] requestBodies = {
					new InputStreamEntity(new ByteArrayInputStream(baos.toByteArray()), ContentType.TEXT_PLAIN) };

			for (int i = 0; i < requestBodies.length; i++) {
				if (!conn.isOpen()) {
					Socket socket = new Socket(host.getHostName(), host.getPort());
					conn.bind(socket);
				}
				BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST",
						captureURL.getPath());
				request.setEntity(requestBodies[i]);
				System.out.println(">> Request URI: " + request.getRequestLine().getUri());

				httpexecutor.preProcess(request, httpproc, coreContext);
				HttpResponse response = httpexecutor.execute(request, conn, coreContext);
				httpexecutor.postProcess(response, httpproc, coreContext);

				// System.out.println("<< Response: " +
				// response.getStatusLine());
				// System.out.println(EntityUtils.toString(response.getEntity()));
				// System.out.println("==============");
				if (!connStrategy.keepAlive(response, coreContext)) {
					conn.close();
				} else {
					// System.out.println("Connection kept alive...");
				}
			}
		} catch (IOException e) {

		} catch (HttpException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static long getTimeMillis(String standardDateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			GregorianCalendar eventTimeCalendar = new GregorianCalendar();
			eventTimeCalendar.setTime(sdf.parse(standardDateString));
			return eventTimeCalendar.getTimeInMillis();
		} catch (ParseException e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				GregorianCalendar eventTimeCalendar = new GregorianCalendar();
				eventTimeCalendar.setTime(sdf.parse(standardDateString));
				return eventTimeCalendar.getTimeInMillis();
			} catch (ParseException e1) {
				Configuration.logger.log(Level.ERROR, e1.toString());
			}
		}
		// Never Happened
		return 0;
	}

	private static Set<String> addEPCtoSet(Set<String> epcSet, BsonArray epcList) {
		Iterator<BsonValue> epcIterator = epcList.iterator();
		while (epcIterator.hasNext()) {
			BsonDocument epcDocument = epcIterator.next().asDocument();
			epcSet.add(epcDocument.getString("epc").getValue());
		}
		return epcSet;
	}

	private static Set<String> addEPCClasstoSet(Set<String> epcSet, BsonArray epcList) {
		Iterator<BsonValue> epcIterator = epcList.iterator();
		while (epcIterator.hasNext()) {
			BsonDocument epcDocument = epcIterator.next().asDocument();
			epcSet.add(epcDocument.getString("epcClass").getValue());
		}
		return epcSet;
	}

	private static boolean isExtraParameter(String paramName) {

		if (paramName.contains("eventTime"))
			return false;
		if (paramName.contains("recordTime"))
			return false;
		if (paramName.contains("action"))
			return false;
		if (paramName.contains("bizStep"))
			return false;
		if (paramName.contains("disposition"))
			return false;
		if (paramName.contains("readPoint"))
			return false;
		if (paramName.contains("bizLocation"))
			return false;
		if (paramName.contains("bizTransaction"))
			return false;
		if (paramName.contains("source"))
			return false;
		if (paramName.contains("destination"))
			return false;
		if (paramName.contains("transformationID"))
			return false;
		return true;
	}

	private static BsonValue converseType(String value) {
		String[] valArr = value.split("\\^");
		if (valArr.length != 2) {
			return new BsonString(value);
		}
		try {
			String type = valArr[1];
			if (type.equals("int")) {
				return new BsonInt32(Integer.parseInt(valArr[0]));
			} else if (type.equals("long")) {
				return new BsonInt64(Long.parseLong(valArr[0]));
			} else if (type.equals("double")) {
				return new BsonDouble(Double.parseDouble(valArr[0]));
			} else if (type.equals("boolean")) {
				return new BsonBoolean(Boolean.parseBoolean(valArr[0]));
			} else {
				return new BsonString(value);
			}
		} catch (NumberFormatException e) {
			return new BsonString(value);
		}
	}
}
