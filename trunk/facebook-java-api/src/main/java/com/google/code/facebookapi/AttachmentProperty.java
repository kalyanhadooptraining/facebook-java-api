package com.google.code.facebookapi;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple data structure for property link type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public class AttachmentProperty {
	private String caption;
	private String href;
	private String text;

	/**
	 * Constructor.
	 * 
	 * @param caption
	 *            The caption (required).
	 * @param text
	 *            The text for link (required).
	 * @param href
	 *            The target for link (required).
	 */
	public AttachmentProperty( final String caption, final String text, final String href ) {
		this.caption = caption;
		this.text = text;
		this.href = href;
	}

	/**
	 * @return JSON Object of this attachment link.
	 */
	public JSONObject toJson() {
		JSONObject link = new JSONObject();
		try {
			link.put( "text", text );
			link.put( "href", href );
		}
		catch ( JSONException ignored ) {
			//
		}

		return link;
	}

	/**
	 * 
	 * @return String of JSON Array of this attachment link.
	 */
	public String toJsonString() {
		return this.toJson().toString();
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}
}
