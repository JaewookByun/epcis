package org.oliot.epcis.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class ConfigurationServlet
 */
public class ConfigurationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String backend;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConfigurationServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(ServletConfig servletConfig ) throws ServletException
	{
		String path = servletConfig.getServletContext().getRealPath("/WEB-INF");
		try
		{
			File file = new File(path+"/Configuration.json");
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);

			String data = "";
			String line = null;
			while(( line = reader.readLine()) != null )
			{
				data += line;
			}
			reader.close();
			JSONObject json = new JSONObject(data);
			String backend = json.getString("backend");
			if( backend == null )
			{
				System.out.println( "[Configuration Servlet] : Backend is null, please restart the service ");
			}
			else
			{
				ConfigurationServlet.backend = backend;
				System.out.println( "[Configuration Servlet] : Backend - " + ConfigurationServlet.backend);
			}
		}catch(Exception ex)
		{

		}
	}
}
