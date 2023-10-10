package org.oliot.epcis.tdt;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.resource.DigitalLinkPatterns;
import org.oliot.epcis.resource.EPCPatterns;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class IndividualTradeItemPiece {
	private String companyPrefix;
	private String indicator;
	private String itemRef;
	private String checkDigit;
	private String piece;
	private String total;
	private String serialNumber;
	private boolean isLicensedCompanyPrefix;

	private String epc;
	private String dl;

	public Matcher getSerialEPCMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.ITIPList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.ITIPList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getClassEPCMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.cITIPList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.cITIPList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public Matcher getSerialDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.ITIP.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public Matcher getClassDLMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.cITIP.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getSerialElectronicProductCodeMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.ITIPList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.ITIPList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getClassElectronicProductCodeMatcher(String epc) {
		Pattern[] patterns = EPCPatterns.cITIPList;
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = EPCPatterns.cITIPList[i].matcher(epc);
			if (m.find())
				return m;
		}
		return null;
	}

	public static Matcher getSerialDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.ITIP.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public static Matcher getClassDigitalLinkMatcher(String dl) {
		Matcher m = DigitalLinkPatterns.cITIP.matcher(dl);
		if (m.find())
			return m;
		return null;
	}

	public IndividualTradeItemPiece(HashMap<String, Integer> gcpLengthList, String id, CodeScheme scheme)
			throws ValidationException {
		if (scheme == CodeScheme.EPCPureIdentitiyURI) {
			Matcher m = getSerialEPCMatcher(id);
			if (m != null) {
				companyPrefix = m.group(1);
				indicator = m.group(2);
				itemRef = m.group(3);
				checkDigit = getCheckDigit(indicator + companyPrefix + itemRef);
				piece = m.group(4);
				total = m.group(5);
				serialNumber = m.group(6);
				isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
				this.epc = id;
				this.dl = "https://id.gs1.org/8006/" + indicator + companyPrefix + itemRef + checkDigit + piece + total
						+ "/21/" + serialNumber;
			} else {
				m = getClassEPCMatcher(id);
				if (m == null)
					throw new ValidationException("Illegal ITIP");

				companyPrefix = m.group(1);
				indicator = m.group(2);
				itemRef = m.group(3);
				checkDigit = getCheckDigit(indicator + companyPrefix + itemRef);
				piece = m.group(4);
				total = m.group(5);
				isLicensedCompanyPrefix = TagDataTranslationEngine.isGlobalCompanyPrefix(gcpLengthList, companyPrefix);
				this.epc = id;
				this.dl = "https://id.gs1.org/8006/" + indicator + companyPrefix + itemRef + checkDigit + piece + total;
			}
		} else if (scheme == CodeScheme.GS1DigitalLink) {
			Matcher m = getSerialDLMatcher(id);
			if (m != null) {
				indicator = m.group(1);
				String companyPrefixItemRef = m.group(2);
				int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixItemRef);
				companyPrefix = companyPrefixItemRef.substring(0, gcpLength);
				itemRef = companyPrefixItemRef.substring(gcpLength);
				checkDigit = m.group(3);
				if (!checkDigit.equals(getCheckDigit(indicator + companyPrefix + itemRef)))
					throw new ValidationException("Invalid check digit");
				piece = m.group(4);
				total = m.group(5);
				serialNumber = m.group(6);
				isLicensedCompanyPrefix = true;
				this.dl = id;
				this.epc = "urn:epc:id:itip:" + companyPrefix + "." + indicator + itemRef + "." + piece + "." + total
						+ "." + serialNumber;
			} else {
				m = getClassDLMatcher(id);
				if (m == null)
					throw new ValidationException("Illegal ITIP");
				indicator = m.group(1);
				String companyPrefixItemRef = m.group(2);
				int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixItemRef);
				companyPrefix = companyPrefixItemRef.substring(0, gcpLength);
				itemRef = companyPrefixItemRef.substring(gcpLength);
				checkDigit = m.group(3);
				if (!checkDigit.equals(getCheckDigit(indicator + companyPrefix + itemRef)))
					throw new ValidationException("Invalid check digit");
				piece = m.group(4);
				total = m.group(5);
				isLicensedCompanyPrefix = true;
				this.dl = id;
				this.epc = "urn:epc:idpat:itip:" + companyPrefix + "." + indicator + itemRef + "." + piece + "." + total
						+ ".*";
			}

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
		Matcher m = getSerialDigitalLinkMatcher(dl);
		if (m != null) {
			String indicator = m.group(1);
			String companyPrefixItemRef = m.group(2);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixItemRef);
			String companyPrefix = companyPrefixItemRef.substring(0, gcpLength);
			String itemRef = companyPrefixItemRef.substring(gcpLength);
			String checkDigit = m.group(3);
			if (!isValidCheckDigit(indicator + companyPrefix + itemRef, checkDigit))
				throw new ValidationException("Invalid check digit");
			String piece = m.group(4);
			String total = m.group(5);
			String serialNumber = m.group(6);
			return "urn:epc:id:itip:" + companyPrefix + "." + indicator + itemRef + "." + piece + "." + total + "."
					+ serialNumber;
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
					throw new ValidationException("Illegal ITIP");
			}

			String indicator = m.group(1);
			String companyPrefixItemRef = m.group(2);
			int gcpLength = TagDataTranslationEngine.getGCPLength(StaticResource.gcpLength, companyPrefixItemRef);
			String companyPrefix = companyPrefixItemRef.substring(0, gcpLength);
			String itemRef = companyPrefixItemRef.substring(gcpLength);
			String checkDigit = m.group(3);
			if (!isValidCheckDigit(indicator + companyPrefix + itemRef, checkDigit))
				throw new ValidationException("Invalid check digit");
			String piece = m.group(4);
			String total = m.group(5);
			return "urn:epc:idpat:itip:" + companyPrefix + "." + indicator + itemRef + "." + piece + "." + total + ".*";
		}
	}

	public static String toDL(String epc) throws ValidationException {
		Matcher m = getSerialElectronicProductCodeMatcher(epc);
		if (m == null) {
			m = getClassElectronicProductCodeMatcher(epc);
			if (m == null) {
				m = getSerialDigitalLinkMatcher(epc);
				if (m == null) {
					m = getClassDigitalLinkMatcher(epc);
					if (m == null) {
						throw new ValidationException("Illegal ITIP");
					}
					return epc;
				}
				return epc;
			}
		}

		String companyPrefix = m.group(1);
		String indicator = m.group(2);
		String itemRef = m.group(3);
		String checkDigit = retrieveCheckDigit(indicator + companyPrefix + itemRef);
		String piece = m.group(4);
		String total = m.group(5);
		String serialNumber = null;
		try {
			serialNumber = m.group(6);
		} catch (IndexOutOfBoundsException e) {

		}
		if (serialNumber == null) {
			return "https://id.gs1.org/8006/" + indicator + companyPrefix + itemRef + checkDigit + piece + total;
		} else {
			return "https://id.gs1.org/8006/" + indicator + companyPrefix + itemRef + checkDigit + piece + total
					+ "/21/" + serialNumber;
		}
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("epc", epc);
		obj.put("dl", dl);
		obj.put("companyPrefix", companyPrefix);
		obj.put("indicator", indicator);
		obj.put("itemRef", itemRef);
		obj.put("checkDigit", checkDigit);
		obj.put("piece", piece);
		obj.put("total", total);
		if (serialNumber != null && !serialNumber.equals("*")) {
			obj.put("serialNumber", serialNumber);
			obj.put("granularity", "instance");
		} else {
			obj.put("granularity", "class");
		}
		obj.put("isLicensedCompanyPrefix", isLicensedCompanyPrefix);
		obj.put("type", "ITIP");
		return obj;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
