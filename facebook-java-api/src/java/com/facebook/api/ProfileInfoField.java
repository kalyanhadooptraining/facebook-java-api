package com.facebook.api;

import java.util.ArrayList;
import java.util.List;

/**
 * A data structure for managing the profile info fields objects required by the 
 * profile.setInfo and profile.setInfoOptions API calls.  Each field is identified
 * by name, and may contain any number of field items.  Each field-item must specify 
 * a label and a link URL, and may optionally include a description, a sublabel, and 
 * an image URL. 
 * 
 * @author aroth
 */
public class ProfileInfoField {
	String fieldName;
	List<ProfileFieldItem> items;
	
	public ProfileInfoField(String name) {
		this.fieldName = name;
		this.items = new ArrayList<ProfileFieldItem>();
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public List<ProfileFieldItem> getItems() {
		return items;
	}
	public void setItems(List<ProfileFieldItem> items) {
		this.items = items;
	}
	
	/**
	 * Add an item to this ProfileInfoField.
	 * 
	 * @param item the item to add.
	 */
	public void addItem(ProfileFieldItem item) {
		this.items.add(item);
	}
}
