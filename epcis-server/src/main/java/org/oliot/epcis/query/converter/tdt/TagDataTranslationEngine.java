package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.Comp;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;
import static org.oliot.epcis.resource.EPCPatterns.*;

public class TagDataTranslationEngine {
	public static void checkEPCPureIdentity(HashMap<String, Integer> gcpLengthList, String epcString)
			throws ValidationException {
		if (!epcString.startsWith("urn:epc:id:")) {
			throw new ValidationException("Pure Identity EPC should start with urn:epc:id:");
		} else if (!epcString.startsWith("urn:epc:id:gid") && !epcString.startsWith("urn:epc:id:usdod")
				&& !epcString.startsWith("urn:epc:id:adi") && !epcString.startsWith("urn:epc:id:bic")
				&& !epcString.startsWith("urn:epc:id:imovn")) {
			try {
				String[] colonArr = epcString.split(":");
				String gcp = colonArr[colonArr.length - 1].split("\\.")[0];
				if (!isGlobalCompanyPrefix(gcpLengthList, gcp)) {
					throw new ValidationException("Unlicensed global company prefix number : " + gcp);
				}
			} catch (Exception e) {
				throw new ValidationException(e.getMessage());
			}
		}

		if (epcString.startsWith("urn:epc:id:sgtin")) {
			for (int i = 0; i < SGTINList.length; i++) {
				if (SGTINList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:sscc")) {
			for (int i = 0; i < SSCCList.length; i++) {
				if (SSCCList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:sgln")) {
			for (int i = 0; i < SGLNList.length; i++) {
				if (SGLNList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:grai")) {
			for (int i = 0; i < GRAIList.length; i++) {
				if (GRAIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:giai")) {
			for (int i = 0; i < GIAIList.length; i++) {
				if (GIAIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:gsrn")) {
			for (int i = 0; i < GSRNList.length; i++) {
				if (GSRNList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:gsrnp")) {
			for (int i = 0; i < GSRNPList.length; i++) {
				if (GSRNPList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:gdti")) {
			for (int i = 0; i < GDTIList.length; i++) {
				if (GDTIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:cpi")) {
			for (int i = 0; i < CPIList.length; i++) {
				if (CPIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:sgcn")) {
			for (int i = 0; i < SGCNList.length; i++) {
				if (SGCNList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:ginc")) {
			for (int i = 0; i < GINCList.length; i++) {
				if (GINCList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:gsin")) {
			for (int i = 0; i < GSINList.length; i++) {
				if (GSINList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:upui")) {
			for (int i = 0; i < UPUIList.length; i++) {
				if (UPUIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("^urn:epc:id:pgln")) {
			for (int i = 0; i < PGLNList.length; i++) {
				if (PGLNList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:gid")) {
			if (GID.matcher(epcString).find())
				return;
		} else if (epcString.startsWith("urn:epc:id:usdod")) {
			for (int i = 0; i < USDODList.length; i++) {
				if (USDODList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:adi")) {
			for (int i = 0; i < ADIVarList.length; i++) {
				if (ADIVarList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:bic")) {
			if (BIC.matcher(epcString).find())
				return;
		} else if (epcString.startsWith("urn:epc:id:imovn")) {
			if (IMOVN.matcher(epcString).find())
				return;
		}
		throw new ValidationException(epcString + " should comply with EPC Pure Identity format");
	}

	public static void checkEPCClassPureIdentity(HashMap<String, Integer> gcpLengthList, String epcString)
			throws ValidationException {
		try {
			String[] colonArr = epcString.split(":");
			String gcp = colonArr[colonArr.length - 1].split("\\.")[0];
			if (!isGlobalCompanyPrefix(gcpLengthList, gcp)) {
				throw new ValidationException("Unlicensed global company prefix number : " + gcp);
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

		// Class-level object identifier (8.3.1, CBV)
		if (epcString.startsWith("urn:epc:idpat:sgtin")) {
			for (int i = 0; i < GTINList.length; i++) {
				if (GTINList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:class:lgtin")) {
			for (int i = 0; i < LGTINList.length; i++) {
				if (LGTINList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:idpat:grai")) {
			for (int i = 0; i < cGRAIList.length; i++) {
				if (cGRAIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:idpat:gdti")) {
			for (int i = 0; i < cGDTIList.length; i++) {
				if (cGDTIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:idpat:sgcn")) {
			for (int i = 0; i < cSGCNList.length; i++) {
				if (cSGCNList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:idpat:cpi")) {
			for (int i = 0; i < cCPIList.length; i++) {
				if (cCPIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:idpat:itip")) {
			for (int i = 0; i < ITIPList.length; i++) {
				if (ITIPList[i].matcher(epcString).find())
					return;
			}
		}

		throw new ValidationException(epcString
				+ " should comply with pure identity class-level object identifier format. See. CBV v2.0.0 section 8.3.1 (SGTIN(idpat), LGTIN, GRAI(idpat), GDTI(idpat), SGCN(idpat), CPI(idpat), ITIP(idpat)."
				+ ").");
	}

	public static void checkBusinessTransactionEPCPureIdentity(HashMap<String, Integer> gcpLengthList, String epcString)
			throws ValidationException {
		try {
			String[] colonArr = epcString.split(":");
			String gcp = colonArr[colonArr.length - 1].split("\\.")[0];
			if (!isGlobalCompanyPrefix(gcpLengthList, gcp)) {
				throw new ValidationException("Unlicensed global company prefix number : " + gcp);
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

		if (epcString.startsWith("urn:epc:id:gdti")) {
			for (int i = 0; i < GDTIList.length; i++) {
				if (GDTIList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:gsrn")) {
			for (int i = 0; i < GSRNList.length; i++) {
				if (GSRNList[i].matcher(epcString).find())
					return;
			}
		}
		throw new ValidationException(
				epcString + " should comply with pure identity business transaction identifier format (GDTI, GSRN).");
	}

	public static void checkSourceDestinationEPCPureIdentity(HashMap<String, Integer> gcpLengthList, String epcString)
			throws ValidationException {
		try {
			String[] colonArr = epcString.split(":");
			String gcp = colonArr[colonArr.length - 1].split("\\.")[0];
			if (!isGlobalCompanyPrefix(gcpLengthList, gcp)) {
				throw new ValidationException("Unlicensed global company prefix number : " + gcp);
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

		if (epcString.startsWith("urn:epc:id:sgln")) {
			for (int i = 0; i < SGLNList.length; i++) {
				if (SGLNList[i].matcher(epcString).find())
					return;
			}
		} else if (epcString.startsWith("urn:epc:id:pgln")) {
			for (int i = 0; i < PGLNList.length; i++) {
				if (PGLNList[i].matcher(epcString).find())
					return;
			}
		}
		throw new ValidationException(
				epcString + " should comply with pure identity source destination identifier format (SGLN, PGLN).");
	}

	public static void checkPartyEPCPureIdentity(HashMap<String, Integer> gcpLengthList, String epcString)
			throws ValidationException {
		try {
			String[] colonArr = epcString.split(":");
			String gcp = colonArr[colonArr.length - 1].split("\\.")[0];
			if (!isGlobalCompanyPrefix(gcpLengthList, gcp)) {
				throw new ValidationException("Unlicensed global company prefix number : " + gcp);
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

		if (epcString.startsWith("urn:epc:id:pgln")) {
			for (int i = 0; i < PGLNList.length; i++) {
				if (PGLNList[i].matcher(epcString).find())
					return;
			}
		}
		throw new ValidationException(epcString + " should comply with pure identity party identifier format (PGLN).");
	}

	public static void checkDocumentEPCPureIdentity(HashMap<String, Integer> gcpLengthList, String epcString)
			throws ValidationException {
		try {
			String[] colonArr = epcString.split(":");
			String gcp = colonArr[colonArr.length - 1].split("\\.")[0];
			if (!isGlobalCompanyPrefix(gcpLengthList, gcp)) {
				throw new ValidationException("Unlicensed global company prefix number : " + gcp);
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

		if (epcString.startsWith("urn:epc:id:gdti")) {
			for (int i = 0; i < GDTIList.length; i++) {
				if (GDTIList[i].matcher(epcString).find())
					return;
			}
		}
		throw new ValidationException(
				epcString + " should comply with pure identity transformation identifier format (GDTI).");
	}

	public static void checkLocationEPCPureIdentity(HashMap<String, Integer> gcpLengthList, String epcString)
			throws ValidationException {
		try {
			String[] colonArr = epcString.split(":");
			String gcp = colonArr[colonArr.length - 1].split("\\.")[0];
			if (!isGlobalCompanyPrefix(gcpLengthList, gcp)) {
				throw new ValidationException("Unlicensed global company prefix number : " + gcp);
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

		if (epcString.startsWith("urn:epc:id:sgln")) {
			for (int i = 0; i < SGLNList.length; i++) {
				if (SGLNList[i].matcher(epcString).find())
					return;
			}
		}
		throw new ValidationException(
				epcString + " should comply with pure identity location identifier format (SGLN).");
	}

	public static void checkMicroorganismValue(String microorganism) throws ValidationException {
		String regex = "^https://www.ncbi.nlm.nih.gov/taxonomy/([!%-?A-Z_a-z\\x22]+)$";
		if (!microorganism.matches(regex)) {
			ValidationException e = new ValidationException(microorganism + " should comply with microorganism format");
			throw e;
		}
	}

	public static void checkChemicalSubstance(String chemicalSubstance) throws ValidationException {
		String regex = "^https://identifiers.org/inchikey:([!%-?A-Z_a-z\\x22]{1,27})$";
		if (!chemicalSubstance.matches(regex)) {
			ValidationException e = new ValidationException(
					chemicalSubstance + " should comply with chemical substance format");
			throw e;
		}
	}

	public static void checkComponent(String comp) throws ValidationException {
		if (!Comp.values.contains(comp)) {
			ValidationException e = new ValidationException(
					comp + " should be one of cbv:Comp* listed in CBV v2.0 Section 7.8.3.");
			throw e;
		}
	}

	public static boolean isGlobalCompanyPrefix(HashMap<String, Integer> gcpLengthList, String gcp) {
		Integer gcpLength = null;
		String _01back = gcp;
		while (!_01back.equals("")) {
			gcpLength = gcpLengthList.get(_01back);
			if (gcpLength != null) {
				return true;
			}
			_01back = _01back.substring(0, _01back.length() - 1);
		}
		return false;
	}

	public static int getGCPLength(HashMap<String, Integer> gcpLengthList, String code) throws ValidationException {
		Integer gcpLength = null;
		String _01back = code;
		while (!_01back.equals("")) {
			gcpLength = gcpLengthList.get(_01back);
			if (gcpLength != null)
				break;
			_01back = _01back.substring(0, _01back.length() - 1);
		}
		if (gcpLength == null)
			throw new ValidationException("Non-licensed GCP");
		else
			return gcpLength;
	}

	public static String getDL(String epc) {
		// https://id.gs1.org/01/70614141123451/21/2017
		// urn:epc:id:sgtin:0614141.712345.2017
		// https://id.gs1.org/01/70614141123451/10/998877
		// urn:epc:class:lgtin:0614141.712345.998877
		// urn:epc:idpat:sgtin:4012345.066666.*

		if (epc.startsWith("urn:epc:id:sgtin:")) {
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			if (numArr.length != 3)
				return epc;
			String n01 = numArr[1].substring(0, 1) + numArr[0] + numArr[1].substring(1, numArr[1].length());
			n01 = appendGtinCheckDigit(n01);
			return "https://id.gs1.org/01/" + n01 + "/21/" + numArr[2];
		} else if (epc.startsWith("urn:epc:class:lgtin:")) {
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			if (numArr.length != 3)
				return epc;
			String n01 = numArr[1].substring(0, 1) + numArr[0] + numArr[1].substring(1, numArr[1].length());
			n01 = appendGtinCheckDigit(n01);
			return "https://id.gs1.org/01/" + n01 + "/10/" + numArr[2];
		} else if (epc.startsWith("urn:epc:idpat:sgtin:")) {
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			if (numArr.length != 3)
				return epc;
			String n01 = numArr[1].substring(0, 1) + numArr[0] + numArr[1].substring(1, numArr[1].length());
			n01 = appendGtinCheckDigit(n01);
			return "https://id.gs1.org/01/" + n01;
		} else if (epc.startsWith("urn:epc:id:sscc:")) {
			String[] elemArr = epc.split(":");
			String last = elemArr[elemArr.length - 1];
			String[] numArr = last.split("\\.");
			if (numArr.length != 2)
				return epc;
			String n01 = numArr[1].substring(0, 1) + numArr[0] + numArr[1].substring(1, numArr[1].length());
			n01 = appendSSCCCheckDigit(n01);
			return "https://id.gs1.org/00/" + n01;
		}
		return epc;
	}

	public static String appendGtinCheckDigit(String gtin) {
		if (gtin.length() != 13) {
			return null;
		}
		int[] e = toIntArray(gtin);

		for (int i = 0; i < gtin.length(); i++) {
			e[i] = Integer.parseInt(gtin.charAt(i) + "");
		}

		int correctCheckDigit = (10
				- ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12]) + e[1] + e[3] + e[5] + e[7] + e[9] + e[11])
						% 10))
				% 10;

		return gtin + correctCheckDigit;
	}

	public static String appendSSCCCheckDigit(String sscc) {
		if (sscc.length() != 17) {
			return null;
		}
		int[] e = toIntArray(sscc);

		for (int i = 0; i < sscc.length(); i++) {
			e[i] = Integer.parseInt(sscc.charAt(i) + "");
		}

		int correctCheckDigit = (10 - ((3 * (e[0] + e[2] + e[4] + e[6] + e[8] + e[10] + e[12] + e[14] + e[16]) + e[1]
				+ e[3] + e[5] + e[7] + e[9] + e[11] + e[13] + e[15]) % 10)) % 10;

		return sscc + correctCheckDigit;
	}

	public static int[] toIntArray(String str) {
		int[] e = new int[str.length()];

		for (int i = 0; i < str.length(); i++) {
			e[i] = Integer.parseInt(str.charAt(i) + "");
		}
		return e;
	}

	private static IdentifierType getEPCType(String epcString) {
		if (epcString.startsWith("urn:epc:id:adi")) {
			return IdentifierType.ADI;
		} else if (epcString.startsWith("urn:epc:id:gdti")) {
			return IdentifierType.GDTI;
		} else if (epcString.startsWith("urn:epc:idpat:gdti")) {
			return IdentifierType.GDTI;
		} else if (epcString.startsWith("urn:epc:id:giai")) {
			return IdentifierType.GIAI;
		} else if (epcString.startsWith("urn:epc:id:gid")) {
			return IdentifierType.GID;
		} else if (epcString.startsWith("urn:epc:id:grai")) {
			return IdentifierType.GRAI;
		} else if (epcString.startsWith("urn:epc:id:gsrnp")) {
			return IdentifierType.GSRNP;
		} else if (epcString.startsWith("urn:epc:id:gsrn")) {
			return IdentifierType.GSRN;
		} else if (epcString.startsWith("urn:epc:id:sgln")) {
			return IdentifierType.SGLN;
		} else if (epcString.startsWith("urn:epc:id:pgln")) {
			return IdentifierType.PGLN;
		} else if (epcString.startsWith("urn:epc:id:sgtin")) {
			return IdentifierType.SGTIN;
		} else if (epcString.startsWith("urn:epc:idpat:sgtin")) {
			return IdentifierType.GTIN;
		} else if (epcString.startsWith("urn:epc:class:lgtin")) {
			return IdentifierType.LGTIN;
		} else if (epcString.startsWith("urn:epc:id:upui")) {
			return IdentifierType.UPUI;
		} else if (epcString.startsWith("urn:epc:id:sgcn")) {
			return IdentifierType.SGCN;
		} else if (epcString.startsWith("urn:epc:idpat:sgcn")) {
			return IdentifierType.SGCN;
		} else if (epcString.startsWith("urn:epc:id:cpi")) {
			return IdentifierType.CPI;
		} else if (epcString.startsWith("urn:epc:idpat:cpi")) {
			return IdentifierType.CPI;
		} else if (epcString.startsWith("urn:epc:id:gsin")) {
			return IdentifierType.GSIN;
		} else if (epcString.startsWith("urn:epc:id:ginc")) {
			return IdentifierType.GINC;
		} else if (epcString.startsWith("urn:epc:id:sscc")) {
			return IdentifierType.SSCC;
		} else if (epcString.startsWith("urn:epc:id:usdod")) {
			return IdentifierType.USDOD;
		} else if (epcString.startsWith("urn:epc:idpat:itip")) {
			return IdentifierType.ITIP;
		} else if (epcString.startsWith("urn:epc:id:bic")) {
			return IdentifierType.BIC;
		} else if (epcString.startsWith("urn:epc:id:imovn")) {
			return IdentifierType.IMOVN;
		}
		return null;
	}

	private static IdentifierType getDLType(String dl) {
		if (dl.startsWith("urn:epc:id:adi")) {
			return IdentifierType.ADI;
		} else if (dl.contains("/253/")) {
			return IdentifierType.GDTI;
		} else if (dl.contains("/8004/")) {
			return IdentifierType.GIAI;
		} else if (dl.startsWith("urn:epc:id:gid")) {
			return IdentifierType.GID;
		} else if (dl.contains("/8003/")) {
			return IdentifierType.GRAI;
		} else if (dl.contains("/8018/")) {
			return IdentifierType.GSRN;
		} else if (dl.contains("/8017/")) {
			return IdentifierType.GSRNP;
		} else if (dl.contains("/414/")) {
			return IdentifierType.SGLN;
		} else if (dl.contains("/417/")) {
			return IdentifierType.PGLN;
		} else if (dl.contains("/01/") && !dl.contains("/21/") && !dl.contains("/10/")) {
			return IdentifierType.GTIN;
		} else if (dl.contains("/01/") && dl.contains("/21/")) {
			return IdentifierType.SGTIN;
		} else if (dl.contains("/01/") && dl.contains("/10/")) {
			return IdentifierType.LGTIN;
		} else if (dl.startsWith("urn:epc:id:upui")) {
			return IdentifierType.UPUI;
		} else if (dl.startsWith("urn:epc:id:sgcn")) {
			return IdentifierType.SGCN;
		} else if (dl.startsWith("urn:epc:idpat:sgcn")) {
			return IdentifierType.SGCN;
		} else if (dl.contains("/8010/")) {
			return IdentifierType.CPI;
		} else if (dl.startsWith("urn:epc:id:gsin")) {
			return IdentifierType.GSIN;
		} else if (dl.startsWith("urn:epc:id:ginc")) {
			return IdentifierType.GINC;
		} else if (dl.contains("/00/")) {
			return IdentifierType.SSCC;
		} else if (dl.startsWith("urn:epc:id:usdod")) {
			return IdentifierType.USDOD;
		} else if (dl.startsWith("urn:epc:idpat:itip")) {
			return IdentifierType.ITIP;
		} else if (dl.startsWith("urn:epc:id:bic")) {
			return IdentifierType.BIC;
		} else if (dl.startsWith("urn:epc:id:imovn")) {
			return IdentifierType.IMOVN;
		}
		return null;
	}

	private static IdentifierType getClassLevelDLType(String dl) {
		if (dl.contains("/8010/")) {
			return IdentifierType.CPI;
		} else if (dl.startsWith("urn:epc:idpat:itip")) {
			return IdentifierType.ITIP;
		} else if (dl.startsWith("urn:epc:idpat:upui")) {
			return IdentifierType.UPUI;
		} else if (dl.contains("/01/") && !dl.contains("/21/") && !dl.contains("/10/")) {
			return IdentifierType.GTIN;
		} else if (dl.contains("/01/") && dl.contains("/10/") && !dl.contains("/21/")) {
			return IdentifierType.LGTIN;
		}
		return null;
	}

	private static IdentifierType getInstanceLevelDLType(String dl) {
		// EXCEPT LGTIN, GTIN, CPI, ITIP, UPUI
		if (dl.startsWith("urn:epc:id:adi")) {
			return IdentifierType.ADI;
		} else if (dl.contains("/253/")) {
			return IdentifierType.GDTI;
		} else if (dl.contains("/8004/")) {
			return IdentifierType.GIAI;
		} else if (dl.startsWith("urn:epc:id:gid")) {
			return IdentifierType.GID;
		} else if (dl.contains("/8003/")) {
			return IdentifierType.GRAI;
		} else if (dl.contains("/8018/")) {
			return IdentifierType.GSRN;
		} else if (dl.contains("/8017/")) {
			return IdentifierType.GSRNP;
		} else if (dl.contains("/414/")) {
			return IdentifierType.SGLN;
		} else if (dl.contains("/417/")) {
			return IdentifierType.PGLN;
		} else if (dl.contains("/01/") && dl.contains("/21/")) {
			return IdentifierType.SGTIN;
		} else if (dl.startsWith("urn:epc:id:upui")) {
			return IdentifierType.UPUI;
		} else if (dl.startsWith("urn:epc:id:sgcn")) {
			return IdentifierType.SGCN;
		} else if (dl.contains("/8010/")) {
			return IdentifierType.CPI;
		} else if (dl.startsWith("urn:epc:id:gsin")) {
			return IdentifierType.GSIN;
		} else if (dl.startsWith("urn:epc:id:ginc")) {
			return IdentifierType.GINC;
		} else if (dl.contains("/00/")) {
			return IdentifierType.SSCC;
		} else if (dl.startsWith("urn:epc:id:usdod")) {
			return IdentifierType.USDOD;
		} else if (dl.startsWith("urn:epc:id:bic")) {
			return IdentifierType.BIC;
		} else if (dl.startsWith("urn:epc:id:imovn")) {
			return IdentifierType.IMOVN;
		}
		return null;
	}

	public static String toEPC(String dl) throws ValidationException {
		IdentifierType type = getDLType(dl);
		if (type == null)
			type = getEPCType(dl);

		if (type == IdentifierType.GTIN) {
			return GlobalTradeItemNumber.toEPC(dl);
		} else if (type == IdentifierType.LGTIN) {
			return GlobalTradeItemNumberWithLot.toEPC(dl);
		} else if (type == IdentifierType.SGTIN) {
			return SerializedGlobalTradeItemNumber.toEPC(dl);
		} else if (type == IdentifierType.SSCC) {
			return SerialShippingContainerCode.toEPC(dl);
		} else if (type == IdentifierType.SGLN) {
			return GlobalLocationNumber.toEPC(dl);
		} else if (type == IdentifierType.GRAI) {
			return GlobalReturnableAssetIdentifier.toEPC(dl);
		} else if (type == IdentifierType.GIAI) {
			return GlobalIndividualAssetIdentifier.toEPC(dl);
		} else if (type == IdentifierType.GSRN) {
			return GlobalServiceRelationNumber.toEPC(dl);
		} else if (type == IdentifierType.GSRNP) {
			return GlobalServiceRelationNumberProvider.toEPC(dl);
		} else if (type == IdentifierType.GDTI) {
			return GlobalDocumentTypeIdentifier.toEPC(dl);
		} else if (type == IdentifierType.CPI) {
			return ComponentPartIdentifier.toEPC(dl);
		} else if (type == IdentifierType.PGLN) {
			return GlobalLocationNumberOfParty.toEPC(dl);
		} else
			throw new ValidationException("Unsupported code scheme");
	}

	// O 1613 SGTIN, LGTIN, GTIN
	// O 1758 SSCC
	// O 1784 SGLN
	// O 1835 GRAI
	// O 1880 GIAI
	// O 1912 GSRN
	// O 1939 GSRNP
	// O 1966 GDTI
	// O 2006 CPI
	// 2043 SGCN
	// 2075 GINC
	// 2107 GSIN
	// 2133 ITIP
	// 2173 UPUI
	// O 2217 PGLN

	public static String toClassLevelEPC(String dl) throws ValidationException {
		IdentifierType type = getClassLevelDLType(dl);
		if (type == IdentifierType.LGTIN) {
			return GlobalTradeItemNumberWithLot.toEPC(dl);
		} else if (type == IdentifierType.GTIN) {
			return GlobalTradeItemNumber.toEPC(dl);
		} else if (type == IdentifierType.CPI) {
			return ComponentPartIdentifier.toEPC(dl);
		} else if (type == IdentifierType.ITIP) {
			// TODO
			throw new ValidationException("To be supported");
		} else if (type == IdentifierType.UPUI) {
			// TODO
			throw new ValidationException("To be supported");
		} else
			throw new ValidationException("Unsupported class level code scheme");
	}

	public static String toInstanceLevelEPC(String instanceLevelDL) throws ValidationException {
		IdentifierType type = getInstanceLevelDLType(instanceLevelDL);
		if (type == IdentifierType.SGTIN) {
			return SerializedGlobalTradeItemNumber.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.SSCC) {
			return SerialShippingContainerCode.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.SGLN) {
			return GlobalLocationNumber.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.PGLN) {
			return GlobalLocationNumberOfParty.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.GRAI) {
			return GlobalReturnableAssetIdentifier.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.GIAI) {
			return GlobalIndividualAssetIdentifier.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.GSRN) {
			return GlobalServiceRelationNumber.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.GSRNP) {
			return GlobalServiceRelationNumberProvider.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.GSRNP) {
			return GlobalServiceRelationNumberProvider.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.GDTI) {
			return GlobalDocumentTypeIdentifier.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.CPI) {
			return GlobalLocationNumberOfParty.toEPC(instanceLevelDL);
		} else if (type == IdentifierType.PGLN) {
			return GlobalLocationNumberOfParty.toEPC(instanceLevelDL);
		} else
			throw new ValidationException("Unsupported instance level code scheme");
	}

	// O 1613 SGTIN
	// O 1758 SSCC
	// O 1784 SGLN
	// O 1835 GRAI
	// O 1880 GIAI
	// O 1912 GSRN
	// O 1939 GSRNP
	// O 1966 GDTI
	// O 2006 CPI
	// 2043 SGCN
	// 2075 GINC
	// 2107 GSIN
	// 2133 ITIP
	// 2173 UPUI
	// O 2217 PGLN

	public static JsonObject parse(String id) throws IllegalArgumentException, ValidationException {
		if (id.startsWith("urn:epc")) {
			IdentifierType type = getEPCType(id);
			if (type == IdentifierType.GTIN) {
				return new GlobalTradeItemNumber(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI).toJson();
			} else if (type == IdentifierType.LGTIN) {
				return new GlobalTradeItemNumberWithLot(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			} else if (type == IdentifierType.SGTIN) {
				return new SerializedGlobalTradeItemNumber(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			} else if (type == IdentifierType.SSCC) {
				return new SerialShippingContainerCode(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			} else if (type == IdentifierType.SGLN) {
				return new GlobalLocationNumber(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI).toJson();
			} else if (type == IdentifierType.GDTI) {
				return new GlobalDocumentTypeIdentifier(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			} else if (type == IdentifierType.PGLN) {
				return new GlobalLocationNumberOfParty(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			} else if (type == IdentifierType.GIAI) {
				return new GlobalIndividualAssetIdentifier(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			} else if (type == IdentifierType.GRAI) {
				return new GlobalReturnableAssetIdentifier(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			} else if (type == IdentifierType.GSRN) {
				return new GlobalServiceRelationNumber(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			} else if (type == IdentifierType.GSRNP) {
				return new GlobalServiceRelationNumberProvider(StaticResource.gcpLength, id,
						CodeScheme.EPCPureIdentitiyURI).toJson();
			} else if (type == IdentifierType.CPI) {
				return new ComponentPartIdentifier(StaticResource.gcpLength, id, CodeScheme.EPCPureIdentitiyURI)
						.toJson();
			}
		} else if (id.startsWith("https://id.gs1.org/")) {
			IdentifierType type = getDLType(id);
			if (type == IdentifierType.GTIN) {
				return new GlobalTradeItemNumber(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink).toJson();
			} else if (type == IdentifierType.LGTIN) {
				return new GlobalTradeItemNumberWithLot(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.SGTIN) {
				return new SerializedGlobalTradeItemNumber(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.SSCC) {
				return new SerialShippingContainerCode(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.SGLN) {
				return new GlobalLocationNumber(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink).toJson();
			} else if (type == IdentifierType.GDTI) {
				return new GlobalDocumentTypeIdentifier(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.PGLN) {
				return new GlobalLocationNumberOfParty(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.GIAI) {
				return new GlobalIndividualAssetIdentifier(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.GRAI) {
				return new GlobalReturnableAssetIdentifier(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.GSRN) {
				return new GlobalServiceRelationNumber(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.GSRNP) {
				return new GlobalServiceRelationNumberProvider(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink)
						.toJson();
			} else if (type == IdentifierType.CPI) {
				return new ComponentPartIdentifier(StaticResource.gcpLength, id, CodeScheme.GS1DigitalLink).toJson();
			}
		}

		// O 1613 SGTIN, LGTIN, GTIN
		// O 1758 SSCC
		// O 1784 SGLN
		// O 1835 GRAI
		// O 1880 GIAI
		// O 1912 GSRN
		// O 1939 GSRNP
		// O 1966 GDTI
		// O 2006 CPI
		// 2043 SGCN
		// 2075 GINC
		// 2107 GSIN
		// 2133 ITIP
		// 2173 UPUI
		// O 2217 PGLN

		return null;
	}

}
