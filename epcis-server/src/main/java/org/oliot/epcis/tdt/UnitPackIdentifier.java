package org.oliot.epcis.tdt;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class UnitPackIdentifier {
	private String companyPrefix;
	private String indicator;
	private String itemRef;
	private String checkDigit;
	private String tpx;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.UPUIList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.UPUIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.UPUI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getElectronicProductCodeMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.UPUIList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.UPUIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.UPUI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public UnitPackIdentifier(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal UPUI");
			companyPrefix = m.group(1);
			indicator = m.group(2);
			itemRef = m.group(3);
			checkDigit = getCheckDigit(indicator + companyPrefix + itemRef);
			tpx = m.group(4);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/01/" + indicator + companyPrefix + itemRef + checkDigit + "/235/" + tpx;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal UPUI");
			indicator = m.group(1);
			String companyPrefixItemRef = m.group(2);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixItemRef);
			companyPrefix = companyPrefixItemRef.substring(0, gcpLength);
			itemRef = companyPrefixItemRef.substring(gcpLength);
			checkDigit = m.group(3);
			if (!checkDigit.equals(getCheckDigit(indicator + companyPrefix + itemRef)))
				throw new ValidationException("Invalid check digit");
			tpx = m.group(4);
			isLicensedCompanyPrefix = true;
			this.dl = id;
			this.epc = "urn:epc:id:upui:" + companyPrefix + "." + indicator + itemRef + "." + tpx;
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

	public static String retrieveCheckDigit(String indicatorGtin) {
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

	public static boolean isValidCheckDigit(String indicatorGtin, String checkDigit) {
		if (indicatorGtin.length() != 13) {
			return false;
		}
		int[] e = TagDataTranslationEngine.toIntArray(indicatorGtin);

		for (int i = 0; i < indicatorGtin.length(); i++) {
			e[i] = Integer.parseInt(indicatorGtin.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12]) + e[1] + e[3] + e[5] + e[7] + e[9] + e[11])
						% 10))
				% 10;

		return String.valueOf(correctCheckDigit).equals(checkDigit);
	}

	public static String toEPC(String dl) throws ValidationException {
		Matcher m = getDigitalLinkMatcher(dl);
		if (m == null) {
			m = getElectronicProductCodeMatcher(dl);
			if (m == null)
				throw new ValidationException("Illegal UPUI");
			else
				return dl;
		}

		String indicator = m.group(1);
		String companyPrefixItemRef = m.group(2);
		int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixItemRef);
		String companyPrefix = companyPrefixItemRef.substring(0, gcpLength);
		String itemRef = companyPrefixItemRef.substring(gcpLength);
		String checkDigit = m.group(3);
		if (!isValidCheckDigit(indicator + companyPrefix + itemRef, checkDigit))
			throw new ValidationException("Invalid check digit");
		String tpx = m.group(4);
		return "urn:epc:id:upui:" + companyPrefix + "." + indicator + itemRef + "." + tpx;
	}

	public static String toDL(String epc) throws ValidationException {
		Matcher m = getElectronicProductCodeMatcher(epc);
		if (m == null) {
			m = getDigitalLinkMatcher(epc);
			if (m == null)
				throw new ValidationException("Illegal UPUI");
			else
				return epc;
		}

		String companyPrefix = m.group(1);
		String indicator = m.group(2);
		String itemRef = m.group(3);
		String checkDigit = retrieveCheckDigit(indicator + companyPrefix + itemRef);
		String tpx = m.group(4);
		if (!TagDataTranslationEngine.isGlobalCompanyPrefix(StaticResource.gcpLength, companyPrefix)) {
			throw new ValidationException("unlicensed global company prefix");
		}
		return "https://id.gs1.org/01/" + indicator + companyPrefix + itemRef + checkDigit + "/235/" + tpx;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		try {
			obj.put("dl_encoded", URLEncoder.encode(dl, StandardCharsets.UTF_8.toString()));
		} catch (Exception e) {

		}
		obj.put("companyPrefix", companyPrefix);
		obj.put("indicator", indicator);
		obj.put("itemRef", itemRef);
		obj.put("checkDigit", checkDigit);
		obj.put("tpx", tpx);
		obj.put("granularity", "instance");
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		obj.put("type", "UPUI");
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
