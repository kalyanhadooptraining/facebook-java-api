package com.google.code.facebookapi;

import org.json.JSONArray;


/**
 * Abstract class for attachment media types.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public abstract class AttachmentMedia {
	protected String mediaType;

	/**
	 * @return JSON Array of this media attachment.
	 */
	public abstract JSONArray toJson();

	/**
	 * 
	 * @return String of JSON Array of this media attachment.
	 */
	public abstract String toJsonString();

	/**
	 * 
	 * @return Type of this media attachment.
	 */
	public String getMediaType() {
		return mediaType;
	}
}
