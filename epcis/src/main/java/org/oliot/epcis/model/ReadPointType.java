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
 * ReadPointType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;

public class ReadPointType {

	private URI id;
	private ReadPointExtensionType extension;
	private MessageElement[] _any;

	public ReadPointType() {
	}

	public ReadPointType(URI id, ReadPointExtensionType extension,
			MessageElement[] _any) {
		this.id = id;
		this.extension = extension;
		this._any = _any;
	}

	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}

	public ReadPointExtensionType getExtension() {
		return extension;
	}

	public void setExtension(ReadPointExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

}
