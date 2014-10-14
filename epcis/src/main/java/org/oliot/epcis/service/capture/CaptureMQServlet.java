package org.oliot.epcis.service.capture;

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
 * Servlet implementation class MessageQueueServlet
 */
public class CaptureMQServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CaptureMQServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	@Override
	public void init() {
		if (ConfigurationServlet.isMessageQueueOn == true) {
			ConfigurationServlet.logger
					.info("Message Queue Service - Started ");
			ConfigurationServlet.logger
					.info("Message Queue Service - Number of Capture Listener: "
							+ ConfigurationServlet.numCaptureListener);
			ConfigurationServlet.logger
			.info("Message Queue Service - Capture Queue Name: "
					+ ConfigurationServlet.captureQueue);
			
			// Message Queue Initialization
			ConnectionFactory connectionFactory = new CachingConnectionFactory();
			AmqpAdmin admin = new RabbitAdmin(connectionFactory);
			boolean isExistQueue = admin.deleteQueue(ConfigurationServlet.captureQueue);
			if( isExistQueue == true )
			{
				ConfigurationServlet.logger
				.info("Capture Queue Initialized");
			}
			Queue queue = new Queue(ConfigurationServlet.captureQueue);
			admin.declareQueue(queue);
			
			for(int i = 0 ; i < ConfigurationServlet.numCaptureListener ; i ++ )
			{
				SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
				container.setConnectionFactory(connectionFactory);
				container.setQueueNames(ConfigurationServlet.captureQueue);
				CaptureMQListener listener = new CaptureMQListener();
				container.setMessageListener(listener);
				container.start();
				ConfigurationServlet.logger
				.info("Message Queue Service - Capture Listener " + (i+1) + " started");
			}
		}
	}
}
