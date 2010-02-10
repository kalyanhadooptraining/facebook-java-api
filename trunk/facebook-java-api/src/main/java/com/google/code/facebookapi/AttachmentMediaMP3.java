package com.google.code.facebookapi;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple data structure for mp3 media type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public class AttachmentMediaMP3 extends AttachmentMedia {

	private String src;
	private String title;
	private String artist;
	private String album;

	public AttachmentMediaMP3() {
		super( "mp3" );
	}

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
		this();
		this.src = src;
		this.title = title;
		this.artist = artist;
		this.album = album;
	}

	@Override
	public JSONObject toJson() {
		try {
			JSONObject json = super.toJson();
			json.put( "src", src );
			if ( !StringUtils.isEmpty( title ) ) {
				json.put( "title", title );
			}
			if ( !StringUtils.isEmpty( artist ) ) {
				json.put( "artist", artist );
			}
			if ( !StringUtils.isEmpty( album ) ) {
				json.put( "album", album );
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

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist( String artist ) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum( String album ) {
		this.album = album;
	}

}
