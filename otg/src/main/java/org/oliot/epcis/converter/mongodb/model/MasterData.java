package org.oliot.epcis.converter.mongodb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;

/**
 * Copyright (C) 2014-17 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */
public class MasterData {

	private VocabularyType type;
	private String id;
	private Map<String, String> attributes;
	private List<String> children;

	public MasterData(VocabularyType type, String id) {
		this.type = type;
		this.id = id;
		attributes = new HashMap<String, String>();
		children = new ArrayList<String>();
	}

	public VocabularyType getType() {
		return type;
	}

	public void setType(VocabularyType type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public List<String> getChildren() {
		return children;
	}

	public void setChildren(List<String> children) {
		this.children = children;
	}

	public BsonDocument asBsonDocument() {
		CaptureUtil util = new CaptureUtil();

		BsonDocument masterData = new BsonDocument();
		masterData = util.putType(masterData, type);
		masterData = util.putID(masterData, id);
		
		if (this.attributes != null && this.attributes.isEmpty() == false) {
			masterData = util.putAttributes(masterData, attributes);
		}
		if (this.children != null && this.children.isEmpty() == false) {
			masterData = util.putChildren(masterData, children);
		}
		return masterData;
	}
}
