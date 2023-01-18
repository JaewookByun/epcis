package org.oliot.epcis.converter.pojo_to_bson;

import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.putErrorDeclaration;
import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.putEventID;
import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.putEventTime;
import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.putEventTimeZoneOffset;
import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.putFlatten;
import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.putOtherAttributes;
import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.putRecordTime;
import static org.oliot.epcis.converter.pojo_to_bson.POJOtoBSONUtil.putCertificationInfo;

import org.bson.Document;
import org.oliot.epcis.model.EPCISEventType;
import org.oliot.epcis.model.ValidationException;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * The class converts EPCISEvent from a format to another format.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class EPCISEventConverter {
	public static Document toBson(EPCISEventType obj) throws ValidationException {
		Document dbo = new Document();

		// Event Time
		putEventTime(dbo, obj.getEventTime());
		// Event Time Zone
		putEventTimeZoneOffset(dbo, obj.getEventTimeZoneOffset());
		// Record Time : according to M5
		putRecordTime(dbo);
		// Certification Info
		putCertificationInfo(dbo, obj.getCertificationInfo());
		
		// OtherAttributes
		putOtherAttributes(dbo, obj.getOtherAttributes());

		// Event ID
		putEventID(dbo, obj.getEventID());

		// Error Declaration
		Document errExtension = putErrorDeclaration(dbo, obj.getErrorDeclaration());
		if (errExtension != null)
			putFlatten(dbo, "errf", errExtension.get("extension", Document.class));

		return dbo;
	}
}
