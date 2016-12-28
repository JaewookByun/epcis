package org.oliot.epcis.service.capture;

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

@WebService(endpointInterface = "org.oliot.epcis.service.capture.CoreCaptureService", targetNamespace="urn:epcglobal:epcis-capture:xsd:1")
public class SoapCaptureService implements CoreCaptureService {

	@Override
	public void capture(EPCISDocumentType epcisDocument) {
		CaptureService cs = new CaptureService();
		cs.capture(epcisDocument);
	}

	@Override
	public void capture(EPCISMasterDataDocumentType epcisMasterDataDocument) {
		CaptureService cs = new CaptureService();
		cs.capture(epcisMasterDataDocument);
	}

}
