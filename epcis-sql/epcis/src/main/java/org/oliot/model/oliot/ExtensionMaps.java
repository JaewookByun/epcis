package org.oliot.model.oliot;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
@Entity
public class ExtensionMaps {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	//@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="extensionmaps_seq")
	//@SequenceGenerator(name="extensionmaps_seq", sequenceName="extensionmaps_seq", allocationSize=1)
	private int id;
	
	@OneToMany
	List<ExtensionMap> extensionMapList;
	
	public List<ExtensionMap> getExtensionMapList() {
		return extensionMapList;
	}
	public void setExtensionMapList(List<ExtensionMap> extensionMapList) {
		this.extensionMapList = extensionMapList;
	}

	
	
}
