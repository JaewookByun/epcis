package org.oliot.epcis.unit_converter.unit.back;

import org.oliot.epcis.converter.unit.measurement.Unit;

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
public class DimensionlessConcentration extends Unit {

	public static String rType = "P1";
	public static double rm = 0.01;
	public static double ro = 0.0;
	
	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		P1("percent", "%"), _59("part per million", "ppm"), _61("part per billion (US)", "ppm"),
		_60("percent weight", "wt%"), E40("part per hundred thousand", "ppht"), NX("part per thousand, per mille", "‰"),
		GK("gram per kilogram", "g/kg"), NA("milligram per kilogram", "mg/kg"), J33("microgram per kilogram", "µg/kg"),
		L32("nanogram per kilogram", "ng/kg"), M29("kilogram per kilogram", "kg/kg"), K62("litre per litre", "l/l"),
		L19("millilitre per litre", "ml/l"), J36("microlitre per litre", "µl/l"),
		H60("cubic metre per cubic metre", "m³/m³"), H65("millilitre per cubic metre", "ml/m³"),
		J87("cubic centimetre per cubic metre", "cm³/m³"), L21("cubic millimetre per cubic metre", "mm³/m³"),
		J91("cubic decimetre per cubic metre", "dm³/m³");

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

	public static Double[] multipliers = { 0.01, 1.0E-6, 1.0E-9, 0.01, 1.0E-5, 0.001, 0.001, 1.0E-6, 1.0E-9, 1.0E-12,
			1.0, 1.0, 0.001, 1.0E-6, 1.0, 1.0E-6, 1.0E-6, 1.0E-9, 0.001 };
	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0 };

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

	public DimensionlessConcentration(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public DimensionlessConcentration(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "gs1:Dimensionless_concentration";
	}
}
