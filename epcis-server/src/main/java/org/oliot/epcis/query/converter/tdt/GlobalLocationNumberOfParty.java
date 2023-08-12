package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class GlobalLocationNumberOfParty {
	private String companyPrefix;
	private String partyRef;
	private String checkDigit;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.PGLNList.length; i++) {
			Matcher m = EPCPatterns.PGLNList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.PGLN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getElectronicProductCodeMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.PGLNList.length; i++) {
			Matcher m = EPCPatterns.PGLNList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.PGLN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public GlobalLocationNumberOfParty(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal PGLN");

			companyPrefix = m.group(1);
			partyRef = m.group(2);
			checkDigit = getCheckDigit(companyPrefix + partyRef);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/417/" + companyPrefix + partyRef + checkDigit;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null) {
				throw new ValidationException("Illegal PGLN");
			}
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			partyRef = companyPrefixLocationRef.substring(gcpLength);
			checkDigit = m.group(2);
			if (!checkDigit.equals(getCheckDigit(companyPrefix + partyRef)))
				throw new ValidationException("Invalid check digit");
			isLicensedCompanyPrefix = true;
			this.dl = id;
			this.epc = "urn:epc:id:pgln:" + companyPrefix + "." + partyRef;

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
		Matcher m = getDigitalLinkMatcher(dl);
		if (m != null) {
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			String companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			String partyRef = companyPrefixLocationRef.substring(gcpLength);
			String checkDigit = m.group(2);
			if (!isValidCheckDigit(companyPrefix + partyRef, checkDigit))
				throw new ValidationException("Invalid check digit");
			return "urn:epc:id:pgln:" + companyPrefix + "." + partyRef;
		} else {
			m = getDigitalLinkMatcher(dl);
			if (m == null) {
				m = getElectronicProductCodeMatcher(dl);
				if (m == null)
					throw new ValidationException("Illegal PGLN");
				else
					return dl;
			}
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			String companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			String partyRef = companyPrefixLocationRef.substring(gcpLength);
			String checkDigit = m.group(2);
			if (!isValidCheckDigit(companyPrefix + partyRef, checkDigit))
				throw new ValidationException("Invalid check digit");
			return "urn:epc:id:pgln:" + companyPrefix + "." + partyRef;
		}
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("partyRef", partyRef);
		obj.put("checkDigit", checkDigit);
		obj.put("granularity", "instance");
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		obj.put("type", "PGLN");
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
