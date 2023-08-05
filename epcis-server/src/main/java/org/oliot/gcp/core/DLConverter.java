package org.oliot.gcp.core;

import java.util.HashMap;

import org.oliot.epcis.model.ValidationException;

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
public class DLConverter {

	public static String getDL(String epc) {
		// https://id.gs1.org/01/70614141123451/21/2017
		// urn:epc:id:sgtin:0614141.712345.2017
		// https://id.gs1.org/01/70614141123451/10/998877
		// urn:epc:class:lgtin:0614141.712345.998877
		// urn:epc:idpat:sgtin:4012345.066666.*

		if (epc.startsWith("urn:epc:id:sgtin:")) {
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			if (numArr.length != 3)
				return epc;
			String n01 = numArr[1].substring(0, 1) + numArr[0] + numArr[1].substring(1, numArr[1].length());
			n01 = appendGtinCheckDigit(n01);
			return "https://id.gs1.org/01/" + n01 + "/21/" + numArr[2];
		} else if (epc.startsWith("urn:epc:class:lgtin:")) {
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			if (numArr.length != 3)
				return epc;
			String n01 = numArr[1].substring(0, 1) + numArr[0] + numArr[1].substring(1, numArr[1].length());
			n01 = appendGtinCheckDigit(n01);
			return "https://id.gs1.org/01/" + n01 + "/10/" + numArr[2];
		} else if (epc.startsWith("urn:epc:idpat:sgtin:")) {
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			if (numArr.length != 3)
				return epc;
			String n01 = numArr[1].substring(0, 1) + numArr[0] + numArr[1].substring(1, numArr[1].length());
			n01 = appendGtinCheckDigit(n01);
			return "https://id.gs1.org/01/" + n01;
		}
		return epc;
	}
	
