package org.oliot.gcp.core;

/**
 * Copyright (C) 2020 Jaewook Byun
 * <p>
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v2.0.x
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class SimplePureIdentityFilter {

	private static final String[] ADIVarList = new String[] {
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.\\.([0-9A-Z/-]{1,30})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.([0-9A-Z/-]{1,32})\\.([0-9A-Z/-]{1,30})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.\\.(#[0-9A-Z/-]{1,29})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.([0-9A-Z/-]{1,32})\\.(#[0-9A-Z/-]{1,29})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.\\.([0-9A-Z/-]{1,30})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.([0-9A-Z/-]{1,32})\\.([0-9A-Z/-]{1,30})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.\\.(#[0-9A-Z/-]{1,29})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.([0-9A-Z/-]{1,32})\\.(#[0-9A-Z/-]{1,29})$" };

	private static final String[] GDTIList = new String[] {
			"^urn:epc:id:gdti:([0-9]{12})\\.([0-9]{0})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{11})\\.([0-9]{1})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{10})\\.([0-9]{2})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{9})\\.([0-9]{3})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{8})\\.([0-9]{4})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{7})\\.([0-9]{5})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{6})\\.([0-9]{6})\\.([0-9]{0,17})$" };

	private static final String[] cGDTIList = new String[] { "^urn:epc:idpat:gdti:([0-9]{12})\\.([0-9]{0})\\.\\*$",
			"^urn:epc:idpat:gdti:([0-9]{11})\\.([0-9]{1})\\.\\*$",
			"^urn:epc:idpat:gdti:([0-9]{10})\\.([0-9]{2})\\.\\*$", "^urn:epc:idpat:gdti:([0-9]{9})\\.([0-9]{3})\\.\\*$",
			"^urn:epc:idpat:gdti:([0-9]{8})\\.([0-9]{4})\\.\\*$", "^urn:epc:idpat:gdti:([0-9]{7})\\.([0-9]{5})\\.\\*$",
			"^urn:epc:idpat:gdti:([0-9]{6})\\.([0-9]{6})\\.\\*$" };

	private static final String[] GIAIList = new String[] {
			"^urn:epc:id:giai:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})$",
			"^urn:epc:id:giai:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})$",
			"^urn:epc:id:giai:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:giai:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})$",
			"^urn:epc:id:giai:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})$",
			"^urn:epc:id:giai:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})$",
			"^urn:epc:id:giai:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})$" };

	private static final String GID = "^urn:epc:id:gid:([0-9]{1,9})\\.([0-9]{1,8})\\.([0-9]{1,11})$";

	private static final String[] GRAIList = new String[] { "^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,6})$",
			"^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,16})$" };

	private static final String[] cGRAIList = new String[] {
			"^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,6})$",
			"^urn:epc:idpat:grai:([0-9]{12})\\.([0-9]{0})\\.\\*", "^urn:epc:idpat:grai:([0-9]{11})\\.([0-9]{1})\\.\\*$",
			"^urn:epc:idpat:grai:([0-9]{10})\\.([0-9]{2})\\.\\*$", "^urn:epc:idpat:grai:([0-9]{9})\\.([0-9]{3})\\.\\*$",
			"^urn:epc:idpat:grai:([0-9]{8})\\.([0-9]{4})\\.\\*$", "^urn:epc:idpat:grai:([0-9]{7})\\.([0-9]{5})\\.\\*$",
			"^urn:epc:idpat:grai:([0-9]{6})\\.([0-9]{6})\\.\\*$" };

	private static final String[] GSRNList = new String[] { "^urn:epc:id:gsrn:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:gsrn:([0-9]{11})\\.([0-9]{6})$", "^urn:epc:id:gsrn:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:gsrn:([0-9]{9})\\.([0-9]{8})$", "^urn:epc:id:gsrn:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:gsrn:([0-9]{7})\\.([0-9]{10})$", "^urn:epc:id:gsrn:([0-9]{6})\\.([0-9]{11})$" };

	private static final String[] GSRNPList = new String[] { "^urn:epc:id:gsrnp:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:gsrnp:([0-9]{11})\\.([0-9]{6})$", "^urn:epc:id:gsrnp:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:gsrnp:([0-9]{9})\\.([0-9]{8})$", "^urn:epc:id:gsrnp:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:gsrnp:([0-9]{7})\\.([0-9]{10})$", "^urn:epc:id:gsrnp:([0-9]{6})\\.([0-9]{11})$" };

	private static final String[] SGLNList = new String[] {
			"^urn:epc:id:sgln:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	private static final String[] PGLNList = new String[] { "^urn:epc:id:pgln:([0-9]{12})\\.([0-9]{0})$",
			"^urn:epc:id:pgln:([0-9]{11})\\.([0-9]{1})$", "^urn:epc:id:pgln:([0-9]{10})\\.([0-9]{2})$",
			"^urn:epc:id:pgln:([0-9]{9})\\.([0-9]{3})$", "^urn:epc:id:pgln:([0-9]{8})\\.([0-9]{4})$",
			"^urn:epc:id:pgln:([0-9]{7})\\.([0-9]{5})$", "^urn:epc:id:pgln:([0-9]{6})\\.([0-9]{6})$" };

	private static final String[] SGTINList = new String[] {
			"^urn:epc:id:sgtin:([0-9]{12})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{11})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{10})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{9})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{8})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{7})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{6})\\.([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	private static final String[] cGTINList = new String[] {
			"^urn:epc:idpat:sgtin:([0-9]{12})\\.(\\*|[0-9]{1})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{11})\\.(\\*|[0-9]{2})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{10})\\.(\\*|[0-9]{3})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{9})\\.(\\*|[0-9]{4})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{8})\\.(\\*|[0-9]{5})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{7})\\.(\\*|[0-9]{6})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{6})\\.(\\*|[0-9]{7})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$" };

	private static final String[] LGTINList = new String[] {
			"^urn:epc:class:lgtin:([0-9]{12})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{11})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{10})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{9})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{8})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{7})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{6})\\.([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	private static final String[] UPUIList = new String[] {
			"^urn:epc:id:upui:([0-9]{12})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{11})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{10})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{9})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{8})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{7})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{6})\\.([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,28})$" };

	private static final String[] SGCNList = new String[] {
			"^urn:epc:id:sgcn:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{0,12})$" };

	private static final String[] cSGCNList = new String[] { "^urn:epc:idpat:sgcn:([0-9]{12})\\.([0-9]{0})\\.\\*$",
			"^urn:epc:idpat:sgcn:([0-9]{11})\\.([0-9]{1})\\.\\*$",
			"^urn:epc:idpat:sgcn:([0-9]{10})\\.([0-9]{2})\\.\\*$", "^urn:epc:idpat:sgcn:([0-9]{9})\\.([0-9]{3})\\.\\*$",
			"^urn:epc:idpat:sgcn:([0-9]{8})\\.([0-9]{4})\\.\\*$", "^urn:epc:idpat:sgcn:([0-9]{7})\\.([0-9]{5})\\.\\*$",
			"^urn:epc:idpat:sgcn:([0-9]{6})\\.([0-9]{6})\\.\\*$" };

	private static final String[] CPIList = new String[] {
			"^urn:epc:id:cpi:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})\\.([!%-?A-Z_a-z\\x22]{1,12})$" };

	private static final String[] cCPIList = new String[] {
			"^urn:epc:idpat:cpi:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})\\.\\*$" };

	private static final String[] GSINList = new String[] { "^urn:epc:id:gsin:([0-9]{12})\\.([0-9]{4})$",
			"^urn:epc:id:gsin:([0-9]{11})\\.([0-9]{5})$", "^urn:epc:id:gsin:([0-9]{10})\\.([0-9]{6})$",
			"^urn:epc:id:gsin:([0-9]{9})\\.([0-9]{7})$", "^urn:epc:id:gsin:([0-9]{8})\\.([0-9]{8})$",
			"^urn:epc:id:gsin:([0-9]{7})\\.([0-9]{9})$", "^urn:epc:id:gsin:([0-9]{6})\\.([0-9]{10})$" };

	private static final String[] GINCList = new String[] { "^urn:epc:id:ginc:([0-9]{12})\\.([0-9]{1,18})$",
			"^urn:epc:id:ginc:([0-9]{11})\\.([0-9]{1,19})$", "^urn:epc:id:ginc:([0-9]{10})\\.([0-9]{1,20})$",
			"^urn:epc:id:ginc:([0-9]{9})\\.([0-9]{1,21})$", "^urn:epc:id:ginc:([0-9]{8})\\.([0-9]{1,22})$",
			"^urn:epc:id:ginc:([0-9]{7})\\.([0-9]{1,23})$", "^urn:epc:id:ginc:([0-9]{6})\\.([0-9]{1,24})$" };

	private static final String[] SSCCList = new String[] { "^urn:epc:id:sscc:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:sscc:([0-9]{11})\\.([0-9]{6})$", "^urn:epc:id:sscc:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:sscc:([0-9]{9})\\.([0-9]{8})$", "^urn:epc:id:sscc:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:sscc:([0-9]{7})\\.([0-9]{10})$", "^urn:epc:id:sscc:([0-9]{6})\\.([0-9]{11})$",

			"^urn:epc:id:sscc:([0-9]{12})\\.([0-9]{5})$", "^urn:epc:id:sscc:([0-9]{11})\\.([0-9]{6})$",
			"^urn:epc:id:sscc:([0-9]{10})\\.([0-9]{7})$", "^urn:epc:id:sscc:([0-9]{9})\\.([0-9]{8})$",
			"^urn:epc:id:sscc:([0-9]{8})\\.([0-9]{9})$", "^urn:epc:id:sscc:([0-9]{7})\\.([0-9]{10})$",
			"^urn:epc:id:sscc:([0-9]{6})\\.([0-9]{11})$" };

	private static final String[] USDODList = new String[] { "^urn:epc:id:usdod:([0-9A-HJ-NP-Z]{5})\\.([0-9]{1,8})$",

			"^urn:epc:id:usdod:([0-9A-HJ-NP-Z]{5,6})\\.([0-9]{1,11})$" };

	public static boolean isPureIdentityInstanceLevelObjectIdentifier(String epcString) {
		if (epcString == null)
			return false;
		// Instance-level object identifier (8.2.3.2, CBV)
		if (epcString.startsWith("urn:epc:id:sgtin")) {
			for (int i = 0; i < SGTINList.length; i++) {
				if (epcString.matches(SGTINList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:sscc")) {
			for (int i = 0; i < SSCCList.length; i++) {
				if (epcString.matches(SSCCList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:grai")) {
			for (int i = 0; i < GRAIList.length; i++) {
				if (epcString.matches(GRAIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:giai")) {
			for (int i = 0; i < GIAIList.length; i++) {
				if (epcString.matches(GIAIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsrn")) {
			for (int i = 0; i < GSRNList.length; i++) {
				if (epcString.matches(GSRNList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsrnp")) {
			for (int i = 0; i < GSRNPList.length; i++) {
				if (epcString.matches(GSRNPList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gdti")) {
			for (int i = 0; i < GDTIList.length; i++) {
				if (epcString.matches(GDTIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:upui")) {
			for (int i = 0; i < UPUIList.length; i++) {
				if (epcString.matches(UPUIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:cpi")) {
			for (int i = 0; i < CPIList.length; i++) {
				if (epcString.matches(CPIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:sgcn")) {
			for (int i = 0; i < SGCNList.length; i++) {
				if (epcString.matches(SGCNList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsin")) {
			for (int i = 0; i < GSINList.length; i++) {
				if (epcString.matches(GSINList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:ginc")) {
			for (int i = 0; i < GINCList.length; i++) {
				if (epcString.matches(GINCList[i])) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isPureIdentityClassLevelObjectIdentifier(String epcString) {
		if (epcString == null)
			return false;
		// Class-level object identifier (8.3.1, CBV)
		if (epcString.startsWith("urn:epc:idpat:sgtin")) {
			for (int i = 0; i < cGTINList.length; i++) {
				if (epcString.matches(cGTINList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:class:lgtin")) {
			for (int i = 0; i < LGTINList.length; i++) {
				if (epcString.matches(LGTINList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:grai")) {
			for (int i = 0; i < cGRAIList.length; i++) {
				if (epcString.matches(cGRAIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:gdti")) {
			for (int i = 0; i < cGDTIList.length; i++) {
				if (epcString.matches(cGDTIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:sgcn")) {
			for (int i = 0; i < cSGCNList.length; i++) {
				if (epcString.matches(cSGCNList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:cpi")) {
			for (int i = 0; i < cCPIList.length; i++) {
				if (epcString.matches(cCPIList[i])) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isPureIdentityBusinessTransactionIdentifier(String epcString) {
		if (epcString == null)
			return false;

		if (epcString.startsWith("urn:epc:id:gdti")) {
			for (int i = 0; i < GDTIList.length; i++) {
				if (epcString.matches(GDTIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsrn")) {
			for (int i = 0; i < GSRNList.length; i++) {
				if (epcString.matches(GSRNList[i])) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isPureIdentityTransformationIdentifier(String epcString) {
		if (epcString == null)
			return false;

		if (epcString.startsWith("urn:epc:id:gdti")) {
			for (int i = 0; i < GDTIList.length; i++) {
				if (epcString.matches(GDTIList[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isPureIdentityLocationIdentifier(String epcString) {
		if (epcString == null)
			return false;

		if (epcString.startsWith("urn:epc:id:sgln")) {
			for (int i = 0; i < SGLNList.length; i++) {
				if (epcString.matches(SGLNList[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isPureIdentityPartyIdentifier(String epcString) {
		if (epcString == null)
			return false;

		if (epcString.startsWith("urn:epc:id:pgln")) {
			for (int i = 0; i < PGLNList.length; i++) {
				if (epcString.matches(PGLNList[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isPureIdentity(String epcString) {
		if (epcString == null)
			return false;

		if (epcString.startsWith("urn:epc:id:adi")) {
			for (int i = 0; i < ADIVarList.length; i++) {
				if (epcString.matches(ADIVarList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gdti")) {
			for (int i = 0; i < GDTIList.length; i++) {
				if (epcString.matches(GDTIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:giai")) {
			for (int i = 0; i < GIAIList.length; i++) {
				if (epcString.matches(GIAIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gid")) {
			// noinspection RedundantIfStatement
			if (epcString.matches(GID)) {
				return true;
			}
		} else if (epcString.startsWith("urn:epc:id:grai")) {
			for (int i = 0; i < GRAIList.length; i++) {
				if (epcString.matches(GRAIList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsrn")) {
			for (int i = 0; i < GSRNList.length; i++) {
				if (epcString.matches(GSRNList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:sgln")) {
			for (int i = 0; i < SGLNList.length; i++) {
				if (epcString.matches(SGLNList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:sgtin")) {
			for (int i = 0; i < SGTINList.length; i++) {
				if (epcString.matches(SGTINList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:sscc")) {
			for (int i = 0; i < SSCCList.length; i++) {
				if (epcString.matches(SSCCList[i])) {
					return true;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:usdod")) {
			for (int i = 0; i < USDODList.length; i++) {
				if (epcString.matches(USDODList[i])) {
					return true;
				}
			}
		}
		return false;
	}
}
