package org.oliot.epcis.serde.sql;

public class Edge {
	
	private Integer leftNodeNumber;
	private Integer rightNodeNumber;
	
	public Edge(){
		this.leftNodeNumber=1;
		this.rightNodeNumber=1;
	}

	public Integer getLeftNodeNumber() {
		return leftNodeNumber;
	}

	public void setLeftNodeNumber(Integer leftNodeNumber) {
		this.leftNodeNumber = leftNodeNumber;
	}

	public Integer getRightNodeNumber() {
		return rightNodeNumber;
	}

	public void setRightNodeNumber(Integer rightNodeNumber) {
		this.rightNodeNumber = rightNodeNumber;
	}




	
}
