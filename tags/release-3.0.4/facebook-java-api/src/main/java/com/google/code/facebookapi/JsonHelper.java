package com.google.code.facebookapi;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {

	public static JSONObject toJson( Map<String,String> map ) {
		return new JSONObject( map );
	}

	public static JSONArray toJsonListOfStrings( Collection<String> list ) {
		return new JSONArray( list );
	}

	public static JSONArray toJsonListOfMaps( Collection<Map<String,String>> listOfMaps ) {
		JSONArray out = new JSONArray();
		for ( Map<String,String> map : listOfMaps ) {
			out.put( toJson( map ) );
		}
		return out;
	}

	/**
	 * Parses the result of an API call from JSON into Java Objects.
	 * 
	 * @param data
	 *            an InputStream with the results of a request to the Facebook servers
	 * @param method
	 *            the method
	 * @return a Java Object
	 * @throws FacebookException
	 *             if <code>data</code> represents an error
	 * @throws IOException
	 *             if <code>data</code> is not readable
	 * @see JSONObject
	 */
	public static Object parseCallResult( Object rawResponse ) throws FacebookException {
		if ( rawResponse == null ) {
			return null;
		}
		String jsonResp = (String) rawResponse;
		Object json = jsonToJavaValue( jsonResp );

		if ( json instanceof JSONObject ) {
			JSONObject jsonObj = (JSONObject) json;
			try {
				if ( jsonObj.has( "error_code" ) ) {
					int code = jsonObj.getInt( "error_code" );
					String message = null;
					if ( jsonObj.has( "error_msg" ) ) {
						message = jsonObj.getString( "error_msg" );
					}
					throw new FacebookException( code, message );
				}
			}
			catch ( JSONException ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
		}
		return json;
	}

	/**
	 * Determines the correct datatype for a json string and converts it. The json.org library really should have a method to do this.
	 */
	public static Object jsonToJavaValue( String s ) {
		if ( s.startsWith( "[" ) ) {
			try {
				return new JSONArray( s );
			}
			catch ( JSONException ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
		}

		if ( s.startsWith( "{" ) ) {
			try {
				return new JSONObject( s );
			}
			catch ( JSONException ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
		}

		Object returnMe = stringToValue( s );

		// If we have a string, strip off the quotes

		if ( returnMe instanceof String ) {
			String strValue = (String) returnMe;
			if ( strValue.length() > 1 ) {
				strValue = strValue.trim();
				if ( strValue.startsWith( "\"" ) && strValue.endsWith( "\"" ) ) {
					returnMe = strValue.substring( 1, strValue.length() - 1 );
				}
			}
		}

		return returnMe;
	}

	/**
	 * COPIED FROM LATEST JSON.ORG SOURCE CODE FOR JSONObject
	 * 
	 * Try to convert a string into a number, boolean, or null. If the string can't be converted, return the string.
	 * 
	 * @param s
	 *            A String.
	 * @return A simple JSON value.
	 */
	public static Object stringToValue( String s ) {
		if ( s.equals( "" ) ) {
			return s;
		}
		if ( s.equalsIgnoreCase( "true" ) ) {
			return Boolean.TRUE;
		}
		if ( s.equalsIgnoreCase( "false" ) ) {
			return Boolean.FALSE;
		}
		if ( s.equalsIgnoreCase( "null" ) ) {
			return JSONObject.NULL;
		}

		/*
		 * If it might be a number, try converting it. We support the 0- and 0x- conventions. If a number cannot be produced, then the value will just be a string. Note
		 * that the 0-, 0x-, plus, and implied string conventions are non-standard. A JSON parser is free to accept non-JSON forms as long as it accepts all correct JSON
		 * forms.
		 */

		char b = s.charAt( 0 );
		if ( ( b >= '0' && b <= '9' ) || b == '.' || b == '-' || b == '+' ) {
			if ( b == '0' ) {
				if ( s.length() > 2 && ( s.charAt( 1 ) == 'x' || s.charAt( 1 ) == 'X' ) ) {
					try {
						return new Integer( Integer.parseInt( s.substring( 2 ), 16 ) );
					}
					catch ( Exception e ) {
						/* Ignore the error */
					}
				} else {
					try {
						return new Integer( Integer.parseInt( s, 8 ) );
					}
					catch ( Exception e ) {
						/* Ignore the error */
					}
				}
			}
			try {
				return new Integer( s );
			}
			catch ( Exception e ) {
				try {
					return new Long( s );
				}
				catch ( Exception f ) {
					try {
						return new Double( s );
					}
					catch ( Exception g ) {
						/* Ignore the error */
					}
				}
			}
		}
		return s;
	}

}
