package org.oliot.epcis.converter.unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.oliot.epcis.converter.unit.measurement.AbsoluteHumidity;
import org.oliot.epcis.converter.unit.measurement.AbsorbedDose;
import org.oliot.epcis.converter.unit.measurement.AbsorbedDoseRate;
import org.oliot.epcis.converter.unit.measurement.Acceleration;
import org.oliot.epcis.converter.unit.measurement.Altitude;
import org.oliot.epcis.converter.unit.measurement.AmountOfSubstance;
import org.oliot.epcis.converter.unit.measurement.AmountOfSubstancePerUnitVolume;
import org.oliot.epcis.converter.unit.measurement.Angle;
import org.oliot.epcis.converter.unit.measurement.Area;
import org.oliot.epcis.converter.unit.measurement.Capacitance;
import org.oliot.epcis.converter.unit.measurement.Conductance;
import org.oliot.epcis.converter.unit.measurement.Count;
import org.oliot.epcis.converter.unit.measurement.Density;
import org.oliot.epcis.converter.unit.measurement.Dimensionless;
import org.oliot.epcis.converter.unit.measurement.DoseEquivalent;
import org.oliot.epcis.converter.unit.measurement.DoseEquivalentRate;
import org.oliot.epcis.converter.unit.measurement.DynamicViscosity;
import org.oliot.epcis.converter.unit.measurement.ElectricCharge;
import org.oliot.epcis.converter.unit.measurement.ElectricCurrent;
import org.oliot.epcis.converter.unit.measurement.Energy;
import org.oliot.epcis.converter.unit.measurement.Force;
import org.oliot.epcis.converter.unit.measurement.Frequency;
import org.oliot.epcis.converter.unit.measurement.Illuminance;
import org.oliot.epcis.converter.unit.measurement.Inductance;
import org.oliot.epcis.converter.unit.measurement.Length;
import org.oliot.epcis.converter.unit.measurement.LuminousIntensity;
import org.oliot.epcis.converter.unit.measurement.MagneticFlux;
import org.oliot.epcis.converter.unit.measurement.MagneticFluxDensity;
import org.oliot.epcis.converter.unit.measurement.MagneticVectorPotential;
import org.oliot.epcis.converter.unit.measurement.Mass;
import org.oliot.epcis.converter.unit.measurement.MassConcentration;
import org.oliot.epcis.converter.unit.measurement.MolarEnergy;
import org.oliot.epcis.converter.unit.measurement.MolarMass;
import org.oliot.epcis.converter.unit.measurement.MolarVolume;
import org.oliot.epcis.converter.unit.measurement.Power;
import org.oliot.epcis.converter.unit.measurement.Pressure;
import org.oliot.epcis.converter.unit.measurement.Radioactivity;
import org.oliot.epcis.converter.unit.measurement.RelativeHumidity;
import org.oliot.epcis.converter.unit.measurement.Resistance;
import org.oliot.epcis.converter.unit.measurement.SpecificVolume;
import org.oliot.epcis.converter.unit.measurement.Speed;
import org.oliot.epcis.converter.unit.measurement.Temperature;
import org.oliot.epcis.converter.unit.measurement.Time;
import org.oliot.epcis.converter.unit.measurement.Torque;
import org.oliot.epcis.converter.unit.measurement.Voltage;
import org.oliot.epcis.converter.unit.measurement.Volume;
import org.oliot.epcis.converter.unit.measurement.VolumeFraction;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.model.ValidationException;
import org.oliot.epcis.unit_converter.unit.back.DimensionlessConcentration;
import org.oliot.epcis.unit_converter.unit.back.MolarConcentration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * This class is a Java implementation of UnitConverterUNECERec20 written in
 * Javascript (https://github.com/mgh128/UnitConverterUNECERec20)
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class UnitConverter {

	private HashMap<String, List<String>> typeUoMListMap;
	private HashMap<String, String> uomTypeMap;

	public UnitConverter() {
		typeUoMListMap = new HashMap<String, List<String>>();

		typeUoMListMap.put("gs1:AbsoluteHumidity",
				List.of("KMQ", "_23", "D41", "GJ", "B35", "GL", "A93", "GP", "B72", "B34", "H29", "M1", "GQ", "F23",
						"G31", "_87", "GE", "LA", "G32", "K41", "K71", "K84", "L37", "L38", "L39", "L65", "L92",
						"L93"));

		typeUoMListMap.put("gs1:AbsorbedDose", List.of("A95", "C13", "C80", "A61"));

		typeUoMListMap.put("gs1:AbsorbedDoseRate",
				List.of("P54", "P55", "P56", "P57", "P58", "P59", "P60", "P61", "P62", "P63", "P64"));

		typeUoMListMap.put("gs1:Acceleration",
				List.of("MSK", "A76", "C11", "M38", "M39", "M41", "A73", "IV", "K40", "M40", "M42"));

		typeUoMListMap.put("gs1:Altitude", List.of("MTR", "A11", "A71", "C45", "_4H", "A12", "DMT", "CMT", "MMT", "INH",
				"FOT", "YRD", "NMI", "A45", "HMT", "KMT", "B57", "AK", "M50", "M49", "X1", "M51"));

		typeUoMListMap.put("gs1:AmountOfSubstance", List.of("C34", "B45", "C18", "FH"));

		typeUoMListMap.put("gs1:AmountOfSubstancePerUnitVolume", List.of("C36", "M33", "C38", "C35", "B46"));

		typeUoMListMap.put("gs1:Angle", List.of("C81", "C25", "B97", "A91", "DD", "D61", "D62", "M43", "M44"));

		typeUoMListMap.put("gs1:Area", List.of("MTK", "KMK", "H30", "DAA", "CMK", "DMK", "H16", "H18", "MMK", "ARE",
				"HAR", "INK", "FTK", "YDK", "MIK", "M48", "ACR", "M47"));

		typeUoMListMap.put("gs1:Capacitance", List.of("FAR", "H48", "C10", "_4O", "C41", "_4T", "N90"));

		typeUoMListMap.put("gs1:Conductance", List.of("SIE", "B53", "C27", "B99", "N92", "NQ", "NR"));

		typeUoMListMap.put("gs1:Count", List.of("C62"));

		typeUoMListMap.put("gs1:Density",
				List.of("KMQ", "_23", "D41", "GJ", "B35", "GL", "A93", "GP", "B72", "B34", "H29", "M1", "GQ", "F23",
						"G31", "_87", "GE", "LA", "G32", "K41", "K71", "K84", "L37", "L38", "L39", "L65", "L92",
						"L93"));

		typeUoMListMap.put("gs1:Dimensionless", List.of("C62"));

		typeUoMListMap.put("gs1:DoseEquivalent", List.of("D13", "C28", "D91", "L31"));

		typeUoMListMap.put("gs1:DoseEquivalentRate",
				List.of("P65", "P66", "P67", "P68", "P69", "P70", "P71", "P72", "P73", "P74", "P75", "P76", "P77"));

		typeUoMListMap.put("gs1:DynamicViscosity",
				List.of("PAL", "H75", "_74", "A89", "A97", "B96", "KPA", "MPA", "BAR", "MBR", "C55", "C56", "B40", "UA",
						"_80", "H78", "HP", "F79", "F78", "ATT", "ATM", "J89", "K24", "K25", "K31", "E42", "E41", "K85",
						"_84", "N13", "N14", "N15", "N16", "N17", "N18", "N19", "N20", "N21", "N22", "N23", "HN",
						"PS"));

		typeUoMListMap.put("gs1:ElectricCharge",
				List.of("COU", "A8", "AMH", "TAH", "D77", "D86", "B26", "B86", "C40", "C71", "E09", "N95", "N94"));

		typeUoMListMap.put("gs1:ElectricCurrent",
				List.of("AMP", "B22", "H38", "_4K", "B84", "C39", "C70", "N96", "N97"));

		typeUoMListMap.put("gs1:Energy",
				List.of("JOU", "KJO", "A68", "C68", "D30", "GV", "_3B", "C15", "A70", "A13", "WHR", "MWH", "KWH", "GWH",
						"D32", "A53", "B71", "A85", "B29", "A57", "_85", "B38", "N46", "N47", "D60", "J75", "E14",
						"K51", "K53", "BTU", "J37", "N71", "N72", "J55"));

		typeUoMListMap.put("gs1:Force", List.of("NEW", "B47", "B73", "B92", "C20", "DU", "C78", "B37", "B51", "L40",
				"L94", "M75", "M76", "M77", "M78"));

		typeUoMListMap.put("gs1:Frequency", List.of("HTZ", "KHZ", "MHZ", "A86", "D29"));

		typeUoMListMap.put("gs1:Illuminance", List.of("B60", "LUX", "KLX", "P25", "P26", "P27"));

		typeUoMListMap.put("gs1:Inductance", List.of("_81", "C14", "B90", "C43", "C73", "P24"));

		typeUoMListMap.put("gs1:Length", List.of("MTR", "A11", "A71", "C45", "_4H", "A12", "DMT", "CMT", "MMT", "INH",
				"FOT", "YRD", "NMI", "A45", "HMT", "KMT", "B57", "AK", "M50", "M49", "X1", "M51"));

		typeUoMListMap.put("gs1:LuminousIntensity", List.of("CDL", "P33", "P34"));

		typeUoMListMap.put("gs1:MagneticFlux", List.of("WEB", "C33", "P11"));

		typeUoMListMap.put("gs1:MagneticFluxDensity", List.of("D33", "C29", "D81", "C48", "P13", "P12"));

		typeUoMListMap.put("gs1:MagneticVectorPotential", List.of("D59", "B56", "D60"));

		typeUoMListMap.put("gs1:Mass", List.of("KGM", "KTN", "LTN", "_2U", "TNE", "STN", "DTN", "STI", "LBR", "HGM",
				"ONZ", "DJ", "APZ", "GRM", "DG", "CGM", "MGM", "MC", "F13", "CWI", "CWA", "M86"));

		typeUoMListMap.put("gs1:MassConcentration",
				List.of("KMQ", "_23", "D41", "GJ", "B35", "GL", "A93", "GP", "B72", "B34", "H29", "M1", "GQ", "F23",
						"G31", "_87", "GE", "LA", "G32", "K41", "K71", "K84", "L37", "L38", "L39", "L65", "L92",
						"L93"));

		typeUoMListMap.put("gs1:MolarEnergy", List.of("B15", "B44"));

		typeUoMListMap.put("gs1:MolarMass", List.of("D74", "A94"));
		typeUoMListMap.put("gs1:MolarVolume", List.of("A40", "A37", "A36", "B58"));

		typeUoMListMap.put("gs1:Power", List.of("WTT", "KWT", "MAW", "A90", "C31", "D80", "F80", "A63", "A74", "B39",
				"HJ", "A25", "BHP", "K15", "K16", "K42", "N12"));

		typeUoMListMap.put("gs1:Pressure",
				List.of("PAL", "H75", "_74", "A89", "A97", "B96", "KPA", "MPA", "BAR", "MBR", "C55", "C56", "B40", "UA",
						"_80", "H78", "HP", "F79", "F78", "ATT", "ATM", "J89", "K24", "K25", "K31", "E42", "E41", "K85",
						"_84", "N13", "N14", "N15", "N16", "N17", "N18", "N19", "N20", "N21", "N22", "N23", "HN",
						"PS"));

		typeUoMListMap.put("gs1:Radioactivity", List.of("CUR", "MCU", "M5", "_2R", "BQL", "GBQ", "_2Q", "_4N", "H08"));

		typeUoMListMap.put("gs1:RelativeHumidity", List.of("P1", "_59", "_61", "_60", "E40", "NX", "GK", "NA", "J33",
				"L32", "M29", "K62", "L19", "J36", "H60", "H65", "J87", "L21", "J91"));

		typeUoMListMap.put("gs1:Resistance", List.of("OHM", "A87", "B75", "H44", "B49", "E45", "B94", "P22"));

		typeUoMListMap.put("gs1:SpecificVolume", List.of("A39", "_22", "H83", "KX", "N28", "N29", "N30"));

		typeUoMListMap.put("gs1:Speed", List.of("KMH", "MTS", "HM", "M57", "M58", "KNT", "M62", "H49", "_2M", "K14",
				"FR", "FS", "M63", "IU", "M61", "_2X"));

		typeUoMListMap.put("gs1:Temperature", List.of("KEL", "FAH", "CEL", "A48"));

		typeUoMListMap.put("gs1:Time", List.of("SEC", "MIN", "HUR", "DAY", "B52", "C26", "H70", "B98", "C47", "WEE",
				"MON", "ANN", "D42", "L95", "L96", "M56"));

		typeUoMListMap.put("gs1:Torque", List.of("NU", "B74", "B48", "D83", "B93", "DN", "J72", "B38", "F21", "J94",
				"L41", "M92", "M95", "M96", "M97"));

		typeUoMListMap.put("gs1:Voltage", List.of("VLT", "N99", "_2Z", "B78", "D82", "KVT"));

		typeUoMListMap.put("gs1:Volume",
				List.of("MTQ", "MAL", "LTR", "MMQ", "CMQ", "DMQ", "MLT", "HLT", "CLT", "DMA", "H19", "H20", "DLT",
						"_4G", "K6", "A44", "INQ", "FTQ", "YDQ", "GLI", "GLL", "PT", "PTI", "QTI", "PTL", "QTL", "PTD",
						"OZI", "QT", "J57", "L43", "L61", "L62", "L84", "L86", "OZA", "BUI", "BUA", "BLL", "BLD", "GLD",
						"QTD", "G26", "G21", "G24", "G25", "G23", "M67", "M68", "M69", "M70"));

		uomTypeMap = new HashMap<>();

		for (Entry<String, List<String>> entry : typeUoMListMap.entrySet()) {
			for (String value : entry.getValue()) {
				uomTypeMap.put(value, entry.getKey());
			}
		}

		// ------------------------------------------

//		unitToGroup = new HashMap<>();
//		unitToGroup.put("A95", "AbsorbedDose");
//		unitToGroup.put("C13", "AbsorbedDose");
//		unitToGroup.put("C80", "AbsorbedDose");
//		unitToGroup.put("A61", "AbsorbedDose");
//
//		unitToGroup.put("P54", "AbsorbedDoseRate");
//		unitToGroup.put("P55", "AbsorbedDoseRate");
//		unitToGroup.put("P56", "AbsorbedDoseRate");
//		unitToGroup.put("P57", "AbsorbedDoseRate");
//		unitToGroup.put("P58", "AbsorbedDoseRate");
//		unitToGroup.put("P59", "AbsorbedDoseRate");
//		unitToGroup.put("P60", "AbsorbedDoseRate");
//		unitToGroup.put("P61", "AbsorbedDoseRate");
//		unitToGroup.put("P62", "AbsorbedDoseRate");
//		unitToGroup.put("P63", "AbsorbedDoseRate");
//		unitToGroup.put("P64", "AbsorbedDoseRate");
//
//		unitToGroup.put("MSK", "Acceleration");
//		unitToGroup.put("A76", "Acceleration");
//		unitToGroup.put("M38", "Acceleration");
//		unitToGroup.put("M39", "Acceleration");
//		unitToGroup.put("M41", "Acceleration");
//		unitToGroup.put("A73", "Acceleration");
//		unitToGroup.put("IV", "Acceleration");
//		unitToGroup.put("K40", "Acceleration");
//		unitToGroup.put("M40", "Acceleration");
//		unitToGroup.put("M42", "Acceleration");
//
//		unitToGroup.put("C34", "AmountOfSubstance");
//		unitToGroup.put("B45", "AmountOfSubstance");
//		unitToGroup.put("C18", "AmountOfSubstance");
//		unitToGroup.put("FH", "AmountOfSubstance");
//
//		unitToGroup.put("C81", "Angle");
//		unitToGroup.put("C25", "Angle");
//		unitToGroup.put("B97", "Angle");
//		unitToGroup.put("A91", "Angle");
//		unitToGroup.put("DD", "Angle");
//		unitToGroup.put("D61", "Angle");
//		unitToGroup.put("D62", "Angle");
//		unitToGroup.put("M43", "Angle");
//		unitToGroup.put("M44", "Angle");
//
//		unitToGroup.put("MTK", "Area");
//		unitToGroup.put("KMK", "Area");
//		unitToGroup.put("H30", "Area");
//		unitToGroup.put("DAA", "Area");
//		unitToGroup.put("CMK", "Area");
//		unitToGroup.put("DMK", "Area");
//		unitToGroup.put("H16", "Area");
//		unitToGroup.put("H18", "Area");
//		unitToGroup.put("MMK", "Area");
//		unitToGroup.put("ARE", "Area");
//		unitToGroup.put("HAR", "Area");
//		unitToGroup.put("INK", "Area");
//		unitToGroup.put("FTK", "Area");
//		unitToGroup.put("YDK", "Area");
//		unitToGroup.put("MIK", "Area");
//		unitToGroup.put("M48", "Area");
//		unitToGroup.put("ACR", "Area");
//		unitToGroup.put("M47", "Area");
//
//		unitToGroup.put("FAR", "Capacitance");
//		unitToGroup.put("H48", "Capacitance");
//		unitToGroup.put("C10", "Capacitance");
//		unitToGroup.put("_40", "Capacitance");
//		unitToGroup.put("C41", "Capacitance");
//		unitToGroup.put("_4T", "Capacitance");
//		unitToGroup.put("N90", "Capacitance");
//
//		unitToGroup.put("COU", "Charge");
//		unitToGroup.put("A8", "Charge");
//		unitToGroup.put("AMH", "Charge");
//		unitToGroup.put("TAH", "Charge");
//		unitToGroup.put("D77", "Charge");
//		unitToGroup.put("D86", "Charge");
//		unitToGroup.put("B26", "Charge");
//		unitToGroup.put("B86", "Charge");
//		unitToGroup.put("C40", "Charge");
//		unitToGroup.put("C71", "Charge");
//		unitToGroup.put("E09", "Charge");
//		unitToGroup.put("N95", "Charge");
//		unitToGroup.put("N94", "Charge");
//
//		unitToGroup.put("SIE", "Conductance");
//		unitToGroup.put("B53", "Conductance");
//		unitToGroup.put("C27", "Conductance");
//		unitToGroup.put("B99", "Conductance");
//		unitToGroup.put("N92", "Conductance");
//		unitToGroup.put("NQ", "Conductance");
//		unitToGroup.put("NR", "Conductance");
//
//		unitToGroup.put("AMP", "Current");
//		unitToGroup.put("B22", "Current");
//		unitToGroup.put("H38", "Current");
//		unitToGroup.put("_4K", "Current");
//		unitToGroup.put("B84", "Current");
//		unitToGroup.put("C39", "Current");
//		unitToGroup.put("C70", "Current");
//		unitToGroup.put("N96", "Current");
//		unitToGroup.put("N97", "Current");
//
//		unitToGroup.put("KMQ", "DensityHumidity");
//		unitToGroup.put("_23", "DensityHumidity");
//		unitToGroup.put("D41", "DensityHumidity");
//		unitToGroup.put("GJ", "DensityHumidity");
//		unitToGroup.put("B35", "DensityHumidity");
//		unitToGroup.put("GL", "DensityHumidity");
//		unitToGroup.put("A93", "DensityHumidity");
//		unitToGroup.put("GP", "DensityHumidity");
//		unitToGroup.put("B72", "DensityHumidity");
//		unitToGroup.put("B34", "DensityHumidity");
//		unitToGroup.put("H29", "DensityHumidity");
//		unitToGroup.put("M1", "DensityHumidity");
//		unitToGroup.put("GQ", "DensityHumidity");
//		unitToGroup.put("F23", "DensityHumidity");
//		unitToGroup.put("G31", "DensityHumidity");
//		unitToGroup.put("_87", "DensityHumidity");
//		unitToGroup.put("GE", "DensityHumidity");
//		unitToGroup.put("LA", "DensityHumidity");
//		unitToGroup.put("G32", "DensityHumidity");
//		unitToGroup.put("K41", "DensityHumidity");
//		unitToGroup.put("K71", "DensityHumidity");
//		unitToGroup.put("K84", "DensityHumidity");
//		unitToGroup.put("L37", "DensityHumidity");
//		unitToGroup.put("L38", "DensityHumidity");
//		unitToGroup.put("L39", "DensityHumidity");
//		unitToGroup.put("L65", "DensityHumidity");
//		unitToGroup.put("L92", "DensityHumidity");
//		unitToGroup.put("L93", "DensityHumidity");
//
//		unitToGroup.put("P1", "DimensionlessConcentration");
//		unitToGroup.put("_59", "DimensionlessConcentration");
//		unitToGroup.put("_61", "DimensionlessConcentration");
//		unitToGroup.put("_60", "DimensionlessConcentration");
//		unitToGroup.put("E40", "DimensionlessConcentration");
//		unitToGroup.put("NX", "DimensionlessConcentration");
//		unitToGroup.put("GK", "DimensionlessConcentration");
//		unitToGroup.put("NA", "DimensionlessConcentration");
//		unitToGroup.put("J33", "DimensionlessConcentration");
//		unitToGroup.put("L32", "DimensionlessConcentration");
//		unitToGroup.put("M29", "DimensionlessConcentration");
//		unitToGroup.put("K62", "DimensionlessConcentration");
//		unitToGroup.put("L19", "DimensionlessConcentration");
//		unitToGroup.put("J36", "DimensionlessConcentration");
//		unitToGroup.put("H60", "DimensionlessConcentration");
//		unitToGroup.put("H65", "DimensionlessConcentration");
//		unitToGroup.put("J87", "DimensionlessConcentration");
//		unitToGroup.put("L21", "DimensionlessConcentration");
//		unitToGroup.put("J91", "DimensionlessConcentration");
//
//		unitToGroup.put("D13", "EffectiveDose");
//		unitToGroup.put("C28", "EffectiveDose");
//		unitToGroup.put("D91", "EffectiveDose");
//		unitToGroup.put("L31", "EffectiveDose");
//
//		unitToGroup.put("P65", "EffectiveDoseRate");
//		unitToGroup.put("P66", "EffectiveDoseRate");
//		unitToGroup.put("P67", "EffectiveDoseRate");
//		unitToGroup.put("P68", "EffectiveDoseRate");
//		unitToGroup.put("P69", "EffectiveDoseRate");
//		unitToGroup.put("P70", "EffectiveDoseRate");
//		unitToGroup.put("P71", "EffectiveDoseRate");
//		unitToGroup.put("P72", "EffectiveDoseRate");
//		unitToGroup.put("P73", "EffectiveDoseRate");
//		unitToGroup.put("P74", "EffectiveDoseRate");
//		unitToGroup.put("P75", "EffectiveDoseRate");
//		unitToGroup.put("P76", "EffectiveDoseRate");
//		unitToGroup.put("P77", "EffectiveDoseRate");
//
//		unitToGroup.put("JOU", "Energy");
//		unitToGroup.put("KJO", "Energy");
//		unitToGroup.put("A68", "Energy");
//		unitToGroup.put("C68", "Energy");
//		unitToGroup.put("D30", "Energy");
//		unitToGroup.put("GV", "Energy");
//		unitToGroup.put("_3B", "Energy");
//		unitToGroup.put("C15", "Energy");
//		unitToGroup.put("A70", "Energy");
//		unitToGroup.put("A13", "Energy");
//		unitToGroup.put("WHR", "Energy");
//		unitToGroup.put("MWH", "Energy");
//		unitToGroup.put("KMH", "Energy");
//		unitToGroup.put("GWH", "Energy");
//		unitToGroup.put("D32", "Energy");
//		unitToGroup.put("A53", "Energy");
//		unitToGroup.put("B71", "Energy");
//		unitToGroup.put("A85", "Energy");
//		unitToGroup.put("B29", "Energy");
//		unitToGroup.put("A57", "Energy");
//		unitToGroup.put("_85", "Energy");
//		unitToGroup.put("B38", "Energy");
//		unitToGroup.put("N46", "Energy");
//		unitToGroup.put("N47", "Energy");
//		unitToGroup.put("D60", "Energy");
//		unitToGroup.put("J75", "Energy");
//		unitToGroup.put("E14", "Energy");
//		unitToGroup.put("K51", "Energy");
//		unitToGroup.put("K53", "Energy");
//		unitToGroup.put("BTU", "Energy");
//		unitToGroup.put("J37", "Energy");
//		unitToGroup.put("N71", "Energy");
//		unitToGroup.put("N72", "Energy");
//		unitToGroup.put("J55", "Energy");
//
//		unitToGroup.put("NEW", "Force");
//		unitToGroup.put("B47", "Force");
//		unitToGroup.put("B73", "Force");
//		unitToGroup.put("B92", "Force");
//		unitToGroup.put("C20", "Force");
//		unitToGroup.put("DU", "Force");
//		unitToGroup.put("C78", "Force");
//		unitToGroup.put("B37", "Force");
//		unitToGroup.put("B51", "Force");
//		unitToGroup.put("L40", "Force");
//		unitToGroup.put("L94", "Force");
//		unitToGroup.put("M75", "Force");
//		unitToGroup.put("M76", "Force");
//		unitToGroup.put("M77", "Force");
//		unitToGroup.put("M78", "Force");
//
//		unitToGroup.put("HTZ", "Frequency");
//		unitToGroup.put("KHZ", "Frequency");
//		unitToGroup.put("MHZ", "Frequency");
//		unitToGroup.put("A86", "Frequency");
//		unitToGroup.put("D29", "Frequency");
//
//		unitToGroup.put("B60", "Illuminance");
//		unitToGroup.put("LUX", "Illuminance");
//		unitToGroup.put("KLX", "Illuminance");
//		unitToGroup.put("P25", "Illuminance");
//		unitToGroup.put("P26", "Illuminance");
//		unitToGroup.put("P27", "Illuminance");
//
//		unitToGroup.put("_81", "Inductance");
//		unitToGroup.put("C14", "Inductance");
//		unitToGroup.put("B90", "Inductance");
//		unitToGroup.put("C43", "Inductance");
//		unitToGroup.put("C73", "Inductance");
//		unitToGroup.put("P24", "Inductance");
//
//		unitToGroup.put("MTR", "Length");
//		unitToGroup.put("A11", "Length");
//		unitToGroup.put("A71", "Length");
//		unitToGroup.put("C45", "Length");
//		unitToGroup.put("_4H", "Length");
//		unitToGroup.put("A12", "Length");
//		unitToGroup.put("DMT", "Length");
//		unitToGroup.put("CMT", "Length");
//		unitToGroup.put("MMT", "Length");
//		unitToGroup.put("INH", "Length");
//		unitToGroup.put("FOT", "Length");
//		unitToGroup.put("YRD", "Length");
//		unitToGroup.put("NMI", "Length");
//		unitToGroup.put("A45", "Length");
//		unitToGroup.put("HMT", "Length");
//		unitToGroup.put("KMT", "Length");
//		unitToGroup.put("B57", "Length");
//		unitToGroup.put("AK", "Length");
//		unitToGroup.put("M50", "Length");
//		unitToGroup.put("M49", "Length");
//		unitToGroup.put("X1", "Length");
//		unitToGroup.put("M51", "Length");
//
//		unitToGroup.put("CDL", "LuminousIntensity");
//		unitToGroup.put("P33", "LuminousIntensity");
//		unitToGroup.put("P34", "LuminousIntensity");
//
//		unitToGroup.put("WEB", "MagneticFlux");
//		unitToGroup.put("C33", "MagneticFlux");
//		unitToGroup.put("P11", "MagneticFlux");
//
//		unitToGroup.put("D33", "MagneticFluxDensity");
//		unitToGroup.put("C29", "MagneticFluxDensity");
//		unitToGroup.put("D81", "MagneticFluxDensity");
//		unitToGroup.put("C48", "MagneticFluxDensity");
//		unitToGroup.put("P13", "MagneticFluxDensity");
//		unitToGroup.put("P12", "MagneticFluxDensity");
//
//		unitToGroup.put("D59", "MagneticVectorPotential");
//		unitToGroup.put("B56", "MagneticVectorPotential");
//		unitToGroup.put("D60", "MagneticVectorPotential");
//
//		unitToGroup.put("KGM", "Mass");
//		unitToGroup.put("KTN", "Mass");
//		unitToGroup.put("LTN", "Mass");
//		unitToGroup.put("_2U", "Mass");
//		unitToGroup.put("TNE", "Mass");
//		unitToGroup.put("STN", "Mass");
//		unitToGroup.put("DTN", "Mass");
//		unitToGroup.put("STI", "Mass");
//		unitToGroup.put("LBR", "Mass");
//		unitToGroup.put("HGM", "Mass");
//		unitToGroup.put("ONZ", "Mass");
//		unitToGroup.put("DJ", "Mass");
//		unitToGroup.put("APZ", "Mass");
//		unitToGroup.put("GRM", "Mass");
//		unitToGroup.put("DG", "Mass");
//		unitToGroup.put("CGM", "Mass");
//		unitToGroup.put("MGM", "Mass");
//		unitToGroup.put("MC", "Mass");
//		unitToGroup.put("F13", "Mass");
//		unitToGroup.put("CWI", "Mass");
//		unitToGroup.put("CWA", "Mass");
//		unitToGroup.put("M86", "Mass");
//
//		unitToGroup.put("C36", "MolarConcentration");
//		unitToGroup.put("M33", "MolarConcentration");
//		unitToGroup.put("C38", "MolarConcentration");
//		unitToGroup.put("C35", "MolarConcentration");
//		unitToGroup.put("B46", "MolarConcentration");
//
//		unitToGroup.put("D74", "MolarMass");
//		unitToGroup.put("A94", "MolarMass");
//
//		unitToGroup.put("B15", "MolarThermodynamicEnergy");
//		unitToGroup.put("B44", "MolarThermodynamicEnergy");
//
//		unitToGroup.put("A40", "MolarVolume");
//		unitToGroup.put("A37", "MolarVolume");
//		unitToGroup.put("A36", "MolarVolume");
//		unitToGroup.put("B58", "MolarVolume");
//
//		unitToGroup.put("WTT", "Power");
//		unitToGroup.put("KWT", "Power");
//		unitToGroup.put("MAW", "Power");
//		unitToGroup.put("A90", "Power");
//		unitToGroup.put("C31", "Power");
//		unitToGroup.put("D80", "Power");
//		unitToGroup.put("F80", "Power");
//		unitToGroup.put("A63", "Power");
//		unitToGroup.put("A74", "Power");
//		unitToGroup.put("B39", "Power");
//		unitToGroup.put("HJ", "Power");
//		unitToGroup.put("A25", "Power");
//		unitToGroup.put("BHP", "Power");
//		unitToGroup.put("K15", "Power");
//		unitToGroup.put("K16", "Power");
//		unitToGroup.put("K42", "Power");
//		unitToGroup.put("N12", "Power");
//
//		unitToGroup.put("PAL", "Pressure");
//		unitToGroup.put("H75", "Pressure");
//		unitToGroup.put("_74", "Pressure");
//		unitToGroup.put("A89", "Pressure");
//		unitToGroup.put("A97", "Pressure");
//		unitToGroup.put("B96", "Pressure");
//		unitToGroup.put("KPA", "Pressure");
//		unitToGroup.put("MPA", "Pressure");
//		unitToGroup.put("BAR", "Pressure");
//		unitToGroup.put("MBR", "Pressure");
//		unitToGroup.put("C55", "Pressure");
//		unitToGroup.put("C56", "Pressure");
//		unitToGroup.put("B40", "Pressure");
//		unitToGroup.put("UA", "Pressure");
//		unitToGroup.put("_80", "Pressure");
//		unitToGroup.put("H78", "Pressure");
//		unitToGroup.put("HP", "Pressure");
//		unitToGroup.put("F79", "Pressure");
//		unitToGroup.put("F78", "Pressure");
//		unitToGroup.put("ATT", "Pressure");
//		unitToGroup.put("ATM", "Pressure");
//		unitToGroup.put("J89", "Pressure");
//		unitToGroup.put("K24", "Pressure");
//		unitToGroup.put("K25", "Pressure");
//		unitToGroup.put("K31", "Pressure");
//		unitToGroup.put("E42", "Pressure");
//		unitToGroup.put("E41", "Pressure");
//		unitToGroup.put("K85", "Pressure");
//		unitToGroup.put("_84", "Pressure");
//		unitToGroup.put("N13", "Pressure");
//		unitToGroup.put("N14", "Pressure");
//		unitToGroup.put("N15", "Pressure");
//		unitToGroup.put("N16", "Pressure");
//		unitToGroup.put("N17", "Pressure");
//		unitToGroup.put("N18", "Pressure");
//		unitToGroup.put("N19", "Pressure");
//		unitToGroup.put("N20", "Pressure");
//		unitToGroup.put("N21", "Pressure");
//		unitToGroup.put("N22", "Pressure");
//		unitToGroup.put("N23", "Pressure");
//		unitToGroup.put("HN", "Pressure");
//		unitToGroup.put("PS", "Pressure");
//
//		unitToGroup.put("CUR", "RadioActivity");
//		unitToGroup.put("MCU", "RadioActivity");
//		unitToGroup.put("M5", "RadioActivity");
//		unitToGroup.put("_2R", "RadioActivity");
//		unitToGroup.put("BQL", "RadioActivity");
//		unitToGroup.put("GBQ", "RadioActivity");
//		unitToGroup.put("_2Q", "RadioActivity");
//		unitToGroup.put("_4N", "RadioActivity");
//		unitToGroup.put("H08", "RadioActivity");
//
//		unitToGroup.put("OHM", "Resistance");
//		unitToGroup.put("A87", "Resistance");
//		unitToGroup.put("B75", "Resistance");
//		unitToGroup.put("H44", "Resistance");
//		unitToGroup.put("B49", "Resistance");
//		unitToGroup.put("E45", "Resistance");
//		unitToGroup.put("B94", "Resistance");
//		unitToGroup.put("P22", "Resistance");
//
//		unitToGroup.put("A39", "SpecificVolume");
//		unitToGroup.put("_22", "SpecificVolume");
//		unitToGroup.put("H83", "SpecificVolume");
//		unitToGroup.put("KX", "SpecificVolume");
//		unitToGroup.put("N28", "SpecificVolume");
//		unitToGroup.put("N29", "SpecificVolume");
//		unitToGroup.put("N30", "SpecificVolume");
//
//		unitToGroup.put("KMH", "Speed");
//		unitToGroup.put("MTS", "Speed");
//		unitToGroup.put("HM", "Speed");
//		unitToGroup.put("M57", "Speed");
//		unitToGroup.put("M58", "Speed");
//		unitToGroup.put("KNT", "Speed");
//		unitToGroup.put("M62", "Speed");
//		unitToGroup.put("H49", "Speed");
//		unitToGroup.put("_2M", "Speed");
//		unitToGroup.put("K14", "Speed");
//		unitToGroup.put("FR", "Speed");
//		unitToGroup.put("FS", "Speed");
//		unitToGroup.put("M63", "Speed");
//		unitToGroup.put("IU", "Speed");
//		unitToGroup.put("M61", "Speed");
//		unitToGroup.put("_2X", "Speed");
//
//		unitToGroup.put("KEL", "Temperature");
//		unitToGroup.put("FAH", "Temperature");
//		unitToGroup.put("CEL", "Temperature");
//		unitToGroup.put("A48", "Temperature");
//
//		unitToGroup.put("SEC", "Time");
//		unitToGroup.put("MIN", "Time");
//		unitToGroup.put("HUR", "Time");
//		unitToGroup.put("DAY", "Time");
//		unitToGroup.put("B52", "Time");
//		unitToGroup.put("C26", "Time");
//		unitToGroup.put("H70", "Time");
//		unitToGroup.put("B98", "Time");
//		unitToGroup.put("C47", "Time");
//		unitToGroup.put("WEE", "Time");
//		unitToGroup.put("MON", "Time");
//		unitToGroup.put("ANN", "Time");
//		unitToGroup.put("D42", "Time");
//		unitToGroup.put("L95", "Time");
//		unitToGroup.put("L96", "Time");
//		unitToGroup.put("M56", "Time");
//
//		unitToGroup.put("NU", "Torque");
//		unitToGroup.put("B74", "Torque");
//		unitToGroup.put("B48", "Torque");
//		unitToGroup.put("D83", "Torque");
//		unitToGroup.put("B93", "Torque");
//		unitToGroup.put("DN", "Torque");
//		unitToGroup.put("J72", "Torque");
//		unitToGroup.put("B38", "Torque");
//		unitToGroup.put("F21", "Torque");
//		unitToGroup.put("J94", "Torque");
//		unitToGroup.put("L41", "Torque");
//		unitToGroup.put("M92", "Torque");
//		unitToGroup.put("M95", "Torque");
//		unitToGroup.put("M96", "Torque");
//		unitToGroup.put("M97", "Torque");
//
//		unitToGroup.put("VLT", "Voltage");
//		unitToGroup.put("N99", "Voltage");
//		unitToGroup.put("_2Z", "Voltage");
//		unitToGroup.put("B78", "Voltage");
//		unitToGroup.put("D82", "Voltage");
//		unitToGroup.put("KVT", "Voltage");
//
//		unitToGroup.put("MTQ", "Volume");
//		unitToGroup.put("MAL", "Volume");
//		unitToGroup.put("LTR", "Volume");
//		unitToGroup.put("MMQ", "Volume");
//		unitToGroup.put("CMQ", "Volume");
//		unitToGroup.put("DMQ", "Volume");
//		unitToGroup.put("MLT", "Volume");
//		unitToGroup.put("CLT", "Volume");
//		unitToGroup.put("DMA", "Volume");
//		unitToGroup.put("H19", "Volume");
//		unitToGroup.put("H20", "Volume");
//		unitToGroup.put("DLT", "Volume");
//		unitToGroup.put("_4G", "Volume");
//		unitToGroup.put("K6", "Volume");
//		unitToGroup.put("A44", "Volume");
//		unitToGroup.put("INQ", "Volume");
//		unitToGroup.put("FTQ", "Volume");
//		unitToGroup.put("YDQ", "Volume");
//		unitToGroup.put("GLI", "Volume");
//		unitToGroup.put("GLL", "Volume");
//		unitToGroup.put("PT", "Volume");
//		unitToGroup.put("PTI", "Volume");
//		unitToGroup.put("QTI", "Volume");
//		unitToGroup.put("PTL", "Volume");
//		unitToGroup.put("QTL", "Volume");
//		unitToGroup.put("PTD", "Volume");
//		unitToGroup.put("OZI", "Volume");
//		unitToGroup.put("QT", "Volume");
//		unitToGroup.put("J57", "Volume");
//		unitToGroup.put("L43", "Volume");
//		unitToGroup.put("L61", "Volume");
//		unitToGroup.put("L62", "Volume");
//		unitToGroup.put("L84", "Volume");
//		unitToGroup.put("L86", "Volume");
//		unitToGroup.put("OZA", "Volume");
//		unitToGroup.put("BUI", "Volume");
//		unitToGroup.put("BUA", "Volume");
//		unitToGroup.put("BLL", "Volume");
//		unitToGroup.put("BLD", "Volume");
//		unitToGroup.put("GLD", "Volume");
//		unitToGroup.put("QTD", "Volume");
//		unitToGroup.put("G26", "Volume");
//		unitToGroup.put("G21", "Volume");
//		unitToGroup.put("G24", "Volume");
//		unitToGroup.put("G25", "Volume");
//		unitToGroup.put("G23", "Volume");
//		unitToGroup.put("M67", "Volume");
//		unitToGroup.put("M68", "Volume");
//		unitToGroup.put("M69", "Volume");
//		unitToGroup.put("M70", "Volume");
	}

	public String getRepresentativeUoMFromType(String measurementType) {
		return typeUoMListMap.get(measurementType).get(0);
	}

	public String getRepresentativeUoMFromUoM(String uom) {
		return typeUoMListMap.get(uomTypeMap.get(uom)).get(0);
	}

	public String getType(String uom) throws QueryParameterException {
		String type = uomTypeMap.get(uom);
		if (type != null)
			return type;
		else
			throw new QueryParameterException(uom + " is not of " + uomTypeMap.keySet());
	}

	public void checkUnitOfMeasure(String measurementType, String uom) throws ValidationException {
		if (!typeUoMListMap.get(measurementType).contains(uom)) {
			throw new ValidationException(uom + " is not of " + measurementType);
		}
	}

	/**
	 * get a value of a representative type of uom where each unit group has a
	 * representative value to be stored
	 * 
	 * @param uom
	 * @param value
	 * @return
	 */
	public Double getRepresentativeValue(String measurementType, String uom, Double value) {

		if (measurementType.equals("gs1:AbsoluteHumidity")) {
			AbsoluteHumidity val = new AbsoluteHumidity(uom, value);
			int idx = val.getType().ordinal();
			return (value * AbsoluteHumidity.multipliers[idx] + AbsoluteHumidity.offsets[idx] - AbsoluteHumidity.ro)
					/ AbsoluteHumidity.rm;
		} else if (measurementType.equals("gs1:AbsorbedDose")) {
			AbsorbedDose val = new AbsorbedDose(uom, value);
			int idx = val.getType().ordinal();
			return (value * AbsorbedDose.multipliers[idx] + AbsorbedDose.offsets[idx] - AbsorbedDose.ro)
					/ AbsorbedDose.rm;
		} else if (measurementType.equals("gs1:AbsorbedDoseRate")) {
			AbsorbedDoseRate val = new AbsorbedDoseRate(uom, value);
			int idx = val.getType().ordinal();
			return (value * AbsorbedDoseRate.multipliers[idx] + AbsorbedDoseRate.offsets[idx] - AbsorbedDoseRate.ro)
					/ AbsorbedDoseRate.rm;
		} else if (measurementType.equals("gs1:Acceleration")) {
			Acceleration val = new Acceleration(uom, value);
			int idx = val.getType().ordinal();
			return (value * Acceleration.multipliers[idx] + Acceleration.offsets[idx] - Acceleration.ro)
					/ Acceleration.rm;
		} else if (measurementType.equals("gs1:Altitude")) {
			Altitude val = new Altitude(uom, value);
			int idx = val.getType().ordinal();
			return (value * Altitude.multipliers[idx] + Altitude.offsets[idx] - Altitude.ro) / Acceleration.rm;
		} else if (measurementType.equals("gs1:AmountOfSubstance")) {
			AmountOfSubstance val = new AmountOfSubstance(uom, value);
			int idx = val.getType().ordinal();
			return (value * AmountOfSubstance.multipliers[idx] + AmountOfSubstance.offsets[idx] - AmountOfSubstance.ro)
					/ AmountOfSubstance.rm;
		} else if (measurementType.equals("gs1:AmountOfSubstancePerUnitVolume")) {
			AmountOfSubstancePerUnitVolume val = new AmountOfSubstancePerUnitVolume(uom, value);
			int idx = val.getType().ordinal();
			return (value * AmountOfSubstancePerUnitVolume.multipliers[idx]
					+ AmountOfSubstancePerUnitVolume.offsets[idx] - AmountOfSubstancePerUnitVolume.ro)
					/ AmountOfSubstancePerUnitVolume.rm;
		} else if (measurementType.equals("gs1:Angle")) {
			Angle val = new Angle(uom, value);
			int idx = val.getType().ordinal();
			return (value * Angle.multipliers[idx] + Angle.offsets[idx] - Angle.ro) / Angle.rm;
		} else if (measurementType.equals("gs1:Area")) {
			Area val = new Area(uom, value);
			int idx = val.getType().ordinal();
			return (value * Area.multipliers[idx] + Area.offsets[idx] - Area.ro) / Area.rm;
		} else if (measurementType.equals("gs1:Capacitance")) {
			Capacitance val = new Capacitance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Capacitance.multipliers[idx] + Capacitance.offsets[idx] - Capacitance.ro) / Capacitance.rm;
		} else if (measurementType.equals("gs1:Conductance")) {
			Conductance val = new Conductance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Conductance.multipliers[idx] + Conductance.offsets[idx] - Conductance.ro) / Conductance.rm;
		} else if (measurementType.equals("gs1:Count")) {
			Count val = new Count(uom, value);
			int idx = val.getType().ordinal();
			return (value * Count.multipliers[idx] + Count.offsets[idx] - Count.ro) / Count.rm;
		} else if (measurementType.equals("gs1:Density")) {
			Density val = new Density(uom, value);
			int idx = val.getType().ordinal();
			return (value * Density.multipliers[idx] + Density.offsets[idx] - Density.ro) / Density.rm;
		} else if (measurementType.equals("gs1:Dimensionless")) {
			Dimensionless val = new Dimensionless(uom, value);
			int idx = val.getType().ordinal();
			return (value * Dimensionless.multipliers[idx] + Dimensionless.offsets[idx] - Dimensionless.ro)
					/ Dimensionless.rm;
		} else if (measurementType.equals("gs1:DoseEquivalent")) {
			DoseEquivalent val = new DoseEquivalent(uom, value);
			int idx = val.getType().ordinal();
			return (value * DoseEquivalent.multipliers[idx] + DoseEquivalent.offsets[idx] - DoseEquivalent.ro)
					/ DoseEquivalent.rm;
		} else if (measurementType.equals("gs1:DoseEquivalentRate")) {
			DoseEquivalentRate val = new DoseEquivalentRate(uom, value);
			int idx = val.getType().ordinal();
			return (value * DoseEquivalentRate.multipliers[idx] + DoseEquivalentRate.offsets[idx]
					- DoseEquivalentRate.ro) / DoseEquivalentRate.rm;
		} else if (measurementType.equals("gs1:DynamicViscosity")) {
			DynamicViscosity val = new DynamicViscosity(uom, value);
			int idx = val.getType().ordinal();
			return (value * DynamicViscosity.multipliers[idx] + DynamicViscosity.offsets[idx] - DynamicViscosity.ro)
					/ DynamicViscosity.rm;
		} else if (measurementType.equals("gs1:ElectricCharge")) {
			ElectricCharge val = new ElectricCharge(uom, value);
			int idx = val.getType().ordinal();
			return (value * ElectricCharge.multipliers[idx] + ElectricCharge.offsets[idx] - ElectricCharge.ro)
					/ ElectricCharge.rm;
		} else if (measurementType.equals("gs1:ElectricCurrent")) {
			ElectricCurrent val = new ElectricCurrent(uom, value);
			int idx = val.getType().ordinal();
			return (value * ElectricCurrent.multipliers[idx] + ElectricCurrent.offsets[idx] - ElectricCurrent.ro)
					/ ElectricCurrent.rm;
		} else if (measurementType.equals("gs1:Energy")) {
			Energy val = new Energy(uom, value);
			int idx = val.getType().ordinal();
			return (value * Energy.multipliers[idx] + Energy.offsets[idx] - Energy.ro) / Energy.rm;
		} else if (measurementType.equals("gs1:Force")) {
			Force val = new Force(uom, value);
			int idx = val.getType().ordinal();
			return (value * Force.multipliers[idx] + Force.offsets[idx] - Force.ro) / Force.rm;
		} else if (measurementType.equals("gs1:Frequency")) {
			Frequency val = new Frequency(uom, value);
			int idx = val.getType().ordinal();
			return (value * Frequency.multipliers[idx] + Frequency.offsets[idx] - Frequency.ro) / Frequency.rm;
		} else if (measurementType.equals("gs1:Illuminance")) {
			Illuminance val = new Illuminance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Illuminance.multipliers[idx] + Illuminance.offsets[idx] - Illuminance.ro) / Illuminance.rm;
		} else if (measurementType.equals("gs1:Inductance")) {
			Inductance val = new Inductance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Inductance.multipliers[idx] + Inductance.offsets[idx] - Inductance.ro) / Inductance.rm;
		} else if (measurementType.equals("gs1:Length")) {
			Length val = new Length(uom, value);
			int idx = val.getType().ordinal();
			return (value * Length.multipliers[idx] + Length.offsets[idx] - Length.ro) / Length.rm;
		} else if (measurementType.equals("gs1:LuminousIntensity")) {
			LuminousIntensity val = new LuminousIntensity(uom, value);
			int idx = val.getType().ordinal();
			return (value * LuminousIntensity.multipliers[idx] + LuminousIntensity.offsets[idx] - LuminousIntensity.ro)
					/ LuminousIntensity.rm;
		} else if (measurementType.equals("gs1:MagneticFlux")) {
			MagneticFlux val = new MagneticFlux(uom, value);
			int idx = val.getType().ordinal();
			return (value * MagneticFlux.multipliers[idx] + MagneticFlux.offsets[idx] - MagneticFlux.ro)
					/ MagneticFlux.rm;
		} else if (measurementType.equals("gs1:MagneticFluxDensity")) {
			MagneticFluxDensity val = new MagneticFluxDensity(uom, value);
			int idx = val.getType().ordinal();
			return (value * MagneticFluxDensity.multipliers[idx] + MagneticFluxDensity.offsets[idx]
					- MagneticFluxDensity.ro) / MagneticFluxDensity.rm;
		} else if (measurementType.equals("gs1:MagneticVectorPotential")) {
			MagneticVectorPotential val = new MagneticVectorPotential(uom, value);
			int idx = val.getType().ordinal();
			return (value * MagneticVectorPotential.multipliers[idx] + MagneticVectorPotential.offsets[idx]
					- MagneticVectorPotential.ro) / MagneticVectorPotential.rm;
		} else if (measurementType.equals("gs1:Mass")) {
			Mass val = new Mass(uom, value);
			int idx = val.getType().ordinal();
			return (value * Mass.multipliers[idx] + Mass.offsets[idx] - Mass.ro) / Mass.rm;
		} else if (measurementType.equals("gs1:MassConcentration")) {
			MassConcentration val = new MassConcentration(uom, value);
			int idx = val.getType().ordinal();
			return (value * MassConcentration.multipliers[idx] + MassConcentration.offsets[idx] - MassConcentration.ro)
					/ MassConcentration.rm;
		} else if (measurementType.equals("gs1:MolarEnergy")) {
			MolarEnergy val = new MolarEnergy(uom, value);
			int idx = val.getType().ordinal();
			return (value * MolarEnergy.multipliers[idx] + MolarEnergy.offsets[idx] - MolarEnergy.ro) / MolarEnergy.rm;
		} else if (measurementType.equals("gs1:MolarMass")) {
			MolarMass val = new MolarMass(uom, value);
			int idx = val.getType().ordinal();
			return (value * MolarMass.multipliers[idx] + MolarMass.offsets[idx] - MolarMass.ro) / MolarMass.rm;
		} else if (measurementType.equals("gs1:MolarVolume")) {
			MolarVolume val = new MolarVolume(uom, value);
			int idx = val.getType().ordinal();
			return (value * MolarVolume.multipliers[idx] + MolarVolume.offsets[idx] - MolarVolume.ro) / MolarVolume.rm;
		} else if (measurementType.equals("gs1:Power")) {
			Power val = new Power(uom, value);
			int idx = val.getType().ordinal();
			return (value * Power.multipliers[idx] + Power.offsets[idx] - Power.ro) / Power.rm;
		} else if (measurementType.equals("gs1:Pressure")) {
			Pressure val = new Pressure(uom, value);
			int idx = val.getType().ordinal();
			return (value * Pressure.multipliers[idx] + Pressure.offsets[idx] - Pressure.ro) / Pressure.rm;
		} else if (measurementType.equals("gs1:Radioactivity")) {
			Radioactivity val = new Radioactivity(uom, value);
			int idx = val.getType().ordinal();
			return (value * Radioactivity.multipliers[idx] + Radioactivity.offsets[idx] - Radioactivity.ro)
					/ Radioactivity.rm;
		} else if (measurementType.equals("gs1:RelativeHumidity")) {
			RelativeHumidity val = new RelativeHumidity(uom, value);
			int idx = val.getType().ordinal();
			return (value * RelativeHumidity.multipliers[idx] + RelativeHumidity.offsets[idx] - RelativeHumidity.ro)
					/ RelativeHumidity.rm;
		} else if (measurementType.equals("gs1:Resistance")) {
			Resistance val = new Resistance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Resistance.multipliers[idx] + Resistance.offsets[idx] - Resistance.ro) / Resistance.rm;
		} else if (measurementType.equals("gs1:SpecificVolume")) {
			SpecificVolume val = new SpecificVolume(uom, value);
			int idx = val.getType().ordinal();
			return (value * SpecificVolume.multipliers[idx] + SpecificVolume.offsets[idx] - SpecificVolume.ro)
					/ SpecificVolume.rm;
		} else if (measurementType.equals("gs1:Speed")) {
			Speed val = new Speed(uom, value);
			int idx = val.getType().ordinal();
			return (value * Speed.multipliers[idx] + Speed.offsets[idx] - Speed.ro) / Speed.rm;
		} else if (measurementType.equals("gs1:Temperature")) {
			Temperature val = new Temperature(uom, value);
			int idx = val.getType().ordinal();
			return (value * Temperature.multipliers[idx] + Temperature.offsets[idx] - Temperature.ro) / Temperature.rm;
		} else if (measurementType.equals("gs1:Time")) {
			Time val = new Time(uom, value);
			int idx = val.getType().ordinal();
			return (value * Time.multipliers[idx] + Time.offsets[idx] - Time.ro) / Time.rm;
		} else if (measurementType.equals("gs1:Torque")) {
			Torque val = new Torque(uom, value);
			int idx = val.getType().ordinal();
			return (value * Torque.multipliers[idx] + Torque.offsets[idx] - Torque.ro) / Torque.rm;
		} else if (measurementType.equals("gs1:Voltage")) {
			Voltage val = new Voltage(uom, value);
			int idx = val.getType().ordinal();
			return (value * Voltage.multipliers[idx] + Voltage.offsets[idx] - Voltage.ro) / Voltage.rm;
		} else if (measurementType.equals("gs1:Volume")) {
			Volume val = new Volume(uom, value);
			int idx = val.getType().ordinal();
			return (value * Volume.multipliers[idx] + Volume.offsets[idx] - Volume.ro) / Volume.rm;
		} else if (measurementType.equals("gs1:VolumeFraction")) {
			VolumeFraction val = new VolumeFraction(uom, value);
			int idx = val.getType().ordinal();
			return (value * VolumeFraction.multipliers[idx] + VolumeFraction.offsets[idx] - VolumeFraction.ro)
					/ VolumeFraction.rm;
		} else {
			// Not happened
			return null;
		}
	}

	/**
	 * converts the value of E input according to a given output unit
	 * 
	 * @param input,      which has a value to be converted, E should be one of
	 *                    classes in org.oliot.unit_converter.unit
	 * @param outputUnit, the given output unit, should be one of enum Type of E
	 * @return true if the conversion is successful
	 */
	public boolean convert(Object input, Object outputUnit) {

		if (input instanceof AbsorbedDose) {
			AbsorbedDose inputObj = (AbsorbedDose) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = AbsorbedDose.multipliers[inputIdx];
			Double inputOffsets = AbsorbedDose.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof AbsorbedDose.Type) {
				AbsorbedDose.Type type = (AbsorbedDose.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = AbsorbedDose.multipliers[outputIdx];
				outputOffset = AbsorbedDose.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(AbsorbedDose.Type.values()[outputIdx]);
		} else if (input instanceof AbsorbedDoseRate) {
			AbsorbedDoseRate inputObj = (AbsorbedDoseRate) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = AbsorbedDoseRate.multipliers[inputIdx];
			Double inputOffsets = AbsorbedDoseRate.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof AbsorbedDoseRate.Type) {
				AbsorbedDoseRate.Type type = (AbsorbedDoseRate.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = AbsorbedDoseRate.multipliers[outputIdx];
				outputOffset = AbsorbedDoseRate.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(AbsorbedDoseRate.Type.values()[outputIdx]);
		} else if (input instanceof Acceleration) {
			Acceleration inputObj = (Acceleration) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Acceleration.multipliers[inputIdx];
			Double inputOffsets = Acceleration.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Acceleration.Type) {
				Acceleration.Type type = (Acceleration.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Acceleration.multipliers[outputIdx];
				outputOffset = Acceleration.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Acceleration.Type.values()[outputIdx]);
		} else if (input instanceof AmountOfSubstance) {
			AmountOfSubstance inputObj = (AmountOfSubstance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = AmountOfSubstance.multipliers[inputIdx];
			Double inputOffsets = AmountOfSubstance.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof AmountOfSubstance.Type) {
				AmountOfSubstance.Type type = (AmountOfSubstance.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = AmountOfSubstance.multipliers[outputIdx];
				outputOffset = AmountOfSubstance.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(AmountOfSubstance.Type.values()[outputIdx]);
		} else if (input instanceof Angle) {
			Angle inputObj = (Angle) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Angle.multipliers[inputIdx];
			Double inputOffsets = Angle.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Angle.Type) {
				Angle.Type type = (Angle.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Angle.multipliers[outputIdx];
				outputOffset = Angle.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Angle.Type.values()[outputIdx]);
		} else if (input instanceof Area) {
			Area inputObj = (Area) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Area.multipliers[inputIdx];
			Double inputOffsets = Area.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Area.Type) {
				Area.Type type = (Area.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Area.multipliers[outputIdx];
				outputOffset = Area.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Area.Type.values()[outputIdx]);
		} else if (input instanceof Capacitance) {
			Capacitance inputObj = (Capacitance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Capacitance.multipliers[inputIdx];
			Double inputOffsets = Capacitance.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Capacitance.Type) {
				Capacitance.Type type = (Capacitance.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Capacitance.multipliers[outputIdx];
				outputOffset = Capacitance.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Capacitance.Type.values()[outputIdx]);
		} else if (input instanceof ElectricCharge) {
			ElectricCharge inputObj = (ElectricCharge) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = ElectricCharge.multipliers[inputIdx];
			Double inputOffsets = ElectricCharge.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof ElectricCharge.Type) {
				ElectricCharge.Type type = (ElectricCharge.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = ElectricCharge.multipliers[outputIdx];
				outputOffset = ElectricCharge.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(ElectricCharge.Type.values()[outputIdx]);
		} else if (input instanceof Conductance) {
			Conductance inputObj = (Conductance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Conductance.multipliers[inputIdx];
			Double inputOffsets = Conductance.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Conductance.Type) {
				Conductance.Type type = (Conductance.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Conductance.multipliers[outputIdx];
				outputOffset = Conductance.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Conductance.Type.values()[outputIdx]);
		} else if (input instanceof ElectricCurrent) {
			ElectricCurrent inputObj = (ElectricCurrent) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = ElectricCurrent.multipliers[inputIdx];
			Double inputOffsets = ElectricCurrent.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof ElectricCurrent.Type) {
				ElectricCurrent.Type type = (ElectricCurrent.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = ElectricCurrent.multipliers[outputIdx];
				outputOffset = ElectricCurrent.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(ElectricCurrent.Type.values()[outputIdx]);
		} else if (input instanceof AbsoluteHumidity) {
			AbsoluteHumidity inputObj = (AbsoluteHumidity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = AbsoluteHumidity.multipliers[inputIdx];
			Double inputOffsets = AbsoluteHumidity.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof AbsoluteHumidity.Type) {
				AbsoluteHumidity.Type type = (AbsoluteHumidity.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = AbsoluteHumidity.multipliers[outputIdx];
				outputOffset = AbsoluteHumidity.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(AbsoluteHumidity.Type.values()[outputIdx]);
		} else if (input instanceof DimensionlessConcentration) {
			DimensionlessConcentration inputObj = (DimensionlessConcentration) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = DimensionlessConcentration.multipliers[inputIdx];
			Double inputOffsets = DimensionlessConcentration.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof DimensionlessConcentration.Type) {
				DimensionlessConcentration.Type type = (DimensionlessConcentration.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = DimensionlessConcentration.multipliers[outputIdx];
				outputOffset = DimensionlessConcentration.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(DimensionlessConcentration.Type.values()[outputIdx]);
		} else if (input instanceof DoseEquivalent) {
			DoseEquivalent inputObj = (DoseEquivalent) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = DoseEquivalent.multipliers[inputIdx];
			Double inputOffsets = DoseEquivalent.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof DoseEquivalent.Type) {
				DoseEquivalent.Type type = (DoseEquivalent.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = DoseEquivalent.multipliers[outputIdx];
				outputOffset = DoseEquivalent.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(DoseEquivalent.Type.values()[outputIdx]);
		} else if (input instanceof DoseEquivalentRate) {
			DoseEquivalentRate inputObj = (DoseEquivalentRate) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = DoseEquivalentRate.multipliers[inputIdx];
			Double inputOffsets = DoseEquivalentRate.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof DoseEquivalentRate.Type) {
				DoseEquivalentRate.Type type = (DoseEquivalentRate.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = DoseEquivalentRate.multipliers[outputIdx];
				outputOffset = DoseEquivalentRate.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(DoseEquivalentRate.Type.values()[outputIdx]);
		} else if (input instanceof Energy) {
			Energy inputObj = (Energy) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Energy.multipliers[inputIdx];
			Double inputOffsets = Energy.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Energy.Type) {
				Energy.Type type = (Energy.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Energy.multipliers[outputIdx];
				outputOffset = Energy.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Energy.Type.values()[outputIdx]);
		} else if (input instanceof Force) {
			Force inputObj = (Force) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Force.multipliers[inputIdx];
			Double inputOffsets = Force.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Force.Type) {
				Force.Type type = (Force.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Force.multipliers[outputIdx];
				outputOffset = Force.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Force.Type.values()[outputIdx]);
		} else if (input instanceof Frequency) {
			Frequency inputObj = (Frequency) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Frequency.multipliers[inputIdx];
			Double inputOffsets = Frequency.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Frequency.Type) {
				Frequency.Type type = (Frequency.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Frequency.multipliers[outputIdx];
				outputOffset = Frequency.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Frequency.Type.values()[outputIdx]);
		} else if (input instanceof Illuminance) {
			Illuminance inputObj = (Illuminance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Illuminance.multipliers[inputIdx];
			Double inputOffsets = Illuminance.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Illuminance.Type) {
				Illuminance.Type type = (Illuminance.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Illuminance.multipliers[outputIdx];
				outputOffset = Illuminance.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Illuminance.Type.values()[outputIdx]);
		} else if (input instanceof Inductance) {
			Inductance inputObj = (Inductance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Inductance.multipliers[inputIdx];
			Double inputOffsets = Inductance.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Inductance.Type) {
				Inductance.Type type = (Inductance.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Inductance.multipliers[outputIdx];
				outputOffset = Inductance.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Inductance.Type.values()[outputIdx]);
		} else if (input instanceof Length) {
			Length inputObj = (Length) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Length.multipliers[inputIdx];
			Double inputOffsets = Length.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Length.Type) {
				Length.Type type = (Length.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Length.multipliers[outputIdx];
				outputOffset = Length.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Length.Type.values()[outputIdx]);
		} else if (input instanceof LuminousIntensity) {
			LuminousIntensity inputObj = (LuminousIntensity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = LuminousIntensity.multipliers[inputIdx];
			Double inputOffsets = LuminousIntensity.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof LuminousIntensity.Type) {
				LuminousIntensity.Type type = (LuminousIntensity.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = LuminousIntensity.multipliers[outputIdx];
				outputOffset = LuminousIntensity.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(LuminousIntensity.Type.values()[outputIdx]);
		} else if (input instanceof MagneticFlux) {
			MagneticFlux inputObj = (MagneticFlux) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MagneticFlux.multipliers[inputIdx];
			Double inputOffsets = MagneticFlux.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof MagneticFlux.Type) {
				MagneticFlux.Type type = (MagneticFlux.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = MagneticFlux.multipliers[outputIdx];
				outputOffset = MagneticFlux.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(MagneticFlux.Type.values()[outputIdx]);
		} else if (input instanceof MagneticFluxDensity) {
			MagneticFluxDensity inputObj = (MagneticFluxDensity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MagneticFluxDensity.multipliers[inputIdx];
			Double inputOffsets = MagneticFluxDensity.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof MagneticFluxDensity.Type) {
				MagneticFluxDensity.Type type = (MagneticFluxDensity.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = MagneticFluxDensity.multipliers[outputIdx];
				outputOffset = MagneticFluxDensity.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(MagneticFluxDensity.Type.values()[outputIdx]);
		} else if (input instanceof MagneticVectorPotential) {
			MagneticVectorPotential inputObj = (MagneticVectorPotential) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MagneticVectorPotential.multipliers[inputIdx];
			Double inputOffsets = MagneticVectorPotential.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof MagneticVectorPotential.Type) {
				MagneticVectorPotential.Type type = (MagneticVectorPotential.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = MagneticVectorPotential.multipliers[outputIdx];
				outputOffset = MagneticVectorPotential.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(MagneticVectorPotential.Type.values()[outputIdx]);
		} else if (input instanceof Mass) {
			Mass inputObj = (Mass) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Mass.multipliers[inputIdx];
			Double inputOffsets = Mass.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Mass.Type) {
				Mass.Type type = (Mass.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Mass.multipliers[outputIdx];
				outputOffset = Mass.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Mass.Type.values()[outputIdx]);
		} else if (input instanceof MolarConcentration) {
			MolarConcentration inputObj = (MolarConcentration) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarConcentration.multipliers[inputIdx];
			Double inputOffsets = MolarConcentration.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof MolarConcentration.Type) {
				MolarConcentration.Type type = (MolarConcentration.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = MolarConcentration.multipliers[outputIdx];
				outputOffset = MolarConcentration.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(MolarConcentration.Type.values()[outputIdx]);
		} else if (input instanceof MolarMass) {
			MolarMass inputObj = (MolarMass) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarMass.multipliers[inputIdx];
			Double inputOffsets = MolarMass.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof MolarMass.Type) {
				MolarMass.Type type = (MolarMass.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = MolarMass.multipliers[outputIdx];
				outputOffset = MolarMass.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(MolarMass.Type.values()[outputIdx]);
		} else if (input instanceof MolarEnergy) {
			MolarEnergy inputObj = (MolarEnergy) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarEnergy.multipliers[inputIdx];
			Double inputOffsets = MolarEnergy.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof MolarEnergy.Type) {
				MolarEnergy.Type type = (MolarEnergy.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = MolarEnergy.multipliers[outputIdx];
				outputOffset = MolarEnergy.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(MolarEnergy.Type.values()[outputIdx]);
		} else if (input instanceof MolarVolume) {
			MolarVolume inputObj = (MolarVolume) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarVolume.multipliers[inputIdx];
			Double inputOffsets = MolarVolume.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof MolarVolume.Type) {
				MolarVolume.Type type = (MolarVolume.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = MolarVolume.multipliers[outputIdx];
				outputOffset = MolarVolume.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(MolarVolume.Type.values()[outputIdx]);
		} else if (input instanceof Power) {
			Power inputObj = (Power) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Power.multipliers[inputIdx];
			Double inputOffsets = Power.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Power.Type) {
				Power.Type type = (Power.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Power.multipliers[outputIdx];
				outputOffset = Power.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Power.Type.values()[outputIdx]);
		} else if (input instanceof Pressure) {
			Pressure inputObj = (Pressure) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Pressure.multipliers[inputIdx];
			Double inputOffsets = Pressure.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Pressure.Type) {
				Pressure.Type type = (Pressure.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Pressure.multipliers[outputIdx];
				outputOffset = Pressure.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Pressure.Type.values()[outputIdx]);
		} else if (input instanceof Radioactivity) {
			Radioactivity inputObj = (Radioactivity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Radioactivity.multipliers[inputIdx];
			Double inputOffsets = Radioactivity.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Radioactivity.Type) {
				Radioactivity.Type type = (Radioactivity.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Radioactivity.multipliers[outputIdx];
				outputOffset = Radioactivity.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Radioactivity.Type.values()[outputIdx]);
		} else if (input instanceof Resistance) {
			Resistance inputObj = (Resistance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Resistance.multipliers[inputIdx];
			Double inputOffsets = Resistance.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Resistance.Type) {
				Resistance.Type type = (Resistance.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Resistance.multipliers[outputIdx];
				outputOffset = Resistance.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Resistance.Type.values()[outputIdx]);
		} else if (input instanceof SpecificVolume) {
			SpecificVolume inputObj = (SpecificVolume) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = SpecificVolume.multipliers[inputIdx];
			Double inputOffsets = SpecificVolume.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof SpecificVolume.Type) {
				SpecificVolume.Type type = (SpecificVolume.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = SpecificVolume.multipliers[outputIdx];
				outputOffset = SpecificVolume.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(SpecificVolume.Type.values()[outputIdx]);
		} else if (input instanceof Speed) {
			Speed inputObj = (Speed) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Speed.multipliers[inputIdx];
			Double inputOffsets = Speed.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Speed.Type) {
				Speed.Type type = (Speed.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Speed.multipliers[outputIdx];
				outputOffset = Speed.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Speed.Type.values()[outputIdx]);
		} else if (input instanceof Temperature) {
			Temperature inputObj = (Temperature) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Temperature.multipliers[inputIdx];
			Double inputOffsets = Temperature.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Temperature.Type) {
				Temperature.Type type = (Temperature.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Temperature.multipliers[outputIdx];
				outputOffset = Temperature.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Temperature.Type.values()[outputIdx]);
		} else if (input instanceof Time) {
			Time inputObj = (Time) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Time.multipliers[inputIdx];
			Double inputOffsets = Time.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Time.Type) {
				Time.Type type = (Time.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Time.multipliers[outputIdx];
				outputOffset = Time.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Time.Type.values()[outputIdx]);
		} else if (input instanceof Torque) {
			Torque inputObj = (Torque) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Torque.multipliers[inputIdx];
			Double inputOffsets = Torque.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Torque.Type) {
				Torque.Type type = (Torque.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Torque.multipliers[outputIdx];
				outputOffset = Torque.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Torque.Type.values()[outputIdx]);
		} else if (input instanceof Voltage) {
			Voltage inputObj = (Voltage) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Voltage.multipliers[inputIdx];
			Double inputOffsets = Voltage.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Voltage.Type) {
				Voltage.Type type = (Voltage.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Voltage.multipliers[outputIdx];
				outputOffset = Voltage.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Voltage.Type.values()[outputIdx]);
		} else if (input instanceof Volume) {
			Volume inputObj = (Volume) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Volume.multipliers[inputIdx];
			Double inputOffsets = Volume.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Volume.Type) {
				Volume.Type type = (Volume.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Volume.multipliers[outputIdx];
				outputOffset = Volume.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Volume.Type.values()[outputIdx]);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * return all the possible conversion of input E
	 * 
	 * @param input,E should be one of classes in org.oliot.unit_converter.unit
	 * @return json-representation of the result
	 */
	public String convert(Object input) {

		JsonArray result = new JsonArray();
		if (input instanceof AbsorbedDose) {
			AbsorbedDose inputObj = (AbsorbedDose) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = AbsorbedDose.multipliers[inputIdx];
			Double inputOffsets = AbsorbedDose.offsets[inputIdx];
			AbsorbedDose.Type[] types = AbsorbedDose.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				AbsorbedDose.Type type = types[i];
				outputMultiplier = AbsorbedDose.multipliers[i];
				outputOffset = AbsorbedDose.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof AbsorbedDoseRate) {
			AbsorbedDoseRate inputObj = (AbsorbedDoseRate) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = AbsorbedDoseRate.multipliers[inputIdx];
			Double inputOffsets = AbsorbedDoseRate.offsets[inputIdx];
			AbsorbedDoseRate.Type[] types = AbsorbedDoseRate.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				AbsorbedDoseRate.Type type = types[i];
				outputMultiplier = AbsorbedDoseRate.multipliers[i];
				outputOffset = AbsorbedDoseRate.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Acceleration) {
			Acceleration inputObj = (Acceleration) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Acceleration.multipliers[inputIdx];
			Double inputOffsets = Acceleration.offsets[inputIdx];
			Acceleration.Type[] types = Acceleration.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Acceleration.Type type = types[i];
				outputMultiplier = Acceleration.multipliers[i];
				outputOffset = Acceleration.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof AmountOfSubstance) {
			AmountOfSubstance inputObj = (AmountOfSubstance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = AmountOfSubstance.multipliers[inputIdx];
			Double inputOffsets = AmountOfSubstance.offsets[inputIdx];
			AmountOfSubstance.Type[] types = AmountOfSubstance.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				AmountOfSubstance.Type type = types[i];
				outputMultiplier = AbsorbedDose.multipliers[i];
				outputOffset = AbsorbedDose.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Angle) {
			Angle inputObj = (Angle) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Angle.multipliers[inputIdx];
			Double inputOffsets = Angle.offsets[inputIdx];
			Angle.Type[] types = Angle.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Angle.Type type = types[i];
				outputMultiplier = Angle.multipliers[i];
				outputOffset = Angle.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Area) {
			Area inputObj = (Area) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Area.multipliers[inputIdx];
			Double inputOffsets = Area.offsets[inputIdx];
			Area.Type[] types = Area.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Area.Type type = types[i];
				outputMultiplier = Area.multipliers[i];
				outputOffset = Area.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Capacitance) {
			Capacitance inputObj = (Capacitance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Capacitance.multipliers[inputIdx];
			Double inputOffsets = Capacitance.offsets[inputIdx];
			Capacitance.Type[] types = Capacitance.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Capacitance.Type type = types[i];
				outputMultiplier = Capacitance.multipliers[i];
				outputOffset = Capacitance.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof ElectricCharge) {
			ElectricCharge inputObj = (ElectricCharge) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = ElectricCharge.multipliers[inputIdx];
			Double inputOffsets = ElectricCharge.offsets[inputIdx];
			ElectricCharge.Type[] types = ElectricCharge.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				ElectricCharge.Type type = types[i];
				outputMultiplier = ElectricCharge.multipliers[i];
				outputOffset = ElectricCharge.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Conductance) {
			Conductance inputObj = (Conductance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Conductance.multipliers[inputIdx];
			Double inputOffsets = Conductance.offsets[inputIdx];
			Conductance.Type[] types = Conductance.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Conductance.Type type = types[i];
				outputMultiplier = Conductance.multipliers[i];
				outputOffset = Conductance.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof ElectricCurrent) {
			ElectricCurrent inputObj = (ElectricCurrent) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = ElectricCurrent.multipliers[inputIdx];
			Double inputOffsets = ElectricCurrent.offsets[inputIdx];
			ElectricCurrent.Type[] types = ElectricCurrent.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				ElectricCurrent.Type type = types[i];
				outputMultiplier = ElectricCurrent.multipliers[i];
				outputOffset = ElectricCurrent.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof AbsoluteHumidity) {
			AbsoluteHumidity inputObj = (AbsoluteHumidity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = AbsoluteHumidity.multipliers[inputIdx];
			Double inputOffsets = AbsoluteHumidity.offsets[inputIdx];
			AbsoluteHumidity.Type[] types = AbsoluteHumidity.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				AbsoluteHumidity.Type type = types[i];
				outputMultiplier = AbsoluteHumidity.multipliers[i];
				outputOffset = AbsoluteHumidity.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof DimensionlessConcentration) {
			DimensionlessConcentration inputObj = (DimensionlessConcentration) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = DimensionlessConcentration.multipliers[inputIdx];
			Double inputOffsets = DimensionlessConcentration.offsets[inputIdx];
			DimensionlessConcentration.Type[] types = DimensionlessConcentration.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				DimensionlessConcentration.Type type = types[i];
				outputMultiplier = DimensionlessConcentration.multipliers[i];
				outputOffset = DimensionlessConcentration.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof DoseEquivalent) {
			DoseEquivalent inputObj = (DoseEquivalent) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = DoseEquivalent.multipliers[inputIdx];
			Double inputOffsets = DoseEquivalent.offsets[inputIdx];
			DoseEquivalent.Type[] types = DoseEquivalent.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				DoseEquivalent.Type type = types[i];
				outputMultiplier = DoseEquivalent.multipliers[i];
				outputOffset = DoseEquivalent.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof DoseEquivalentRate) {
			DoseEquivalentRate inputObj = (DoseEquivalentRate) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = DoseEquivalentRate.multipliers[inputIdx];
			Double inputOffsets = DoseEquivalentRate.offsets[inputIdx];
			DoseEquivalentRate.Type[] types = DoseEquivalentRate.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				DoseEquivalentRate.Type type = types[i];
				outputMultiplier = DoseEquivalentRate.multipliers[i];
				outputOffset = DoseEquivalentRate.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Energy) {
			Energy inputObj = (Energy) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Energy.multipliers[inputIdx];
			Double inputOffsets = Energy.offsets[inputIdx];
			Energy.Type[] types = Energy.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Energy.Type type = types[i];
				outputMultiplier = Energy.multipliers[i];
				outputOffset = Energy.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Force) {
			Force inputObj = (Force) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Force.multipliers[inputIdx];
			Double inputOffsets = Force.offsets[inputIdx];
			Force.Type[] types = Force.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Force.Type type = types[i];
				outputMultiplier = Force.multipliers[i];
				outputOffset = Force.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Frequency) {
			Frequency inputObj = (Frequency) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Frequency.multipliers[inputIdx];
			Double inputOffsets = Frequency.offsets[inputIdx];
			Frequency.Type[] types = Frequency.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Frequency.Type type = types[i];
				outputMultiplier = Frequency.multipliers[i];
				outputOffset = Frequency.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Illuminance) {
			Illuminance inputObj = (Illuminance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Illuminance.multipliers[inputIdx];
			Double inputOffsets = Illuminance.offsets[inputIdx];
			Illuminance.Type[] types = Illuminance.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Illuminance.Type type = types[i];
				outputMultiplier = Illuminance.multipliers[i];
				outputOffset = Illuminance.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Inductance) {
			Inductance inputObj = (Inductance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Inductance.multipliers[inputIdx];
			Double inputOffsets = Inductance.offsets[inputIdx];
			Inductance.Type[] types = Inductance.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Inductance.Type type = types[i];
				outputMultiplier = Inductance.multipliers[i];
				outputOffset = Inductance.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Length) {
			Length inputObj = (Length) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Length.multipliers[inputIdx];
			Double inputOffsets = Length.offsets[inputIdx];
			Length.Type[] types = Length.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Length.Type type = types[i];
				outputMultiplier = Length.multipliers[i];
				outputOffset = Length.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof LuminousIntensity) {
			LuminousIntensity inputObj = (LuminousIntensity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = LuminousIntensity.multipliers[inputIdx];
			Double inputOffsets = LuminousIntensity.offsets[inputIdx];
			LuminousIntensity.Type[] types = LuminousIntensity.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				LuminousIntensity.Type type = types[i];
				outputMultiplier = LuminousIntensity.multipliers[i];
				outputOffset = LuminousIntensity.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof MagneticFlux) {
			MagneticFlux inputObj = (MagneticFlux) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MagneticFlux.multipliers[inputIdx];
			Double inputOffsets = MagneticFlux.offsets[inputIdx];
			MagneticFlux.Type[] types = MagneticFlux.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				MagneticFlux.Type type = types[i];
				outputMultiplier = MagneticFlux.multipliers[i];
				outputOffset = MagneticFlux.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof MagneticFluxDensity) {
			MagneticFluxDensity inputObj = (MagneticFluxDensity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MagneticFluxDensity.multipliers[inputIdx];
			Double inputOffsets = MagneticFluxDensity.offsets[inputIdx];
			MagneticFluxDensity.Type[] types = MagneticFluxDensity.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				MagneticFluxDensity.Type type = types[i];
				outputMultiplier = MagneticFluxDensity.multipliers[i];
				outputOffset = MagneticFluxDensity.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof MagneticVectorPotential) {
			MagneticVectorPotential inputObj = (MagneticVectorPotential) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MagneticVectorPotential.multipliers[inputIdx];
			Double inputOffsets = MagneticVectorPotential.offsets[inputIdx];
			MagneticVectorPotential.Type[] types = MagneticVectorPotential.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				MagneticVectorPotential.Type type = types[i];
				outputMultiplier = MagneticVectorPotential.multipliers[i];
				outputOffset = MagneticVectorPotential.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Mass) {
			Mass inputObj = (Mass) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Mass.multipliers[inputIdx];
			Double inputOffsets = Mass.offsets[inputIdx];
			Mass.Type[] types = Mass.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Mass.Type type = types[i];
				outputMultiplier = Mass.multipliers[i];
				outputOffset = Mass.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof MolarConcentration) {
			MolarConcentration inputObj = (MolarConcentration) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarConcentration.multipliers[inputIdx];
			Double inputOffsets = MolarConcentration.offsets[inputIdx];
			MolarConcentration.Type[] types = MolarConcentration.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				MolarConcentration.Type type = types[i];
				outputMultiplier = MolarConcentration.multipliers[i];
				outputOffset = MolarConcentration.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof MolarMass) {
			MolarMass inputObj = (MolarMass) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarMass.multipliers[inputIdx];
			Double inputOffsets = MolarMass.offsets[inputIdx];
			MolarMass.Type[] types = MolarMass.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				MolarMass.Type type = types[i];
				outputMultiplier = MolarMass.multipliers[i];
				outputOffset = MolarMass.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof MolarEnergy) {
			MolarEnergy inputObj = (MolarEnergy) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarEnergy.multipliers[inputIdx];
			Double inputOffsets = MolarEnergy.offsets[inputIdx];
			MolarEnergy.Type[] types = MolarEnergy.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				MolarEnergy.Type type = types[i];
				outputMultiplier = MolarEnergy.multipliers[i];
				outputOffset = MolarEnergy.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof MolarVolume) {
			MolarVolume inputObj = (MolarVolume) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarVolume.multipliers[inputIdx];
			Double inputOffsets = MolarVolume.offsets[inputIdx];
			MolarVolume.Type[] types = MolarVolume.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				MolarVolume.Type type = types[i];
				outputMultiplier = MolarVolume.multipliers[i];
				outputOffset = MolarVolume.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Power) {
			Power inputObj = (Power) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Power.multipliers[inputIdx];
			Double inputOffsets = Power.offsets[inputIdx];
			Power.Type[] types = Power.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Power.Type type = types[i];
				outputMultiplier = Power.multipliers[i];
				outputOffset = Power.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Pressure) {
			Pressure inputObj = (Pressure) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Pressure.multipliers[inputIdx];
			Double inputOffsets = Pressure.offsets[inputIdx];
			Pressure.Type[] types = Pressure.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Pressure.Type type = types[i];
				outputMultiplier = Pressure.multipliers[i];
				outputOffset = Pressure.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Radioactivity) {
			Radioactivity inputObj = (Radioactivity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Radioactivity.multipliers[inputIdx];
			Double inputOffsets = Radioactivity.offsets[inputIdx];
			Radioactivity.Type[] types = Radioactivity.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Radioactivity.Type type = types[i];
				outputMultiplier = Radioactivity.multipliers[i];
				outputOffset = Radioactivity.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Resistance) {
			Resistance inputObj = (Resistance) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Resistance.multipliers[inputIdx];
			Double inputOffsets = Resistance.offsets[inputIdx];
			Resistance.Type[] types = Resistance.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Resistance.Type type = types[i];
				outputMultiplier = Resistance.multipliers[i];
				outputOffset = Resistance.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof SpecificVolume) {
			SpecificVolume inputObj = (SpecificVolume) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = SpecificVolume.multipliers[inputIdx];
			Double inputOffsets = SpecificVolume.offsets[inputIdx];
			SpecificVolume.Type[] types = SpecificVolume.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				SpecificVolume.Type type = types[i];
				outputMultiplier = SpecificVolume.multipliers[i];
				outputOffset = SpecificVolume.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Speed) {
			Speed inputObj = (Speed) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Speed.multipliers[inputIdx];
			Double inputOffsets = Speed.offsets[inputIdx];
			Speed.Type[] types = Speed.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Speed.Type type = types[i];
				outputMultiplier = Speed.multipliers[i];
				outputOffset = Speed.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Temperature) {
			Temperature inputObj = (Temperature) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Temperature.multipliers[inputIdx];
			Double inputOffsets = Temperature.offsets[inputIdx];
			Temperature.Type[] types = Temperature.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Temperature.Type type = types[i];
				outputMultiplier = Temperature.multipliers[i];
				outputOffset = Temperature.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Time) {
			Time inputObj = (Time) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Time.multipliers[inputIdx];
			Double inputOffsets = Time.offsets[inputIdx];
			Time.Type[] types = Time.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Time.Type type = types[i];
				outputMultiplier = Time.multipliers[i];
				outputOffset = Time.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Torque) {
			Torque inputObj = (Torque) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Torque.multipliers[inputIdx];
			Double inputOffsets = Torque.offsets[inputIdx];
			Torque.Type[] types = Torque.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Torque.Type type = types[i];
				outputMultiplier = Torque.multipliers[i];
				outputOffset = Torque.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Voltage) {
			Voltage inputObj = (Voltage) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Voltage.multipliers[inputIdx];
			Double inputOffsets = Voltage.offsets[inputIdx];
			Voltage.Type[] types = Voltage.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Voltage.Type type = types[i];
				outputMultiplier = Voltage.multipliers[i];
				outputOffset = Voltage.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof Volume) {
			Volume inputObj = (Volume) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Volume.multipliers[inputIdx];
			Double inputOffsets = Volume.offsets[inputIdx];
			Volume.Type[] types = Volume.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Volume.Type type = types[i];
				outputMultiplier = Volume.multipliers[i];
				outputOffset = Volume.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else {
			return null;
		}
		return result.toString();
	}
}
