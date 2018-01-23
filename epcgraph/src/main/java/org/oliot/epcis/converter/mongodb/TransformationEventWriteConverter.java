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
import org.oliot.model.epcis.DestinationListType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCISEventExtensionType;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ErrorDeclarationType;
import org.oliot.model.epcis.ILMDType;
import org.oliot.model.epcis.QuantityElementType;
import org.oliot.model.epcis.QuantityListType;
import org.oliot.model.epcis.ReadPointType;
import org.oliot.model.epcis.SourceDestType;
import org.oliot.model.epcis.SourceListType;
import org.oliot.model.epcis.TransformationEventExtensionType;
import org.oliot.model.epcis.TransformationEventType;

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

public class TransformationEventWriteConverter {

	public BsonDocument convert(TransformationEventType transformationEventType, Integer gcpLength) {

		BsonDocument dbo = new BsonDocument();

		dbo.put("eventType", new BsonString("TransformationEvent"));
		// Event Time
		if (transformationEventType.getEventTime() != null)
			dbo.put("eventTime",
					new BsonDateTime(transformationEventType.getEventTime().toGregorianCalendar().getTimeInMillis()));
		// Event Time Zone
		if (transformationEventType.getEventTimeZoneOffset() != null)
			dbo.put("eventTimeZoneOffset", new BsonString(transformationEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		dbo.put("recordTime", new BsonDateTime(recordTimeMilis));
		// Input EPCList
		if (transformationEventType.getInputEPCList() != null) {
			EPCListType epcs = transformationEventType.getInputEPCList();
			List<EPC> epcList = epcs.getEpc();
			BsonArray epcDBList = new BsonArray();

			for (int i = 0; i < epcList.size(); i++) {
				BsonDocument epcDB = new BsonDocument();
				epcDB.put("epc", new BsonString(MongoWriterUtil.getInstanceEPC(epcList.get(i).getValue(), gcpLength)));
				epcDBList.add(epcDB);
			}
			dbo.put("inputEPCList", epcDBList);
		}
		// Output EPCList
		List<EPC> outputList = null;
		if (transformationEventType.getOutputEPCList() != null) {
			EPCListType epcs = transformationEventType.getOutputEPCList();
			outputList = epcs.getEpc();
			BsonArray epcDBList = new BsonArray();

			for (int i = 0; i < outputList.size(); i++) {
				BsonDocument epcDB = new BsonDocument();
				epcDB.put("epc",
						new BsonString(MongoWriterUtil.getInstanceEPC(outputList.get(i).getValue(), gcpLength)));
				epcDBList.add(epcDB);
			}
			dbo.put("outputEPCList", epcDBList);
		}
		// TransformationID
		if (transformationEventType.getTransformationID() != null) {
			dbo.put("transformationID", new BsonString(transformationEventType.getTransformationID()));
		}
		// BizStep
		if (transformationEventType.getBizStep() != null)
			dbo.put("bizStep", new BsonString(transformationEventType.getBizStep()));
		// Disposition
		if (transformationEventType.getDisposition() != null)
			dbo.put("disposition", new BsonString(transformationEventType.getDisposition()));
		// ReadPoint
		if (transformationEventType.getReadPoint() != null) {
			ReadPointType readPointType = transformationEventType.getReadPoint();
			BsonDocument readPoint = getReadPointObject(readPointType, gcpLength);
			dbo.put("readPoint", readPoint);
		}
		// BizLocation
		if (transformationEventType.getBizLocation() != null) {
			BusinessLocationType bizLocationType = transformationEventType.getBizLocation();
			BsonDocument bizLocation = getBizLocationObject(bizLocationType, gcpLength);
			dbo.put("bizLocation", bizLocation);
		}
		// BizTransaction
		if (transformationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transformationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			dbo.put("bizTransactionList", bizTranList);
		}
		// Input Quantity List
		if (transformationEventType.getInputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getInputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			BsonArray quantityList = getQuantityObjectList(qetList, gcpLength);
			dbo.put("inputQuantityList", quantityList);
		}
		// Output Quantity List
		if (transformationEventType.getOutputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getOutputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			BsonArray quantityList = getQuantityObjectList(qetList, gcpLength);
			dbo.put("outputQuantityList", quantityList);
		}
		// Source List
		if (transformationEventType.getSourceList() != null) {
			SourceListType sdtl = transformationEventType.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			BsonArray dbList = getSourceDestObjectList(sdtList, gcpLength);
			dbo.put("sourceList", dbList);
		}
		// Dest List
		if (transformationEventType.getDestinationList() != null) {
			DestinationListType sdtl = transformationEventType.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			BsonArray dbList = getSourceDestObjectList(sdtList, gcpLength);
			dbo.put("destinationList", dbList);
		}
		// ILMD
		if (transformationEventType.getIlmd() != null) {
			ILMDType ilmd = transformationEventType.getIlmd();

			if (ilmd.getAny() != null) {
				BsonDocument map2Save = getAnyMap(ilmd.getAny());
				if (map2Save != null && map2Save.isEmpty() == false) {
					dbo.put("ilmd", new BsonDocument("any", map2Save));
				}
				if (outputList != null) {
					MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
					mdConverter.capture(outputList, map2Save);
				}
			}
		}

		// Vendor Extension
		if (transformationEventType.getAny() != null) {
			List<Object> objList = transformationEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				dbo.put("any", map2Save);

		}

		// Extension
		if (transformationEventType.getExtension() != null) {
			TransformationEventExtensionType oee = transformationEventType.getExtension();
			BsonDocument extension = getTransformationEventExtensionObject(oee);
			dbo.put("extension", extension);
		}

		// Event ID
		if (transformationEventType.getBaseExtension() != null) {
			if (transformationEventType.getBaseExtension().getEventID() != null) {
				dbo.put("eventID", new BsonString(transformationEventType.getBaseExtension().getEventID()));
			}
		}

		// Error Declaration
		// If declared, it notes that the event is erroneous
		if (transformationEventType.getBaseExtension() != null) {
			EPCISEventExtensionType eeet = transformationEventType.getBaseExtension();
			ErrorDeclarationType edt = eeet.getErrorDeclaration();
			if (edt != null) {
				if (edt.getDeclarationTime() != null) {
					dbo.put("errorDeclaration", MongoWriterUtil.getErrorDeclaration(edt));
				}
			}
		}

		// Build Graph
		capture(transformationEventType, gcpLength);

		return dbo;
	}

	public void capture(TransformationEventType transformationEventType, Integer gcpLength) {

		ChronoGraph pg = Configuration.persistentGraph;

		// input EPC list
		HashSet<String> inputSet = new HashSet<String>();
		if (transformationEventType.getInputEPCList() != null) {
			EPCListType epcs = transformationEventType.getInputEPCList();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				inputSet.add(epcList.get(i).getValue());
			}
		}

		// Output EPCList
		HashSet<String> outputSet = new HashSet<String>();
		if (transformationEventType.getOutputEPCList() != null) {
			EPCListType epcs = transformationEventType.getOutputEPCList();
			List<EPC> epcList = epcs.getEpc();
			for (int i = 0; i < epcList.size(); i++) {
				outputSet.add(epcList.get(i).getValue());
			}
		}

		// Input Quantity List
		BsonArray inputClassSet = null;
		if (transformationEventType.getInputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getInputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			inputClassSet = getQuantityObjectList(qetList, gcpLength);
		}
		// Output Quantity List
		BsonArray outputClass = null;
		if (transformationEventType.getOutputQuantityList() != null) {
			QuantityListType qetl = transformationEventType.getOutputQuantityList();
			List<QuantityElementType> qetList = qetl.getQuantityElement();
			outputClass = getQuantityObjectList(qetList, gcpLength);
		}
		final BsonArray outputClassSet = outputClass;

		Long eventTime = null;
		if (transformationEventType.getEventTime() != null)
			eventTime = transformationEventType.getEventTime().toGregorianCalendar().getTimeInMillis();
		else
			eventTime = System.currentTimeMillis();

		final long t = eventTime;

		BsonDocument objProperty = new BsonDocument();

		// Event Time Zone
		if (transformationEventType.getEventTimeZoneOffset() != null)
			objProperty.put("eventTimeZoneOffset", new BsonString(transformationEventType.getEventTimeZoneOffset()));
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMilis = recordTime.getTimeInMillis();
		objProperty.put("recordTime", new BsonDateTime(recordTimeMilis));

		// TransformationID
		if (transformationEventType.getTransformationID() != null) {
			objProperty.put("transformationID", new BsonString(transformationEventType.getTransformationID()));
		}
		// BizStep
		if (transformationEventType.getBizStep() != null)
			objProperty.put("bizStep", new BsonString(transformationEventType.getBizStep()));
		// Disposition
		if (transformationEventType.getDisposition() != null)
			objProperty.put("disposition", new BsonString(transformationEventType.getDisposition()));
		// BizTransaction
		if (transformationEventType.getBizTransactionList() != null) {
			BusinessTransactionListType bizListType = transformationEventType.getBizTransactionList();
			List<BusinessTransactionType> bizList = bizListType.getBizTransaction();
			BsonArray bizTranList = getBizTransactionObjectList(bizList);
			objProperty.put("bizTransactionList", bizTranList);
		}

		// Source List
		if (transformationEventType.getSourceList() != null) {
			SourceListType sdtl = transformationEventType.getSourceList();
			List<SourceDestType> sdtList = sdtl.getSource();
			BsonArray dbList = getSourceDestObjectList(sdtList, gcpLength);
			objProperty.put("sourceList", dbList);
		}
		// Dest List
		if (transformationEventType.getDestinationList() != null) {
			DestinationListType sdtl = transformationEventType.getDestinationList();
			List<SourceDestType> sdtList = sdtl.getDestination();
			BsonArray dbList = getSourceDestObjectList(sdtList, gcpLength);
			objProperty.put("destinationList", dbList);
		}
		// Vendor Extension
		if (transformationEventType.getAny() != null) {
			List<Object> objList = transformationEventType.getAny();
			BsonDocument map2Save = getAnyMap(objList);
			if (map2Save != null && map2Save.isEmpty() == false)
				objProperty.put("any", map2Save);
		}

		// Extension
		if (transformationEventType.getExtension() != null) {
			TransformationEventExtensionType oee = transformationEventType.getExtension();
			BsonDocument extension = getTransformationEventExtensionObject(oee);
			objProperty.put("extension", extension);
		}

		if (inputSet != null)
			inputSet.stream().forEach(input -> {

				// Read Point
				if (transformationEventType.getReadPoint() != null) {
					ReadPointType readPointType = transformationEventType.getReadPoint();
					String locID = readPointType.getId();
					pg.addTimestampEdgeProperties(input, locID, "isLocatedIn", t, new BsonDocument());
				}
				// BizLocation
				if (transformationEventType.getBizLocation() != null) {
					BusinessLocationType bizLocationType = transformationEventType.getBizLocation();
					String locID = bizLocationType.getId();
					pg.addTimestampEdgeProperties(input, locID, "isLocatedIn", t, new BsonDocument());
				}

				if (outputSet != null)
					outputSet.stream().forEach(output -> {
						pg.addTimestampEdgeProperties(input, output, "transformsTo", t, objProperty);
					});

				if (outputClassSet != null)
					outputClassSet.stream().forEach(classElem -> {
						String epcClass = classElem.asDocument().getString("epcClass").getValue();
						pg.addTimestampEdgeProperties(input, epcClass, "transformsTo", t, objProperty);
					});
			});

		if (inputClassSet != null)
			inputClassSet.stream().forEach(inputClassElem -> {
				BsonDocument classDoc = inputClassElem.asDocument();
				String inputClassID = inputClassElem.asDocument().getString("epcClass").getValue();
				BsonDocument classProperty = new BsonDocument();
				if (!classDoc.containsKey("epcClass"))
					return;
				if (classDoc.containsKey("quantity"))
					classProperty.put("quantity", classDoc.getDouble("quantity"));
				if (classDoc.containsKey("uom"))
					classProperty.put("uom", classDoc.getString("uom"));

				// Read Point
				if (transformationEventType.getReadPoint() != null) {
					ReadPointType readPointType = transformationEventType.getReadPoint();
					String locID = readPointType.getId();
					pg.addTimestampEdgeProperties(inputClassID, locID, "isLocatedIn", t, new BsonDocument());
				}
				// BizLocation
				if (transformationEventType.getBizLocation() != null) {
					BusinessLocationType bizLocationType = transformationEventType.getBizLocation();
					String locID = bizLocationType.getId();
					pg.addTimestampEdgeProperties(inputClassID, locID, "isLocatedIn", t, new BsonDocument());
				}

				if (outputSet != null)
					outputSet.stream().forEach(output -> {
						pg.addTimestampEdgeProperties(inputClassID, output, "transformsTo", t, objProperty);
					});

				if (outputClassSet != null)
					outputClassSet.stream().forEach(classElem -> {
						String epcClass = classElem.asDocument().getString("epcClass").getValue();
						pg.addTimestampEdgeProperties(inputClassID, epcClass, "transformsTo", t, objProperty);
					});

				pg.getChronoVertex(inputClassID).setTimestampProperties(t, classProperty);
			});

		if (outputSet != null)
			outputSet.stream().forEach(output -> {

				// Read Point
				if (transformationEventType.getReadPoint() != null) {
					ReadPointType readPointType = transformationEventType.getReadPoint();
					String locID = readPointType.getId();
					pg.addTimestampEdgeProperties(output, locID, "isLocatedIn", t, new BsonDocument());
				}
				// BizLocation
				if (transformationEventType.getBizLocation() != null) {
					BusinessLocationType bizLocationType = transformationEventType.getBizLocation();
					String locID = bizLocationType.getId();
					pg.addTimestampEdgeProperties(output, locID, "isLocatedIn", t, new BsonDocument());
				}
			});

		if (outputClassSet != null)
			outputClassSet.stream().forEach(outputClassElem -> {

				BsonDocument classDoc = outputClassElem.asDocument();
				String outputClassID = outputClassElem.asDocument().getString("epcClass").getValue();
				BsonDocument classProperty = new BsonDocument();
				if (!classDoc.containsKey("epcClass"))
					return;
				if (classDoc.containsKey("quantity"))
					classProperty.put("quantity", classDoc.getDouble("quantity"));
				if (classDoc.containsKey("uom"))
					classProperty.put("uom", classDoc.getString("uom"));

				// Read Point
				if (transformationEventType.getReadPoint() != null) {
					ReadPointType readPointType = transformationEventType.getReadPoint();
					String locID = readPointType.getId();
					pg.addTimestampEdgeProperties(outputClassID, locID, "isLocatedIn", t, new BsonDocument());
				}
				// BizLocation
				if (transformationEventType.getBizLocation() != null) {
					BusinessLocationType bizLocationType = transformationEventType.getBizLocation();
					String locID = bizLocationType.getId();
					pg.addTimestampEdgeProperties(outputClassID, locID, "isLocatedIn", t, new BsonDocument());
				}

				pg.getChronoVertex(outputClassID).setTimestampProperties(t, classProperty);
			});

		return;
	}
}
