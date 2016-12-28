package org.oliot.model.oliot;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class CorrectiveEventID {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	//@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="correctiveeventid_seq")
	//@SequenceGenerator(name="correctiveeventid_seq", sequenceName="correctiveeventid_seq", allocationSize=1)
	private int id;
	
	String correctiveEventID;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCorrectiveEventID() {
		return correctiveEventID;
	}

	public void setCorrectiveEventID(String correctiveEventID) {
		this.correctiveEventID = correctiveEventID;
	}
	

}
