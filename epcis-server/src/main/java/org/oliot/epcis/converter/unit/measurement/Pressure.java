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
 * The perpendicular force per unit area acting on a material and tending to
 * change its dimensions. SI Units: pascal, newton per square metre gs1:Pressure
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Pressure extends Unit {

	public static String rType = "PAL";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		PAL("pascal", "Pa"), H75("decapascal", "daPa"), _74("millipascal", "mPa"), A89("gigapascal", "GPa"),
		A97("hectopascal", "hPa"), B96("micropascal", "µPa"), KPA("kilopascal", "kPa"), MPA("megapascal", "MPa"),
		BAR("bar", "bar"), MBR("millibar", "mbar"), C55("newton per square metre", "N/m²"),
		C56("newton per square millimetre", "N/mm²"), B40("kilogram-force per square metre", "kgf/m²"),
		UA("torr", "Torr"), _80("pound per square inch absolute", "lb/in²"),
		H78("conventional centimetre of water", "cm H₂O"), HP("conventional millimetre of water", "mm H₂O"),
		F79("inch of mercury", "inHg"), F78("inch of water", "inH₂O"), ATT("technical atmosphere", "at"),
		ATM("standard atmosphere", "atm"), J89("centimetres of mercury", "cm Hg"), K24("foot of water", "ft H₂O"),
		K25("foot of mercury", "ft Hg"), K31("gram-force per square centimetre", "gf/cm²"),
		E42("kilogram-force per square centimetre", "kgf/cm²"), E41("kilogram-force per square millimetre", "kgf·/mm²"),
		K85("pound-force per square foot", "lbf/ft²"), _84("kilopounds force per square inch", "klbf/in²"),
		N13("centimetre of mercury (0 ºC)", "cmHg (0 ºC)"), N14("centimetre of water (4 ºC)", "cmH₂O (4 ºC)"),
		N15("foot of water (39.2 ºF)", "ftH₂O (392 ºF)"), N16("inch of mercury (32 ºF)", "inHG (32 ºF)"),
		N17("inch of mercury (60 ºF)", "inHg (60 ºF)"), N18("inch of water (39.2 ºF)", "inH₂O (392 ºF)"),
		N19("inch of water (60 ºF)", "inH₂O (60 ºF)"), N20("kip per square inch", "ksi"),
		N21("poundal per square foot", "pdl/ft²"), N22("ounce (avoirdupois) per square inch", "oz/in²"),
		N23("conventional metre of water", "mH₂O"), HN("millimetres of mercury", "mm Hg"),
		PS("pound force per square inch", "psi");

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

	public static Double[] multipliers = { 1.0, 10.0, 1.00E-03, 1.00E+09, 100.0, 1.00E-06, 1.00E+03, 1.00E+06, 100000.0,
			100.0, 1.00, 1000000.00, 9.80665, 133.3224, 6897.112776, 98.0665, 9.80665, 33220.4859, 2443.56309, 98066.5,
			101325.0, 1333.224, 2989.067, 40636.66, 98.0665, 98066.50, 9806650.00, 47.88026, 6894757.00, 1333.22,
			98.0638, 2988.98, 3386.38, 3376.85, 249.082, 248.84, 6894757.00, 1.488164, 431.0695485, 9806.65, 13332.24,
			6894.757 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public Pressure(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Pressure(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Pressure";
	}
}
