package org.oliot.epccr.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is incubating project named Electronic Product Code Context
 * Repository (EPCCR). This project pursues Resource Oriented Architecture (ROA)
 * for EPC-based event
 * 
 * Commonality with EPCIS Getting powered with EPC's global uniqueness
 * 
 * Differences Resource Oriented, not Service Oriented Resource(EPC)-driven URL
 * scheme Best efforts to comply RESTful principle Exploit flexibility rather
 * than formal verification JSON vs. XML NOSQL vs. SQL Focus on the Internet of
 * Things beyond Supply Chain Management
 * 
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@{kaist.ac.kr,gmail.com}
 */
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		InputStream is = request.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");	
		System.out.println("TEST Servlet: " + writer.toString());
	}

}
