package com.google.code.facebookapi;

import org.json.JSONArray;

/**
 * Abstract class for attachment media types.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public abstract class AttachmentMedia {

	private String mediaType;

	protected AttachmentMedia( String mediaType ) {
		this.mediaType = mediaType;
	}

	/**
	 * @return JSON Array of this media attachment.
	 */
	public abstract JSONArray toJson();

	/**
	 * @return Type of this media attachment.
	 */
	public String getMediaType() {
		return mediaType;
	}

}
