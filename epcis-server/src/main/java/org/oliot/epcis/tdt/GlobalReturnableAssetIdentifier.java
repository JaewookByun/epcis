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

public class GlobalReturnableAssetIdentifier {
	private String companyPrefix;
	private String assetType;
	private String checkDigit;
	private String serialNumber;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.GRAIList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.GRAIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.GRAI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getElectronicProductCodeMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.GRAIList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.GRAIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.SGTIN.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public GlobalReturnableAssetIdentifier(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal GRAI");
			companyPrefix = m.group(1);
			assetType = m.group(2);
			checkDigit = getCheckDigit(companyPrefix + assetType);
			serialNumber = m.group(3);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/8003/0" + companyPrefix + assetType + checkDigit + serialNumber;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal GRAI");
			String companyPrefixAssetType = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixAssetType);
			companyPrefix = companyPrefixAssetType.substring(0, gcpLength);
			assetType = companyPrefixAssetType.substring(gcpLength);
			checkDigit = m.group(2);
			if (!checkDigit.equals(getCheckDigit(companyPrefix + assetType)))
				throw new ValidationException("Invalid check digit");
			serialNumber = m.group(3);
			isLicensedCompanyPrefix = true;
			this.dl = id;
			this.epc = "urn:epc:id:grai:" + companyPrefix + "." + assetType + "." + serialNumber;
		}
	}

	public String getCheckDigit(String companyPrefixAssetType) {
		if (companyPrefixAssetType.length() != 12) {
			return null;
		}
		int[] e = TagDataTranslationEngine.toIntArray(companyPrefixAssetType);

		for (int i = 0; i < companyPrefixAssetType.length(); i++) {
			e[i] = Integer.parseInt(companyPrefixAssetType.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10;

		return String.valueOf(correctCheckDigit);
	}

	public static String retrieveCheckDigit(String companyPrefixAssetType) {
		if (companyPrefixAssetType.length() != 12) {
			return null;
		}
		int[] e = TagDataTranslationEngine.toIntArray(companyPrefixAssetType);

		for (int i = 0; i < companyPrefixAssetType.length(); i++) {
			e[i] = Integer.parseInt(companyPrefixAssetType.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10;

		return String.valueOf(correctCheckDigit);
	}

	public static boolean isValidCheckDigit(String companyPrefixAssetType, String checkDigit) {
		if (companyPrefixAssetType.length() != 12) {
			return false;
		}
		int[] e = TagDataTranslationEngine.toIntArray(companyPrefixAssetType);

		for (int i = 0; i < companyPrefixAssetType.length(); i++) {
			e[i] = Integer.parseInt(companyPrefixAssetType.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[1] + e[3] + e[5] + e[7] + e[9] + e[11]) + e[0] + e[2] + e[4] + e[6] + e[8] + e[10]) % 10))
				% 10;

		return String.valueOf(correctCheckDigit).equals(checkDigit);
	}

	public static String toEPC(String dl) throws ValidationException {
		Matcher m = getDigitalLinkMatcher(dl);
		if (m == null) {
			m = getElectronicProductCodeMatcher(dl);
			if (m == null)
				throw new ValidationException("Illegal GRAI");
			else
				return dl;
		}

		String companyPrefixAssetType = m.group(1);
		int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixAssetType);
		String companyPrefix = companyPrefixAssetType.substring(0, gcpLength);
		String assetType = companyPrefixAssetType.substring(gcpLength);
		String checkDigit = m.group(2);
		if (!isValidCheckDigit(companyPrefix + assetType, checkDigit))
			throw new ValidationException("Invalid check digit");
		String serialNumber = m.group(3);
		return "urn:epc:id:grai:" + companyPrefix + "." + assetType + "." + serialNumber;
	}

	public static String toDL(String epc) throws ValidationException {
		Matcher m = getElectronicProductCodeMatcher(epc);
		if (m == null) {
			m = getDigitalLinkMatcher(epc);
			if (m == null)
				throw new ValidationException("Illegal GRAI");
			else
				return epc;
		}

		String companyPrefix = m.group(1);
		String assetType = m.group(2);
		String checkDigit = retrieveCheckDigit(companyPrefix + assetType);
		String serialNumber = m.group(3);
		if (!TagDataTranslationEngine.isGlobalCompanyPrefix(StaticResource.gcpLength, companyPrefix)) {
			throw new ValidationException("unlicensed global company prefix");
		}

		return "https://id.gs1.org/8003/0" + companyPrefix + assetType + checkDigit + serialNumber;
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
		obj.put("assetType", assetType);
		obj.put("checkDigit", checkDigit);
		obj.put("serialNumber", serialNumber);
		obj.put("granularity", "instance");
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		obj.put("type", "GRAI");
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
