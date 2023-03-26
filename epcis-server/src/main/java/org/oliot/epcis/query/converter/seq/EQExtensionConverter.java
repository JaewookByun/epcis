package org.oliot.epcis.query.converter.seq;

import org.bson.Document;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.query.converter.BaseConverter;
import org.oliot.epcis.query.converter.QueryConverter;

/**
 * This is not a single parameter, but a family of parameters. If a parameter of
 * this form is specified, the result will only include events that (a) have a
 * top-level extension field named fieldname whose type is either String or a
 * vocabulary type; and where (b) the value of that field matches one of the
 * values specified in this parameter. fieldname is the fully qualified name of
 * a top-level extension field. The name of an extension field is an XML qname;
 * that is, a pair consisting of an XML namespace URI and a name. The name of
 * the corresponding query parameter is constructed by concatenating the
 * following: the string EQ_, the namespace URI for the extension field, a pound
 * sign (#), and the name of the extension field. “Top level” means that the
 * matching extension data field must be nested as an immediate child attribute
 * of the containing EPCIS event, not a data field nested within a top-level
 * event extension or class. See EQ_INNER_fieldname for querying data fields
 * nested within extension elements / classes.
 * 
 * Like EQ_fieldname as described above, but may be applied to a field of type
 * Int, Float, Double or Time. The result will include events that (a) have a
 * field named fieldname; and where (b) the type of the field matches the type
 * of this parameter (Int, Float, Double or Time); and where (c) the value of
 * the field is equal to the specified value. fieldname is constructed as for
 * EQ_fieldname.
 * 
 * v2.0.0
 */
public class EQExtensionConverter extends BaseConverter implements QueryConverter {

	@Override
	public Document convert(String key, Object value) throws QueryParameterException {
		return getEQExtensionQuery("extension." + retrieveParameterType(key, 3), value);
	}
}
