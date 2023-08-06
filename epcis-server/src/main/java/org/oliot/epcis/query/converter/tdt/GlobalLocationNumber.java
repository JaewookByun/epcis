package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;

import io.vertx.core.json.JsonObject;

public class GlobalLocationNumber {
	private String companyPrefix;
	private String locationRef;
	private String checkDigit;
	private String glnExtension;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public static boolean isSGLN(String epc) {
		boolean isSGLN = false;
		for (int i = 0; i < TagDataTranslationEngine.SGLNList.length; i++) {
			if (epc.matches(TagDataTranslationEngine.SGLNList[i])) {
				isSGLN = true;
			}
		}
		return isSGLN;
	}

	public GlobalLocationNumber(HashMap<String, Integer> gcpLengthList, String epc) throws IllegalArgumentException {
		if (!isSGLN(epc))
			throw new IllegalArgumentException("Illegal SGTIN");
		String[] elemArr = epc.split(":");
		String last = elemArr[elemArr.length - 1];
		String[] numArr = last.split("\\.");
		companyPrefix = numArr[0];
		locationRef = numArr[1];
		checkDigit = getCheckDigit(companyPrefix + locationRef);
		glnExtension = numArr[2];
		isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
		this.epc = epc;
		if (glnExtension.equals("0")) {
			this.dl = "https://id.gs1.org/414/" + companyPrefix + locationRef + checkDigit;
		} else
			this.dl = "https://id.gs1.org/414/" + companyPrefix + locationRef + checkDigit + "/254/" + glnExtension;
	}

	public String getCheckDigit(String indicatorGtin) {
		if (indicatorGtin.length() != 12) {
			return null;
		}
		int[] e = TagDataTranslationEngine.toIntArray(indicatorGtin);

		for (int i = 0; i < indicatorGtin.length(); i++) {
			e[i] = Integer.parseInt(indicatorGtin.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10;

		return String.valueOf(correctCheckDigit);
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("locationRef", locationRef);
		obj.put("checkDigit", checkDigit);
		if (!glnExtension.equals("0"))
			obj.put("glnExtension", glnExtension);
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
