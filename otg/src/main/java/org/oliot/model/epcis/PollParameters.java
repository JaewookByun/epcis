package org.oliot.model.epcis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PollParameters {

	private String queryName;
	private String eventType;
	private String GE_eventTime;
	private String LT_eventTime;
	private String GE_recordTime;
	private String LT_recordTime;
	private String EQ_action;
	private String EQ_bizStep;
	private String EQ_disposition;
	private String EQ_readPoint;
	private String WD_readPoint;
	private String EQ_bizLocation;
	private String WD_bizLocation;
	private String EQ_transformationID;
	private String MATCH_epc;
	private String MATCH_parentID;
	private String MATCH_inputEPC;
	private String MATCH_outputEPC;
	private String MATCH_anyEPC;
	private String MATCH_epcClass;
	private String MATCH_inputEPCClass;
	private String MATCH_outputEPCClass;
	private String MATCH_anyEPCClass;
	private Integer EQ_quantity;
	private Integer GT_quantity;
	private Integer GE_quantity;
	private Integer LT_quantity;
	private Integer LE_quantity;
	private String EQ_eventID;
	private Boolean EXISTS_errorDeclaration;
	private String GE_errorDeclarationTime;
	private String LT_errorDeclarationTime;
	private String EQ_errorReason;
	private String EQ_correctiveEventID;
	private String orderBy;
	private String orderDirection;
	private Integer eventCountLimit;
	private Integer maxEventCount;

	private String vocabularyName;
	private Boolean includeAttributes;
	private Boolean includeChildren;
	private String attributeNames;
	private String EQ_name;
	private String WD_name;
	private String HASATTR;
	private Integer maxElementCount;
	private String format;
	private Map<String, String> params;

	public static BsonDocument asBsonDocument(PollParameters p) {
		BsonDocument bson = new BsonDocument();
		if (p.getQueryName() != null) {
			bson.put("queryName", new BsonString(p.getQueryName()));
		}
		if (p.getEventType() != null) {
			bson.put("eventType", new BsonString(p.getEventType()));
		}
		if (p.getGE_eventTime() != null) {
			bson.put("GE_eventTime", new BsonString(p.getGE_eventTime()));
		}
		if (p.getLT_eventTime() != null) {
			bson.put("LT_eventTime", new BsonString(p.getLT_eventTime()));
		}
		if (p.getGE_recordTime() != null) {
			bson.put("GE_recordTime", new BsonString(p.getGE_recordTime()));
		}
		if (p.getLT_recordTime() != null) {
			bson.put("LT_recordTime", new BsonString(p.getLT_recordTime()));
		}
		if (p.getEQ_action() != null) {
			bson.put("EQ_action", new BsonString(p.getEQ_action()));
		}
		if (p.getEQ_bizStep() != null) {
			bson.put("EQ_bizStep", new BsonString(p.getEQ_bizStep()));
		}
		if (p.getEQ_disposition() != null) {
			bson.put("EQ_disposition", new BsonString(p.getEQ_disposition()));
		}
		if (p.getEQ_readPoint() != null) {
			bson.put("EQ_readPoint", new BsonString(p.getEQ_readPoint()));
		}
		if (p.getWD_readPoint() != null) {
			bson.put("WD_readPoint", new BsonString(p.getWD_readPoint()));
		}
		if (p.getEQ_bizLocation() != null) {
			bson.put("EQ_bizLocation", new BsonString(p.getEQ_bizLocation()));
		}
		if (p.getWD_bizLocation() != null) {
			bson.put("WD_bizLocation", new BsonString(p.getWD_bizLocation()));
		}
		if (p.getEQ_transformationID() != null) {
			bson.put("EQ_transformationID", new BsonString(p.getEQ_transformationID()));
		}
		if (p.getMATCH_epc() != null) {
			bson.put("MATCH_epc", new BsonString(p.getMATCH_epc()));
		}
		if (p.getMATCH_parentID() != null) {
			bson.put("MATCH_parentID", new BsonString(p.getMATCH_parentID()));
		}
		if (p.getMATCH_inputEPC() != null) {
			bson.put("MATCH_inputEPC", new BsonString(p.getMATCH_inputEPC()));
		}
		if (p.getMATCH_outputEPC() != null) {
			bson.put("MATCH_outputEPC", new BsonString(p.getMATCH_outputEPC()));
		}
		if (p.getMATCH_anyEPC() != null) {
			bson.put("MATCH_anyEPC", new BsonString(p.getMATCH_anyEPC()));
		}
		if (p.getMATCH_epcClass() != null) {
			bson.put("MATCH_epcClass", new BsonString(p.getMATCH_epcClass()));
		}
		if (p.getMATCH_inputEPCClass() != null) {
			bson.put("MATCH_inputEPCClass", new BsonString(p.getMATCH_inputEPCClass()));
		}
		if (p.getMATCH_outputEPCClass() != null) {
			bson.put("MATCH_outputEPCClass", new BsonString(p.getMATCH_outputEPCClass()));
		}
		if (p.getMATCH_anyEPCClass() != null) {
			bson.put("MATCH_anyEPCClass", new BsonString(p.getMATCH_anyEPCClass()));
		}
		if (p.getEQ_quantity() != null) {
			bson.put("EQ_quantity", new BsonInt32(p.getEQ_quantity()));
		}
		if (p.getGT_quantity() != null) {
			bson.put("GT_quantity", new BsonInt32(p.getGT_quantity()));
		}
		if (p.getGE_quantity() != null) {
			bson.put("GE_quantity", new BsonInt32(p.getGE_quantity()));
		}
		if (p.getLT_quantity() != null) {
			bson.put("LT_quantity", new BsonInt32(p.getLT_quantity()));
		}
		if (p.getLE_quantity() != null) {
			bson.put("LE_quantity", new BsonInt32(p.getLE_quantity()));
		}

		if (p.getEQ_eventID() != null) {
			bson.put("EQ_eventID", new BsonString(p.getEQ_eventID()));
		}
		if (p.getEXISTS_errorDeclaration() != null) {
			bson.put("EXISTS_errorDeclaration", new BsonBoolean(p.getEXISTS_errorDeclaration()));
		}
		if (p.getGE_errorDeclarationTime() != null) {
			bson.put("GE_errorDeclarationTime", new BsonString(p.getGE_errorDeclarationTime()));
		}
		if (p.getLT_errorDeclarationTime() != null) {
			bson.put("LT_errorDeclarationTime", new BsonString(p.getLT_errorDeclarationTime()));
		}
		if (p.getEQ_errorReason() != null) {
			bson.put("EQ_errorReason", new BsonString(p.getEQ_errorReason()));
		}
		if (p.getEQ_correctiveEventID() != null) {
			bson.put("EQ_correctiveEventID", new BsonString(p.getEQ_correctiveEventID()));
		}
		if (p.getOrderBy() != null) {
			bson.put("orderBy", new BsonString(p.getOrderBy()));
		}
		if (p.getOrderDirection() != null) {
			bson.put("orderDirection", new BsonString(p.getOrderDirection()));
		}
		if (p.getEventCountLimit() != null) {
			bson.put("eventCountLimit", new BsonInt32(p.getEventCountLimit()));
		}
		if (p.getMaxEventCount() != null) {
			bson.put("maxEventCount", new BsonInt32(p.getMaxEventCount()));
		}
		if (p.getVocabularyName() != null) {
			bson.put("vocabularyName", new BsonString(p.getVocabularyName()));
		}
		if (p.getIncludeAttributes() != null) {
			bson.put("includeAttributes", new BsonBoolean(p.getIncludeAttributes()));
		}
		if (p.getIncludeChildren() != null) {
			bson.put("includeChildren", new BsonBoolean(p.getIncludeChildren()));
		}
		if (p.getAttributeNames() != null) {
			bson.put("attributeNames", new BsonString(p.getAttributeNames()));
		}
		if (p.getEQ_name() != null) {
			bson.put("EQ_name", new BsonString(p.getEQ_name()));
		}
		if (p.getWD_name() != null) {
			bson.put("WD_name", new BsonString(p.getWD_name()));
		}
		if (p.getHASATTR() != null) {
			bson.put("HASATTR", new BsonString(p.getHASATTR()));
		}
		if (p.getMaxElementCount() != null) {
			bson.put("maxElementCount", new BsonInt32(p.getMaxElementCount()));
		}
		if (p.getFormat() != null) {
			bson.put("format", new BsonString(p.getFormat()));
		}
		if (p.getParams() != null && p.getParams().isEmpty() == false) {
			BsonDocument paramMap = new BsonDocument();
			for (String key : p.getParams().keySet()) {
				if( key.equals("description") || key.equals("userID") || key.equals("accessToken"))
					continue;
				String value = p.getParams().get(key).toString();
				paramMap.put(key, new BsonString(value));
			}
			bson.put("paramMap", paramMap);
		}
		return bson;
	}

	public PollParameters(BsonDocument doc) {
		if (doc.containsKey("queryName"))
			this.queryName = doc.getString("queryName").getValue();
		if (doc.containsKey("eventType"))
			this.eventType = doc.getString("eventType").getValue();
		if (doc.containsKey("GE_eventTime"))
			this.GE_eventTime = doc.getString("GE_eventTime").getValue();
		if (doc.containsKey("LT_eventTime"))
			this.LT_eventTime = doc.getString("LT_eventTime").getValue();
		if (doc.containsKey("GE_recordTime"))
			this.GE_recordTime = doc.getString("GE_recordTime").getValue();
		if (doc.containsKey("LT_recordTime"))
			this.LT_recordTime = doc.getString("LT_recordTime").getValue();
		if (doc.containsKey("EQ_action"))
			this.EQ_action = doc.getString("EQ_action").getValue();
		if (doc.containsKey("EQ_bizStep"))
			this.EQ_bizStep = doc.getString("EQ_bizStep").getValue();
		if (doc.containsKey("EQ_disposition"))
			this.EQ_disposition = doc.getString("EQ_disposition").getValue();
		if (doc.containsKey("EQ_readPoint")) {
			this.EQ_readPoint = doc.getString("EQ_readPoint").getValue();
		}
		if (doc.containsKey("WD_readPoint")) {
			this.WD_readPoint = doc.getString("WD_readPoint").getValue();
		}
		if (doc.containsKey("EQ_bizLocation")) {
			this.EQ_bizLocation = doc.getString("EQ_bizLocation").getValue();
		}
		if (doc.containsKey("WD_bizLocation"))
			this.WD_bizLocation = doc.getString("WD_bizLocation").getValue();
		if (doc.containsKey("EQ_transformationID"))
			this.EQ_transformationID = doc.getString("EQ_transformationID").getValue();
		if (doc.containsKey("MATCH_epc"))
			this.MATCH_epc = doc.getString("MATCH_epc").getValue();
		if (doc.containsKey("MATCH_parentID"))
			this.MATCH_parentID = doc.getString("MATCH_parentID").getValue();
		if (doc.containsKey("MATCH_inputEPC"))
			this.MATCH_inputEPC = doc.getString("MATCH_inputEPC").getValue();
		if (doc.containsKey("MATCH_outputEPC"))
			this.MATCH_outputEPC = doc.getString("MATCH_outputEPC").getValue();
		if (doc.containsKey("MATCH_anyEPC"))
			this.MATCH_anyEPC = doc.getString("MATCH_anyEPC").getValue();
		if (doc.containsKey("MATCH_epcClass"))
			this.MATCH_epcClass = doc.getString("MATCH_epcClass").getValue();

		if (doc.containsKey("MATCH_inputEPCClass")) {
			this.MATCH_inputEPCClass = doc.getString("MATCH_inputEPCClass").getValue();
		}
		if (doc.containsKey("MATCH_outputEPCClass")) {
			this.MATCH_outputEPCClass = doc.getString("MATCH_outputEPCClass").getValue();
		}
		if (doc.containsKey("MATCH_anyEPCClass"))
			this.MATCH_anyEPCClass = doc.getString("MATCH_anyEPCClass").getValue();
		if (doc.containsKey("EQ_quantity"))
			this.EQ_quantity = doc.getInt32("EQ_quantity").getValue();
		if (doc.containsKey("GT_quantity"))
			this.GT_quantity = doc.getInt32("GT_quantity").getValue();
		if (doc.containsKey("GE_quantity"))
			this.GE_quantity = doc.getInt32("GE_quantity").getValue();
		if (doc.containsKey("LT_quantity"))
			this.LT_quantity = doc.getInt32("LT_quantity").getValue();
		if (doc.containsKey("LE_quantity"))
			this.LE_quantity = doc.getInt32("LE_quantity").getValue();
		if (doc.containsKey("EQ_eventID")) {
			this.EQ_eventID = doc.getString("EQ_eventID").getValue();
		}
		if (doc.containsKey("EXISTS_errorDeclaration")) {
			this.EXISTS_errorDeclaration = doc.getBoolean("EXISTS_errorDeclaration").getValue();
		}
		if (doc.containsKey("GE_errorDeclarationTime")) {
			this.GE_errorDeclarationTime = doc.getString("GE_errorDeclarationTime").getValue();
		}
		if (doc.containsKey("LT_errorDeclarationTime")) {
			this.LT_errorDeclarationTime = doc.getString("LT_errorDeclarationTime").getValue();
		}
		if (doc.containsKey("orderBy"))
			this.orderBy = doc.getString("orderBy").getValue();
		if (doc.containsKey("orderDirection"))
			this.orderDirection = doc.getString("orderDirection").getValue();
		if (doc.containsKey("eventCountLimit"))
			this.eventCountLimit = doc.getInt32("eventCountLimit").getValue();
		if (doc.containsKey("maxEventCount"))
			this.maxEventCount = doc.getInt32("maxEventCount").getValue();

		if (doc.containsKey("vocabularyName")) {
			this.vocabularyName = doc.getString("vocabularyName").getValue();
		}
		if (doc.containsKey("includeAttributes")) {
			this.includeAttributes = doc.getBoolean("includeAttributes").getValue();
		}
		if (doc.containsKey("includeChildren")) {
			this.includeChildren = doc.getBoolean("includeChildren").getValue();
		}
		if (doc.containsKey("attributeNames")) {
			this.attributeNames = doc.getString("attributeNames").getValue();
		}
		if (doc.containsKey("EQ_name")) {
			this.EQ_name = doc.getString("EQ_name").getValue();
		}
		if (doc.containsKey("WD_name")) {
			this.WD_name = doc.getString("WD_name").getValue();
		}
		if (doc.containsKey("HASATTR")) {
			this.HASATTR = doc.getString("HASATTR").getValue();
		}
		if (doc.containsKey("maxElementCount")) {
			this.maxElementCount = doc.getInt32("maxElementCount").getValue();
		}
		if (doc.containsKey("format"))
			this.format = doc.getString("format").getValue();
		if (doc.containsKey("paramMap")) {
			Map<String, String> paramMap = new HashMap<String, String>();
			BsonDocument bsonParam = doc.getDocument("paramMap");
			for (String key : bsonParam.keySet()) {
				paramMap.put(key, bsonParam.getString(key).getValue());
			}
			if (bsonParam.isEmpty() == false)
				this.params = paramMap;
		}
	}

	private String getString(Element element) {
		Node node = element.getFirstChild();
		if (node.getNodeName().equals("string")) {
			return node.getTextContent();
		}
		return null;
	}

	private List<String> getListOfString(Element element) {
		List<String> los = new ArrayList<String>();

		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("string")) {
				los.add(node.getTextContent());
			}
		}
		return los;
	}

	private Integer getInteger(Element element) {
		Node node = element.getFirstChild();
		if (node.getNodeName().equals("int")) {
			try {
				return Integer.parseInt(node.getTextContent());
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	private String getDateTime(Element element) {
		Node node = element.getFirstChild();
		if (node.getNodeName().equals("dateTime")) {
			return node.getTextContent() + "^dateTime";
		}
		return null;
	}

	private Boolean getBoolean(Element element) {
		Node node = element.getFirstChild();
		if (node.getNodeName().equals("boolean")) {
			try {
				return Boolean.parseBoolean(node.getTextContent());
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	public PollParameters(String queryName, QueryParams queryParams) {
		this.queryName = queryName;
		List<QueryParam> queryParamList = queryParams.getParam();
		// SimpleDateFormat sdf = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		this.params = new HashMap<String, String>();
		for (QueryParam qp : queryParamList) {
			String name = qp.getName();

			// Note: We refer Fosstrak how they represent parameter values
			// Supported Type: int, long, float, double, boolean, dateTime
			if (name.equals("eventType")) {
				// List of String
				// <value>
				// <string>ObjectEvent</string>
				// <string>AggregationEvent</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				eventType = generateCSV(valueList);
				continue;
			} else if (name.equals("GE_eventTime")) {
				// Time
				// <value>
				// <dateTime>yyyy-MM-dd'T'HH:mm:ss.SSSXXX<dateTime>
				// </value>
				GE_eventTime = getDateTime((Element) qp.getValue());
				continue;
			} else if (name.equals("LT_eventTime")) {
				// Time
				// <value>
				// <dateTime>yyyy-MM-dd'T'HH:mm:ss.SSSXXX<dateTime>
				// </value>
				LT_eventTime = getDateTime((Element) qp.getValue());
				continue;
			} else if (name.equals("GE_recordTime")) {
				// Time
				// <value>
				// <dateTime>yyyy-MM-dd'T'HH:mm:ss.SSSXXX<dateTime>
				// </value>
				GE_recordTime = getDateTime((Element) qp.getValue());
				continue;
			} else if (name.equals("LT_recordTime")) {
				// Time
				// <value>
				// <dateTime>yyyy-MM-dd'T'HH:mm:ss.SSSXXX<dateTime>
				// </value>
				LT_recordTime = getDateTime((Element) qp.getValue());
				continue;
			} else if (name.equals("EQ_action")) {
				// List of String
				// <value>
				// <string>ADD</string>
				// <string>OBSERVE</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_action = generateCSV(valueList);
				continue;
			} else if (name.equals("EQ_bizStep")) {
				// List of String
				// <value>
				// <string>urn:epcglobal:cbv:bizstep:receiving</string>
				// <string>urn:epcglobal:cbv:bizstep:accepting</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_bizStep = generateCSV(valueList);
				continue;
			} else if (name.equals("EQ_disposition")) {
				// List of String
				// <value>
				// <string>urn:epcglobal:cbv:bizstep:receiving</string>
				// <string>urn:epcglobal:cbv:bizstep:receiving2</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_disposition = generateCSV(valueList);
				continue;
			} else if (name.equals("EQ_readPoint")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sgln:0614141.00777.0</string>
				// <string>urn:epc:id:sgln:0614141.00777.1</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_readPoint = generateCSV(valueList);
				continue;
			} else if (name.equals("WD_readPoint")) {
				List<String> valueList = getListOfString((Element) qp.getValue());
				WD_readPoint = generateCSV(valueList);
				continue;
			} else if (name.equals("EQ_bizLocation")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sgln:0614141.00888.0</string>
				// <string>urn:epc:id:sgln:0614141.00888.1</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_bizLocation = generateCSV(valueList);
				continue;
			} else if (name.equals("WD_bizLocation")) {
				List<String> valueList = getListOfString((Element) qp.getValue());
				WD_bizLocation = generateCSV(valueList);
				continue;
			} else if (name.equals("EQ_transformationID")) {
				// List of String
				// <value>
				// <string>TransformationID</string>
				// <string>TransformationID2</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_transformationID = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_epc")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sgtin:0614141.107346.2018</string>
				// <string>urn:epc:id:sgtin:0614141.107346.2019</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_epc = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_parentID")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sscc:0614141.1234567890</string>
				// <string>urn:epc:id:sscc:0614141.1234567891</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_parentID = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_inputEPC")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sgtin:4012345.011122.25</string>
				// <string>urn:epc:id:sgtin:4000001.065432.99886655</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_inputEPC = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_outputEPC")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sgtin:4012345.077889.25</string>
				// <string>urn:epc:id:sgtin:4012345.077889.26</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_outputEPC = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_anyEPC")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sscc:0614141.1234567890</string>
				// <string>urn:epc:id:sgtin:4012345.011122.25</string>
				// <string>urn:epc:id:sgtin:4012345.077889.26</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_anyEPC = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_epcClass")) {
				// List of String
				// <value>
				// <string>urn:epc:class:lgtin:4012345.012345.998877</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_epcClass = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_inputEPCClass")) {
				// List of String
				// <value>
				// <string>urn:epc:class:lgtin:4012345.011111.4444</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_inputEPCClass = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_outputEPCClass")) {
				// List of String
				// <value>
				// <string>urn:epc:class:lgtin:4012345.011111.4444</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_outputEPCClass = generateCSV(valueList);
				continue;
			} else if (name.equals("MATCH_anyEPCClass")) {
				// List of String
				// <value>
				// <string>urn:epc:class:lgtin:4012345.012345.998877</string>
				// <string>urn:epc:class:lgtin:4012345.011111.4444</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				MATCH_anyEPCClass = generateCSV(valueList);
				continue;
			} else if (name.equals("EQ_quantity")) {
				// Deprecated
				EQ_quantity = getInteger((Element) qp.getValue());
				continue;
			} else if (name.equals("GT_quantity")) {
				// Deprecated
				GT_quantity = getInteger((Element) qp.getValue());
				continue;
			} else if (name.equals("GE_quantity")) {
				// Deprecated
				GE_quantity = getInteger((Element) qp.getValue());
				continue;
			} else if (name.equals("LT_quantity")) {
				// Deprecated
				LT_quantity = getInteger((Element) qp.getValue());
				continue;
			} else if (name.equals("LE_quantity")) {
				// Deprecated
				LE_quantity = getInteger((Element) qp.getValue());
				continue;
			} else if (name.equals("EQ_eventID")) {
				// List of String
				// <value>
				// <string>5785b1c7deab32499fe6a299</string>
				// <string>5785b1c7deab32499fe6a200</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_eventID = generateCSV(valueList);
				continue;
			} else if (name.equals("EXISTS_errorDeclaration")) {
				// Void
				EXISTS_errorDeclaration = true;
				continue;
			} else if (name.equals("GE_errorDeclarationTime")) {
				// Time
				// <value>
				// <time>yyyy-MM-dd'T'HH:mm:ss.SSSXXX<time>
				// </value>
				GE_errorDeclarationTime = getDateTime((Element) qp.getValue());
				continue;
			} else if (name.equals("LT_errorDeclarationTime")) {
				// Time
				// <value>
				// <time>yyyy-MM-dd'T'HH:mm:ss.SSSXXX<time>
				// </value>
				LT_errorDeclarationTime = getDateTime((Element) qp.getValue());
				continue;
			} else if (name.equals("EQ_errorReason")) {
				// List of String
				// <value>
				// <string>urn:epcglobal:cbv:error:add</string>
				// <string>urn:epcglobal:cbv:error:remove</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_errorReason = generateCSV(valueList);
				continue;
			} else if (name.equals("EQ_correctiveEventID")) {
				// List of String
				// <value>
				// <string>5722d7e1deab322596705146</string>
				// <string>5722d7e1deab322596705147</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_correctiveEventID = generateCSV(valueList);
				continue;
			} else if (name.equals("orderBy")) {
				// String
				// <value>
				// <string>eventTime / recordTime / example0:a</string>
				// </value>
				orderBy = getString((Element) qp.getValue());
				continue;
			} else if (name.equals("orderDirection")) {
				// String
				// <value>
				// <string>ASC DESC</string>
				// </value>
				orderDirection = getString((Element) qp.getValue());
				continue;
			} else if (name.equals("eventCountLimit")) {
				// Integer
				eventCountLimit = getInteger((Element) qp.getValue());
				continue;
			} else if (name.equals("maxEventCount")) {
				// Integer
				maxEventCount = getInteger((Element) qp.getValue());
				continue;
			} else if (name.equals("vocabularyName")) {
				// List of String
				// <value>
				// <string>urn:epcglobal:epcis:vtype:BusinessLocation</string>
				// <string>urn:epcglobal:epcis:vtype:ReadPoint</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				vocabularyName = generateCSV(valueList);
				continue;
			} else if (name.equals("includeAttributes")) {
				// boolean
				includeAttributes = getBoolean((Element) qp.getValue());
				continue;
			} else if (name.equals("includeChildren")) {
				// boolean
				includeChildren = getBoolean((Element) qp.getValue());
				continue;
			} else if (name.equals("attributeNames")) {
				// List of String
				// <value>
				// <string>http://epcis.example.com/mda/latitude</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				attributeNames = generateCSV(valueList);
				continue;
			} else if (name.equals("EQ_name")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sgln:0037000.00729.0</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				EQ_name = generateCSV(valueList);
				continue;
			} else if (name.equals("WD_name")) {
				// List of String
				// <value>
				// <string>urn:epc:id:sgln:0037000.00729.0</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				WD_name = generateCSV(valueList);
				continue;
			} else if (name.equals("HASATTR")) {
				// List of String
				// <value>
				// <string>http://epcis.example.com/mda/latitude</string>
				// </value>
				List<String> valueList = getListOfString((Element) qp.getValue());
				HASATTR = generateCSV(valueList);
				continue;
			} else if (name.equals("maxElementCount")) {
				// Integer
				maxElementCount = getInteger((Element) qp.getValue());
				continue;
			} else {
				// EQ_bizTransaction_type : lString
				// EQ_source_type : lString
				// EQ_destination_type : lString
				// EQ_fieldname : lString
				// EQ_fieldname : int / float / time
				// GT|GE|LT|LE_fieldname : int / float / time
				// EQ_ILMD_fieldname : lString
				// EQ|GT|GE|LT|LE_ILMD_fieldname : int / float / time

				// EQ_INNER_fieldname : lString
				// EQ|GT|GE|LT|LE_INNER_fieldname : lString
				// EQ_INNER_ILMD_fieldname : lString
				// EQ|GT|GE|LT|LE_INNER_ILMD_fieldname : int / float / time

				// EXISTS_fieldname : Void
				// EXISTS_ILMD_fieldname : Void
				// EXISTS_ILMD_fieldname : Void

				// HASATTR_fieldname : lString
				// EQATTR_fieldname_attrname : lString

				// EQ_ERROR_DECLARATION_fieldname : lString
				// EQ|GT|GE|LT|GE_ERROR_DECLARATION_fieldname : int / float /
				// time
				// EQ_INNER_ERROR_DECLARATION_fieldname : lString
				// EQ|GT|GE|LT|GE_ERROR_DECLARATION_fieldname : int / float /
				// time

				// Supported Type: int, long, float, double, boolean, time,
				// string

				if ((qp.getValue() instanceof Element)) {
					Element element = (Element) qp.getValue();
					NodeList nodeList = element.getChildNodes();
					List<String> valueList = new ArrayList<String>();
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node node = nodeList.item(i);
						String type = node.getNodeName();
						String value = node.getTextContent();
						if (type.equals("#text"))
							continue;
						if (type.equals("int")) {
							valueList.add(value + "^int");
						} else if (type.equals("long")) {
							valueList.add(value + "^long");
						} else if (type.equals("float")) {
							valueList.add(value + "^float");
						} else if (type.equals("double")) {
							valueList.add(value + "^double");
						} else if (type.equals("boolean")) {
							valueList.add(value + "^boolean");
						} else if (type.equals("dateTime")) {
							valueList.add(value + "^dateTime");
						} else if (type.equals("string")) {
							valueList.add(value);
						} else if (type.equals("void")) {
							valueList.add("true^boolean");
						}
					}
					params.put(name, generateCSV(valueList));
				} else if (qp.getValue() == null && qp.getName().contains("EXISTS")) {
					params.put(name, "true");
				}
			}
			format = "XML";
		}
	}

	public PollParameters(String queryName, String eventType, String GE_eventTime, String LT_eventTime,
			String GE_recordTime, String LT_recordTime, String EQ_action, String EQ_bizStep, String EQ_disposition,
			String EQ_readPoint, String WD_readPoint, String EQ_bizLocation, String WD_bizLocation,
			String EQ_transformationID, String MATCH_epc, String MATCH_parentID, String MATCH_inputEPC,
			String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass, String MATCH_inputEPCClass,
			String MATCH_outputEPCClass, String MATCH_anyEPCClass, Integer EQ_quantity, Integer GT_quantity,
			Integer GE_quantity, Integer LT_quantity, Integer LE_quantity, String EQ_eventID,
			Boolean EXISTS_errorDeclaration, String GE_errorDeclarationTime, String LT_errorDeclarationTime,
			String EQ_errorReason, String EQ_correctiveEventID, String orderBy, String orderDirection,
			Integer eventCountLimit, Integer maxEventCount, String vocabularyName, Boolean includeAttributes,
			Boolean includeChildren, String attributeNames, String EQ_name, String WD_name, String HASATTR,
			Integer maxElementCount, String format, Map<String, String> params) {
		this.queryName = queryName;
		this.eventType = eventType;
		this.GE_eventTime = GE_eventTime;
		this.LT_eventTime = LT_eventTime;
		this.GE_recordTime = GE_recordTime;
		this.LT_recordTime = LT_recordTime;
		this.EQ_action = EQ_action;
		this.EQ_bizStep = EQ_bizStep;
		this.EQ_disposition = EQ_disposition;
		this.EQ_readPoint = EQ_readPoint;
		this.WD_readPoint = WD_readPoint;
		this.EQ_bizLocation = EQ_bizLocation;
		this.WD_bizLocation = WD_bizLocation;
		this.EQ_transformationID = EQ_transformationID;
		this.MATCH_epc = MATCH_epc;
		this.MATCH_parentID = MATCH_parentID;
		this.MATCH_inputEPC = MATCH_inputEPC;
		this.MATCH_outputEPC = MATCH_outputEPC;
		this.MATCH_anyEPC = MATCH_anyEPC;
		this.MATCH_epcClass = MATCH_epcClass;
		this.MATCH_inputEPCClass = MATCH_inputEPCClass;
		this.MATCH_outputEPCClass = MATCH_outputEPCClass;
		this.MATCH_anyEPCClass = MATCH_anyEPCClass;
		this.EQ_quantity = EQ_quantity;
		this.GT_quantity = GT_quantity;
		this.GE_quantity = GE_quantity;
		this.LT_quantity = LT_quantity;
		this.LE_quantity = LE_quantity;
		this.EQ_eventID = EQ_eventID;
		this.EXISTS_errorDeclaration = EXISTS_errorDeclaration;
		this.GE_errorDeclarationTime = GE_errorDeclarationTime;
		this.LT_errorDeclarationTime = LT_errorDeclarationTime;
		this.EQ_errorReason = EQ_errorReason;
		this.EQ_correctiveEventID = EQ_correctiveEventID;
		this.orderBy = orderBy;
		this.orderDirection = orderDirection;
		this.eventCountLimit = eventCountLimit;
		this.maxEventCount = maxEventCount;
		this.vocabularyName = vocabularyName;
		this.includeAttributes = includeAttributes;
		this.includeChildren = includeChildren;
		this.attributeNames = attributeNames;
		this.EQ_name = EQ_name;
		this.WD_name = WD_name;
		this.HASATTR = HASATTR;
		this.maxElementCount = maxElementCount;
		this.format = format;
		this.params = params;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getGE_eventTime() {
		return GE_eventTime;
	}

	public void setGE_eventTime(String gE_eventTime) {
		GE_eventTime = gE_eventTime;
	}

	public String getLT_eventTime() {
		return LT_eventTime;
	}

	public void setLT_eventTime(String lT_eventTime) {
		LT_eventTime = lT_eventTime;
	}

	public String getGE_recordTime() {
		return GE_recordTime;
	}

	public void setGE_recordTime(String gE_recordTime) {
		GE_recordTime = gE_recordTime;
	}

	public String getLT_recordTime() {
		return LT_recordTime;
	}

	public void setLT_recordTime(String lT_recordTime) {
		LT_recordTime = lT_recordTime;
	}

	public String getEQ_action() {
		return EQ_action;
	}

	public void setEQ_action(String eQ_action) {
		EQ_action = eQ_action;
	}

	public String getEQ_bizStep() {
		return EQ_bizStep;
	}

	public void setEQ_bizStep(String eQ_bizStep) {
		EQ_bizStep = eQ_bizStep;
	}

	public String getEQ_disposition() {
		return EQ_disposition;
	}

	public void setEQ_disposition(String eQ_disposition) {
		EQ_disposition = eQ_disposition;
	}

	public String getEQ_readPoint() {
		return EQ_readPoint;
	}

	public void setEQ_readPoint(String eQ_readPoint) {
		EQ_readPoint = eQ_readPoint;
	}

	public String getWD_readPoint() {
		return WD_readPoint;
	}

	public void setWD_readPoint(String wD_readPoint) {
		WD_readPoint = wD_readPoint;
	}

	public String getEQ_bizLocation() {
		return EQ_bizLocation;
	}

	public void setEQ_bizLocation(String eQ_bizLocation) {
		EQ_bizLocation = eQ_bizLocation;
	}

	public String getWD_bizLocation() {
		return WD_bizLocation;
	}

	public void setWD_bizLocation(String wD_bizLocation) {
		WD_bizLocation = wD_bizLocation;
	}

	public String getEQ_transformationID() {
		return EQ_transformationID;
	}

	public void setEQ_transformationID(String eQ_transformationID) {
		EQ_transformationID = eQ_transformationID;
	}

	public String getMATCH_epc() {
		return MATCH_epc;
	}

	public void setMATCH_epc(String mATCH_epc) {
		MATCH_epc = mATCH_epc;
	}

	public String getMATCH_parentID() {
		return MATCH_parentID;
	}

	public void setMATCH_parentID(String mATCH_parentID) {
		MATCH_parentID = mATCH_parentID;
	}

	public String getMATCH_inputEPC() {
		return MATCH_inputEPC;
	}

	public void setMATCH_inputEPC(String mATCH_inputEPC) {
		MATCH_inputEPC = mATCH_inputEPC;
	}

	public String getMATCH_outputEPC() {
		return MATCH_outputEPC;
	}

	public void setMATCH_outputEPC(String mATCH_outputEPC) {
		MATCH_outputEPC = mATCH_outputEPC;
	}

	public String getMATCH_anyEPC() {
		return MATCH_anyEPC;
	}

	public void setMATCH_anyEPC(String mATCH_anyEPC) {
		MATCH_anyEPC = mATCH_anyEPC;
	}

	public String getMATCH_epcClass() {
		return MATCH_epcClass;
	}

	public void setMATCH_epcClass(String mATCH_epcClass) {
		MATCH_epcClass = mATCH_epcClass;
	}

	public String getMATCH_inputEPCClass() {
		return MATCH_inputEPCClass;
	}

	public void setMATCH_inputEPCClass(String mATCH_inputEPCClass) {
		MATCH_inputEPCClass = mATCH_inputEPCClass;
	}

	public String getMATCH_outputEPCClass() {
		return MATCH_outputEPCClass;
	}

	public void setMATCH_outputEPCClass(String mATCH_outputEPCClass) {
		MATCH_outputEPCClass = mATCH_outputEPCClass;
	}

	public String getMATCH_anyEPCClass() {
		return MATCH_anyEPCClass;
	}

	public void setMATCH_anyEPCClass(String mATCH_anyEPCClass) {
		MATCH_anyEPCClass = mATCH_anyEPCClass;
	}

	public Integer getEQ_quantity() {
		return EQ_quantity;
	}

	public void setEQ_quantity(Integer eQ_quantity) {
		EQ_quantity = eQ_quantity;
	}

	public Integer getGT_quantity() {
		return GT_quantity;
	}

	public void setGT_quantity(Integer gT_quantity) {
		GT_quantity = gT_quantity;
	}

	public Integer getGE_quantity() {
		return GE_quantity;
	}

	public void setGE_quantity(Integer gE_quantity) {
		GE_quantity = gE_quantity;
	}

	public Integer getLT_quantity() {
		return LT_quantity;
	}

	public void setLT_quantity(Integer lT_quantity) {
		LT_quantity = lT_quantity;
	}

	public Integer getLE_quantity() {
		return LE_quantity;
	}

	public void setLE_quantity(Integer lE_quantity) {
		LE_quantity = lE_quantity;
	}

	public String getEQ_eventID() {
		return EQ_eventID;
	}

	public void setEQ_eventID(String eQ_eventID) {
		EQ_eventID = eQ_eventID;
	}

	public Boolean getEXISTS_errorDeclaration() {
		return EXISTS_errorDeclaration;
	}

	public void setEXISTS_errorDeclaration(Boolean eXISTS_errorDeclaration) {
		EXISTS_errorDeclaration = eXISTS_errorDeclaration;
	}

	public String getGE_errorDeclarationTime() {
		return GE_errorDeclarationTime;
	}

	public void setGE_errorDeclarationTime(String gE_errorDeclarationTime) {
		GE_errorDeclarationTime = gE_errorDeclarationTime;
	}

	public String getLT_errorDeclarationTime() {
		return LT_errorDeclarationTime;
	}

	public void setLT_errorDeclarationTime(String lT_errorDeclarationTime) {
		LT_errorDeclarationTime = lT_errorDeclarationTime;
	}

	public String getEQ_errorReason() {
		return EQ_errorReason;
	}

	public void setEQ_errorReason(String eQ_errorReason) {
		EQ_errorReason = eQ_errorReason;
	}

	public String getEQ_correctiveEventID() {
		return EQ_correctiveEventID;
	}

	public void setEQ_correctiveEventID(String eQ_correctiveEventID) {
		EQ_correctiveEventID = eQ_correctiveEventID;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}

	public Integer getEventCountLimit() {
		return eventCountLimit;
	}

	public void setEventCountLimit(Integer eventCountLimit) {
		this.eventCountLimit = eventCountLimit;
	}

	public Integer getMaxEventCount() {
		return maxEventCount;
	}

	public void setMaxEventCount(Integer maxEventCount) {
		this.maxEventCount = maxEventCount;
	}

	public String getVocabularyName() {
		return vocabularyName;
	}

	public void setVocabularyName(String vocabularyName) {
		this.vocabularyName = vocabularyName;
	}

	public Boolean getIncludeAttributes() {
		return includeAttributes;
	}

	public void setIncludeAttributes(Boolean includeAttributes) {
		this.includeAttributes = includeAttributes;
	}

	public Boolean getIncludeChildren() {
		return includeChildren;
	}

	public void setIncludeChildren(Boolean includeChildren) {
		this.includeChildren = includeChildren;
	}

	public String getAttributeNames() {
		return attributeNames;
	}

	public void setAttributeNames(String attributeNames) {
		this.attributeNames = attributeNames;
	}

	public String getEQ_name() {
		return EQ_name;
	}

	public void setEQ_name(String eQ_name) {
		EQ_name = eQ_name;
	}

	public String getWD_name() {
		return WD_name;
	}

	public void setWD_name(String wD_name) {
		WD_name = wD_name;
	}

	public String getHASATTR() {
		return HASATTR;
	}

	public void setHASATTR(String hASATTR) {
		HASATTR = hASATTR;
	}

	public Integer getMaxElementCount() {
		return maxElementCount;
	}

	public void setMaxElementCount(Integer maxElementCount) {
		this.maxElementCount = maxElementCount;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	private String generateCSV(List<String> valueList) {
		String returnValue = null;
		if (valueList.size() != 0)
			returnValue = "";
		for (int i = 0; i < valueList.size(); i++) {
			if (i == valueList.size() - 1) {
				returnValue += valueList.get(i).trim();
			} else {
				returnValue += valueList.get(i).trim() + "|";
			}
		}
		return returnValue;
	}
}
