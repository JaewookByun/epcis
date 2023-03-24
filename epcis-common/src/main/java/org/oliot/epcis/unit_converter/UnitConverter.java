package org.oliot.epcis.unit_converter;

import java.util.HashMap;

import org.oliot.epcis.unit_converter.unit.AbsorbedDose;
import org.oliot.epcis.unit_converter.unit.AbsorbedDoseRate;
import org.oliot.epcis.unit_converter.unit.Acceleration;
import org.oliot.epcis.unit_converter.unit.AmountOfSubstance;
import org.oliot.epcis.unit_converter.unit.Angle;
import org.oliot.epcis.unit_converter.unit.Area;
import org.oliot.epcis.unit_converter.unit.Capacitance;
import org.oliot.epcis.unit_converter.unit.Charge;
import org.oliot.epcis.unit_converter.unit.Conductance;
import org.oliot.epcis.unit_converter.unit.Current;
import org.oliot.epcis.unit_converter.unit.DensityHumidity;
import org.oliot.epcis.unit_converter.unit.DimensionlessConcentration;
import org.oliot.epcis.unit_converter.unit.EffectiveDose;
import org.oliot.epcis.unit_converter.unit.EffectiveDoseRate;
import org.oliot.epcis.unit_converter.unit.Energy;
import org.oliot.epcis.unit_converter.unit.Force;
import org.oliot.epcis.unit_converter.unit.Frequency;
import org.oliot.epcis.unit_converter.unit.Illuminance;
import org.oliot.epcis.unit_converter.unit.Inductance;
import org.oliot.epcis.unit_converter.unit.Length;
import org.oliot.epcis.unit_converter.unit.LuminousIntensity;
import org.oliot.epcis.unit_converter.unit.MagneticFlux;
import org.oliot.epcis.unit_converter.unit.MagneticFluxDensity;
import org.oliot.epcis.unit_converter.unit.MagneticVectorPotential;
import org.oliot.epcis.unit_converter.unit.Mass;
import org.oliot.epcis.unit_converter.unit.MolarConcentration;
import org.oliot.epcis.unit_converter.unit.MolarMass;
import org.oliot.epcis.unit_converter.unit.MolarThermodynamicEnergy;
import org.oliot.epcis.unit_converter.unit.MolarVolume;
import org.oliot.epcis.unit_converter.unit.Power;
import org.oliot.epcis.unit_converter.unit.Pressure;
import org.oliot.epcis.unit_converter.unit.RadioActivity;
import org.oliot.epcis.unit_converter.unit.Resistance;
import org.oliot.epcis.unit_converter.unit.SpecificVolume;
import org.oliot.epcis.unit_converter.unit.Speed;
import org.oliot.epcis.unit_converter.unit.Temperature;
import org.oliot.epcis.unit_converter.unit.Time;
import org.oliot.epcis.unit_converter.unit.Torque;
import org.oliot.epcis.unit_converter.unit.Voltage;
import org.oliot.epcis.unit_converter.unit.Volume;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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
public class UnitConverter {

	public HashMap<String, String> unitToGroup;

