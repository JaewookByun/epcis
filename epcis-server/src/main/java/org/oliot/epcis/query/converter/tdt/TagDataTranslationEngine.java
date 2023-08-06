package org.oliot.epcis.query.converter.tdt;

import java.util.HashMap;

import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.model.cbv.Comp;
import org.oliot.epcis.resource.StaticResource;

import io.vertx.core.json.JsonObject;

public class TagDataTranslationEngine {
	static final String[] ADIVarList = new String[] { "^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.\\.([0-9A-Z/-]{1,30})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.([0-9A-Z/-]{1,32})\\.([0-9A-Z/-]{1,30})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.\\.(#[0-9A-Z/-]{1,29})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.([0-9A-Z/-]{1,32})\\.(#[0-9A-Z/-]{1,29})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.\\.([0-9A-Z/-]{1,30})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.([0-9A-Z/-]{1,32})\\.([0-9A-Z/-]{1,30})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.\\.(#[0-9A-Z/-]{1,29})$",
			"^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.([0-9A-Z/-]{1,32})\\.(#[0-9A-Z/-]{1,29})$" };

	static final String[] GDTIList = new String[] { "^urn:epc:id:gdti:([0-9]{12})\\.([0-9]{0})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{11})\\.([0-9]{1})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{10})\\.([0-9]{2})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{9})\\.([0-9]{3})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{8})\\.([0-9]{4})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{7})\\.([0-9]{5})\\.([0-9]{0,17})$",
			"^urn:epc:id:gdti:([0-9]{6})\\.([0-9]{6})\\.([0-9]{0,17})$" };

	static final String[] cGDTIList = new String[] { "^urn:epc:idpat:gdti:([0-9]{12})\\.([0-9]{0})\\.\\*$",
			"^urn:epc:idpat:gdti:([0-9]{11})\\.([0-9]{1})\\.\\*$",
			"^urn:epc:idpat:gdti:([0-9]{10})\\.([0-9]{2})\\.\\*$", "^urn:epc:idpat:gdti:([0-9]{9})\\.([0-9]{3})\\.\\*$",
			"^urn:epc:idpat:gdti:([0-9]{8})\\.([0-9]{4})\\.\\*$", "^urn:epc:idpat:gdti:([0-9]{7})\\.([0-9]{5})\\.\\*$",
			"^urn:epc:idpat:gdti:([0-9]{6})\\.([0-9]{6})\\.\\*$" };

	static final String[] GIAIList = new String[] { "^urn:epc:id:giai:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})$",
			"^urn:epc:id:giai:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})$",
			"^urn:epc:id:giai:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:giai:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})$",
			"^urn:epc:id:giai:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})$",
			"^urn:epc:id:giai:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})$",
			"^urn:epc:id:giai:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})$" };

	static final String GID = "^urn:epc:id:gid:([0-9]{1,9})\\.([0-9]{1,8})\\.([0-9]{1,11})$";

	static final String[] GRAIList = new String[] { "^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,6})$",
			"^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,16})$",
			"^urn:epc:id:grai:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,16})$" };

	static final String[] cGRAIList = new String[] { "^urn:epc:idpat:grai:([0-9]{12})\\.([0-9]{0})\\.\\*",
			"^urn:epc:idpat:grai:([0-9]{11})\\.([0-9]{1})\\.\\*$",
			"^urn:epc:idpat:grai:([0-9]{10})\\.([0-9]{2})\\.\\*$", "^urn:epc:idpat:grai:([0-9]{9})\\.([0-9]{3})\\.\\*$",
			"^urn:epc:idpat:grai:([0-9]{8})\\.([0-9]{4})\\.\\*$", "^urn:epc:idpat:grai:([0-9]{7})\\.([0-9]{5})\\.\\*$",
			"^urn:epc:idpat:grai:([0-9]{6})\\.([0-9]{6})\\.\\*$" };

	static final String[] GSRNList = new String[] { "^urn:epc:id:gsrn:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:gsrn:([0-9]{11})\\.([0-9]{6})$", "^urn:epc:id:gsrn:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:gsrn:([0-9]{9})\\.([0-9]{8})$", "^urn:epc:id:gsrn:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:gsrn:([0-9]{7})\\.([0-9]{10})$", "^urn:epc:id:gsrn:([0-9]{6})\\.([0-9]{11})$" };

