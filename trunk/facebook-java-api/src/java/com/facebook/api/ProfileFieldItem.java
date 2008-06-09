package com.facebook.api;

import java.util.HashMap;
import java.util.Map;

/**
 * A data structure for managing the profile field-item objects required by the 
 * profile.setInfo and profile.setInfoOptions API calls.  Each field-item must specify 
 * a label and a link URL, and may optionally include a description, a sublabel, and 
 * an image URL. 
 * 
 * @author aroth
 */
public class ProfileFieldItem {
	private Map<String, String> properties;
	
	public ProfileFieldItem(String label, String url) {
		properties = new HashMap<String, String>();
		properties.put("label", label);
		properties.put("link", url);
	}
	
	public String getLabel() {
		return properties.get("label");
	}
	
	public String getUrl() {
		return properties.get("url");
	}
	
	public String getDescription() {
		return properties.get("description");
	}
	
	public String getImageUrl() {
		return properties.get("image");
	}
	
	public String getSublabel() {
		return properties.get("label");
	}
	
	public void setDescription(String description) {
		properties.put("description", description);
	}
	
	public void setImageUrl(String imageUrl) {
		properties.put("image", imageUrl);
	}
	
	public void setSublabel(String sublabel) {
		properties.put("sublabel", sublabel);
	}
	
	Map<String, String> getMap() throws FacebookException {
		if ("".equals(getLabel()) || getLabel() == null || "".equals(getUrl()) || getUrl() == null) {
			throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "Field items must include both a label and a link URL.");
		}
		return properties;
	}
}