	public UnitConverter() {
		unitToGroup = new HashMap<>();
		unitToGroup.put("A95", "AbsorbedDose");
		unitToGroup.put("C13", "AbsorbedDose");
		unitToGroup.put("C80", "AbsorbedDose");
		unitToGroup.put("A61", "AbsorbedDose");

		unitToGroup.put("P54", "AbsorbedDoseRate");
		unitToGroup.put("P55", "AbsorbedDoseRate");
		unitToGroup.put("P56", "AbsorbedDoseRate");
		unitToGroup.put("P57", "AbsorbedDoseRate");
		unitToGroup.put("P58", "AbsorbedDoseRate");
		unitToGroup.put("P59", "AbsorbedDoseRate");
		unitToGroup.put("P60", "AbsorbedDoseRate");
		unitToGroup.put("P61", "AbsorbedDoseRate");
		unitToGroup.put("P62", "AbsorbedDoseRate");
		unitToGroup.put("P63", "AbsorbedDoseRate");
		unitToGroup.put("P64", "AbsorbedDoseRate");

		unitToGroup.put("MSK", "Acceleration");
		unitToGroup.put("A76", "Acceleration");
		unitToGroup.put("M38", "Acceleration");
		unitToGroup.put("M39", "Acceleration");
		unitToGroup.put("M41", "Acceleration");
		unitToGroup.put("A73", "Acceleration");
		unitToGroup.put("IV", "Acceleration");
		unitToGroup.put("K40", "Acceleration");
		unitToGroup.put("M40", "Acceleration");
		unitToGroup.put("M42", "Acceleration");

		unitToGroup.put("C34", "AmountOfSubstance");
		unitToGroup.put("B45", "AmountOfSubstance");
		unitToGroup.put("C18", "AmountOfSubstance");
		unitToGroup.put("FH", "AmountOfSubstance");

		unitToGroup.put("C81", "Angle");
		unitToGroup.put("C25", "Angle");
		unitToGroup.put("B97", "Angle");
		unitToGroup.put("A91", "Angle");
		unitToGroup.put("DD", "Angle");
		unitToGroup.put("D61", "Angle");
		unitToGroup.put("D62", "Angle");
		unitToGroup.put("M43", "Angle");
		unitToGroup.put("M44", "Angle");

		unitToGroup.put("MTK", "Area");
		unitToGroup.put("KMK", "Area");
		unitToGroup.put("H30", "Area");
		unitToGroup.put("DAA", "Area");
		unitToGroup.put("CMK", "Area");
		unitToGroup.put("DMK", "Area");
		unitToGroup.put("H16", "Area");
		unitToGroup.put("H18", "Area");
		unitToGroup.put("MMK", "Area");
		unitToGroup.put("ARE", "Area");
		unitToGroup.put("HAR", "Area");
		unitToGroup.put("INK", "Area");
		unitToGroup.put("FTK", "Area");
		unitToGroup.put("YDK", "Area");
		unitToGroup.put("MIK", "Area");
		unitToGroup.put("M48", "Area");
		unitToGroup.put("ACR", "Area");
		unitToGroup.put("M47", "Area");

		unitToGroup.put("FAR", "Capacitance");
		unitToGroup.put("H48", "Capacitance");
		unitToGroup.put("C10", "Capacitance");
		unitToGroup.put("_40", "Capacitance");
		unitToGroup.put("C41", "Capacitance");
		unitToGroup.put("_4T", "Capacitance");
		unitToGroup.put("N90", "Capacitance");

		unitToGroup.put("COU", "Charge");
		unitToGroup.put("A8", "Charge");
		unitToGroup.put("AMH", "Charge");
		unitToGroup.put("TAH", "Charge");
		unitToGroup.put("D77", "Charge");
		unitToGroup.put("D86", "Charge");
		unitToGroup.put("B26", "Charge");
		unitToGroup.put("B86", "Charge");
		unitToGroup.put("C40", "Charge");
		unitToGroup.put("C71", "Charge");
		unitToGroup.put("E09", "Charge");
		unitToGroup.put("N95", "Charge");
		unitToGroup.put("N94", "Charge");

		unitToGroup.put("SIE", "Conductance");
		unitToGroup.put("B53", "Conductance");
		unitToGroup.put("C27", "Conductance");
		unitToGroup.put("B99", "Conductance");
		unitToGroup.put("N92", "Conductance");
		unitToGroup.put("NQ", "Conductance");
		unitToGroup.put("NR", "Conductance");

		unitToGroup.put("AMP", "Current");
		unitToGroup.put("B22", "Current");
		unitToGroup.put("H38", "Current");
		unitToGroup.put("_4K", "Current");
		unitToGroup.put("B84", "Current");
		unitToGroup.put("C39", "Current");
		unitToGroup.put("C70", "Current");
		unitToGroup.put("N96", "Current");
		unitToGroup.put("N97", "Current");

		unitToGroup.put("KMQ", "DensityHumidity");
		unitToGroup.put("_23", "DensityHumidity");
		unitToGroup.put("D41", "DensityHumidity");
		unitToGroup.put("GJ", "DensityHumidity");
		unitToGroup.put("B35", "DensityHumidity");
		unitToGroup.put("GL", "DensityHumidity");
		unitToGroup.put("A93", "DensityHumidity");
		unitToGroup.put("GP", "DensityHumidity");
		unitToGroup.put("B72", "DensityHumidity");
		unitToGroup.put("B34", "DensityHumidity");
		unitToGroup.put("H29", "DensityHumidity");
		unitToGroup.put("M1", "DensityHumidity");
		unitToGroup.put("GQ", "DensityHumidity");
		unitToGroup.put("F23", "DensityHumidity");
		unitToGroup.put("G31", "DensityHumidity");
		unitToGroup.put("_87", "DensityHumidity");
		unitToGroup.put("GE", "DensityHumidity");
		unitToGroup.put("LA", "DensityHumidity");
		unitToGroup.put("G32", "DensityHumidity");
		unitToGroup.put("K41", "DensityHumidity");
		unitToGroup.put("K71", "DensityHumidity");
		unitToGroup.put("K84", "DensityHumidity");
		unitToGroup.put("L37", "DensityHumidity");
		unitToGroup.put("L38", "DensityHumidity");
		unitToGroup.put("L39", "DensityHumidity");
		unitToGroup.put("L65", "DensityHumidity");
		unitToGroup.put("L92", "DensityHumidity");
		unitToGroup.put("L93", "DensityHumidity");

		unitToGroup.put("P1", "DimensionlessConcentration");
		unitToGroup.put("_59", "DimensionlessConcentration");
		unitToGroup.put("_61", "DimensionlessConcentration");
		unitToGroup.put("_60", "DimensionlessConcentration");
		unitToGroup.put("E40", "DimensionlessConcentration");
		unitToGroup.put("NX", "DimensionlessConcentration");
		unitToGroup.put("GK", "DimensionlessConcentration");
		unitToGroup.put("NA", "DimensionlessConcentration");
		unitToGroup.put("J33", "DimensionlessConcentration");
		unitToGroup.put("L32", "DimensionlessConcentration");
		unitToGroup.put("M29", "DimensionlessConcentration");
		unitToGroup.put("K62", "DimensionlessConcentration");
		unitToGroup.put("L19", "DimensionlessConcentration");
		unitToGroup.put("J36", "DimensionlessConcentration");
		unitToGroup.put("H60", "DimensionlessConcentration");
		unitToGroup.put("H65", "DimensionlessConcentration");
		unitToGroup.put("J87", "DimensionlessConcentration");
		unitToGroup.put("L21", "DimensionlessConcentration");
		unitToGroup.put("J91", "DimensionlessConcentration");

		unitToGroup.put("D13", "EffectiveDose");
		unitToGroup.put("C28", "EffectiveDose");
		unitToGroup.put("D91", "EffectiveDose");
		unitToGroup.put("L31", "EffectiveDose");

		unitToGroup.put("P65", "EffectiveDoseRate");
		unitToGroup.put("P66", "EffectiveDoseRate");
		unitToGroup.put("P67", "EffectiveDoseRate");
		unitToGroup.put("P68", "EffectiveDoseRate");
		unitToGroup.put("P69", "EffectiveDoseRate");
		unitToGroup.put("P70", "EffectiveDoseRate");
		unitToGroup.put("P71", "EffectiveDoseRate");
		unitToGroup.put("P72", "EffectiveDoseRate");
		unitToGroup.put("P73", "EffectiveDoseRate");
		unitToGroup.put("P74", "EffectiveDoseRate");
		unitToGroup.put("P75", "EffectiveDoseRate");
		unitToGroup.put("P76", "EffectiveDoseRate");
		unitToGroup.put("P77", "EffectiveDoseRate");

		unitToGroup.put("JOU", "Energy");
		unitToGroup.put("KJO", "Energy");
		unitToGroup.put("A68", "Energy");
		unitToGroup.put("C68", "Energy");
		unitToGroup.put("D30", "Energy");
		unitToGroup.put("GV", "Energy");
		unitToGroup.put("_3B", "Energy");
		unitToGroup.put("C15", "Energy");
		unitToGroup.put("A70", "Energy");
		unitToGroup.put("A13", "Energy");
		unitToGroup.put("WHR", "Energy");
		unitToGroup.put("MWH", "Energy");
		unitToGroup.put("KMH", "Energy");
		unitToGroup.put("GWH", "Energy");
		unitToGroup.put("D32", "Energy");
		unitToGroup.put("A53", "Energy");
		unitToGroup.put("B71", "Energy");
		unitToGroup.put("A85", "Energy");
		unitToGroup.put("B29", "Energy");
		unitToGroup.put("A57", "Energy");
		unitToGroup.put("_85", "Energy");
		unitToGroup.put("B38", "Energy");
		unitToGroup.put("N46", "Energy");
		unitToGroup.put("N47", "Energy");
		unitToGroup.put("D60", "Energy");
		unitToGroup.put("J75", "Energy");
		unitToGroup.put("E14", "Energy");
		unitToGroup.put("K51", "Energy");
		unitToGroup.put("K53", "Energy");
		unitToGroup.put("BTU", "Energy");
		unitToGroup.put("J37", "Energy");
		unitToGroup.put("N71", "Energy");
		unitToGroup.put("N72", "Energy");
		unitToGroup.put("J55", "Energy");

		unitToGroup.put("NEW", "Force");
		unitToGroup.put("B47", "Force");
		unitToGroup.put("B73", "Force");
		unitToGroup.put("B92", "Force");
		unitToGroup.put("C20", "Force");
		unitToGroup.put("DU", "Force");
		unitToGroup.put("C78", "Force");
		unitToGroup.put("B37", "Force");
		unitToGroup.put("B51", "Force");
		unitToGroup.put("L40", "Force");
		unitToGroup.put("L94", "Force");
		unitToGroup.put("M75", "Force");
		unitToGroup.put("M76", "Force");
		unitToGroup.put("M77", "Force");
		unitToGroup.put("M78", "Force");

		unitToGroup.put("HTZ", "Frequency");
		unitToGroup.put("KHZ", "Frequency");
		unitToGroup.put("MHZ", "Frequency");
		unitToGroup.put("A86", "Frequency");
		unitToGroup.put("D29", "Frequency");
		
		unitToGroup.put("B60", "Illuminance");
		unitToGroup.put("LUX", "Illuminance");
		unitToGroup.put("KLX", "Illuminance");
		unitToGroup.put("P25", "Illuminance");
		unitToGroup.put("P26", "Illuminance");
		unitToGroup.put("P27", "Illuminance");
		
		unitToGroup.put("_81", "Inductance");
		unitToGroup.put("C14", "Inductance");
		unitToGroup.put("B90", "Inductance");
		unitToGroup.put("C43", "Inductance");
		unitToGroup.put("C73", "Inductance");
		unitToGroup.put("P24", "Inductance");
		
		unitToGroup.put("MTR", "Length");
		unitToGroup.put("A11", "Length");
		unitToGroup.put("A71", "Length");
		unitToGroup.put("C45", "Length");
		unitToGroup.put("_4H", "Length");
		unitToGroup.put("A12", "Length");
		unitToGroup.put("DMT", "Length");
		unitToGroup.put("CMT", "Length");
		unitToGroup.put("MMT", "Length");
		unitToGroup.put("INH", "Length");
		unitToGroup.put("FOT", "Length");
		unitToGroup.put("YRD", "Length");
		unitToGroup.put("NMI", "Length");
		unitToGroup.put("A45", "Length");
		unitToGroup.put("HMT", "Length");
		unitToGroup.put("KMT", "Length");
		unitToGroup.put("B57", "Length");
		unitToGroup.put("AK", "Length");
		unitToGroup.put("M50", "Length");
		unitToGroup.put("M49", "Length");
		unitToGroup.put("X1", "Length");
		unitToGroup.put("M51", "Length");
		
		unitToGroup.put("CDL", "LuminousIntensity");
		unitToGroup.put("P33", "LuminousIntensity");
		unitToGroup.put("P34", "LuminousIntensity");
		
		unitToGroup.put("WEB", "MagneticFlux");
		unitToGroup.put("C33", "MagneticFlux");
		unitToGroup.put("P11", "MagneticFlux");
		
		unitToGroup.put("D33", "MagneticFluxDensity");
		unitToGroup.put("C29", "MagneticFluxDensity");
		unitToGroup.put("D81", "MagneticFluxDensity");
		unitToGroup.put("C48", "MagneticFluxDensity");
		unitToGroup.put("P13", "MagneticFluxDensity");
		unitToGroup.put("P12", "MagneticFluxDensity");
		
		unitToGroup.put("D59", "MagneticVectorPotential");
		unitToGroup.put("B56", "MagneticVectorPotential");
		unitToGroup.put("D60", "MagneticVectorPotential");
		
		unitToGroup.put("KGM", "Mass");
		unitToGroup.put("KTN", "Mass");
		unitToGroup.put("LTN", "Mass");
		unitToGroup.put("_2U", "Mass");
		unitToGroup.put("TNE", "Mass");
		unitToGroup.put("STN", "Mass");
		unitToGroup.put("DTN", "Mass");
		unitToGroup.put("STI", "Mass");
		unitToGroup.put("LBR", "Mass");
		unitToGroup.put("HGM", "Mass");
		unitToGroup.put("ONZ", "Mass");
		unitToGroup.put("DJ", "Mass");
		unitToGroup.put("APZ", "Mass");
		unitToGroup.put("GRM", "Mass");
		unitToGroup.put("DG", "Mass");
		unitToGroup.put("CGM", "Mass");
		unitToGroup.put("MGM", "Mass");
		unitToGroup.put("MC", "Mass");
		unitToGroup.put("F13", "Mass");
		unitToGroup.put("CWI", "Mass");
		unitToGroup.put("CWA", "Mass");
		unitToGroup.put("M86", "Mass");
		
		unitToGroup.put("C36", "MolarConcentration");
		unitToGroup.put("M33", "MolarConcentration");
		unitToGroup.put("C38", "MolarConcentration");
		unitToGroup.put("C35", "MolarConcentration");
		unitToGroup.put("B46", "MolarConcentration");
		
		unitToGroup.put("D74", "MolarMass");
		unitToGroup.put("A94", "MolarMass");
		
		unitToGroup.put("B15", "MolarThermodynamicEnergy");
		unitToGroup.put("B44", "MolarThermodynamicEnergy");
		
		unitToGroup.put("A40", "MolarVolume");
		unitToGroup.put("A37", "MolarVolume");
		unitToGroup.put("A36", "MolarVolume");
		unitToGroup.put("B58", "MolarVolume");
		
		unitToGroup.put("WTT", "Power");
		unitToGroup.put("KWT", "Power");
		unitToGroup.put("MAW", "Power");
		unitToGroup.put("A90", "Power");
		unitToGroup.put("C31", "Power");
		unitToGroup.put("D80", "Power");
		unitToGroup.put("F80", "Power");
		unitToGroup.put("A63", "Power");
		unitToGroup.put("A74", "Power");
		unitToGroup.put("B39", "Power");
		unitToGroup.put("HJ", "Power");
		unitToGroup.put("A25", "Power");
		unitToGroup.put("BHP", "Power");
		unitToGroup.put("K15", "Power");
		unitToGroup.put("K16", "Power");
		unitToGroup.put("K42", "Power");
		unitToGroup.put("N12", "Power");
		
		unitToGroup.put("PAL", "Pressure");
		unitToGroup.put("H75", "Pressure");
		unitToGroup.put("_74", "Pressure");
		unitToGroup.put("A89", "Pressure");
		unitToGroup.put("A97", "Pressure");
		unitToGroup.put("B96", "Pressure");
		unitToGroup.put("KPA", "Pressure");
		unitToGroup.put("MPA", "Pressure");
		unitToGroup.put("BAR", "Pressure");
		unitToGroup.put("MBR", "Pressure");
		unitToGroup.put("C55", "Pressure");
		unitToGroup.put("C56", "Pressure");
		unitToGroup.put("B40", "Pressure");
		unitToGroup.put("UA", "Pressure");
		unitToGroup.put("_80", "Pressure");
		unitToGroup.put("H78", "Pressure");
		unitToGroup.put("HP", "Pressure");
		unitToGroup.put("F79", "Pressure");
		unitToGroup.put("F78", "Pressure");
		unitToGroup.put("ATT", "Pressure");
		unitToGroup.put("ATM", "Pressure");
		unitToGroup.put("J89", "Pressure");
		unitToGroup.put("K24", "Pressure");
		unitToGroup.put("K25", "Pressure");
		unitToGroup.put("K31", "Pressure");
		unitToGroup.put("E42", "Pressure");
		unitToGroup.put("E41", "Pressure");
		unitToGroup.put("K85", "Pressure");
		unitToGroup.put("_84", "Pressure");
		unitToGroup.put("N13", "Pressure");
		unitToGroup.put("N14", "Pressure");
		unitToGroup.put("N15", "Pressure");
		unitToGroup.put("N16", "Pressure");
		unitToGroup.put("N17", "Pressure");
		unitToGroup.put("N18", "Pressure");
		unitToGroup.put("N19", "Pressure");
		unitToGroup.put("N20", "Pressure");
		unitToGroup.put("N21", "Pressure");
		unitToGroup.put("N22", "Pressure");
		unitToGroup.put("N23", "Pressure");
		unitToGroup.put("HN", "Pressure");
		unitToGroup.put("PS", "Pressure");

		unitToGroup.put("CUR", "RadioActivity");
		unitToGroup.put("MCU", "RadioActivity");
		unitToGroup.put("M5", "RadioActivity");
		unitToGroup.put("_2R", "RadioActivity");
		unitToGroup.put("BQL", "RadioActivity");
		unitToGroup.put("GBQ", "RadioActivity");
		unitToGroup.put("_2Q", "RadioActivity");
		unitToGroup.put("_4N", "RadioActivity");
		unitToGroup.put("H08", "RadioActivity");
		
		unitToGroup.put("OHM", "Resistance");
		unitToGroup.put("A87", "Resistance");
		unitToGroup.put("B75", "Resistance");
		unitToGroup.put("H44", "Resistance");
		unitToGroup.put("B49", "Resistance");
		unitToGroup.put("E45", "Resistance");
		unitToGroup.put("B94", "Resistance");
		unitToGroup.put("P22", "Resistance");
		
		unitToGroup.put("A39", "SpecificVolume");
		unitToGroup.put("_22", "SpecificVolume");
		unitToGroup.put("H83", "SpecificVolume");
		unitToGroup.put("KX", "SpecificVolume");
		unitToGroup.put("N28", "SpecificVolume");
		unitToGroup.put("N29", "SpecificVolume");
		unitToGroup.put("N30", "SpecificVolume");
		
		unitToGroup.put("KMH", "Speed");
		unitToGroup.put("MTS", "Speed");
		unitToGroup.put("HM", "Speed");
		unitToGroup.put("M57", "Speed");
		unitToGroup.put("M58", "Speed");
		unitToGroup.put("KNT", "Speed");
		unitToGroup.put("M62", "Speed");
		unitToGroup.put("H49", "Speed");
		unitToGroup.put("_2M", "Speed");
		unitToGroup.put("K14", "Speed");
		unitToGroup.put("FR", "Speed");
		unitToGroup.put("FS", "Speed");
		unitToGroup.put("M63", "Speed");
		unitToGroup.put("IU", "Speed");
		unitToGroup.put("M61", "Speed");
		unitToGroup.put("_2X", "Speed");
		
		unitToGroup.put("KEL", "Temperature");
		unitToGroup.put("FAH", "Temperature");
		unitToGroup.put("CEL", "Temperature");
		unitToGroup.put("A48", "Temperature");
		
		unitToGroup.put("SEC", "Time");
		unitToGroup.put("MIN", "Time");
		unitToGroup.put("HUR", "Time");
		unitToGroup.put("DAY", "Time");
		unitToGroup.put("B52", "Time");
		unitToGroup.put("C26", "Time");
		unitToGroup.put("H70", "Time");
		unitToGroup.put("B98", "Time");
		unitToGroup.put("C47", "Time");
		unitToGroup.put("WEE", "Time");
		unitToGroup.put("MON", "Time");
		unitToGroup.put("ANN", "Time");
		unitToGroup.put("D42", "Time");
		unitToGroup.put("L95", "Time");
		unitToGroup.put("L96", "Time");
		unitToGroup.put("M56", "Time");
	
		unitToGroup.put("NU", "Torque");
		unitToGroup.put("B74", "Torque");
		unitToGroup.put("B48", "Torque");
		unitToGroup.put("D83", "Torque");
		unitToGroup.put("B93", "Torque");
		unitToGroup.put("DN", "Torque");
		unitToGroup.put("J72", "Torque");
		unitToGroup.put("B38", "Torque");
		unitToGroup.put("F21", "Torque");
		unitToGroup.put("J94", "Torque");
		unitToGroup.put("L41", "Torque");
		unitToGroup.put("M92", "Torque");
		unitToGroup.put("M95", "Torque");
		unitToGroup.put("M96", "Torque");
		unitToGroup.put("M97", "Torque");

		unitToGroup.put("VLT", "Voltage");
		unitToGroup.put("N99", "Voltage");
		unitToGroup.put("_2Z", "Voltage");
		unitToGroup.put("B78", "Voltage");
		unitToGroup.put("D82", "Voltage");
		unitToGroup.put("KVT", "Voltage");
		
		unitToGroup.put("MTQ", "Volume");
		unitToGroup.put("MAL", "Volume");
		unitToGroup.put("LTR", "Volume");
		unitToGroup.put("MMQ", "Volume");
		unitToGroup.put("CMQ", "Volume");
		unitToGroup.put("DMQ", "Volume");
		unitToGroup.put("MLT", "Volume");
		unitToGroup.put("CLT", "Volume");
		unitToGroup.put("DMA", "Volume");
		unitToGroup.put("H19", "Volume");
		unitToGroup.put("H20", "Volume");
		unitToGroup.put("DLT", "Volume");
		unitToGroup.put("_4G", "Volume");
		unitToGroup.put("K6", "Volume");
		unitToGroup.put("A44", "Volume");
		unitToGroup.put("INQ", "Volume");
		unitToGroup.put("FTQ", "Volume");
		unitToGroup.put("YDQ", "Volume");
		unitToGroup.put("GLI", "Volume");
		unitToGroup.put("GLL", "Volume");
		unitToGroup.put("PT", "Volume");
		unitToGroup.put("PTI", "Volume");
		unitToGroup.put("QTI", "Volume");
		unitToGroup.put("PTL", "Volume");
		unitToGroup.put("QTL", "Volume");
		unitToGroup.put("PTD", "Volume");
		unitToGroup.put("OZI", "Volume");
		unitToGroup.put("QT", "Volume");
		unitToGroup.put("J57", "Volume");
		unitToGroup.put("L43", "Volume");
		unitToGroup.put("L61", "Volume");
		unitToGroup.put("L62", "Volume");
		unitToGroup.put("L84", "Volume");
		unitToGroup.put("L86", "Volume");
		unitToGroup.put("OZA", "Volume");
		unitToGroup.put("BUI", "Volume");
		unitToGroup.put("BUA", "Volume");
		unitToGroup.put("BLL", "Volume");
		unitToGroup.put("BLD", "Volume");
		unitToGroup.put("GLD", "Volume");
		unitToGroup.put("QTD", "Volume");
		unitToGroup.put("G26", "Volume");
		unitToGroup.put("G21", "Volume");
		unitToGroup.put("G24", "Volume");
		unitToGroup.put("G25", "Volume");
		unitToGroup.put("G23", "Volume");
		unitToGroup.put("M67", "Volume");
		unitToGroup.put("M68", "Volume");
		unitToGroup.put("M69", "Volume");
		unitToGroup.put("M70", "Volume");
	}

