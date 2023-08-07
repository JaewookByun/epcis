package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class SerializedGlobalTradeItemNumber {
	private String companyPrefix;
	private String indicator;
	private String itemRef;
	private String checkDigit;
	private String serialNumber;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.SGTINList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.SGTINList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.SGTIN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public SerializedGlobalTradeItemNumber(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal SGTIN");
			companyPrefix = m.group(1);
			indicator = m.group(2);
			itemRef = m.group(3);
			checkDigit = getCheckDigit(indicator + companyPrefix + itemRef);
			serialNumber = m.group(4);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/01/" + indicator + companyPrefix + itemRef + checkDigit + "/21/"
					+ serialNumber;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null)
				throw new IllegalArgumentException("Illegal SGTIN");
			indicator = m.group(1);
			String companyPrefixItemRef = m.group(2);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixItemRef);
			companyPrefix = companyPrefixItemRef.substring(0, gcpLength);
			itemRef = companyPrefixItemRef.substring(gcpLength);
			checkDigit = m.group(3);
			if (!checkDigit.equals(getCheckDigit(indicator + companyPrefix + itemRef)))
				throw new IllegalArgumentException("Invalid check digit");
			serialNumber = m.group(4);
			isLicensedCompanyPrefix = true;
			this.dl = id;
			this.epc = "urn:epc:id:sgtin:" + companyPrefix + "." + indicator + itemRef + "." + serialNumber;
		}
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
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
