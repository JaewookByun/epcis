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
public class EffectiveDoseRate extends Unit {

	public static String rType = "P65";
	public static double rm = 1.0;
	public static double ro = 0.0;
	
	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		P65("sievert per second", "Sv/s"), P66("millisievert per second", "mSv/s"),
		P67("microsievert per second", "µSv/s"), P68("nanosievert per second", "nSv/s"), P69("rem per second", "rem/s"),
		P70("sievert per hour", "Sv/h"), P71("millisievert per hour", "mSv/h"), P72("microsievert per hour", "µSv/h"),
		P73("nanosievert per hour", "nSv/h"), P74("sievert per minute", "Sv/min"),
		P75("millisievert per minute", "mSv/min"), P76("microsievert per minute", "µSv/min"),
		P77("nanosievert per minute", "nSv/min");

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

	public static Double[] multipliers = { 1.0, 0.001, 1.0E-6, 1.0E-9, 0.01, 2.77778E-4, 2.77777778E-8, 2.77777778E-11,
			2.77777778E-14, 0.016666, 1.666666667E-5, 1.666666667E-8, 1.666666667E-11 };
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

	public EffectiveDoseRate(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public EffectiveDoseRate(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "gs1:Effective_dose_rate";
	}
}
