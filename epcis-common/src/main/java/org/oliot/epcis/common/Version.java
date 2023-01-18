package org.oliot.epcis.common;

/**
 * https://ref.gs1.org/standards/epcis/archive
 * 
 * @author Sejong
 *
 */
public enum Version {
	v2_0_0("2.0.0"), v1_2_0("1.2.0"), v1_1_0("1.1.0"), v1_0_1("1.0.1");

	private String version;

	private Version(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public static boolean isValid(String version) {
		if ( getVersion(version) == null )
			return false;
		else
			return true;
	}

	public static int getNumber(Version version) throws NullPointerException {
		if (version == Version.v2_0_0) {
			return 4;
		} else if (version == Version.v1_2_0) {
			return 3;
		} else if (version == Version.v1_1_0) {
			return 2;
		} else if (version == Version.v1_0_1) {
			return 1;
		} else
			throw new NullPointerException();
	}

	public static Version getVersion(String version) {
		if (version.equals(Version.v2_0_0.getVersion())) {
			return Version.v2_0_0;
		} else if (version.equals(Version.v1_2_0.getVersion())) {
			return Version.v1_2_0;
		} else if (version.equals(Version.v1_1_0.getVersion())) {
			return Version.v1_1_0;
		} else if (version.equals(Version.v1_0_1.getVersion())) {
			return Version.v1_0_1;
		} else {
			return null;
		}
	}

	public static boolean isCompatible(Version systemVersion, Version minVersion, Version maxVersion) {
		int system = getNumber(systemVersion);
		int min = getNumber(minVersion);
		int max = getNumber(maxVersion);
		if (system < min)
			return false;
		if (system > max)
			return false;
		return true;
	}

	public static boolean isCompatible(String system, String min, String max) {
		Version minVersion = getVersion(min);
		if(minVersion == null)
			minVersion = Version.v1_0_1;
		Version maxVersion = getVersion(max);
		if(maxVersion == null)
			maxVersion = Version.v2_0_0;

		return isCompatible(getVersion(system), minVersion, maxVersion);
	}
}
