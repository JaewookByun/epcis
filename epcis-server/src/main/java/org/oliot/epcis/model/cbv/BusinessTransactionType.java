package org.oliot.epcis.model.cbv;

public enum BusinessTransactionType {
	bol("urn:epcglobal:cbv:btt:bol"), cert("urn:epcglobal:cbv:btt:cert"), desadv("urn:epcglobal:cbv:btt:desadv"),
	inv("urn:epcglobal:cbv:btt:inv"), pedigree("urn:epcglobal:cbv:btt:pedigree"), po("urn:epcglobal:cbv:btt:po"),
	poc("urn:epcglobal:cbv:btt:poc"), prodorder("urn:epcglobal:cbv:btt:prodorder"),
	recadv("urn:epcglobal:cbv:btt:recadv"), rma("urn:epcglobal:cbv:btt:rma"), testprd("urn:epcglobal:cbv:btt:testprd"),
	testres("urn:epcglobal:cbv:btt:testres"), upevt("urn:epcglobal:cbv:btt:upevt");

	private String businessTransactionType;

	private BusinessTransactionType(String businessTransactionType) {
		this.businessTransactionType = businessTransactionType;
	}

	public String getBusinessTransactionType() {
		return businessTransactionType;
	}
	
	/**
	 * @param shortCBV
	 * @return CBV or shortCBV if it is not defined in standard
	 */
	public static String getFullVocabularyName(String shortCBV) {
		try {
			return BusinessTransactionType.valueOf(shortCBV).businessTransactionType;
		}catch(IllegalArgumentException e) {
			return shortCBV;
		}
	}
	
	/**
	 * @param cbv
	 * @return shortCBV or cbv if it is not defined in standard
	 */
	public static String getShortVocabularyName(String cbv) {
		BusinessTransactionType[] cbvs = BusinessTransactionType.values();
		for(BusinessTransactionType v: cbvs) {
			if(cbv.equals(v.businessTransactionType))
				return v.name();
		}
		return cbv;
	}
}
