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
 * The rate of doing work or rate of production, transfer or consumption of
 * energy; the amount of energy transferred or converted per unit time. SI
 * Units: watt gs1:Power
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Power extends Unit {

	public static String rType = "WTT";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		WTT("watt", "W"), KWT("kilowatt", "kW"), MAW("megawatt", "MW"), A90("gigawatt", "GW"), C31("milliwatt", "mW"),
		D80("microwatt", "µW"), F80("water horse power", "water hp"), A63("erg per second", "erg/s"),
		A74("foot pound-force per second", "ft·lbf/s"), B39("kilogram-force metre per second", "kgf·m/s"),
		HJ("metric horse power", "metric hp"), A25("cheval vapeur", "CV"), BHP("brake horse power", "BHP"),
		K15("foot pound-force per hour", "ft·lbf/h"), K16("foot pound-force per minute", "ft·lbf/min"),
		K42("horsepower (boiler)", "boiler hp"), N12("Pferdestaerke", "PS");

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

	public static Double[] multipliers = { 1.0, 1000.0, 1000000.0, 1.0E9, 0.001, 1.0E-6, 746.043, 1.0E-7, 1.355818,
			9.80665, 735.49875, 735.4988, 745.7, 3.766161E-4, 0.02259697, 9809.5, 735.4988 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0 };

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

	public Power(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Power(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Power";
	}
}
