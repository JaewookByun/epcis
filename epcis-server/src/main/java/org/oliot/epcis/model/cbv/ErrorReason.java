package org.oliot.epcis.model.cbv;

public enum ErrorReason {
	did_not_occur("urn:epcglobal:cbv:er:did_not_occur"), incorrect_data("urn:epcglobal:cbv:er:incorrect_data");

	private String errorReason;

	private ErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public String getErrorReason() {
		return errorReason;
	}
	
	/**
	 * @param shortCBV
	 * @return CBV or shortCBV if it is not defined in standard
	 */
	public static String getFullVocabularyName(String shortCBV) {
		try {
			return ErrorReason.valueOf(shortCBV).errorReason;
		}catch(IllegalArgumentException e) {
			return shortCBV;
		}
	}
	
	/**
	 * @param cbv
	 * @return shortCBV or cbv if it is not defined in standard
	 */
	public static String getShortVocabularyName(String cbv) {
		ErrorReason[] cbvs = ErrorReason.values();
		for(ErrorReason v: cbvs) {
			if(cbv.equals(v.errorReason))
				return v.name();
		}
		return cbv;
	}
}
