package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class GlobalIdentificationNumberConsignment {
	private String companyPrefix;
	private String consignmentRef;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.GINCList.length; i++) {
			Matcher m = EPCPatterns.GINCList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.GINC.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getElectronicProductCodeMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.GINCList.length; i++) {
			Matcher m = EPCPatterns.GINCList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.GINC.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public GlobalIdentificationNumberConsignment(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getEPCMatcher(id);
			if (m == null)
				throw new ValidationException("Illegal GINC");
			companyPrefix = m.group(1);
			consignmentRef = m.group(2);
			isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
			this.epc = id;
			this.dl = "https://id.gs1.org/401/" + companyPrefix + consignmentRef;
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getDLMatcher(id);
			if (m == null) {
				throw new ValidationException("Illegal GINC");
			}
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			consignmentRef = companyPrefixLocationRef.substring(gcpLength);
			isLicensedCompanyPrefix = true;
			this.dl = id;
			this.epc = "urn:epc:id:ginc:" + companyPrefix + "." + consignmentRef;
		}
	}

	public static String toEPC(String dl) throws ValidationException {
		Matcher m = getDigitalLinkMatcher(dl);
		if (m == null) {
			m = getElectronicProductCodeMatcher(dl);
			if (m == null)
				throw new ValidationException("Illegal GINC");
			else
				return dl;
		}

		String companyPrefixLocationRef = m.group(1);
		int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
		String companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
		String consignmentRef = companyPrefixLocationRef.substring(gcpLength);
		return "urn:epc:id:ginc:" + companyPrefix + "." + consignmentRef;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("consignmentRef", consignmentRef);
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		obj.put("granularity", "instance");
		obj.put("type", "GINC");
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