	public String getRepresentativeType(String uom) {
		return unitToGroup.get(uom);
	}
	
	/**
	 * get a value of a representative type of uom where each unit group has a
	 * representative value to be stored
	 * 
	 * @param uom
	 * @param value
	 * @return
	 */
	public Double getRepresentativeValue(String uom, Double value) {

		// uom 을 대표
		String group = unitToGroup.get(uom);
		if(group == null)
			return null;
		
		if (group.equals("AbsorbedDose")) {
			AbsorbedDose val = new AbsorbedDose(uom, value);
			int idx = val.getType().ordinal();
			return (value * AbsorbedDose.multipliers[idx] + AbsorbedDose.offsets[idx] - AbsorbedDose.ro) / AbsorbedDose.rm;
		} else if (group.equals("AbsorbedDoseRate")) {
			AbsorbedDoseRate val = new AbsorbedDoseRate(uom, value);
			int idx = val.getType().ordinal();
			return (value * AbsorbedDoseRate.multipliers[idx] + AbsorbedDoseRate.offsets[idx] - AbsorbedDoseRate.ro) / AbsorbedDoseRate.rm;
		} else if (group.equals("Acceleration")) {
			Acceleration val = new Acceleration(uom, value);
			int idx = val.getType().ordinal();
			return (value * Acceleration.multipliers[idx] + Acceleration.offsets[idx] - Acceleration.ro) / Acceleration.rm;
		} else if (group.equals("AmountOfSubstance")) {
			AmountOfSubstance val = new AmountOfSubstance(uom, value);
			int idx = val.getType().ordinal();
			return (value * AmountOfSubstance.multipliers[idx] + AmountOfSubstance.offsets[idx] - AmountOfSubstance.ro) / AmountOfSubstance.rm;
		} else if (group.equals("Angle")) {
			Angle val = new Angle(uom, value);
			int idx = val.getType().ordinal();
			return (value * Angle.multipliers[idx] + Angle.offsets[idx] - Angle.ro) / Angle.rm;
		} else if (group.equals("Area")) {
			Area val = new Area(uom, value);
			int idx = val.getType().ordinal();
			return (value * Area.multipliers[idx] + Area.offsets[idx] - Area.ro) / Area.rm;
		} else if (group.equals("Capacitance")) {
			Capacitance val = new Capacitance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Capacitance.multipliers[idx] + Capacitance.offsets[idx] - Capacitance.ro) / Capacitance.rm;
		} else if (group.equals("Charge")) {
			Charge val = new Charge(uom, value);
			int idx = val.getType().ordinal();
			return (value * Charge.multipliers[idx] + Charge.offsets[idx] - Charge.ro) / Charge.rm;
		} else if (group.equals("Conductance")) {
			Conductance val = new Conductance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Conductance.multipliers[idx] + Conductance.offsets[idx] - Conductance.ro) / Conductance.rm;
		} else if (group.equals("Current")) {
			Current val = new Current(uom, value);
			int idx = val.getType().ordinal();
			return (value * Current.multipliers[idx] + Current.offsets[idx] - Current.ro) / Current.rm;
		} else if (group.equals("DensityHumidity")) {
			DensityHumidity val = new DensityHumidity(uom, value);
			int idx = val.getType().ordinal();
			return (value * DensityHumidity.multipliers[idx] + DensityHumidity.offsets[idx] - DensityHumidity.ro) / DensityHumidity.rm;
		} else if (group.equals("DimensionlessConcentration")) {
			DimensionlessConcentration val = new DimensionlessConcentration(uom, value);
			int idx = val.getType().ordinal();
			return (value * DimensionlessConcentration.multipliers[idx] + DimensionlessConcentration.offsets[idx] - DimensionlessConcentration.ro) / DimensionlessConcentration.rm;
		} else if (group.equals("EffectiveDose")) {
			EffectiveDose val = new EffectiveDose(uom, value);
			int idx = val.getType().ordinal();
			return (value * EffectiveDose.multipliers[idx] + EffectiveDose.offsets[idx] - EffectiveDose.ro) / EffectiveDose.rm;
		} else if (group.equals("EffectiveDoseRate")) {
			EffectiveDoseRate val = new EffectiveDoseRate(uom, value);
			int idx = val.getType().ordinal();
			return (value * EffectiveDoseRate.multipliers[idx] + EffectiveDoseRate.offsets[idx] - EffectiveDoseRate.ro) / EffectiveDoseRate.rm;
		} else if (group.equals("Energy")) {
			Energy val = new Energy(uom, value);
			int idx = val.getType().ordinal();
			return (value * Energy.multipliers[idx] + Energy.offsets[idx] - Energy.ro) / Energy.rm;
		} else if (group.equals("Force")) {
			Force val = new Force(uom, value);
			int idx = val.getType().ordinal();
			return (value * Force.multipliers[idx] + Force.offsets[idx] - Force.ro) / Force.rm;
		} else if (group.equals("Frequency")) {
			Frequency val = new Frequency(uom, value);
			int idx = val.getType().ordinal();
			return (value * Frequency.multipliers[idx] + Frequency.offsets[idx] - Frequency.ro) / Frequency.rm;
		} else if (group.equals("Illuminance")) {
			Illuminance val = new Illuminance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Illuminance.multipliers[idx] + Illuminance.offsets[idx] - Illuminance.ro) / Illuminance.rm;
		} else if (group.equals("Inductance")) {
			Inductance val = new Inductance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Inductance.multipliers[idx] + Inductance.offsets[idx] - Inductance.ro) / Inductance.rm;
		} else if (group.equals("Length")) {
			Length val = new Length(uom, value);
			int idx = val.getType().ordinal();
			return (value * Length.multipliers[idx] + Length.offsets[idx] - Length.ro) / Length.rm;
		} else if (group.equals("LuminousIntensity")) {
			LuminousIntensity val = new LuminousIntensity(uom, value);
			int idx = val.getType().ordinal();
			return (value * LuminousIntensity.multipliers[idx] + LuminousIntensity.offsets[idx] - LuminousIntensity.ro) / LuminousIntensity.rm;
		} else if (group.equals("MagneticFlux")) {
			MagneticFlux val = new MagneticFlux(uom, value);
			int idx = val.getType().ordinal();
			return (value * MagneticFlux.multipliers[idx] + MagneticFlux.offsets[idx] - MagneticFlux.ro) / MagneticFlux.rm;
		} else if (group.equals("MagneticFluxDensity")) {
			MagneticFluxDensity val = new MagneticFluxDensity(uom, value);
			int idx = val.getType().ordinal();
			return (value * MagneticFluxDensity.multipliers[idx] + MagneticFluxDensity.offsets[idx] - MagneticFluxDensity.ro) / MagneticFluxDensity.rm;
		} else if (group.equals("MagneticVectorPotential")) {
			MagneticVectorPotential val = new MagneticVectorPotential(uom, value);
			int idx = val.getType().ordinal();
			return (value * MagneticVectorPotential.multipliers[idx] + MagneticVectorPotential.offsets[idx] - MagneticVectorPotential.ro) / MagneticVectorPotential.rm;
		} else if (group.equals("Mass")) {
			Mass val = new Mass(uom, value);
			int idx = val.getType().ordinal();
			return (value * Mass.multipliers[idx] + Mass.offsets[idx] - Mass.ro) / Mass.rm;
		} else if (group.equals("MolarConcentration")) {
			MolarConcentration val = new MolarConcentration(uom, value);
			int idx = val.getType().ordinal();
			return (value * MolarConcentration.multipliers[idx] + MolarConcentration.offsets[idx] - MolarConcentration.ro) / MolarConcentration.rm;
		} else if (group.equals("MolarMass")) {
			MolarMass val = new MolarMass(uom, value);
			int idx = val.getType().ordinal();
			return (value * MolarMass.multipliers[idx] + MolarMass.offsets[idx] - MolarMass.ro) / MolarMass.rm;
		} else if (group.equals("MolarThermodynamicEnergy")) {
			MolarThermodynamicEnergy val = new MolarThermodynamicEnergy(uom, value);
			int idx = val.getType().ordinal();
			return (value * MolarThermodynamicEnergy.multipliers[idx] + MolarThermodynamicEnergy.offsets[idx] - MolarThermodynamicEnergy.ro) / MolarThermodynamicEnergy.rm;
		} else if (group.equals("MolarVolume")) {
			MolarVolume val = new MolarVolume(uom, value);
			int idx = val.getType().ordinal();
			return (value * MolarVolume.multipliers[idx] + MolarVolume.offsets[idx] - MolarVolume.ro) / MolarVolume.rm;
		} else if (group.equals("Power")) {
			Power val = new Power(uom, value);
			int idx = val.getType().ordinal();
			return (value * Power.multipliers[idx] + Power.offsets[idx] - Power.ro) / Power.rm;
		} else if (group.equals("Pressure")) {
			Pressure val = new Pressure(uom, value);
			int idx = val.getType().ordinal();
			return (value * Pressure.multipliers[idx] + Pressure.offsets[idx] - Pressure.ro) / Pressure.rm;
		} else if (group.equals("RadioActivity")) {
			RadioActivity val = new RadioActivity(uom, value);
			int idx = val.getType().ordinal();
			return (value * RadioActivity.multipliers[idx] + RadioActivity.offsets[idx] - RadioActivity.ro) / RadioActivity.rm;
		} else if (group.equals("Resistance")) {
			Resistance val = new Resistance(uom, value);
			int idx = val.getType().ordinal();
			return (value * Resistance.multipliers[idx] + Resistance.offsets[idx] - Resistance.ro) / Resistance.rm;
		} else if (group.equals("SpecificVolume")) {
			SpecificVolume val = new SpecificVolume(uom, value);
			int idx = val.getType().ordinal();
			return (value * SpecificVolume.multipliers[idx] + SpecificVolume.offsets[idx] - SpecificVolume.ro) / SpecificVolume.rm;
		} else if (group.equals("Speed")) {
			Speed val = new Speed(uom, value);
			int idx = val.getType().ordinal();
			return (value * Speed.multipliers[idx] + Speed.offsets[idx] - Speed.ro) / Speed.rm;
		} else if (group.equals("Temperature")) {
			Temperature val = new Temperature(uom, value);
			int idx = val.getType().ordinal();
			return (value * Temperature.multipliers[idx] + Temperature.offsets[idx] - Temperature.ro) / Temperature.rm;
		} else if (group.equals("Time")) {
			Time val = new Time(uom, value);
			int idx = val.getType().ordinal();
			return (value * Time.multipliers[idx] + Time.offsets[idx] - Time.ro) / Time.rm;
		} else if (group.equals("Torque")) {
			Torque val = new Torque(uom, value);
			int idx = val.getType().ordinal();
			return (value * Torque.multipliers[idx] + Torque.offsets[idx] - Torque.ro) / Torque.rm;
		} else if (group.equals("Voltage")) {
			Voltage val = new Voltage(uom, value);
			int idx = val.getType().ordinal();
			return (value * Voltage.multipliers[idx] + Voltage.offsets[idx] - Voltage.ro) / Voltage.rm;
		} else if (group.equals("Volume")) {
			Volume val = new Volume(uom, value);
			int idx = val.getType().ordinal();
			return (value * Volume.multipliers[idx] + Volume.offsets[idx] - Volume.ro) / Volume.rm;
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
		} else if (input instanceof Charge) {
			Charge inputObj = (Charge) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Charge.multipliers[inputIdx];
			Double inputOffsets = Charge.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Charge.Type) {
				Charge.Type type = (Charge.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Charge.multipliers[outputIdx];
				outputOffset = Charge.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Charge.Type.values()[outputIdx]);
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
		} else if (input instanceof Current) {
			Current inputObj = (Current) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Current.multipliers[inputIdx];
			Double inputOffsets = Current.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof Current.Type) {
				Current.Type type = (Current.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = Current.multipliers[outputIdx];
				outputOffset = Current.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(Current.Type.values()[outputIdx]);
		} else if (input instanceof DensityHumidity) {
			DensityHumidity inputObj = (DensityHumidity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = DensityHumidity.multipliers[inputIdx];
			Double inputOffsets = DensityHumidity.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof DensityHumidity.Type) {
				DensityHumidity.Type type = (DensityHumidity.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = DensityHumidity.multipliers[outputIdx];
				outputOffset = DensityHumidity.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(DensityHumidity.Type.values()[outputIdx]);
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
		} else if (input instanceof EffectiveDose) {
			EffectiveDose inputObj = (EffectiveDose) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = EffectiveDose.multipliers[inputIdx];
			Double inputOffsets = EffectiveDose.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof EffectiveDose.Type) {
				EffectiveDose.Type type = (EffectiveDose.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = EffectiveDose.multipliers[outputIdx];
				outputOffset = EffectiveDose.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(EffectiveDose.Type.values()[outputIdx]);
		} else if (input instanceof EffectiveDoseRate) {
			EffectiveDoseRate inputObj = (EffectiveDoseRate) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = EffectiveDoseRate.multipliers[inputIdx];
			Double inputOffsets = EffectiveDoseRate.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof EffectiveDoseRate.Type) {
				EffectiveDoseRate.Type type = (EffectiveDoseRate.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = EffectiveDoseRate.multipliers[outputIdx];
				outputOffset = EffectiveDoseRate.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(EffectiveDoseRate.Type.values()[outputIdx]);
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
		} else if (input instanceof MolarThermodynamicEnergy) {
			MolarThermodynamicEnergy inputObj = (MolarThermodynamicEnergy) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarThermodynamicEnergy.multipliers[inputIdx];
			Double inputOffsets = MolarThermodynamicEnergy.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof MolarThermodynamicEnergy.Type) {
				MolarThermodynamicEnergy.Type type = (MolarThermodynamicEnergy.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = MolarThermodynamicEnergy.multipliers[outputIdx];
				outputOffset = MolarThermodynamicEnergy.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(MolarThermodynamicEnergy.Type.values()[outputIdx]);
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
		} else if (input instanceof RadioActivity) {
			RadioActivity inputObj = (RadioActivity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = RadioActivity.multipliers[inputIdx];
			Double inputOffsets = RadioActivity.offsets[inputIdx];
			int outputIdx = -1;
			Double outputMultiplier = null;
			Double outputOffset = null;
			if (outputUnit instanceof RadioActivity.Type) {
				RadioActivity.Type type = (RadioActivity.Type) outputUnit;
				outputIdx = type.ordinal();
				outputMultiplier = RadioActivity.multipliers[outputIdx];
				outputOffset = RadioActivity.offsets[outputIdx];
			} else {
				return false;
			}
			inputObj.setValue((inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier);
			inputObj.setType(RadioActivity.Type.values()[outputIdx]);
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
		} else if (input instanceof Charge) {
			Charge inputObj = (Charge) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Charge.multipliers[inputIdx];
			Double inputOffsets = Charge.offsets[inputIdx];
			Charge.Type[] types = Charge.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Charge.Type type = types[i];
				outputMultiplier = Charge.multipliers[i];
				outputOffset = Charge.offsets[i];
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
		} else if (input instanceof Current) {
			Current inputObj = (Current) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = Current.multipliers[inputIdx];
			Double inputOffsets = Current.offsets[inputIdx];
			Current.Type[] types = Current.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				Current.Type type = types[i];
				outputMultiplier = Current.multipliers[i];
				outputOffset = Current.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof DensityHumidity) {
			DensityHumidity inputObj = (DensityHumidity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = DensityHumidity.multipliers[inputIdx];
			Double inputOffsets = DensityHumidity.offsets[inputIdx];
			DensityHumidity.Type[] types = DensityHumidity.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				DensityHumidity.Type type = types[i];
				outputMultiplier = DensityHumidity.multipliers[i];
				outputOffset = DensityHumidity.offsets[i];
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
		} else if (input instanceof EffectiveDose) {
			EffectiveDose inputObj = (EffectiveDose) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = EffectiveDose.multipliers[inputIdx];
			Double inputOffsets = EffectiveDose.offsets[inputIdx];
			EffectiveDose.Type[] types = EffectiveDose.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				EffectiveDose.Type type = types[i];
				outputMultiplier = EffectiveDose.multipliers[i];
				outputOffset = EffectiveDose.offsets[i];
				double val = (inputObj.getValue() * inputMultiplier + inputOffsets - outputOffset) / outputMultiplier;
				obj.put("rec20", type.toString()).put("name", type.getName()).put("symbol", type.getSymbol())
						.put("value", val);
				result.add(obj);
			}
		} else if (input instanceof EffectiveDoseRate) {
			EffectiveDoseRate inputObj = (EffectiveDoseRate) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = EffectiveDoseRate.multipliers[inputIdx];
			Double inputOffsets = EffectiveDoseRate.offsets[inputIdx];
			EffectiveDoseRate.Type[] types = EffectiveDoseRate.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				EffectiveDoseRate.Type type = types[i];
				outputMultiplier = EffectiveDoseRate.multipliers[i];
				outputOffset = EffectiveDoseRate.offsets[i];
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
		} else if (input instanceof MolarThermodynamicEnergy) {
			MolarThermodynamicEnergy inputObj = (MolarThermodynamicEnergy) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = MolarThermodynamicEnergy.multipliers[inputIdx];
			Double inputOffsets = MolarThermodynamicEnergy.offsets[inputIdx];
			MolarThermodynamicEnergy.Type[] types = MolarThermodynamicEnergy.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				MolarThermodynamicEnergy.Type type = types[i];
				outputMultiplier = MolarThermodynamicEnergy.multipliers[i];
				outputOffset = MolarThermodynamicEnergy.offsets[i];
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
		} else if (input instanceof RadioActivity) {
			RadioActivity inputObj = (RadioActivity) input;
			int inputIdx = inputObj.getType().ordinal();
			Double inputMultiplier = RadioActivity.multipliers[inputIdx];
			Double inputOffsets = RadioActivity.offsets[inputIdx];
			RadioActivity.Type[] types = RadioActivity.Type.values();
			Double outputMultiplier = null;
			Double outputOffset = null;
			for (int i = 0; i < types.length; i++) {
				JsonObject obj = new JsonObject();
				RadioActivity.Type type = types[i];
				outputMultiplier = RadioActivity.multipliers[i];
				outputOffset = RadioActivity.offsets[i];
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
