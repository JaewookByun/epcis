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
public class Acceleration extends Unit {

	public static String rType = "MSK";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		MSK("metre per second squared", "m/s²"), A76("gal", "Gal"), C11("milligal", "mGal"),
		M38("kilometre per second squared", "km/s²"), M39("centimetre per second squared", "cm/s²"),
		M41("millimetre per second squared", "mm/s²"), A73("foot per second squared", "ft/s²"),
		IV("inch per second squared", "in/s²"), K40("standard acceleration of free fall", "gn"),
		M40("yard per second squared", "yd/s²"), M42("mile (statute mile) per second squared", "mi/s²");

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

	public static Double[] multipliers = { 1.0, 0.01, 1.0E-5, 1000.0, 0.01, 0.001, 0.3048, 0.0254, 9.80665, 0.9144,
			1609.344 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public Acceleration(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}
	
	public Acceleration(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "gs1:Acceleration";
	}
}
