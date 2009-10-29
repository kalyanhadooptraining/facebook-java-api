package com.google.code.facebookapi;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple data structure for image media type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public class AttachmentMediaImage extends AttachmentMedia {

	private Map<String,String> images;

	public AttachmentMediaImage() {
		super( "image" );
		images = new TreeMap<String,String>();
	}

	/**
	 * Add an image. Max number of images is 5.
	 * 
	 * @param src
	 *            URL of the image.
	 * @param href
	 *            Location to link image to.
	 */
	public void addImage( final String src, final String href ) {
		if ( StringUtils.isEmpty( src ) || StringUtils.isEmpty( href ) ) {
			return;
		}
		if ( images.size() > 4 ) {
			return;
		}
		images.put( src, href );
	}

	/**
	 * @return a JSON representation of attachment.
	 */
	@Override
	public JSONArray toJson() {
		JSONArray jsonArray = new JSONArray();
		for ( String key : images.keySet() ) {
			JSONObject image = new JSONObject();

			try {
				image.put( "type", getMediaType() );
			}
			catch ( Exception ignored ) {
				// ignore
			}

			try {
				image.put( "src", key );
			}
			catch ( Exception ignored ) {
				// ignore
			}

			try {
				image.put( "href", images.get( key ) );
			}
			catch ( Exception ignored ) {
				// ignore
			}

			jsonArray.put( image );
		}

		return jsonArray;
	}

}
