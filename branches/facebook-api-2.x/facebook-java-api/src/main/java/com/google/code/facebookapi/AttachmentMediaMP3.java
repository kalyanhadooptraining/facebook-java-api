package com.google.code.facebookapi;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple data structure for mp3 media type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
@SuppressWarnings("serial")
public class AttachmentMediaMP3 extends AttachmentMedia {
	private String src;
	private String title;
	private String artist;
	private String album;
	private JSONObject jsonObject;

	/**
	 * Construct a MP3 attachment.
	 * 
	 * @param src
	 *            URL of the MP3 file to be rendered within Facebook's MP3 player widget.
	 * @param title
	 *            MP3 title. (optional)
	 * @param artist
	 *            MP3 artist. (optional)
	 * @param album
	 *            MP3 album. (optional)
	 */
	public AttachmentMediaMP3( final String src, final String title, final String artist, final String album ) {
		this.mediaType = "mp3";
		this.src = src;
		this.title = title;
		this.artist = artist;
		this.album = album;
	}

	/**
	 * Construct a MP3 attachment.
	 */
	public AttachmentMediaMP3() {
		this.mediaType = "mp3";
	}

	/**
	 * @return a JSON representation of attachment.
	 */
	@Override
	public JSONArray toJson() {
		jsonObject = new JSONObject();
		putJsonProperty( "type", mediaType );
		putJsonProperty( "src", src );
		if ( !StringUtils.isEmpty( title ) ) {
			putJsonProperty( "title", title );
		}
		if ( !StringUtils.isEmpty( artist ) ) {
			putJsonProperty( "artist", artist );
		}
		if ( !StringUtils.isEmpty( album ) ) {
			putJsonProperty( "album", album );
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
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @param src
	 *            the src to set
	 */
	public void setSrc( String src ) {
		this.src = src;
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
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist( String artist ) {
		this.artist = artist;
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @param album
	 *            the album to set
	 */
	public void setAlbum( String album ) {
		this.album = album;
	}
}
