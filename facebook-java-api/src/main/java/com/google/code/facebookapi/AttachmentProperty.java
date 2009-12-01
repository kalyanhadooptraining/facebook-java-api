package com.google.code.facebookapi;


/**
 * A simple data structure for property link type used by Attachment.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Attachment_(Streams)}
 */
public class AttachmentProperty {

	private String key;
	private String value;
	private String href;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            The key (required).
	 * @param value
	 *            The value for link (required).
	 * @param href
	 *            The target for link (optional).
	 */
	public AttachmentProperty( final String key, final String value, final String href ) {
		if ( value == null ) {
			throw new IllegalArgumentException( "AttachmentProperty value must not be null" );
		}
		this.key = key;
		this.value = value;
		this.href = href;
	}

	public String getHref() {
		return href;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

}
