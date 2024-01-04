package org.oliot.epcis.unit_converter.unit.back;

import org.oliot.epcis.converter.unit.measurement.Unit;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class MolarConcentration extends Unit {

	public static String rType = "C36";
	public static double rm = 1.0;
	public static double ro = 0.0;
	
	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		C36("mole per cubic metre", "mol/m³"), M33("millimole per litre", "mmol/l"), C38("mole per litre", "mol/l"),
		C35("mole per cubic decimetre", "mol/dm³"), B46("kilomole per cubic metre", "kmol/m³");

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

	public static Double[] multipliers = { 1.0, 1.0, 1000.0, 1000.0, 1000.0 };
	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public MolarConcentration(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public MolarConcentration(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "gs1:Molar_concentration";
	}
}
