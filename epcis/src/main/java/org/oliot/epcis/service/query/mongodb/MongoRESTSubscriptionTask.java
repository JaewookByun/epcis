package org.oliot.epcis.service.query.mongodb;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

public class MongoRESTSubscriptionTask implements Job  {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		JobDataMap map = context.getJobDetail().getJobDataMap();
		String target = map.getString("target");
		String targetType = map.getString("targetType");
		String destURL = map.getString("destURL");
		//String subscriptionID = map.getString("subscriptionID");
		String cronExpression = map.getString("cronExpression");
		
		MongoRESTQueryService mrqs = new MongoRESTQueryService();
		String result = mrqs.getEPCResourceOne(target, targetType);
		
		ConnectionFactory connectionFactory = new CachingConnectionFactory();
		AmqpAdmin admin = new RabbitAdmin(connectionFactory);
		AmqpTemplate template = new RabbitTemplate(connectionFactory);
		
		FanoutExchange fex = new FanoutExchange(targetType+","+target+","+cronExpression);
		admin.declareExchange(fex);
		
		template.convertAndSend(fex.getName(), null, result);
		
		Queue queue = new Queue(fex.getName()+","+destURL);
		admin.declareQueue(queue);
		
		Binding binding = BindingBuilder.bind(queue).to(fex);
		admin.declareBinding(binding);
		
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queue.getName());
		MongoSubscriptionListener listener = new MongoSubscriptionListener(destURL);		
		container.setMessageListener(new MessageListenerAdapter(listener));
		container.start();				
	}
}
