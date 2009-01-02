package com.google.code.facebookapi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;

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

	// used so that executeBatch can return the correct types in its list, without killing efficiency.
	private static final Map<FacebookMethod,String> RETURN_TYPES;
	static {
		RETURN_TYPES = new HashMap<FacebookMethod,String>();
		Method[] candidates = FacebookJsonRestClient.class.getMethods();
		// this loop is inefficient, but it only executes once per JVM, so it doesn't really matter
		for ( FacebookMethod method : EnumSet.allOf( FacebookMethod.class ) ) {
			String name = method.methodName();
			name = name.substring( name.indexOf( "." ) + 1 );
			name = name.replace( ".", "_" );
			for ( Method candidate : candidates ) {
				if ( candidate.getName().equalsIgnoreCase( name ) ) {
					String typeName = candidate.getReturnType().getName().toLowerCase();
					// possible types are Document, String, Boolean, Integer, Long, void
					if ( typeName.indexOf( "object" ) != -1 ) {
						RETURN_TYPES.put( method, "default" );
					} else if ( typeName.indexOf( "string" ) != -1 ) {
						RETURN_TYPES.put( method, "string" );
					} else if ( typeName.indexOf( "bool" ) != -1 ) {
						RETURN_TYPES.put( method, "bool" );
					} else if ( typeName.indexOf( "long" ) != -1 ) {
						RETURN_TYPES.put( method, "long" );
					} else if ( typeName.indexOf( "int" ) != -1 ) {
						RETURN_TYPES.put( method, "int" );
					} else if ( ( typeName.indexOf( "applicationpropertyset" ) != -1 ) || ( typeName.indexOf( "list" ) != -1 ) || ( typeName.indexOf( "url" ) != -1 )
							|| ( typeName.indexOf( "map" ) != -1 ) ) {
						RETURN_TYPES.put( method, "default" );
					} else if ( ( typeName.indexOf( "jsonarray" ) != -1 ) ) {
						RETURN_TYPES.put( method, "default" );
					} else {
						RETURN_TYPES.put( method, "void" );
					}
					break;
				}
			}
		}
	}
	
	protected ExtensibleClient client;
	public ExtensibleClient getClient() {
		return client;
	}
	public void setClient(ExtensibleClient client) {
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
		super( "json" );
		client = new ExtensibleClient( apiKey, secret );
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
		super( "json" );
		client = new ExtensibleClient( apiKey, secret, connectionTimeout );
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
		super( "json" );
		client = new ExtensibleClient( apiKey, secret, sessionKey );
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
		super( "json" );
		client = new ExtensibleClient( apiKey, secret, sessionKey, connectionTimeout );
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
		super( "json" );
		client = new ExtensibleClient( serverAddr, apiKey, secret, sessionKey );
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
		super( "json" );
		client = new ExtensibleClient( serverAddr, apiKey, secret, sessionKey, connectionTimeout );
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
		super( "json" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey );
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
		super( "json" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 );
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
		super( "json" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout );
	}

	public String admin_getAppPropertiesAsString( Collection<ApplicationProperty> properties ) throws FacebookException {
		return client.admin_getAppPropertiesAsString( properties );
	}
	
	/**
	 * Extracts a String from a result consisting entirely of a String.
	 * 
	 * @param val
	 * @return the String
	 */
	static String extractString( Object val ) {
		if ( val == null ) {
			return null;
		}
		try {
			if ( val instanceof JSONArray ) {
				try {
					// sometimes facebook will wrap its primitive types in JSON markup
					return (String) ( (JSONArray) val ).get( 0 );
				}
				catch ( Exception e ) {
					log.error( "Exception: " + e.getMessage(), e );
				}
			}
			return (String) val;
		}
		catch ( ClassCastException cce ) {
			log.error( "Exception: " + cce.getMessage(), cce );
			return null;
		}
	}

	/**
	 * Sets the session information (sessionKey) using the token from auth_createToken.
	 * 
	 * @param authToken
	 *            the token returned by auth_createToken or passed back to your callback_url.
	 * @return the session key
	 * @throws FacebookException
	 * @throws IOException
	 */
	public String auth_getSession( String authToken ) throws FacebookException {
		return client.auth_getSession( authToken );
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
		if( rawResponse == null ) {
			return null;
		}
		String jsonResp = (String)rawResponse;
		Object json = null;
		if ( jsonResp.matches( "[\\{\\[].*[\\}\\]]" ) ) {
			try {
				if ( jsonResp.matches( "\\{.*\\}" ) ) {
					json = new JSONObject( jsonResp );
				} else {
					json = new JSONArray( jsonResp );
				}
			}
			catch ( Exception ignored ) {
				ignored.printStackTrace();
			}
		} else {
			if ( jsonResp.startsWith( "\"" ) ) {
				jsonResp = jsonResp.substring( 1 );
			}
			if ( jsonResp.endsWith( "\"" ) ) {
				jsonResp = jsonResp.substring( 0, jsonResp.length() - 1 );
			}
			try {
				// it's either a number...
				json = Long.parseLong( jsonResp );
			}
			catch ( Exception e ) {
				// ...or a string
				json = jsonResp;
			}
		}
		//log.debug( method.methodName() + ": " + json );

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
	 * Extracts a URL from a result that consists of a URL only. For JSON, that result is simply a String.
	 * 
	 * @param url
	 * @return the URL
	 */
	static URL extractURL( Object url ) throws IOException {
		if ( url == null ) {
			return null;
		}
		if ( url instanceof String ) {
			return ( "".equals( url ) ) ? null : new URL( (String) url );
		}
		if ( url instanceof JSONArray ) {
			try {
				// sometimes facebook will wrap its primitive types in JSON markup
				return new URL( (String) ( (JSONArray) url ).get( 0 ) );
			}
			catch ( Exception e ) {
				log.error( "Exception: " + e.getMessage(), e );
			}
		}
		return null;
	}

	/**
	 * Extracts an Integer from a result that consists of an Integer only.
	 * 
	 * @param val
	 * @return the Integer
	 */
	static int extractInt( Object val ) {
		if ( val == null ) {
			return 0;
		}
		try {
			if ( val instanceof JSONArray ) {
				try {
					// sometimes facebook will wrap its primitive types in JSON markup
					val = ( (JSONArray) val ).get( 0 );
					if ( "true".equals( val ) || ( val instanceof Boolean && (Boolean) val ) ) {
						val = 1;
					} else if ( "false".equals( val ) || ( val instanceof Boolean && (Boolean) val ) ) {
						val = 0;
					}
				}
				catch ( Exception e ) {
					log.error( "Exception: " + e.getMessage(), e );
				}
			}
			if ( val instanceof String ) {
				// shouldn't happen, really
				return Integer.parseInt( (String) val );
			}
			if ( val instanceof Long ) {
				// this one will happen, the parse method parses all numbers as longs
				return ( (Long) val ).intValue();
			}
			return (Integer) val;
		}
		catch ( ClassCastException cce ) {
			log.error( "Exception: " + cce.getMessage(), cce );
			return 0;
		}
	}

	/**
	 * Extracts a Boolean from a result that consists of a Boolean only.
	 * 
	 * @param val
	 * @return the Boolean
	 */
	static boolean extractBoolean( Object val ) {
		if ( val == null ) {
			return false;
		}
		try {
			if ( val instanceof JSONArray ) {
				try {
					// sometimes facebook will wrap its primitive types in JSON markup
					val = ( (JSONArray) val ).get( 0 );
				}
				catch ( Exception e ) {
					log.error( "Exception: " + e.getMessage(), e );
				}
			}
			if ( val instanceof String ) {
				return ( val.equals( "true" ) || val.equals( "1" ) );
			}
			if ( val instanceof Boolean ) {
				return (Boolean) val;
			}
			if ( val instanceof Number ) {
				return ( (Number) val ).longValue() == 1l;
			}
			return ( (Long) val == 1l );
		}
		catch ( ClassCastException cce ) {
			log.error( "Exception: " + cce.getMessage(), cce );
		}
		return false;
	}

	/**
	 * Extracts a Long from a result that consists of an Long only.
	 * 
	 * @param val
	 * @return the Integer
	 */
	static Long extractLong( Object val ) {
		if ( val == null ) {
			return 0l;
		}
		try {
			if ( val instanceof JSONArray ) {
				try {
					// sometimes facebook will wrap its primitive types in JSON markup
					val = ( (JSONArray) val ).get( 0 );
					if ( "true".equals( val ) || ( val instanceof Boolean && (Boolean) val ) ) {
						val = 1l;
					} else if ( "false".equals( val ) || ( val instanceof Boolean && (Boolean) val ) ) {
						val = 0l;
					}
				}
				catch ( Exception e ) {
					log.error( "Exception: " + e.getMessage(), e );
				}
			}
			if ( val instanceof String ) {
				// shouldn't happen, really
				return Long.parseLong( (String) val );
			}
			return (Long) val;
		}
		catch ( ClassCastException cce ) {
			log.error( "Exception: " + cce.getMessage(), cce );
			return null;
		}
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
		//Take a copy of the queries being run so that we can associate them
		//with the correct return type later.
		List<BatchQuery> queries = new ArrayList<BatchQuery>(client.getQueries());
		
		List<String> clientResults = client.executeBatch( serial );
		
		List<Object> result = new ArrayList<Object>();
		
		int outerBatchCount = 0;
		
		for(String clientResult : clientResults) {
			JSONArray doc;
			try {
				//Must ensure that JSONArray(String) constructor
				//is called. JSONArray(Object) behaves differently.
				doc = new JSONArray(clientResult);
			} catch(JSONException ex) {
				throw new RuntimeException("Error parsing client result", ex);
			}
			for ( int count = 0; count < doc.length(); count++ ) {
				try {
					String response = (String) doc.get( count );
					if ( response.startsWith( "\"" ) ) {
						// remove extraneous quote characters
						response = response.substring( 1, response.length() - 1 );
					}
					String type = RETURN_TYPES.get( queries.get( outerBatchCount++ ).getMethod() );
					// possible types are document, string, bool, int, long, void
					if ( type.equals( "default" ) ) {
						if ( response.matches( "\\{.*\\}" ) ) {
							result.add( new JSONObject( response.replace( "\\", "" ) ) );
						} else {
							result.add( new JSONArray( response ) );
						}
					} else if ( type.equals( "string" ) ) {
						result.add( response );
					} else if ( type.equals( "bool" ) ) {
						result.add( extractBoolean( response ) );
					} else if ( type.equals( "int" ) ) {
						result.add( extractInt( response ) );
					} else if ( type.equals( "long" ) ) {
						result.add( extractLong( response ) );
					} else {
						// void
						result.add( null );
					}
				}
				catch ( Exception ignored ) {
					ignored.printStackTrace();
					if ( result.size() < count + 1 ) {
						result.add( null );
					}
				}
			}
		}
		return result;
	}

	/**
	 * Return the object's 'friendsList' property. This method does not call the Facebook API server.
	 * 
	 * @return the friends-list stored in the API client.
	 */
	public JSONArray getCacheFriendsList() {
		return toFriendsGetResponse( client.getCacheFriendsList() );
	}

	/**
	 * Set/override the list of friends stored in the client.
	 * 
	 * @param friendsList
	 *            the new list to use.
	 */
	public void setCacheFriendsList( List<Long> ids ) {
		client.setCacheFriendsList( ids );
	}

	public static JSONArray toFriendsGetResponse( List<Long> ids ) {
		JSONArray out = new JSONArray();
		for ( Long id : ids ) {
			out.put( id );
		}
		return out;
	}
	
	@Deprecated
	public Object getResponsePOJO() {
		throw new RuntimeException("XML is required to convert the Facebook data to Java Objects via JAXB. JSON not supported.");
	}

	public String getRawResponse() {
		return client.getRawResponse();
	}

	/**
	 * @Deprecated Use FacebookJaxbRestClient.getJaxbContext() instead
	 */
	@Deprecated
	public JAXBContext getJaxbContext() {
		return FacebookJaxbRestClient.JAXB_CONTEXT;
	}
	
	/**
	 * @Deprecated Use FacebookJaxbRestClient.setJaxbContext(context) instead
	 */
	@Deprecated
	public void setJaxbContext( JAXBContext context ) {
		FacebookJaxbRestClient.JAXB_CONTEXT = context;
	}
}
