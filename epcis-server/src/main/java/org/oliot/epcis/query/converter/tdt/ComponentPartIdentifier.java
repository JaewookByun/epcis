package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class ComponentPartIdentifier {
	private String companyPrefix;
	private String documentType;
	private String cpidSerial;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getSerialEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.CPIList.length; i++) {
			Matcher m = EPCPatterns.CPIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getClassEPCMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.cCPIList.length; i++) {
			Matcher m = EPCPatterns.cCPIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getSerialDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.CPI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public Matcher getClassDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.cCPI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getSerialElectronicProductCodeMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.CPIList.length; i++) {
			Matcher m = EPCPatterns.CPIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getClassElectronicProductCodeMatcher(String epc) {
		for (int i = 0; i < EPCPatterns.cCPIList.length; i++) {
			Matcher m = EPCPatterns.cCPIList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getSerialDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.CPI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getClassDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.cCPI.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public ComponentPartIdentifier(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getSerialEPCMatcher(id);
			if (m != null) {
				companyPrefix = m.group(1);
				documentType = m.group(2);
				cpidSerial = m.group(3);
				isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
				this.epc = id;
				this.dl = "https://id.gs1.org/8010/" + companyPrefix + documentType + "/8011/" + cpidSerial;
			} else {
				m = getClassEPCMatcher(id);
				if (m == null)
					throw new ValidationException("Illegal CPI");
				companyPrefix = m.group(1);
				documentType = m.group(2);
				isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
				this.epc = id;
				this.dl = "https://id.gs1.org/8010/" + companyPrefix + documentType;
			}
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getSerialDLMatcher(id);
			if (m != null) {

				String companyPrefixLocationRef = m.group(1);
				int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength,
						companyPrefixLocationRef);
				companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
				documentType = companyPrefixLocationRef.substring(gcpLength);
				cpidSerial = m.group(2);
				isLicensedCompanyPrefix = true;
				this.dl = id;
				this.epc = "urn:epc:id:cpi:" + companyPrefix + "." + documentType + "." + cpidSerial;
			} else {
				m = getClassDLMatcher(id);
				if (m == null)
					throw new ValidationException("Illegal CPI");
				String companyPrefixLocationRef = m.group(1);
				int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength,
						companyPrefixLocationRef);
				companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
				documentType = companyPrefixLocationRef.substring(gcpLength);
				isLicensedCompanyPrefix = true;
				this.dl = id;
				this.epc = "urn:epc:idpat:cpi:" + companyPrefix + "." + documentType + ".*";
			}
		}
	}

	public static String toEPC(String dl) throws ValidationException {
		Matcher m = getSerialDigitalLinkMatcher(dl);
		if (m != null) {
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			String companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			String documentType = companyPrefixLocationRef.substring(gcpLength);
			String cpidSerial = m.group(2);
			return "urn:epc:id:cpi:" + companyPrefix + "." + documentType + "." + cpidSerial;
		} else {
			m = getClassDigitalLinkMatcher(dl);
			if (m == null) {
				m = getSerialElectronicProductCodeMatcher(dl);
				if (m != null)
					return dl;
				m = getClassElectronicProductCodeMatcher(dl);
				if (m != null)
					return dl;
				else
					throw new ValidationException("Illegal CPI");
			}
			String companyPrefixLocationRef = m.group(1);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixLocationRef);
			String companyPrefix = companyPrefixLocationRef.substring(0, gcpLength);
			String documentType = companyPrefixLocationRef.substring(gcpLength);
			return "urn:epc:idpat:cpi:" + companyPrefix + "." + documentType + ".*";
		}
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("documentType", documentType);
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		obj.put("type", "CPI");
		if (cpidSerial != null && !cpidSerial.equals("*")) {
			obj.put("cpidSerial", cpidSerial);
			obj.put("granularity", "instance");
		}else {
			obj.put("granularity", "class");
		}

		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
