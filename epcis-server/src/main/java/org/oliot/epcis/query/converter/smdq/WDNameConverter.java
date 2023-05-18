package org.oliot.epcis.query.converter.smdq;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.ImplementationException;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.server.EPCISServer;

/**
 * If this parameter is specified, the result will only include events that (a)
 * have a non-null eventID field; and where (b) the eventID field is equal to
 * one of the values specified in this parameter. If this parameter is omitted,
 * events are returned regardless of the value of the eventID field or whether
 * the eventID field exists at all.
 * 
 * List of URIs
 * 
 */
public class WDNameConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException, ImplementationException {
		List<String> valueList = getListOfString(value);
		HashSet<String> wdValues = new HashSet<String>();
		for (String v : valueList) {
			try {
				URI.create(v);
				wdValues.add(v);
				List<org.bson.Document> rDocs = new ArrayList<org.bson.Document>();
				EPCISServer.mVocCollection.find(new org.bson.Document("id", v)).into(rDocs);
				for (org.bson.Document rDoc : rDocs) {
					if (rDoc.containsKey("children")) {
						List<String> children = rDoc.getList("children", String.class);
						wdValues.addAll(children);
					}
				}
			} catch (IllegalArgumentException e) {
				throw new QueryParameterException("the value of a parameter is of the wrong type or out of range: URI");
			}
		}

		return getEQQuery("id", new ArrayList<String>(wdValues));
	}
}
