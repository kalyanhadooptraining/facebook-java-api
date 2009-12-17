package com.google.code.facebookapi;

import java.util.HashMap;
import java.util.Map;

/**
 * List of URL parameters added by Facebook.
 * 
 * @see http://wiki.developers.facebook.com/index.php/Authorizing_Applications
 */
public enum FacebookParam implements CharSequence {

	//
	SIGNATURE,
	ADDED("added"),
	API_KEY("api_key"),
	FRIENDS("friends"),
	LOCALE("locale"),
	TIME("time"),
	USER("user"),
	CANVAS_USER("canvas_user"),
	SESSION_KEY("session_key"),
	EXPIRES("expires"),
	PROFILE_UPDATE_TIME("profile_update_time"),
	EXT_PERMS("ext_perms"),
	IN_CANVAS("in_canvas"),
	IN_PROFILE_TAB("in_profile_tab"),
	PROFILE_USER("profile_user"),
	PROFILE_SESSION_KEY("profile_session_key"),
	PAGE_ID("page_id"),
	PAGE_ADDED("page_added"),
	REQUEST_METHOD("request_method"),

	IN_NEW_FACEBOOK("in_new_facebook"),

	IN_IFRAME("in_iframe"),

	// parameters passed from a Facebook Page
	IS_ADMIN("is_admin"),
	IS_FAN("is_fan"),
	TYPE("type"),

	// for use in javascript
	SS("ss"),

	// SMS sig params
	SMS("sms"),
	MESSAGE("message"),
	SMS_SID("sms_sid"),
	SMS_NEW_USER("sms_new_user"),
	POSITION_FIX("position_fix"),
	// other
	UNINSTALL("uninstall")
	//
	;

	private static final Map<String,FacebookParam> _lookupTable;
	static {
		_lookupTable = new HashMap<String,FacebookParam>();
		for ( FacebookParam param : FacebookParam.values() ) {
			_lookupTable.put( param.toString(), param );
		}
	}

	/**
	 * Retrieves the FacebookParam corresponding to the supplied String key.
	 * 
	 * @param key
	 *            a possible FacebookParam
	 * @return the matching FacebookParam or null if there's no match
	 */
	public static FacebookParam get( String key ) {
		return _lookupTable.get( key );
	}

	/**
	 * Indicates whether a given key is in the FacebookParam namespace
	 * 
	 * @param key
	 * @return boolean
	 */
	public static boolean isInNamespace( String key ) {
		return null != key && key.startsWith( "fb_sig" );
	}

	/**
	 * Check to see if a given parameter name is a signature parameter.
	 * 
	 * @param key
	 *            the parameter name to check
	 * 
	 * @return true if the parameter is a signature parameter false otherwise
	 */
	public static boolean isSignature( String key ) {
		return null != key && key.equals( "fb_sig" );
	}

	private String _paramName;
	private String _signatureName;

	private FacebookParam() {
		this._paramName = "fb_sig";
	}

	private FacebookParam( String name ) {
		this._signatureName = name;
		this._paramName = "fb_sig_" + name;
	}

	/* Implementing CharSequence */
	public char charAt( int index ) {
		return this._paramName.charAt( index );
	}

	public int length() {
		return this._paramName.length();
	}

	public CharSequence subSequence( int start, int end ) {
		return this._paramName.subSequence( start, end );
	}

	public String toString() {
		return this._paramName;
	}

	/**
	 * @return the signature name of this parameter
	 */
	public String getSignatureName() {
		return this._signatureName;
	}

	/**
	 * Remove the Facebook signature prefix from the specified parameter.
	 * 
	 * @param paramName
	 *            the name to remove the prefix from.
	 * 
	 * @return the specified name, with the Facebook signature prefix removed, if necessary.
	 */
	public static String stripSignaturePrefix( String paramName ) {
		if ( paramName != null && paramName.startsWith( "fb_sig_" ) ) {
			return paramName.substring( 7 );
		}
		return paramName;
	}

}
