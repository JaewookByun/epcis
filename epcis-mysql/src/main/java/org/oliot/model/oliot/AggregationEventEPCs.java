package org.oliot.model.oliot;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;



@Entity
public class AggregationEventEPCs {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
//	@OneToOne
//	@JoinColumn(name="aggregationEvent_id")
//	private AggregationEvent aggregationEvent;
	
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
	public AggregationEventEPCs(int id, List<EPCN> epc) {
		super();
		this.id = id;
		this.epc = epc;
	}
	public AggregationEventEPCs() {
		super();
	}

//	public AggregationEvent getAggregationEvent() {
//		return aggregationEvent;
//	}
//
//	public void setAggregationEvent(AggregationEvent aggregationEvent) {
//		this.aggregationEvent = aggregationEvent;
//	}
	
	

}
