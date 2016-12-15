package org.oliot.model.oliot;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
@Entity
public class ExtensionMaps {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
