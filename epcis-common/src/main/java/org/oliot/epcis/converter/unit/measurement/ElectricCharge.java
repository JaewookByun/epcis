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
 * Quantity of unbalanced electricity in an object, i.e. excess or deficiency of
 * electrons, resulting in negative or positive electrification, respectively.
 * SI Units: coulomb gs1:ElectricCharge
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class ElectricCharge extends Unit {

	public static String rType = "COU";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		COU("coulomb", "C"), A8("ampere second", "A·s"), AMH("ampere hour", "A·h"),
		TAH("kiloampere hour (thousand ampere hour)", "kA·h"), D77("megacoulomb", "MC"), D86("millicoulomb", "mC"),
		B26("kilocoulomb", "kC"), B86("microcoulomb", "µC"), C40("nanocoulomb", "nC"), C71("picocoulomb", "pC"),
		E09("milliampere hour", "mA·h"), N95("ampere minute", "A·min"), N94("franklin", "Fr");

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

	public static Double[] multipliers = { 1.0, 1.0, 3600.0, 3600000.0, 1000000.0, 0.001, 1000.0, 1.0E-6, 1.0E-9,
			1.0E-12, 3.6, 60.0, 3.335641E-10 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public ElectricCharge(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public ElectricCharge(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:ElectricCharge";
	}
}
