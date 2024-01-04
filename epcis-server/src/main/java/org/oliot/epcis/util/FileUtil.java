package org.oliot.epcis.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
public class FileUtil {
	public static String readFile(InputStream is) throws IOException {
		return new String(is.readAllBytes());
	}

	public static String readFile(String loc) throws IOException {
		return Files.readString(Paths.get(loc));
	}
	
	public static byte[] readFileAsBytes(String loc) throws IOException {
		return Files.readAllBytes(Paths.get(loc));
	}
	
	public static byte[] getByteArray(String xmlString) {
		return xmlString.getBytes(StandardCharsets.UTF_8);
	}
}
