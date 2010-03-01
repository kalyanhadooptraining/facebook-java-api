package com.google.code.facebookapi;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base class for common news and activity dashboard news items.
 */
public abstract class DashboardItem implements Serializable {

	private String message;
	private BundleActionLink actionLink;
	
	/**
	 * Returns the message associated with news item.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message associated with news item.
	 */
	public void setMessage( String message ) {
		this.message = message;
	}

	/**
	 * Returns the action link associated with news item.
	 */
	public BundleActionLink getActionLink() {
		return actionLink;
	}

	/**
	 * Sets the action link assaciated with news item.
	 */
	public void setActionLink( BundleActionLink actionLink ) {
		this.actionLink = actionLink;
	}
	
	/**
	 * Creates a JSONObject corresponding to contents of object.
	 */
	public JSONObject toJSON() {
		
		JSONObject itemJSON = new JSONObject();
		
		try {
			itemJSON.put( "message", getMessage() );
			
			if (getActionLink() != null) {
				itemJSON.put( "action_link", getActionLink().toJson() );
			}
		}
		catch ( JSONException exception ) {
			throw BasicClientHelper.runtimeException( exception );
		}
		
		return itemJSON;
	}
}
