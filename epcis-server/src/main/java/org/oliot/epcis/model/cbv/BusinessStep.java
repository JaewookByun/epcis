package org.oliot.epcis.model.cbv;

public enum BusinessStep {
	accepting("urn:epcglobal:cbv:bizstep:accepting"), arriving("urn:epcglobal:cbv:bizstep:arriving"),
	assembling("urn:epcglobal:cbv:bizstep:assembling"), collecting("urn:epcglobal:cbv:bizstep:collecting"),
	commissioning("urn:epcglobal:cbv:bizstep:commissioning"), consigning("urn:epcglobal:cbv:bizstep:consigning"),
	creating_class_instance("urn:epcglobal:cbv:bizstep:creating_class_instance"),
	cycle_counting("urn:epcglobal:cbv:bizstep:cycle_counting"),
	decommissioning("urn:epcglobal:cbv:bizstep:decommissioning"), departing("urn:epcglobal:cbv:bizstep:departing"),
	destroying("urn:epcglobal:cbv:bizstep:destroying"), disassembling("urn:epcglobal:cbv:bizstep:disassembling"),
	dispensing("urn:epcglobal:cbv:bizstep:dispensing"), encoding("urn:epcglobal:cbv:bizstep:encoding"),
	entering_exiting("urn:epcglobal:cbv:bizstep:entering_exiting"), holding("urn:epcglobal:cbv:bizstep:holding"),
	inspecting("urn:epcglobal:cbv:bizstep:inspecting"), installing("urn:epcglobal:cbv:bizstep:installing"),
	killing("urn:epcglobal:cbv:bizstep:killing"), loading("urn:epcglobal:cbv:bizstep:loading"),
	other("urn:epcglobal:cbv:bizstep:other"), packing("urn:epcglobal:cbv:bizstep:packing"),
	picking("urn:epcglobal:cbv:bizstep:picking"), receiving("urn:epcglobal:cbv:bizstep:receiving"),
	removing("urn:epcglobal:cbv:bizstep:removing"), repackaging("urn:epcglobal:cbv:bizstep:repackaging"),
	repairing("urn:epcglobal:cbv:bizstep:repairing"), replacing("urn:epcglobal:cbv:bizstep:replacing"),
	reserving("urn:epcglobal:cbv:bizstep:reserving"), retail_selling("urn:epcglobal:cbv:bizstep:retail_selling"),
	sampling("urn:epcglobal:cbv:bizstep:sampling"), sensor_reporting("urn:epcglobal:cbv:bizstep:sensor_reporting"),
	shipping("urn:epcglobal:cbv:bizstep:shipping"), staging_outbound("urn:epcglobal:cbv:bizstep:staging_outbound"),
	stock_taking("urn:epcglobal:cbv:bizstep:stock_taking"), stocking("urn:epcglobal:cbv:bizstep:stocking"),
	storing("urn:epcglobal:cbv:bizstep:storing"), transporting("urn:epcglobal:cbv:bizstep:transporting"),
	unloading("urn:epcglobal:cbv:bizstep:unloading"), unpacking("urn:epcglobal:cbv:bizstep:unpacking"),
	void_shipping("urn:epcglobal:cbv:bizstep:void_shipping");

	private String businessStep;

	private BusinessStep(String businessStep) {
		this.businessStep = businessStep;
	}

	public String getBusinessStep() {
		return businessStep;
	}

	/**
	 * @param shortCBV
	 * @return CBV or shortCBV if it is not defined in standard
	 */
	public static String getFullVocabularyName(String shortCBV) {
		try {
			return BusinessStep.valueOf(shortCBV).businessStep;
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}

	/**
	 * @param cbv
	 * @return shortCBV or cbv if it is not defined in standard
	 */
	public static String getShortVocabularyName(String cbv) {
		BusinessStep[] cbvs = BusinessStep.values();
		for (BusinessStep v : cbvs) {
			if (cbv.equals(v.businessStep))
				return v.name();
		}
		throw new IllegalArgumentException("non-CBV business step: " + cbv);
	}
}
