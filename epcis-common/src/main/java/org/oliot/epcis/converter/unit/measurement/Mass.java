package org.oliot.epcis.converter.unit.measurement;

/**
 * Copyright (C) 2020-2023 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v2.0.x
 *
 * This class is a Java implementation of UnitConverterUNECERec20 written in
 * Javascript (https://github.com/mgh128/UnitConverterUNECERec20)
 * 
 * https://www.gs1.org/voc/MeasurementType
 *
 * The quantity of matter in a body. Inertial mass is the measure of the inertia
 * of a body; its resistance to acceleration. SI Units: kilogram gs1:Mass
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Mass extends Unit {

	public static String rType = "KGM";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		KGM("kilogram", "kg"), KTN("kilotonne", "kt"), LTN("ton (UK) or long ton (US)", "ton (UK)"),
		_2U("megagram", "Mg"), TNE("tonne (metric ton)", "t"), STN("ton (US) or short ton (UK/US)", "ton (US)"),
		DTN("decitonne", "dt"), STI("stone (UK)", "st"), LBR("pound", "lb"), HGM("hectogram", "hg"), ONZ("ounce", "oz"),
		DJ("decagram", "dag"), APZ("troy ounce or apothecary ounce", "tr oz"), GRM("gram", "g"), DG("decigram", "dg"),
		CGM("centigram", "cg"), MGM("milligram", "mg"), MC("microgram", "µg"), F13("slug", "slug"),
		CWI("hundred weight (UK)", "cwt (UK)"), CWA("hundred pound (cwt) / hundred weight (US)", "cwt (US)"),
		M86("pfund", "pfd");

		private final String name;
		private final String symbol;

		Type(String name, String symbol) {
			this.name = name;
			this.symbol = symbol;
		}

		public String getName() {
			return name;
		}

		public String getSymbol() {
			return symbol;
		}
	}

	public static Double[] multipliers = { 1.0, 1000000.0, 1016.047, 1000.0, 1000.0, 907.1847, 100.0, 6.350293,
			0.45359237, 0.1, 0.02834952, 0.01, 0.003110348, 0.001, 1.0E-4, 1.0E-5, 1.0E-6, 1.0E-9, 14.5939, 50.8023,
			45.3592, 0.5 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

	public static Type[] getRec20Types() {
		return Type.values();
	}

	private Type type;
	private Double value;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Mass(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Mass(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Mass";
	}
}
