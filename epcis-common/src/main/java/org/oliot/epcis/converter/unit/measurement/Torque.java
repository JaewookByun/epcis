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
 * The product of a force and its perpendicular distance from a point about
 * which it causes rotation or torsion. SI Units: newton metre gs1:Torque
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Torque extends Unit {

	public static String rType = "NU";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		NU("newton metre", "N·m"), B74("meganewton metre", "MN·m"), B48("kilonewton metre", "kN·m"),
		D83("millinewton metre", "mN·m"), B93("micronewton metre", "µN·m"), DN("decinewton metre", "dN·m"),
		J72("centinewton metre", "cN·m"), B38("kilogram-force metre", "kgf·m"), F21("pound-force inch", "lbf·in"),
		J94("dyne centimetre", "dyn·cm"), L41("ounce (avoirdupois)-force inch", "ozf·in"),
		M92("pound-force foot", "lbf·ft"), M95("poundal foot", "pdl·ft"), M96("poundal inch", "pdl·in"),
		M97("dyne metre", "dyn·m");

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

	public static Double[] multipliers = { 1.0, 1000000.0, 1000.0, 0.001, 1.0E-6, 0.1, 0.01, 9.80665, 0.112985, 1.0E-7,
			0.007061552, 1.355818, 0.04214011, 0.003511677, 1.0E-5 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public Torque(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Torque(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Torque";
	}
}
