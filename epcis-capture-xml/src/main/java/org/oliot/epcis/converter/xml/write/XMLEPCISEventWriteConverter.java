package org.oliot.epcis.converter.xml.write;

import static org.oliot.epcis.util.BSONWriteUtil.putErrorDeclaration;
import static org.oliot.epcis.util.BSONWriteUtil.putEventID;
import static org.oliot.epcis.util.BSONWriteUtil.putEventTime;
import static org.oliot.epcis.util.BSONWriteUtil.putEventTimeZoneOffset;
import static org.oliot.epcis.util.BSONWriteUtil.putFlatten;
import static org.oliot.epcis.util.BSONWriteUtil.putOtherAttributes;
import static org.oliot.epcis.util.BSONWriteUtil.putRecordTime;

import org.bson.Document;
import org.oliot.epcis.model.EPCISEventType;
import org.oliot.epcis.model.exception.ValidationException;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class XMLEPCISEventWriteConverter {
	public static Document convert(EPCISEventType obj) throws ValidationException {
		Document dbo = new Document();

		// Event Time
		putEventTime(dbo, obj.getEventTime());
		// Event Time Zone
		putEventTimeZoneOffset(dbo, obj.getEventTimeZoneOffset());
		// Record Time : according to M5
		putRecordTime(dbo);

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
