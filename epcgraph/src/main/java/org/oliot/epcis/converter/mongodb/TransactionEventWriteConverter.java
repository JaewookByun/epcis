package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.TransactionEventExtensionType;
import org.oliot.model.epcis.TransactionEventType;

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
 */

public class TransactionEventWriteConverter {

	public BsonDocument convert(TransactionEventType transactionEventType, Integer gcpLength) {

		BsonDocument dbo = new BsonDocument();

		dbo.put("eventType", new BsonString("TransactionEvent"));
		// Event Time
		if (transactionEventType.getEventTime() != null)
			dbo.put("eventTime",
					new BsonDateTime(transactionEventType.getEventTime().toGregorianCalendar().getTimeInMillis()));
		// Event Time Zone
		if (transactionEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(transactionEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));
		// Parent ID
		if (transactionEventType.getParentID() != null)
			dbo.put("parentID",
					new BsonString(MongoWriterUtil.getInstanceEPC(transactionEventType.getParentID(), gcpLength)));
		// EPC List
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			BsonArray epcDBList = new BsonArray();
			for (int i = 0; i < epcList.size(); i++) {
				BsonDocument epcDB = new BsonDocument();
				epcDB.put("epc", new BsonString(MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(), gcpLength)));
				epcDBList.add(epcDB);
			}
			dbo.put("epcList", epcDBList);
		}
		// Action
		if (transactionEventType.getAction() != null)
			dbo.put("action", new BsonString(transactionEventType.getAction().name()));
		// BizStep
		if (transactionEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(transactionEventType.getBizStep()));
		// Disposition
		if (transactionEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(transactionEventType.getDisposition()));
		if (transactionEventType.getReadPoint() != null) {
			ReadPointType readPointType = transactionEventType.getReadPoint();
			BsonDocument readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		if (transactionEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transactionEventType.getBizLocation();
			BsonDocument bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}

		if (transactionEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transactionEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();

			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// Vendor Extension
		if (transactionEventType.getAny() != null) {
			List<Object> objList = transactionEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (transactionEventType.getExtension() != null) {
			TransactionEventExtensionType oee = transactionEventType.getExtension();
			BsonDocument extension = getTransactionEventExtensionObject(oee, gcpLength);
			dbo.put("extension", extension);
		}

		// Event ID
		if (transactionEventType.getBaseExtension() != null) {
			if (transactionEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(transactionEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (transactionEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = transactionEventType.getBaseExtension();
			ErrorDeclarationType edt = eeet.getErrorDeclaration();
			if (edt != null) {
				if (edt.getDeclarationTime() != null) {
					dbo.put("errorDeclaration", MongoWriterUtil.getErrorDeclaration(edt));
				}
			}
		}

		// Build Graph

		return dbo;
	}

	public void capture(TransactionEventType transactionEventType, Integer gcpLength) {

		ChronoGraph g = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName);

		// Parent ID
		String parentID = transactionEventType.getParentID();

		// EPC List
		HashSet<String> objectSet = new HashSet<String>();
		if (transactionEventType.getEpcList() != null) {
			EPCListType epcs = transactionEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				objectSet.add(epcList.get(i).getValue());
			}
		}

		Long eventTime = null;
		if (transactionEventType.getEventTime() != null)
			eventTime = transactionEventType.getEventTime().toGregorianCalendar().getTimeInMillis();
		else
			eventTime = System.currentTimeMillis();

		final long t = eventTime;

		BsonDocument objProperty = new BsonDocument();
		// Event Time Zone
		if (transactionEventType.getEventTimeZoneOffset() != null)
			objProperty.put("eventTimeZoneOffset", new BsonString(transactionEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		objProperty.put("recordTime", new BsonDateTime(recordTimeMilis));
		// Action
		if (transactionEventType.getAction() != null)
			objProperty.put("action", new BsonString(transactionEventType.getAction().name()));
		// Biz Step
		if (transactionEventType.getBizStep() != null)
			objProperty.put("bizStep", new BsonString(transactionEventType.getBizStep()));
		// Disposition
		if (transactionEventType.getDisposition() != null)
			objProperty.put("disposition", new BsonString(transactionEventType.getDisposition()));
		// BizTransaction
		if (transactionEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transactionEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			objProperty.put("bizTransactionList", bizTranList);
		}
		// Vendor Extension
		if (transactionEventType.getAny() != null) {
			List<Object> objList = transactionEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				objProperty.put("any", map2Save);

		}
		// Extension
		BsonArray classArray = null;
		BsonDocument extension = null;
		if (transactionEventType.getExtension() != null) {
			TransactionEventExtensionType oee = transactionEventType.getExtension();
			extension = getTransactionEventExtensionObject(oee, gcpLength);
			if (extension.containsKey("quantityList"))
				classArray = extension.getArray("quantityList");
		}

		final BsonDocument extensionf = extension;

		if (parentID != null) {
			g.getChronoVertex(parentID).setTimestampProperties(t, objProperty);
		}

		objectSet.parallelStream().forEach(object -> {
			// object = vid
			g.getChronoVertex(object).setTimestampProperties(t, objProperty);

			// Read Point
			if (transactionEventType.getReadPoint() != null) {
				ReadPointType readPointType = transactionEventType.getReadPoint();
				String locID = readPointType.getId();
				g.addTimestampEdgeProperties(object, locID, "isLocatedIn", t, new BsonDocument());
			}
			// BizLocation
			if (transactionEventType.getBizLocation() != null) {
				BusinessLocationType bizLocationType = transactionEventType.getBizLocation();
				String locID = bizLocationType.getId();
				g.addTimestampEdgeProperties(object, locID, "isLocatedIn", t, new BsonDocument());
			}

			if (extensionf != null) {
				if (extensionf.containsKey("sourceList")) {
					BsonArray sources = extensionf.getArray("sourceList");
					sources.parallelStream().forEach(elem -> {
						BsonDocument sourceDoc = elem.asDocument();
						if (sourceDoc.containsKey("urn:epcglobal:cbv:sdt:possessing_party")) {
							g.addTimestampEdgeProperties(object,
									sourceDoc.getString("urn:epcglobal:cbv:sdt:possessing_party").getValue(),
									"isPossessed", t, new BsonDocument("action", new BsonString("DELETE")));
						}

						if (sourceDoc.containsKey("urn:epcglobal:cbv:sdt:owning_party")) {
							g.addTimestampEdgeProperties(object,
									sourceDoc.getString("urn:epcglobal:cbv:sdt:owning_party").getValue(), "isOwned", t,
									new BsonDocument("action", new BsonString("DELETE")));
						}
					});
				}

				if (extensionf.containsKey("destinationList")) {
					BsonArray destinations = extensionf.getArray("destinationList");
					destinations.parallelStream().forEach(elem -> {
						BsonDocument destDoc = elem.asDocument();
						if (destDoc.containsKey("urn:epcglobal:cbv:sdt:possessing_party")) {
							g.addTimestampEdgeProperties(object,
									destDoc.getString("urn:epcglobal:cbv:sdt:possessing_party").getValue(),
									"isPossessed", t, new BsonDocument("action", new BsonString("ADD")));
						}

						if (destDoc.containsKey("urn:epcglobal:cbv:sdt:owning_party")) {
							g.addTimestampEdgeProperties(object,
									destDoc.getString("urn:epcglobal:cbv:sdt:owning_party").getValue(), "isOwned", t,
									new BsonDocument("action", new BsonString("ADD")));
						}
					});
				}
			}

		});

		classArray.parallelStream().forEach(classElem -> {

			BsonDocument classDoc = classElem.asDocument();
			String epcClass = classDoc.getString("epcClass").getValue();

			BsonDocument classProperty = objProperty.clone();
			if (!classDoc.containsKey("epcClass"))
				return;
			if (classDoc.containsKey("quantity"))
				classProperty.put("quantity", classDoc.getDouble("quantity"));
			if (classDoc.containsKey("uom"))
				classProperty.put("uom", classDoc.getString("uom"));
			g.getChronoVertex(epcClass).setTimestampProperties(t, classProperty);

			// Read Point
			if (transactionEventType.getReadPoint() != null) {
				ReadPointType readPointType = transactionEventType.getReadPoint();
				String locID = readPointType.getId();
				g.addTimestampEdgeProperties(epcClass, locID, "isLocatedIn", t, new BsonDocument());
			}
			// BizLocation
			if (transactionEventType.getBizLocation() != null) {
				BusinessLocationType bizLocationType = transactionEventType.getBizLocation();
				String locID = bizLocationType.getId();
				g.addTimestampEdgeProperties(epcClass, locID, "isLocatedIn", t, new BsonDocument());
			}

			if (extensionf != null) {
				if (extensionf.containsKey("sourceList")) {
					BsonArray sources = extensionf.getArray("sourceList");
					sources.parallelStream().forEach(elem -> {
						BsonDocument sourceDoc = elem.asDocument();
						if (sourceDoc.containsKey("urn:epcglobal:cbv:sdt:possessing_party")) {
							g.addTimestampEdgeProperties(epcClass,
									sourceDoc.getString("urn:epcglobal:cbv:sdt:possessing_party").getValue(),
									"isPossessed", t, new BsonDocument("action", new BsonString("DELETE")));
						}

						if (sourceDoc.containsKey("urn:epcglobal:cbv:sdt:owning_party")) {
							g.addTimestampEdgeProperties(epcClass,
									sourceDoc.getString("urn:epcglobal:cbv:sdt:owning_party").getValue(), "isOwned", t,
									new BsonDocument("action", new BsonString("DELETE")));
						}
					});
				}

				if (extensionf.containsKey("destinationList")) {
					BsonArray destinations = extensionf.getArray("destinationList");
					destinations.parallelStream().forEach(elem -> {
						BsonDocument destDoc = elem.asDocument();
						if (destDoc.containsKey("urn:epcglobal:cbv:sdt:possessing_party")) {
							g.addTimestampEdgeProperties(epcClass,
									destDoc.getString("urn:epcglobal:cbv:sdt:possessing_party").getValue(),
									"isPossessed", t, new BsonDocument("action", new BsonString("ADD")));
						}

						if (destDoc.containsKey("urn:epcglobal:cbv:sdt:owning_party")) {
							g.addTimestampEdgeProperties(epcClass,
									destDoc.getString("urn:epcglobal:cbv:sdt:owning_party").getValue(), "isOwned", t,
									new BsonDocument("action", new BsonString("ADD")));
						}
					});
				}
			}

		});

		g.shutdown();

		return;
	}
}
