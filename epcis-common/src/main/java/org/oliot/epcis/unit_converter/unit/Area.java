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
public class Area extends Unit {

	public static String rType = "MTK";
	public static double rm = 1.0;
	public static double ro = 0.0;
	
	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		MTK("square metre", "m²"), KMK("square kilometre", "km²"), H30("square micrometre (square micron)", "µm²"),
		DAA("decare", "daa"), CMK("square centimetre", "cm²"), DMK("square decimetre", "dm²"),
		H16("square decametre", "dam²"), H18("square hectometre", "hm²"), MMK("square millimetre", "mm²"),
		ARE("are", "a"), HAR("hectare", "ha"), INK("square inch", "in²"), FTK("square foot", "ft²"),
		YDK("square yard", "yd²"), MIK("square mile (statute mile)", "mi²"),
		M48("square mile (based on U.S. survey foot) ", "mi² (US survey)"), ACR("acre", "acre"),
		M47("circular mil ", "cmil");

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

	public static Double[] multipliers = { 1.0, 1000000.0, 1.0E-12, 1000.0, 1.0E-4, 0.01, 100.0, 10000.0, 1.0E-6, 100.0,
			10000.0, 6.4516E-4, 0.09290304, 0.8361274, 2589988.0, 2589998.0, 4046.873, 5.067075E-10 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0 };

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

	public Area(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Area(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "gs1:Area";
	}
}
