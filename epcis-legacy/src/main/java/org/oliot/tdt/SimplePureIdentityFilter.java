package org.oliot.tdt;

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
			"^urn:epc:id:gdti:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,13})$",
			"^urn:epc:id:gdti:([0-9]{11})\\.([0-9]{1})\\.([0-9]{1,13})$",
			"^urn:epc:id:gdti:([0-9]{10})\\.([0-9]{2})\\.([0-9]{1,13})$",
			"^urn:epc:id:gdti:([0-9]{9})\\.([0-9]{3})\\.([0-9]{1,13})$",
			"^urn:epc:id:gdti:([0-9]{8})\\.([0-9]{4})\\.([0-9]{1,13})$",
			"^urn:epc:id:gdti:([0-9]{7})\\.([0-9]{5})\\.([0-9]{1,13})$",
			"^urn:epc:id:gdti:([0-9]{6})\\.([0-9]{6})\\.([0-9]{1,13})$",

			"^urn:epc:id:gdti:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,17})$",
			"^urn:epc:id:gdti:([0-9]{11})\\.([0-9]{1})\\.([0-9]{1,17})$",
			"^urn:epc:id:gdti:([0-9]{10})\\.([0-9]{2})\\.([0-9]{1,17})$",
			"^urn:epc:id:gdti:([0-9]{9})\\.([0-9]{3})\\.([0-9]{1,17})$",
			"^urn:epc:id:gdti:([0-9]{8})\\.([0-9]{4})\\.([0-9]{1,17})$",
			"^urn:epc:id:gdti:([0-9]{7})\\.([0-9]{5})\\.([0-9]{1,17})$",
			"^urn:epc:id:gdti:([0-9]{6})\\.([0-9]{6})\\.([0-9]{1,17})$" };

	private static final String[] GIAIList = new String[] {
			"^urn:epc:id:giai:([0-9]{12})\\.([0-9]{1,12})$",
			"^urn:epc:id:giai:([0-9]{11})\\.([0-9]{1,12})$",
			"^urn:epc:id:giai:([0-9]{10})\\.([0-9]{1,12})$",
			"^urn:epc:id:giai:([0-9]{9})\\.([0-9]{1,12})$",
			"^urn:epc:id:giai:([0-9]{8})\\.([0-9]{1,12})$",
			"^urn:epc:id:giai:([0-9]{7})\\.([0-9]{1,12})$",
			"^urn:epc:id:giai:([0-9]{6})\\.([0-9]{1,12})$",

			"^urn:epc:id:giai:([0-9]{12})\\.([0-9]{1,13})$",
			"^urn:epc:id:giai:([0-9]{11})\\.([0-9]{1,14})$",
			"^urn:epc:id:giai:([0-9]{10})\\.([0-9]{1,15})$",
			"^urn:epc:id:giai:([0-9]{9})\\.([0-9]{1,16})$",
			"^urn:epc:id:giai:([0-9]{8})\\.([0-9]{1,17})$",
			"^urn:epc:id:giai:([0-9]{7})\\.([0-9]{1,18})$",
			"^urn:epc:id:giai:([0-9]{6})\\.([0-9]{1,19})$",

			"^urn:epc:id:giai:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})$",
			"^urn:epc:id:giai:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})$",
			"^urn:epc:id:giai:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:giai:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})$",
			"^urn:epc:id:giai:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})$",
			"^urn:epc:id:giai:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})$",
			"^urn:epc:id:giai:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})$" };

	private static final String GID = "^urn:epc:id:gid:([0-9]{1,9})\\.([0-9]{1,8})\\.([0-9]{1,11})$";

	private static final String[] GRAIList = new String[] {
			"^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,6})$",
			"^urn:epc:id:grai:([0-9]{11})\\.([0-9]{1})\\.([0-9]{1,6})$",
			"^urn:epc:id:grai:([0-9]{10})\\.([0-9]{2})\\.([0-9]{1,6})$",
			"^urn:epc:id:grai:([0-9]{9})\\.([0-9]{3})\\.([0-9]{1,6})$",
			"^urn:epc:id:grai:([0-9]{8})\\.([0-9]{4})\\.([0-9]{1,6})$",
			"^urn:epc:id:grai:([0-9]{7})\\.([0-9]{5})\\.([0-9]{1,6})$",
			"^urn:epc:id:grai:([0-9]{6})\\.([0-9]{6})\\.([0-9]{1,6})$",

			"^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,12})$",
			"^urn:epc:id:grai:([0-9]{11})\\.([0-9]{1})\\.([0-9]{1,12})$",
			"^urn:epc:id:grai:([0-9]{10})\\.([0-9]{2})\\.([0-9]{1,12})$",
			"^urn:epc:id:grai:([0-9]{9})\\.([0-9]{3})\\.([0-9]{1,12})$",
			"^urn:epc:id:grai:([0-9]{8})\\.([0-9]{4})\\.([0-9]{1,12})$",
			"^urn:epc:id:grai:([0-9]{7})\\.([0-9]{5})\\.([0-9]{1,12})$",
			"^urn:epc:id:grai:([0-9]{6})\\.([0-9]{6})\\.([0-9]{1,12})$",

			"^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,16})$" };

	private static final String[] GSRNList = new String[] {
			"^urn:epc:id:gsrn:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:gsrn:([0-9]{11})\\.([0-9]{6})$",
			"^urn:epc:id:gsrn:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:gsrn:([0-9]{9})\\.([0-9]{8})$",
			"^urn:epc:id:gsrn:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:gsrn:([0-9]{7})\\.([0-9]{10})$",
			"^urn:epc:id:gsrn:([0-9]{6})\\.([0-9]{11})$" };

	private static final String[] SGLNList = new String[] {
			"^urn:epc:id:sgln:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,6})$",
			"^urn:epc:id:sgln:([0-9]{11})\\.([0-9]{1})\\.([0-9]{1,6})$",
			"^urn:epc:id:sgln:([0-9]{10})\\.([0-9]{2})\\.([0-9]{1,6})$",
			"^urn:epc:id:sgln:([0-9]{9})\\.([0-9]{3})\\.([0-9]{1,6})$",
			"^urn:epc:id:sgln:([0-9]{8})\\.([0-9]{4})\\.([0-9]{1,6})$",
			"^urn:epc:id:sgln:([0-9]{7})\\.([0-9]{5})\\.([0-9]{1,6})$",
			"^urn:epc:id:sgln:([0-9]{6})\\.([0-9]{6})\\.([0-9]{1,6})$",

			"^urn:epc:id:sgln:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,13})$",
			"^urn:epc:id:sgln:([0-9]{11})\\.([0-9]{1})\\.([0-9]{1,13})$",
			"^urn:epc:id:sgln:([0-9]{10})\\.([0-9]{2})\\.([0-9]{1,13})$",
			"^urn:epc:id:sgln:([0-9]{9})\\.([0-9]{3})\\.([0-9]{1,13})$",
			"^urn:epc:id:sgln:([0-9]{8})\\.([0-9]{4})\\.([0-9]{1,13})$",
			"^urn:epc:id:sgln:([0-9]{7})\\.([0-9]{5})\\.([0-9]{1,13})$",
			"^urn:epc:id:sgln:([0-9]{6})\\.([0-9]{6})\\.([0-9]{1,13})$",

			"^urn:epc:id:sgln:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	private static final String[] SGTINList = new String[] {
			"^urn:epc:id:sgtin:([0-9]{12})\\.([0-9]{1})\\.([0-9]{1,8})$",
			"^urn:epc:id:sgtin:([0-9]{11})\\.([0-9]{2})\\.([0-9]{1,8})$",
			"^urn:epc:id:sgtin:([0-9]{10})\\.([0-9]{3})\\.([0-9]{1,8})$",
			"^urn:epc:id:sgtin:([0-9]{9})\\.([0-9]{4})\\.([0-9]{1,8})$",
			"^urn:epc:id:sgtin:([0-9]{8})\\.([0-9]{5})\\.([0-9]{1,8})$",
			"^urn:epc:id:sgtin:([0-9]{7})\\.([0-9]{6})\\.([0-9]{1,8})$",
			"^urn:epc:id:sgtin:([0-9]{6})\\.([0-9]{7})\\.([0-9]{1,8})$",

			"^urn:epc:id:sgtin:([0-9]{12})\\.([0-9]{1})\\.([0-9]{1,12})$",
			"^urn:epc:id:sgtin:([0-9]{11})\\.([0-9]{2})\\.([0-9]{1,12})$",
			"^urn:epc:id:sgtin:([0-9]{10})\\.([0-9]{3})\\.([0-9]{1,12})$",
			"^urn:epc:id:sgtin:([0-9]{9})\\.([0-9]{4})\\.([0-9]{1,12})$",
			"^urn:epc:id:sgtin:([0-9]{8})\\.([0-9]{5})\\.([0-9]{1,12})$",
			"^urn:epc:id:sgtin:([0-9]{7})\\.([0-9]{6})\\.([0-9]{1,12})$",
			"^urn:epc:id:sgtin:([0-9]{6})\\.([0-9]{7})\\.([0-9]{1,12})$",

			"^urn:epc:id:sgtin:([0-9]{12})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{11})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{10})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{9})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{8})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{7})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{6})\\.([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	private static final String[] SSCCList = new String[] {
			"^urn:epc:id:sscc:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:sscc:([0-9]{11})\\.([0-9]{6})$",
			"^urn:epc:id:sscc:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:sscc:([0-9]{9})\\.([0-9]{8})$",
			"^urn:epc:id:sscc:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:sscc:([0-9]{7})\\.([0-9]{10})$",
			"^urn:epc:id:sscc:([0-9]{6})\\.([0-9]{11})$",

			"^urn:epc:id:sscc:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:sscc:([0-9]{11})\\.([0-9]{6})$",
			"^urn:epc:id:sscc:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:sscc:([0-9]{9})\\.([0-9]{8})$",
			"^urn:epc:id:sscc:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:sscc:([0-9]{7})\\.([0-9]{10})$",
			"^urn:epc:id:sscc:([0-9]{6})\\.([0-9]{11})$" };

	private static final String[] USDODList = new String[] {
			"^urn:epc:id:usdod:([0-9A-HJ-NP-Z]{5})\\.([0-9]{1,8})$",

			"^urn:epc:id:usdod:([0-9A-HJ-NP-Z]{5,6})\\.([0-9]{1,11})$" };

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
