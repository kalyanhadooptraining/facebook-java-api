package com.google.code.facebookapi;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Facebook has deprecated this {@link http://developers.facebook.com/news.php?blog=1&story=340}
 * 
 * A simple data structure for video media type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
@Deprecated
public class AttachmentMediaVideo extends AttachmentMedia {

	private String videoSrc;
	private String previewImg;
	private String title;
	private String type;
	private String link;

	public AttachmentMediaVideo() {
		super( "video" );
	}

	/**
	 * Construct a video attachment.
	 * 
	 * @param videoSrc
	 *            URL of the video to be rendered. (required)
	 * @param previewImg
	 *            URL of an image that should be displayed in place of the video until the user clicks to play. (required)
	 * @param title
	 *            Video title. (optional)
	 * @param type
	 *            Video type, default is "application/x-shockwave-flash". (optional)
	 * @param link
	 *            Video link, default is value of videoSrc. (optional)
	 */
	public AttachmentMediaVideo( final String videoSrc, final String previewImg, final String title, final String type, final String link ) {
		this();
		this.videoSrc = videoSrc;
		this.previewImg = previewImg;
		this.title = title;
		this.type = type;
		this.link = link;
	}

	/**
	 * @return a JSON representation of attachment.
	 */
	@Override
	public JSONObject toJson() {
		try {
			JSONObject json = super.toJson();
			json.put( "video_src", videoSrc );
			json.put( "preview_img", previewImg );
			if ( !StringUtils.isEmpty( title ) ) {
				json.put( "video_title", title );
			}
			if ( !StringUtils.isEmpty( type ) ) {
				json.put( "video_type", type );
			}
			if ( !StringUtils.isEmpty( link ) ) {
				json.put( "video_link", link );
			}
			return json;
		}
		catch ( JSONException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	public String getVideoSrc() {
		return videoSrc;
	}

	public void setVideoSrc( String videoSrc ) {
		this.videoSrc = videoSrc;
	}

	public String getPreviewImg() {
		return previewImg;
	}

	public void setPreviewImg( String previewImg ) {
		this.previewImg = previewImg;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}

	public void setLink( String link ) {
		this.link = link;
	}

}
