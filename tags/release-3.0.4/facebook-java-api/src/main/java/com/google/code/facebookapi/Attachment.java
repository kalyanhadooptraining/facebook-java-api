package com.google.code.facebookapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple data structure for storing stream attachments used in the stream_publish API call.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
@SuppressWarnings("serial")
public class Attachment implements Serializable, ToJsonObject {

	private String name;
	private String href;
	private String caption;
	private String description;
	private List<AttachmentProperty> properties;
	private List<AttachmentMedia> media;
	private Map<String,String> additionalInfo;
	private String commentsXid;

	public Attachment() {
		// empty
	}

	/**
	 * @return a JSON representation of attachment.
	 */
	public JSONObject toJson() {
		JSONObject out = new JSONObject();
		putQuiet( "name", name, out );
		putQuiet( "href", href, out );
		putQuiet( "caption", caption, out );
		putQuiet( "description", description, out );
		putQuiet( "comments_xid", commentsXid, out );

		putProperties( out );
		putMedia( out );
		putAdditionalInfo( out );

		return out;
	}

	private static void putQuiet( final String key, final Object value, JSONObject json ) {
		if ( value != null ) {
			try {
				json.put( key, value );
			}
			catch ( JSONException ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
		}
	}

	private void putProperties( JSONObject tojson ) {
		if ( properties == null || properties.isEmpty() ) {
			return;
		}

		JSONObject jsonProperties = new JSONObject( new LinkedHashMap() ) {
			public Iterator sortedKeys() {
				return this.keys();
			}
		};

		for ( AttachmentProperty link : properties ) {
			try {
				if ( !StringUtils.isEmpty( link.getHref() ) ) {
					JSONObject val = new JSONObject();
					val.put( "text", link.getValue() );
					val.put( "href", link.getHref() );
					jsonProperties.put( link.getKey(), val );
				} else {
					jsonProperties.put( link.getKey(), link.getValue() );
				}
			}
			catch ( JSONException exception ) {
				throw BasicClientHelper.runtimeException( exception );
			}
		}

		putQuiet( "properties", jsonProperties, tojson );
	}

	private void putMedia( JSONObject tojson ) {
		if ( media == null || media.isEmpty() ) {
			return;
		}

		JSONArray ar = new JSONArray();
		for ( AttachmentMedia m : media ) {
			ar.put( m.toJson() );
		}
		putQuiet( "media", ar, tojson );
	}

	private void putAdditionalInfo( JSONObject tojson ) {
		if ( additionalInfo == null || additionalInfo.isEmpty() ) {
			return;
		}

		for ( String key : additionalInfo.keySet() ) {
			putQuiet( key, additionalInfo.get( key ), tojson );
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

	public String getCommentsXid() {
		return commentsXid;
	}

	public void setCommentsXid( String commentsXid ) {
		this.commentsXid = commentsXid;
	}

	public List<AttachmentMedia> getMedia() {
		return media;
	}

	public void setMedia( List<AttachmentMedia> media ) {
		this.media = media;
	}

	public void setMedia( AttachmentMedia media ) {
		this.media = new ArrayList<AttachmentMedia>();
		this.media.add( media );
	}

	public void addMedia( AttachmentMedia media ) {
		if ( this.media == null ) {
			this.media = new ArrayList<AttachmentMedia>();
		}
		this.media.add( media );
	}

}
