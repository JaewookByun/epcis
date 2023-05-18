package org.oliot.epcis.model.cbv;

public enum Comparator {
	Equal("EQ"), GreaterThan("GT"), GreaterThanOrEqualTo("GE"), LessThan("LT"), LessThanOrEqualTo("LE");
	
	private String comparator;

	private Comparator(String comparator) {
		this.comparator = comparator;
	}

	public String getComparator() {
		return comparator;
	}
}
