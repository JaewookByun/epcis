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
 * The rate of spontaneous disintegration or decay of certain natural heavy
 * elements, accompanied by alpha-rays, beta-rays or gamma-rays. SI Units:
 * becquerel gs1:Radioactivity
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Radioactivity extends Unit {

	public static String rType = "CUR";
	public static double rm = 3.7E10;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		CUR("curie", "Ci"), MCU("millicurie", "mCi"), M5("microcurie", "µCi"), _2R("kilocurie", "kCi"),
		BQL("becquerel", "Bq"), GBQ("gigabecquerel", "GBq"), _2Q("kilobecquerel", "kBq"), _4N("megabecquerel", "MBq"),
		H08("microbecquerel", "µBq");

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

	public static Double[] multipliers = { 3.7E10, 3.7E7, 37000.0, 3.7E13, 1.0, 1.0E9, 1000.0, 1000000.0, 1.0E-6 };
	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public Radioactivity(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Radioactivity(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Radioactivity";
	}
}
