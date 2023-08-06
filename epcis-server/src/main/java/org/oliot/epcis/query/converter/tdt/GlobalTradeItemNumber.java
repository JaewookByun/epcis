package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;

import io.vertx.core.json.JsonObject;

public class GlobalTradeItemNumber {
	private String companyPrefix;
	private String indicator;
	private String itemRef;
	private String checkDigit;
	private String serialNumber;
	private String lotNumber;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public static boolean isSGTIN(String epc) {
		boolean isSGTIN = false;
		for (int i = 0; i < TagDataTranslationEngine.SGTINList.length; i++) {
			if (epc.matches(TagDataTranslationEngine.SGTINList[i])) {
				isSGTIN = true;
			}
		}
		return isSGTIN;
	}
	
	public static boolean isLGTIN(String epc) {
		boolean isLGTIN = false;
		for (int i = 0; i < TagDataTranslationEngine.LGTINList.length; i++) {
			if (epc.matches(TagDataTranslationEngine.LGTINList[i])) {
				isLGTIN = true;
			}
		}
		return isLGTIN;
	}
	
	public static boolean isGTIN(String epc) {
		boolean isGTIN = false;
		for (int i = 0; i < TagDataTranslationEngine.cGTINList.length; i++) {
			if (epc.matches(TagDataTranslationEngine.cGTINList[i])) {
				isGTIN = true;
			}
		}
		return isGTIN;
	}
	
	public GlobalTradeItemNumber(HashMap<String, Integer> gcpLengthList, String epc) throws IllegalArgumentException {
		if (epc.startsWith("urn:epc:id:sgtin:")) {
			if(!isSGTIN(epc))
				throw new IllegalArgumentException("Illegal SGTIN");
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			companyPrefix = numArr[0];
			indicator = numArr[1].substring(0, 1);
			itemRef = numArr[1].substring(1, numArr[1].length());
			checkDigit = getCheckDigit(indicator + companyPrefix + itemRef);
			serialNumber = numArr[2];
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);	
			this.epc = epc;
			this.dl = "https://id.gs1.org/01/" + indicator + companyPrefix + itemRef + checkDigit + "/21/" + serialNumber;
		} else if (epc.startsWith("urn:epc:class:lgtin:")) {
			if(!isLGTIN(epc))
				throw new IllegalArgumentException("Illegal LGTIN");
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			companyPrefix = numArr[0];
			indicator = numArr[1].substring(0, 1);
			itemRef = numArr[1].substring(1, numArr[1].length());
			checkDigit = getCheckDigit(indicator + companyPrefix + itemRef);
			lotNumber = numArr[2];
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);

			this.epc = epc;
			this.dl = "https://id.gs1.org/01/" + indicator + companyPrefix + itemRef + checkDigit + "/10/" + lotNumber;
		} else if (epc.startsWith("urn:epc:idpat:sgtin:")) {
			if(!isGTIN(epc))
				throw new IllegalArgumentException("Illegal GTIN");
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			companyPrefix = numArr[0];
			indicator = numArr[1].substring(0, 1);
			itemRef = numArr[1].substring(1, numArr[1].length());
			checkDigit = getCheckDigit(indicator + companyPrefix + itemRef);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);

			this.epc = epc;
			this.dl = "https://id.gs1.org/01/" + indicator + companyPrefix + itemRef + checkDigit;
		} else
			throw new IllegalArgumentException("Illegal GTIN");
	}

	public String getCheckDigit(String indicatorGtin) {
		if (indicatorGtin.length() != 13) {
			return null;
		}
		int[] e = TagDataTranslationEngine.toIntArray(indicatorGtin);

		for (int i = 0; i < indicatorGtin.length(); i++) {
			e[i] = Integer.parseInt(indicatorGtin.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12]) + e[1] + e[3] + e[5] + e[7] + e[9] + e[11])
						% 10))
				% 10;

		return String.valueOf(correctCheckDigit);
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("indicator", indicator);
		obj.put("itemRef", itemRef);
		obj.put("checkDigit", checkDigit);
		obj.put("serialNumber", serialNumber);
		obj.put("lotNumber", lotNumber);
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
