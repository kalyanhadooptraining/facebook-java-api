package com.google.code.facebookapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple data structure for storing a story-template action link, used in the feed_registerTemplateBundle API call.
 * 
 * @see {@link http://wiki.developers.facebook.com/index.php/Action_Links}
 */
@SuppressWarnings("serial")
public class BundleActionLink implements ToJsonObject, Serializable {

	private String text;
	private String href;

	/**
	 * Constructor. If you use this version, you must make sure you set both the 'text' and 'href' fields before trying to submit your template, otherwise it will not
	 * serialize correctly.
	 */
	public BundleActionLink() {
		// empty
	}

	/**
	 * Constructor.
	 * 
	 * @param text
	 *            the text to display for the action.
	 * @param href
	 *            the action link (may include tokens).
	 */
	public BundleActionLink( String text, String href ) {
		this.text = text;
		this.href = href;
	}

	/**
	 * @return a JSON representation of this template.
	 */
	public JSONObject toJson() {
		JSONObject result = new JSONObject();
		if ( ( text == null ) || ( href == null ) || ( "".equals( text ) ) || ( "".equals( href ) ) ) {
			return result;
		}
		try {
			result.put( "text", text );
			result.put( "href", href );
		}
		catch ( Exception ignored ) {
			// ignore
		}
		return result;
	}

	/**
	 * Get the text to display for the action.
	 */
	public final String getText() {
		return text;
	}

	/**
	 * Set the text to display for the action.
	 */
	public final void setText( String text ) {
		this.text = text;
	}

	/**
	 * Get the action link (may include tokens).
	 */
	public final String getHref() {
		return href;
	}

	/**
	 * Set the action link (may include tokens).
	 */
	public final void setHref( String href ) {
		this.href = href;
	}

	public static JSONArray toJsonArray( Iterable<BundleActionLink> list ) {
		if ( list != null ) {
			JSONArray out = new JSONArray();
			for ( BundleActionLink link : list ) {
				out.put( link.toJson() );
			}
			return out;
		}
		return null;
	}

	public static BundleActionLink fromJson( JSONObject obj ) throws JSONException {
		BundleActionLink out = null;
		if ( obj.has( "text" ) ) {
			out = new BundleActionLink();
			out.setText( obj.getString( "text" ) );
			if ( obj.has( "href" ) ) {
				out.setHref( obj.getString( "href" ) );
			}
		}
		return out;
	}

	public static List<BundleActionLink> fromJson( JSONArray arr ) throws JSONException {
		if ( arr != null ) {
			List<BundleActionLink> out = new ArrayList<BundleActionLink>();
			for ( int i = 0; i < arr.length(); i++ ) {
				JSONObject link = arr.getJSONObject( i );
				BundleActionLink outa = fromJson( link );
				if ( outa != null ) {
					out.add( outa );
				}
			}
		}
		return null;
	}

}
