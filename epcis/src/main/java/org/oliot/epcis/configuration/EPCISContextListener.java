package org.oliot.epcis.configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.oliot.epcis.service.capture.CaptureMQServlet;
import org.oliot.epcis.service.query.mongodb.MongoSubscription;
import org.quartz.SchedulerException;

public class EPCISContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			if(MongoSubscription.sched.isStarted())
			{
				MongoSubscription.sched.shutdown();
			}
			
			for(int i = 0 ; i < CaptureMQServlet.MQContainerList.size() ; i ++ )
			{
				CaptureMQServlet.MQContainerList.get(i).destroy();
			}
			
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

	}

}
