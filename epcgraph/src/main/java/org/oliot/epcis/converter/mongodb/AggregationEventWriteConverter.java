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
import org.oliot.model.epcis.AggregationEventExtensionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.BusinessTransactionListType;
import org.oliot.model.epcis.BusinessTransactionType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
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

public class AggregationEventWriteConverter {

	public BsonDocument convert(AggregationEventType aggregationEventType, Integer gcpLength) {
		BsonDocument dbo = new BsonDocument();

		dbo.put("eventType", new BsonString("AggregationEvent"));
		// Event Time
		if (aggregationEventType.getEventTime() != null)
			dbo.put("eventTime",
					new BsonDateTime(aggregationEventType.getEventTime().toGregorianCalendar().getTimeInMillis()));
		// Event Time Zone
		if (aggregationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(aggregationEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));

		// Parent ID
		if (aggregationEventType.getParentID() != null) {
			dbo.put("parentID",
					new BsonString(MongoWriterUtil.getInstanceEPC(aggregationEventType.getParentID(), gcpLength)));
		}
		// Child EPCs
		if (aggregationEventType.getChildEPCs() != null) {
			EPCListType epcs = aggregationEventType.getChildEPCs();
			List<EPC> epcList = epcs.getEpc();
			BsonArray epcDBList = new BsonArray();

			for (int i = 0; i < epcList.size(); i++) {
				BsonDocument epcDB = new BsonDocument();
				epcDB.put("epc", new BsonString(MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(), gcpLength)));
				epcDBList.add(epcDB);
			}
			dbo.put("childEPCs", epcDBList);
		}
		// Action
		if (aggregationEventType.getAction() != null)
			dbo.put("action", new BsonString(aggregationEventType.getAction().name()));
		// Biz Step
		if (aggregationEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(aggregationEventType.getBizStep()));
		// Disposition
		if (aggregationEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(aggregationEventType.getDisposition()));
		// ReadPoint
		if (aggregationEventType.getReadPoint() != null) {
			ReadPointType readPointType = aggregationEventType.getReadPoint();
			BsonDocument readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (aggregationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = aggregationEventType.getBizLocation();
			BsonDocument bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}

		if (aggregationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = aggregationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();

			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}

		// Vendor Extension
		if (aggregationEventType.getAny() != null) {
			List<Object> objList = aggregationEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (aggregationEventType.getExtension() != null) {
			AggregationEventExtensionType aee = aggregationEventType.getExtension();
			BsonDocument extension = getAggregationEventExtensionObject(aee, gcpLength);
			dbo.put("extension", extension);

		}

		// Event ID
		if (aggregationEventType.getBaseExtension() != null) {
			if (aggregationEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(aggregationEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (aggregationEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = aggregationEventType.getBaseExtension();
			ErrorDeclarationType edt = eeet.getErrorDeclaration();
			if (edt != null) {
				if (edt.getDeclarationTime() != null) {
					dbo.put("errorDeclaration", MongoWriterUtil.getErrorDeclaration(edt));
				}
			}
		}

		// Build Graph
		capture(aggregationEventType, gcpLength);

		return dbo;
	}

	public void capture(AggregationEventType aggregationEventType, Integer gcpLength) {

		ChronoGraph g = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName);

		// Parent ID
		String parent = null;
		if (aggregationEventType.getParentID() != null) {
			parent = aggregationEventType.getParentID();
		}
		final String parentID = parent;

		// Child EPC List
		HashSet<String> childSet = new HashSet<String>();
		if (aggregationEventType.getChildEPCs() != null) {
			EPCListType epcs = aggregationEventType.getChildEPCs();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				childSet.add(epcList.get(i).getValue());
			}
		}

		Long eventTime = null;
		if (aggregationEventType.getEventTime() != null)
			eventTime = aggregationEventType.getEventTime().toGregorianCalendar().getTimeInMillis();
		else
			eventTime = System.currentTimeMillis();

		final long t = eventTime;

		BsonDocument objProperty = new BsonDocument();

		// Event Time Zone
		if (aggregationEventType.getEventTimeZoneOffset() != null)
			objProperty.put("eventTimeZoneOffset", new BsonString(aggregationEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		objProperty.put("recordTime", new BsonDateTime(recordTimeMilis));

		// Action
		if (aggregationEventType.getAction() != null)
			objProperty.put("action", new BsonString(aggregationEventType.getAction().name()));
		// Biz Step
		if (aggregationEventType.getBizStep() != null)
			objProperty.put("bizStep", new BsonString(aggregationEventType.getBizStep()));
		// Disposition
		if (aggregationEventType.getDisposition() != null)
			objProperty.put("disposition", new BsonString(aggregationEventType.getDisposition()));
		// Biztransaction
		if (aggregationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = aggregationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();

			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			objProperty.put("bizTransactionList", bizTranList);
		}
		// Vendor Extension
		if (aggregationEventType.getAny() != null) {
			List<Object> objList = aggregationEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				objProperty.put("any", map2Save);
		}

		// Extension
		BsonArray childClassArray = new BsonArray();
		BsonDocument extension = null;
		if (aggregationEventType.getExtension() != null) {
			AggregationEventExtensionType aee = aggregationEventType.getExtension();
			extension = getAggregationEventExtensionObject(aee, gcpLength);
			if (extension.containsKey("childQuantityList"))
				childClassArray = extension.getArray("childQuantityList");
		}

		final BsonDocument extensionf = extension;

		// object = vid
		if (parentID != null) {
			// Read Point
			if (aggregationEventType.getReadPoint() != null) {
				ReadPointType readPointType = aggregationEventType.getReadPoint();
				String locID = readPointType.getId();
				g.addTimestampEdgeProperties(parentID, locID, "isLocatedIn", t, new BsonDocument());
			}
			// BizLocation
			if (aggregationEventType.getBizLocation() != null) {
				BusinessLocationType bizLocationType = aggregationEventType.getBizLocation();
				String locID = bizLocationType.getId();
				g.addTimestampEdgeProperties(parentID, locID, "isLocatedIn", t, new BsonDocument());
			}

			if (extensionf != null) {
				if (extensionf.containsKey("sourceList")) {
					BsonArray sources = extensionf.getArray("sourceList");
					sources.parallelStream().forEach(elem -> {
						BsonDocument sourceDoc = elem.asDocument();
						if (sourceDoc.containsKey("urn:epcglobal:cbv:sdt:possessing_party")) {
							g.addTimestampEdgeProperties(parentID,
									sourceDoc.getString("urn:epcglobal:cbv:sdt:possessing_party").getValue(),
									"isPossessed", t, new BsonDocument("action", new BsonString("DELETE")));
						}

						if (sourceDoc.containsKey("urn:epcglobal:cbv:sdt:owning_party")) {
							g.addTimestampEdgeProperties(parentID,
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
							g.addTimestampEdgeProperties(parentID,
									destDoc.getString("urn:epcglobal:cbv:sdt:possessing_party").getValue(),
									"isPossessed", t, new BsonDocument("action", new BsonString("ADD")));
						}

						if (destDoc.containsKey("urn:epcglobal:cbv:sdt:owning_party")) {
							g.addTimestampEdgeProperties(parentID,
									destDoc.getString("urn:epcglobal:cbv:sdt:owning_party").getValue(), "isOwned", t,
									new BsonDocument("action", new BsonString("ADD")));
						}
					});
				}
			}
		}

		childSet.stream().forEach(child -> {
			// Read Point
			if (aggregationEventType.getReadPoint() != null) {
				ReadPointType readPointType = aggregationEventType.getReadPoint();
				String locID = readPointType.getId();
				g.addTimestampEdgeProperties(child, locID, "isLocatedIn", t, new BsonDocument());
			}
			// BizLocation
			if (aggregationEventType.getBizLocation() != null) {
				BusinessLocationType bizLocationType = aggregationEventType.getBizLocation();
				String locID = bizLocationType.getId();
				g.addTimestampEdgeProperties(child, locID, "isLocatedIn", t, new BsonDocument());
			}

			if (extensionf != null) {
				if (extensionf.containsKey("sourceList")) {
					BsonArray sources = extensionf.getArray("sourceList");
					sources.parallelStream().forEach(elem -> {
						BsonDocument sourceDoc = elem.asDocument();
						if (sourceDoc.containsKey("urn:epcglobal:cbv:sdt:possessing_party")) {
							g.addTimestampEdgeProperties(child,
									sourceDoc.getString("urn:epcglobal:cbv:sdt:possessing_party").getValue(),
									"isPossessed", t, new BsonDocument("action", new BsonString("DELETE")));
						}

						if (sourceDoc.containsKey("urn:epcglobal:cbv:sdt:owning_party")) {
							g.addTimestampEdgeProperties(child,
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
							g.addTimestampEdgeProperties(child,
									destDoc.getString("urn:epcglobal:cbv:sdt:possessing_party").getValue(),
									"isPossessed", t, new BsonDocument("action", new BsonString("ADD")));
						}

						if (destDoc.containsKey("urn:epcglobal:cbv:sdt:owning_party")) {
							g.addTimestampEdgeProperties(child,
									destDoc.getString("urn:epcglobal:cbv:sdt:owning_party").getValue(), "isOwned", t,
									new BsonDocument("action", new BsonString("ADD")));
						}
					});
				}
			}
		});

		
		childClassArray.stream().forEach(classElem -> {

			BsonDocument classDoc = classElem.asDocument();
			String epcClass = classDoc.getString("epcClass").getValue();

			BsonDocument classProperty = new BsonDocument();
			if (!classDoc.containsKey("epcClass"))
				return;
			if (classDoc.containsKey("quantity"))
				classProperty.put("quantity", classDoc.getDouble("quantity"));
			if (classDoc.containsKey("uom"))
				classProperty.put("uom", classDoc.getString("uom"));
			g.getChronoVertex(epcClass).setTimestampProperties(t, classProperty);

			// Read Point
			if (aggregationEventType.getReadPoint() != null) {
				ReadPointType readPointType = aggregationEventType.getReadPoint();
				String locID = readPointType.getId();
				g.addTimestampEdgeProperties(epcClass, locID, "isLocatedIn", t, new BsonDocument());
			}
			// BizLocation
			if (aggregationEventType.getBizLocation() != null) {
				BusinessLocationType bizLocationType = aggregationEventType.getBizLocation();
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

		if (parentID != null) {
			childSet.stream().forEach(child -> {
				g.addTimestampEdgeProperties(parentID, child, "contains", t, objProperty);
			});
			childClassArray.stream().forEach(classElem -> {
				String epcClass = classElem.asDocument().getString("epcClass").getValue();
				g.addTimestampEdgeProperties(parentID, epcClass, "contains", t, objProperty);
			});
		}

		g.shutdown();

		return;
	}
}
