/*
 +---------------------------------------------------------------------------+
 | Facebook Development Platform Java Client                                 |
 +---------------------------------------------------------------------------+
 | Copyright (c) 2007 Facebook, Inc.                                         |
 | All rights reserved.                                                      |
 |                                                                           |
 | Redistribution and use in source and binary forms, with or without        |
 | modification, are permitted provided that the following conditions        |
 | are met:                                                                  |
 |                                                                           |
 | 1. Redistributions of source code must retain the above copyright         |
 |    notice, this list of conditions and the following disclaimer.          |
 | 2. Redistributions in binary form must reproduce the above copyright      |
 |    notice, this list of conditions and the following disclaimer in the    |
 |    documentation and/or other materials provided with the distribution.   |
 |                                                                           |
 | THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR      |
 | IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES |
 | OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.   |
 | IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,          |
 | INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT  |
 | NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, |
 | DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY     |
 | THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT       |
 | (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF  |
 | THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.         |
 +---------------------------------------------------------------------------+
 | For help with this library, contact developers-help@facebook.com          |
 +---------------------------------------------------------------------------+
 */

package com.google.code.facebookapi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.code.facebookapi.schema.Listing;
import com.google.code.facebookapi.schema.MarketplaceGetCategoriesResponse;
import com.google.code.facebookapi.schema.MarketplaceGetListingsResponse;
import com.google.code.facebookapi.schema.MarketplaceGetSubCategoriesResponse;
import com.google.code.facebookapi.schema.MarketplaceSearchResponse;

/**
 * A FacebookRestClient that uses the XML result format. This means results from calls to the Facebook API are returned as XML and transformed into instances of Document.
 * 
 * Allocate an instance of this class to make Facebook API requests.
 * 
 * @deprecated this is provided for legacy support only. Please use FacebookXmlRestClient instead if you want to use the Facebook Platform XML API.
 */
@Deprecated
public class FacebookRestClient implements IFacebookRestClient<Document> {

	protected static Log log = LogFactory.getLog( FacebookRestClient.class );

	/**
	 * API version to request when making calls to the server
	 */
	public static final String TARGET_API_VERSION = "1.0";
	/**
	 * Flag indicating an erroneous response
	 */
	public static final String ERROR_TAG = "error_response";
	/**
	 * Facebook API server, part 1
	 */
	public static final String FB_SERVER = "api.facebook.com/restserver.php";
	/**
	 * Facebook API server, part 2a
	 */
	public static final String SERVER_ADDR = "http://" + FB_SERVER;
	/**
	 * Facebook API server, part 2b
	 */
	public static final String HTTPS_SERVER_ADDR = "https://" + FB_SERVER;
	/**
	 * Facebook API server, part 3a
	 */
	public static URL SERVER_URL = null;
	/**
	 * Facebook API server, part 3b
	 */
	public static URL HTTPS_SERVER_URL = null;
	protected static JAXBContext JAXB_CONTEXT;
	static {
		try {
			JAXB_CONTEXT = JAXBContext.newInstance( "com.google.code.facebookapi.schema" );
			SERVER_URL = new URL( SERVER_ADDR );
			HTTPS_SERVER_URL = new URL( HTTPS_SERVER_ADDR );
		}
		catch ( MalformedURLException ex ) {
			log.error( "MalformedURLException: " + ex.getMessage(), ex );
			System.exit( 1 );
		}
		catch ( JAXBException ex ) {
			JAXB_CONTEXT = null;
			log.error( "Could not get JAXB context:  " + ex.getMessage(), ex );
		}
	}

	// used so that executeBatch can return the correct types in its list, without killing efficiency.
	private static final Map<FacebookMethod,String> RETURN_TYPES;
	static {
		RETURN_TYPES = new HashMap<FacebookMethod,String>();
		Method[] candidates = FacebookRestClient.class.getMethods();
		// this loop is inefficient, but it only executes once per JVM, so it doesn't really matter
		for ( FacebookMethod method : EnumSet.allOf( FacebookMethod.class ) ) {
			String name = method.methodName();
			name = name.substring( name.indexOf( "." ) + 1 );
			name = name.replace( ".", "_" );
			for ( Method candidate : candidates ) {
				if ( candidate.getName().equalsIgnoreCase( name ) ) {
					String typeName = candidate.getReturnType().getName().toLowerCase();
					// possible types are Document, String, Boolean, Integer, Long, void
					if ( typeName.indexOf( "document" ) != -1 ) {
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
							|| ( typeName.indexOf( "map" ) != -1 ) || ( typeName.indexOf( "object" ) != -1 ) ) {
						// we don't autobox these for now, the user can parse them on their own
						RETURN_TYPES.put( method, "default" );
					} else {
						RETURN_TYPES.put( method, "void" );
					}
					break;
				}
			}
		}
	}

	protected final String _secret;
	protected final String _apiKey;
	protected URL _serverUrl;
	protected boolean namespaceAware = true;
	protected int _timeout;
	protected int _readTimeout;

	protected boolean _isDesktop = false;

	protected String cacheSessionKey; // filled in when session is established
	protected Long cacheUserId;
	protected Long cacheSessionExpires; // also filled in when session is established
	protected String cacheSessionSecret; // only used for desktop apps

	protected String rawResponse;
	protected boolean batchMode;
	protected List<BatchQuery> queries;

	protected String permissionsApiKey = null;

	protected Document cacheFriendsList; // to save making the friends.get api call, this will get prepopulated on canvas pages
	protected Boolean cacheAppAdded; // to save making the users.isAppAdded api call, this will get prepopulated on canvas pages


	/**
	 * number of params that the client automatically appends to every API call
	 */
	public static final int NUM_AUTOAPPENDED_PARAMS = 6;
	/** @deprecated DEBUG flags will be removed, logging controlled via commons-logging now */
	@Deprecated
	protected Boolean _debug = null;

	protected File _uploadFile = null;
	protected static final String CRLF = "\r\n";
	protected static final String PREF = "--";
	protected static final int UPLOAD_BUFFER_SIZE = 512;

	public static final String MARKETPLACE_STATUS_DEFAULT = "DEFAULT";
	public static final String MARKETPLACE_STATUS_NOT_SUCCESS = "NOT_SUCCESS";
	public static final String MARKETPLACE_STATUS_SUCCESS = "SUCCESS";

	/**
	 * Constructor
	 * 
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 */
	public FacebookRestClient( String apiKey, String secret ) {
		this( SERVER_URL, apiKey, secret, null );
	}

	/**
	 * Constructor
	 * 
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookRestClient( String apiKey, String secret, int connectionTimeout ) {
		this( SERVER_URL, apiKey, secret, null, connectionTimeout );
	}

	/**
	 * Constructor
	 * 
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 * @param sessionKey
	 *            the session-id to use
	 */
	public FacebookRestClient( String apiKey, String secret, String sessionKey ) {
		this( SERVER_URL, apiKey, secret, sessionKey );
	}

	/**
	 * Constructor
	 * 
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookRestClient( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( SERVER_URL, apiKey, secret, sessionKey, connectionTimeout );
	}

	/**
	 * Constructor
	 * 
	 * @param serverAddr
	 *            the URL of the Facebook API server to use, allows overriding of the default API server.
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 * @param sessionKey
	 *            the session-id to use
	 * 
	 * @throws MalformedURLException
	 *             if the specified serverAddr is invalid
	 */
	public FacebookRestClient( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		this( new URL( serverAddr ), apiKey, secret, sessionKey );
	}

	/**
	 * Constructor
	 * 
	 * @param serverAddr
	 *            the URL of the Facebook API server to use, allows overriding of the default API server.
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 * 
	 * @throws MalformedURLException
	 *             if the specified serverAddr is invalid
	 */
	public FacebookRestClient( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		this( new URL( serverAddr ), apiKey, secret, sessionKey, connectionTimeout );
	}

	/**
	 * Constructor
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use, allows overriding of the default API server.
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 * @param sessionKey
	 *            the session-id to use
	 */
	public FacebookRestClient( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		cacheSessionKey = sessionKey;
		_apiKey = apiKey;
		_secret = secret;
		_serverUrl = ( null != serverUrl ) ? serverUrl : SERVER_URL;
		_timeout = -1;
		_readTimeout = -1;
		batchMode = false;
		queries = new ArrayList<BatchQuery>();
	}

	public boolean isNamespaceAware() {
		return namespaceAware;
	}

	public void setNamespaceAware( boolean v ) {
		this.namespaceAware = v;
	}

	public void beginPermissionsMode( String apiKey ) {
		this.permissionsApiKey = apiKey;
	}

	public void endPermissionsMode() {
		this.permissionsApiKey = null;
	}

	public JAXBContext getJaxbContext() {
		return JAXB_CONTEXT;
	}

	public void setJaxbContext( JAXBContext context ) {
		JAXB_CONTEXT = context;
	}

	/**
	 * Return the object's session-key property. This method does not call the Facebook API server.
	 * 
	 * @return the session-key stored in the API client.
	 */
	@Deprecated
	public String _getSessionKey() {
		return cacheSessionKey;
	}

	/**
	 * Set/override the session-key used by the client.
	 * 
	 * @param key
	 *            the new key to use.
	 */
	@Deprecated
	public void _setSessionKey( String key ) {
		cacheSessionKey = key;
	}

	/**
	 * Return the object's 'expires' property. This method does not call the Facebook API server.
	 * 
	 * @return the expiration value stored in the API client.
	 */
	@Deprecated
	public Long _getExpires() {
		return cacheSessionExpires;
	}

	/**
	 * Set/override the session expiration timestamp used by the client.
	 * 
	 * @param _expires
	 *            the new timestamp to use.
	 */
	@Deprecated
	public void _setExpires( Long _expires ) {
		this.cacheSessionExpires = _expires;
	}

	/**
	 * Return the object's user-id property. This method does not call the Facebook API server.
	 * 
	 * @return the user-id stored in the API client.
	 */
	@Deprecated
	public Long _getUserId() {
		return cacheUserId;
	}

	/**
	 * Set/override the user-id stored in the client.
	 * 
	 * @param id
	 *            the new user-id to use.
	 */
	@Deprecated
	public void _setUserId( Long id ) {
		cacheUserId = id;
	}

	public String getCacheSessionSecret() {
		return cacheSessionSecret;
	}

	public void setCacheSessionSecret( String cacheSessionSecret ) {
		this.cacheSessionSecret = cacheSessionSecret;
	}

	public void setCacheSession( String cacheSessionKey, Long cacheUserId, Long cacheSessionExpires ) {
		setCacheSessionKey( cacheSessionKey );
		setCacheUserId( cacheUserId );
		setCacheSessionExpires( cacheSessionExpires );
	}

	public Long getCacheSessionExpires() {
		return cacheSessionExpires;
	}

	public void setCacheSessionExpires( Long cacheSessionExpires ) {
		this.cacheSessionExpires = cacheSessionExpires;
	}

	public String getCacheSessionKey() {
		return cacheSessionKey;
	}

	public void setCacheSessionKey( String cacheSessionKey ) {
		this.cacheSessionKey = cacheSessionKey;
	}

	public Long getCacheUserId() {
		return cacheUserId;
	}

	public void setCacheUserId( Long cacheUserId ) {
		this.cacheUserId = cacheUserId;
	}

	/**
	 * Return the object's 'friendsList' property. This method does not call the Facebook API server.
	 * 
	 * @return the friends-list stored in the API client.
	 */
	public Document getCacheFriendsList() {
		return cacheFriendsList;
	}

	/**
	 * Set/override the list of friends stored in the client.
	 * 
	 * @param friendsList
	 *            the new list to use.
	 */
	public void setCacheFriendsList( List<Long> ids ) {
		this.cacheFriendsList = toFriendsGetResponse( ids );
	}

