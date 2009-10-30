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
				throw BasicClientHelper.runtimeException( exception );
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

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public void setHref( String href ) {
		this.href = href;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption( String caption ) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public List<AttachmentProperty> getProperties() {
		return properties;
	}

	public void setProperties( List<AttachmentProperty> properties ) {
		this.properties = properties;
	}

	public Map<String,String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo( Map<String,String> additionalInfo ) {
		this.additionalInfo = additionalInfo;
	}

	public AttachmentMedia getMedia() {
		return media;
	}

	public void setMedia( AttachmentMedia media ) {
		this.media = media;
	}

}
