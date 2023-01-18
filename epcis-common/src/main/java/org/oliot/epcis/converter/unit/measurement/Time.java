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
 * A dimension that enables distinction between two otherwise identical events
 * that occur at the same point in space. The interval between such events is
 * the basis of time measurement. SI Units: second gs1:Time
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Time extends Unit {

	public static String rType = "SEC";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		SEC("second", "s"), MIN("minute", "min"), HUR("hour", "h"), DAY("day", "d"), B52("kilosecond", "ks"),
		C26("millisecond", "ms"), H70("picosecond", "ps"), B98("microsecond", "µs"), C47("nanosecond", "ns"),
		WEE("week", "wk"), MON("month", "mo"), ANN("year", "y"), D42("tropical year", "y (tropical)"),
		L95("common year", "y (365 days)"), L96("sidereal year", "y (sidereal)"), M56("shake", "shake");

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

	public static Double[] multipliers = { 1.0, 60.0, 3600.0, 86400.0, 1000.0, 0.001, 1.0E-12, 1.0E-6, 1.0E-9, 604800.0,
			2629800.0, 3.15576E7, 3.1556925E7, 3.1536E7, 3.155815E7, 1.0E-8 };

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

	public Time(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Time(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Time";
	}
}
