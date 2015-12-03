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

public class CodeParser {

	private HashMap<String, String> applicationIdentifierMap;
	private HashMap<String, String> collection;

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

		// 414 ( SGLN without extension ) 414 & 254 ( SGLN with extension )
		if (applicationIdentifierMap.containsKey("414")) {
			String sgln = generateSgln(gcpLength);
			if (sgln != null) {
				collection.put("sgln", sgln);
			}
		}

		// 11 / 13 / 30 / 310n / 390n
		generateOtherInformation();
		return collection;
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

	private int generateOtherInformation() {
		int cnt = 0;
		// 11 - Production Date - YYMMDD
		if (applicationIdentifierMap.containsKey("11")) {
			SimpleDateFormat sdf = new SimpleDateFormat("YYMMDD");
			String productionDate = applicationIdentifierMap.get("11");
			try {
				sdf.parse(productionDate);
				collection.put("urn:id:epc:ai:11", productionDate);
				// System.out.println("[System] " + "urn:id:epc:ai:11" +
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
				collection.put("urn:id:epc:ai:13", packagingDate);
				// System.out.println("[System] " + "urn:id:epc:ai:13" +
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
					collection.put("urn:id:epc:ai:30", String.valueOf(count));
					// System.out.println("[System] " + "urn:id:epc:ai:30" +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 10));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 100));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 1000));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 1000));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 10000));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 100000));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 1000000));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 10000000));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 100000000));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:310n", String.valueOf(weight / 1000000000));
					// System.out.println("[System] " + "urn:id:epc:ai:310n " +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" + String.valueOf(priceDouble));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 10));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 100));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 1000));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 10000));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 100000));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 1000000));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 10000000));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 100000000));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
					collection.put("urn:id:epc:ai:393n", currencyCode + "|" +  String.valueOf(priceDouble / 1000000000));
					// System.out.println("[System] " + "urn:id:epc:ai:393n:" +
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
			String applicationIdentifier = codeFragment2[0].trim();
			String elementString = codeFragment2[1].trim();

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

	private int[] toIntArray(String str) {
		int[] e = new int[str.length()];

		for (int i = 0; i < str.length(); i++) {
			e[i] = Integer.parseInt(str.charAt(i) + "");
		}
		return e;
	}

}