package org.oliot.epcis.query.converter;

import java.util.HashMap;

import org.oliot.epcis.query.converter.smdq.EQATTRConverter;
import org.oliot.epcis.query.converter.smdq.EQNameConverter;
import org.oliot.epcis.query.converter.smdq.HASATTRConverter;
import org.oliot.epcis.query.converter.smdq.VocabularyNameConverter;
import org.oliot.epcis.query.converter.smdq.WDNameConverter;

public class SimpleMasterDataQueryFactory {
	private HashMap<String, QueryConverter> converterMap;

	public SimpleMasterDataQueryFactory() {
		converterMap = new HashMap<String, QueryConverter>();
		converterMap.put("vocabularyName", new VocabularyNameConverter());
		converterMap.put("EQ_name", new EQNameConverter());
		converterMap.put("WD_name", new WDNameConverter());
		converterMap.put("HASATTR", new HASATTRConverter());
		converterMap.put("EQATTR_", new EQATTRConverter());

		// includeAttributes
		// includeChildren
		// attributeNames (proj)
		// maxElementCount

	}

	public HashMap<String, QueryConverter> getConverterMap() {
		return converterMap;
	}
}