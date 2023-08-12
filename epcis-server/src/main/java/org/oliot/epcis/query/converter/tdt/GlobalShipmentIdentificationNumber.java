package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class GlobalShipmentIdentificationNumber {
	private String companyPrefix;
	private String shipperRef;
	private String checkDigit;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.GSINList.length; i++) {
			Matcher m = EPCPatterns.GSINList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.GSIN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getElectronicProductCodeMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.GSINList.length; i++) {
			Matcher m = EPCPatterns.GSINList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.GSIN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public GlobalShipmentIdentificationNumber(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal GSIN");

			companyPrefix = m.group(1);
			shipperRef = m.group(2);
			checkDigit = getCheckDigit(companyPrefix + shipperRef);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/402/" + companyPrefix + shipperRef + checkDigit;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null) {
				throw new ValidationException("Illegal GSRNP");

			}
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			shipperRef = companyPrefixLocationRef.substring(gcpLength);
			checkDigit = m.group(2);
			if (!checkDigit.equals(getCheckDigit(companyPrefix + shipperRef)))
				throw new ValidationException("Invalid check digit");
			isLicensedCompanyPrefix = true;
			this.dl = id;
			this.epc = "urn:epc:id:gsin:" + companyPrefix + "." + shipperRef;
		}
	}

	public String getCheckDigit(String indicatorGtin) {
		if (indicatorGtin.length() != 16) {
			return null;
		}
		int[] e = TagDataTranslationEngine.toIntArray(indicatorGtin);

		for (int i = 0; i < indicatorGtin.length(); i++) {
			e[i] = Integer.parseInt(indicatorGtin.charAt(i) + "");
		}

		int correctCheckDigit = (10 - ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12] + e[14]) + e[1] + e[3]
				+ e[5] + e[7] + e[9] + e[11] + e[13] + e[15]) % 10)) % 10;

		return String.valueOf(correctCheckDigit);
	}

	public static boolean isValidCheckDigit(String indicatorGtin, String checkDigit) {
		if (indicatorGtin.length() != 16) {
			return false;
		}
		int[] e = TagDataTranslationEngine.toIntArray(indicatorGtin);

		for (int i = 0; i < indicatorGtin.length(); i++) {
			e[i] = Integer.parseInt(indicatorGtin.charAt(i) + "");
		}

		int correctCheckDigit = (10 - ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12] + e[14]) + e[1] + e[3]
				+ e[5] + e[7] + e[9] + e[11] + e[13] + e[15]) % 10)) % 10;

		return String.valueOf(correctCheckDigit).equals(checkDigit);
	}

	public static String toEPC(String dl) throws ValidationException {
		Matcher m = getDigitalLinkMatcher(dl);
		if (m == null) {
			m = getElectronicProductCodeMatcher(dl);
			if (m == null)
				throw new ValidationException("Illegal GSIN");
			else
				return dl;
		}

		String companyPrefixServiceReference = m.group(1);
		int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixServiceReference);
		String companyPrefix = companyPrefixServiceReference.substring(0, gcpLength);
		String shipperRef = companyPrefixServiceReference.substring(gcpLength);
		String checkDigit = m.group(2);
		if (!isValidCheckDigit(companyPrefix + shipperRef, checkDigit))
			throw new ValidationException("Invalid check digit");
		return "urn:epc:id:gsin:" + companyPrefix + "." + shipperRef;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("shipperRef", shipperRef);
		obj.put("checkDigit", checkDigit);
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		obj.put("type", "GSIN");
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
