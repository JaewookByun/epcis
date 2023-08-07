package org.oliot.epcis.resource;

import java.util.regex.Pattern;

public class DigitalLinkPatterns {
	public static final Pattern[] ADIVarList = new Pattern[] {
			Pattern.compile("^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.\\.([0-9A-Z/-]{1,30})$"),
			Pattern.compile("^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.([0-9A-Z/-]{1,32})\\.([0-9A-Z/-]{1,30})$"),
			Pattern.compile("^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.\\.(#[0-9A-Z/-]{1,29})$"),
			Pattern.compile("^urn:epc:id:adi:([0-9A-HJ-NP-Z]{5})\\.([0-9A-Z/-]{1,32})\\.(#[0-9A-Z/-]{1,29})$"),
			Pattern.compile("^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.\\.([0-9A-Z/-]{1,30})$"),
			Pattern.compile("^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.([0-9A-Z/-]{1,32})\\.([0-9A-Z/-]{1,30})$"),
			Pattern.compile("^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.\\.(#[0-9A-Z/-]{1,29})$"),
			Pattern.compile("^urn:epc:id:adi:([0-9A-HJ-NP-Z]{6})\\.([0-9A-Z/-]{1,32})\\.(#[0-9A-Z/-]{1,29})$") };

	public static final Pattern[] GDTIList = new Pattern[] {
			Pattern.compile("^urn:epc:id:gdti:([0-9]{12})\\.([0-9]{0})\\.([0-9]{0,17})$"),
			Pattern.compile("^urn:epc:id:gdti:([0-9]{11})\\.([0-9]{1})\\.([0-9]{0,17})$"),
			Pattern.compile("^urn:epc:id:gdti:([0-9]{10})\\.([0-9]{2})\\.([0-9]{0,17})$"),
			Pattern.compile("^urn:epc:id:gdti:([0-9]{9})\\.([0-9]{3})\\.([0-9]{0,17})$"),
			Pattern.compile("^urn:epc:id:gdti:([0-9]{8})\\.([0-9]{4})\\.([0-9]{0,17})$"),
			Pattern.compile("^urn:epc:id:gdti:([0-9]{7})\\.([0-9]{5})\\.([0-9]{0,17})$"),
			Pattern.compile("^urn:epc:id:gdti:([0-9]{6})\\.([0-9]{6})\\.([0-9]{0,17})$") };

	public static final Pattern[] cGDTIList = new Pattern[] {
			Pattern.compile("^urn:epc:idpat:gdti:([0-9]{12})\\.([0-9]{0})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:gdti:([0-9]{11})\\.([0-9]{1})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:gdti:([0-9]{10})\\.([0-9]{2})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:gdti:([0-9]{9})\\.([0-9]{3})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:gdti:([0-9]{8})\\.([0-9]{4})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:gdti:([0-9]{7})\\.([0-9]{5})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:gdti:([0-9]{6})\\.([0-9]{6})\\.\\*$") };

