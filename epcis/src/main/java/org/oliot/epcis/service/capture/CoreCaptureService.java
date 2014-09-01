package org.oliot.epcis.service.capture;


import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;


public interface CoreCaptureService {
	public void capture(AggregationEventType event);
	public void capture(ObjectEventType event);
	public void capture(QuantityEventType event);
	public void capture(TransactionEventType event);
	public void capture(TransformationEventType event);
	public void capture(EPCISDocumentType epcisDocument);
}
