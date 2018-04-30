package org.oliot.epcis.converter.mongodb.model;

/**
 * Copyright (C) 2014-17 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */
// See EPCIS v1.1 857 line
public enum VocabularyType {
	ReadPointID("urn:epcglobal:epcis:vtype:ReadPoint"), BusinessLocationID(
			"urn:epcglobal:epcis:vtype:BusinessLocation"), BusinessStepID(
					"urn:epcglobal:epcis:vtype:BusinessStep"), DispositionID(
							"urn:epcglobal:epcis:vtype:Disposition"), BusinessTransaction(
									"urn:epcglobal:epcis:vtype:BusinessTransaction"), BusinessTrasactionTypeID(
											"urn:epcglobal:epcis:vtype:BusinessTransactionType"), SourceDestTypeID(
													"urn:epcglobal:epcis:vtype:SourceDestType"), SourceDestID(
															"urn:epcglobal:epcis:vtype:SourceDest"), EPCClass(
																	"urn:epcglobal:epcis:vtype:EPCClass"), EPCInstance(
																			"urn:epcglobal:epcis:vtype:EPCInstance");
	private String vocabularyType;

	private VocabularyType(String vocabularyType) {
		this.vocabularyType = vocabularyType;
	}

	public String getVocabularyType() {
		return vocabularyType;
	}
}
