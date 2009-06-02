package com.google.code.facebookapi;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple data structure for flash media type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
@SuppressWarnings("serial")
public class AttachmentMediaFlash extends AttachmentMedia {
	private String swfsrc;
	private String imgsrc;
	private Integer width;
	private Integer height;
	private Integer expandedWidth;
	private Integer expandedHeight;
	private JSONObject jsonObject;

	/**
	 * Construct a Flash attachment.
	 * 
	 * @param swfsrc
	 *            URL of the Flash object to be rendered.
	 * @param imgsrc
	 *            URL of an image that should be displayed in place of the Flash object until the user clicks to prompt the Flash object to play.
	 * @param width
	 *            Width must be one of three numbers: 100, 110, or 130. (optional)
	 * @param height
	 *            Height must be between 30 and 100. (optional)
	 * @param expandedWidth
	 *            On user click the flash is resized to this width, must be between 30 and 320. (optional)
	 * @param expandedHeight
	 *            On user click the flash is resized to this height, must be between 30 and 260. (optional)
	 */
	public AttachmentMediaFlash( final String swfsrc, final String imgsrc, final Integer width, final Integer height, final Integer expandedWidth,
			final Integer expandedHeight ) {
		this.mediaType = "flash";
		this.swfsrc = swfsrc;
		this.imgsrc = imgsrc;
		this.height = height;
		this.width = width;
		this.expandedHeight = expandedHeight;
		this.expandedWidth = expandedWidth;
	}

	/**
	 * @return a JSON representation of attachment.
	 */
	@Override
	public JSONArray toJson() {
		jsonObject = new JSONObject();
		putJsonProperty( "type", mediaType );
		putJsonProperty( "swfsrc", swfsrc );
		putJsonProperty( "imgsrc", imgsrc );
		if ( height != null ) {
			putJsonProperty( "height", height );
		}
		if ( width != null ) {
			putJsonProperty( "width", width );
		}
		if ( expandedHeight != null ) {
			putJsonProperty( "expanded_height", expandedHeight );
		}
		if ( expandedWidth != null ) {
			putJsonProperty( "expanded_width", expandedWidth );
		}

		JSONArray jsonArray = new JSONArray();
		jsonArray.put( jsonObject );

		return jsonArray;
	}

	private JSONObject putJsonProperty( final String key, final Object value ) {
		try {
			jsonObject.put( key, value );
		}
		catch ( Exception ignored ) {
			// ignore
		}

		return jsonObject;
	}

	@Override
	public String toJsonString() {
		return this.toJson().toString();
	}

	/**
	 * @return the swfsrc
	 */
	public String getSwfsrc() {
		return swfsrc;
	}

	/**
	 * @param swfsrc the swfsrc to set
	 */
	public void setSwfsrc( String swfsrc ) {
		this.swfsrc = swfsrc;
	}

	/**
	 * @return the imgsrc
	 */
	public String getImgsrc() {
		return imgsrc;
	}

	/**
	 * @param imgsrc the imgsrc to set
	 */
	public void setImgsrc( String imgsrc ) {
		this.imgsrc = imgsrc;
	}

	/**
	 * @return the width
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth( Integer width ) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public Integer getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight( Integer height ) {
		this.height = height;
	}

	/**
	 * @return the expandedWidth
	 */
	public Integer getExpandedWidth() {
		return expandedWidth;
	}

	/**
	 * @param expandedWidth the expandedWidth to set
	 */
	public void setExpandedWidth( Integer expandedWidth ) {
		this.expandedWidth = expandedWidth;
	}

	/**
	 * @return the expandedHeight
	 */
	public Integer getExpandedHeight() {
		return expandedHeight;
	}

	/**
	 * @param expandedHeight the expandedHeight to set
	 */
	public void setExpandedHeight( Integer expandedHeight ) {
		this.expandedHeight = expandedHeight;
	}
}
