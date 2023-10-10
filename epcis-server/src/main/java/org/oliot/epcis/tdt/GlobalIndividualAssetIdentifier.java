package org.oliot.epcis.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class GlobalIndividualAssetIdentifier {
	private String companyPrefix;
	private String assetReference;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.GIAIList.length; i++) {
			Matcher m = EPCPatterns.GIAIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.GIAI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getElectronicProductCodeMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.GIAIList.length; i++) {
			Matcher m = EPCPatterns.GIAIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.GIAI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public GlobalIndividualAssetIdentifier(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal GDTI");
			companyPrefix = m.group(1);
			assetReference = m.group(2);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/8004/" + companyPrefix + assetReference;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null) {
				throw new ValidationException("Illegal GDTI");
			}
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			assetReference = companyPrefixLocationRef.substring(gcpLength);
			isLicensedCompanyPrefix = true;
			this.dl = id;
			this.epc = "urn:epc:id:giai:" + companyPrefix + "." + assetReference;
		}
	}

	public static String toEPC(String dl) throws ValidationException {
		Matcher m = getDigitalLinkMatcher(dl);
		if (m == null) {
			m = getElectronicProductCodeMatcher(dl);
			if (m == null)
				throw new ValidationException("Illegal GDTI");
			else
				return dl;
		}

		String companyPrefixLocationRef = m.group(1);
		int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
		String companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
		String assetReference = companyPrefixLocationRef.substring(gcpLength);
		return "urn:epc:id:giai:" + companyPrefix + "." + assetReference;
	}

	public static String toDL(String epc) throws ValidationException {
		Matcher m = getElectronicProductCodeMatcher(epc);
		if (m == null) {
			m = getDigitalLinkMatcher(epc);
			if (m == null)
				throw new ValidationException("Illegal GDTI");
			else
				return epc;
		}

		String companyPrefix = m.group(1);
		String assetReference = m.group(2);
		if (!TagDataTranslationEngine.isGlobalCompanyPrefix(StaticResource.gcpLength, companyPrefix)) {
			throw new ValidationException("unlicensed global company prefix");
		}
		return "https://id.gs1.org/8004/" + companyPrefix + assetReference;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("assetReference", assetReference);
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		obj.put("granularity", "instance");
		obj.put("type", "GIAI");
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
