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
 * The rate of change of linear momentum of a body on which a force acts. A
 * force acting on a body which is free to move produces an acceleration in the
 * motion of the body. SI Units: newton gs1:Force
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Force extends Unit {

	public static String rType = "NEW";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		NEW("newton", "N"), B47("kilonewton", "kN"), B73("meganewton", "MN"), B92("micronewton", "µN"),
		C20("millinewton", "mN"), DU("dyne", "dyn"), C78("pound-force", "lbf"), B37("kilogram-force", "kgf"),
		B51("kilopond", "kp"), L40("ounce (avoirdupois)-force", "ozf"), L94("ton-force (US short)", "ton.sh-force"),
		M75("kilopound-force", "kip"), M76("poundal", "pdl"), M77("kilogram metre per second squared", "kg·m/s²"),
		M78("pond", "p");

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

	public static Double[] multipliers = { 1.0, 1000.0, 1000000.0, 1.0E-6, 0.001, 1.0E-5, 4.448222, 9.80665, 9.80665,
			0.2780139, 8896.443, 4448.222, 0.138255, 1.0, 0.00980665 };

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

	public Force(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Force(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Force";
	}
}