	static final String[] GSRNPList = new String[] { "^urn:epc:id:gsrnp:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:gsrnp:([0-9]{11})\\.([0-9]{6})$", "^urn:epc:id:gsrnp:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:gsrnp:([0-9]{9})\\.([0-9]{8})$", "^urn:epc:id:gsrnp:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:gsrnp:([0-9]{7})\\.([0-9]{10})$", "^urn:epc:id:gsrnp:([0-9]{6})\\.([0-9]{11})$" };

	static final String[] SGLNList = new String[] {
			"^urn:epc:id:sgln:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgln:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	static final String[] PGLNList = new String[] { "^urn:epc:id:pgln:([0-9]{12})\\.([0-9]{0})$",
			"^urn:epc:id:pgln:([0-9]{11})\\.([0-9]{1})$", "^urn:epc:id:pgln:([0-9]{10})\\.([0-9]{2})$",
			"^urn:epc:id:pgln:([0-9]{9})\\.([0-9]{3})$", "^urn:epc:id:pgln:([0-9]{8})\\.([0-9]{4})$",
			"^urn:epc:id:pgln:([0-9]{7})\\.([0-9]{5})$", "^urn:epc:id:pgln:([0-9]{6})\\.([0-9]{6})$" };

	static final String[] SGTINList = new String[] {
			"^urn:epc:id:sgtin:([0-9]{12})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{11})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{10})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{9})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{8})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{7})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:id:sgtin:([0-9]{6})\\.([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	static final String[] cGTINList = new String[] {
			"^urn:epc:idpat:sgtin:([0-9]{12})\\.(\\*|[0-9]{1})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{11})\\.(\\*|[0-9]{2})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{10})\\.(\\*|[0-9]{3})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{9})\\.(\\*|[0-9]{4})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{8})\\.(\\*|[0-9]{5})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{7})\\.(\\*|[0-9]{6})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:sgtin:([0-9]{6})\\.(\\*|[0-9]{7})\\.(\\*|[!%-?A-Z_a-z\\x22]{1,20})$" };

	static final String[] LGTINList = new String[] {
			"^urn:epc:class:lgtin:([0-9]{12})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{11})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{10})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{9})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{8})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{7})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:class:lgtin:([0-9]{6})\\.([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	static final String[] UPUIList = new String[] {
			"^urn:epc:id:upui:([0-9]{12})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{11})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{10})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{9})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{8})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{7})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,28})$",
			"^urn:epc:id:upui:([0-9]{6})\\.([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,28})$" };

	static final String[] SGCNList = new String[] {
			"^urn:epc:id:sgcn:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{0,12})$",
			"^urn:epc:id:sgcn:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{0,12})$" };

	static final String[] cSGCNList = new String[] { "^urn:epc:idpat:sgcn:([0-9]{12})\\.([0-9]{0})\\.\\*$",
			"^urn:epc:idpat:sgcn:([0-9]{11})\\.([0-9]{1})\\.\\*$",
			"^urn:epc:idpat:sgcn:([0-9]{10})\\.([0-9]{2})\\.\\*$", "^urn:epc:idpat:sgcn:([0-9]{9})\\.([0-9]{3})\\.\\*$",
			"^urn:epc:idpat:sgcn:([0-9]{8})\\.([0-9]{4})\\.\\*$", "^urn:epc:idpat:sgcn:([0-9]{7})\\.([0-9]{5})\\.\\*$",
			"^urn:epc:idpat:sgcn:([0-9]{6})\\.([0-9]{6})\\.\\*$" };

	static final String[] CPIList = new String[] {
			"^urn:epc:id:cpi:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})\\.([!%-?A-Z_a-z\\x22]{1,12})$",
			"^urn:epc:id:cpi:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})\\.([!%-?A-Z_a-z\\x22]{1,12})$" };

	static final String[] cCPIList = new String[] {
			"^urn:epc:idpat:cpi:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})\\.\\*$",
			"^urn:epc:idpat:cpi:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})\\.\\*$" };

