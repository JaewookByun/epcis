/**
 * Copyright (C) 2014 KAIST RESL 
 *
 * This file is part of Oliot (oliot.org).

 * @author Jack Jaewook Byun, Ph.D student
 * Korea Advanced Institute of Science and Technology
 * Real-time Embedded System Laboratory(RESL)
 * bjw0829@kaist.ac.kr
 */

/**
 * EPCISBodyType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.message.MessageElement;

/**
 * specific body that contains EPCIS related Events.
 */

public class EPCISBodyType {

	private EventListType eventList;
	private EPCISBodyExtensionType extension;
	private MessageElement[] _any;

	public EPCISBodyType() {
	}

	public EPCISBodyType(EventListType eventList,
			EPCISBodyExtensionType extension, MessageElement[] _any) {
		this.eventList = eventList;
		this.extension = extension;
		this._any = _any;
	}

	public EventListType getEventList() {
		return eventList;
	}

	public void setEventList(EventListType eventList) {
		this.eventList = eventList;
	}

	public EPCISBodyExtensionType getExtension() {
		return extension;
	}

	public void setExtension(EPCISBodyExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

}
