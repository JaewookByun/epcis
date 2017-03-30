package org.oliot.epcis_client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;

public abstract class EPCISEvent {
    private long eventTime;
    private long recordTime;
    private String eventTimeZoneOffset;

    private String bizStep;
    private String disposition;
    private String readPoint;
    private String bizLocation;

    private Map<String, List<String>> sourceList;
    private Map<String, List<String>> destinationList;
    private Map<String, String> namespaces;

    private Map<String, Map<String, Object>> extensions;

    public EPCISEvent() {
        this(System.currentTimeMillis(), new SimpleDateFormat("XXX").format(new Date()));
    }

    public EPCISEvent(long eventTime, String eventTimeZoneOffset) {
        this.eventTime = eventTime;
        this.eventTimeZoneOffset = eventTimeZoneOffset;
        recordTime = 0;
        sourceList = new HashMap<String, List<String>>();
        destinationList = new HashMap<String, List<String>>();
        namespaces = new HashMap<String, String>();
        extensions = new HashMap<String, Map<String, Object>>();
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public String getEventTimeZoneOffset() {
        return eventTimeZoneOffset;
    }

    public void setEventTimeZoneOffset() {
        SimpleDateFormat format = new SimpleDateFormat("XXX");
        eventTimeZoneOffset = format.format(new Date());
    }

    public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
        this.eventTimeZoneOffset = eventTimeZoneOffset;
    }

    public String getBizStep() {
        return bizStep;
    }

    public void setBizStep(String bizStep) {
        this.bizStep = bizStep;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getReadPoint() {
        return readPoint;
    }

    public void setReadPoint(String readPoint) {
        this.readPoint = readPoint;
    }

    public String getBizLocation() {
        return bizLocation;
    }

    public void setBizLocation(String bizLocation) {
        this.bizLocation = bizLocation;
    }

    public Map<String, List<String>> getSourceList() {
        return sourceList;
    }

    public void setSourceList(Map<String, List<String>> sourceList) {
        this.sourceList = sourceList;
    }

    public Map<String, List<String>> getDestinationList() {
        return destinationList;
    }

    public void setDestinationList(Map<String, List<String>> destinationList) {
        this.destinationList = destinationList;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public Map<String, Map<String, Object>> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Map<String, Object>> extensions) {
        this.extensions = extensions;
    }


    public BsonDocument asBsonDocument() {
        CaptureUtil util = new CaptureUtil();

        BsonDocument baseEvent = new BsonDocument();
        // Required Fields
        baseEvent = util.putEventTime(baseEvent, eventTime);
        baseEvent = util.putEventTimeZoneOffset(baseEvent, eventTimeZoneOffset);

        // Optional Fields
        if (this.recordTime != 0) {
            baseEvent = util.putRecordTime(baseEvent, recordTime);
        }
        if (this.bizStep != null) {
            baseEvent = util.putBizStep(baseEvent, bizStep);
        }
        if (this.disposition != null) {
            baseEvent = util.putDisposition(baseEvent, disposition);
        }
        if (this.readPoint != null) {
            baseEvent = util.putReadPoint(baseEvent, readPoint);
        }
        if (this.bizLocation != null) {
            baseEvent = util.putBizLocation(baseEvent, bizLocation);
        }
        if (this.extensions != null && this.extensions.isEmpty() == false) {
            baseEvent = util.putExtensions(baseEvent, namespaces, extensions);
        }

        return baseEvent;
    }

    BsonDocument asExtensionBsonDocument(CaptureUtil util) {
        BsonDocument extension = new BsonDocument();
        if (this.sourceList != null && this.sourceList.isEmpty() == false) {
            extension = util.putSourceList(extension, sourceList);
        }
        if (this.destinationList != null && this.destinationList.isEmpty() == false) {
            extension = util.putDestinationList(extension, destinationList);
        }
        return extension;
    }

}
