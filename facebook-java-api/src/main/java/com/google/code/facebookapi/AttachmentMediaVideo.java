package com.google.code.facebookapi;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple data structure for video media type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
@SuppressWarnings("serial")
public class AttachmentMediaVideo extends AttachmentMedia {
	private String videoSrc;
	private String previewImg;
	private String title;
	private String type;
	private String link;
	private JSONObject jsonObject;

	/**
	 * Construct a video attachment.
	 * 
	 * @param videoSrc
	 *            URL of the video to be rendered.
	 * @param previewImg
	 *            URL of an image that should be displayed in place of the video until the user clicks to play.
	 * @param title
	 *            Video title. (optional)
	 * @param type
	 *            Video type, default is "application/x-shockwave-flash". (optional)
	 * @param link
	 *            Video link, default is value of videoSrc. (optional)
	 */
	public AttachmentMediaVideo( final String videoSrc, final String previewImg, final String title, final String type, final String link ) {
		this.mediaType = "video";
		this.videoSrc = videoSrc;
		this.previewImg = previewImg;
		this.title = title;
		this.type = type;
		this.link = link;
	}

	/**
	 * Construct a video attachment.
	 */
	public AttachmentMediaVideo() {
		this.mediaType = "video";
	}

	/**
	 * @return a JSON representation of attachment.
	 */
	@Override
	public JSONArray toJson() {
		jsonObject = new JSONObject();
		putJsonProperty( "type", mediaType );
		putJsonProperty( "video_src", videoSrc );
		putJsonProperty( "preview_img", previewImg );

		if ( !StringUtils.isEmpty( title ) ) {
			putJsonProperty( "video_title", title );
		}
		if ( !StringUtils.isEmpty( type ) ) {
			putJsonProperty( "video_type", type );
		}
		if ( !StringUtils.isEmpty( link ) ) {
			putJsonProperty( "video_link", link );
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
	 * @return the videoSrc
	 */
	public String getVideoSrc() {
		return videoSrc;
	}

	/**
	 * @param videoSrc
	 *            the videoSrc to set
	 */
	public void setVideoSrc( String videoSrc ) {
		this.videoSrc = videoSrc;
	}

	/**
	 * @return the previewImg
	 */
	public String getPreviewImg() {
		return previewImg;
	}

	/**
	 * @param previewImg
	 *            the previewImg to set
	 */
	public void setPreviewImg( String previewImg ) {
		this.previewImg = previewImg;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle( String title ) {
		this.title = title;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType( String type ) {
		this.type = type;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink( String link ) {
		this.link = link;
	}
}
