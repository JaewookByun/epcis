package org.oliot.gcp.core;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BinaryCodeParser {

	/**
	 * @param epcHexCode
	 *            : Hex representation for Binary RFID Tag
	 * @return pure-identity EPC
	 */
	public String parse(String epcHexCode) {

		if (epcHexCode.length() == 16) {

		} else if (epcHexCode.length() == 24) {
			String binaryCode = String.format("%96s", new BigInteger(epcHexCode, 16).toString(2)).replace(" ", "0");

			if (binaryCode.matches("00101100([01]+)")) {
				// GDTI-96

			} else if (binaryCode.matches("00110100([01]+)")) {
				// GIAI-96

			} else if (binaryCode.matches("00110101([01]+)")) {
				// GID-96

			} else if (binaryCode.matches("00110011([01]+)")) {
				// GRAI-96

			} else if (binaryCode.matches("00101101([01]+)")) {
				// GSRN-96

			} else if (binaryCode.matches("00110010([01]+)")) {
				// SGLN-96
				
			} else if (binaryCode.matches("00110000([01]+)")) {
				// SGTIN-96
				return generateSgtin96(binaryCode);
			} else if (binaryCode.matches("00110100([01]+)")) {
				// GIAI-96

			} else if (binaryCode.matches("00110001([01]+)")) {
				// SSCC-96

			}
		}
		return null;
	}

	private String generateSgtin96(String binaryCode) {
		// SGTIN-96
		Pattern pattern;
		Matcher matcher;
		String gcp = "";
		String itemref = "";
		String serial = "";
		if (binaryCode.matches("([01]{11})000([01]+)")) {
			pattern = Pattern.compile("00110000([01]{3})000([01]{40})([01]{4})([01]{38})");
			matcher = pattern.matcher(binaryCode);
			if (matcher.find()) {
				gcp = String.format("%12s", Integer.parseInt(matcher.group(2), 2)).replace(" ", "0");
				itemref = String.format("%1s", Integer.parseInt(matcher.group(3), 2)).replace(" ", "0");
				serial = String.valueOf(Long.parseLong(matcher.group(4), 2));
			} else {
				return null;
			}
		} else if (binaryCode.matches("([01]{11})001([01]+)")) {
			pattern = Pattern.compile("00110000([01]{3})001([01]{37})([01]{7})([01]{38})");
			matcher = pattern.matcher(binaryCode);
			if (matcher.find()) {
				gcp = String.format("%11s", Integer.parseInt(matcher.group(2), 2)).replace(" ", "0");
				itemref = String.format("%2s", Integer.parseInt(matcher.group(3), 2)).replace(" ", "0");
				serial = String.valueOf(Long.parseLong(matcher.group(4), 2));
			} else {
				return null;
			}
		} else if (binaryCode.matches("([01]{11})010([01]+)")) {
			pattern = Pattern.compile("00110000([01]{3})010([01]{34})([01]{10})([01]{38})");
			matcher = pattern.matcher(binaryCode);
			if (matcher.find()) {
				gcp = String.format("%10s", Integer.parseInt(matcher.group(2), 2)).replace(" ", "0");
				itemref = String.format("%3s", Integer.parseInt(matcher.group(3), 2)).replace(" ", "0");
				serial = String.valueOf(Long.parseLong(matcher.group(4), 2));
			} else {
				return null;
			}
		} else if (binaryCode.matches("([01]{11})011([01]+)")) {
			pattern = Pattern.compile("00110000([01]{3})011([01]{30})([01]{14})([01]{38})");
			matcher = pattern.matcher(binaryCode);
			if (matcher.find()) {
				gcp = String.format("%9s", Integer.parseInt(matcher.group(2), 2)).replace(" ", "0");
				itemref = String.format("%4s", Integer.parseInt(matcher.group(3), 2)).replace(" ", "0");
				serial = String.valueOf(Long.parseLong(matcher.group(4), 2));
			} else {
				return null;
			}
		} else if (binaryCode.matches("([01]{11})100([01]+)")) {
			pattern = Pattern.compile("00110000([01]{3})100([01]{27})([01]{17})([01]{38})");
			matcher = pattern.matcher(binaryCode);
			if (matcher.find()) {
				gcp = String.format("%8s", Integer.parseInt(matcher.group(2), 2)).replace(" ", "0");
				itemref = String.format("%5s", Integer.parseInt(matcher.group(3), 2)).replace(" ", "0");
				serial = String.valueOf(Long.parseLong(matcher.group(4), 2));
			} else {
				return null;
			}
		} else if (binaryCode.matches("([01]{11})101([01]+)")) {
			pattern = Pattern.compile("00110000([01]{3})101([01]{24})([01]{20})([01]{38})");
			matcher = pattern.matcher(binaryCode);
			if (matcher.find()) {
				gcp = String.format("%7s", Integer.parseInt(matcher.group(2), 2)).replace(" ", "0");
				itemref = String.format("%6s", Integer.parseInt(matcher.group(3), 2)).replace(" ", "0");
				serial = String.valueOf(Long.parseLong(matcher.group(4), 2));
			} else {
				return null;
			}
		} else if (binaryCode.matches("([01]{11})110([01]+)")) {
			pattern = Pattern.compile("00110000([01]{3})110([01]{20})([01]{24})([01]{38})");
			matcher = pattern.matcher(binaryCode);
			if (matcher.find()) {
				gcp = String.format("%6s", Integer.parseInt(matcher.group(2), 2)).replace(" ", "0");
				itemref = String.format("%7s", Integer.parseInt(matcher.group(3), 2)).replace(" ", "0");
				serial = String.valueOf(Long.parseLong(matcher.group(4), 2));
			} else {
				return null;
			}
		}
		return new String("urn:epc:id:sgtin:" + gcp + "." + itemref + "." + serial);
	}
}