	static final String[] GSINList = new String[] { "^urn:epc:id:gsin:([0-9]{12})\\.([0-9]{4})$",
			"^urn:epc:id:gsin:([0-9]{11})\\.([0-9]{5})$", "^urn:epc:id:gsin:([0-9]{10})\\.([0-9]{6})$",
			"^urn:epc:id:gsin:([0-9]{9})\\.([0-9]{7})$", "^urn:epc:id:gsin:([0-9]{8})\\.([0-9]{8})$",
			"^urn:epc:id:gsin:([0-9]{7})\\.([0-9]{9})$", "^urn:epc:id:gsin:([0-9]{6})\\.([0-9]{10})$" };

	static final String[] GINCList = new String[] { "^urn:epc:id:ginc:([0-9]{12})\\.([0-9]{1,18})$",
			"^urn:epc:id:ginc:([0-9]{11})\\.([0-9]{1,19})$", "^urn:epc:id:ginc:([0-9]{10})\\.([0-9]{1,20})$",
			"^urn:epc:id:ginc:([0-9]{9})\\.([0-9]{1,21})$", "^urn:epc:id:ginc:([0-9]{8})\\.([0-9]{1,22})$",
			"^urn:epc:id:ginc:([0-9]{7})\\.([0-9]{1,23})$", "^urn:epc:id:ginc:([0-9]{6})\\.([0-9]{1,24})$" };

	static final String[] SSCCList = new String[] { "^urn:epc:id:sscc:([0-9]{12})\\.([0-9]{5})$",
			"^urn:epc:id:sscc:([0-9]{11})\\.([0-9]{6})$", "^urn:epc:id:sscc:([0-9]{10})\\.([0-9]{7})$",
			"^urn:epc:id:sscc:([0-9]{9})\\.([0-9]{8})$", "^urn:epc:id:sscc:([0-9]{8})\\.([0-9]{9})$",
			"^urn:epc:id:sscc:([0-9]{7})\\.([0-9]{10})$", "^urn:epc:id:sscc:([0-9]{6})\\.([0-9]{11})$",

			"^urn:epc:id:sscc:([0-9]{12})\\.([0-9]{5})$", "^urn:epc:id:sscc:([0-9]{11})\\.([0-9]{6})$",
			"^urn:epc:id:sscc:([0-9]{10})\\.([0-9]{7})$", "^urn:epc:id:sscc:([0-9]{9})\\.([0-9]{8})$",
			"^urn:epc:id:sscc:([0-9]{8})\\.([0-9]{9})$", "^urn:epc:id:sscc:([0-9]{7})\\.([0-9]{10})$",
			"^urn:epc:id:sscc:([0-9]{6})\\.([0-9]{11})$" };

	static final String[] USDODList = new String[] { "^urn:epc:id:usdod:([0-9A-HJ-NP-Z]{5})\\.([0-9]{1,8})$",

			"^urn:epc:id:usdod:([0-9A-HJ-NP-Z]{5,6})\\.([0-9]{1,11})$" };

	static final String[] ITIPList = new String[] {
			"^urn:epc:idpat:itip:([0-9]{12})\\.([0-9]{1})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:itip:([0-9]{11})\\.([0-9]{2})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:itip:([0-9]{10})\\.([0-9]{3})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:itip:([0-9]{9})\\.([0-9]{4})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:itip:([0-9]{8})\\.([0-9]{5})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:itip:([0-9]{7})\\.([0-9]{6})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$",
			"^urn:epc:idpat:itip:([0-9]{6})\\.([0-9]{7})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$" };

	static final String BIC = "^urn:epc:id:bic:([A-Z]{3})(J,U,Z]{1})([0-9]{6})([0-9]{1})$";

