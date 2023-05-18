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
 * The amount of three-dimensional space occupied by a body, measured in cubic
 * length units. SI Units: cubic metre gs1:Volume
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 * 
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class Volume extends Unit {

	public static String rType = "MTQ";
	public static double rm = 1.0;
	public static double ro = 0.0;

	/**
	 * 'Rec20 representation'('name', 'symbol')
	 */
	public enum Type {

		MTQ("cubic metre", "m³"), MAL("megalitre", "Ml"), LTR("litre", "l"), MMQ("cubic millimetre", "mm³"),
		CMQ("cubic centimetre", "cm³"), DMQ("cubic decimetre", "dm³"), MLT("millilitre", "ml"), HLT("hectolitre", "hl"),
		CLT("centilitre", "cl"), DMA("cubic decametre", "dam³"), H19("cubic hectometre", "hm³"),
		H20("cubic kilometre", "km³"), DLT("decilitre", "dl"), _4G("microlitre", "µl"), K6("kilolitre", "kl"),
		A44("decalitre", "dal"), INQ("cubic inch", "in³"), FTQ("cubic foot", "ft³"), YDQ("cubic yard", "yd³"),
		GLI("gallon (UK)", "gal (UK)"), GLL("gallon (US)", "gal (US)"), PT("pint (US)", "pt (US)"),
		PTI("pint (UK)", "pt (UK)"), QTI("quart (UK)", "qt (UK)"), PTL("liquid pint (US)", "liq pt (US)"),
		QTL("liquid quart (US)", "liq qt (US)"), PTD("dry pint (US)", "dry pt (US)"),
		OZI("fluid ounce (UK)", "fl oz (UK)"), QT("quart (US)", "qt (US)"),
		J57("barrel (UK petroleum)", "bbl (UK liq.)"), L43("peck (UK)", "pk (UK)"), L61("pint (US dry)", "pt (US dry)"),
		L62("quart (US dry)", "qt (US dry)"), L84("ton (UK shipping)", "British shipping ton"),
		L86("ton (US shipping)", "(US) shipping ton"), OZA("fluid ounce (US)", "fl oz (US)"),
		BUI("bushel (UK)", "bushel (UK)"), BUA("bushel (US)", "bu (US)"), BLL("barrel (US)", "barrel (US)"),
		BLD("dry barrel (US)", "bbl (US)"), GLD("dry gallon (US)", "dry gal (US)"),
		QTD("dry quart (US)", "dry qt (US)"), G26("stere", "st"), G21("cup unit of volume", "cup (US)"),
		G24("tablespoon (US)", "tablespoon (US)"), G25("teaspoon (US)", "teaspoon (US)"), G23("peck", "pk (US)"),
		M67("acre-foot (based on U.S. survey foot)", "acre-ft (US survey)"), M68("cord (128 ft³)", "cord"),
		M69("cubic mile (UK statute)", "mi³"), M70("ton. register ", "RT");

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

	public static Double[] multipliers = { 1.0, 1000.0, 0.001, 1.0E-9, 1.0E-6, 0.001, 1.0E-6, 0.1, 1.0E-5, 1000.0,
			1000000.0, 1.0E9, 1.0E-4, 1.0E-9, 1.0, 0.01, 1.6387064E-5, 0.02831685, 0.764555, 0.004546092, 0.003785412,
			4.73176E-4, 5.68261E-4, 0.0011365225, 4.731765E-4, 9.463529E-4, 5.506105E-4, 2.841306E-5, 9.463529E-4,
			0.15911315, 0.009092181, 5.506105E-4, 0.001101221, 1.1893, 1.1326, 2.957353E-5, 0.03636872, 0.03523907,
			0.1589873, 0.115627, 0.004404884, 0.001101221, 1.0, 2.365882E-4, 1.478676E-5, 4.928922E-6, 0.008809768,
			1233.489, 3.624556, 4.168182E9, 2.831685 };

	public static Double[] offsets = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

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

	public Volume(Type type, Double value) {
		this.type = type;
		this.uom = type.toString();
		this.value = value;
	}

	public Volume(String uom, Double value) {
		this.type = Type.valueOf(uom);
		this.uom = uom;
		this.value = value;
	}

	@Override
	public String toString() {
		return "gs1:Volume";
	}
}
