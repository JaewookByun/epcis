package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.validation.IdentifierValidator;

/**
 * If this parameter is specified, the result will only include events that (a)
 * have a transformationID field (that is, TransformationEvents or extension
 * event type that extend TransformationEvent); and where (b) the
 * transformationID field is equal to one of the values specified in this
 * parameter.
 * 
 * List of URIs
 * 
 */
public class EQTransformationIDConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		for (String v : valueList) {
			try {
				IdentifierValidator.checkDocumentEPCPureIdentity(StaticResource.gcpLength, v);
			} catch (ValidationException e) {
				throw new QueryParameterException(e.getReason());
			}
		}
		return getEQQuery("transformationID", valueList);
	}
}