	public static final Pattern[] GIAIList = new Pattern[] {
			Pattern.compile("^urn:epc:id:giai:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})$"),
			Pattern.compile("^urn:epc:id:giai:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})$"),
			Pattern.compile("^urn:epc:id:giai:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile("^urn:epc:id:giai:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})$"),
			Pattern.compile("^urn:epc:id:giai:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})$"),
			Pattern.compile("^urn:epc:id:giai:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})$"),
			Pattern.compile("^urn:epc:id:giai:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})$") };

	public static final Pattern GID = Pattern.compile("^urn:epc:id:gid:([0-9]{1,9})\\.([0-9]{1,8})\\.([0-9]{1,11})$");

	public static final Pattern[] GRAIList = new Pattern[] {
			Pattern.compile("^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([0-9]{1,6})$"),
			Pattern.compile("^urn:epc:id:grai:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{1,16})$"),
			Pattern.compile("^urn:epc:id:grai:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,16})$"),
			Pattern.compile("^urn:epc:id:grai:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,16})$"),
			Pattern.compile("^urn:epc:id:grai:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,16})$"),
			Pattern.compile("^urn:epc:id:grai:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,16})$"),
			Pattern.compile("^urn:epc:id:grai:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,16})$"),
			Pattern.compile("^urn:epc:id:grai:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,16})$") };

	public static final Pattern[] cGRAIList = new Pattern[] {
			Pattern.compile("^urn:epc:idpat:grai:([0-9]{12})\\.([0-9]{0})\\.\\*"),
			Pattern.compile("^urn:epc:idpat:grai:([0-9]{11})\\.([0-9]{1})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:grai:([0-9]{10})\\.([0-9]{2})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:grai:([0-9]{9})\\.([0-9]{3})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:grai:([0-9]{8})\\.([0-9]{4})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:grai:([0-9]{7})\\.([0-9]{5})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:grai:([0-9]{6})\\.([0-9]{6})\\.\\*$") };

	public static final Pattern[] GSRNList = new Pattern[] {
			Pattern.compile("^urn:epc:id:gsrn:([0-9]{12})\\.([0-9]{5})$"),
			Pattern.compile("^urn:epc:id:gsrn:([0-9]{11})\\.([0-9]{6})$"),
			Pattern.compile("^urn:epc:id:gsrn:([0-9]{10})\\.([0-9]{7})$"),
			Pattern.compile("^urn:epc:id:gsrn:([0-9]{9})\\.([0-9]{8})$"),
			Pattern.compile("^urn:epc:id:gsrn:([0-9]{8})\\.([0-9]{9})$"),
			Pattern.compile("^urn:epc:id:gsrn:([0-9]{7})\\.([0-9]{10})$"),
			Pattern.compile("^urn:epc:id:gsrn:([0-9]{6})\\.([0-9]{11})$") };

	public static final Pattern[] GSRNPList = new Pattern[] {
			Pattern.compile("^urn:epc:id:gsrnp:([0-9]{12})\\.([0-9]{5})$"),
			Pattern.compile("^urn:epc:id:gsrnp:([0-9]{11})\\.([0-9]{6})$"),
			Pattern.compile("^urn:epc:id:gsrnp:([0-9]{10})\\.([0-9]{7})$"),
			Pattern.compile("^urn:epc:id:gsrnp:([0-9]{9})\\.([0-9]{8})$"),
			Pattern.compile("^urn:epc:id:gsrnp:([0-9]{8})\\.([0-9]{9})$"),
			Pattern.compile("^urn:epc:id:gsrnp:([0-9]{7})\\.([0-9]{10})$"),
			Pattern.compile("^urn:epc:id:gsrnp:([0-9]{6})\\.([0-9]{11})$") };

	public static final Pattern[] SGLNList = new Pattern[] {
			Pattern.compile("^urn:epc:id:sgln:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile("^urn:epc:id:sgln:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile("^urn:epc:id:sgln:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile("^urn:epc:id:sgln:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile("^urn:epc:id:sgln:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile("^urn:epc:id:sgln:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile("^urn:epc:id:sgln:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,20})$") };

	public static final Pattern[] PGLNList = new Pattern[] {
			Pattern.compile("^urn:epc:id:pgln:([0-9]{12})\\.([0-9]{0})$"),
			Pattern.compile("^urn:epc:id:pgln:([0-9]{11})\\.([0-9]{1})$"),
			Pattern.compile("^urn:epc:id:pgln:([0-9]{10})\\.([0-9]{2})$"),
			Pattern.compile("^urn:epc:id:pgln:([0-9]{9})\\.([0-9]{3})$"),
			Pattern.compile("^urn:epc:id:pgln:([0-9]{8})\\.([0-9]{4})$"),
			Pattern.compile("^urn:epc:id:pgln:([0-9]{7})\\.([0-9]{5})$"),
			Pattern.compile("^urn:epc:id:pgln:([0-9]{6})\\.([0-9]{6})$") };

	public static final Pattern SGTIN = Pattern
			.compile("^https://id.gs1.org/01/([0-9]{1})([0-9]{12})([0-9]{1})/21/([!%-?A-Z_a-z\\x22]{1,20})$");
	public static final Pattern LGTIN = Pattern
			.compile("^https://id.gs1.org/01/([0-9]{1})([0-9]{12})([0-9]{1})/10/([!%-?A-Z_a-z\\x22]{1,20})$");
	public static final Pattern GTIN = Pattern.compile("^https://id.gs1.org/01/([0-9]{1})([0-9]{12})([0-9]{1})");

	public static final Pattern[] UPUIList = new Pattern[] {
			Pattern.compile("^urn:epc:id:upui:([0-9]{12})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{1,28})$"),
			Pattern.compile("^urn:epc:id:upui:([0-9]{11})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,28})$"),
			Pattern.compile("^urn:epc:id:upui:([0-9]{10})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{1,28})$"),
			Pattern.compile("^urn:epc:id:upui:([0-9]{9})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{1,28})$"),
			Pattern.compile("^urn:epc:id:upui:([0-9]{8})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{1,28})$"),
			Pattern.compile("^urn:epc:id:upui:([0-9]{7})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,28})$"),
			Pattern.compile("^urn:epc:id:upui:([0-9]{6})\\.([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,28})$") };

	public static final Pattern[] SGCNList = new Pattern[] {
			Pattern.compile("^urn:epc:id:sgcn:([0-9]{12})\\.([0-9]{0})\\.([!%-?A-Z_a-z\\x22]{0,12})$"),
			Pattern.compile("^urn:epc:id:sgcn:([0-9]{11})\\.([0-9]{1})\\.([!%-?A-Z_a-z\\x22]{0,12})$"),
			Pattern.compile("^urn:epc:id:sgcn:([0-9]{10})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{0,12})$"),
			Pattern.compile("^urn:epc:id:sgcn:([0-9]{9})\\.([0-9]{3})\\.([!%-?A-Z_a-z\\x22]{0,12})$"),
			Pattern.compile("^urn:epc:id:sgcn:([0-9]{8})\\.([0-9]{4})\\.([!%-?A-Z_a-z\\x22]{0,12})$"),
			Pattern.compile("^urn:epc:id:sgcn:([0-9]{7})\\.([0-9]{5})\\.([!%-?A-Z_a-z\\x22]{0,12})$"),
			Pattern.compile("^urn:epc:id:sgcn:([0-9]{6})\\.([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{0,12})$") };

	public static final Pattern[] cSGCNList = new Pattern[] {
			Pattern.compile("^urn:epc:idpat:sgcn:([0-9]{12})\\.([0-9]{0})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:sgcn:([0-9]{11})\\.([0-9]{1})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:sgcn:([0-9]{10})\\.([0-9]{2})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:sgcn:([0-9]{9})\\.([0-9]{3})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:sgcn:([0-9]{8})\\.([0-9]{4})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:sgcn:([0-9]{7})\\.([0-9]{5})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:sgcn:([0-9]{6})\\.([0-9]{6})\\.\\*$") };

	public static final Pattern[] CPIList = new Pattern[] {
			Pattern.compile("^urn:epc:id:cpi:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})\\.([!%-?A-Z_a-z\\x22]{1,12})$"),
			Pattern.compile("^urn:epc:id:cpi:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})\\.([!%-?A-Z_a-z\\x22]{1,12})$"),
			Pattern.compile("^urn:epc:id:cpi:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})\\.([!%-?A-Z_a-z\\x22]{1,12})$"),
			Pattern.compile("^urn:epc:id:cpi:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})\\.([!%-?A-Z_a-z\\x22]{1,12})$"),
			Pattern.compile("^urn:epc:id:cpi:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})\\.([!%-?A-Z_a-z\\x22]{1,12})$"),
			Pattern.compile("^urn:epc:id:cpi:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})\\.([!%-?A-Z_a-z\\x22]{1,12})$"),
			Pattern.compile("^urn:epc:id:cpi:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})\\.([!%-?A-Z_a-z\\x22]{1,12})$") };

	public static final Pattern[] cCPIList = new Pattern[] {
			Pattern.compile("^urn:epc:idpat:cpi:([0-9]{12})\\.([!%-?A-Z_a-z\\x22]{1,18})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:cpi:([0-9]{11})\\.([!%-?A-Z_a-z\\x22]{1,19})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:cpi:([0-9]{10})\\.([!%-?A-Z_a-z\\x22]{1,20})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:cpi:([0-9]{9})\\.([!%-?A-Z_a-z\\x22]{1,21})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:cpi:([0-9]{8})\\.([!%-?A-Z_a-z\\x22]{1,22})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:cpi:([0-9]{7})\\.([!%-?A-Z_a-z\\x22]{1,23})\\.\\*$"),
			Pattern.compile("^urn:epc:idpat:cpi:([0-9]{6})\\.([!%-?A-Z_a-z\\x22]{1,24})\\.\\*$") };

	public static final Pattern[] GSINList = new Pattern[] {
			Pattern.compile("^urn:epc:id:gsin:([0-9]{12})\\.([0-9]{4})$"),
			Pattern.compile("^urn:epc:id:gsin:([0-9]{11})\\.([0-9]{5})$"),
			Pattern.compile("^urn:epc:id:gsin:([0-9]{10})\\.([0-9]{6})$"),
			Pattern.compile("^urn:epc:id:gsin:([0-9]{9})\\.([0-9]{7})$"),
			Pattern.compile("^urn:epc:id:gsin:([0-9]{8})\\.([0-9]{8})$"),
			Pattern.compile("^urn:epc:id:gsin:([0-9]{7})\\.([0-9]{9})$"),
			Pattern.compile("^urn:epc:id:gsin:([0-9]{6})\\.([0-9]{10})$") };

	public static final Pattern[] GINCList = new Pattern[] {
			Pattern.compile("^urn:epc:id:ginc:([0-9]{12})\\.([0-9]{1,18})$"),
			Pattern.compile("^urn:epc:id:ginc:([0-9]{11})\\.([0-9]{1,19})$"),
			Pattern.compile("^urn:epc:id:ginc:([0-9]{10})\\.([0-9]{1,20})$"),
			Pattern.compile("^urn:epc:id:ginc:([0-9]{9})\\.([0-9]{1,21})$"),
			Pattern.compile("^urn:epc:id:ginc:([0-9]{8})\\.([0-9]{1,22})$"),
			Pattern.compile("^urn:epc:id:ginc:([0-9]{7})\\.([0-9]{1,23})$"),
			Pattern.compile("^urn:epc:id:ginc:([0-9]{6})\\.([0-9]{1,24})$") };

	public static final Pattern SSCC = Pattern.compile("^https://id.gs1.org/00/([0-9]{1})([0-9]{16})([0-9]{1})$");

	public static final Pattern[] USDODList = new Pattern[] {
			Pattern.compile("^urn:epc:id:usdod:([0-9A-HJ-NP-Z]{5})\\.([0-9]{1,8})$"),
			Pattern.compile("^urn:epc:id:usdod:([0-9A-HJ-NP-Z]{5,6})\\.([0-9]{1,11})$") };

	public static final Pattern[] ITIPList = new Pattern[] { Pattern.compile(
			"^urn:epc:idpat:itip:([0-9]{12})\\.([0-9]{1})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile(
					"^urn:epc:idpat:itip:([0-9]{11})\\.([0-9]{2})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile(
					"^urn:epc:idpat:itip:([0-9]{10})\\.([0-9]{3})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile(
					"^urn:epc:idpat:itip:([0-9]{9})\\.([0-9]{4})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile(
					"^urn:epc:idpat:itip:([0-9]{8})\\.([0-9]{5})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile(
					"^urn:epc:idpat:itip:([0-9]{7})\\.([0-9]{6})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$"),
			Pattern.compile(
					"^urn:epc:idpat:itip:([0-9]{6})\\.([0-9]{7})\\.([0-9]{2})\\.([0-9]{2})\\.([!%-?A-Z_a-z\\x22]{1,20})$") };

	public static final Pattern BIC = Pattern.compile("^urn:epc:id:bic:([A-Z]{3})(J,U,Z]{1})([0-9]{6})([0-9]{1})$");

	public static final Pattern IMOVN = Pattern.compile("^urn:epc:id:imovn:([0-9]{7})$");
}
