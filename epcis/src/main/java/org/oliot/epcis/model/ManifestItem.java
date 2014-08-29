/**
 * ManifestItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.types.URI;

public class ManifestItem {
	private String mimeTypeQualifierCode;

	private URI uniformResourceIdentifier;

	private String description;

	private String languageCode;

	public ManifestItem() {
	}

	public ManifestItem(String mimeTypeQualifierCode,
			URI uniformResourceIdentifier, String description,
			String languageCode) {
		this.mimeTypeQualifierCode = mimeTypeQualifierCode;
		this.uniformResourceIdentifier = uniformResourceIdentifier;
		this.description = description;
		this.languageCode = languageCode;
	}

	public String getMimeTypeQualifierCode() {
		return mimeTypeQualifierCode;
	}

	public void setMimeTypeQualifierCode(String mimeTypeQualifierCode) {
		this.mimeTypeQualifierCode = mimeTypeQualifierCode;
	}

	public URI getUniformResourceIdentifier() {
		return uniformResourceIdentifier;
	}

	public void setUniformResourceIdentifier(URI uniformResourceIdentifier) {
		this.uniformResourceIdentifier = uniformResourceIdentifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}
