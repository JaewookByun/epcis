package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class GlobalLocationNumber {
	private String companyPrefix;
	private String locationRef;
	private String checkDigit;
	private String glnExtension;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.SGLNList.length; i++) {
			Matcher m = EPCPatterns.SGLNList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getSerialDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.SGLN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public Matcher getClassDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.cSGLN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getElectronicProductCodeMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.SGLNList.length; i++) {
			Matcher m = EPCPatterns.SGLNList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getSerialDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.SGLN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getClassDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.cSGLN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public GlobalLocationNumber(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal SGLN");

			companyPrefix = m.group(1);
			locationRef = m.group(2);
			checkDigit = getCheckDigit(companyPrefix + locationRef);
			glnExtension = m.group(3);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			if (glnExtension.equals("0")) {
				this.dl = "https://id.gs1.org/414/" + companyPrefix + locationRef + checkDigit;
			} else
				this.dl = "https://id.gs1.org/414/" + companyPrefix + locationRef + checkDigit + "/254/" + glnExtension;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getSerialDLMatcher(id);
			if (m != null) {
				String companyPrefixLocationRef = m.group(1);
				int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength,
						companyPrefixLocationRef);
				companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
				locationRef = companyPrefixLocationRef.substring(gcpLength);
				checkDigit = m.group(2);
				if (!checkDigit.equals(getCheckDigit(companyPrefix + locationRef)))
					throw new ValidationException("Invalid check digit");
				glnExtension = m.group(3);
				isLicensedCompanyPrefix = true;
				this.dl = id;
				this.epc = "urn:epc:id:sgln:" + companyPrefix + "." + locationRef + "." + glnExtension;
			} else {
				m = getClassDLMatcher(id);
				if (m == null)
					throw new ValidationException("Illegal SGLN");
				String companyPrefixLocationRef = m.group(1);
				int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength,
						companyPrefixLocationRef);
				companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
				locationRef = companyPrefixLocationRef.substring(gcpLength);
				checkDigit = m.group(2);
				if (!checkDigit.equals(getCheckDigit(companyPrefix + locationRef)))
					throw new ValidationException("Invalid check digit");
				glnExtension = "0";
				isLicensedCompanyPrefix = true;
				this.dl = id;
				this.epc = "urn:epc:id:sgln:" + companyPrefix + "." + locationRef + "." + glnExtension;
			}
		}
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

	public static boolean isValidCheckDigit(String indicatorGtin, String checkDigit) {
		if (indicatorGtin.length() != 12) {
			return false;
		}
		int[] e = TagDataTranslationEngine.toIntArray(indicatorGtin);

		for (int i = 0; i < indicatorGtin.length(); i++) {
			e[i] = Integer.parseInt(indicatorGtin.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10;

		return String.valueOf(correctCheckDigit).equals(checkDigit);
	}

	public static String toEPC(String dl) throws ValidationException {
		Matcher m = getSerialDigitalLinkMatcher(dl);
		if (m != null) {
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			String companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			String locationRef = companyPrefixLocationRef.substring(gcpLength);
			String checkDigit = m.group(2);
			if (!isValidCheckDigit(companyPrefix + locationRef, checkDigit))
				throw new ValidationException("Invalid check digit");
			String glnExtension = m.group(3);
			return "urn:epc:id:sgln:" + companyPrefix + "." + locationRef + "." + glnExtension;
		} else {
			m = getClassDigitalLinkMatcher(dl);
			if (m == null) {
				m = getElectronicProductCodeMatcher(dl);
				if (m == null)
					throw new ValidationException("Illegal SGLN");
				else
					return dl;
			}

			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			String companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			String locationRef = companyPrefixLocationRef.substring(gcpLength);
			String checkDigit = m.group(2);
			if (!isValidCheckDigit(companyPrefix + locationRef, checkDigit))
				throw new ValidationException("Invalid check digit");
			String glnExtension = "0";
			return "urn:epc:id:sgln:" + companyPrefix + "." + locationRef + "." + glnExtension;
		}
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
		obj.put("granularity", "instance");
		obj.put("type", "SGLN");
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
