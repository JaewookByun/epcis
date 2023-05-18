package org.oliot.epcis.model.cbv;

public enum EPCISVocabularyType {
	ReadPointID("urn:epcglobal:epcis:vtype:ReadPoint"),
	BusinessLocationID("urn:epcglobal:epcis:vtype:BusinessLocation"),
	BusinessStepID("urn:epcglobal:epcis:vtype:BusinessStep"), DispositionID("urn:epcglobal:epcis:vtype:Disposition"),
	BusinessTransaction("urn:epcglobal:epcis:vtype:BusinessTransaction"),
	BusinessTransactionTypeID("urn:epcglobal:epcis:vtype:BusinessTransactionType"),
	EPCClass("urn:epcglobal:epcis:vtype:EPCClass"), SourceDestTypeID("urn:epcglobal:epcis:vtype:SourceDestType"),
	SourceDestID("urn:epcglobal:epcis:vtype:SourceDest"), LocationID("urn:epcglobal:epcis:vtype:Location"),
	ErrorReasonID("urn:epcglobal:epcis:vtype:ErrorReason"),
	SensorPropertyTypeID("urn:epcglobal:epcis:vtype:SensorPropertyType"),
	MicroorganismID("urn:epcglobal:epcis:vtype:Microorganism"),
	ChemicalSubstanceID("urn:epcglobal:epcis:vtype:ChemicalSubstance"),
	ResourceID("urn:epcglobal:epcis:vtype:Resource");

	private String vocabularyType;

	private EPCISVocabularyType(String vocabularyType) {
		this.vocabularyType = vocabularyType;
	}

	public String getVocabularyType() {
		return vocabularyType;
	}
}
