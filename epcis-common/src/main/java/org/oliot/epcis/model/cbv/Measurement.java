package org.oliot.epcis.model.cbv;

public enum Measurement {
	AbsorbedDose("gs1:Absorbed_dose"), AbsorbedDoseRate("gs1:Absorbed_dose_rate"), Acceleration("gs1:Acceleration"),
	AmountOfSubstance("gs1:Amount_of_substance"), Angle("gs1:Angle"), Area("gs1:Area"), Capacitance("gs1:Capacitance"),
	Charge("gs1:Charge"), Conductance("gs1:Conductance"), Current("gs1:Current"),
	DensityHumidity("gs1:Density_humidity"), DimensionlessConcentration("gs1:Dimensionless_concentration"),
	EffectiveDose("gs1:Effective_dose"), EffectiveDoseRate("gs1:Effective_dose_rate"), Energy("gs1:Energy"),
	Force("gs1:Force"), Frequency("gs1:Frequency"), Illuminance("gs1:Illuminance"), Inductance("gs1:Inductance"),
	Length("gs1:Length"), Luminous_intensity("gs1:Luminous_intensity"), MagneticFlux("gs1:Magnetic_flux"),
	MagneticFluxDensity("gs1:Magnetic_flux_density"), MagneticVectorPotential("gs1:Magnetic_vector_potential"),
	Mass("gs1:Mass"), MolarConcentration("gs1:Molar_concentration"), MolarMass("gs1:Molar_mass"),
	MolarThermodynamicEnergy("gs1:Molar_thermodynamic_energy"), MolarVolume("gs1:Molar_volume"), Power("gs1:Power"),
	Pressure("gs1:Pressure"), Radioactivity("gs1:RadioActivity"), Resistance("gs1:Resistance"),
	SpecificVolume("gs1:Specific_volume"), Speed("gs1:Speed"), Temperature("gs1:Temperature"), Time("gs1:Time"),
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
		}catch(IllegalArgumentException e) {
			return shortCBV;
		}
	}
	
	/**
	 * @param cbv
	 * @return shortCBV or cbv if it is not defined in standard
	 */
	public static String getShortVocabularyName(String cbv) {
		Measurement[] cbvs = Measurement.values();
		for(Measurement v: cbvs) {
			if(cbv.equals(v.measurement))
				return v.name();
		}
		return cbv;
	}
}
