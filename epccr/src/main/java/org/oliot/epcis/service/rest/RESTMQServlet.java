package org.oliot.epcis.service.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * Servlet implementation class RESTMQServlet
 */
public class RESTMQServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RESTMQServlet() {
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
		if (ConfigurationServlet.isRestMessageQueueOn == true) {
			ConfigurationServlet.logger
					.info("REST Message Queue Service - Started ");
			ConfigurationServlet.logger
					.info("REST Message Queue Service - Number of Capture Listener: "
							+ ConfigurationServlet.numRestCaptureListener);
			ConfigurationServlet.logger
			.info("REST Message Queue Service - REST Capture Queue Name: "
					+ ConfigurationServlet.restCaptureQueue);
			
			// Message Queue Initialization
			ConnectionFactory connectionFactory = new CachingConnectionFactory();
			AmqpAdmin admin = new RabbitAdmin(connectionFactory);
			boolean isExistQueue = admin.deleteQueue(ConfigurationServlet.restCaptureQueue);
			if( isExistQueue == true )
			{
				ConfigurationServlet.logger
				.info("REST Capture Queue Initialized");
			}
			Queue queue = new Queue(ConfigurationServlet.restCaptureQueue);
			admin.declareQueue(queue);
			
			for(int i = 0 ; i < ConfigurationServlet.numRestCaptureListener ; i ++ )
			{
				SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
				container.setConnectionFactory(connectionFactory);
				container.setQueueNames(ConfigurationServlet.restCaptureQueue);
				RESTCaptureMQListener listener = new RESTCaptureMQListener();
				container.setMessageListener(listener);
				container.start();
				ConfigurationServlet.logger
				.info("REST Message Queue Service - REST Capture Listener " + (i+1) + " started");
			}
		}
	}
}
