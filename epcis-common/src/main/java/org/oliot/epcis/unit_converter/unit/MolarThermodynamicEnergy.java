package org.oliot.epcis.unit_converter.unit;

/**
 * Copyright (C) 2020 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v2.0.x
 *
 * This class is a Java implementation of UnitConverterUNECERec20 written in
 * Javascript (https://github.com/mgh128/UnitConverterUNECERec20)
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class MolarThermodynamicEnergy extends Unit {

	public static String rType = "B15";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		B15("joule per mole", "J/mol"), B44("kilojoule per mole", "kJ/mol");

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

	public static Double[] multipliers = { 1.0, 1000.0 };
	public static Double[] offsets = { 0.0, 0.0 };

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

	public MolarThermodynamicEnergy(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public MolarThermodynamicEnergy(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "gs1:Molar_thermodynamic_energy";
	}
}
