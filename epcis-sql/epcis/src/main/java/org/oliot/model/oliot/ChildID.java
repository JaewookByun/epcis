package org.oliot.model.oliot;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ChildID {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	protected String sID;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getsID() {
		return sID;
	}
	public void setsID(String sID) {
		this.sID = sID;
	}
	
	
	
	

}
