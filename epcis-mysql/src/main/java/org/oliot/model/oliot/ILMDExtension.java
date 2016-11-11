

package org.oliot.model.oliot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;


@Entity
public class ILMDExtension {
 
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Transient
	@XmlAnyElement(lax = true)
	protected List<Object> any;
	
	@Transient
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}





	public List<Object> getAny() {
		if (any == null) {
			any = new ArrayList<Object>();
		}
		return this.any;
	}

	
	public void setAny(List<Object> any) {
		this.any = any;
	}

	public void setOtherAttributes(Map<QName, String> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	
	
	public Map<QName, String> getOtherAttributes() {
		return otherAttributes;
	}

}
