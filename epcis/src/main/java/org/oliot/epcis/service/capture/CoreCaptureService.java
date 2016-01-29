package org.oliot.epcis.service.capture;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISMasterDataDocumentType;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
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

@WebService
public interface CoreCaptureService {

	@WebMethod(operationName = "EventCapture")
	public void capture(EPCISDocumentType epcisDocument);

	@WebMethod(operationName = "VocabularyCapture")
	public void capture(EPCISMasterDataDocumentType epcisMasterDataDocument);
}
