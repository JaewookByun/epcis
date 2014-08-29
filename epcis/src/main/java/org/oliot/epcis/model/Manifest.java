/**
 * Manifest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import java.math.BigInteger;

public class Manifest {
	private BigInteger numberOfItems;

	private ManifestItem[] manifestItem;

	public Manifest() {
	}

	public Manifest(BigInteger numberOfItems, ManifestItem[] manifestItem) {
		this.numberOfItems = numberOfItems;
		this.manifestItem = manifestItem;
	}

	public BigInteger getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(BigInteger numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public ManifestItem[] getManifestItem() {
		return manifestItem;
	}

	public void setManifestItem(ManifestItem[] manifestItem) {
		this.manifestItem = manifestItem;
	}

}
