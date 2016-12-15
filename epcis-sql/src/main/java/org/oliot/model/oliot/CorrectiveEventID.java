package org.oliot.model.oliot;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CorrectiveEventID {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
