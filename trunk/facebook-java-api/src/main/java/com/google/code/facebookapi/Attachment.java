package com.google.code.facebookapi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple data structure for storing stream attachments used in the stream_publish API call.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
@SuppressWarnings("serial")
public class Attachment implements Serializable {
	private String name;
	private String href;
	private String caption;
	private String description;
	private List<AttachmentProperty> properties;
	private AttachmentMedia media;
	private Map<String,String> additionalInfo;
	private JSONObject jsonAttachment;

	/**
	 * Constructor.
	 */
	public Attachment() {
		// empty
	}

	/**
	 * @return a JSON representation of attachment.
	 */
	public JSONObject toJson() {
		jsonAttachment = new JSONObject();

		putJsonObject( "name", name );
		putJsonObject( "href", href );
		putJsonObject( "caption", caption );
		putJsonObject( "description", description );

		putJsonProperties();
		putJsonMedia();
		putJsonAdditionalInfo();

		return jsonAttachment;
	}

	private void putJsonObject( final String key, final Object value ) {
		if ( jsonAttachment == null ) {
			// this should only be called by toJson() after the object is initialized
			return;
		}
		try {
			jsonAttachment.put( key, value );
		}
		catch ( Exception ignored ) {
			// ignore
		}
	}

	private void putJsonProperties() {
		if ( properties == null || properties.isEmpty() ) {
			return;
		}

		JSONObject jsonProperties = new JSONObject();
		for ( AttachmentProperty link : properties ) {
			try {
				if ( !StringUtils.isEmpty( link.getCaption() ) ) {
					if ( !StringUtils.isEmpty( link.getText() ) && !StringUtils.isEmpty( link.getHref() ) ) {
						jsonProperties.put( link.getCaption(), link.toJson() );
					} else if ( !StringUtils.isEmpty( link.getText() ) ) {
						jsonProperties.put( link.getCaption(), link.getText() );
					}
				}
			}
			catch ( JSONException exception ) {
				throw ExtensibleClient.runtimeException( exception );
			}
		}

		putJsonObject( "properties", jsonProperties );
	}

	private void putJsonMedia() {
		if ( media == null ) {
			return;
		}

		putJsonObject( "media", media.toJson() );
	}

	private void putJsonAdditionalInfo() {
		if ( additionalInfo == null || additionalInfo.isEmpty() ) {
			return;
		}

		for ( String key : additionalInfo.keySet() ) {
			putJsonObject( key, additionalInfo.get( key ) );
		}
	}

	/**
	 * @return a JSON-encoded String representation of this template. The resulting String is appropriate for passing to the Facebook API server.
	 */
	public String toJsonString() {
		return this.toJson().toString();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href
	 *            the href to set
	 */
	public void setHref( String href ) {
		this.href = href;
	}

	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * @param caption
	 *            the caption to set
	 */
	public void setCaption( String caption ) {
		this.caption = caption;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription( String description ) {
		this.description = description;
	}


	/**
	 * @return the properties
	 */
	public List<AttachmentProperty> getProperties() {
		return properties;
	}


	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties( List<AttachmentProperty> properties ) {
		this.properties = properties;
	}

	/**
	 * @return the additionalInfo
	 */
	public Map<String,String> getAdditionalInfo() {
		return additionalInfo;
	}


	/**
	 * @param additionalInfo
	 *            the additionalInfo to set
	 */
	public void setAdditionalInfo( Map<String,String> additionalInfo ) {
		this.additionalInfo = additionalInfo;
	}

	/**
	 * @return the media
	 */
	public AttachmentMedia getMedia() {
		return media;
	}

	/**
	 * @param media
	 *            the media to set
	 */
	public void setMedia( AttachmentMedia media ) {
		this.media = media;
	}
}
