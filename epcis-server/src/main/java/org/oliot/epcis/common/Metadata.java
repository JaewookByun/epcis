package org.oliot.epcis.common;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Metadata
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class Metadata {
	public static int GS1_CAPTURE_limit = 10;
	public static int GS1_CAPTURE_file_size_limit = 40960;
	public static long GS1_Next_Page_Token_Expires = 300000;
	public static String GS1_EPCIS_Capture_Error_Behaviour = "rollback";
	public static String GS1_EPCIS_Version = Version.v2_0_0.getVersion();
	public static String GS1_CBV_Version = Version.v2_0_0.getVersion();
	public static String GS1_Extensions = "";
	public static String GS1_Vendor_Version = "org.oliot.epcis-2.2.0";
	public static String GS1_EPC_Format_SOAP = "Always_EPC_URN";
	public static String GS1_EPC_Format_REST = "Always_GS1_Digital_Link";
	public static String GS1_CBV_XML_Format_SOAP = "Always_URN";
	public static String GS1_CBV_XML_Format_REST = "Always_Web_URI";
}
