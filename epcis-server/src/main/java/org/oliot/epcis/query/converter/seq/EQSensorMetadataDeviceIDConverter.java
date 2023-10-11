package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;
import org.oliot.epcis.tdt.TagDataTranslationEngine;

/**
 * If this parameter is specified, the result will only include events that (a)
 * accommodate a deviceID attribute; and where (b) the deviceID attribute is
 * equal to one of the URIs specified in this parameter. If this parameter is
 * omitted, events are returned regardless of the value of the deviceID
 * attribute or whether the deviceID attribute exists at all.
 * 
 * v2.0.0
 * 
 */
public class EQSensorMetadataDeviceIDConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		List<String> valueList = getListOfString(value);
		for (String v : valueList) {
			try {
				TagDataTranslationEngine.checkEPCPureIdentity(StaticResource.gcpLength, v);
			} catch (ValidationException e) {
				throw new QueryParameterException(e.getReason());
			}
		}
		return getEQQuery("sensorElementList.sensorMetadata.deviceID", valueList);
	}
}
