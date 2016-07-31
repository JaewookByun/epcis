package org.oliot.epcis.service.capture;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISMasterDataDocumentType;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

@WebService(targetNamespace="urn:epcglobal:epcis-query:xsd:1")
public interface CoreCaptureService {

	@WebMethod(operationName = "EventCapture")
	public void capture(@WebParam(name = "epcisDocument") EPCISDocumentType epcisDocument);

	@WebMethod(operationName = "VocabularyCapture")
	public void capture(@WebParam(name = "epcisMasterDataDocument") EPCISMasterDataDocumentType epcisMasterDataDocument);
}