	public static Document toFriendsGetResponse( List<Long> ids ) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElementNS( "http://api.facebook.com/1.0/", "friends_get_response" );
			root.setAttributeNS( "http://api.facebook.com/1.0/", "friends_get_response", "http://api.facebook.com/1.0/ http://api.facebook.com/1.0/facebook.xsd" );
			root.setAttribute( "list", "true" );
			for ( Long id : ids ) {
				Element uid = doc.createElement( "uid" );
				uid.appendChild( doc.createTextNode( Long.toString( id ) ) );
				root.appendChild( uid );
			}
			doc.appendChild( root );
			return doc;
		}
		catch ( ParserConfigurationException ex ) {
			throw new RuntimeException( ex );
		}
	}

	/**
	 * Constructor
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use, allows overriding of the default API server.
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 );
		_timeout = connectionTimeout;
	}

	/**
	 * Constructor
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use, allows overriding of the default API server.
	 * @param apiKey
	 *            the developer's API key
	 * @param secret
	 *            the developer's secret key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 * @param readTimeout
	 *            the read timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
		this( serverUrl, apiKey, secret, sessionKey );
		_timeout = connectionTimeout;
		_readTimeout = readTimeout;
	}

	/**
	 * The response format in which results to FacebookMethod calls are returned
	 * 
	 * @return the format: either XML, JSON, or null (API default)
	 */
	public String getResponseFormat() {
		return "xml";
	}

	/**
	 * Set global debugging on.
	 * 
	 * @param isDebug
	 *            true to enable debugging false to disable debugging
	 * @deprecated DEBUG flags will be removed, logging controlled via commons-logging now
	 */
	@Deprecated
	public static void setDebugAll( boolean isDebug ) {
		ExtensibleClient.DEBUG = isDebug;
	}

	/**
	 * Set debugging on for this instance only.
	 * 
	 * @param isDebug
	 *            true to enable debugging false to disable debugging
	 * @deprecated DEBUG flags will be removed, logging controlled via commons-logging now
	 */
	@Deprecated
	public void setDebug( boolean isDebug ) {
		_debug = isDebug;
	}

	/**
	 * Check to see if debug mode is enabled.
	 * 
	 * @return true if debugging is enabled false otherwise
	 * @deprecated DEBUG flags will be removed, logging controlled via commons-logging now
	 */
	@Deprecated
	public boolean isDebug() {
		return ( null == _debug ) ? ExtensibleClient.DEBUG : _debug.booleanValue();
	}

	/**
	 * Check to see if the client is running in desktop mode.
	 * 
	 * @return true if the client is running in desktop mode false otherwise
	 */
	public boolean isDesktop() {
		return this._isDesktop;
	}

	/**
	 * Enable/disable desktop mode.
	 * 
	 * @param isDesktop
	 *            true to enable desktop application mode false to disable desktop application mode
	 */
	public void setIsDesktop( boolean isDesktop ) {
		this._isDesktop = isDesktop;
	}

	/**
	 * Prints out the DOM tree.
	 * 
	 * @param n
	 *            the parent node to start printing from
	 * @param prefix
	 *            string to append to output, should not be null
	 */
	public void printDom( Node n, String prefix ) {
		if ( log.isDebugEnabled() ) {
			StringBuilder sb = new StringBuilder( "\n" );
			ExtensibleClient.printDom( n, prefix, sb );
			log.debug( sb.toString() );
		}
	}

	private static CharSequence delimit( Collection<?> iterable ) {
		// could add a thread-safe version that uses StringBuffer as well
		if ( iterable == null || iterable.isEmpty() ) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		boolean notFirst = false;
		for ( Object item : iterable ) {
			if ( notFirst ) {
				buffer.append( "," );
			} else {
				notFirst = true;
			}
			buffer.append( item.toString() );
		}
		return buffer;
	}

	protected static CharSequence delimit( Collection<Map.Entry<String,String>> entries, String delimiter, String equals, boolean doEncode ) {
		if ( entries == null || entries.isEmpty() ) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		boolean notFirst = false;
		for ( Map.Entry<String,String> entry : entries ) {
			if ( notFirst ) {
				buffer.append( delimiter );
			} else {
				notFirst = true;
			}
			CharSequence value = entry.getValue();
			buffer.append( entry.getKey() ).append( equals ).append( doEncode ? encode( value ) : value );
		}
		return buffer;
	}

	/**
	 * Call the specified method, with the given parameters, and return a DOM tree with the results.
	 * 
	 * @param method
	 *            the fieldName of the method
	 * @param paramPairs
	 *            a list of arguments to the method
	 * @throws Exception
	 *             with a description of any errors given to us by the server.
	 */
	protected Document callMethod( IFacebookMethod method, Pair<String,CharSequence>... paramPairs ) throws FacebookException, IOException {
		return callMethod( method, Arrays.asList( paramPairs ) );
	}

	/**
	 * Starts a batch of queries. Any API calls made after invoking 'beginBatch' will be deferred until the next time you call 'executeBatch', at which time they will be
	 * processed as a batch query. All API calls made in the interim will return null as their result.
	 */
	public void beginBatch() {
		this.batchMode = true;
		this.queries = new ArrayList<BatchQuery>();
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
	public List<? extends Object> executeBatch( boolean serial ) throws FacebookException, IOException {
		this.batchMode = false;
		List<Object> result = new ArrayList<Object>();
		List<BatchQuery> buffer = new ArrayList<BatchQuery>();
		while ( !this.queries.isEmpty() ) {
			buffer.add( this.queries.remove( 0 ) );
			if ( ( buffer.size() == 15 ) || ( this.queries.isEmpty() ) ) {
				// we can only actually batch up to 15 at once
				Document doc = this.batch_run( this.encodeMethods( buffer ), serial );
				NodeList responses = doc.getElementsByTagName( "batch_run_response_elt" );
				for ( int count = 0; count < responses.getLength(); count++ ) {
					String response = extractString( responses.item( count ) );
					try {
						DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document respDoc = builder.parse( new ByteArrayInputStream( response.getBytes( "UTF-8" ) ) );
						String type = RETURN_TYPES.get( buffer.get( count ).getMethod() );
						// possible types are document, string, bool, int, long, void
						if ( type.equals( "default" ) ) {
							result.add( respDoc );
						} else if ( type.equals( "string" ) ) {
							result.add( extractString( respDoc ) );
						} else if ( type.equals( "bool" ) ) {
							result.add( extractBoolean( respDoc ) );
						} else if ( type.equals( "int" ) ) {
							result.add( extractInt( respDoc ) );
						} else if ( type.equals( "long" ) ) {
							result.add( extractLong( respDoc ) );
						} else {
							// void
							result.add( null );
						}
					}
					catch ( Exception ignored ) {
						if ( result.size() < count + 1 ) {
							result.add( null );
						}
					}
				}
			}
		}
		return result;
	}

	private String encodeMethods( List<BatchQuery> queries ) throws FacebookException {
		JSONArray result = new JSONArray();
		for ( BatchQuery query : queries ) {
			if ( query.getMethod().takesFile() ) {
				throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "File upload API calls cannot be batched:  " + query.getMethod().methodName() );
			}
			result.put( delimit( query.getParams().entrySet(), "&", "=", true ) );
		}
		return result.toString();
	}

	/**
	 * Executes a batch of queries. It is your responsibility to encode the method feed correctly. It is not recommended that you call this method directly. Instead use
	 * 'beginBatch' and 'executeBatch', which will take care of the hard parts for you.
	 * 
	 * @param methods
	 *            A JSON encoded array of strings. Each element in the array should contain the full parameters for a method, including method name, sig, etc. Currently,
	 *            there is a maximum limit of 15 elements in the array.
	 * @param serial
	 *            An optional parameter to indicate whether the methods in the method_feed must be executed in order. The default value is false.
	 * 
	 * @return a result containing the response to each individual query in the batch.
	 */
	public Document batch_run( String methods, boolean serial ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "method_feed", methods ) );
		if ( serial ) {
			params.add( new Pair<String,CharSequence>( "serial_only", "1" ) );
		}
		return callMethod( FacebookMethod.BATCH_RUN, params );
	}

	/**
	 * Call the specified method, with the given parameters, and return a DOM tree with the results.
	 * 
	 * @param method
	 *            the fieldName of the method
	 * @param paramPairs
	 *            a list of arguments to the method
	 * @throws Exception
	 *             with a description of any errors given to us by the server.
	 */
	protected Document callMethod( IFacebookMethod method, Collection<Pair<String,CharSequence>> paramPairs ) throws FacebookException, IOException {
		this.rawResponse = null;
		Map<String,String> params = new TreeMap<String,String>();

		if ( this.permissionsApiKey != null ) {
			params.put( "call_as_apikey", permissionsApiKey );
		}
		params.put( "method", method.methodName() );
		params.put( "api_key", _apiKey );
		params.put( "v", TARGET_API_VERSION );

		params.put( "call_id", Long.toString( System.currentTimeMillis() ) );
		boolean includeSession = method.requiresSession() && cacheSessionKey != null;
		if ( includeSession ) {
			params.put( "session_key", cacheSessionKey );
		}
		CharSequence oldVal;
		for ( Pair<String,CharSequence> p : paramPairs ) {
			oldVal = params.put( p.first, FacebookSignatureUtil.toString( p.second ) );
			if ( oldVal != null ) {
				log.warn( "For parameter " + p.first + ", overwrote old value " + oldVal + " with new value " + p.second + "." );
			}
		}

		assert ( !params.containsKey( "sig" ) );
		String signature = generateSignature( FacebookSignatureUtil.convert( params.entrySet() ), includeSession );
		params.put( "sig", signature );

		if ( this.batchMode ) {
			// if we are running in bach mode, don't actually execute the query now, just add it to the list
			boolean addToBatch = true;
			if ( method.methodName().equals( FacebookMethod.USERS_GET_LOGGED_IN_USER.methodName() ) ) {
				Exception trace = new Exception();
				StackTraceElement[] traceElems = trace.getStackTrace();
				int index = 0;
				for ( StackTraceElement elem : traceElems ) {
					if ( elem.getMethodName().indexOf( "_" ) != -1 ) {
						StackTraceElement caller = traceElems[index + 1];
						if ( ( caller.getClassName().equals( this.getClass().getName() ) ) && ( !caller.getMethodName().startsWith( "auth_" ) ) ) {
							addToBatch = false;
						}
						break;
					}
					index++ ;
				}
			}
			if ( addToBatch ) {
				this.queries.add( new BatchQuery( method, params ) );
			}
			return null;
		}

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware( namespaceAware );
			DocumentBuilder builder = factory.newDocumentBuilder();
			boolean doHttps = isDesktop() && FacebookMethod.AUTH_GET_SESSION.equals( method );

			boolean doEncode = true;
			InputStream data = method.takesFile() ? postFileRequest( method.methodName(), params, doEncode ) : postRequest( method.methodName(), params, doHttps,
					doEncode );

			BufferedReader in = new BufferedReader( new InputStreamReader( data, "UTF-8" ) );
			StringBuilder buffer = new StringBuilder();
			String line;
			boolean insideTagBody = false;
			while ( ( line = in.readLine() ) != null ) {
				/*
				 * is the last char a close ('>')? if not, we need to add a comma to the string as FB (unfortunately) lets people enter profile information and use hard
				 * returns, which are stripped out For example, this is a "valid" XML from FB: <?xml version="1.0" encoding="UTF-8"?> <users_getInfo_response
				 * xmlns="http://api.facebook.com/1.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://api.facebook.com/1.0/
				 * http://api.facebook.com/1.0/facebook.xsd" list="true"> <user> <uid>12345678</uid> <first_name>Bob</first_name> <music>My Morning Jacket, Libertines
				 * The Clash</music> </user> </users_getInfo_response>
				 * 
				 * When the buffer is built, <music> ends up like this: "My Morning Jacket, LibertinesTheClash" which makes it impossible to parse as the delimiters are
				 * destroyed
				 */
				if ( method != FacebookMethod.BATCH_RUN ) {
					if ( line.trim().startsWith( "<" ) && line.contains( ">" ) ) {
						insideTagBody = true;
					}
					if ( line.trim().endsWith( ">" ) ) {
						insideTagBody = false;
					}
					if ( insideTagBody ) {
						line += ",";
					}
				}
				buffer.append( line );
			}

			String xmlResp = buffer.toString();
			this.rawResponse = xmlResp;

			Document doc = builder.parse( new ByteArrayInputStream( xmlResp.getBytes( "UTF-8" ) ) );
			doc.normalizeDocument();
			stripEmptyTextNodes( doc );

			printDom( doc, method.methodName() + "| " ); // TEST
			NodeList errors = doc.getElementsByTagName( ERROR_TAG );
			if ( errors.getLength() > 0 ) {
				int errorCode = Integer.parseInt( errors.item( 0 ).getFirstChild().getFirstChild().getTextContent() );
				String message = errors.item( 0 ).getFirstChild().getNextSibling().getTextContent();
				// FIXME: additional printing done for debugging only
				if ( log.isWarnEnabled() ) {
					StringBuilder sb = new StringBuilder( "Facebook returns error code " + errorCode + "\n" );
					for ( Map.Entry<String,String> entry : params.entrySet() ) {
						sb.append( "  - " + entry.getKey() + " -> " + entry.getValue() + "\n" );
					}
					log.warn( sb.toString() );
				}
				throw new FacebookException( errorCode, message );
			}
			return doc;
		}
		catch ( ParserConfigurationException ex ) {
			throw new RuntimeException( "Trouble configuring XML Parser", ex );
		}
		catch ( SAXException ex ) {
			throw new RuntimeException( "Trouble parsing XML from facebook", ex );
		}
	}

	/**
	 * Returns a string representation for the last API response recieved from Facebook, exactly as sent by the API server.
	 * 
	 * Note that calling this method consumes the data held in the internal buffer, and thus it may only be called once per API call.
	 * 
	 * @return a String representation of the last API response sent by Facebook
	 */
	public String getRawResponse() {
		String result = this.rawResponse;
		this.rawResponse = null;
		return result;
	}

	/**
	 * Hack...since DOM reads newlines as textnodes we want to strip out those nodes to make it easier to use the tree.
	 */
	private static void stripEmptyTextNodes( Node n ) {
		NodeList children = n.getChildNodes();
		int length = children.getLength();
		for ( int i = 0; i < length; i++ ) {
			Node c = children.item( i );
			if ( !c.hasChildNodes() && c.getNodeType() == Node.TEXT_NODE && c.getTextContent().trim().length() == 0 ) {
				n.removeChild( c );
				i-- ;
				length-- ;
				children = n.getChildNodes();
			} else {
				stripEmptyTextNodes( c );
			}
		}
	}

	private String generateSignature( List<String> params, boolean requiresSession ) {
		String secret = ( isDesktop() && requiresSession ) ? this.cacheSessionSecret : this._secret;
		return FacebookSignatureUtil.generateSignature( params, secret );
	}

	private static String encode( CharSequence target ) {
		String result = ( target != null ) ? target.toString() : "";
		try {
			result = URLEncoder.encode( result, "UTF8" );
		}
		catch ( UnsupportedEncodingException ex ) {
			log.error( "Unsuccessful attempt to encode '" + result + "' into UTF8", ex );
		}
		return result;
	}

	private InputStream postRequest( CharSequence method, Map<String,String> params, boolean doHttps, boolean doEncode ) throws IOException {
		CharSequence buffer = ( null == params ) ? "" : delimit( params.entrySet(), "&", "=", doEncode );
		URL serverUrl = ( doHttps ) ? HTTPS_SERVER_URL : _serverUrl;
		if ( log.isDebugEnabled() ) {
			StringBuilder sb = new StringBuilder();
			sb.append( method );
			sb.append( " POST: " );
			sb.append( serverUrl.toString() );
			sb.append( "?" );
			sb.append( buffer );
			log.debug( sb.toString() );
		}

		HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
		if ( this._timeout != -1 ) {
			conn.setConnectTimeout( this._timeout );
		}
		if ( this._readTimeout != -1 ) {
			conn.setReadTimeout( this._readTimeout );
		}
		try {
			conn.setRequestMethod( "POST" );
		}
		catch ( ProtocolException ex ) {
			log.error( "Exception: " + ex.getMessage(), ex );
		}
		conn.setDoOutput( true );
		conn.connect();
		conn.getOutputStream().write( buffer.toString().getBytes() );

		return conn.getInputStream();
	}

	/**
	 * Sets the FBML for a user's profile, including the content for both the profile box and the profile actions.
	 * 
	 * @param userId -
	 *            the user whose profile FBML to set
	 * @param fbmlMarkup -
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @return a boolean indicating whether the FBML was successfully set
	 * 
	 * @deprecated Facebook will remove support for this version of the API call on 1/17/2008, please use the alternate version instead.
	 */
	@Deprecated
	public boolean profile_setFBML( CharSequence fbmlMarkup, Long userId ) throws FacebookException, IOException {

		return extractBoolean( callMethod( FacebookMethod.PROFILE_SET_FBML, new Pair<String,CharSequence>( "uid", Long.toString( userId ) ),
				new Pair<String,CharSequence>( "markup", fbmlMarkup ) ) );

	}

	/**
	 * Gets the FBML for a user's profile, including the content for both the profile box and the profile actions.
	 * 
	 * @param userId -
	 *            the user whose profile FBML to set
	 * @return a Document containing FBML markup
	 */
	public Document profile_getFBML( Long userId ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PROFILE_GET_FBML_NOSESSION, new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );

	}

	/**
	 * Recaches the referenced url.
	 * 
	 * @param url
	 *            string representing the URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshRefUrl( String url ) throws FacebookException, IOException {
		return fbml_refreshRefUrl( new URL( url ) );
	}

	/**
	 * Recaches the referenced url.
	 * 
	 * @param url
	 *            the URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshRefUrl( URL url ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.FBML_REFRESH_REF_URL, new Pair<String,CharSequence>( "url", url.toString() ) ) );
	}

	/**
	 * Recaches the image with the specified imageUrl.
	 * 
	 * @param imageUrl
	 *            String representing the image URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshImgSrc( String imageUrl ) throws FacebookException, IOException {
		return fbml_refreshImgSrc( new URL( imageUrl ) );
	}

	/**
	 * Recaches the image with the specified imageUrl.
	 * 
	 * @param imageUrl
	 *            the image URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshImgSrc( URL imageUrl ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.FBML_REFRESH_IMG_SRC, new Pair<String,CharSequence>( "url", imageUrl.toString() ) ) );
	}

	/**
	 * Publishes a templatized action for the current user. The action will appear in their minifeed, and may appear in their friends' newsfeeds depending upon a number
	 * of different factors. When a template match exists between multiple distinct users (like "Bob recommends Bizou" and "Sally recommends Bizou"), the feed entries may
	 * be combined in the newfeed (to something like "Bob and sally recommend Bizou"). This happens automatically, and *only* if the template match between the two feed
	 * entries is identical.<br />
	 * <br />
	 * Feed entries are not aggregated for a single user (so "Bob recommends Bizou" and "Bob recommends Le Charm" *will not* become "Bob recommends Bizou and Le Charm").<br />
	 * <br />
	 * If the user's action involves one or more of their friends, list them in the 'targetIds' parameter. For example, if you have "Bob says hi to Sally and Susie", and
	 * Sally's UID is 1, and Susie's UID is 2, then pass a 'targetIds' paramters of "1,2". If you pass this parameter, you can use the "{target}" token in your templates.
	 * Probably it also makes it more likely that Sally and Susie will see the feed entry in their newsfeed, relative to any other friends Bob might have. It may be a
	 * good idea to always send a list of all the user's friends, and avoid using the "{target}" token, to maximize distribution of the story through the newsfeed.<br />
	 * <br />
	 * The only strictly required parameter is 'titleTemplate', which must contain the "{actor}" token somewhere inside of it. All other parameters, options, and tokens
	 * are optional, and my be set to null if being omitted.<br />
	 * <br />
	 * Not that stories will only be aggregated if *all* templates match and *all* template parameters match, so if two entries have the same templateTitle and titleData,
	 * but a different bodyTemplate, they will not aggregate. Probably it's better to use bodyGeneral instead of bodyTemplate, for the extra flexibility it provides.<br />
	 * <br />
	 * <br />
	 * Note that this method is replacing 'feed_publishActionOfUser', which has been deprecated by Facebook. For specific details, visit
	 * http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction
	 * 
	 * 
	 * @param titleTemplate
	 *            the template for the title of the feed entry, this must contain the "(actor}" token. Any other tokens are optional, i.e. "{actor} recommends {place}".
	 * @param titleData
	 *            JSON-formatted values for any tokens used in titleTemplate, with the exception of "{actor}" and "{target}", which Facebook populates automatically, i.e.
	 *            "{place: "<a href='http://www.bizou.com'>Bizou</a>"}".
	 * @param bodyTemplate
	 *            the template for the body of the feed entry, works the same as 'titleTemplate', but is not required to contain the "{actor}" token.
	 * @param bodyData
	 *            works the same as titleData
	 * @param bodyGeneral
	 *            non-templatized content for the body, may contain markup, may not contain tokens.
	 * @param pictures
	 *            a list of up to 4 images to display, with optional hyperlinks for each one.
	 * @param targetIds
	 *            a comma-seperated list of the UID's of any friend(s) who are involved in this feed action (if there are any), this specifies the value of the "{target}"
	 *            token. If you use this token in any of your templates, you must specify a value for this parameter.
	 * 
	 * @return a Document representing the XML response returned from the Facebook API server.
	 * 
	 * @throws FacebookException
	 *             if any number of bad things happen
	 * @throws IOException
	 */
	public boolean feed_publishTemplatizedAction( String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds ) throws FacebookException, IOException {

		return templatizedFeedHandler( null, FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, pictures,
				targetIds, null );
	}

	public boolean feed_publishTemplatizedAction( String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds, Long pageId ) throws FacebookException, IOException {

		return templatizedFeedHandler( null, FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, pictures,
				targetIds, pageId );
	}

	/**
	 * Publishes a templatized action for the current user. The action will appear in their minifeed, and may appear in their friends' newsfeeds depending upon a number
	 * of different factors. When a template match exists between multiple distinct users (like "Bob recommends Bizou" and "Sally recommends Bizou"), the feed entries may
	 * be combined in the newfeed (to something like "Bob and sally recommend Bizou"). This happens automatically, and *only* if the template match between the two feed
	 * entries is identical.<br />
	 * <br />
	 * Feed entries are not aggregated for a single user (so "Bob recommends Bizou" and "Bob recommends Le Charm" *will not* become "Bob recommends Bizou and Le Charm").<br />
	 * <br />
	 * If the user's action involves one or more of their friends, list them in the 'targetIds' parameter. For example, if you have "Bob says hi to Sally and Susie", and
	 * Sally's UID is 1, and Susie's UID is 2, then pass a 'targetIds' paramters of "1,2". If you pass this parameter, you can use the "{target}" token in your templates.
	 * Probably it also makes it more likely that Sally and Susie will see the feed entry in their newsfeed, relative to any other friends Bob might have. It may be a
	 * good idea to always send a list of all the user's friends, and avoid using the "{target}" token, to maximize distribution of the story through the newsfeed.<br />
	 * <br />
	 * The only strictly required parameter is 'titleTemplate', which must contain the "{actor}" token somewhere inside of it. All other parameters, options, and tokens
	 * are optional, and my be set to null if being omitted.<br />
	 * <br />
	 * Not that stories will only be aggregated if *all* templates match and *all* template parameters match, so if two entries have the same templateTitle and titleData,
	 * but a different bodyTemplate, they will not aggregate. Probably it's better to use bodyGeneral instead of bodyTemplate, for the extra flexibility it provides.<br />
	 * <br />
	 * <br />
	 * Note that this method is replacing 'feed_publishActionOfUser', which has been deprecated by Facebook. For specific details, visit
	 * http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction
	 * 
	 * 
	 * @param action
	 *            a TemplatizedAction instance that represents the feed data to publish
	 * 
	 * @return a Document representing the XML response returned from the Facebook API server.
	 * 
	 * @throws FacebookException
	 *             if any number of bad things happen
	 * @throws IOException
	 */
	public boolean feed_PublishTemplatizedAction( TemplatizedAction action ) throws FacebookException, IOException {
		return feed_publishTemplatizedAction( action.getTitleTemplate(), action.getTitleParams(), action.getBodyTemplate(), action.getBodyParams(), action
				.getBodyGeneral(), action.getPictures(), action.getTargetIds(), action.getPageActorId() );
	}

	/**
	 * Publish the notification of an action taken by a user to newsfeed.
	 * 
	 * @param title
	 *            the title of the feed story
	 * @param body
	 *            the body of the feed story
	 * @param images
	 *            (optional) up to four pairs of image URLs and (possibly null) link URLs
	 * @param priority
	 * @return a document object containing the server response
	 * 
	 * @deprecated Facebook will be removing this API call (it is to be replaced with feed_publishTemplatizedAction)
	 */
	@Deprecated
	public boolean feed_publishActionOfUser( CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images, Integer priority )
			throws FacebookException, IOException {
		return feedHandlerBoolean( FacebookMethod.FEED_PUBLISH_ACTION_OF_USER, title, body, images, priority );
	}

	/**
	 * @see FacebookRestClient#feed_publishActionOfUser(CharSequence,CharSequence,Collection,Integer)
	 * 
	 * @deprecated Facebook will be removing this API call (it is to be replaced with feed_publishTemplatizedAction)
	 */
	@Deprecated
	public boolean feed_publishActionOfUser( String title, String body ) throws FacebookException, IOException {
		return feed_publishActionOfUser( title, body, null, null );
	}

	/**
	 * @see FacebookRestClient#feed_publishActionOfUser(CharSequence,CharSequence,Collection,Integer)
	 * 
	 * @deprecated Facebook will be removing this API call (it is to be replaced with feed_publishTemplatizedAction)
	 */
	@Deprecated
	public boolean feed_publishActionOfUser( CharSequence title, CharSequence body ) throws FacebookException, IOException {
		return feed_publishActionOfUser( title, body, null, null );
	}

	/**
	 * @see FacebookRestClient#feed_publishActionOfUser(CharSequence,CharSequence,Collection,Integer)
	 * 
	 * @deprecated Facebook will be removing this API call (it is to be replaced with feed_publishTemplatizedAction)
	 */
	@Deprecated
	public boolean feed_publishActionOfUser( CharSequence title, CharSequence body, Integer priority ) throws FacebookException, IOException {
		return feed_publishActionOfUser( title, body, null, priority );
	}

	/**
	 * Publish a story to the logged-in user's newsfeed.
	 * 
	 * @param title
	 *            the title of the feed story
	 * @param body
	 *            the body of the feed story
	 * @param images
	 *            (optional) up to four pairs of image URLs and (possibly null) link URLs
	 * @param priority
	 * @return a Document object containing the server response
	 */
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images, Integer priority )
			throws FacebookException, IOException {
		return feedHandlerBoolean( FacebookMethod.FEED_PUBLISH_STORY_TO_USER, title, body, images, priority );
	}

	/**
	 * @see FacebookRestClient#feed_publishStoryToUser(CharSequence,CharSequence,Collection,Integer)
	 */
	public boolean feed_publishStoryToUser( String title, String body ) throws FacebookException, IOException {
		return feed_publishStoryToUser( title, body, null, null );
	}

	/**
	 * @see FacebookRestClient#feed_publishStoryToUser(CharSequence,CharSequence,Collection,Integer)
	 */
	public boolean feed_publishStoryToUser( String title, String body, Integer priority ) throws FacebookException, IOException {
		return feed_publishStoryToUser( title, body, null, priority );
	}

	/**
	 * @see FacebookRestClient#feed_publishStoryToUser(CharSequence,CharSequence,Collection,Integer)
	 */
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body ) throws FacebookException, IOException {
		return feed_publishStoryToUser( title, body, null, null );
	}

	/**
	 * @see FacebookRestClient#feed_publishStoryToUser(CharSequence,CharSequence,Collection,Integer)
	 */
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body, Integer priority ) throws FacebookException, IOException {
		return feed_publishStoryToUser( title, body, null, priority );
	}

	protected Document feedHandler( FacebookMethod feedMethod, CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images,
			Integer priority ) throws FacebookException, IOException {
		assert ( images == null || images.size() <= 4 );

		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( feedMethod.numParams() );

		params.add( new Pair<String,CharSequence>( "title", title ) );
		if ( null != body )
			params.add( new Pair<String,CharSequence>( "body", body ) );
		if ( null != priority )
			params.add( new Pair<String,CharSequence>( "priority", priority.toString() ) );
		if ( null != images && !images.isEmpty() ) {
			int image_count = 0;
			for ( IPair image : images ) {
				++image_count;
				assert ( image.getFirst() != null );
				params.add( new Pair<String,CharSequence>( String.format( "image_%d", image_count ), image.getFirst().toString() ) );
				if ( image.getSecond() != null )
					params.add( new Pair<String,CharSequence>( String.format( "image_%d_link", image_count ), image.getSecond().toString() ) );
			}
		}
		return callMethod( feedMethod, params );
	}

	protected boolean feedHandlerBoolean( FacebookMethod feedMethod, CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images,
			Integer priority ) throws FacebookException, IOException {
		assert ( images == null || images.size() <= 4 );

		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( feedMethod.numParams() );

		params.add( new Pair<String,CharSequence>( "title", title ) );
		if ( null != body )
			params.add( new Pair<String,CharSequence>( "body", body ) );
		if ( null != priority )
			params.add( new Pair<String,CharSequence>( "priority", priority.toString() ) );
		if ( null != images && !images.isEmpty() ) {
			int image_count = 0;
			for ( IPair image : images ) {
				++image_count;
				assert ( image.getFirst() != null );
				params.add( new Pair<String,CharSequence>( String.format( "image_%d", image_count ), image.getFirst().toString() ) );
				if ( image.getSecond() != null )
					params.add( new Pair<String,CharSequence>( String.format( "image_%d_link", image_count ), image.getSecond().toString() ) );
			}
		}
		callMethod( feedMethod, params );
		if ( this.rawResponse == null ) {
			return false;
		}
		return this.rawResponse.contains( ">1<" ); // a code of '1' indicates success
	}


	protected boolean templatizedFeedHandler( Long actorId, FacebookMethod method, String titleTemplate, String titleData, String bodyTemplate, String bodyData,
			String bodyGeneral, Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds, Long pageId ) throws FacebookException, IOException {
		assert ( pictures == null || pictures.size() <= 4 );

		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( method.numParams() );

		// these are always required parameters
		params.add( new Pair<String,CharSequence>( "title_template", titleTemplate ) );

		// these are optional parameters
		if ( titleData != null ) {
			params.add( new Pair<String,CharSequence>( "title_data", titleData ) );
		}
		if ( bodyTemplate != null ) {
			params.add( new Pair<String,CharSequence>( "body_template", bodyTemplate ) );
			if ( bodyData != null ) {
				params.add( new Pair<String,CharSequence>( "body_data", bodyData ) );
			}
		}
		if ( bodyGeneral != null ) {
			params.add( new Pair<String,CharSequence>( "body_general", bodyGeneral ) );
		}
		if ( pictures != null ) {
			int count = 1;
			for ( IPair picture : pictures ) {
				String url = picture.getFirst().toString();
				if ( url.startsWith( TemplatizedAction.UID_TOKEN ) ) {
					url = url.substring( TemplatizedAction.UID_TOKEN.length() );
				}
				params.add( new Pair<String,CharSequence>( "image_" + count, url ) );
				if ( picture.getSecond() != null ) {
					params.add( new Pair<String,CharSequence>( "image_" + count + "_link", picture.getSecond().toString() ) );
				}
				count++ ;
			}
		}
		if ( targetIds != null ) {
			params.add( new Pair<String,CharSequence>( "target_ids", targetIds ) );
		}
		if ( pageId != null ) {
			params.add( new Pair<String,CharSequence>( "page_actor_id", Long.toString( pageId ) ) );
		}
		callMethod( method, params );
		if ( this.rawResponse == null ) {
			return false;
		}
		return this.rawResponse.contains( ">1<" ); // a code of '1' indicates success
	}

	/**
	 * Returns all visible events according to the filters specified. This may be used to find all events of a user, or to query specific eids.
	 * 
	 * @param eventIds
	 *            filter by these event ID's (optional)
	 * @param userId
	 *            filter by this user only (optional)
	 * @param startTime
	 *            UTC lower bound (optional)
	 * @param endTime
	 *            UTC upper bound (optional)
	 * @return Document of events
	 */
	public Document events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.EVENTS_GET.numParams() );

		boolean hasUserId = null != userId && 0 != userId;
		boolean hasEventIds = null != eventIds && !eventIds.isEmpty();
		boolean hasStart = null != startTime && 0 != startTime;
		boolean hasEnd = null != endTime && 0 != endTime;

		if ( hasUserId ) {
			params.add( new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
		}
		if ( hasEventIds ) {
			params.add( new Pair<String,CharSequence>( "eids", delimit( eventIds ) ) );
		}
		if ( hasStart ) {
			params.add( new Pair<String,CharSequence>( "start_time", startTime.toString() ) );
		}
		if ( hasEnd ) {
			params.add( new Pair<String,CharSequence>( "end_time", endTime.toString() ) );
		}
		return callMethod( FacebookMethod.EVENTS_GET, params );
	}

	/**
	 * Retrieves the membership list of an event
	 * 
	 * @param eventId
	 *            event id
	 * @return Document consisting of four membership lists corresponding to RSVP status, with keys 'attending', 'unsure', 'declined', and 'not_replied'
	 */
	public Document events_getMembers( Number eventId ) throws FacebookException, IOException {
		assert ( null != eventId );
		return callMethod( FacebookMethod.EVENTS_GET_MEMBERS, new Pair<String,CharSequence>( "eid", eventId.toString() ) );
	}


	/**
	 * Retrieves the friends of the currently logged in user.
	 * 
	 * @return array of friends
	 */
	public Document friends_areFriends( long userId1, long userId2 ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.FRIENDS_ARE_FRIENDS, new Pair<String,CharSequence>( "uids1", Long.toString( userId1 ) ), new Pair<String,CharSequence>(
				"uids2", Long.toString( userId2 ) ) );
	}

	public Document friends_areFriends( Collection<Long> userIds1, Collection<Long> userIds2 ) throws FacebookException, IOException {
		assert ( userIds1 != null && userIds2 != null );
		assert ( !userIds1.isEmpty() && !userIds2.isEmpty() );
		assert ( userIds1.size() == userIds2.size() );

		return callMethod( FacebookMethod.FRIENDS_ARE_FRIENDS, new Pair<String,CharSequence>( "uids1", delimit( userIds1 ) ), new Pair<String,CharSequence>( "uids2",
				delimit( userIds2 ) ) );
	}

	/**
	 * Retrieves the friends of the currently logged in user.
	 * 
	 * @return array of friends
	 */
	public Document friends_get() throws FacebookException, IOException {
		if ( cacheFriendsList == null ) {
			cacheFriendsList = callMethod( FacebookMethod.FRIENDS_GET );
		}
		return cacheFriendsList;
	}

	/**
	 * A wrapper method for {@link FacebookRestClient#friends_get()}. When a session is started in a canvas, the Facebook server sends a list of friends as an fb_friends
	 * parameter, and it is cached in this instance. This method first checks the cached list, and then calls {@link FacebookRestClient#friends_get()} only if necessary.
	 * 
	 * @return A list of friends uids.
	 * @throws IOException
	 * @throws FacebookException
	 */
	// In the php client this method is the normal friends_get() method, which
	// returns a list. However, in the current state of this Java client it is
	// not possible because friends_get has to return a Document, not a List.
	@Deprecated
	public List<Long> friends_getAsList() throws FacebookException, IOException {
		return toFriendsList( friends_get() );
	}

	public static List<Long> toFriendsList( Document doc ) {
		NodeList uids = doc.getElementsByTagName( "uid" );
		List<Long> out = new ArrayList<Long>( uids.getLength() );
		for ( int i = 0; i < uids.getLength(); i++ ) {
			out.add( Long.parseLong( uids.item( i ).getFirstChild().getTextContent().trim() ) );
		}
		return out;
	}

	/**
	 * Retrieves the friends of the currently logged in user, who are also users of the calling application.
	 * 
	 * @return array of friends
	 */
	public Document friends_getAppUsers() throws FacebookException, IOException {
		return callMethod( FacebookMethod.FRIENDS_GET_APP_USERS );
	}

	public Document users_getStandardInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException, IOException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_STANDARD_INFO, new Pair<String,CharSequence>( "uids", delimit( userIds ) ), new Pair<String,CharSequence>( "fields",
				delimit( fields ) ) );
	}

	public Document users_getStandardInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException, IOException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_STANDARD_INFO, new Pair<String,CharSequence>( "uids", delimit( userIds ) ), new Pair<String,CharSequence>( "fields",
				delimit( fields ) ) );
	}

	public Document users_getInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException, IOException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_INFO, new Pair<String,CharSequence>( "uids", delimit( userIds ) ), new Pair<String,CharSequence>( "fields",
				delimit( fields ) ) );
	}

	public Document users_getInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException, IOException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_INFO, new Pair<String,CharSequence>( "uids", delimit( userIds ) ), new Pair<String,CharSequence>( "fields",
				delimit( fields ) ) );
	}

	/**
	 * Retrieves the user ID of the user logged in to this API session
	 * 
	 * @return the Facebook user ID of the logged-in user
	 */
	public long users_getLoggedInUser() throws FacebookException, IOException {
		if ( this.cacheUserId == null || this.batchMode ) {
			Document d = callMethod( FacebookMethod.USERS_GET_LOGGED_IN_USER );
			if ( d == null ) {
				return 0l;
			}
			this.cacheUserId = Long.parseLong( d.getFirstChild().getTextContent() );
		}
		return this.cacheUserId;
	}

	/**
	 * Retrieves an indicator of whether the logged-in user has installed the application associated with the _apiKey.
	 * 
	 * @return boolean indicating whether the user has installed the app
	 */
	public boolean users_isAppAdded() throws FacebookException, IOException {
		// a null value for added means that facebook didn't send an
		// fb_added param
		if ( cacheAppAdded == null ) {
			cacheAppAdded = extractBoolean( callMethod( FacebookMethod.USERS_IS_APP_ADDED ) );
		}
		return cacheAppAdded.booleanValue();
	}

	public Boolean getCacheAppAdded() {
		return cacheAppAdded;
	}

	public void setCacheAppAdded( Boolean value ) {
		this.cacheAppAdded = value;
	}

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param subjId
	 *            retrieve from photos associated with this user (optional).
	 * @param albumId
	 *            retrieve from photos from this album (optional)
	 * @param photoIds
	 *            retrieve from this list of photos (optional)
	 * 
	 * @return an Document of photo objects.
	 */
	public Document photos_get( Long subjId, Long albumId, Collection<Long> photoIds ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_GET.numParams() );

		boolean hasUserId = null != subjId && 0 != subjId;
		boolean hasAlbumId = null != albumId && 0 != albumId;
		boolean hasPhotoIds = null != photoIds && !photoIds.isEmpty();
		assert ( hasUserId || hasAlbumId || hasPhotoIds );

		if ( hasUserId )
			params.add( new Pair<String,CharSequence>( "subj_id", Long.toString( subjId ) ) );
		if ( hasAlbumId )
			params.add( new Pair<String,CharSequence>( "aid", Long.toString( albumId ) ) );
		if ( hasPhotoIds )
			params.add( new Pair<String,CharSequence>( "pids", delimit( photoIds ) ) );

		return callMethod( FacebookMethod.PHOTOS_GET, params );
	}

	public Document photos_get( Long albumId, Collection<Long> photoIds, boolean album ) throws FacebookException, IOException {
		return photos_get( null/* subjId */, albumId, photoIds );
	}

	public Document photos_get( Long subjId, Collection<Long> photoIds ) throws FacebookException, IOException {
		return photos_get( subjId, null/* albumId */, photoIds );
	}

	public Document photos_get( Long subjId, Long albumId ) throws FacebookException, IOException {
		return photos_get( subjId, albumId, null/* photoIds */);
	}

	public Document photos_get( Collection<Long> photoIds ) throws FacebookException, IOException {
		return photos_get( null/* subjId */, null/* albumId */, photoIds );
	}

	public Document photos_get( Long albumId, boolean album ) throws FacebookException, IOException {
		return photos_get( null/* subjId */, albumId, null/* photoIds */);
	}

	public Document photos_get( Long subjId ) throws FacebookException, IOException {
		return photos_get( subjId, null/* albumId */, null/* photoIds */);
	}

	/**
	 * Retrieves album metadata. Pass a user id and/or a list of album ids to specify the albums to be retrieved (at least one must be provided)
	 * 
	 * @param userId
	 *            retrieve metadata for albums created the id of the user whose album you wish (optional).
	 * @param albumIds
	 *            the ids of albums whose metadata is to be retrieved
	 * @return album objects.
	 */
	public Document photos_getAlbums( Long userId, Collection<Long> albumIds ) throws FacebookException, IOException {
		boolean hasUserId = null != userId && userId != 0;
		boolean hasAlbumIds = null != albumIds && !albumIds.isEmpty();
		assert ( hasUserId || hasAlbumIds ); // one of the two must be provided

		if ( hasUserId )
			return ( hasAlbumIds ) ? callMethod( FacebookMethod.PHOTOS_GET_ALBUMS, new Pair<String,CharSequence>( "uid", Long.toString( userId ) ),
					new Pair<String,CharSequence>( "aids", delimit( albumIds ) ) ) : callMethod( FacebookMethod.PHOTOS_GET_ALBUMS, new Pair<String,CharSequence>( "uid",
					Long.toString( userId ) ) );
		else
			return callMethod( FacebookMethod.PHOTOS_GET_ALBUMS, new Pair<String,CharSequence>( "aids", delimit( albumIds ) ) );
	}

	public Document photos_getAlbums( Long userId ) throws FacebookException, IOException {
		return photos_getAlbums( userId, null /* albumIds */);
	}

	public Document photos_getAlbums( Collection<Long> albumIds ) throws FacebookException, IOException {
		return photos_getAlbums( null /* userId */, albumIds );
	}

	/**
	 * Retrieves the tags for the given set of photos.
	 * 
	 * @param photoIds
	 *            The list of photos from which to extract photo tags.
	 * @return the created album
	 */
	public Document photos_getTags( Collection<Long> photoIds ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PHOTOS_GET_TAGS, new Pair<String,CharSequence>( "pids", delimit( photoIds ) ) );
	}

	/**
	 * Creates an album.
	 * 
	 * @param albumName
	 *            The list of photos from which to extract photo tags.
	 * @return the created album
	 */
	public Document photos_createAlbum( String albumName ) throws FacebookException, IOException {
		return photos_createAlbum( albumName, null/* description */, null/* location */);
	}

	/**
	 * Creates an album.
	 * 
	 * @param name
	 *            The album name.
	 * @param location
	 *            The album location (optional).
	 * @param description
	 *            The album description (optional).
	 * @return an array of photo objects.
	 */
	public Document photos_createAlbum( String name, String description, String location ) throws FacebookException, IOException {
		assert ( null != name && !"".equals( name ) );
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_CREATE_ALBUM.numParams() );
		params.add( new Pair<String,CharSequence>( "name", name ) );
		if ( null != description ) {
			params.add( new Pair<String,CharSequence>( "description", description ) );
		}
		if ( null != location ) {
			params.add( new Pair<String,CharSequence>( "location", location ) );
		}
		return photos_createAlbum( FacebookMethod.PHOTOS_CREATE_ALBUM, params );
	}

	/**
	 * Adds several tags to a photo.
	 * 
	 * @param photoId
	 *            The photo id of the photo to be tagged.
	 * @param tags
	 *            A list of PhotoTags.
	 * @return a list of booleans indicating whether the tag was successfully added.
	 */
	public Document photos_addTags( Long photoId, Collection<PhotoTag> tags ) throws FacebookException, IOException {
		assert ( photoId > 0 );
		assert ( null != tags && !tags.isEmpty() );
		String tagStr = null;
		try {
			JSONArray jsonTags = new JSONArray();
			for ( PhotoTag tag : tags ) {
				jsonTags.put( tag.jsonify() );
			}
			tagStr = jsonTags.toString();
		}
		catch ( Exception ignored ) {
			// ignore
		}
		return callMethod( FacebookMethod.PHOTOS_ADD_TAG, new Pair<String,CharSequence>( "pid", photoId.toString() ), new Pair<String,CharSequence>( "tags", tagStr ) );
	}

	/**
	 * Adds a tag to a photo.
	 * 
	 * @param photoId
	 *            The photo id of the photo to be tagged.
	 * @param xPct
	 *            The horizontal position of the tag, as a percentage from 0 to 100, from the left of the photo.
	 * @param yPct
	 *            The vertical position of the tag, as a percentage from 0 to 100, from the top of the photo.
	 * @param taggedUserId
	 *            The list of photos from which to extract photo tags.
	 * @return whether the tag was successfully added.
	 */
	public boolean photos_addTag( Long photoId, Long taggedUserId, Double xPct, Double yPct ) throws FacebookException, IOException {
		return photos_addTag( photoId, xPct, yPct, taggedUserId, null );
	}

	/**
	 * Adds a tag to a photo.
	 * 
	 * @param photoId
	 *            The photo id of the photo to be tagged.
	 * @param xPct
	 *            The horizontal position of the tag, as a percentage from 0 to 100, from the left of the photo.
	 * @param yPct
	 *            The list of photos from which to extract photo tags.
	 * @param tagText
	 *            The text of the tag.
	 * @return whether the tag was successfully added.
	 */
	public boolean photos_addTag( Long photoId, CharSequence tagText, Double xPct, Double yPct ) throws FacebookException, IOException {
		return photos_addTag( photoId, xPct, yPct, null, tagText );
	}

	private boolean photos_addTag( Long photoId, Double xPct, Double yPct, Long taggedUserId, CharSequence tagText ) throws FacebookException, IOException {
		assert ( null != photoId && !photoId.equals( 0 ) );
		assert ( null != taggedUserId || null != tagText );
		assert ( null != xPct && xPct >= 0 && xPct <= 100 );
		assert ( null != yPct && yPct >= 0 && yPct <= 100 );
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		Pair<String,CharSequence> tagData;
		if ( taggedUserId != null ) {
			tagData = new Pair<String,CharSequence>( "tag_uid", taggedUserId.toString() );
		} else {
			tagData = new Pair<String,CharSequence>( "tag_text", tagText.toString() );
		}
		params.add( tagData );
		params.add( new Pair<String,CharSequence>( "x", xPct.toString() ) );
		params.add( new Pair<String,CharSequence>( "y", yPct.toString() ) );
		params.add( new Pair<String,CharSequence>( "pid", photoId.toString() ) );

		return photos_addTag( FacebookMethod.PHOTOS_ADD_TAG, params );
	}

	public Document photos_upload( File photo ) throws FacebookException, IOException {
		return /* caption *//* albumId */photos_upload( photo, null, null );
	}

	public Document photos_upload( File photo, String caption ) throws FacebookException, IOException {
		return /* albumId */photos_upload( photo, caption, null );
	}

	public Document photos_upload( File photo, Long albumId ) throws FacebookException, IOException {
		return /* caption */photos_upload( photo, null, albumId );
	}

	public Document photos_upload( File photo, String caption, Long albumId ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_UPLOAD.numParams() );
		assert ( photo.exists() && photo.canRead() );
		this._uploadFile = photo;
		if ( null != albumId ) {
			params.add( new Pair<String,CharSequence>( "aid", Long.toString( albumId ) ) );
		}
		if ( null != caption ) {
			params.add( new Pair<String,CharSequence>( "caption", caption ) );
		}
		return photos_upload( FacebookMethod.PHOTOS_UPLOAD, params );
	}

	/**
	 * Retrieves the groups associated with a user
	 * 
	 * @param userId
	 *            Optional: User associated with groups. A null parameter will default to the session user.
	 * @param groupIds
	 *            Optional: group ids to query. A null parameter will get all groups for the user.
	 * @return array of groups
	 */
	public Document groups_get( Long userId, Collection<Long> groupIds ) throws FacebookException, IOException {
		boolean hasGroups = ( null != groupIds && !groupIds.isEmpty() );
		if ( null != userId ) {
			return hasGroups ? callMethod( FacebookMethod.GROUPS_GET, new Pair<String,CharSequence>( "uid", userId.toString() ), new Pair<String,CharSequence>( "gids",
					delimit( groupIds ) ) ) : callMethod( FacebookMethod.GROUPS_GET, new Pair<String,CharSequence>( "uid", userId.toString() ) );
		} else {
			return hasGroups ? callMethod( FacebookMethod.GROUPS_GET, new Pair<String,CharSequence>( "gids", delimit( groupIds ) ) ) : this
					.callMethod( FacebookMethod.GROUPS_GET );
		}
	}

	/**
	 * Retrieves the membership list of a group
	 * 
	 * @param groupId
	 *            the group id
	 * @return a Document containing four membership lists of 'members', 'admins', 'officers', and 'not_replied'
	 */
	public Document groups_getMembers( Number groupId ) throws FacebookException, IOException {
		assert ( null != groupId );
		return callMethod( FacebookMethod.GROUPS_GET_MEMBERS, new Pair<String,CharSequence>( "gid", groupId.toString() ) );
	}

	/**
	 * Retrieves the results of a Facebook Query Language query
	 * 
	 * @param query :
	 *            the FQL query statement
	 * @return varies depending on the FQL query
	 */
	public Document fql_query( CharSequence query ) throws FacebookException, IOException {
		assert ( null != query );
		return callMethod( FacebookMethod.FQL_QUERY, new Pair<String,CharSequence>( "query", query ) );
	}

	/**
	 * Retrieves the outstanding notifications for the session user.
	 * 
	 * @return a Document containing notification count pairs for 'messages', 'pokes' and 'shares', a uid list of 'friend_requests', a gid list of 'group_invites', and an
	 *         eid list of 'event_invites'
	 */
	public Document notifications_get() throws FacebookException, IOException {
		return callMethod( FacebookMethod.NOTIFICATIONS_GET );
	}

	/**
	 * Send a request or invitations to the specified users.
	 * 
	 * @param recipientIds
	 *            the user ids to which the request is to be sent
	 * @param type
	 *            the type of request/invitation - e.g. the word "event" in "1 event invitation."
	 * @param content
	 *            Content of the request/invitation. This should be FBML containing only links and the special tag &lt;fb:req-choice url="" label="" /&gt; to specify the
	 *            buttons to be included in the request.
	 * @param image
	 *            URL of an image to show beside the request. It will be resized to be 100 pixels wide.
	 * @param isInvite
	 *            whether this is a "request" or an "invite"
	 * @return a URL, possibly null, to which the user should be redirected to finalize the sending of the message
	 * 
	 * @deprecated this method has been removed from the Facebook API server
	 */
	@Deprecated
	public URL notifications_sendRequest( Collection<Long> recipientIds, CharSequence type, CharSequence content, URL image, boolean isInvite ) throws FacebookException,
			IOException {
		assert ( null != recipientIds && !recipientIds.isEmpty() );
		assert ( null != type );
		assert ( null != content );
		assert ( null != image );
		Document d = callMethod( FacebookMethod.NOTIFICATIONS_SEND_REQUEST, new Pair<String,CharSequence>( "to_ids", delimit( recipientIds ) ),
				new Pair<String,CharSequence>( "type", type ), new Pair<String,CharSequence>( "content", content ), new Pair<String,CharSequence>( "image", image
						.toString() ), new Pair<String,CharSequence>( "invite", isInvite ? "1" : "0" ) );
		if ( d == null ) {
			return null;
		}
		String url = d.getFirstChild().getTextContent();
		return ( null == url || "".equals( url ) ) ? null : new URL( url );
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public URL notifications_send( Collection<Long> recipientIds, CharSequence notification, CharSequence email ) throws FacebookException, IOException {
		this.notifications_send( recipientIds, notification );
		return null;
	}

	public static boolean extractBoolean( Node doc ) {
		if ( doc == null ) {
			return false;
		}
		String content = doc.getFirstChild().getTextContent();
		return "1".equals( content );
	}

	/**
	 * Helper function for posting a request that includes raw file data, eg {@link #photos_upload(File)}.
	 * 
	 * @param methodName
	 *            the name of the method
	 * @param params
	 *            request parameters (not including the file)
	 * @param doEncode
	 *            whether to UTF8-encode the parameters
	 * @return an InputStream with the request response
	 * @see #photos_upload(File)
	 */
	protected InputStream postFileRequest( String methodName, Map<String,String> params ) throws IOException {
		return postFileRequest( methodName, params, /* doEncode */true );
	}

	public InputStream postFileRequest( String methodName, Map<String,String> params, boolean doEncode ) {
		assert ( null != _uploadFile );
		try {
			BufferedInputStream bufin = new BufferedInputStream( new FileInputStream( _uploadFile ) );

			String boundary = Long.toString( System.currentTimeMillis(), 16 );
			URLConnection con = _serverUrl.openConnection();
			con.setDoInput( true );
			con.setDoOutput( true );
			con.setUseCaches( false );
			con.setRequestProperty( "Content-Type", "multipart/form-data; boundary=" + boundary );
			con.setRequestProperty( "MIME-version", "1.0" );

			DataOutputStream out = new DataOutputStream( con.getOutputStream() );

			for ( Map.Entry<String,String> entry : params.entrySet() ) {
				out.writeBytes( PREF + boundary + CRLF );
				out.writeBytes( "Content-disposition: form-data; name=\"" + entry.getKey() + "\"" );
				out.writeBytes( CRLF + CRLF );
				out.writeBytes( doEncode ? encode( entry.getValue() ) : entry.getValue().toString() );
				out.writeBytes( CRLF );
			}

			out.writeBytes( PREF + boundary + CRLF );
			out.writeBytes( "Content-disposition: form-data; filename=\"" + _uploadFile.getName() + "\"" + CRLF );
			out.writeBytes( "Content-Type: image/jpeg" + CRLF );
			// out.writeBytes("Content-Transfer-Encoding: binary" + CRLF); // not necessary

			// Write the file
			out.writeBytes( CRLF );
			byte b[] = new byte[UPLOAD_BUFFER_SIZE];
			int byteCounter = 0;
			int i;
			while ( -1 != ( i = bufin.read( b ) ) ) {
				byteCounter += i;
				out.write( b, 0, i );
			}
			out.writeBytes( CRLF + PREF + boundary + PREF + CRLF );

			out.flush();
			out.close();

			InputStream is = con.getInputStream();
			return is;
		}
		catch ( Exception ex ) {
			log.error( "exception: " + ex, ex );
			return null;
		}
	}

	/**
	 * Call this function and store the result, using it to generate the appropriate login url and then to retrieve the session information.
	 * 
	 * @return String the auth_token string
	 */
	public String auth_createToken() throws FacebookException, IOException {
		Document d = callMethod( FacebookMethod.AUTH_CREATE_TOKEN );
		if ( d == null ) {
			return null;
		}
		return d.getFirstChild().getTextContent();
	}

	/**
	 * Call this function to retrieve the session information after your user has logged in.
	 * 
	 * @param authToken
	 *            the token returned by auth_createToken or passed back to your callback_url.
	 */
	public String auth_getSession( String authToken ) throws FacebookException, IOException {
		Document d = callMethod( FacebookMethod.AUTH_GET_SESSION, new Pair<String,CharSequence>( "auth_token", authToken.toString() ) );
		if ( d == null ) {
			return null;
		}
		this.cacheSessionKey = d.getElementsByTagName( "session_key" ).item( 0 ).getFirstChild().getTextContent();
		this.cacheUserId = Long.parseLong( d.getElementsByTagName( "uid" ).item( 0 ).getFirstChild().getTextContent() );
		this.cacheSessionExpires = Long.parseLong( d.getElementsByTagName( "expires" ).item( 0 ).getFirstChild().getTextContent() );
		if ( this._isDesktop ) {
			this.cacheSessionSecret = d.getElementsByTagName( "secret" ).item( 0 ).getFirstChild().getTextContent();
		}
		return this.cacheSessionKey;
	}

	/**
	 * Returns a JAXB object of the type that corresponds to the last API call made on the client. Each Facebook Platform API call that returns a Document object has a
	 * JAXB response object associated with it. The naming convention is generally intuitive. For example, if you invoke the 'user_getInfo' API call, the associated JAXB
	 * response object is 'UsersGetInfoResponse'.<br />
	 * <br />
	 * An example of how to use this method:<br />
	 * <br />
	 * FacebookRestClient client = new FacebookRestClient("apiKey", "secretKey", "sessionId");<br />
	 * client.friends_get();<br />
	 * FriendsGetResponse response = (FriendsGetResponse)client.getResponsePOJO();<br />
	 * List<Long> friends = response.getUid(); <br />
	 * <br />
	 * This is particularly useful in the case of API calls that return a Document object, as working with the JAXB response object is generally much simple than trying
	 * to walk/parse the DOM by hand.<br />
	 * <br />
	 * This method can be safely called multiple times, though note that it will only return the response-object corresponding to the most recent Facebook Platform API
	 * call made.<br />
	 * <br />
	 * Note that you must cast the return value of this method to the correct type in order to do anything useful with it.
	 * 
	 * @return a JAXB POJO ("Plain Old Java Object") of the type that corresponds to the last API call made on the client. Note that you must cast this object to its
	 *         proper type before you will be able to do anything useful with it.
	 */
	public Object getResponsePOJO() {
		if ( this.rawResponse == null ) {
			return null;
		}
		if ( JAXB_CONTEXT == null ) {
			return null;
		}
		Object pojo = null;
		try {
			Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
			pojo = unmarshaller.unmarshal( new ByteArrayInputStream( this.rawResponse.getBytes( "UTF-8" ) ) );
		}
		catch ( JAXBException ex ) {
			log.error( "getResponsePOJO() - Could not unmarshall XML stream into POJO", ex );
		}
		catch ( NullPointerException ex ) {
			log.error( "getResponsePOJO() - Could not unmarshall XML stream into POJO", ex );
		}
		catch ( UnsupportedEncodingException ex ) {
			log.error( "getResponsePOJO() - Could not unmarshall XML stream into POJO", ex );
		}
		return pojo;
	}

	/**
	 * Lookup a single preference value for the current user.
	 * 
	 * @param prefId
	 *            the id of the preference to lookup. This should be an integer value from 0-200.
	 * 
	 * @return The value of that preference, or null if it is not yet set.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public String data_getUserPreference( Integer prefId ) throws FacebookException, IOException {
		if ( ( prefId < 0 ) || ( prefId > 200 ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The preference id must be an integer value from 0-200." );
		}
		callMethod( FacebookMethod.DATA_GET_USER_PREFERENCE, new Pair<String,CharSequence>( "pref_id", Integer.toString( prefId ) ) );
		if ( ( this.rawResponse == null ) || ( !this.rawResponse.contains( "</data_getUserPreference_response>" ) ) ) {
			return null;
		}
		String result;
		if ( this.rawResponse != null ) {
			result = this.rawResponse.substring( 0, this.rawResponse.indexOf( "</data_getUserPreference_response>" ) );
			result = result.substring( result.indexOf( "facebook.xsd\">" ) + "facebook.xsd\">".length() );
		} else {
			result = null;
		}

		return reconstructValue( result );
	}

	/**
	 * Get a map containing all preference values set for the current user.
	 * 
	 * @return a map of preference values, keyed by preference id. The map will contain all preferences that have been set for the current user. If there are no
	 *         preferences currently set, the map will be empty. The map returned will never be null.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Map<Integer,String> data_getUserPreferences() throws FacebookException, IOException {
		Document response = callMethod( FacebookMethod.DATA_GET_USER_PREFERENCES );
		if ( response == null ) {
			return null;
		}

		Map<Integer,String> results = new TreeMap<Integer,String>();
		NodeList ids = response.getElementsByTagName( "pref_id" );
		NodeList values = response.getElementsByTagName( "value" );
		for ( int count = 0; count < ids.getLength(); count++ ) {
			results
					.put( Integer.parseInt( ids.item( count ).getFirstChild().getTextContent() ),
							reconstructValue( values.item( count ).getFirstChild().getTextContent() ) );
		}

		return results;
	}

	private void checkError() throws FacebookException {
		if ( this.rawResponse.contains( "error_response" ) ) {
			// <error_code>xxx</error_code>
			Integer code = Integer.parseInt( this.rawResponse.substring( this.rawResponse.indexOf( "<error_code>" ) + "<error_code>".length(), this.rawResponse
					.indexOf( "</error_code>" )
					+ "</error_code>".length() ) );
			throw new FacebookException( code, "The request could not be completed!" );
		}
	}

	private String reconstructValue( String input ) {
		if ( ( input == null ) || ( "".equals( input ) ) ) {
			return null;
		}
		if ( input.charAt( 0 ) == '_' ) {
			return input.substring( 1 );
		}
		return input;
	}

	/**
	 * Set a user-preference value. The value can be any string up to 127 characters in length, while the preference id can only be an integer between 0 and 200. Any
	 * preference set applies only to the current user of the application.
	 * 
	 * To clear a user-preference, specify null as the value parameter. The values of "0" and "" will be stored as user-preferences with a literal value of "0" and ""
	 * respectively.
	 * 
	 * @param prefId
	 *            the id of the preference to set, an integer between 0 and 200.
	 * @param value
	 *            the value to store, a String of up to 127 characters in length.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public void data_setUserPreference( Integer prefId, String value ) throws FacebookException, IOException {
		if ( ( prefId < 0 ) || ( prefId > 200 ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The preference id must be an integer value from 0-200." );
		}
		if ( ( value != null ) && ( value.length() > 127 ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The preference value cannot be longer than 128 characters." );
		}

		value = normalizePreferenceValue( value );

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "pref_id", Integer.toString( prefId ) ) );
		params.add( new Pair<String,CharSequence>( "value", value ) );
		callMethod( FacebookMethod.DATA_SET_USER_PREFERENCE, params );
		checkError();
	}

	/**
	 * Set multiple user-preferences values. The values can be strings up to 127 characters in length, while the preference id can only be an integer between 0 and 200.
	 * Any preferences set apply only to the current user of the application.
	 * 
	 * To clear a user-preference, specify null as its value in the map. The values of "0" and "" will be stored as user-preferences with a literal value of "0" and ""
	 * respectively.
	 * 
	 * @param values
	 *            the values to store, specified in a map. The keys should be preference-id values from 0-200, and the values should be strings of up to 127 characters in
	 *            length.
	 * @param replace
	 *            set to true if you want to remove any pre-existing preferences before writing the new ones set to false if you want the new preferences to be merged
	 *            with any pre-existing preferences
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public void data_setUserPreferences( Map<Integer,String> values, boolean replace ) throws FacebookException, IOException {
		JSONObject map = new JSONObject();

		for ( Integer key : values.keySet() ) {
			if ( ( key < 0 ) || ( key > 200 ) ) {
				throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The preference id must be an integer value from 0-200." );
			}
			if ( ( values.get( key ) != null ) && ( values.get( key ).length() > 127 ) ) {
				throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The preference value cannot be longer than 128 characters." );
			}
			try {
				map.put( Integer.toString( key ), normalizePreferenceValue( values.get( key ) ) );
			}
			catch ( JSONException e ) {
				FacebookException ex = new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "Error when translating {key=" + key + ", value=" + values.get( key )
						+ "}to JSON!" );
				ex.setStackTrace( e.getStackTrace() );
				throw ex;
			}
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "values", map.toString() ) );
		if ( replace ) {
			params.add( new Pair<String,CharSequence>( "replace", "true" ) );
		}

		callMethod( FacebookMethod.DATA_SET_USER_PREFERENCES, params );
		checkError();
	}

	private String normalizePreferenceValue( String input ) {
		if ( input == null ) {
			return "0";
		}
		return "_" + input;
	}

	/**
	 * Check to see if the application is permitted to send SMS messages to the current application user.
	 * 
	 * @return true if the application is presently able to send SMS messages to the current user false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean sms_canSend() throws FacebookException, IOException {
		return sms_canSend( users_getLoggedInUser() );
	}

	/**
	 * Check to see if the application is permitted to send SMS messages to the specified user.
	 * 
	 * @param userId
	 *            the UID of the user to check permissions for
	 * 
	 * @return true if the application is presently able to send SMS messages to the specified user false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean sms_canSend( Long userId ) throws FacebookException, IOException {
		callMethod( FacebookMethod.SMS_CAN_SEND, new Pair<String,CharSequence>( "uid", userId.toString() ) );
		if ( this.rawResponse == null ) {
			return false;
		}
		return this.rawResponse.contains( ">0<" ); // a status code of "0" indicates that the app can send messages
	}

	/**
	 * Send an SMS message to the current application user.
	 * 
	 * @param message
	 *            the message to send.
	 * @param smsSessionId
	 *            the SMS session id to use, note that that is distinct from the user's facebook session id. It is used to allow applications to keep track of individual
	 *            SMS conversations/threads for a single user. Specify null if you do not want/need to use a session for the current message.
	 * @param makeNewSession
	 *            set to true to request that Facebook allocate a new SMS session id for this message. The allocated id will be returned as the result of this API call.
	 *            You should only set this to true if you are passing a null 'smsSessionId' value. Otherwise you already have a SMS session id, and do not need a new one.
	 * 
	 * @return an integer specifying the value of the session id alocated by Facebook, if one was requested. If a new session id was not requested, this method will
	 *         return null.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Integer sms_send( String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException, IOException {
		return sms_send( users_getLoggedInUser(), message, smsSessionId, makeNewSession );
	}

	/**
	 * Send an SMS message to the specified user.
	 * 
	 * @param userId
	 *            the id of the user to send the message to.
	 * @param message
	 *            the message to send.
	 * @param smsSessionId
	 *            the SMS session id to use, note that that is distinct from the user's facebook session id. It is used to allow applications to keep track of individual
	 *            SMS conversations/threads for a single user. Specify null if you do not want/need to use a session for the current message.
	 * @param makeNewSession
	 *            set to true to request that Facebook allocate a new SMS session id for this message. The allocated id will be returned as the result of this API call.
	 *            You should only set this to true if you are passing a null 'smsSessionId' value. Otherwise you already have a SMS session id, and do not need a new one.
	 * 
	 * @return an integer specifying the value of the session id alocated by Facebook, if one was requested. If a new session id was not requested, this method will
	 *         return null.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Integer sms_send( Long userId, String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "uid", userId.toString() ) );
		params.add( new Pair<String,CharSequence>( "message", message ) );
		if ( smsSessionId != null ) {
			params.add( new Pair<String,CharSequence>( "session_id", smsSessionId.toString() ) );
		}
		if ( makeNewSession ) {
			params.add( new Pair<String,CharSequence>( "req_session", "true" ) );
		}

		callMethod( FacebookMethod.SMS_SEND, params );

		// XXX: needs testing to make sure it's correct (Facebook always gives me a code 270 permissions error no matter what I do)
		Integer response = null;
		if ( ( this.rawResponse != null ) && ( this.rawResponse.indexOf( "</sms" ) != -1 ) && ( makeNewSession ) ) {
			String result = this.rawResponse.substring( 0, this.rawResponse.indexOf( "</sms" ) );
			result = result.substring( result.lastIndexOf( ">" ) + 1 );
			response = Integer.parseInt( result );
		}

		return response;
	}

	/**
	 * Check to see if the user has granted the app a specific external permission. In order to be granted a permission, an application must direct the user to a URL of
	 * the form:
	 * 
	 * http://www.facebook.com/authorize.php?api_key=[YOUR_API_KEY]&v=1.0&ext_perm=[PERMISSION NAME]
	 * 
	 * @param perm
	 *            the permission to check for
	 * 
	 * @return true if the user has granted the application the specified permission false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean users_hasAppPermission( Permission perm ) throws FacebookException, IOException {
		callMethod( FacebookMethod.USERS_HAS_PERMISSION, new Pair<String,CharSequence>( "ext_perm", perm.getName() ) );
		if ( this.rawResponse == null ) {
			return false;
		}
		return this.rawResponse.contains( ">1<" ); // a code of '1' is sent back to indicate that the user has the request permission
	}

	public boolean users_setStatus( String newStatus, boolean clear ) throws FacebookException, IOException {
		return users_setStatus( newStatus, clear, false );
	}

	/**
	 * Associates the specified FBML markup with the specified handle/id. The markup can then be referenced using the fb:ref FBML tag, to allow a given snippet to be
	 * reused easily across multiple users, and also to allow the application to update the fbml for multiple users more easily without having to make a seperate call for
	 * each user, by just changing the FBML markup that is associated with the handle/id.
	 * 
	 * This method cannot be called by desktop apps.
	 * 
	 * @param handle
	 *            the id to associate the specified markup with. Put this in fb:ref FBML tags to reference your markup.
	 * @param markup
	 *            the FBML markup to store.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean fbml_setRefHandle( String handle, String markup ) throws FacebookException, IOException {
		if ( this._isDesktop ) {
			// this method cannot be called from a desktop app
			return false;
		}
		if ( ( handle == null ) || ( "".equals( handle ) ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The FBML handle may not be null or empty!" );
		}
		if ( markup == null ) {
			markup = "";
		}
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "handle", handle ) );
		params.add( new Pair<String,CharSequence>( "fbml", markup ) );

		return extractBoolean( callMethod( FacebookMethod.FBML_SET_REF_HANDLE, params ) );
	}

	/**
	 * Create a new marketplace listing, or modify an existing one.
	 * 
	 * @param listingId
	 *            the id of the listing to modify, set to 0 (or null) to create a new listing.
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile (Facebook appears to ignore this setting).
	 * @param attributes
	 *            JSON-encoded attributes for this listing.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( Long listingId, boolean showOnProfile, String attributes ) throws FacebookException, IOException {
		if ( listingId == null ) {
			listingId = 0l;
		}
		MarketListing test = new MarketListing( attributes );
		if ( !test.verify() ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The specified listing is invalid!" );
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "listing_id", listingId.toString() ) );
		if ( showOnProfile ) {
			params.add( new Pair<String,CharSequence>( "show_on_profile", "true" ) );
		}
		params.add( new Pair<String,CharSequence>( "listing_attrs", attributes ) );

		return marketplace_createListing( FacebookMethod.MARKET_CREATE_LISTING, params );
	}

	/**
	 * Create a new marketplace listing, or modify an existing one.
	 * 
	 * @param listingId
	 *            the id of the listing to modify, set to 0 (or null) to create a new listing.
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
	 * @param listing
	 *            the listing to publish.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( Long listingId, boolean showOnProfile, MarketListing listing ) throws FacebookException, IOException {
		return marketplace_createListing( listingId, showOnProfile, listing.getAttribs() );
	}

	/**
	 * Create a new marketplace listing.
	 * 
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
	 * @param listing
	 *            the listing to publish.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( boolean showOnProfile, MarketListing listing ) throws FacebookException, IOException {
		return marketplace_createListing( 0l, showOnProfile, listing.getAttribs() );
	}

	/**
	 * Create a new marketplace listing, or modify an existing one.
	 * 
	 * @param listingId
	 *            the id of the listing to modify, set to 0 (or null) to create a new listing.
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
	 * @param listing
	 *            the listing to publish.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( Long listingId, boolean showOnProfile, JSONObject listing ) throws FacebookException, IOException {
		return marketplace_createListing( listingId, showOnProfile, listing.toString() );
	}

	/**
	 * Create a new marketplace listing.
	 * 
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
	 * @param listing
	 *            the listing to publish.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( boolean showOnProfile, JSONObject listing ) throws FacebookException, IOException {
		return marketplace_createListing( 0l, showOnProfile, listing.toString() );
	}

	/**
	 * Return a list of all valid Marketplace categories.
	 * 
	 * @return a list of marketplace categories allowed by Facebook.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public List<String> marketplace_getCategories() throws FacebookException, IOException {
		callMethod( FacebookMethod.MARKET_GET_CATEGORIES );
		if ( this.rawResponse == null ) {
			return null;
		}
		MarketplaceGetCategoriesResponse resp = (MarketplaceGetCategoriesResponse) getResponsePOJO();
		return resp.getMarketplaceCategory();
	}

	/**
	 * Return a list of all valid Marketplace categories.
	 * 
	 * @return a list of marketplace categories allowed by Facebook.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Document marketplace_getCategoriesObject() throws FacebookException, IOException {
		return callMethod( FacebookMethod.MARKET_GET_CATEGORIES );
	}

	/**
	 * Return a list of all valid Marketplace subcategories.
	 * 
	 * @return a list of marketplace subcategories allowed by Facebook.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public List<String> marketplace_getSubCategories() throws FacebookException, IOException {
		callMethod( FacebookMethod.MARKET_GET_SUBCATEGORIES );
		if ( this.rawResponse == null ) {
			return null;
		}
		MarketplaceGetSubCategoriesResponse resp = (MarketplaceGetSubCategoriesResponse) getResponsePOJO();
		return resp.getMarketplaceSubcategory();
	}

	/**
	 * Retrieve listings from the marketplace. The listings can be filtered by listing-id or user-id (or both).
	 * 
	 * @param listingIds
	 *            the ids of listings to filter by, only listings matching the specified ids will be returned.
	 * @param uids
	 *            the ids of users to filter by, only listings submitted by those users will be returned.
	 * 
	 * @return A list of marketplace listings that meet the specified filter criteria.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public List<Listing> marketplace_getListings( List<Long> listingIds, List<Long> uids ) throws FacebookException, IOException {
		String listings = stringify( listingIds );
		String users = stringify( uids );

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( listings != null ) {
			params.add( new Pair<String,CharSequence>( "listing_ids", listings ) );
		}
		if ( uids != null ) {
			params.add( new Pair<String,CharSequence>( "uids", users ) );
		}

		callMethod( FacebookMethod.MARKET_GET_LISTINGS, params );
		if ( this.rawResponse == null ) {
			return null;
		}
		MarketplaceGetListingsResponse resp = (MarketplaceGetListingsResponse) getResponsePOJO();
		return resp.getListing();
	}

	private String stringify( List input ) {
		if ( ( input == null ) || ( input.isEmpty() ) ) {
			return null;
		}
		String result = "";
		for ( Object elem : input ) {
			if ( !"".equals( result ) ) {
				result += ",";
			}
			result += elem.toString();
		}
		return result;
	}

	/**
	 * Search the marketplace listings by category, subcategory, and keyword.
	 * 
	 * @param category
	 *            the category to search in, optional (unless subcategory is specified).
	 * @param subcategory
	 *            the subcategory to search in, optional.
	 * @param searchTerm
	 *            the keyword to search for, optional.
	 * 
	 * @return a list of marketplace entries that match the specified search parameters.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public List<Listing> marketplace_search( MarketListingCategory category, MarketListingSubcategory subcategory, String searchTerm ) throws FacebookException,
			IOException {
		if ( "".equals( searchTerm ) ) {
			searchTerm = null;
		}
		if ( ( subcategory != null ) && ( category == null ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You cannot search by subcategory without also specifying a category!" );
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( category != null ) {
			params.add( new Pair<String,CharSequence>( "category", category.getName() ) );
		}
		if ( subcategory != null ) {
			params.add( new Pair<String,CharSequence>( "subcategory", subcategory.getName() ) );
		}
		if ( searchTerm != null ) {
			params.add( new Pair<String,CharSequence>( "query", searchTerm ) );
		}

		callMethod( FacebookMethod.MARKET_SEARCH, params );
		if ( this.rawResponse == null ) {
			return null;
		}
		MarketplaceSearchResponse resp = (MarketplaceSearchResponse) getResponsePOJO();
		return resp.getListing();
	}

	/**
	 * Remove a listing from the marketplace by id.
	 * 
	 * @param listingId
	 *            the id of the listing to remove.
	 * @param status
	 *            the status to apply when removing the listing. Should be one of MarketListingStatus.SUCCESS or MarketListingStatus.NOT_SUCCESS.
	 * 
	 * @return true if the listing was successfully removed false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean marketplace_removeListing( Long listingId, MarketListingStatus status ) throws FacebookException, IOException {
		if ( status == null ) {
			status = MarketListingStatus.DEFAULT;
		}
		if ( listingId == null ) {
			return false;
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "listing_id", listingId.toString() ) );
		params.add( new Pair<String,CharSequence>( "status", status.getName() ) );
		return marketplace_removeListing( FacebookMethod.MARKET_REMOVE_LISTING, params );
	}

	public boolean users_clearStatus() throws FacebookException, IOException {
		return users_setStatus( null, true );
	}

	/**
	 * Modify a marketplace listing
	 * 
	 * @param listingId
	 *            identifies the listing to be modified
	 * @param showOnProfile
	 *            whether the listing can be shown on the user's profile
	 * @param attrs
	 *            the properties of the listing
	 * @return the id of the edited listing
	 * @see MarketplaceListing
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.createListing"> Developers Wiki: marketplace.createListing</a>
	 * 
	 * @deprecated provided for legacy support only. Please use the version that takes a MarketListing instead.
	 */
	@Deprecated
	public Long marketplace_editListing( Long listingId, Boolean showOnProfile, MarketplaceListing attrs ) throws FacebookException, IOException {
		return marketplace_createListing( listingId, showOnProfile, attrs.getAttribs() );
	}

	/**
	 * Modify a marketplace listing
	 * 
	 * @param listingId
	 *            identifies the listing to be modified
	 * @param showOnProfile
	 *            whether the listing can be shown on the user's profile
	 * @param attrs
	 *            the properties of the listing
	 * @return the id of the edited listing
	 * @see MarketplaceListing
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.createListing"> Developers Wiki: marketplace.createListing</a>
	 */
	public Long marketplace_editListing( Long listingId, Boolean showOnProfile, MarketListing attrs ) throws FacebookException, IOException {
		return marketplace_createListing( listingId, showOnProfile, attrs );
	}

	/**
	 * Publish a story to the logged-in user's newsfeed.
	 * 
	 * @param title
	 *            the title of the feed story
	 * @param body
	 *            the body of the feed story
	 * @param images
	 *            (optional) up to four pairs of image URLs and (possibly null) link URLs
	 * @return whether the story was successfully published; false in case of permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser"> Developers Wiki: Feed.publishStoryToUser</a>
	 */
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images ) throws FacebookException,
			IOException {
		return feed_publishStoryToUser( title, body, images, null );
	}

	/**
	 * Create a marketplace listing
	 * 
	 * @param showOnProfile
	 *            whether the listing can be shown on the user's profile
	 * @param attrs
	 *            the properties of the listing
	 * @return the id of the created listing
	 * @see MarketplaceListing
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.createListing"> Developers Wiki: marketplace.createListing</a>
	 * 
	 * @deprecated provided for legacy support only.
	 */
	@Deprecated
	public Long marketplace_createListing( Boolean showOnProfile, MarketplaceListing attrs ) throws FacebookException, IOException {
		return marketplace_createListing( null, showOnProfile, attrs.getAttribs() );
	}

	@Deprecated
	public long auth_getUserId( String authToken ) throws FacebookException, IOException {
		if ( null == this.cacheSessionKey ) {
			auth_getSession( authToken );
		}
		return users_getLoggedInUser();
	}

	/**
	 * @deprecated use feed_publishTemplatizedAction instead.
	 */
	@Deprecated
	public boolean feed_publishActionOfUser( CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images ) throws FacebookException,
			IOException {
		return feed_publishActionOfUser( title, body, images, null );
	}

	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate ) throws FacebookException, IOException {
		return feed_publishTemplatizedAction( actorId, titleTemplate == null ? null : titleTemplate.toString(), null, null, null, null, null, null );
	}

	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException, IOException {
		return feed_publishTemplatizedActionInternal( actorId, titleTemplate == null ? null : titleTemplate.toString(), mapToJsonString( titleData ),
				bodyTemplate == null ? null : bodyTemplate.toString(), mapToJsonString( bodyData ), bodyGeneral == null ? null : bodyGeneral.toString(), images,
				targetIds, null );
	}

	private String mapToJsonString( Map<String,CharSequence> map ) {
		if ( null == map || map.isEmpty() ) {
			return null;
		}
		JSONObject titleDataJson = new JSONObject();
		for ( String key : map.keySet() ) {
			try {
				titleDataJson.put( key, map.get( key ) );
			}
			catch ( Exception ignored ) {
				// ignore
			}
		}
		return titleDataJson.toString();
	}

	private boolean feed_publishTemplatizedActionInternal( Long actor, String titleTemp, String titleData, String bodyTemp, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> images, Collection<Long> targetIds, Long pageId ) throws FacebookException, IOException {
		if ( ( targetIds != null ) && ( !targetIds.isEmpty() ) ) {
			return templatizedFeedHandler( actor, FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, titleTemp, titleData, bodyTemp, bodyData, bodyGeneral, images, delimit(
					targetIds ).toString(), pageId );
		} else {
			return templatizedFeedHandler( actor, FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, titleTemp, titleData, bodyTemp, bodyData, bodyGeneral, images, null,
					pageId );
		}
	}

	/**
	 * @deprecated provided for legacy support only. Use the version that returns a List<String> instead.
	 */
	@Deprecated
	public Document marketplace_getListings( Collection<Long> listingIds, Collection<Long> userIds ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.MARKETPLACE_GET_LISTINGS.numParams() );
		if ( null != listingIds && !listingIds.isEmpty() ) {
			params.add( new Pair<String,CharSequence>( "listing_ids", delimit( listingIds ) ) );
		}
		if ( null != userIds && !userIds.isEmpty() ) {
			params.add( new Pair<String,CharSequence>( "uids", delimit( userIds ) ) );
		}

		assert !params.isEmpty() : "Either listingIds or userIds should be provided";
		return callMethod( FacebookMethod.MARKETPLACE_GET_LISTINGS, params );
	}

	public Document marketplace_getSubCategories( CharSequence category ) throws FacebookException, IOException {
		if ( category != null ) {
			return callMethod( FacebookMethod.MARKET_GET_SUBCATEGORIES, new Pair<String,CharSequence>( "category", category ) );
		}
		return callMethod( FacebookMethod.MARKET_GET_SUBCATEGORIES );
	}

	public boolean marketplace_removeListing( Long listingId ) throws FacebookException, IOException {
		return marketplace_removeListing( listingId, MarketListingStatus.DEFAULT );
	}

	/**
	 * @deprecated provided for legacy support only. Use marketplace_removeListing(Long, MarketListingStatus) instead.
	 */
	@Deprecated
	public boolean marketplace_removeListing( Long listingId, CharSequence status ) throws FacebookException, IOException {
		return marketplace_removeListing( listingId );
	}

	/**
	 * @deprecated provided for legacy support only. Use the version that returns a List<Listing> instead.
	 */
	@Deprecated
	public Document marketplace_search( CharSequence category, CharSequence subCategory, CharSequence query ) throws FacebookException, IOException {
		if ( "".equals( query ) ) {
			query = null;
		}
		if ( ( subCategory != null ) && ( category == null ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You cannot search by subcategory without also specifying a category!" );
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( category != null ) {
			params.add( new Pair<String,CharSequence>( "category", category ) );
		}
		if ( subCategory != null ) {
			params.add( new Pair<String,CharSequence>( "subcategory", subCategory ) );
		}
		if ( query != null ) {
			params.add( new Pair<String,CharSequence>( "query", query ) );
		}

		return callMethod( FacebookMethod.MARKET_SEARCH, params );
	}

	/**
	 * @deprecated provided for legacy support only. Use users_hasAppPermission(Permission) instead.
	 */
	@Deprecated
	public boolean users_hasAppPermission( CharSequence permission ) throws FacebookException, IOException {
		callMethod( FacebookMethod.USERS_HAS_PERMISSION, new Pair<String,CharSequence>( "ext_perm", permission ) );
		if ( this.rawResponse == null ) {
			return false;
		}
		return this.rawResponse.contains( ">1<" ); // a code of '1' is sent back to indicate that the user has the request permission
	}

	public boolean users_setStatus( String status ) throws FacebookException, IOException {
		return users_setStatus( status, false );
	}

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param albumId
	 *            retrieve from photos from this album (optional)
	 * @param photoIds
	 *            retrieve from this list of photos (optional)
	 * @return an T of photo objects.
	 * @see #photos_get(Integer, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public Document photos_getByAlbum( Long albumId, Collection<Long> photoIds ) throws FacebookException, IOException {
		return photos_get( null /* subjId */, albumId, photoIds );
	}

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param albumId
	 *            retrieve from photos from this album (optional)
	 * @return an T of photo objects.
	 * @see #photos_get(Integer, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public Document photos_getByAlbum( Long albumId ) throws FacebookException, IOException {
		return photos_get( null /* subjId */, albumId, null /* photoIds */);
	}

	/**
	 * Sends a message via SMS to the user identified by <code>userId</code> in response to a user query associated with <code>mobileSessionId</code>.
	 * 
	 * @param userId
	 *            a user ID
	 * @param response
	 *            the message to be sent via SMS
	 * @param mobileSessionId
	 *            the mobile session
	 * @throws FacebookException
	 *             in case of error
	 * @throws IOException
	 * @see FacebookExtendedPerm#SMS
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Application_generated_messages"> Developers Wiki: Mobile: Application Generated Messages</a>
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Workflow"> Developers Wiki: Mobile: Workflow</a>
	 */
	public void sms_sendResponse( Integer userId, CharSequence response, Integer mobileSessionId ) throws FacebookException, IOException {
		callMethod( FacebookMethod.SMS_SEND_MESSAGE, new Pair<String,CharSequence>( "uid", userId.toString() ), new Pair<String,CharSequence>( "message", response ),
				new Pair<String,CharSequence>( "session_id", mobileSessionId.toString() ) );
	}

	/**
	 * Sends a message via SMS to the user identified by <code>userId</code>. The SMS extended permission is required for success.
	 * 
	 * @param userId
	 *            a user ID
	 * @param message
	 *            the message to be sent via SMS
	 * @throws FacebookException
	 *             in case of error
	 * @throws IOException
	 * @see FacebookExtendedPerm#SMS
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Application_generated_messages"> Developers Wiki: Mobile: Application Generated Messages</a>
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Workflow"> Developers Wiki: Mobile: Workflow</a>
	 */
	public void sms_sendMessage( Long userId, CharSequence message ) throws FacebookException, IOException {
		callMethod( FacebookMethod.SMS_SEND_MESSAGE, new Pair<String,CharSequence>( "uid", userId.toString() ), new Pair<String,CharSequence>( "message", message ),
				new Pair<String,CharSequence>( "req_session", "0" ) );
	}

	/**
	 * Sends a message via SMS to the user identified by <code>userId</code>, with the expectation that the user will reply. The SMS extended permission is required
	 * for success. The returned mobile session ID can be stored and used in {@link #sms_sendResponse} when the user replies.
	 * 
	 * @param userId
	 *            a user ID
	 * @param message
	 *            the message to be sent via SMS
	 * @return a mobile session ID (can be used in {@link #sms_sendResponse})
	 * @throws FacebookException
	 *             in case of error, e.g. SMS is not enabled
	 * @throws IOException
	 * @see FacebookExtendedPerm#SMS
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Application_generated_messages"> Developers Wiki: Mobile: Application Generated Messages</a>
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Workflow"> Developers Wiki: Mobile: Workflow</a>
	 */
	public int sms_sendMessageWithSession( Long userId, CharSequence message ) throws FacebookException, IOException {
		return extractInt( callMethod( FacebookMethod.SMS_SEND_MESSAGE, new Pair<String,CharSequence>( "uid", userId.toString() ), new Pair<String,CharSequence>(
				"message", message ), new Pair<String,CharSequence>( "req_session", "1" ) ) );
	}

	/**
	 * Extracts an Integer from a document that consists of an Integer only.
	 * 
	 * @param doc
	 * @return the Integer
	 */
	public static int extractInt( Node doc ) {
		if ( doc == null ) {
			return 0;
		}
		return Integer.parseInt( doc.getFirstChild().getTextContent() );
	}

	/**
	 * Retrieves the requested profile fields for the Facebook Pages with the given <code>pageIds</code>. Can be called for pages that have added the application
	 * without establishing a session.
	 * 
	 * @param pageIds
	 *            the page IDs
	 * @param fields
	 *            a set of page profile fields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo"> Developers Wiki: Pages.getInfo</a>
	 */
	public Document pages_getInfo( Collection<Long> pageIds, EnumSet<PageProfileField> fields ) throws FacebookException, IOException {
		if ( pageIds == null || pageIds.isEmpty() ) {
			throw new IllegalArgumentException( "pageIds cannot be empty or null" );
		}
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		IFacebookMethod method = null == this.cacheSessionKey ? FacebookMethod.PAGES_GET_INFO_NO_SESSION : FacebookMethod.PAGES_GET_INFO;
		return callMethod( method, new Pair<String,CharSequence>( "page_ids", delimit( pageIds ) ), new Pair<String,CharSequence>( "fields", delimit( fields ) ) );
	}

	/**
	 * Retrieves the requested profile fields for the Facebook Pages with the given <code>pageIds</code>. Can be called for pages that have added the application
	 * without establishing a session.
	 * 
	 * @param pageIds
	 *            the page IDs
	 * @param fields
	 *            a set of page profile fields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo"> Developers Wiki: Pages.getInfo</a>
	 */
	public Document pages_getInfo( Collection<Long> pageIds, Set<CharSequence> fields ) throws FacebookException, IOException {
		if ( pageIds == null || pageIds.isEmpty() ) {
			throw new IllegalArgumentException( "pageIds cannot be empty or null" );
		}
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		IFacebookMethod method = null == this.cacheSessionKey ? FacebookMethod.PAGES_GET_INFO_NO_SESSION : FacebookMethod.PAGES_GET_INFO;
		return callMethod( method, new Pair<String,CharSequence>( "page_ids", delimit( pageIds ) ), new Pair<String,CharSequence>( "fields", delimit( fields ) ) );
	}

	/**
	 * Retrieves the requested profile fields for the Facebook Pages of the user with the given <code>userId</code>.
	 * 
	 * @param userId
	 *            the ID of a user about whose pages to fetch info (defaulted to the logged-in user)
	 * @param fields
	 *            a set of PageProfileFields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo"> Developers Wiki: Pages.getInfo</a>
	 */
	public Document pages_getInfo( Long userId, EnumSet<PageProfileField> fields ) throws FacebookException, IOException {
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		if ( userId == null ) {
			userId = this.cacheUserId;
		}
		return callMethod( FacebookMethod.PAGES_GET_INFO, new Pair<String,CharSequence>( "uid", userId.toString() ), new Pair<String,CharSequence>( "fields",
				delimit( fields ) ) );
	}

	/**
	 * Retrieves the requested profile fields for the Facebook Pages of the user with the given <code>userId</code>.
	 * 
	 * @param userId
	 *            the ID of a user about whose pages to fetch info (defaulted to the logged-in user)
	 * @param fields
	 *            a set of page profile fields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo"> Developers Wiki: Pages.getInfo</a>
	 */
	public Document pages_getInfo( Long userId, Set<CharSequence> fields ) throws FacebookException, IOException {
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		if ( userId == null ) {
			userId = this.cacheUserId;
		}
		return callMethod( FacebookMethod.PAGES_GET_INFO, new Pair<String,CharSequence>( "uid", userId.toString() ), new Pair<String,CharSequence>( "fields",
				delimit( fields ) ) );
	}

	/**
	 * Checks whether a page has added the application
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the page has added the application
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isAppAdded"> Developers Wiki: Pages.isAppAdded</a>
	 */
	public boolean pages_isAppAdded( Long pageId ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_APP_ADDED, new Pair<String,CharSequence>( "page_id", pageId.toString() ) ) );
	}

	/**
	 * Checks whether a user is a fan of the page with the given <code>pageId</code>.
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @param userId
	 *            the ID of the user (defaults to the logged-in user if null)
	 * @return true if the user is a fan of the page
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isFan"> Developers Wiki: Pages.isFan</a>
	 */
	public boolean pages_isFan( Long pageId, Long userId ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_FAN, new Pair<String,CharSequence>( "page_id", pageId.toString() ), new Pair<String,CharSequence>(
				"uid", userId.toString() ) ) );
	}

	/**
	 * Checks whether the logged-in user is a fan of the page with the given <code>pageId</code>.
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the logged-in user is a fan of the page
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isFan"> Developers Wiki: Pages.isFan</a>
	 */
	public boolean pages_isFan( Long pageId ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_FAN, new Pair<String,CharSequence>( "page_id", pageId.toString() ) ) );
	}

	/**
	 * Checks whether the logged-in user for this session is an admin of the page with the given <code>pageId</code>.
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the logged-in user is an admin
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isAdmin"> Developers Wiki: Pages.isAdmin</a>
	 */
	public boolean pages_isAdmin( Long pageId ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_ADMIN, new Pair<String,CharSequence>( "page_id", pageId.toString() ) ) );
	}

	/**
	 * @deprecated use the version that treats actorId as a Long. UID's *are not ever to be* expressed as Integers.
	 */
	@Deprecated
	public boolean feed_publishTemplatizedAction( Integer actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException, IOException {
		return feed_publishTemplatizedAction( (long) ( actorId.intValue() ), titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images );
	}

	public void notifications_send( Collection<Long> recipientIds, CharSequence notification ) throws FacebookException, IOException {
		this.notifications_send( recipientIds, notification.toString(), false );
	}

	private Document notifications_sendEmail( CharSequence recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException,
			IOException {
		if ( null == recipients || "".equals( recipients ) ) {
			// we throw an exception here because returning a sensible result (like an empty list) is problematic due to the use of Document as the return type
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You must specify at least one recipient when sending an email!" );
		}
		if ( ( null == email || "".equals( email ) ) && ( null == fbml || "".equals( fbml ) ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You cannot send an empty email!" );
		}
		Document d;
		String paramName = "text";
		String paramValue;
		if ( ( email == null ) || ( "".equals( email.toString() ) ) ) {
			paramValue = fbml.toString();
			paramName = "fbml";
		} else {
			paramValue = email.toString();
		}

		// session is only required to send email from a desktop app
		FacebookMethod method = isDesktop() ? FacebookMethod.NOTIFICATIONS_SEND_EMAIL_SESSION : FacebookMethod.NOTIFICATIONS_SEND_EMAIL;
		if ( ( subject != null ) && ( !"".equals( subject ) ) ) {
			d = callMethod( method, new Pair<String,CharSequence>( "recipients", recipients ), new Pair<String,CharSequence>( "subject", subject ),
					new Pair<String,CharSequence>( paramName, paramValue ) );
		} else {
			d = callMethod( method, new Pair<String,CharSequence>( "recipients", recipients ), new Pair<String,CharSequence>( paramName, paramValue ) );
		}

		return d;
	}

	public Document notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException,
			IOException {
		return notifications_sendEmail( delimit( recipients ), subject, email, fbml );
	}

	public Document notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException, IOException {
		Long currentUser = users_getLoggedInUser();
		return notifications_sendEmail( currentUser.toString(), subject, email, fbml );
	}

	public Document notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException, IOException {
		return notifications_sendEmail( delimit( recipients ), subject, null, fbml );
	}

	public Document notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException, IOException {
		Long currentUser = users_getLoggedInUser();
		return notifications_sendEmail( currentUser.toString(), subject, null, fbml );
	}

	public Document notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException, IOException {
		return notifications_sendEmail( delimit( recipients ), subject, email, null );
	}

	public Document notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException, IOException {
		Long currentUser = users_getLoggedInUser();
		return notifications_sendEmail( currentUser.toString(), subject, email, null );
	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( newStatus != null ) {
			params.add( new Pair<String,CharSequence>( "status", newStatus ) );
		}
		if ( clear ) {
			params.add( new Pair<String,CharSequence>( "clear", "1" ) );
		}
		if ( statusIncludesVerb ) {
			params.add( new Pair<String,CharSequence>( "status_includes_verb", "true" ) );
		}
		return extractBoolean( callMethod( FacebookMethod.USERS_SET_STATUS, params ) );
	}

	/**
	 * Send a notification message to the logged-in user.
	 * 
	 * @param notification
	 *            the FBML to be displayed on the notifications page; only a stripped-down set of FBML tags that result in text and links is allowed
	 * @return a URL, possibly null, to which the user should be redirected to finalize the sending of the email
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.sendEmail"> Developers Wiki: notifications.send</a>
	 */
	public void notifications_send( CharSequence notification ) throws FacebookException, IOException {
		Long currentUser = users_getLoggedInUser();
		Collection<Long> coll = new ArrayList<Long>();
		coll.add( currentUser );
		notifications_send( coll, notification );
	}

	/**
	 * Sends a notification email to the specified users, who must have added your application. You can send five (5) emails to a user per day. Requires a session key for
	 * desktop applications, which may only send email to the person whose session it is. This method does not require a session for Web applications. Either
	 * <code>fbml</code> or <code>text</code> must be specified.
	 * 
	 * @param recipientIds
	 *            up to 100 user ids to which the message is to be sent
	 * @param subject
	 *            the subject of the notification email (optional)
	 * @param fbml
	 *            markup to be sent to the specified users via email; only a stripped-down set of FBML tags that result in text, links and linebreaks is allowed
	 * @param text
	 *            the plain text to send to the specified users via email
	 * @return a comma-separated list of the IDs of the users to whom the email was successfully sent
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.send"> Developers Wiki: notifications.sendEmail</a>
	 * 
	 * @deprecated provided for legacy support only, please use one of the alternate notifications_sendEmail calls.
	 */
	@Deprecated
	public String notifications_sendEmailStr( Collection<Long> recipientIds, CharSequence subject, CharSequence fbml, CharSequence text ) throws FacebookException,
			IOException {
		if ( null == recipientIds || recipientIds.isEmpty() ) {
			throw new IllegalArgumentException( "List of email recipients cannot be empty" );
		}
		boolean hasText = null != text && ( 0 != text.length() );
		boolean hasFbml = null != fbml && ( 0 != fbml.length() );
		if ( !hasText && !hasFbml ) {
			throw new IllegalArgumentException( "Text and/or fbml must not be empty" );
		}
		ArrayList<Pair<String,CharSequence>> args = new ArrayList<Pair<String,CharSequence>>( 4 );
		args.add( new Pair<String,CharSequence>( "recipients", delimit( recipientIds ) ) );
		args.add( new Pair<String,CharSequence>( "subject", subject ) );
		if ( hasText ) {
			args.add( new Pair<String,CharSequence>( "text", text ) );
		}
		if ( hasFbml ) {
			args.add( new Pair<String,CharSequence>( "fbml", fbml ) );
		}
		// this method requires a session only if we're dealing with a desktop app
		Document result = callMethod( isDesktop() ? FacebookMethod.NOTIFICATIONS_SEND_EMAIL_SESSION : FacebookMethod.NOTIFICATIONS_SEND_EMAIL, args );
		return extractString( result );
	}

	/**
	 * Sends a notification email to the specified users, who must have added your application. You can send five (5) emails to a user per day. Requires a session key for
	 * desktop applications, which may only send email to the person whose session it is. This method does not require a session for Web applications.
	 * 
	 * @param recipientIds
	 *            up to 100 user ids to which the message is to be sent
	 * @param subject
	 *            the subject of the notification email (optional)
	 * @param fbml
	 *            markup to be sent to the specified users via email; only a stripped-down set of FBML that allows only tags that result in text, links and linebreaks is
	 *            allowed
	 * @return a comma-separated list of the IDs of the users to whom the email was successfully sent
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.send"> Developers Wiki: notifications.sendEmail</a>
	 * 
	 * @deprecated provided for legacy support only, please use one of the alternate notifications_sendEmail calls.
	 */
	@Deprecated
	public String notifications_sendEmail( Collection<Long> recipientIds, CharSequence subject, CharSequence fbml ) throws FacebookException, IOException {
		return notifications_sendEmailStr( recipientIds, subject, fbml, /* text */null );
	}

	/**
	 * Sends a notification email to the specified users, who must have added your application. You can send five (5) emails to a user per day. Requires a session key for
	 * desktop applications, which may only send email to the person whose session it is. This method does not require a session for Web applications.
	 * 
	 * @param recipientIds
	 *            up to 100 user ids to which the message is to be sent
	 * @param subject
	 *            the subject of the notification email (optional)
	 * @param text
	 *            the plain text to send to the specified users via email
	 * @return a comma-separated list of the IDs of the users to whom the email was successfully sent
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.sendEmail"> Developers Wiki: notifications.sendEmail</a>
	 * 
	 * @deprecated provided for legacy support only, please use one of the alternate notifications_sendEmail calls.
	 */
	@Deprecated
	public String notifications_sendEmailPlain( Collection<Long> recipientIds, CharSequence subject, CharSequence text ) throws FacebookException, IOException {
		return notifications_sendEmailStr( recipientIds, subject, /* fbml */null, text );
	}

	/**
	 * Extracts a String from a T consisting entirely of a String.
	 * 
	 * @return the String
	 */
	public static String extractString( Node d ) {
		if ( d == null ) {
			return null;
		}
		return d.getFirstChild().getTextContent();
	}

	public boolean admin_setAppProperties( Map<ApplicationProperty,String> properties ) throws FacebookException, IOException {
		if ( this._isDesktop ) {
			// this method cannot be called from a desktop app
			return false;
		}

		if ( ( properties == null ) || ( properties.isEmpty() ) ) {
			// nothing to do
			return true;
		}

		// Facebook is nonspecific about how they want the parameters encoded in JSON, so we make two attempts
		JSONObject encoding1 = new JSONObject();
		JSONArray encoding2 = new JSONArray();
		for ( ApplicationProperty property : properties.keySet() ) {
			JSONObject temp = new JSONObject();
			if ( property.getType().equals( "string" ) ) {
				// simple case, just treat it as a literal string
				try {
					encoding1.put( property.getName(), properties.get( property ) );
					temp.put( property.getName(), properties.get( property ) );
					encoding2.put( temp );
				}
				catch ( JSONException ignored ) {
					// ignore
				}
			} else {
				// we need to parse a boolean value
				String val = properties.get( property );
				if ( ( val == null ) || ( val.equals( "" ) ) || ( val.equalsIgnoreCase( "false" ) ) || ( val.equals( "0" ) ) ) {
					// false
					val = "0";
				} else {
					// true
					val = "1";
				}
				try {
					encoding1.put( property.getName(), val );
					temp.put( property.getName(), val );
					encoding2.put( temp );
				}
				catch ( JSONException ignored ) {
					// ignore
				}
			}
		}

		// now we've built our JSON-encoded parameter, so attempt to set the properties
		try {
			// first assume that Facebook is sensible enough to be able to undestand an associative array
			Document d = callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, new Pair<String,CharSequence>( "properties", encoding1.toString() ) );
			return extractBoolean( d );
		}
		catch ( FacebookException e ) {
			// if that didn't work, try the more convoluted encoding (which matches what they send back in response to admin_getAppProperties calls)
			Document d = callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, new Pair<String,CharSequence>( "properties", encoding2.toString() ) );
			return extractBoolean( d );
		}
	}

	/**
	 * @deprecated use admin_getAppPropertiesMap() instead
	 */
	@Deprecated
	public JSONObject admin_getAppProperties( Collection<ApplicationProperty> properties ) throws FacebookException, IOException {
		String json = admin_getAppPropertiesAsString( properties );
		if ( json == null ) {
			return null;
		}
		try {
			if ( json.matches( "\\{.*\\}" ) ) {
				return new JSONObject( json );
			} else {
				JSONArray temp = new JSONArray( json );
				JSONObject result = new JSONObject();
				for ( int count = 0; count < temp.length(); count++ ) {
					JSONObject obj = (JSONObject) temp.get( count );
					Iterator it = obj.keys();
					while ( it.hasNext() ) {
						String next = (String) it.next();
						result.put( next, obj.get( next ) );
					}
				}
				return result;
			}
		}
		catch ( Exception e ) {
			// response failed to parse
			throw new FacebookException( ErrorCode.GEN_SERVICE_ERROR, "Failed to parse server response:  " + json );
		}
	}

	public Map<ApplicationProperty,String> admin_getAppPropertiesMap( Collection<ApplicationProperty> properties ) throws FacebookException, IOException {
		Map<ApplicationProperty,String> result = new LinkedHashMap<ApplicationProperty,String>();
		String json = admin_getAppPropertiesAsString( properties );
		if ( json == null ) {
			return null;
		}
		if ( json.matches( "\\{.*\\}" ) ) {
			json = json.substring( 1, json.lastIndexOf( "}" ) );
		} else {
			json = json.substring( 1, json.lastIndexOf( "]" ) );
		}
		String[] parts = json.split( "\\," );
		for ( String part : parts ) {
			parseFragment( part, result );
		}

		return result;
	}

	private void parseFragment( String fragment, Map<ApplicationProperty,String> result ) {
		if ( fragment.startsWith( "{" ) ) {
			fragment = fragment.substring( 1, fragment.lastIndexOf( "}" ) );
		}
		String keyString = fragment.substring( 1 );
		keyString = keyString.substring( 0, keyString.indexOf( '"' ) );
		ApplicationProperty key = ApplicationProperty.getPropertyForString( keyString );
		String value = fragment.substring( fragment.indexOf( ":" ) + 1 ).replaceAll( "\\\\", "" ); // strip escape characters
		if ( key.getType().equals( "string" ) ) {
			result.put( key, value.substring( 1, value.lastIndexOf( '"' ) ) );
		} else {
			if ( value.equals( "1" ) ) {
				result.put( key, "true" );
			} else {
				result.put( key, "false" );
			}
		}
	}

	public String admin_getAppPropertiesAsString( Collection<ApplicationProperty> properties ) throws FacebookException, IOException {
		if ( this._isDesktop ) {
			// this method cannot be called from a desktop app
			throw new FacebookException( ErrorCode.GEN_PERMISSIONS_ERROR, "Desktop applications cannot use 'admin.getAppProperties'" );
		}
		JSONArray props = new JSONArray();
		for ( ApplicationProperty property : properties ) {
			props.put( property.getName() );
		}
		Document d = callMethod( FacebookMethod.ADMIN_GET_APP_PROPERTIES, new Pair<String,CharSequence>( "properties", props.toString() ) );
		return extractString( d );
	}

	public Document data_getCookies() throws FacebookException, IOException {
		return data_getCookies( users_getLoggedInUser(), null );
	}

	public Document data_getCookies( Long userId ) throws FacebookException, IOException {
		return data_getCookies( userId, null );
	}

	public Document data_getCookies( String name ) throws FacebookException, IOException {
		return data_getCookies( users_getLoggedInUser(), name );
	}

	public Document data_getCookies( Long userId, CharSequence name ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> args = new ArrayList<Pair<String,CharSequence>>();
		args.add( new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
		if ( ( name != null ) && ( !"".equals( name ) ) ) {
			args.add( new Pair<String,CharSequence>( "name", name ) );
		}

		return callMethod( FacebookMethod.DATA_GET_COOKIES, args );
	}

	public boolean data_setCookie( String name, String value ) throws FacebookException, IOException {
		return data_setCookie( users_getLoggedInUser(), name, value, null, null );
	}

	public boolean data_setCookie( String name, String value, String path ) throws FacebookException, IOException {
		return data_setCookie( users_getLoggedInUser(), name, value, null, path );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value ) throws FacebookException, IOException {
		return data_setCookie( userId, name, value, null, null );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, CharSequence path ) throws FacebookException, IOException {
		return data_setCookie( userId, name, value, null, path );
	}

	public boolean data_setCookie( String name, String value, Long expires ) throws FacebookException, IOException {
		return data_setCookie( users_getLoggedInUser(), name, value, expires, null );
	}

	public boolean data_setCookie( String name, String value, Long expires, String path ) throws FacebookException, IOException {
		return data_setCookie( users_getLoggedInUser(), name, value, expires, path );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires ) throws FacebookException, IOException {
		return data_setCookie( userId, name, value, expires, null );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires, CharSequence path ) throws FacebookException, IOException {
		if ( ( name == null ) || ( "".equals( name ) ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The cookie name cannot be null or empty!" );
		}
		if ( value == null ) {
			value = "";
		}

		Document doc;
		List<Pair<String,CharSequence>> args = new ArrayList<Pair<String,CharSequence>>();
		args.add( new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
		args.add( new Pair<String,CharSequence>( "name", name ) );
		args.add( new Pair<String,CharSequence>( "value", value ) );
		if ( ( expires != null ) && ( expires > 0 ) ) {
			args.add( new Pair<String,CharSequence>( "expires", expires.toString() ) );
		}
		if ( ( path != null ) && ( !"".equals( path ) ) ) {
			args.add( new Pair<String,CharSequence>( "path", path ) );
		}
		doc = callMethod( FacebookMethod.DATA_SET_COOKIE, args );

		return extractBoolean( doc );
	}

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate ) throws FacebookException, IOException {
		return feed_publishTemplatizedAction( titleTemplate, null );
	}

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Long pageActorId ) throws FacebookException, IOException {
		return feed_publishTemplatizedAction( titleTemplate, null, null, null, null, null, null, pageActorId );
	}

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images,
			Long pageActorId ) throws FacebookException, IOException {
		return feed_publishTemplatizedActionInternal( null, titleTemplate == null ? null : titleTemplate.toString(), mapToJsonString( titleData ),
				bodyTemplate == null ? null : bodyTemplate.toString(), mapToJsonString( bodyData ), bodyGeneral == null ? null : bodyGeneral.toString(), images,
				targetIds, pageActorId );
	}

	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup ) throws FacebookException, IOException {
		return profile_setFBML( users_getLoggedInUser(), profileFbmlMarkup == null ? null : profileFbmlMarkup.toString(), profileActionFbmlMarkup == null ? null
				: profileActionFbmlMarkup.toString(), null );
	}

	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, Long profileId ) throws FacebookException, IOException {
		return profile_setFBML( profileId, profileFbmlMarkup == null ? null : profileFbmlMarkup.toString(), profileActionFbmlMarkup == null ? null
				: profileActionFbmlMarkup.toString(), null );
	}

	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup ) throws FacebookException,
			IOException {
		return profile_setFBML( users_getLoggedInUser(), profileFbmlMarkup == null ? null : profileFbmlMarkup.toString(), profileActionFbmlMarkup == null ? null
				: profileActionFbmlMarkup.toString(), mobileFbmlMarkup == null ? null : mobileFbmlMarkup.toString() );
	}

	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup, Long profileId )
			throws FacebookException, IOException {
		return profile_setFBML( profileId, profileFbmlMarkup == null ? null : profileFbmlMarkup.toString(), profileActionFbmlMarkup == null ? null
				: profileActionFbmlMarkup.toString(), mobileFbmlMarkup == null ? null : mobileFbmlMarkup.toString() );
	}

	public boolean profile_setMobileFBML( CharSequence fbmlMarkup ) throws FacebookException, IOException {
		return profile_setFBML( users_getLoggedInUser(), null, null, fbmlMarkup == null ? null : fbmlMarkup.toString() );
	}

	public boolean profile_setMobileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException, IOException {
		return profile_setFBML( profileId, null, null, fbmlMarkup == null ? null : fbmlMarkup.toString() );
	}

	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup ) throws FacebookException, IOException {
		return profile_setFBML( users_getLoggedInUser(), null, fbmlMarkup == null ? null : fbmlMarkup.toString(), null );
	}

	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException, IOException {
		return profile_setFBML( profileId, null, fbmlMarkup == null ? null : fbmlMarkup.toString(), null );
	}

	public boolean profile_setProfileFBML( CharSequence fbmlMarkup ) throws FacebookException, IOException {
		return profile_setFBML( users_getLoggedInUser(), fbmlMarkup == null ? null : fbmlMarkup.toString(), null, null );
	}

	public boolean profile_setProfileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException, IOException {
		return profile_setFBML( profileId, fbmlMarkup == null ? null : fbmlMarkup.toString(), null, null );
	}

	public Document friends_get( Long friendListId ) throws FacebookException, IOException {
		FacebookMethod method = FacebookMethod.FRIENDS_GET;
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( method.numParams() );
		if ( null != friendListId ) {
			if ( 0L >= friendListId ) {
				throw new IllegalArgumentException( "given invalid friendListId " + friendListId.toString() );
			}
			params.add( new Pair<String,CharSequence>( "flid", friendListId.toString() ) );
		}
		return callMethod( method, params );
	}

	public Document friends_getLists() throws FacebookException, IOException {
		return callMethod( FacebookMethod.FRIENDS_GET_LISTS );
	}

	/**
	 * Sets several property values for an application. The properties available are analogous to the ones editable via the Facebook Developer application. A session is
	 * not required to use this method.
	 * 
	 * This method cannot be called from a desktop app.
	 * 
	 * @param properties
	 *            an ApplicationPropertySet that is translated into a single JSON String.
	 * @return a boolean indicating whether the properties were successfully set
	 */
	public boolean admin_setAppProperties( ApplicationPropertySet properties ) throws FacebookException, IOException {
		if ( this._isDesktop ) {
			// this method cannot be called from a desktop app
			return false;
		}
		if ( null == properties || properties.isEmpty() ) {
			throw new IllegalArgumentException( "expecting a non-empty set of application properties" );
		}
		return extractBoolean( callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, new Pair<String,CharSequence>( "properties", properties.toJsonString() ) ) );
	}

	/**
	 * Gets property values previously set for an application on either the Facebook Developer application or the with the <code>admin.setAppProperties</code> call. A
	 * session is not required to use this method.
	 * 
	 * @param properties
	 *            an enumeration of the properties to get
	 * @return an ApplicationPropertySet
	 * @see ApplicationProperty
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Admin.getAppProperties"> Developers Wiki: Admin.getAppProperties</a>
	 */
	public ApplicationPropertySet admin_getAppPropertiesAsSet( EnumSet<ApplicationProperty> properties ) throws FacebookException, IOException {
		String propJson = admin_getAppPropertiesAsString( properties );
		return new ApplicationPropertySet( propJson );
	}

	/**
	 * Gets the public information about the specified application. Only one of the 3 parameters needs to be specified.
	 * 
	 * @param applicationId
	 *            the id of the application to get the info for.
	 * @param applicationKey
	 *            the public API key of the application to get the info for.
	 * @param applicationCanvas
	 *            the canvas-page name of the application to get the info for.
	 * 
	 * @return the public information for the specified application
	 */
	public Document application_getPublicInfo( Long applicationId, String applicationKey, String applicationCanvas ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();

		if ( ( applicationId != null ) && ( applicationId > 0 ) ) {
			params.add( new Pair<String,CharSequence>( "application_id", Long.toString( applicationId ) ) );
		} else if ( ( applicationKey != null ) && ( !"".equals( applicationKey ) ) ) {
			params.add( new Pair<String,CharSequence>( "application_api_key", applicationKey ) );
		} else if ( ( applicationCanvas != null ) && ( !"".equals( applicationCanvas ) ) ) {
			params.add( new Pair<String,CharSequence>( "application_canvas_name", applicationCanvas ) );
		} else {
			// we need at least one of them to be valid
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You must specify at least on of {applicationId, applicationKey, applicationCanvas}" );
		}

		return callMethod( FacebookMethod.APPLICATION_GET_PUBLIC_INFO, params );
	}

	/**
	 * Gets the public information about the specified application, by application id.
	 * 
	 * @param applicationId
	 *            the id of the application to get the info for.
	 * 
	 * @return the public information for the specified application
	 */
	public Document application_getPublicInfoById( Long applicationId ) throws FacebookException, IOException {
		return application_getPublicInfo( applicationId, null, null );
	}

	/**
	 * Gets the public information about the specified application, by API key.
	 * 
	 * @param applicationKey
	 *            the public API key of the application to get the info for.
	 * 
	 * @return the public information for the specified application
	 */
	public Document application_getPublicInfoByApiKey( String applicationKey ) throws FacebookException, IOException {
		return application_getPublicInfo( null, applicationKey, null );
	}

	/**
	 * Gets the public information about the specified application, by canvas-page name.
	 * 
	 * @param applicationCanvas
	 *            the canvas-page name of the application to get the info for.
	 * 
	 * @return the public information for the specified application
	 */
	public Document application_getPublicInfoByCanvasName( String applicationCanvas ) throws FacebookException, IOException {
		return application_getPublicInfo( null, null, applicationCanvas );
	}

	public int admin_getAllocation( String allocationType ) throws FacebookException, IOException {
		return extractInt( callMethod( FacebookMethod.ADMIN_GET_ALLOCATION, new Pair<String,CharSequence>( "integration_point_name", allocationType ) ) );
	}

	public int admin_getAllocation( AllocationType allocationType ) throws FacebookException, IOException {
		return admin_getAllocation( allocationType.getName() );
	}

	@Deprecated
	public int admin_getNotificationAllocation() throws FacebookException, IOException {
		return admin_getAllocation( "notifications_per_day" );
	}

	@Deprecated
	public int admin_getRequestAllocation() throws FacebookException, IOException {
		return admin_getAllocation( "requests_per_day" );
	}

	@Deprecated
	public Document admin_getDailyMetrics( Set<Metric> metrics, Date start, Date end ) throws FacebookException, IOException {
		return admin_getDailyMetrics( metrics, start.getTime(), end.getTime() );
	}

	@Deprecated
	public Document admin_getDailyMetrics( Set<Metric> metrics, long start, long end ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		metrics.remove( Metric.ACTIVE_USERS );
		if ( ( metrics != null ) && ( !metrics.isEmpty() ) ) {
			JSONArray metricsJson = new JSONArray();
			for ( Metric metric : metrics ) {
				metricsJson.put( metric.getName() );
			}
			params.add( new Pair<String,CharSequence>( "metrics", metricsJson.toString() ) );
		}
		params.add( new Pair<String,CharSequence>( "start_date", Long.toString( start / 1000 ) ) );
		params.add( new Pair<String,CharSequence>( "end_date", Long.toString( end / 1000 ) ) );

		return callMethod( FacebookMethod.ADMIN_GET_DAILY_METRICS, params );
	}

	public Document admin_getMetrics( Set<Metric> metrics, Date start, Date end, long period ) throws FacebookException, IOException {
		return admin_getMetrics( metrics, start.getTime(), end.getTime(), period );
	}

	public Document admin_getMetrics( Set<Metric> metrics, long start, long end, long period ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		metrics.remove( Metric.DAILY_ACTIVE_USERS );
		if ( ( metrics != null ) && ( !metrics.isEmpty() ) ) {
			JSONArray metricsJson = new JSONArray();
			for ( Metric metric : metrics ) {
				metricsJson.put( metric.getName() );
			}
			params.add( new Pair<String,CharSequence>( "metrics", metricsJson.toString() ) );
		}
		params.add( new Pair<String,CharSequence>( "start_time", Long.toString( start / 1000 ) ) );
		params.add( new Pair<String,CharSequence>( "end_time", Long.toString( end / 1000 ) ) );
		params.add( new Pair<String,CharSequence>( "period", Long.toString( period ) ) );

		return callMethod( FacebookMethod.ADMIN_GET_METRICS, params );
	}

	public Document permissions_checkGrantedApiAccess( String apiKey ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PERM_CHECK_GRANTED_API_ACCESS, new Pair<String,CharSequence>( "permissions_apikey", apiKey ) );
	}

	public Document permissions_checkAvailableApiAccess( String apiKey ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PERM_CHECK_AVAILABLE_API_ACCESS, new Pair<String,CharSequence>( "permissions_apikey", apiKey ) );
	}

	public boolean permissions_grantApiAccess( String apiKey, Set<FacebookMethod> methods ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();

		if ( ( methods != null ) && ( !methods.isEmpty() ) ) {
			JSONArray methodsJson = new JSONArray();
			for ( FacebookMethod method : methods ) {
				methodsJson.put( method.methodName() );
			}
			params.add( new Pair<String,CharSequence>( "method_arr", methodsJson.toString() ) );
		}
		params.add( new Pair<String,CharSequence>( "permissions_apikey", apiKey ) );

		return extractBoolean( callMethod( FacebookMethod.PERM_GRANT_API_ACCESS, params ) );
	}

	public boolean permissions_grantFullApiAccess( String apiKey ) throws FacebookException, IOException {
		return permissions_grantApiAccess( apiKey, null );
	}

	public boolean permissions_revokeApiAccess( String apiKey ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.PERM_REVOKE_API_ACCESS, new Pair<String,CharSequence>( "permissions_apikey", apiKey ) ) );
	}

	public String auth_promoteSession() throws FacebookException, IOException {
		return extractString( callMethod( FacebookMethod.AUTH_PROMOTE_SESSION ) );
	}

	public boolean auth_revokeAuthorization() throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.AUTH_REVOKE_AUTHORIZATION ) );
	}

	public boolean auth_expireSession() throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.AUTH_EXPIRE_SESSION ) );
	}

	public Long marketplace_createListing( Long listingId, boolean showOnProfile, String attributes, Long userId ) throws FacebookException, IOException {
		if ( listingId == null ) {
			listingId = 0l;
		}
		MarketListing test = new MarketListing( attributes );
		if ( !test.verify() ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The specified listing is invalid!" );
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "listing_id", listingId.toString() ) );
		if ( showOnProfile ) {
			params.add( new Pair<String,CharSequence>( "show_on_profile", "true" ) );
		}
		params.add( new Pair<String,CharSequence>( "listing_attrs", attributes ) );

		params.add( new Pair<String,CharSequence>( "uid", Long.toString( listingId ) ) );

		return marketplace_createListing( FacebookMethod.MARKET_CREATE_LISTING_NOSESSION, params );
	}

	public Long marketplace_createListing( Long listingId, boolean showOnProfile, MarketListing listing, Long userId ) throws FacebookException, IOException {
		return marketplace_createListing( listingId, showOnProfile, listing.getAttribs(), userId );
	}

	public Long marketplace_createListing( boolean showOnProfile, MarketListing listing, Long userId ) throws FacebookException, IOException {
		return marketplace_createListing( 0l, showOnProfile, listing.getAttribs(), userId );
	}

	public boolean marketplace_removeListing( Long listingId, Long userId ) throws FacebookException, IOException {
		return marketplace_removeListing( listingId, MarketListingStatus.DEFAULT, userId );
	}

	public boolean marketplace_removeListing( Long listingId, MarketListingStatus status, Long userId ) throws FacebookException, IOException {
		if ( status == null ) {
			status = MarketListingStatus.DEFAULT;
		}
		if ( listingId == null ) {
			return false;
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "listing_id", listingId.toString() ) );
		params.add( new Pair<String,CharSequence>( "status", status.getName() ) );
		params.add( new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
		return marketplace_removeListing( FacebookMethod.MARKET_REMOVE_LISTING_NOSESSION, params );
	}

	private boolean photos_addTag( Long photoId, Double xPct, Double yPct, Long taggedUserId, CharSequence tagText, Long userId ) throws FacebookException, IOException {
		assert ( null != photoId && !photoId.equals( 0 ) );
		assert ( null != taggedUserId || null != tagText );
		assert ( null != xPct && xPct >= 0 && xPct <= 100 );
		assert ( null != yPct && yPct >= 0 && yPct <= 100 );
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		Pair<String,CharSequence> tagData;
		if ( taggedUserId != null ) {
			tagData = new Pair<String,CharSequence>( "tag_uid", taggedUserId.toString() );
		} else {
			tagData = new Pair<String,CharSequence>( "tag_text", tagText.toString() );
		}
		params.add( tagData );
		params.add( new Pair<String,CharSequence>( "x", xPct.toString() ) );
		params.add( new Pair<String,CharSequence>( "y", yPct.toString() ) );
		params.add( new Pair<String,CharSequence>( "pid", photoId.toString() ) );
		params.add( new Pair<String,CharSequence>( "owner_uid", Long.toString( userId ) ) );

		return photos_addTag( FacebookMethod.PHOTOS_ADD_TAG_NOSESSION, params );
	}

	public boolean photos_addTag( Long photoId, Long taggedUserId, Double pct, Double pct2, Long userId ) throws FacebookException, IOException {
		return photos_addTag( photoId, pct, pct2, taggedUserId, null, userId );
	}

	public boolean photos_addTag( Long photoId, CharSequence tagText, Double pct, Double pct2, Long userId ) throws FacebookException, IOException {
		return photos_addTag( photoId, pct, pct2, null, tagText );
	}

	public Document photos_createAlbum( String albumName, Long userId ) throws FacebookException, IOException {
		return photos_createAlbum( albumName, null, null, userId );
	}

	public Document photos_createAlbum( String name, String description, String location, Long userId ) throws FacebookException, IOException {
		assert ( null != name && !"".equals( name ) );
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_CREATE_ALBUM.numParams() );
		params.add( new Pair<String,CharSequence>( "name", name ) );
		if ( null != description )
			params.add( new Pair<String,CharSequence>( "description", description ) );
		if ( null != location )
			params.add( new Pair<String,CharSequence>( "location", location ) );
		params.add( new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
		return photos_createAlbum( FacebookMethod.PHOTOS_CREATE_ALBUM_NOSESSION, params );
	}

	public Document photos_upload( Long userId, File photo ) throws FacebookException, IOException {
		return photos_upload( userId, photo, null, null );
	}

	public Document photos_upload( Long userId, File photo, String caption ) throws FacebookException, IOException {
		return photos_upload( userId, photo, caption, null );
	}

	public Document photos_upload( Long userId, File photo, Long albumId ) throws FacebookException, IOException {
		return photos_upload( userId, photo, null, albumId );
	}

	public Document photos_upload( Long userId, File photo, String caption, Long albumId ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_UPLOAD.numParams() );
		assert ( photo.exists() && photo.canRead() );
		this._uploadFile = photo;
		if ( null != albumId )
			params.add( new Pair<String,CharSequence>( "aid", Long.toString( albumId ) ) );
		if ( null != caption )
			params.add( new Pair<String,CharSequence>( "caption", caption ) );
		params.add( new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
		return photos_upload( FacebookMethod.PHOTOS_UPLOAD_NOSESSION, params );
	}

	public Document profile_getFBML() throws FacebookException, IOException {
		return callMethod( FacebookMethod.PROFILE_GET_FBML );
	}

	public boolean users_hasAppPermission( Permission perm, Long userId ) throws FacebookException, IOException {
		callMethod( FacebookMethod.USERS_HAS_PERMISSION_NOSESSION, new Pair<String,CharSequence>( "ext_perm", perm.getName() ), new Pair<String,CharSequence>( "uid",
				Long.toString( userId ) ) );
		if ( this.rawResponse == null ) {
			return false;
		}
		return this.rawResponse.contains( ">1<" ); // a code of '1' is sent back to indicate that the user has the request permission
	}

	public boolean users_isAppAdded( Long userId ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.USERS_IS_APP_ADDED_NOSESSION, new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) ) );
	}

	public boolean users_setStatus( String status, Long userId ) throws FacebookException, IOException {
		return users_setStatus( status, false, userId );
	}

	public boolean users_setStatus( String newStatus, boolean clear, Long userId ) throws FacebookException, IOException {
		return users_setStatus( newStatus, clear, false, userId );
	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb, Long userId ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( newStatus != null ) {
			params.add( new Pair<String,CharSequence>( "status", newStatus ) );
		}
		if ( clear ) {
			params.add( new Pair<String,CharSequence>( "clear", "1" ) );
		}
		if ( statusIncludesVerb ) {
			params.add( new Pair<String,CharSequence>( "status_includes_verb", "true" ) );
		}
		params.add( new Pair<String,CharSequence>( "uid", userId.toString() ) );
		return extractBoolean( callMethod( FacebookMethod.USERS_SET_STATUS_NOSESSION, params ) );
	}

	public Document feed_getRegisteredTemplateBundleByID( Long id ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.FEED_GET_TEMPLATE_BY_ID, new Pair<String,CharSequence>( "template_bundle_id", Long.toString( id ) ) );
	}

	public Document feed_getRegisteredTemplateBundles() throws FacebookException, IOException {
		return callMethod( FacebookMethod.FEED_GET_TEMPLATES );
	}

	public Boolean feed_publishUserAction( Long bundleId ) throws FacebookException, IOException {
		return feed_publishUserAction( bundleId, null, null, null );
	}

	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<Long> targetIds, String bodyGeneral ) throws FacebookException,
			IOException {
		return this.feed_publishUserAction( bundleId, templateData, null, targetIds, bodyGeneral );
	}

	public Long feed_registerTemplateBundle( String template ) throws FacebookException, IOException {
		List<String> temp = new ArrayList<String>();
		temp.add( template );
		return feed_registerTemplateBundle( temp );
	}

	public Long feed_registerTemplateBundle( Collection<String> templates ) throws FacebookException, IOException {
		return feed_registerTemplateBundle( templates, null, null );
	}

	public Long feed_registerTemplateBundle( Collection<String> templates, Collection<BundleStoryTemplate> shortTemplates, BundleStoryTemplate longTemplate )
			throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		JSONArray templateArray = new JSONArray();
		for ( String template : templates ) {
			templateArray.put( template );
		}
		params.add( new Pair<String,CharSequence>( "one_line_story_templates", templateArray.toString() ) );
		if ( shortTemplates != null && !shortTemplates.isEmpty() ) {
			JSONArray shortArray = new JSONArray();
			for ( BundleStoryTemplate template : shortTemplates ) {
				shortArray.put( template.toJson() );
			}
			params.add( new Pair<String,CharSequence>( "short_story_templates", shortArray.toString() ) );
		}
		if ( longTemplate != null ) {
			params.add( new Pair<String,CharSequence>( "full_story_template", longTemplate.toJsonString() ) );
		}

		return extractLong( callMethod( FacebookMethod.FEED_REGISTER_TEMPLATE, params ) );
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public Long feed_registerTemplateBundle( String template, String shortTemplate, String longTemplate ) throws FacebookException, IOException {
		List<String> templates = new ArrayList<String>();
		templates.add( template );
		return feed_registerTemplateBundle( templates, null, null );
	}

	public Document profile_getInfo( Long userId ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PROFILE_GET_INFO, new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
	}

	public Document profile_getInfoOptions( String field ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PROFILE_GET_INFO_OPTIONS, new Pair<String,CharSequence>( "field", field ) );
	}

	public void profile_setInfo( Long userId, String title, boolean textOnly, List<ProfileInfoField> fields ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		JSONArray json = new JSONArray();
		params.add( new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
		params.add( new Pair<String,CharSequence>( "title", title ) );
		if ( textOnly ) {
			params.add( new Pair<String,CharSequence>( "type", "1" ) );
		} else {
			params.add( new Pair<String,CharSequence>( "type", "5" ) );
		}
		for ( ProfileInfoField field : fields ) {
			try {
				JSONObject innerJSON = new JSONObject();
				JSONArray fieldItems = new JSONArray();
				innerJSON.put( "field", field.getFieldName() );
				for ( ProfileFieldItem item : field.getItems() ) {
					JSONObject itemJSON = new JSONObject();
					for ( String key : item.getMap().keySet() ) {
						itemJSON.put( key, item.getMap().get( key ) );
					}
					fieldItems.put( itemJSON );
				}

				innerJSON.put( "items", fieldItems );
				json.put( innerJSON );
			}
			catch ( Exception ignored ) {
				ignored.printStackTrace();
			}
		}
		params.add( new Pair<String,CharSequence>( "info_fields", json.toString() ) );
		callMethod( FacebookMethod.PROFILE_SET_INFO, params );
	}

	public void profile_setInfoOptions( ProfileInfoField field ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		JSONArray json = new JSONArray();
		params.add( new Pair<String,CharSequence>( "field", field.getFieldName() ) );

		for ( ProfileFieldItem item : field.getItems() ) {
			JSONObject itemJSON = new JSONObject();
			for ( String key : item.getMap().keySet() ) {
				try {
					itemJSON.put( key, item.getMap().get( key ) );
				}
				catch ( Exception e ) {
					e.printStackTrace();
				}
			}
			json.put( itemJSON );
		}
		params.add( new Pair<String,CharSequence>( "options", json.toString() ) );
		callMethod( FacebookMethod.PROFILE_SET_INFO_OPTIONS, params );
	}

	/**
	 * Adds several tags to a photo.
	 * 
	 * @param photoId
	 *            The photo id of the photo to be tagged.
	 * @param tags
	 *            A list of PhotoTags.
	 * @return a list of booleans indicating whether the tag was successfully added.
	 */
	public Document photos_addTags( Long photoId, Collection<PhotoTag> tags, Long userId ) throws FacebookException, IOException {
		assert ( photoId > 0 );
		assert ( null != tags && !tags.isEmpty() );
		String tagStr = null;
		try {
			JSONArray jsonTags = new JSONArray();
			for ( PhotoTag tag : tags ) {
				jsonTags.put( tag.jsonify() );
			}
			tagStr = jsonTags.toString();
		}
		catch ( Exception ignored ) {
			// ignore
		}
		return callMethod( FacebookMethod.PHOTOS_ADD_TAG_NOSESSION, new Pair<String,CharSequence>( "pid", photoId.toString() ), new Pair<String,CharSequence>( "tags",
				tagStr ), new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
	}

	private Long marketplace_createListing( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		callMethod( method, params );
		String result;
		if ( this.rawResponse != null ) {
			result = this.rawResponse.substring( 0, this.rawResponse.indexOf( "</marketplace" ) );
			result = result.substring( result.lastIndexOf( ">" ) + 1 );
		} else {
			return null;
		}
		return Long.parseLong( result );
	}

	private boolean marketplace_removeListing( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		callMethod( method, params );
		if ( this.rawResponse == null ) {
			return false;
		}

		return this.rawResponse.contains( ">1<" ); // a code of '1' indicates success
	}

	private boolean photos_addTag( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		Document d = callMethod( method, params );
		return extractBoolean( d );
	}

	private Document photos_createAlbum( IFacebookMethod method, ArrayList<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		return callMethod( method, params );
	}

	private Document photos_upload( IFacebookMethod method, ArrayList<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		return callMethod( method, params );
	}

	public static boolean addParam( String name, Long value, Collection<Pair<String,CharSequence>> params ) {
		return addParam( name, Long.toString( value ), params );
	}

	public static boolean addParamIfNotBlank( String name, Long value, Collection<Pair<String,CharSequence>> params ) {
		if ( value != null ) {
			return addParamIfNotBlank( name, Long.toString( value ), params );
		}
		return false;
	}

	public static boolean addParam( String name, CharSequence value, Collection<Pair<String,CharSequence>> params ) {
		params.add( new Pair<String,CharSequence>( name, value ) );
		return true;
	}

	public static boolean addParamIfNotBlank( String name, CharSequence value, Collection<Pair<String,CharSequence>> params ) {
		if ( ( value != null ) && ( !"".equals( value ) ) ) {
			params.add( new Pair<String,CharSequence>( name, value ) );
			return true;
		}
		return false;
	}

	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml ) throws FacebookException, IOException {
		return profile_setFBML( userId, profileFbml, actionFbml, mobileFbml, null );
	}

	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml, String profileMain ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "uid", Long.toString( userId ) ) );
		addParamIfNotBlank( "profile", profileFbml, params );
		addParamIfNotBlank( "profile_action", actionFbml, params );
		addParamIfNotBlank( "mobile_fbml", mobileFbml, params );
		addParamIfNotBlank( "profile_main", profileMain, params );
		FacebookMethod method = this.isDesktop() ? FacebookMethod.PROFILE_SET_FBML : FacebookMethod.PROFILE_SET_FBML_NOSESSION;
		return extractBoolean( callMethod( method, params ) );
	}

	public void setServerUrl( String newUrl ) {
		String base = newUrl;
		if ( base.startsWith( "http" ) ) {
			base = base.substring( base.indexOf( "://" ) + 3 );
		}
		try {
			String url = "http://" + base;
			_serverUrl = new URL( url );
			setDefaultServerUrl( _serverUrl );
		}
		catch ( MalformedURLException ex ) {
			throw new RuntimeException( ex );
		}
	}

	public URL getDefaultServerUrl() {
		return SERVER_URL;
	}

	public void setDefaultServerUrl( URL newUrl ) {
		SERVER_URL = newUrl;
	}

	public void useBetaApiServer() {
		setServerUrl( "http://api.new.facebook.com/restserver.php" );
	}

	protected Long extractLong( Document doc ) {
		if ( doc == null ) {
			return 0l;
		}
		return Long.parseLong( doc.getFirstChild().getTextContent() );
	}

	public Boolean liveMessage_send( Long recipient, String eventName, JSONObject message ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "uid", Long.toString( recipient ) ) );
		params.add( new Pair<String,CharSequence>( "event_name", eventName ) );
		params.add( new Pair<String,CharSequence>( "message", message.toString() ) );
		return extractBoolean( callMethod( FacebookMethod.LIVEMESSAGE_SEND, params ) );
	}

	public boolean feed_deactivateTemplateBundleByID( Long bundleId ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "template_bundle_id", Long.toString( bundleId ) ) );
		return extractBoolean( this.callMethod( FacebookMethod.FEED_DEACTIVATE_TEMPLATE_BUNDLE, params ) );
	}

	public void notifications_send( Collection<Long> recipientIds, String notification, boolean isAppToUser ) throws FacebookException, IOException {
		if ( null == notification || "".equals( notification ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You cannot send an empty notification!" );
		}
		Pair<String,CharSequence> type = new Pair<String,CharSequence>( "type", isAppToUser ? "app_to_user" : "user_to_user" );
		if ( ( recipientIds != null ) && ( !recipientIds.isEmpty() ) ) {
			callMethod( FacebookMethod.NOTIFICATIONS_SEND, new Pair<String,CharSequence>( "to_ids", delimit( recipientIds ) ), new Pair<String,CharSequence>(
					"notification", notification ), type );
		} else {
			callMethod( FacebookMethod.NOTIFICATIONS_SEND, new Pair<String,CharSequence>( "notification", notification ), type );
		}
	}

	/**
	 * @see http://wiki.developers.facebook.com/index.php/Feed.publishUserAction
	 */
	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<IFeedImage> images, List<Long> targetIds, String bodyGeneral )
			throws FacebookException, IOException {

		// validate maximum of 4 images
		if ( images != null && images.size() > 4 ) {
			throw new IllegalArgumentException( "Maximum of 4 images allowed per feed item." );
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>( "template_bundle_id", Long.toString( bundleId ) ) );

		if ( targetIds != null && !targetIds.isEmpty() ) {
			params.add( new Pair<String,CharSequence>( "target_ids", delimit( targetIds ) ) );
		}

		if ( bodyGeneral != null && !"".equals( bodyGeneral ) ) {
			params.add( new Pair<String,CharSequence>( "body_general", bodyGeneral ) );
		}

		JSONObject jsonTemplateData = new JSONObject();
		if ( templateData != null && !templateData.isEmpty() ) {
			for ( String key : templateData.keySet() ) {
				try {
					jsonTemplateData.put( key, templateData.get( key ) );
				}
				catch ( Exception exception ) {
					throw new RuntimeException( "Error constructing JSON object", exception );
				}
			}
		}

		/*
		 * Associate images to "images" label in the form of:
		 * 
		 * "images":[{"src":"http:\/\/www.facebook.com\/images\/image1.gif", "href":"http:\/\/www.facebook.com"}, {"src":"http:\/\/www.facebook.com\/images\/image2.gif",
		 * "href":"http:\/\/www.facebook.com"}]
		 */
		if ( images != null && !images.isEmpty() ) {

			try {
				// create images array
				JSONArray jsonArray = new JSONArray();
				for ( int i = 0; i < images.size(); i++ ) {
					IFeedImage image = images.get( i );
					JSONObject jsonImage = new JSONObject();
					jsonImage.put( "src", image.getImageUrlString() );
					jsonImage.put( "href", image.getLinkUrl().toExternalForm() );
					jsonArray.put( i, jsonImage );
				}

				// associate to key label
				jsonTemplateData.put( "images", jsonArray );
			}
			catch ( Exception exception ) {
				throw new RuntimeException( "Error constructing JSON object", exception );
			}
		}

		// associate to param
		if ( jsonTemplateData.length() > 0 ) {
			params.add( new Pair<String,CharSequence>( "template_data", jsonTemplateData.toString() ) );
		}

		return extractBoolean( callMethod( FacebookMethod.FEED_PUBLISH_USER_ACTION, params ) );
	}
}
