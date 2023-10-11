package org.oliot.epcis.query.converter.seq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.ImplementationExceptionSeverity;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.tdt.TagDataTranslationEngine;

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
public class WDBizLocationConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException, ImplementationException {
		List<String> valueList = getListOfString(value);
		Set<String> wdValues = new HashSet<String>();
		for (String v : valueList) {
			try {
				TagDataTranslationEngine.checkLocationEPCPureIdentity(StaticResource.gcpLength, v);
				wdValues.add(v);
				List<org.bson.Document> rDocs = new ArrayList<org.bson.Document>();
				EPCISServer.mVocCollection.find(new org.bson.Document("id", v)).into(rDocs);
				for (org.bson.Document rDoc : rDocs) {
					if (rDoc.containsKey("children")) {
						List<String> children = rDoc.getList("children", String.class);
						wdValues.addAll(children);
					}
				}

			} catch (ValidationException e) {
				throw new QueryParameterException(e.getReason());
			} catch (Throwable e) {
				throw new ImplementationException(ImplementationExceptionSeverity.ERROR, null, null, e.getMessage());
			}
		}
		return getEQQuery("bizLocation", new ArrayList<String>(wdValues));
	}
}
