package org.oliot.epcis.converter.unit.measurement;

import java.util.Arrays;

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
 * The ratio of the mass of water vapour in a sample of moist air to the volume
 * of the sample. SI Units: kilogram per cubic metre gs1:AbsoluteHumidity
 *
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class AbsoluteHumidity extends Unit {

	public static String rType = "KMQ";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		KMQ("kilogram per cubic metre", "kg/m³"), _23("gram per cubic centimetre", "g/cm³"),
		D41("tonne per cubic metre", "t/m³"), GJ("gram per millilitre", "g/ml"),
		B35("kilogram per litre", "kg/l or kg/L"), GL("gram per litre", "g/l"), A93("gram per cubic metre", "g/m³"),
		GP("milligram per cubic metre", "mg/m³"), B72("megagram per cubic metre", "Mg/m³"),
		B34("kilogram per cubic decimetre", "kg/dm³"), H29("microgram per litre", "µg/l"),
		M1("milligram per litre", "mg/l"), GQ("microgram per cubic metre", "µg/m³"),
		F23("gram per cubic decimetre", "g/dm³"), G31("kilogram per cubic centimetre", "kg/cm³"),
		_87("pound per cubic foot", "lb/ft³"), GE("pound per gallon (US)", "lb/gal (US)"),
		LA("pound per cubic inch", "lb/in³"), G32("ounce (avoirdupois) per cubic yard", "oz/yd³"),
		K41("grain per gallon (US)", "gr/gal (US)"), K71("pound (avoirdupois) per gallon (UK)", "lb/gal (UK)"),
		K84("pound per cubic yard", "lb/yd³"), L37("ounce (avoirdupois) per gallon (UK)", "oz/gal (UK)"),
		L38("ounce (avoirdupois) per gallon (US)", "oz/gal (US)"), L39("ounce (avoirdupois) per cubic inch", "oz/in³"),
		L65("slug per cubic foot", "slug/ft³"), L92("ton (UK long) per cubic yard", "ton.l/yd³ (UK)"),
		L93("ton (US short) per cubic yard", "ton.s/yd³ (US)");

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

	public static Double[] multipliers = { 1.0, 1e3, 1e3, 1e3, 1e3, 1.0, 1e-3, 1e-6, 1e3, 1e3, 1e-6, 1e-3, 1e-9, 1.0,
			1e6, 16.01846, 119.8264, 27679.90, 0.0370798, 0.01711806, 99.77637, 0.5932764, 6.236023, 7.489152, 1729.994,
			515.3788, 1328.939, 1186.553 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public AbsoluteHumidity(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public AbsoluteHumidity(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:AbsoluteHumidity";
	}

	public static void main(String[] args) {
		for (org.oliot.epcis.converter.unit.measurement.Volume.Type t : Arrays.stream(Volume.getRec20Types()).toList()) {
			System.out.print("\"" + t + "\",");
		}
	}
}
