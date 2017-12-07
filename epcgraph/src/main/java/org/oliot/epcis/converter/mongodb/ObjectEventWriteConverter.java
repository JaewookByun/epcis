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
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.ReadPointType;

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

public class ObjectEventWriteConverter {

	public BsonDocument convert(ObjectEventType objectEventType, Integer gcpLength) {
		BsonDocument dbo = new BsonDocument();
		dbo.put("eventType", new BsonString("ObjectEvent"));
		// Event Time
		if (objectEventType.getEventTime() != null)
			dbo.put("eventTime",
					new BsonDateTime(objectEventType.getEventTime().toGregorianCalendar().getTimeInMillis()));
		// Event Time Zone
		if (objectEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(objectEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));
		// EPC List
		List<EPC> epcList = null;
		if (objectEventType.getEpcList() != null) {
			EPCListType epcs = objectEventType.getEpcList();
			epcList = epcs.getEpc();
			BsonArray epcDBList = new BsonArray();

			for (int i = 0; i < epcList.size(); i++) {
				BsonDocument epcDB = new BsonDocument();
				epcDB.put("epc", new BsonString(MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(), gcpLength)));
				epcDBList.add(epcDB);
			}
			dbo.put("epcList", epcDBList);
		}
		// Action
		if (objectEventType.getAction() != null)
			dbo.put("action", new BsonString(objectEventType.getAction().name()));
		// Biz Step
		if (objectEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(objectEventType.getBizStep()));
		// Disposition
		if (objectEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(objectEventType.getDisposition()));
		// Read Point
		if (objectEventType.getReadPoint() != null) {
			ReadPointType readPointType = objectEventType.getReadPoint();
			BsonDocument readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (objectEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = objectEventType.getBizLocation();
			BsonDocument bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}
		// BizTransaction
		if (objectEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = objectEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// ILMD: moves to Extension
		/*
		 * if (objectEventType.getIlmd() != null) { ILMDType ilmd =
		 * objectEventType.getIlmd(); if (ilmd.getExtension() != null) {
		 * ILMDExtensionType ilmdExtension = ilmd.getExtension(); BsonDocument map2Save
		 * = getILMDExtensionMap(ilmdExtension); if (map2Save != null) dbo.put("ilmd",
		 * map2Save); if (epcList != null) { MasterDataWriteConverter mdConverter = new
		 * MasterDataWriteConverter(); mdConverter.capture(epcList, map2Save); } } }
		 */
		// Vendor Extension
		if (objectEventType.getAny() != null) {
			List<Object> objList = objectEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (objectEventType.getExtension() != null) {
			ObjectEventExtensionType oee = objectEventType.getExtension();
			BsonDocument extension = getObjectEventExtensionObject(oee, gcpLength, epcList);
			dbo.put("extension", extension);
		}

		// Event ID
		if (objectEventType.getBaseExtension() != null) {
			if (objectEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(objectEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (objectEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = objectEventType.getBaseExtension();
			ErrorDeclarationType edt = eeet.getErrorDeclaration();
			if (edt != null) {
				if (edt.getDeclarationTime() != null) {
					dbo.put("errorDeclaration", MongoWriterUtil.getErrorDeclaration(edt));
				}
			}
		}

		// Build Graph
		capture(objectEventType, gcpLength);

		return dbo;
	}

	public void capture(ObjectEventType objectEventType, Integer gcpLength) {

		ChronoGraph g = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName);

		// EPC List
		HashSet<String> objectSet = new HashSet<String>();
		if (objectEventType.getEpcList() != null) {
			EPCListType epcs = objectEventType.getEpcList();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				objectSet.add(epcList.get(i).getValue());
			}
		}

		Long eventTime = null;
		if (objectEventType.getEventTime() != null)
			eventTime = objectEventType.getEventTime().toGregorianCalendar().getTimeInMillis();
		else
			eventTime = System.currentTimeMillis();

		final long t = eventTime;

		BsonDocument objProperty = new BsonDocument();
		// Event Time Zone
		if (objectEventType.getEventTimeZoneOffset() != null)
			objProperty.put("eventTimeZoneOffset", new BsonString(objectEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		objProperty.put("recordTime", new BsonDateTime(recordTimeMilis));
		// Action
		if (objectEventType.getAction() != null)
			objProperty.put("action", new BsonString(objectEventType.getAction().name()));
		// Biz Step
		if (objectEventType.getBizStep() != null)
			objProperty.put("bizStep", new BsonString(objectEventType.getBizStep()));
		// Disposition
		if (objectEventType.getDisposition() != null)
			objProperty.put("disposition", new BsonString(objectEventType.getDisposition()));
		// BizTransaction
		if (objectEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = objectEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			objProperty.put("bizTransactionList", bizTranList);
		}
		// Vendor Extension
		if (objectEventType.getAny() != null) {
			List<Object> objList = objectEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				objProperty.put("any", map2Save);

		}
		// Extension
		BsonArray classArray = null;
		BsonDocument extension = null;
		if (objectEventType.getExtension() != null) {
			ObjectEventExtensionType oee = objectEventType.getExtension();
			extension = getObjectEventExtensionObject(oee, gcpLength);
			if (extension.containsKey("quantityList"))
				classArray = extension.getArray("quantityList");
		}

		final BsonDocument extensionf = extension;

		objectSet.stream().forEach(object -> {
			// object = vid
			g.getChronoVertex(object).setTimestampProperties(t, objProperty);

			// Read Point
			if (objectEventType.getReadPoint() != null) {
				ReadPointType readPointType = objectEventType.getReadPoint();
				String locID = readPointType.getId();
				g.addTimestampEdgeProperties(object, locID, "isLocatedIn", t, new BsonDocument());
			}
			// BizLocation
			if (objectEventType.getBizLocation() != null) {
				BusinessLocationType bizLocationType = objectEventType.getBizLocation();
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

		classArray.stream().forEach(classElem -> {

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
			if (objectEventType.getReadPoint() != null) {
				ReadPointType readPointType = objectEventType.getReadPoint();
				String locID = readPointType.getId();
				g.addTimestampEdgeProperties(epcClass, locID, "isLocatedIn", t, new BsonDocument());
			}
			// BizLocation
			if (objectEventType.getBizLocation() != null) {
				BusinessLocationType bizLocationType = objectEventType.getBizLocation();
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