	public static String appendGtinCheckDigit(String gtin) {
		if (gtin.length() != 13) {
			return null;
		}
		int[] e = toIntArray(gtin);

		for (int i = 0; i < gtin.length(); i++) {
			e[i] = Integer.parseInt(gtin.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12]) + e[1] + e[3] + e[5] + e[7] + e[9] + e[11])
						% 10))
				% 10;
		
		return gtin + correctCheckDigit;
	}

	public static String getEPC(HashMap<String, Integer> gcpLengthList, String dl) throws ValidationException {
		String[] dlArray = dl.split("/");

		String s01 = null;
		String s10 = null;
		String s21 = null;

		for (int i = 0; i < dlArray.length - 1; i++) {
			if (dlArray[i].equals("01")) {
				s01 = dlArray[i + 1];
			} else if (dlArray[i].equals("10")) {
				s10 = dlArray[i + 1];
			} else if (dlArray[i].equals("21")) {
				s21 = dlArray[i + 1];
			}
		}

		if (s01 != null && s21 != null) {
			return generateSgtin(gcpLengthList, s01, s21);
		} else if (s01 != null && s10 != null) {
			return generateLgtin(gcpLengthList, s01, s10);
		} else if (s01 != null) {
			return generateGtin(gcpLengthList, s01);
		} else {
			throw new ValidationException("");
		}
	}

	public static String applySerialEncoding(String serial) {
		serial = serial.replaceAll("/", "%2F");
		return serial;
	}

	public static String generateSgtin(String gtin, String serial, int gcpLength) {
		// SGTIN
		// System.out.println("[System] SGTIN exists");

		// Validation Check
		if (!gtin.matches("([0-9]{14})")) {
			return null;
		}
		// Check digit validation
		if (!isGtinCheckDigitCorrect(gtin)) {
			return null;
		}

		if (!serial.matches("([!%-?A-Z_a-z\"]{1,20})")) {
			return null;
		}
		serial = applySerialEncoding(serial);

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.charAt(0) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:id:sgtin:" + gcp + "." + itemref + "." + serial;
	}

	public static String generateLgtin(String gtin, String lot, int gcpLength) {
		// LGTIN
		// System.out.println("[System] LGTIN exists");

		// Validation Check
		if (!gtin.matches("([0-9]{14})")) {
			return null;
		}
		// Check digit validation
		if (!isGtinCheckDigitCorrect(gtin)) {
			return null;
		}

		if (!lot.matches("([!%-?A-Z_a-z\"]{1,20})")) {
			return null;
		}
		lot = applySerialEncoding(lot);

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.charAt(0) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:class:lgtin:" + gcp + "." + itemref + "." + lot;
	}

	public static String generateGtin(String gtin, int gcpLength) {
		// GTIN
		// System.out.println("[System] SGTIN exists");

		// Validation Check
		if (!gtin.matches("([0-9]{14})")) {
			return null;
		}
		// Check digit validation
		if (!isGtinCheckDigitCorrect(gtin)) {
			return null;
		}

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.charAt(0) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:idpat:sgtin:" + gcp + "." + itemref + ".*";
	}
	
	public static String generateSscc(String sscc, int gcpLength) {
		// GTIN
		// System.out.println("[System] SGTIN exists");

		// Validation Check
		if (!sscc.matches("([0-9]{18})")) {
			return null;
		}
		// Check digit validation
		if (!isSsccCheckDigitCorrect(sscc)) {
			return null;
		}

		String gcp = sscc.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String serialref = sscc.substring(gcpLength + 1, sscc.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		serialref = sscc.charAt(0) + serialref;
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:id:sscc:" + gcp + "." + serialref;
	}

	public static int getGCPLength(HashMap<String, Integer> gcpLengthList, String code) throws ValidationException {
		Integer gcpLength = null;
		String _01back = code;
		while (!_01back.equals("")) {
			if (_01back.length() == 0)
				throw new ValidationException("");

			gcpLength = gcpLengthList.get(_01back);

			if (gcpLength != null)
				break;

			_01back = _01back.substring(0, _01back.length() - 1);
		}
		return gcpLength;
	}

	public static String generateGtin(HashMap<String, Integer> gcpLengthList, String gtin) throws ValidationException {
		// GTIN
		// System.out.println("[System] SGTIN exists");

		// Validation Check
		if (!gtin.matches("([0-9]{14})")) {
			return null;
		}
		// Check digit validation
		if (!isGtinCheckDigitCorrect(gtin)) {
			return null;
		}
		// Get GCP Length
		int gcpLength = getGCPLength(gcpLengthList, gtin.substring(1));

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.charAt(0) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:idpat:sgtin:" + gcp + "." + itemref + ".*";
	}

	public static String generateLgtin(HashMap<String, Integer> gcpLengthList, String gtin, String lot)
			throws ValidationException {
		// LGTIN
		// System.out.println("[System] LGTIN exists");

		// Validation Check
		if (!gtin.matches("([0-9]{14})")) {
			return null;
		}
		// Check digit validation
		if (!isGtinCheckDigitCorrect(gtin)) {
			return null;
		}

		if (!lot.matches("([!%-?A-Z_a-z\"]{1,20})")) {
			return null;
		}
		lot = applySerialEncoding(lot);
		// Get GCP Length
		int gcpLength = getGCPLength(gcpLengthList, gtin.substring(1));

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.charAt(0) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:class:lgtin:" + gcp + "." + itemref + "." + lot;
	}

	public static String generateSgtin(HashMap<String, Integer> gcpLengthList, String gtin, String serial)
			throws ValidationException {
		// SGTIN
		// System.out.println("[System] SGTIN exists");

		// Validation Check
		if (!gtin.matches("([0-9]{14})")) {
			return null;
		}
		// Check digit validation
		if (!isGtinCheckDigitCorrect(gtin)) {
			return null;
		}

		if (!serial.matches("([!%-?A-Z_a-z\"]{1,20})")) {
			return null;
		}
		serial = applySerialEncoding(serial);

		// Get GCP Length
		int gcpLength = getGCPLength(gcpLengthList, gtin.substring(1));

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.charAt(0) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:id:sgtin:" + gcp + "." + itemref + "." + serial;
	}

	public static String generateSGLN(HashMap<String, Integer> gcpLengthList, String gln, String ext)
			throws ValidationException {
		// LGTIN
		// System.out.println("[System] LGTIN exists");

		// Validation Check
		if (!gln.matches("([0-9]{13})")) {
			throw new ValidationException("");
		}
		// TODO: Check digit validation
		// if (!isGtinCheckDigitCorrect(gtin)) {
		// return null;
		// }

		if (ext != null) {
			if (!ext.matches("([!%-?A-Z_a-z\"]{1,20})")) {
				throw new ValidationException("");
			}
			ext = applySerialEncoding(ext);
		}

		// Get GCP Length
		int gcpLength = getGCPLength(gcpLengthList, gln);

		String gcp = gln.substring(0, gcpLength);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gln.substring(gcpLength, gln.length() - 1);
		// System.out.println("[System] Itemref: " + itemref);

		if (ext == null)
			return "urn:epc:id:sgln:" + gcp + "." + itemref + ".0";
		else
			return "urn:epc:id:sgln:" + gcp + "." + itemref + "." + ext;
	}

	public static String generatePGLN(HashMap<String, Integer> gcpLengthList, String gln) throws ValidationException {
		// LGTIN
		// System.out.println("[System] LGTIN exists");

		// Validation Check
		if (!gln.matches("([0-9]{13})")) {
			throw new ValidationException("");
		}
		// TODO: Check digit validation
		// if (!isGtinCheckDigitCorrect(gtin)) {
		// return null;
		// }

		// Get GCP Length
		int gcpLength = getGCPLength(gcpLengthList, gln);

		String gcp = gln.substring(0, gcpLength);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gln.substring(gcpLength, gln.length() - 1);
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:id:pgln:" + gcp + "." + itemref;

	}

	public static String generateGDTI(HashMap<String, Integer> gcpLengthList, String gdti) throws ValidationException {
		// LGTIN
		// System.out.println("[System] LGTIN exists");

		// Validation Check
		if (!gdti.matches("([0-9]{13}[!%-?A-Z_a-z\\\"]{1,17})")) {
			throw new ValidationException("");
		}
		// TODO: Check digit validation
		// if (!isGtinCheckDigitCorrect(gtin)) {
		// return null;
		// }

		String gdtiClass = gdti.substring(0, 12);
		String gdtiSerial = gdti.substring(13, gdti.length());

		// Get GCP Length
		int gcpLength = getGCPLength(gcpLengthList, gdtiClass);

		String gcp = gdtiClass.substring(0, gcpLength);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gdtiClass.substring(gcpLength, gdtiClass.length());
		// System.out.println("[System] Itemref: " + itemref);

		return "urn:epc:id:gdti:" + gcp + "." + itemref + "." + gdtiSerial;
	}

	public static int[] toIntArray(String str) {
		int[] e = new int[str.length()];

		for (int i = 0; i < str.length(); i++) {
			e[i] = Integer.parseInt(str.charAt(i) + "");
		}
		return e;
	}

	public static boolean isGtinCheckDigitCorrect(String gtin) {
		if (gtin.length() != 14) {
			return false;
		}
		int[] e = toIntArray(gtin);

		for (int i = 0; i < gtin.length(); i++) {
			e[i] = Integer.parseInt(gtin.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12]) + e[1] + e[3] + e[5] + e[7] + e[9] + e[11])
						% 10))
				% 10;
		if (!(e[13] == correctCheckDigit)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}
	
	public static boolean isSsccCheckDigitCorrect(String sscc) {
		if (sscc.length() != 18) {
			return false;
		}
		int[] e = toIntArray(sscc);

		for (int i = 0; i < sscc.length(); i++) {
			e[i] = Integer.parseInt(sscc.charAt(i) + "");
		}

		int correctCheckDigit = (10 - ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12] + e[14] + e[16]) + e[1]
				+ e[3] + e[5] + e[7] + e[9] + e[11] + e[13] + e[15]) % 10)) % 10;
		
		if (!(e[17] == correctCheckDigit)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}
}