	static final String IMOVN = "^urn:epc:id:imovn:([0-9]{7})$";

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
				if (epcString.matches(SGTINList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:sscc")) {
			for (int i = 0; i < SSCCList.length; i++) {
				if (epcString.matches(SSCCList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:sgln")) {
			for (int i = 0; i < SGLNList.length; i++) {
				if (epcString.matches(SGLNList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:grai")) {
			for (int i = 0; i < GRAIList.length; i++) {
				if (epcString.matches(GRAIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:giai")) {
			for (int i = 0; i < GIAIList.length; i++) {
				if (epcString.matches(GIAIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsrn")) {
			for (int i = 0; i < GSRNList.length; i++) {
				if (epcString.matches(GSRNList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsrnp")) {
			for (int i = 0; i < GSRNPList.length; i++) {
				if (epcString.matches(GSRNPList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gdti")) {
			for (int i = 0; i < GDTIList.length; i++) {
				if (epcString.matches(GDTIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:cpi")) {
			for (int i = 0; i < CPIList.length; i++) {
				if (epcString.matches(CPIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:sgcn")) {
			for (int i = 0; i < SGCNList.length; i++) {
				if (epcString.matches(SGCNList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:ginc")) {
			for (int i = 0; i < GINCList.length; i++) {
				if (epcString.matches(GINCList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsin")) {
			for (int i = 0; i < GSINList.length; i++) {
				if (epcString.matches(GSINList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:upui")) {
			for (int i = 0; i < UPUIList.length; i++) {
				if (epcString.matches(UPUIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("^urn:epc:id:pgln")) {
			for (int i = 0; i < PGLNList.length; i++) {
				if (epcString.matches(PGLNList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gid")) {
			if (epcString.matches(GID)) {
				return;
			}
		} else if (epcString.startsWith("urn:epc:id:usdod")) {
			for (int i = 0; i < USDODList.length; i++) {
				if (epcString.matches(USDODList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:adi")) {
			for (int i = 0; i < ADIVarList.length; i++) {
				if (epcString.matches(ADIVarList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:bic")) {
			if (epcString.matches(BIC)) {
				return;
			}
		} else if (epcString.startsWith("urn:epc:id:imovn")) {
			if (epcString.matches(IMOVN)) {
				return;
			}
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
			for (int i = 0; i < cGTINList.length; i++) {
				if (epcString.matches(cGTINList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:class:lgtin")) {
			for (int i = 0; i < LGTINList.length; i++) {
				if (epcString.matches(LGTINList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:grai")) {
			for (int i = 0; i < cGRAIList.length; i++) {
				if (epcString.matches(cGRAIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:gdti")) {
			for (int i = 0; i < cGDTIList.length; i++) {
				if (epcString.matches(cGDTIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:sgcn")) {
			for (int i = 0; i < cSGCNList.length; i++) {
				if (epcString.matches(cSGCNList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:cpi")) {
			for (int i = 0; i < cCPIList.length; i++) {
				if (epcString.matches(cCPIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:idpat:itip")) {
			for (int i = 0; i < ITIPList.length; i++) {
				if (epcString.matches(ITIPList[i])) {
					return;
				}
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
				if (epcString.matches(GDTIList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:gsrn")) {
			for (int i = 0; i < GSRNList.length; i++) {
				if (epcString.matches(GSRNList[i])) {
					return;
				}
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
				if (epcString.matches(SGLNList[i])) {
					return;
				}
			}
		} else if (epcString.startsWith("urn:epc:id:pgln")) {
			for (int i = 0; i < PGLNList.length; i++) {
				if (epcString.matches(PGLNList[i])) {
					return;
				}
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
				if (epcString.matches(PGLNList[i])) {
					return;
				}
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
				if (epcString.matches(GDTIList[i])) {
					return;
				}
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
				if (epcString.matches(SGLNList[i])) {
					return;
				}
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
			if (_01back.length() == 0)
				return false;

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
			if (_01back.length() == 0)
				throw new ValidationException("");

			gcpLength = gcpLengthList.get(_01back);

			if (gcpLength != null)
				break;

			_01back = _01back.substring(0, _01back.length() - 1);
		}
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

	public static JsonObject parseEPC(String epc, IdentifierType type) throws IllegalArgumentException {
		if (type == IdentifierType.GTIN) {
			GlobalTradeItemNumber gtin = new GlobalTradeItemNumber(StaticResource.gcpLength, epc);
			return gtin.toJson().put("type", "GTIN");
		} else if (type == IdentifierType.SSCC) {
			SerialShippingContainerCode sscc = new SerialShippingContainerCode(StaticResource.gcpLength, epc);
			return sscc.toJson().put("type", "SSCC");
		} else if( type == IdentifierType.SGLN) {
			GlobalLocationNumber gln = new GlobalLocationNumber(StaticResource.gcpLength, epc);
			return gln.toJson().put("type", "SGLN");
		}
		return null;
	}
}
