package com.google.code.facebookapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple data structure for image media type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public class AttachmentMediaImage extends AttachmentMedia {

	private String src;
	private String href;

	public AttachmentMediaImage( String src, String href ) {
		super( "image" );
		this.src = src;
		this.href = href;
	}

	@Override
	public JSONObject toJson() {
		try {
			JSONObject json = super.toJson();
			json.put( "src", src );
			if ( href != null ) {
				json.put( "href", href );
			}
			return json;
		}
		catch ( JSONException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	public String getSrc() {
		return src;
	}

	public void setSrc( String src ) {
		this.src = src;
	}

	public String getHref() {
		return href;
	}

	public void setHref( String href ) {
		this.href = href;
	}

}
