package com.google.code.facebookapi;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstract class for attachment media types.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public abstract class AttachmentMedia implements ToJsonObject, Serializable {

	private String mediaType;

	protected AttachmentMedia( String mediaType ) {
		this.mediaType = mediaType;
	}

	public String getMediaType() {
		return mediaType;
	}

	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		try {
			json.put( "type", mediaType );
		}
		catch ( JSONException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
		return json;
	}

}
