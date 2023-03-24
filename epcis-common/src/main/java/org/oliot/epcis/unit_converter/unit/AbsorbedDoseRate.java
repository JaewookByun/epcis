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
public class AbsorbedDoseRate extends Unit {

	public static String rType = "P54";
	public static double rm = 0.001;
	public static double ro = 0.0;
	
	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		P54("miligray per second", "mGy/s"), P55("microgray per second", "µGy/s"), P56("nanogray per second", "nGy/s"),
		P57("gray per minute", "Gy/min"), P58("milligray per minute", "mGy/min"),
		P59("microgray per minute", "µGy/min"), P60("nanogray per minute", "nGy/min"), P61("gray per hour", "Gy/h"),
		P62("milligray per hour", "mGy/h"), P63("microgray per hour", "µGy/h"), P64("nanogray per hour", "nGy/h");

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

	public static Double[] multipliers = { 0.001, 1.0E-6, 1.0E-9, 0.0166667, 1.66667E-5, 1.66667E-8, 1.66667E-11,
			2.77778E-4, 2.77778E-7, 2.77778E-10, 2.77778E-13 };
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

	public AbsorbedDoseRate(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}
	public AbsorbedDoseRate(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "gs1:Absorbed_dose_rate";
	}
}
