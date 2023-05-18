package org.oliot.epcis.model.cbv;

public enum Measurement {
	AbsoluteHumidity("gs1:AbsoluteHumidity"), AbsorbedDose("gs1:AbsorbedDose"),
	AbsorbedDoseRate("gs1:AbsorbedDoseRate"), Acceleration("gs1:Acceleration"), Altitude("gs1:Altitude"),
	AmountOfSubstance("gs1:AmountOfSubstance"), AmountOfSubstancePerUnitVolume("gs1:AmountOfSubstancePerUnitVolume"),
	Angle("gs1:Angle"), Area("gs1:Area"), Capacitance("gs1:Capacitance"), Conductance("gs1:Conductance"),
	Count("gs1:Count"), Density("gs1:Density"), Dimensionless("gs1:Dimensionless"),
	DoseEquivalent("gs1:DoseEquivalent"), DoseEquivalentRate("gs1:DoseEquivalentRate"),
	DynamicViscosity("gs1:DynamicViscosity"), ElectricCharge("gs1:ElectricCharge"),
	ElectricCurrent("gs1:ElectricCurrent"), Energy("gs1:Energy"), Force("gs1:Force"), Frequency("gs1:Frequency"),
	Illuminance("gs1:Illuminance"), Inductance("gs1:Inductance"), Length("gs1:Length"),
	LuminousIntensity("gs1:LuminousIntensity"), MagneticFlux("gs1:MagneticFlux"),
	MagneticFluxDensity("gs1:MagneticFluxDensity"), MagneticVectorPotential("gs1:MagneticVectorPotential"),
	Mass("gs1:Mass"), MassConcentration("gs1:MassConcentration"), MolarEnergy("gs1:MolarEnergy"),
	MolarMass("gs1:MolarMass"), MolarVolume("gs1:Molar_volume"), Power("gs1:Power"), Pressure("gs1:Pressure"),
	Radioactivity("gs1:RadioActivity"), RelativeHumidity("gs1:RelativeHumidity"), Resistance("gs1:Resistance"),
	SpecificVolume("gs1:SpecificVolume"), Speed("gs1:Speed"), Temperature("gs1:Temperature"), Time("gs1:Time"),
	Torque("gs1:Torque"), Voltage("gs1:Voltage"), Volume("gs1:Volume");

	private String measurement;

	private Measurement(String measurement) {
		this.measurement = measurement;
	}

	public String getMeasurement() {
		return measurement;
	}

	/**
	 * @param shortCBV
	 * @return CBV or shortCBV if it is not defined in standard
	 */
	public static String getFullVocabularyName(String shortCBV) {
		try {
			return Measurement.valueOf(shortCBV).measurement;
		} catch (IllegalArgumentException e) {
			return shortCBV;
		}
	}

	/**
	 * @param cbv
	 * @return shortCBV or cbv if it is not defined in standard
	 */
	public static String getShortVocabularyName(String cbv) {
		Measurement[] cbvs = Measurement.values();
		for (Measurement v : cbvs) {
			if (cbv.equals(v.measurement))
				return v.name();
		}
		return cbv;
	}
}
