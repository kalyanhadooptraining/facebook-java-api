package com.google.code.facebookapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A FacebookRestClient that uses the JSON result format. This means results from calls to the Facebook API are returned as <a href="http://www.json.org/">JSON</a> and
 * transformed into Java <code>Object</code>'s.
 */
public abstract class FacebookJsonRestClientBase extends SpecificReturnTypeAdapter implements IFacebookRestClient<Object> {

	protected static Log log = LogFactory.getLog( FacebookJsonRestClientBase.class );

	protected ExtensibleClient client;

	public ExtensibleClient getClient() {
		return client;
	}

	public void setClient( ExtensibleClient client ) {
		this.client = client;
	}

	public FacebookJsonRestClientBase( ExtensibleClient client ) {
		super( "json" );
		this.client = client;
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 */
	public FacebookJsonRestClientBase( String apiKey, String secret ) {
		this( new ExtensibleClient( apiKey, secret ) );
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookJsonRestClientBase( String apiKey, String secret, int connectionTimeout ) {
		this( new ExtensibleClient( apiKey, secret, connectionTimeout ) );
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 */
	public FacebookJsonRestClientBase( String apiKey, String secret, String sessionKey ) {
		this( new ExtensibleClient( apiKey, secret, sessionKey ) );
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookJsonRestClientBase( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( new ExtensibleClient( apiKey, secret, sessionKey, connectionTimeout ) );
	}


	/**
	 * Constructor.
	 * 
	 * @param serverAddr
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * 
	 * @throws MalformedURLException
	 *             if you specify an invalid URL
	 */
	public FacebookJsonRestClientBase( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		this( new ExtensibleClient( serverAddr, apiKey, secret, sessionKey ) );
	}

	/**
	 * Constructor.
	 * 
	 * @param serverAddr
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 * 
	 * @throws MalformedURLException
	 *             if you specify an invalid URL
	 */
	public FacebookJsonRestClientBase( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		this( new ExtensibleClient( serverAddr, apiKey, secret, sessionKey, connectionTimeout ) );
	}


	/**
	 * Constructor.
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 */
	public FacebookJsonRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey ) );
	}

	/**
	 * Constructor.
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookJsonRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 ) );
	}

	/**
	 * Constructor.
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 * @param readTimeout
	 *            the read timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookJsonRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout ) );
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
	static Object parseCallResult( Object rawResponse ) throws FacebookException {
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
			catch ( JSONException ignored ) {
				// ignore
			}
		}
		return json;
	}

	/**
	 * Executes a batch of queries. You define the queries to execute by calling 'beginBatch' and then invoking the desired API methods that you want to execute as part
	 * of your batch as normal. Invoking this method will then execute the API calls you made in the interim as a single batch query.
	 * 
	 * @param serial
	 *            set to true, and your batch queries will always execute serially, in the same order in which your specified them. If set to false, the Facebook API
	 *            server may execute your queries in parallel and/or out of order in order to improve performance.
	 * 
	 * @return a list containing the results of the batch execution. The list will be ordered such that the first element corresponds to the result of the first query in
	 *         the batch, and the second element corresponds to the result of the second query, and so on. The types of the objects in the list will match the type
	 *         normally returned by the API call being invoked (so calling users_getLoggedInUser as part of a batch will place a Long in the list, and calling friends_get
	 *         will place a Document in the list, etc.).
	 * 
	 * The list may be empty, it will never be null.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public List<? extends Object> executeBatch( boolean serial ) throws FacebookException {
		client.setResponseFormat( "json" );
		List<String> clientResults = client.executeBatch( serial );

		List<Object> result = new ArrayList<Object>();

		for ( String clientResult : clientResults ) {
			JSONArray doc;
			try {
				// Must ensure that JSONArray(String) constructor
				// is called. JSONArray(Object) behaves differently.
				doc = new JSONArray( clientResult );
			}
			catch ( JSONException ex ) {
				throw new RuntimeException( "Error parsing client result", ex );
			}
			for ( int count = 0; count < doc.length(); count++ ) {
				try {
					String response = (String) doc.get( count );
					Object responseObject = parseCallResult( response );
					result.add( responseObject );
				}
				catch ( Exception ignored ) {
					result.add( null );
				}
			}
		}
		return result;
	}

	public String getRawResponse() {
		return client.getRawResponse();
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
				// ignore
			}
		}

		if ( s.startsWith( "{" ) ) {
			try {
				return new JSONObject( s );
			}
			catch ( JSONException ex ) {
				// ignore
			}
		}

		Object returnMe = stringToValue( s );
		// If we have a string, strip off the quotes
		if ( returnMe instanceof String ) {
			String strValue = (String) returnMe;
			if ( strValue.length() > 1 ) {
				returnMe = strValue.trim().substring( 1, strValue.length() - 1 );
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
	static public Object stringToValue( String s ) {
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
