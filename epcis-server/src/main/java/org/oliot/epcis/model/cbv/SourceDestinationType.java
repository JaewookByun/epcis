package org.oliot.epcis.model.cbv;

public enum SourceDestinationType {
	owning_party("urn:epcglobal:cbv:sdt:owning_party"), possessing_party("urn:epcglobal:cbv:sdt:possessing_party"),
	location("urn:epcglobal:cbv:sdt:location");

	private String sourceDestinationType;

	private SourceDestinationType(String sourceDestinationType) {
		this.sourceDestinationType = sourceDestinationType;
	}

	public String getSourceDestinationType() {
		return sourceDestinationType;
	}
	
	/**
	 * @param shortCBV
	 * @return CBV or shortCBV if it is not defined in standard
	 */
	public static String getFullVocabularyName(String shortCBV) {
		try {
			return SourceDestinationType.valueOf(shortCBV).sourceDestinationType;
		}catch(IllegalArgumentException e) {
			return shortCBV;
		}
	}
	
	/**
	 * @param cbv
	 * @return shortCBV or cbv if it is not defined in standard
	 */
	public static String getShortVocabularyName(String cbv) {
		SourceDestinationType[] cbvs = SourceDestinationType.values();
		for(SourceDestinationType v: cbvs) {
			if(cbv.equals(v.sourceDestinationType))
				return v.name();
		}
		return cbv;
	}
}
