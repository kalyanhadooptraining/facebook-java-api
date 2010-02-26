package com.google.code.facebookapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

/**
 * Basic client taking care of rest call mechanics (signing, etc) to facebook. No api knowledge, nor response interpretation is planned.
 */
public class BasicClient {

	protected static Log log = LogFactory.getLog( BasicClient.class );

	protected static final String CRLF = "\r\n";
	protected static final String PREF = "--";
	protected static final int UPLOAD_BUFFER_SIZE = 1024;

	protected URL _serverUrl;
	private CommunicationStrategy _communicationStrategy;

	protected final String _apiKey;
	protected final String _secret;
	protected boolean _isDesktop;

	protected String cacheSessionKey;
	protected Long cacheSessionExpires;

	protected String rawResponse;
	protected boolean batchMode;

	public boolean isBatchMode() {
		return batchMode;
	}

	protected List<BatchQuery> queries;

	public List<BatchQuery> getQueries() {
		return queries;
	}

	protected String permissionsApiKey = null;


	public boolean isDesktop() {
		return _isDesktop;
	}

	public String getRawResponse() {
		return rawResponse;
	}





	protected BasicClient( String apiKey, String secret ) {
		this( apiKey, secret, null );
	}

	protected BasicClient( String apiKey, String secret, String sessionKey ) {
		this( null, apiKey, secret, sessionKey, new DefaultCommunicationStrategy() );
	}

	protected BasicClient( URL serverUrl, String apiKey, String secret, String sessionKey, CommunicationStrategy communicationStrategy ) {
		this.cacheSessionKey = sessionKey;
		this._apiKey = apiKey;
		this._secret = secret;
		if ( secret.endsWith( "__" ) ) {
			_isDesktop = true;
		}
		this._serverUrl = ( null != serverUrl ) ? serverUrl : FacebookApiUrls.getDefaultServerUrl();
		this._communicationStrategy = communicationStrategy;
		this.batchMode = false;
		this.queries = new ArrayList<BatchQuery>();
	}

	public String getApiKey() {
		return _apiKey;
	}

	public String getSecret() {
		return _secret;
	}

	public void beginPermissionsMode( String apiKey ) {
		this.permissionsApiKey = apiKey;
	}

