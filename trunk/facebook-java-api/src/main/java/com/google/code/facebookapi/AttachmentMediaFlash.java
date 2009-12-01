package com.google.code.facebookapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple data structure for flash media type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public class AttachmentMediaFlash extends AttachmentMedia {

	private String swfsrc;
	private String imgsrc;
	private Integer width;
	private Integer height;
	private Integer expandedWidth;
	private Integer expandedHeight;

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
		super( "flash" );
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
	public JSONObject toJson() {
		try {
			JSONObject json = super.toJson();
			json.put( "swfsrc", swfsrc );
			json.put( "imgsrc", imgsrc );
			if ( height != null ) {
				json.put( "height", height );
			}
			if ( width != null ) {
				json.put( "width", width );
			}
			if ( expandedHeight != null ) {
				json.put( "expanded_height", expandedHeight );
			}
			if ( expandedWidth != null ) {
				json.put( "expanded_width", expandedWidth );
			}
			return json;
		}
		catch ( JSONException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	public String getSwfsrc() {
		return swfsrc;
	}

	public void setSwfsrc( String swfsrc ) {
		this.swfsrc = swfsrc;
	}

	public String getImgsrc() {
		return imgsrc;
	}

	public void setImgsrc( String imgsrc ) {
		this.imgsrc = imgsrc;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth( Integer width ) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight( Integer height ) {
		this.height = height;
	}

	public Integer getExpandedWidth() {
		return expandedWidth;
	}

	public void setExpandedWidth( Integer expandedWidth ) {
		this.expandedWidth = expandedWidth;
	}

	public Integer getExpandedHeight() {
		return expandedHeight;
	}

	public void setExpandedHeight( Integer expandedHeight ) {
		this.expandedHeight = expandedHeight;
	}

}
