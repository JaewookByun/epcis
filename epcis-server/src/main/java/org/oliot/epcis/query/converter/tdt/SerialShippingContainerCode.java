package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;

import io.vertx.core.json.JsonObject;

public class SerialShippingContainerCode {
	private String companyPrefix;
	private String extension;
	private String serialRef;
	private String checkDigit;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public static boolean isSSCC(String epc) {
		boolean isSSCC = false;
		for (int i = 0; i < TagDataTranslationEngine.SSCCList.length; i++) {
			if (epc.matches(TagDataTranslationEngine.SSCCList[i])) {
				isSSCC = true;
			}
		}
		return isSSCC;
	}

	public SerialShippingContainerCode(HashMap<String, Integer> gcpLengthList, String epc)
			throws IllegalArgumentException {
		if (!isSSCC(epc))
			throw new IllegalArgumentException("Illegal SGTIN");
		String[] elemArr = epc.split(":");
		String last = elemArr[elemArr.length - 1];
		String[] numArr = last.split("\\.");
		companyPrefix = numArr[0];
		extension = numArr[1].substring(0, 1);
		serialRef = numArr[1].substring(1, numArr[1].length());
		checkDigit = getCheckDigit(extension + companyPrefix + serialRef);
		isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
		this.epc = epc;
		this.dl = "https://id.gs1.org/00/" + extension + companyPrefix + serialRef + checkDigit;
	}

	public String getCheckDigit(String indicatorSSCC) {
		if (indicatorSSCC.length() != 17) {
			return null;
		}
		int[] e = TagDataTranslationEngine.toIntArray(indicatorSSCC);

		for (int i = 0; i < indicatorSSCC.length(); i++) {
			e[i] = Integer.parseInt(indicatorSSCC.charAt(i) + "");
		}

		int correctCheckDigit = (10 - ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12] + e[14] + e[16]) + e[1]
				+ e[3] + e[5] + e[7] + e[9] + e[11] + e[13] + e[15]) % 10)) % 10;

		return String.valueOf(correctCheckDigit);
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("extension", extension);
		obj.put("serialRef", serialRef);
		obj.put("checkDigit", checkDigit);
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
