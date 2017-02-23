package org.oliot.tutorials;

import org.oliot.epcis.serde.sql.Edge;

public class Pointer {
	
	public static void main(String[] args) {
		//Integer vale=12;
		//LeftRight lftrigt=new LeftRight();
		//lftrigt.setRight(12);
		Edge lftrigt=new Edge();
		Pointer point=new Pointer();
		point.increment(lftrigt);
		
		System.out.println(lftrigt.getRightNodeNumber());
		
	}
	
	public void increment(LeftRight val){
		
		//val.right++;
		val.setRight(val.getRight()+1);
		
		if(val.getRight()>14){
			return;
		}
		else{
			increment(val);
			
		}
		
	}
	
	public void increment(Edge val){
		
		//val.right++;
		val.setRightNodeNumber(val.getRightNodeNumber()+1);
		
		if(val.getRightNodeNumber()>12){
			return;
		}
		else{
			increment(val);
			
		}
		
	}
	
	

}
