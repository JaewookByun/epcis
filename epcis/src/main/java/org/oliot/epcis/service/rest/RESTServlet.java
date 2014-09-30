package org.oliot.epcis.service.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.rest.mongodb.MongoRESTSubscription;

/**
 * Servlet implementation class RESTServlet
 */
public class RESTServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RESTServlet() {
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
	public void init() {
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			if (ConfigurationServlet.isMessageQueueOn == true) {
				MongoRESTSubscription msRest = new MongoRESTSubscription();
				msRest.init();
			}
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}

	}
	
}
