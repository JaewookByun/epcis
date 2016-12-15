


package org.oliot.model.oliot;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;





/**
 * @author 
 *
 */
@Entity
public class ReadPointExtension {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
	
	
	@Transient
	private String fieldname;
	@Transient
	private String prefix;
	@Transient
	private int intValue;
	@Transient
	private float floatValue;
	@Transient
	private Date dateValue;
	@Transient
	private String stringValue;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	
	
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	
	public ReadPointExtension() {
		super();
	}
	
	
	public ReadPointExtension(String fieldname) {
		super();
		this.fieldname = fieldname;
	}
	public ReadPointExtension(String fieldname, String prefix, int intValue,
			float floatValue, String stringValue) {
		super();
		this.fieldname = fieldname;
		this.prefix = prefix;
		this.intValue = intValue;
		this.floatValue = floatValue;
		this.stringValue = stringValue;
	}

	
    

}
