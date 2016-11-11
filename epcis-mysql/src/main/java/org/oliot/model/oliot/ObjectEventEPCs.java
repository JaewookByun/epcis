package org.oliot.model.oliot;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;



@Entity
public class ObjectEventEPCs {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	
	@OneToMany
	protected List<EPCN> epc =new ArrayList<EPCN>();
	
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public List<EPCN> getEpc() {
		return epc;
	}
	public void setEpc(List<EPCN> epcList) {
		this.epc = epcList;
	}
	public ObjectEventEPCs(int id, List<EPCN> epc) {
		super();
		this.id = id;
		this.epc = epc;
	}
	public ObjectEventEPCs() {
		super();
	}



	
	

}
