package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.cbv.UnitOfMeasure;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;

/**
 * EQ: If this parameter is specified, the result will only include events that
 * (a) have a quantity and a uom field as part of a QuantityElement; and where
 * (b) a pair of quantity and uom is equal to or – in case the query includes a
 * uom value that is different from those in the events – corresponds to the
 * specified parameter. If omitted, events are in included regardless of the
 * values of their quantity and uom fields.
 * 
 */
public class EQQuantityConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		double d = getDouble(value);

		String type = retrieveParameterType(key, 12);

		if (!type.isEmpty() && !StaticResource.unitOfMeasure.contains(type))
			throw new QueryParameterException(
					"the value of a parameter is of the wrong type or out of range: value should be one of unit of measures defined in "
							+ UnitOfMeasure.values().getClass().getCanonicalName());

		List<Document> docList = null;
		if (type.isEmpty()) {
			docList = List.of(
					new Document("quantityList", new Document("$elemMatch", new Document().append("quantity", d))),
					new Document("inputQuantityList", new Document("$elemMatch", new Document().append("quantity", d))),
					new Document("outputQuantityList",
							new Document("$elemMatch", new Document().append("quantity", d))));
		} else {
			docList = List.of(
					new Document("quantityList",
							new Document("$elemMatch", new Document().append("uom", type).append("quantity", d))),
					new Document("inputQuantityList",
							new Document("$elemMatch", new Document().append("uom", type).append("quantity", d))),
					new Document("outputQuantityList",
							new Document("$elemMatch", new Document().append("uom", type).append("quantity", d))));
		}

		return new Document("$or", docList);
	}
}
