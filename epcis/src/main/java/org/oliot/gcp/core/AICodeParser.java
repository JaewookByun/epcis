package org.oliot.gcp.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Copyright (C) 2015 Jaewook Byun
 *
 * @author Jaewook Byun, Ph.D student Korea Advanced Institute of Science and
 *         Technology (KAIST) Real-time Embedded System Laboratory(RESL)
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class AICodeParser {

	private HashMap<String, String> applicationIdentifierMap;
	private HashMap<String, String> collection;

	/**
	 * 
	 * @param code
	 *            GS1 AI and Element String contains unknown checksum as wild
	 *            character (*)
	 * @return e.g. (01)8061414112345* --> (01)80614141123458
	 */
	public String fillChecksum(String code, int gcpLength) {
		// Null Check
		if (code == null || gcpLength <= 0) {
			return null;
		}
		// Keep code as HashMap
		applicationIdentifierMap = parse(code);
		// Initialize identifiedEPCMap
		collection = new HashMap<String, String>();

		// 00 formulate SSCC
		if (applicationIdentifierMap.containsKey("00")) {
			String sscc = applicationIdentifierMap.get("00");
			sscc = sscc.replaceAll("\\s", "");
			for(int i = 0 ; i < 10 ; i++){
				String temp = sscc.replace('*', String.valueOf(i).toCharArray()[0]);
				if( isSsccCheckDigitCorrect(temp)){
					return "(00)"+temp;
				}
			}
		}

		// 01 formulate GTIN
		if (applicationIdentifierMap.containsKey("01")) {
			String gtin = applicationIdentifierMap.get("01");
			gtin = gtin.replaceAll("\\s", "");
			for(int i = 0 ; i < 10 ; i++){
				String temp = gtin.replace('*', String.valueOf(i).toCharArray()[0]);
				if( isGtinCheckDigitCorrect(temp)){
					return "(01)"+temp;
				}
			}
		}

		// 253 formulate GDTI
		if (applicationIdentifierMap.containsKey("253")) {
			String gdti = applicationIdentifierMap.get("253");
			gdti = gdti.replaceAll("\\s", "");
			for(int i = 0 ; i < 10 ; i++){
				String temp = gdti.replace('*', String.valueOf(i).toCharArray()[0]);
				if( isGdtiCheckDigitCorrect(temp)){
					return "(253)"+temp;
				}
			}
		}

		// 255 formulate SGCN
		if (applicationIdentifierMap.containsKey("255")) {
			String sgcn = applicationIdentifierMap.get("255");
			sgcn = sgcn.replaceAll("\\s", "");
			for(int i = 0 ; i < 10 ; i++){
				String temp = sgcn.replace('*', String.valueOf(i).toCharArray()[0]);
				if( isSgcnCheckDigitCorrect(temp)){
					return "(255)"+temp;
				}
			}
		}

		// 414 ( SGLN without extension )
		if (applicationIdentifierMap.containsKey("414")) {
			String sgln = applicationIdentifierMap.get("414");
			sgln = sgln.replaceAll("\\s", "");
			for(int i = 0 ; i < 10 ; i++){
				String temp = sgln.replace('*', String.valueOf(i).toCharArray()[0]);
				if( isGlnCheckDigitCorrect(temp)){
					return "(414)"+temp;
				}
			}
		}

		// 8003 formulate GRAI
		if (applicationIdentifierMap.containsKey("8003")) {
			String grai = applicationIdentifierMap.get("8003");
			grai = grai.replaceAll("\\s", "");
			for(int i = 0 ; i < 10 ; i++){
				String temp = grai.replace('*', String.valueOf(i).toCharArray()[0]);
				if( isGraiCheckDigitCorrect(temp)){
					return "(8003)"+temp;
				}
			}
		}

		// 8017 formulate GSRNP
		if (applicationIdentifierMap.containsKey("8017")) {
			String gsrnp = applicationIdentifierMap.get("8017");
			gsrnp = gsrnp.replaceAll("\\s", "");
			for(int i = 0 ; i < 10 ; i++){
				String temp = gsrnp.replace('*', String.valueOf(i).toCharArray()[0]);
				if( isGsrnCheckDigitCorrect(temp)){
					return "(8017)"+temp;
				}
			}
		}

		// 8018 formulate GSRN
		if (applicationIdentifierMap.containsKey("8018")) {
			String gsrn = applicationIdentifierMap.get("8018");
			gsrn = gsrn.replaceAll("\\s", "");
			for(int i = 0 ; i < 10 ; i++){
				String temp = gsrn.replace('*', String.valueOf(i).toCharArray()[0]);
				if( isGsrnCheckDigitCorrect(temp)){
					return "(8018)"+temp;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param code
	 *            Sequence of ((GS1 Application Identifier) Element Strings)+
	 *            e.g.
	 *            (01)00037000302414(21)10419703(414)0003700030241(254)1041970
	 *            Support: SGTIN-198 Support:
	 * @param gcpLength
	 *            a length of gs1 global company prefix
	 * @param output
	 *            Key-value pairs of elements in code
	 * @return
	 */
	public HashMap<String, String> parse(String code, int gcpLength) {

		// Null Check
		if (code == null || gcpLength <= 0) {
			return null;
		}
		// Keep code as HashMap
		applicationIdentifierMap = parse(code);
		// Initialize identifiedEPCMap
		collection = new HashMap<String, String>();

		// 00 formulate SSCC
		if (applicationIdentifierMap.containsKey("00")) {
			String sscc = generateSscc(gcpLength);
			if (sscc != null) {
				collection.put("sscc", sscc);
			}
		}

		// 01 formulate GTIN
		if (applicationIdentifierMap.containsKey("01")) {
			String gtin = generateGtin(gcpLength);
			if (gtin != null) {
				collection.put("gtin", gtin);
			}
		}

		// 01 & 21 formulate SGTIN
		if (applicationIdentifierMap.containsKey("01") && applicationIdentifierMap.containsKey("21")) {
			String sgtin = generateSgtin(gcpLength);
			if (sgtin != null) {
				collection.put("sgtin", sgtin);
			}
		}

		// 01 & 10 formulate LGTIN
		if (applicationIdentifierMap.containsKey("01") && applicationIdentifierMap.containsKey("10")) {
			String lgtin = generateLgtin(gcpLength);
			if (lgtin != null) {
				collection.put("lgtin", lgtin);
			}
		}

		// 253 formulate GDTI
		if (applicationIdentifierMap.containsKey("253")) {
			String gdti = generateGdti(gcpLength);
			if (gdti != null) {
				collection.put("gdti", gdti);
			}
		}

		// 255 formulate SGCN
		if (applicationIdentifierMap.containsKey("255")) {
			String sgcn = generateSgcn(gcpLength);
			if (sgcn != null) {
				collection.put("sgcn", sgcn);
			}
		}

		// 414 ( SGLN without extension ) 414 & 254 ( SGLN with extension )
		if (applicationIdentifierMap.containsKey("414")) {
			String sgln = generateSgln(gcpLength);
			if (sgln != null) {
				collection.put("sgln", sgln);
			}
		}

		// 8003 formulate GRAI
		if (applicationIdentifierMap.containsKey("8003")) {
			String grai = generateGrai(gcpLength);
			if (grai != null) {
				collection.put("grai", grai);
			}
		}

		// 8004 formulate GIAI
		if (applicationIdentifierMap.containsKey("8004")) {
			String giai = generateGiai(gcpLength);
			if (giai != null) {
				collection.put("giai", giai);
			}
		}

		// 8010 and 8011 formulate CPI
		if (applicationIdentifierMap.containsKey("8010") && applicationIdentifierMap.containsKey("8011")) {
			String cpi = generateCpi(gcpLength);
			if (cpi != null) {
				collection.put("cpi", cpi);
			}
		}

		// 8017 formulate GSRNP
		if (applicationIdentifierMap.containsKey("8017")) {
			String gsrnp = generateGsrnp(gcpLength);
			if (gsrnp != null) {
				collection.put("gsrnp", gsrnp);
			}
		}

		// 8018 formulate GSRN
		if (applicationIdentifierMap.containsKey("8018")) {
			String gsrn = generateGsrn(gcpLength);
			if (gsrn != null) {
				collection.put("gsrn", gsrn);
			}
		}

		// 11 / 13 / 30 / 310n / 390n
		generateOtherInformation();
		return collection;
	}

	private String generateSscc(int gcpLength) {
		// SSCC
		// System.out.println("[System] SSCC exists");

		// Validation Check
		String sscc = applicationIdentifierMap.get("00");
		if (sscc.matches("([0-9]{18})") == false) {
			return null;
		}
		// Check digit validation
		if (isSsccCheckDigitCorrect(sscc) == false) {
			return null;
		}

		String gcp = sscc.substring(1, gcpLength + 1);
		// System.out.println("[System] SSCC: " + gcp);
		String serialref = sscc.substring(gcpLength + 1, sscc.length() - 1);
		// System.out.println("[System] Serialref Suffix: " + serialref);
		serialref = sscc.substring(0, 1) + serialref;
		// System.out.println("[System] Serialref: " + serialref);

		String ssccEPC = "urn:epc:id:sscc:" + gcp + "." + serialref;
		// System.out.println("[System] SSCC: " + ssccEPC);
		return ssccEPC;
	}

	private String generateGtin(int gcpLength) {
		// GTIN
		// System.out.println("[System] SGTIN exists");

		// Validation Check
		String gtin = applicationIdentifierMap.get("01");
		if (gtin.matches("([0-9]{14})") == false) {
			return null;
		}
		// Check digit validation
		if (isGtinCheckDigitCorrect(gtin) == false) {
			return null;
		}

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.substring(0, 1) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		String gtinEPC = "urn:epc:idpat:sgtin:" + gcp + "." + itemref + ".*";
		// System.out.println("[System] GTIN: " + gtin);
		return gtinEPC;
	}

	private String generateSgtin(int gcpLength) {
		// SGTIN
		// System.out.println("[System] SGTIN exists");

		// Validation Check
		String gtin = applicationIdentifierMap.get("01");
		if (gtin.matches("([0-9]{14})") == false) {
			return null;
		}
		// Check digit validation
		if (isGtinCheckDigitCorrect(gtin) == false) {
			return null;
		}

		String serial = applicationIdentifierMap.get("21");
		if (serial.matches("([!%-?A-Z_a-z\"]{1,20})") == false) {
			return null;
		}
		serial = applySerialEncoding(serial);

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.substring(0, 1) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		String sgtin = "urn:epc:id:sgtin:" + gcp + "." + itemref + "." + serial;
		// System.out.println("[System] SGTIN: " + sgtin);
		return sgtin;
	}

	private String generateLgtin(int gcpLength) {
		// LGTIN
		// System.out.println("[System] LGTIN exists");

		// Validation Check
		String gtin = applicationIdentifierMap.get("01");
		if (gtin.matches("([0-9]{14})") == false) {
			return null;
		}
		// Check digit validation
		if (isGtinCheckDigitCorrect(gtin) == false) {
			return null;
		}

		String lot = applicationIdentifierMap.get("10");
		if (lot.matches("([!%-?A-Z_a-z\"]{1,20})") == false) {
			return null;
		}
		lot = applySerialEncoding(lot);

		String gcp = gtin.substring(1, gcpLength + 1);
		// System.out.println("[System] GTIN: " + gcp);
		String itemref = gtin.substring(gcpLength + 1, gtin.length() - 1);
		// System.out.println("[System] Itemref Suffix: " + itemref);
		itemref = gtin.substring(0, 1) + itemref;
		// System.out.println("[System] Itemref: " + itemref);

		String lgtin = "urn:epc:class:lgtin:" + gcp + "." + itemref + "." + lot;
		// System.out.println("[System] LGTIN: " + lgtin);
		return lgtin;
	}

	private String generateGdti(int gcpLength) {
		// GDTI
		// System.out.println("[System] GDTI exists");

		// Validation Check
		String gdti = applicationIdentifierMap.get("253");
		if (gdti.matches("([0-9]{13})([0-9]{1,17})") == false) {
			return null;
		}
		// Check digit validation
		if (isGdtiCheckDigitCorrect(gdti) == false) {
			return null;
		}

		String gcp = gdti.substring(0, gcpLength);
		// System.out.println("[System] GCP: " + gcp);
		String docType = gdti.substring(gcpLength, 12);
		// System.out.println("[System] Document Type: " + docType);
		String serial = gdti.substring(13, gdti.length());
		// System.out.println("[System] Serial: " + serial);

		String gdtiEPC = "urn:epc:id:gdti:" + gcp + "." + docType + "." + serial;
		// System.out.println("[System] GDTI: " + gdtiEPC);
		return gdtiEPC;
	}

	private String generateSgcn(int gcpLength) {
		// SGCN
		// System.out.println("[System] SGCN exists");

		// Validation Check
		String sgcn = applicationIdentifierMap.get("255");
		if (sgcn.matches("([0-9]{13})([0-9]{1,12})") == false) {
			return null;
		}
		// Check digit validation
		if (isSgcnCheckDigitCorrect(sgcn) == false) {
			return null;
		}

		String gcp = sgcn.substring(0, gcpLength);
		// System.out.println("[System] GCP: " + gcp);
		String couponRef = sgcn.substring(gcpLength, 12);
		// System.out.println("[System] Coupon Reference: " + couponRef);
		String serial = sgcn.substring(13, sgcn.length());
		// System.out.println("[System] Serial: " + serial);

		String sgcnEPC = "urn:epc:id:sgcn:" + gcp + "." + couponRef + "." + serial;
		// System.out.println("[System] SGCN: " + sgcnEPC);
		return sgcnEPC;
	}

	private String generateSgln(int gcpLength) {
		// SGLN
		// System.out.println("[System] SGLN exists");

		// Validation Check
		String gln = applicationIdentifierMap.get("414");
		if (gln.matches("([0-9]{13})") == false) {
			return null;
		}

		// Check digit validation
		if (isGlnCheckDigitCorrect(gln) == false) {
			return null;
		}

		String serial = applicationIdentifierMap.get("254");
		if (serial == null) {
			serial = "0";
		}

		String gcp = gln.substring(0, gcpLength);
		// System.out.println("[System] GTIN: " + gcp);
		String locref = gln.substring(gcpLength, gln.length() - 1);
		// System.out.println("[System] Locref: " + locref);

		String sgln = "urn:epc:id:sgln:" + gcp + "." + locref + "." + serial;
		// System.out.println("[System] SGLN: " + sgln);
		return sgln;
	}

	private String generateGrai(int gcpLength) {
		// GRAI
		// System.out.println("[System] GRAI exists");

		// Validation Check
		String grai = applicationIdentifierMap.get("8003");
		if (grai.matches("0([0-9]{13})([!%-?A-Z_a-z\"]{1,16})") == false) {
			return null;
		}
		// Check digit validation
		if (isGraiCheckDigitCorrect(grai) == false) {
			return null;
		}

		String gcp = grai.substring(1, gcpLength + 1);
		// System.out.println("[System] GCP: " + gcp);
		String assetType = grai.substring(gcpLength + 1, 13);
		// System.out.println("[System] Asset Type: " + assetType);
		String serial = grai.substring(14, grai.length());
		serial = applySerialEncoding(serial);
		// System.out.println("[System] Serial: " + serial);

		String graiEPC = "urn:epc:id:grai:" + gcp + "." + assetType + "." + serial;
		// System.out.println("[System] GRAI: " + graiEPC);
		return graiEPC;
	}

	private String generateGiai(int gcpLength) {
		// GIAI
		// System.out.println("[System] GIAI exists");

		// Validation Check
		String giai = applicationIdentifierMap.get("8004");
		String gcp;
		String assetReference;
		if (gcpLength == 6 && giai.matches("([0-9]{6})([!%-?A-Z_a-z\"]{1,24})")) {
			gcp = giai.substring(0, 6);
			assetReference = giai.substring(6, giai.length());
		} else if (gcpLength == 7 && giai.matches("([0-9]{7})([!%-?A-Z_a-z\"]{1,23})")) {
			gcp = giai.substring(0, 7);
			assetReference = giai.substring(7, giai.length());
		} else if (gcpLength == 8 && giai.matches("([0-9]{8})([!%-?A-Z_a-z\"]{1,22})")) {
			gcp = giai.substring(0, 8);
			assetReference = giai.substring(8, giai.length());
		} else if (gcpLength == 9 && giai.matches("([0-9]{9})([!%-?A-Z_a-z\"]{1,21})")) {
			gcp = giai.substring(0, 9);
			assetReference = giai.substring(9, giai.length());
		} else if (gcpLength == 10 && giai.matches("([0-9]{10})([!%-?A-Z_a-z\"]{1,20})")) {
			gcp = giai.substring(0, 10);
			assetReference = giai.substring(10, giai.length());
		} else if (gcpLength == 11 && giai.matches("([0-9]{11})([!%-?A-Z_a-z\"]{1,19})")) {
			gcp = giai.substring(0, 11);
			assetReference = giai.substring(11, giai.length());
		} else if (gcpLength == 12 && giai.matches("([0-9]{12})([!%-?A-Z_a-z\"]{1,18})")) {
			gcp = giai.substring(0, 12);
			assetReference = giai.substring(12, giai.length());
		} else {
			return null;
		}
		assetReference = applySerialEncoding(assetReference);

		// System.out.println("[System] GCP: " + gcp);
		// System.out.println("[System] assetReference: " + assetReference);
		String giaiEPC = "urn:epc:id:giai:" + gcp + "." + assetReference;
		// System.out.println("[System] GIAI: " + giaiEPC);
		return giaiEPC;
	}

	private String generateGsrnp(int gcpLength) {
		// GSRNP
		// System.out.println("[System] GSRNP exists");

		// Validation Check
		String gsrnp = applicationIdentifierMap.get("8017");
		if (gsrnp.matches("([0-9]{18})") == false) {
			return null;
		}
		// Check digit validation, GSRN and GSRNP shares common check digit
		// logic
		if (isGsrnCheckDigitCorrect(gsrnp) == false) {
			return null;
		}

		String gcp = gsrnp.substring(0, gcpLength);
		// System.out.println("[System] GSRNP: " + gcp);
		String serviceref = gsrnp.substring(gcpLength, gsrnp.length() - 1);
		// System.out.println("[System] Serviceref Suffix: " + serviceref);

		String gsrnpEPC = "urn:epc:id:gsrnp:" + gcp + "." + serviceref;
		// System.out.println("[System] GSRN: " + gsrnEPC);
		return gsrnpEPC;
	}

	private String generateGsrn(int gcpLength) {
		// GSRN
		// System.out.println("[System] GSRN exists");

		// Validation Check
		String gsrn = applicationIdentifierMap.get("8018");
		if (gsrn.matches("([0-9]{18})") == false) {
			return null;
		}
		// Check digit validation
		if (isGsrnCheckDigitCorrect(gsrn) == false) {
			return null;
		}

		String gcp = gsrn.substring(0, gcpLength);
		// System.out.println("[System] GSRN: " + gcp);
		String serviceref = gsrn.substring(gcpLength, gsrn.length() - 1);
		// System.out.println("[System] Serviceref Suffix: " + serviceref);

		String gsrnEPC = "urn:epc:id:gsrn:" + gcp + "." + serviceref;
		// System.out.println("[System] GSRN: " + gsrnEPC);
		return gsrnEPC;
	}

	private String generateCpi(int gcpLength) {
		// CPI
		// System.out.println("[System] CPI exists");

		// Validation Check
		String cp = applicationIdentifierMap.get("8010");
		String cpserial = applicationIdentifierMap.get("8011");

		String gcp;
		String cpref;
		if (gcpLength == 6 && cp.matches("([0-9]{6})([!%-?A-Z_a-z\"]+)")) {
			gcp = cp.substring(0, 6);
			cpref = cp.substring(6, cp.length());
		} else if (gcpLength == 7 && cp.matches("([0-9]{7})([!%-?A-Z_a-z\"]+)")) {
			gcp = cp.substring(0, 7);
			cpref = cp.substring(7, cp.length());
		} else if (gcpLength == 8 && cp.matches("([0-9]{8})([!%-?A-Z_a-z\"]+)")) {
			gcp = cp.substring(0, 8);
			cpref = cp.substring(8, cp.length());
		} else if (gcpLength == 9 && cp.matches("([0-9]{9})([!%-?A-Z_a-z\"]+)")) {
			gcp = cp.substring(0, 9);
			cpref = cp.substring(9, cp.length());
		} else if (gcpLength == 10 && cp.matches("([0-9]{10})([!%-?A-Z_a-z\"]+)")) {
			gcp = cp.substring(0, 10);
			cpref = cp.substring(10, cp.length());
		} else if (gcpLength == 11 && cp.matches("([0-9]{11})([!%-?A-Z_a-z\"]+)")) {
			gcp = cp.substring(0, 11);
			cpref = cp.substring(11, cp.length());
		} else if (gcpLength == 12 && cp.matches("([0-9]{12})([!%-?A-Z_a-z\"]+)")) {
			gcp = cp.substring(0, 12);
			cpref = cp.substring(12, cp.length());
		} else {
			return null;
		}
		cpref = applySerialEncoding(cpref);
		// System.out.println("[System] GCP: " + gcp);
		// System.out.println("[System] CP Reference: " + cpref);
		// System.out.println("[System] CP Serial: " + cpserial);
		String cpiEPC = "urn:epc:id:cpi:" + gcp + "." + cpref + "." + cpserial;
		// System.out.println("[System] CPI: " + cpiEPC);
		return cpiEPC;
	}

	private int generateOtherInformation() {
		int cnt = 0;
		// 11 - Production Date - YYMMDD
		if (applicationIdentifierMap.containsKey("11")) {
			SimpleDateFormat sdf = new SimpleDateFormat("YYMMDD");
			String productionDate = applicationIdentifierMap.get("11");
			try {
				sdf.parse(productionDate);
				collection.put("urn:epc:id:ai:11", productionDate);
				// System.out.println("[System] " + "urn:epc:id:ai:11" +
				// productionDate);
				cnt++;
			} catch (ParseException e) {
				System.out.println("[System] Invalid Date Format : YYMMDD");
			}

		}

		// 13 - Packaging Date - YYMMDD
		if (applicationIdentifierMap.containsKey("13")) {
			SimpleDateFormat sdf = new SimpleDateFormat("YYMMDD");
			String packagingDate = applicationIdentifierMap.get("13");
			try {
				sdf.parse(packagingDate);
				collection.put("urn:epc:id:ai:13", packagingDate);
				// System.out.println("[System] " + "urn:epc:id:ai:13" +
				// packagingDate);
				cnt++;
			} catch (ParseException e) {
				System.out.println("[System] Invalid Date Format : YYMMDD");
			}

		}

		// 30 - Count of Item - up to 8 digit
		if (applicationIdentifierMap.containsKey("30")) {

			String itemCount = applicationIdentifierMap.get("30");
			if (itemCount.length() <= 8) {
				try {
					int count = Integer.parseInt(itemCount);
					collection.put("urn:epc:id:ai:30", String.valueOf(count));
					// System.out.println("[System] " + "urn:epc:id:ai:30" +
					// count);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid Count of Item : 0~99999999");
				}
			}
		}

		// 3103 - Kilograms weight - 6digit
		if (applicationIdentifierMap.containsKey("3100")) {

			String kiloWeight = applicationIdentifierMap.get("3100");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// weight);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3101")) {

			String kiloWeight = applicationIdentifierMap.get("3101");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 10));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 10));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3102")) {

			String kiloWeight = applicationIdentifierMap.get("3102");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 100));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 100));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3103")) {

			String kiloWeight = applicationIdentifierMap.get("3103");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 1000));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 1000));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3103")) {

			String kiloWeight = applicationIdentifierMap.get("3103");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 1000));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 1000));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3104")) {

			String kiloWeight = applicationIdentifierMap.get("3104");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 10000));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 10000));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3105")) {

			String kiloWeight = applicationIdentifierMap.get("3105");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 100000));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 100000));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3106")) {

			String kiloWeight = applicationIdentifierMap.get("3106");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 1000000));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 1000000));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3107")) {

			String kiloWeight = applicationIdentifierMap.get("3107");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 10000000));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 10000000));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3108")) {

			String kiloWeight = applicationIdentifierMap.get("3108");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 100000000));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 100000000));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3109")) {

			String kiloWeight = applicationIdentifierMap.get("3109");
			if (kiloWeight.length() == 6) {
				try {
					float weight = Float.parseFloat(kiloWeight);
					collection.put("urn:epc:id:ai:310n", String.valueOf(weight / 1000000000));
					// System.out.println("[System] " + "urn:epc:id:ai:310n " +
					// String.valueOf(weight / 1000000000));
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid kilograms of weight of 6 digits");
				}
			}
		}
		// 393n - Price
		// - 3digit to iso currency code , e.g. KRW - 410
		// - up to 15digit for price,
		// - n <- start point to floating number, e.g. 3932 | 15000 -> 150.00
		if (applicationIdentifierMap.containsKey("3930")) {

			String isoPrice = applicationIdentifierMap.get("3930");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3931")) {

			String isoPrice = applicationIdentifierMap.get("3931");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 10));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 10);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3932")) {

			String isoPrice = applicationIdentifierMap.get("3932");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 100));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 100);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3933")) {

			String isoPrice = applicationIdentifierMap.get("3933");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 1000));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 1000);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3934")) {

			String isoPrice = applicationIdentifierMap.get("3934");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 10000));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 10000);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3935")) {

			String isoPrice = applicationIdentifierMap.get("3935");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 100000));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 100000);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3936")) {

			String isoPrice = applicationIdentifierMap.get("3936");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 1000000));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 1000000);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3937")) {

			String isoPrice = applicationIdentifierMap.get("3937");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 10000000));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 10000000);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3938")) {

			String isoPrice = applicationIdentifierMap.get("3938");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 100000000));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 100000000);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}
		if (applicationIdentifierMap.containsKey("3939")) {

			String isoPrice = applicationIdentifierMap.get("3939");
			String currencyCode = isoPrice.substring(0, 3);
			String price = isoPrice.substring(3, isoPrice.length());
			if (price.length() <= 15) {
				try {
					double priceDouble = Double.parseDouble(price);
					collection.put("urn:epc:id:ai:393n", currencyCode + "|" + String.valueOf(priceDouble / 1000000000));
					// System.out.println("[System] " + "urn:epc:id:ai:393n:" +
					// currencyCode + " " + priceDouble / 1000000000);
					cnt++;
				} catch (NumberFormatException e) {
					System.out.println("[System] Invalid pounds of price of 15 digits");
				}
			}
		}

		return cnt;
	}

	private HashMap<String, String> parse(String code) {
		HashMap<String, String> applicationIdentifierMap = new HashMap<String, String>();

		String[] codeFragments = code.split("\\(");
		for (int i = 0; i < codeFragments.length; i++) {
			String codeFragment = codeFragments[i];
			if (codeFragment == null) {
				continue;
			}
			String[] codeFragment2 = codeFragment.split("\\)");
			if (codeFragment2.length != 2) {
				continue;
			}
			String applicationIdentifier = codeFragment2[0].replaceAll("\\s", "");
			String elementString = codeFragment2[1].replaceAll("\\s", "");

			try {
				@SuppressWarnings("unused")
				int aiCheck = Integer.parseInt(applicationIdentifier);
			} catch (NumberFormatException e) {
				continue;
			}
			applicationIdentifierMap.put(applicationIdentifier, elementString);

			// System.out.println("[System] AI: " + applicationIdentifier + ",
			// ES: " + elementString);
		}
		return applicationIdentifierMap;
	}

	private boolean isSsccCheckDigitCorrect(String sscc) {
		if (sscc.length() != 18) {
			return false;
		}
		int[] e = toIntArray(sscc);

		for (int i = 0; i < sscc.length(); i++) {
			e[i] = Integer.parseInt(sscc.charAt(i) + "");
		}

		if (!(e[17] == (10 - ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12] + e[14] + e[16]) + e[1] + e[3]
				+ e[5] + e[7] + e[9] + e[11] + e[13] + e[15]) % 10)) % 10)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}

	private boolean isGtinCheckDigitCorrect(String gtin) {
		if (gtin.length() != 14) {
			return false;
		}
		int[] e = toIntArray(gtin);

		for (int i = 0; i < gtin.length(); i++) {
			e[i] = Integer.parseInt(gtin.charAt(i) + "");
		}

		if (!(e[13] == (10
				- ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12]) + e[1] + e[3] + e[5] + e[7] + e[9] + e[11])
						% 10))
				% 10)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}

	private boolean isGdtiCheckDigitCorrect(String gdti) {
		if (gdti.length() < 14) {
			return false;
		}

		String exceptSerial = gdti.substring(0, 13);

		int[] e = toIntArray(exceptSerial);

		for (int i = 0; i < exceptSerial.length(); i++) {
			e[i] = Integer.parseInt(exceptSerial.charAt(i) + "");
		}

		if (!(e[12] == (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}

	private boolean isSgcnCheckDigitCorrect(String sgcn) {
		// Length already checked
		String exceptSerial = sgcn.substring(0, 13);

		int[] e = toIntArray(exceptSerial);

		for (int i = 0; i < exceptSerial.length(); i++) {
			e[i] = Integer.parseInt(exceptSerial.charAt(i) + "");
		}

		if (!(e[12] == (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}

	private boolean isGlnCheckDigitCorrect(String gln) {
		if (gln.length() != 13) {
			return false;
		}
		int[] e = toIntArray(gln);

		if (!(e[12] == (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}

	private boolean isGraiCheckDigitCorrect(String grai) {
		if (grai.length() < 15) {
			return false;
		}

		String exceptSerial = grai.substring(0, 14);

		int[] e = toIntArray(exceptSerial);

		for (int i = 0; i < exceptSerial.length(); i++) {
			e[i] = Integer.parseInt(exceptSerial.charAt(i) + "");
		}

		if (!(e[12] == (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}

	private boolean isGsrnCheckDigitCorrect(String gsrn) {
		if (gsrn.length() != 18) {
			return false;
		}
		int[] e = toIntArray(gsrn);

		if (!(e[17] == (10 - ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12] + e[14] + e[16]) + e[1] + e[3]
				+ e[5] + e[7] + e[9] + e[11] + e[13] + e[15]) % 10)) % 10)) {
			System.out.println("[System] Invalid Check Digit");
			return false;
		}
		return true;
	}

	private int[] toIntArray(String str) {
		int[] e = new int[str.length()];

		for (int i = 0; i < str.length(); i++) {
			e[i] = Integer.parseInt(str.charAt(i) + "");
		}
		return e;
	}

	private String applySerialEncoding(String serial) {
		serial = serial.replaceAll("/", "%2F");
		return serial;
	}

}