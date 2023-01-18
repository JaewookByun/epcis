package org.oliot.epcis.model.cbv;

public enum Disposition {
	active("urn:epcglobal:cbv:disp:active"), available("urn:epcglobal:cbv:disp:available"),
	completeness_verified("urn:epcglobal:cbv:disp:completeness_verified"),
	completeness_inferred("urn:epcglobal:cbv:disp:completeness_inferred"),
	conformant("urn:epcglobal:cbv:disp:conformant"), container_closed("urn:epcglobal:cbv:disp:container_closed"),
	container_open("urn:epcglobal:cbv:disp:container_open"), damaged("urn:epcglobal:cbv:disp:damaged"),
	destroyed("urn:epcglobal:cbv:disp:destroyed"), dispensed("urn:epcglobal:cbv:disp:dispensed"),
	disposed("urn:epcglobal:cbv:disp:disposed"), encoded("urn:epcglobal:cbv:disp:encoded"),
	expired("urn:epcglobal:cbv:disp:expired"), in_progress("urn:epcglobal:cbv:disp:in_progress"),
	in_transit("urn:epcglobal:cbv:disp:in_transit"), inactive("urn:epcglobal:cbv:disp:inactive"),
	mismatch_instance("mismatch_instance"), mismatch_class("urn:epcglobal:cbv:disp:mismatch_class"),
	mismatch_quantity("urn:epcglobal:cbv:disp:mismatch_quantity"),
	needs_replacement("urn:epcglobal:cbv:disp:needs_replacement"),
	no_pedigree_match("urn:epcglobal:cbv:disp:no_pedigree_match"), DEPRECATED("urn:epcglobal:cbv:disp:DEPRECATED"),
	non_conformant("urn:epcglobal:cbv:disp:non_conformant"),
	non_sellable_other("urn:epcglobal:cbv:disp:non_sellable_other"),
	partially_dispensed("urn:epcglobal:cbv:disp:partially_dispensed"), recalled("urn:epcglobal:cbv:disp:recalled"),
	reserved("urn:epcglobal:cbv:disp:reserved"), retail_sold("urn:epcglobal:cbv:disp:retail_sold"),
	returned("urn:epcglobal:cbv:disp:returned"), sellable_accessible("urn:epcglobal:cbv:disp:sellable_accessible"),
	sellable_not_accessible("urn:epcglobal:cbv:disp:sellable_not_accessible"), stolen("urn:epcglobal:cbv:disp:stolen"),
	unavailable("urn:epcglobal:cbv:disp:unavailable"), unknown("urn:epcglobal:cbv:disp:unknown");

	private String disposition;

	private Disposition(String disposition) {
		this.disposition = disposition;
	}

	public String getDisposition() {
		return disposition;
	}
	
	/**
	 * @param shortCBV
	 * @return CBV or shortCBV if it is not defined in standard
	 */
	public static String getFullVocabularyName(String shortCBV) {
		try {
			return Disposition.valueOf(shortCBV).disposition;
		}catch(IllegalArgumentException e) {
			return shortCBV;
		}
	}
	
	/**
	 * @param cbv
	 * @return shortCBV or cbv if it is not defined in standard
	 */
	public static String getShortVocabularyName(String cbv) {
		Disposition[] cbvs = Disposition.values();
		for(Disposition v: cbvs) {
			if(cbv.equals(v.disposition))
				return v.name();
		}
		return cbv;
	}
}
