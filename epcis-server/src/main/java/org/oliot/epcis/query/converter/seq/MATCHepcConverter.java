package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * If this parameter is specified, the result will only include events that (a)
 * have an epcList or a childEPCs field (that is, ObjectEvent, AggregationEvent,
 * TransactionEvent, AssociationEvent or extension event types that extend one
 * of those event types); and where (b) one of the EPCs listed in the epcList or
 * childEPCs field (depending on event type) matches one of the URIs specified
 * in this parameter, where the meaning of “matches” is as specified in section
 * 8.2.7.1.1. If this parameter is omitted, events are included regardless of
 * their epcList or childEPCs field or whether the epcList or childEPCs field
 * exists.
 *
 * List of URIs
 *
 * urn:epc:id:sgln:0614141[.]07346[.]....^regex
 *
 * String value = param.asString().getValue(); value = value.replace(".",
 * "[.]"); value = value.replace("*", "(.)*"); BsonRegularExpression expr = new
 * BsonRegularExpression(value); BsonDocument regexQuery = new
 * BsonDocument(field, new BsonDocument("$regex", expr));
 * orQueries.add(regexQuery);
 *
 */
public class MATCHepcConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		return getMATCHQuery("epcList", valueList);
	}
}
