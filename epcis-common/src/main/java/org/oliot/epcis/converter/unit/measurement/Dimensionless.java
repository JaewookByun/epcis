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
 * A measurement whose units are dimensionless. gs1:Count or gs1:VolumeFraction
 * or gs1:RelativeHumidity should be used in preference if appropriate.
 * gs1:Dimensionless
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Dimensionless extends Unit {

	public static String rType = "C62";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * A measurement whose units are dimensionless. gs1:Count or gs1:VolumeFraction
	 * or gs1:RelativeHumidity should be used in preference if appropriate.
	 */
	public enum Type {
		C62("one", "unit");

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

	public static Double[] multipliers = { 1.0 };
	public static Double[] offsets = { 0.0 };

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

	public Dimensionless(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Dimensionless(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Dimensionless";
	}
}
