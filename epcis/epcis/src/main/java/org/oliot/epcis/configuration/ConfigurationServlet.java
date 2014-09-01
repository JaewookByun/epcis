package org.oliot.epcis.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * Servlet implementation class ConfigurationServlet
 */
public class ConfigurationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String backend;
	public static Logger logger;
	public static String webInfoPath;
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
		// Log4j Setting
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		ConfigurationServlet.logger = Logger.getRootLogger();
		String path = servletConfig.getServletContext().getRealPath("/WEB-INF");
		try
		{
			// Set up Backend
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
				ConfigurationServlet.logger.info("Backend is null, please restart the service");
			}
			else
			{
				ConfigurationServlet.backend = backend;
				ConfigurationServlet.logger.info("Backend - " + ConfigurationServlet.backend);
			}
			ConfigurationServlet.webInfoPath = servletConfig.getServletContext().getRealPath("/WEB-INF");
			
		}catch(Exception ex)
		{
			ConfigurationServlet.logger.error(ex.toString());
		}
	}
}
