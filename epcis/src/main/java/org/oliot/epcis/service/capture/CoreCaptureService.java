package org.oliot.epcis.service.capture;


import java.net.UnknownHostException;

import org.oliot.epcis.AggregationEventType;
import org.oliot.epcis.ObjectEventType;
import org.oliot.epcis.QuantityEventType;
import org.oliot.epcis.TransactionEventType;
import org.oliot.epcis.TransformationEventType;

import com.mongodb.MongoException;

public interface CoreCaptureService {
	public void capture(AggregationEventType event);
	public void capture(ObjectEventType event) throws UnknownHostException, MongoException;
	public void capture(QuantityEventType event);
	public void capture(TransactionEventType event);
	public void capture(TransformationEventType event);	
}
