package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class SerialShippingContainerCode {
	private String companyPrefix;
	private String extension;
	private String serialRef;
	private String checkDigit;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.SSCCList.length; i++) {
			Matcher m = EPCPatterns.SSCCList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.SSCC.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public SerialShippingContainerCode(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal SSCC");
			companyPrefix = m.group(1);
			extension = m.group(2);
			serialRef = m.group(3);
			checkDigit = getCheckDigit(extension + companyPrefix + serialRef);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/00/" + extension + companyPrefix + serialRef + checkDigit;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal SSCC");
			extension = m.group(1);
			String companyPrefixSerialRef = m.group(2);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixSerialRef);
			companyPrefix = companyPrefixSerialRef.substring(0, gcpLength);
			serialRef = companyPrefixSerialRef.substring(gcpLength);
			checkDigit = m.group(3);
			if (!checkDigit.equals(getCheckDigit(extension + companyPrefix + serialRef)))
				throw new IllegalArgumentException("Invalid check digit");
			isLicensedCompanyPrefix = true;
			this.dl = id;
			this.epc = "urn:epc:id:sscc:" + companyPrefix + "." + extension + serialRef;
		}

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
