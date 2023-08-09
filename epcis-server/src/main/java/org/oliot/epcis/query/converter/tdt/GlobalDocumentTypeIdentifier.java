package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class GlobalDocumentTypeIdentifier {
	private String companyPrefix;
	private String documentType;
	private String checkDigit;
	private String serial;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.GDTIList.length; i++) {
			Matcher m = EPCPatterns.GDTIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.GDTI.matcher(dl);
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

	public Matcher getClassDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.cSGLN.matcher(dl);
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

	/*
	 * public static final Pattern[] GDTIList = new Pattern[] {
	 * Pattern.compile("^urn:epc:id:gdti:([0-9]{12})\\.([0-9]{0})\\.([0-9]{0,17})$")
	 * ,
	 * Pattern.compile("^urn:epc:id:gdti:([0-9]{11})\\.([0-9]{1})\\.([0-9]{0,17})$")
	 * ,
	 * Pattern.compile("^urn:epc:id:gdti:([0-9]{10})\\.([0-9]{2})\\.([0-9]{0,17})$")
	 * ,
	 * Pattern.compile("^urn:epc:id:gdti:([0-9]{9})\\.([0-9]{3})\\.([0-9]{0,17})$"),
	 * Pattern.compile("^urn:epc:id:gdti:([0-9]{8})\\.([0-9]{4})\\.([0-9]{0,17})$"),
	 * Pattern.compile("^urn:epc:id:gdti:([0-9]{7})\\.([0-9]{5})\\.([0-9]{0,17})$"),
	 * Pattern.compile("^urn:epc:id:gdti:([0-9]{6})\\.([0-9]{6})\\.([0-9]{0,17})$")
	 * };
	 * 
	 * public static final Pattern[] cGDTIList = new Pattern[] {
	 * Pattern.compile("^urn:epc:idpat:gdti:([0-9]{12})\\.([0-9]{0})\\.\\*$"),
	 * Pattern.compile("^urn:epc:idpat:gdti:([0-9]{11})\\.([0-9]{1})\\.\\*$"),
	 * Pattern.compile("^urn:epc:idpat:gdti:([0-9]{10})\\.([0-9]{2})\\.\\*$"),
	 * Pattern.compile("^urn:epc:idpat:gdti:([0-9]{9})\\.([0-9]{3})\\.\\*$"),
	 * Pattern.compile("^urn:epc:idpat:gdti:([0-9]{8})\\.([0-9]{4})\\.\\*$"),
	 * Pattern.compile("^urn:epc:idpat:gdti:([0-9]{7})\\.([0-9]{5})\\.\\*$"),
	 * Pattern.compile("^urn:epc:idpat:gdti:([0-9]{6})\\.([0-9]{6})\\.\\*$") };
	 */

	public GlobalDocumentTypeIdentifier(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal SGLN");

			companyPrefix = m.group(1);
			documentType = m.group(2);
			checkDigit = getCheckDigit(companyPrefix + documentType);
			serial = m.group(3);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/414/" + companyPrefix + documentType + checkDigit + serial;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null) {
				throw new ValidationException("Illegal SGLN");

			}
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			documentType = companyPrefixLocationRef.substring(gcpLength);
			checkDigit = m.group(2);
			if (!checkDigit.equals(getCheckDigit(companyPrefix + documentType)))
				throw new ValidationException("Invalid check digit");
			serial = m.group(3);
			this.dl = id;
			this.epc = "urn:epc:id:sgln:" + companyPrefix + "." + documentType + "." + serial;
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
		obj.put("documentType", documentType);
		obj.put("checkDigit", checkDigit);
		obj.put("serial", serial);
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
