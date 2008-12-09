package com.google.code.facebookapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A FacebookRestClient that uses the JSON result format. This means results from calls to the Facebook API are returned as <a href="http://www.json.org/">JSON</a> and
 * transformed into Java <code>Object</code>'s.
 */
public class FacebookJsonRestClient extends SpecificReturnTypeAdapter implements IFacebookRestClient<Object> {

	protected static Log log = LogFactory.getLog( FacebookJsonRestClient.class );

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
	
	private ExtensibleClient client;
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
	public FacebookJsonRestClient( String apiKey, String secret ) {
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
	public FacebookJsonRestClient( String apiKey, String secret, int connectionTimeout ) {
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
	public FacebookJsonRestClient( String apiKey, String secret, String sessionKey ) {
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
	public FacebookJsonRestClient( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
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
	public FacebookJsonRestClient( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
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
	public FacebookJsonRestClient( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
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
	public FacebookJsonRestClient( URL serverUrl, String apiKey, String secret, String sessionKey ) {
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
	public FacebookJsonRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
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
	public FacebookJsonRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
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

	public Object admin_getDailyMetrics( Set<Metric> metrics, Date start, Date end ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
		return parseCallResult( rawResponse );
	}
	public Object admin_getDailyMetrics( Set<Metric> metrics, long start, long end ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
		return parseCallResult( rawResponse );
	}
	public Object admin_getMetrics( Set<Metric> metrics, Date start, Date end, long period ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
		return parseCallResult( rawResponse );
	}
	public Object admin_getMetrics( Set<Metric> metrics, long start, long end, long period ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
		return parseCallResult( rawResponse );
	}
	public Object application_getPublicInfo( Long applicationId, String applicationKey, String applicationCanvas ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.application_getPublicInfo( applicationId, applicationKey, applicationCanvas );
		return parseCallResult( rawResponse );
	}
	public Object application_getPublicInfoByApiKey( String applicationKey ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.application_getPublicInfoByApiKey( applicationKey );
		return parseCallResult( rawResponse );
	}
	public Object application_getPublicInfoByCanvasName( String applicationCanvas ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.application_getPublicInfoByCanvasName( applicationCanvas );
		return parseCallResult( rawResponse );
	}
	public Object application_getPublicInfoById( Long applicationId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.application_getPublicInfoById( applicationId );
		return parseCallResult( rawResponse );
	}
	public Object batch_run( String methods, boolean serial ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.batch_run( methods, serial );
		return parseCallResult( rawResponse );
	}
	public Object connect_registerUsers( Collection<Map<String,String>> accounts ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.connect_registerUsers( accounts );
		return parseCallResult( rawResponse );
	}
	public Object connect_unregisterUsers( Collection<String> email_hashes ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.connect_unregisterUsers( email_hashes );
		return parseCallResult( rawResponse );
	}
	public Object data_getAssociationDefinition( String associationName ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getAssociationDefinition( associationName );
		return parseCallResult( rawResponse );
	}
	public Object data_getAssociationDefinitions() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getAssociationDefinitions();
		return parseCallResult( rawResponse );
	}
	public JSONArray data_getCookies() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getCookies();
		return (JSONArray)parseCallResult( rawResponse );
	}
	public JSONArray data_getCookies( Long userId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getCookies( userId );
		return (JSONArray)parseCallResult( rawResponse );
	}
	public JSONArray data_getCookies( String name ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getCookies( name );
		return (JSONArray)parseCallResult( rawResponse );
	}
	public JSONArray data_getCookies( Long userId, CharSequence name ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getCookies( userId, name );
		return (JSONArray)parseCallResult( rawResponse );
	}
	public Object data_getObject( long objectId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getObject( objectId );
		return parseCallResult( rawResponse );
	}
	public Object data_getObjectProperty( long objectId, String propertyName ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getObjectProperty( objectId, propertyName );
		return parseCallResult( rawResponse );
	}
	public Object data_getObjectType( String objectType ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getObjectType( objectType );
		return parseCallResult( rawResponse );
	}
	public Object data_getObjectTypes() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getObjectTypes();
		return parseCallResult( rawResponse );
	}
	public Object data_getObjects( Collection<Long> objectIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getObjects( objectIds );
		return parseCallResult( rawResponse );
	}
	public JSONArray data_getUserPreferences() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.data_getUserPreferences();
		return (JSONArray)parseCallResult( rawResponse );
	}
	public Object events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.events_get( userId, eventIds, startTime, endTime );
		return parseCallResult( rawResponse );
	}
	public Object events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime, String rsvp_status ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.events_get( userId, eventIds, startTime, endTime, rsvp_status );
		return parseCallResult( rawResponse );
	}
	public Object events_getMembers( Long eventId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.events_getMembers( eventId );
		return parseCallResult( rawResponse );
	}
	public Object feed_getRegisteredTemplateBundleByID( Long id ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.feed_getRegisteredTemplateBundleByID( id );
		return parseCallResult( rawResponse );
	}
	public Object feed_getRegisteredTemplateBundles() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.feed_getRegisteredTemplateBundles();
		return parseCallResult( rawResponse );
	}
	public Object fql_query( CharSequence query ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.fql_query( query );
		return parseCallResult( rawResponse );
	}
	public Object friends_areFriends( long userId1, long userId2 ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.friends_areFriends( userId1, userId2 );
		return parseCallResult( rawResponse );
	}
	public Object friends_areFriends( Collection<Long> userIds1, Collection<Long> userIds2 ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.friends_areFriends( userIds1, userIds2 );
		return parseCallResult( rawResponse );
	}
	public JSONArray friends_get( Long uid ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.friends_get( uid );
		return (JSONArray)parseCallResult( rawResponse );
	}
	public JSONArray friends_getAppUsers() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.friends_getAppUsers();
		return (JSONArray)parseCallResult( rawResponse );
	}
	public JSONArray friends_getList( Long friendListId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.friends_getList( friendListId );
		return (JSONArray)parseCallResult( rawResponse );
	}
	public Object friends_getLists() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.friends_getLists();
		return parseCallResult( rawResponse );
	}
	public JSONArray groups_get( Long userId, Collection<Long> groupIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.groups_get( userId, groupIds );
		return (JSONArray)parseCallResult( rawResponse );
	}
	public JSONArray groups_getMembers( Number groupId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.groups_getMembers( groupId );
		return (JSONArray)parseCallResult( rawResponse );
	}
	public Object marketplace_getCategoriesObject() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.marketplace_getCategoriesObject();
		return parseCallResult( rawResponse );
	}
	public Object marketplace_getListings( Collection<Long> listingIds, Collection<Long> userIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.marketplace_getListings( listingIds, userIds );
		return parseCallResult( rawResponse );
	}
	public Object marketplace_getSubCategories( CharSequence category ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.marketplace_getSubCategories( category );
		return parseCallResult( rawResponse );
	}
	public Object marketplace_search( CharSequence category, CharSequence subCategory, CharSequence query ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.marketplace_search( category, subCategory, query );
		return parseCallResult( rawResponse );
	}
	public Object notifications_get() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.notifications_get();
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.notifications_sendEmail( recipients, subject, email, fbml );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.notifications_sendEmailToCurrentUser( subject, email, fbml );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.notifications_sendFbmlEmail( recipients, subject, fbml );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.notifications_sendFbmlEmailToCurrentUser( subject, fbml );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.notifications_sendTextEmail( recipients, subject, email );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.notifications_sendTextEmailToCurrentUser( subject, email );
		return parseCallResult( rawResponse );
	}
	public Object pages_getInfo( Collection<Long> pageIds, EnumSet<PageProfileField> fields ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.pages_getInfo( pageIds, fields );
		return parseCallResult( rawResponse );
	}
	public Object pages_getInfo( Collection<Long> pageIds, Set<CharSequence> fields ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.pages_getInfo( pageIds, fields );
		return parseCallResult( rawResponse );
	}
	public Object pages_getInfo( Long userId, EnumSet<PageProfileField> fields ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.pages_getInfo( userId, fields );
		return parseCallResult( rawResponse );
	}
	public Object pages_getInfo( Long userId, Set<CharSequence> fields ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.pages_getInfo( userId, fields );
		return parseCallResult( rawResponse );
	}
	public Object permissions_checkAvailableApiAccess( String apiKey ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.permissions_checkAvailableApiAccess( apiKey );
		return parseCallResult( rawResponse );
	}
	public Object permissions_checkGrantedApiAccess( String apiKey ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.permissions_checkGrantedApiAccess( apiKey );
		return parseCallResult( rawResponse );
	}
	public Object photos_addTags( Long photoId, Collection<PhotoTag> tags ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_addTags( photoId, tags );
		return parseCallResult( rawResponse );
	}
	public Object photos_addTags( Long photoId, Collection<PhotoTag> tags, Long userId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_addTags( photoId, tags, userId );
		return parseCallResult( rawResponse );
	}
	public Object photos_createAlbum( String albumName ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_createAlbum( albumName );
		return parseCallResult( rawResponse );
	}
	public Object photos_createAlbum( String name, String description, String location ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_createAlbum( name, description, location );
		return parseCallResult( rawResponse );
	}
	public Object photos_createAlbum( String albumName, Long userId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_createAlbum( albumName, userId );
		return parseCallResult( rawResponse );
	}
	public Object photos_createAlbum( String name, String description, String location, Long userId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_createAlbum( name, description, location, userId );
		return parseCallResult( rawResponse );
	}
	public Object photos_get( Long subjId, Long albumId, Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_get( subjId, albumId, photoIds );
		return parseCallResult( rawResponse );
	}
	public Object photos_get( Long subjId, Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_get( subjId, photoIds );
		return parseCallResult( rawResponse );
	}
	public Object photos_get( Long subjId, Long albumId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_get( subjId, albumId );
		return parseCallResult( rawResponse );
	}
	public Object photos_get( Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_get( photoIds );
		return parseCallResult( rawResponse );
	}
	public Object photos_get( Long subjId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_get( subjId );
		return parseCallResult( rawResponse );
	}
	public Object photos_getAlbums( Long userId, Collection<Long> albumIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_getAlbums( userId, albumIds );
		return parseCallResult( rawResponse );
	}
	public Object photos_getAlbums( Long userId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_getAlbums( userId );
		return parseCallResult( rawResponse );
	}
	public Object photos_getAlbums( Collection<Long> albumIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_getAlbums( albumIds );
		return parseCallResult( rawResponse );
	}
	public Object photos_getByAlbum( Long albumId, Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_getByAlbum( albumId, photoIds );
		return parseCallResult( rawResponse );
	}
	public Object photos_getByAlbum( Long albumId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_getByAlbum( albumId );
		return parseCallResult( rawResponse );
	}
	public Object photos_getTags( Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_getTags( photoIds );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( File photo ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( photo );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( File photo, String caption ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( photo, caption );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( File photo, Long albumId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( photo, albumId );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( File photo, String caption, Long albumId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( photo, caption, albumId );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( Long userId, File photo ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( userId, photo );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( Long userId, File photo, String caption ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( userId, photo, caption );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( Long userId, File photo, Long albumId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( userId, photo, albumId );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( Long userId, File photo, String caption, Long albumId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( userId, photo, caption, albumId );
		return parseCallResult( rawResponse );
	}
	public Object photos_upload( Long userId, String caption, Long albumId, String fileName, InputStream fileStream ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.photos_upload( userId, caption, albumId, fileName, fileStream );
		return parseCallResult( rawResponse );
	}
	public String profile_getFBML() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.profile_getFBML();
		return (String)parseCallResult( rawResponse );
	}
	public String profile_getFBML( Long userId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.profile_getFBML( userId );
		return (String)parseCallResult( rawResponse );
	}
	public String profile_getFBML( int type ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.profile_getFBML( type );
		return (String)parseCallResult( rawResponse );
	}
	public String profile_getFBML( int type, Long userId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.profile_getFBML( type, userId );
		return (String)parseCallResult( rawResponse );
	}
	public Object profile_getInfo( Long userId ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.profile_getInfo( userId );
		return parseCallResult( rawResponse );
	}
	public Object profile_getInfoOptions( String field ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.profile_getInfoOptions( field );
		return parseCallResult( rawResponse );
	}
	public Object users_getInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.users_getInfo( userIds, fields );
		return parseCallResult( rawResponse );
	}
	public Object users_getInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.users_getInfo( userIds, fields );
		return parseCallResult( rawResponse );
	}
	public Object users_getStandardInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.users_getStandardInfo( userIds, fields );
		return parseCallResult( rawResponse );
	}
	public Object users_getStandardInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.users_getStandardInfo( userIds, fields );
		return parseCallResult( rawResponse );
	}
	public JSONArray friends_get() throws FacebookException {
		client.setResponseFormat( "json" );
		Object rawResponse = client.friends_get();
		return (JSONArray)parseCallResult( rawResponse );
	}
}
