package org.oliot.epcis.model.cbv;

public class CBVTest {

	public static void main(String[] args) {
		String x = BusinessStep.getFullVocabularyName("accepting");
		System.out.println(x);
		String y = BusinessStep.getFullVocabularyName("x");
		System.out.println(y);
		
		String z = BusinessStep.getShortVocabularyName("urn:epcglobal:cbv:bizstep:accepting");
		System.out.println(z);
		String k = BusinessStep.getShortVocabularyName("accepting");
		System.out.println(k);
	}

}
