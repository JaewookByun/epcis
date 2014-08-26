package org.oliot.epcis.service.capture;


import org.oliot.epcis.AggregationEventType;
import org.oliot.epcis.EPCISEventType;
import org.oliot.epcis.ObjectEventType;
import org.oliot.epcis.QuantityEventType;
import org.oliot.epcis.TransactionEventType;
import org.oliot.epcis.TransformationEventType;

public interface CoreCaptureService {
	public void capture(EPCISEventType event);
	public void capture(AggregationEventType event);
	public void capture(ObjectEventType event);
	public void capture(QuantityEventType event);
	public void capture(TransactionEventType event);
	public void capture(TransformationEventType event);	
}
