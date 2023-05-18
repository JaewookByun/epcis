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
 * The linear magnitude of any thing, as measured end to end. Length, width,
 * depth, height, diameter are all measured in units of length. SI Units: metre
 * gs1:Length
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Length extends Unit {

	public static String rType = "MTR";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		MTR("metre", "m"), A11("angstrom", "Å"), A71("femtometre", "fm"), C45("nanometre", "nm"),
		_4H("micrometre", "µm"), A12("astronomical unit", "ua"), DMT("decimetre", "dm"), CMT("centimetre", "cm"),
		MMT("millimetre", "mm"), INH("inch", "in"), FOT("foot", "ft"), YRD("yard", "yd"),
		NMI("nautical mile", "n mile"), A45("decametre", "dam"), HMT("hectometre", "hm"), KMT("kilometre", "km"),
		B57("light year", "ly"), AK("fathom", "fth"), M50("furlong", "fur"),
		M49("chain (based on US survey foot)", "ch (US survey)"), X1("Gunter's chain", "ch (UK)"),
		M51("foot (US survey)", "ft (US survey)");

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

	public static Double[] multipliers = { 1.0, 1.0E-10, 1.0E-15, 1.0E-9, 1.0E-6, 1.4959787E11, 0.1, 0.01, 0.001,
			0.0254, 0.3048, 0.9144, 1852.0, 10.0, 100.0, 1000.0, 9.46073E15, 1.8288, 201.168, 20.11684, 20.1168,
			0.3048006 };

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

	public Length(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Length(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Length";
	}
}
