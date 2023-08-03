package org.oliot.epcis.query.converter.seq;

import java.util.List;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.cbv.UnitOfMeasure;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;
import org.oliot.epcis.resource.StaticResource;

/**
 * * GT: Like EQ_quantity_uom, but includes events whose quantity-uom-pair (or a
 * corresponding quantity-uom-pair when an alternative uom is applied) is
 * greater than the specified parameter.
 * 
 * 
 */
public class GTQuantityConverter extends BaseConverter implements QueryConverter {

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
					new Document("quantityList",
							new Document("$elemMatch", new Document().append("quantity", new Document("$gt", d)))),
					new Document("inputQuantityList",
							new Document("$elemMatch", new Document().append("quantity", new Document("$gt", d)))),
					new Document("outputQuantityList",
							new Document("$elemMatch", new Document().append("quantity", new Document("$gt", d)))));
		} else {
			docList = List.of(
					new Document("quantityList",
							new Document("$elemMatch",
									new Document().append("uom", type).append("quantity", new Document("$gt", d)))),
					new Document("inputQuantityList",
							new Document("$elemMatch",
									new Document().append("uom", type).append("quantity", new Document("$gt", d)))),
					new Document("outputQuantityList", new Document("$elemMatch",
							new Document().append("uom", type).append("quantity", new Document("$gt", d)))));
		}

		return new Document("$or", docList);
	}
}
