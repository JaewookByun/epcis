package org.oliot.epcis.service.capture;


import org.oliot.epcis.model.AggregationEventType;
import org.oliot.epcis.model.ObjectEventType;
import org.oliot.epcis.model.QuantityEventType;
import org.oliot.epcis.model.TransactionEventType;
import org.oliot.epcis.model.TransformationEventType;


public interface CoreCaptureService {
	public void capture(AggregationEventType event);
	public void capture(ObjectEventType event);
	public void capture(QuantityEventType event);
	public void capture(TransactionEventType event);
	public void capture(TransformationEventType event);
}
