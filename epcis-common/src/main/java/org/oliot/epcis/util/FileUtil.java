package org.oliot.epcis.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
	public static String readFile(InputStream is) throws IOException {
		return new String(is.readAllBytes());
	}

	public static String readFile(String loc) throws IOException {
		return Files.readString(Paths.get(loc));
	}
}
