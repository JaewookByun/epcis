package org.oliot.epcis.query.converter.seq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.BSONReadUtil;

/**
 * If specified, the result will only include events that (a) have a non-null
 * readPoint field; and where (b) the value of the readPoint field matches one
 * of the specified URIs. If this parameter and WD_readPoint are both omitted,
 * events are returned regardless of the value of the readPoint field or whether
 * the readPoint field exists at all.
 * 
 * List of URIs
 * 
 */
public class EQATTRConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException, ImplementationException {
		List<String> valueList = getListOfString(value);
		List<String> mdValueList = new ArrayList<String>();
		String p = retrieveParameterType(key, 8);
		int uIdx = p.indexOf("_");
		String fieldName = p.substring(0, uIdx);
		String attrName = p.substring(uIdx + 1);
		String vtype = null;
		if (fieldName.equals("readPoint")) {
			vtype = "urn:epcglobal:epcis:vtype:ReadPoint";
		} else if (fieldName.equals("bizLocation")) {
			vtype = "urn:epcglobal:epcis:vtype:BusinessLocation";
		} else if (fieldName.equals("BusinessTransaction")) {
			vtype = "urn:epcglobal:epcis:vtype:BusinessTransaction";
		} else if (fieldName.equals("EPCClass")) {
			vtype = "urn:epcglobal:epcis:vtype:EPCClass";
		} else if (fieldName.equals("SourceDestID")) {
			vtype = "urn:epcglobal:epcis:vtype:SourceDest";
		} else if (fieldName.equals("LocationID")) {
			vtype = "urn:epcglobal:epcis:vtype:Location";
		} else if (fieldName.equals("PartyID")) {
			vtype = "urn:epcglobal:epcis:vtype:Party";
		} else if (fieldName.equals("MicroorganismID")) {
			vtype = "urn:epcglobal:epcis:vtype:Microorganism";
		} else if (fieldName.equals("ChemicalSubstanceID")) {
			vtype = "urn:epcglobal:epcis:vtype:ChemicalSubstance";
		} else if (fieldName.equals("ResourceID")) {
			vtype = "urn:epcglobal:epcis:vtype:Resource";
		} else {
			throw new QueryParameterException(fieldName
					+ " should be one of readPoint, bizLocation, BusinessTransaction, EPCClass, SourceDestID, LocationID, PartyID, MicroorganismID, ChemicalSubstanceID, ResourceID");
		}

		for (String v : valueList) {

			List<Document> received = new ArrayList<Document>();
			try {
				Document query = new org.bson.Document().append("type", vtype)
						.append("attributes." + BSONReadUtil.encodeMongoObjectKey(attrName), v);
				EPCISServer.mVocCollection.find(query).into(received);
			} catch (Throwable e) {
				ImplementationException e1 = new ImplementationException(ImplementationExceptionSeverity.ERROR, null,
						null, e.getMessage());
				throw e1;
			}
			if (received != null) {
				mdValueList.addAll(received.parallelStream().map(d -> d.getString("id")).toList());
			}
		}
		if (mdValueList.isEmpty()) {
			return getEQQuery("Make_Result_Empty", 1);
		} else {
			if (fieldName.equals("readPoint")) {
				return getEQQuery("readPoint", mdValueList);
			} else if (fieldName.equals("bizLocation")) {
				return getEQQuery("bizLocation", mdValueList);
			} else if (fieldName.equals("BusinessTransaction")) {
				return getEQQuery("bizTransactionList.value", mdValueList);
			} else if (fieldName.equals("EPCClass")) {
				return getEQQuery("quantityList.epcClass", mdValueList);
			} else if (fieldName.equals("SourceDestID")) {
				return new Document("$or", List.of(getEQQuery("sourceList.value", mdValueList),
						getEQQuery("destinationList.value", mdValueList)));
			} else if (fieldName.equals("LocationID")) {
				return new Document("$or", List.of(getEQQuery("sourceList.value", mdValueList),
						getEQQuery("destinationList.value", mdValueList)));
			} else if (fieldName.equals("PartyID")) {
				return new Document("$or", List.of(getEQQuery("sourceList.value", mdValueList),
						getEQQuery("destinationList.value", mdValueList)));
			} else if (fieldName.equals("MicroorganismID")) {
				return getEQQuery("sensorElementList.sensorReport.microorganism", mdValueList);
			} else if (fieldName.equals("ChemicalSubstanceID")) {
				return getEQQuery("sensorElementList.sensorReport.chemicalSubstance", mdValueList);
			}
		}
		return new Document();

	}
}
