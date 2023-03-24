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
public class Temperature extends Unit {

	public static String rType = "KEL";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {
		KEL("kelvin", "K"), FAH("degrees Fahrenheit", "ºF"), CEL("degrees Celsius", "ºC"), A48("degrees Rankine", "ºR");

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

	public static Double[] multipliers = { 1.0, 5 / 9.0, 1.0, 5 / 9.0 };
	public static Double[] offsets = { 0.0, 273.15 - 32 * 5 / 9.0, 273.15, 0.0 };

	private Type type;
	private Double value;

	public static Type[] getRec20Types() {
		return Type.values();
	}

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

	public Temperature(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}
	
	public Temperature(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}
		
	@Override
	public String toString() {
		return "gs1:Temperature";
	}
}
