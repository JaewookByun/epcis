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
 * The ratio of the linear distance travelled by a body to the time taken. Speed
 * is a scalar quantity. Velocity is a vector with magnitude and direction. SI
 * Units: metre per second gs1:Speed
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Speed extends Unit {

	public static String rType = "KMH";
	public static double rm = 0.277777778;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		KMH("kilometres per hour", "km/h"), MTS("metres per second", "m/s"), HM("miles per hour", "mile/h"),
		M57("miles per minute", "mi/min"), M58("miles per second", "mi/s"), KNT("knot", "kn"),
		M62("kilometres per second", "km/s"), H49("centimetres per hour", "cm/h"),
		_2M("centimetres per second", "cm/s"), K14("foot per hour", "ft/h"), FR("foot per minute", "ft/min"),
		FS("foot per second", "ft/s"), M63("inch per minute", "in/min"), IU("inch per second", "in/s"),
		M61("inch per year", "in/y"), _2X("metre per minute", "m/min");

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

	public static Double[] multipliers = { 0.277777778, 1.0, 0.44704, 26.8224, 1609.344, 0.514444, 1000.0,
			2.77777778E-7, 0.01, 8.466667E-5, 0.00508, 0.3048, 4.233333E-4, 0.0254, 8.048774E-10, 0.016666667 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public Speed(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Speed(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Speed";
	}
}