	public void endPermissionsMode() {
		this.permissionsApiKey = null;
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
	protected String callMethod( String responseFormat, IFacebookMethod method, Pair<String,CharSequence>... paramPairs ) throws FacebookException {
		return callMethod( responseFormat, method, Arrays.asList( paramPairs ), null, null );
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
	public String callMethod( String responseFormat, IFacebookMethod method, Collection<Pair<String,CharSequence>> paramPairs ) throws FacebookException {
		return callMethod( responseFormat, method, paramPairs, null, null );
	}

	public String callMethod( String responseFormat, IFacebookMethod method, Collection<Pair<String,CharSequence>> paramPairs, String fileName, InputStream fileStream )
			throws FacebookException {
		rawResponse = null;

		SortedMap<String,String> params = new TreeMap<String,String>();

		if ( permissionsApiKey != null ) {
			params.put( "call_as_apikey", permissionsApiKey );
		}

		params.put( "method", method.methodName() );
		params.put( "api_key", _apiKey );
		params.put( "v", IFacebookRestClient.TARGET_API_VERSION );

		if ( null != responseFormat ) {
			params.put( "format", responseFormat );
		}

		params.put( "call_id", Long.toString( System.currentTimeMillis() ) );
		boolean includeSession = !method.requiresNoSession() && cacheSessionKey != null;
		if ( includeSession ) {
			params.put( "session_key", cacheSessionKey );
		}

		for ( Pair<String,CharSequence> p : paramPairs ) {
			CharSequence oldVal = params.put( p.first, BasicClientHelper.toString( p.second ) );
			if ( oldVal != null ) {
				log.warn( String.format( "For parameter %s, overwrote old value %s with new value %s.", p.first, oldVal, p.second ) );
			}
		}

		assert ( !params.containsKey( "sig" ) );
		String signature = FacebookSignatureUtil.generateSignature( params, _secret );
		params.put( "sig", signature );

		if ( batchMode ) {
			// if we are running in batch mode, don't actually execute the query now, just add it to the list
			boolean addToBatch = true;
			if ( method.methodName().equals( FacebookMethod.USERS_GET_LOGGED_IN_USER.methodName() ) ) {
				Exception trace = new Exception();
				StackTraceElement[] traceElems = trace.getStackTrace();
				int index = 0;
				for ( StackTraceElement elem : traceElems ) {
					if ( elem.getMethodName().indexOf( "_" ) != -1 ) {
						StackTraceElement caller = traceElems[index + 1];
						if ( ( caller.getClassName().equals( BasicClient.class.getName() ) ) && ( !caller.getMethodName().startsWith( "auth_" ) ) ) {
							addToBatch = false;
						}
						break;
					}
					index++ ;
				}
			}
			if ( addToBatch ) {
				queries.add( new BatchQuery( method, params ) );
			}
			return null;
		}

		boolean doHttps = FacebookMethod.AUTH_GET_SESSION.equals( method ) && "true".equals( params.get( "generate_session_secret" ) );
		try {
			rawResponse = method.takesFile() ? postFileRequest( method, params, fileName, fileStream ) : postRequest( method, params, doHttps );
			return rawResponse;
		}
		catch ( IOException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	private String postRequest( IFacebookMethod method, Map<String,String> params, boolean doHttps ) throws IOException {
		URL serverUrl = ( doHttps ) ? FacebookApiUrls.getDefaultHttpsServerUrl() : _serverUrl;
		if ( log.isDebugEnabled() ) {
			log.debug( method.methodName() + ": POST: " + serverUrl.toString() + ": " + params );
		}
		return _communicationStrategy.sendPostRequest( serverUrl, params );
	}

	/**
	 * Helper function for posting a request that includes raw file data, eg {@link #photos_upload}.
	 * 
	 * @param methodName
	 *            the name of the method
	 * @param params
	 *            request parameters (not including the file)
	 * @param doEncode
	 *            whether to UTF8-encode the parameters
	 * @return an InputStream with the request response
	 * @see #photos_upload
	 */
	protected String postFileRequest( IFacebookMethod method, Map<String,String> params, String fileName, InputStream fileStream ) throws IOException {
		if ( log.isDebugEnabled() ) {
			log.debug( method.methodName() + ": POST-FILE: " + _serverUrl.toString() + ": " + params );
		}
		return _communicationStrategy.postFileRequest( _serverUrl, params, fileName, fileStream );
	}

	public void beginBatch() {
		batchMode = true;
		queries = new ArrayList<BatchQuery>();
	}

	@SuppressWarnings("unchecked")
	public String batch_run( String responseFormat, String methods, boolean serial ) throws FacebookException {
		if ( !serial ) {
			return callMethod( responseFormat, FacebookMethod.BATCH_RUN, Pairs.newPair( "method_feed", methods ) );
		} else {
			return callMethod( responseFormat, FacebookMethod.BATCH_RUN, Pairs.newPair( "method_feed", methods ), Pairs.newPair( "serial_only", "1" ) );
		}
	}

	public void setServerUrl( String newUrl ) {
		String base = newUrl;
		if ( base.startsWith( "http" ) ) {
			base = base.substring( base.indexOf( "://" ) + 3 );
		}
		try {
			String url = "http://" + base;
			_serverUrl = new URL( url );
			// do not set default url
			// setDefaultServerUrl( _serverUrl );
		}
		catch ( MalformedURLException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	/**
	 * Returns a list of String raw responses which will be further broken down by the adapters into the actual individual responses. One string is returned per 20
	 * methods in the batch.
	 */
	public List<String> executeBatch( String responseFormat, boolean serial ) throws FacebookException {
		int BATCH_LIMIT = 20;
		this.batchMode = false;
		List<String> result = new ArrayList<String>();
		List<BatchQuery> buffer = new ArrayList<BatchQuery>();

		while ( !this.queries.isEmpty() ) {
			buffer.add( this.queries.remove( 0 ) );
			if ( ( buffer.size() == BATCH_LIMIT ) || ( this.queries.isEmpty() ) ) {
				// we can only actually batch up to 20 at once

				String batchRawResponse = batch_run( responseFormat, encodeMethods( buffer ), serial );
				result.add( batchRawResponse );

				if ( buffer.size() == BATCH_LIMIT ) {
					log.debug( "Clearing buffer for the next run." );
					buffer.clear();
				} else {
					log.trace( "No need to clear buffer, this is the final iteration of the batch" );
				}
			}
		}

		return result;
	}

	public static String encodeMethods( List<BatchQuery> queryList ) throws FacebookException {
		JSONArray result = new JSONArray();
		for ( BatchQuery query : queryList ) {
			if ( query.getMethod().takesFile() ) {
				throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "File upload API calls cannot be batched:  " + query.getMethod().methodName() );
			}
			result.put( BasicClientHelper.delimit( query.getParams().entrySet(), "&", "=", true ) );
		}
		return result.toString();
	}

}
