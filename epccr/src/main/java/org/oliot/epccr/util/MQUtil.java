package org.oliot.epccr.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.oliot.epccr.configuration.Configuration;
import org.oliot.epccr.rest.PublishQListener;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

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
public class MQUtil {

	public static List<SimpleMessageListenerContainer> containerList = new ArrayList<SimpleMessageListenerContainer>();

	public static void addSubscriptionToMQ(String epc, String destURL) {
		ConnectionFactory connectionFactory = new CachingConnectionFactory();
		AmqpAdmin admin = new RabbitAdmin(connectionFactory);
		FanoutExchange exchange = new FanoutExchange(epc);
		admin.declareExchange(exchange);
		Queue queue = new Queue(epc + destURL);
		admin.declareQueue(queue);
		Binding binding = BindingBuilder.bind(queue).to(exchange);
		admin.declareBinding(binding);
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(epc + destURL);
		PublishQListener listener = new PublishQListener(destURL);
		container.setMessageListener(listener);
		container.start();
		containerList.add(container);
	}

	public static void delSubscriptionFromMQ(String epc, String destURL) {
		ConnectionFactory connectionFactory = new CachingConnectionFactory();
		AmqpAdmin admin = new RabbitAdmin(connectionFactory);

		// Container
		for (int i = 0; i < containerList.size(); i++) {
			SimpleMessageListenerContainer container = containerList.get(i);
			String[] str = container.getQueueNames();
			for (int j = 0; j < str.length; j++) {
				if (str[j].equals(epc + destURL)) {
					container.stop();
					Configuration.logger.info("Stop: Publish Queue Listener [ "
							+ epc + destURL + " ]");
				}
			}
		}

		// Declare
		FanoutExchange exchange = new FanoutExchange(epc);
		admin.declareExchange(exchange);
		Queue queue = new Queue(epc + destURL);

		// Binding
		Binding binding = BindingBuilder.bind(queue).to(exchange);
		admin.removeBinding(binding);
		Configuration.logger.info("Removed: Binding from [ " + epc + destURL
				+ " ] to [ " + epc + " ]");

		admin.deleteQueue(epc + destURL);
		Configuration.logger.info("Removed: Queue [ " + epc + destURL + " ]");

	}

	public static void addSubscriptionToPublishMQ(String epc,
			JSONObject jsonObject) {
		ConnectionFactory connectionFactory = new CachingConnectionFactory();
		AmqpAdmin admin = new RabbitAdmin(connectionFactory);
		FanoutExchange exchange = new FanoutExchange(epc);
		admin.declareExchange(exchange);
		AmqpTemplate template = new RabbitTemplate(connectionFactory);
		template.convertAndSend(epc, null, jsonObject.toString());
	}
}
