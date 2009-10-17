package com.google.code.facebookapi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
	protected int _timeout;
	protected int _readTimeout;

	protected final String _apiKey;
	protected final String _secret;
	protected boolean _isDesktop;

	protected String cacheSessionKey;
	protected Long cacheSessionExpires;

	/** filled in when session is established only used for desktop apps */
	protected String cacheSessionSecret;

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
		this( null, apiKey, secret, null );
	}

	protected BasicClient( String apiKey, String secret, int timeout ) {
		this( null, apiKey, secret, null, timeout );
	}

	protected BasicClient( String apiKey, String secret, String sessionKey ) {
		this( null, apiKey, secret, sessionKey );
	}

	protected BasicClient( String apiKey, String secret, String sessionKey, int timeout ) {
		this( null, apiKey, secret, sessionKey, timeout );
	}

	protected BasicClient( URL serverUrl, String apiKey, String secret, String sessionKey, int timeout ) {
		this( serverUrl, apiKey, secret, sessionKey );
		_timeout = timeout;
	}

	protected BasicClient( URL serverUrl, String apiKey, String secret, String sessionKey, int timeout, int readTimeout ) {
		this( serverUrl, apiKey, secret, sessionKey );
		_timeout = timeout;
		_readTimeout = readTimeout;
	}

	protected BasicClient( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		this.cacheSessionKey = sessionKey;
		this._apiKey = apiKey;
		this._secret = secret;
		if ( secret.endsWith( "__" ) ) {
			_isDesktop = true;
		}
		this._serverUrl = ( null != serverUrl ) ? serverUrl : FacebookApiUrls.getDefaultServerUrl();
		this._timeout = -1;
		this._readTimeout = -1;
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

	/**
	 * Gets the session-token used by Facebook to authenticate a desktop application. If your application does not run in desktop mode, than this field is not relevent to
	 * you.
	 * 
	 * @return the desktop-app session token.
	 */
	public String getSessionSecret() {
		return cacheSessionSecret;
	}

	/**
	 * Allows the session-token to be manually overridden when running a desktop application. If your application does not run in desktop mode, then setting this field
	 * will have no effect. If you set an incorrect value here, your application will probably fail to run.
	 * 
	 * @param key
	 *            the new value to set. Incorrect values may cause your application to fail to run.
	 */
	public void setSessionSecret( String key ) {
		cacheSessionSecret = key;
	}

	public String getCacheSessionSecret() {
		return cacheSessionSecret;
	}

	public void setCacheSessionSecret( String cacheSessionSecret ) {
		this.cacheSessionSecret = cacheSessionSecret;
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

		Map<String,String> params = new TreeMap<String,String>();

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

		CharSequence oldVal;
		for ( Pair<String,CharSequence> p : paramPairs ) {
			oldVal = params.put( p.first, FacebookSignatureUtil.toString( p.second ) );
			if ( oldVal != null ) {
				log.warn( String.format( "For parameter %s, overwrote old value %s with new value %s.", p.first, oldVal, p.second ) );
			}
		}

		assert ( !params.containsKey( "sig" ) );
		String signature = FacebookSignatureUtil.generateSignature( FacebookSignatureUtil.convert( params.entrySet() ), _secret );
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
		CharSequence paramString = ( null == params ) ? "" : BasicClientHelper.delimit( params.entrySet(), "&", "=", true );
		if ( log.isDebugEnabled() ) {
			log.debug( method.methodName() + " POST: " + serverUrl.toString() + "?" + paramString );
		}

		HttpURLConnection conn = null;
		OutputStream out = null;
		InputStream in = null;
		try {
			conn = (HttpURLConnection) serverUrl.openConnection();
			if ( _timeout != -1 ) {
				conn.setConnectTimeout( _timeout );
			}
			if ( _readTimeout != -1 ) {
				conn.setReadTimeout( _readTimeout );
			}
			conn.setRequestMethod( "POST" );
			conn.setDoOutput( true );
			conn.connect();
			out = conn.getOutputStream();
			out.write( paramString.toString().getBytes( "UTF-8" ) );
			in = conn.getInputStream();
			return BasicClientHelper.getResponse( method, in );
		}
		finally {
			BasicClientHelper.close( in );
			BasicClientHelper.close( out );
			BasicClientHelper.disconnect( conn );
		}
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
		HttpURLConnection con = null;
		OutputStream urlOut = null;
		InputStream in = null;
		try {
			String boundary = Long.toString( System.currentTimeMillis(), 16 );
			con = (HttpURLConnection) _serverUrl.openConnection();
			if ( _timeout != -1 ) {
				con.setConnectTimeout( _timeout );
			}
			if ( _readTimeout != -1 ) {
				con.setReadTimeout( _readTimeout );
			}
			con.setDoInput( true );
			con.setDoOutput( true );
			con.setUseCaches( false );
			con.setRequestProperty( "Content-Type", "multipart/form-data; boundary=" + boundary );
			con.setRequestProperty( "MIME-version", "1.0" );

			urlOut = con.getOutputStream();
			DataOutputStream out = new DataOutputStream( urlOut );

			for ( Map.Entry<String,String> entry : params.entrySet() ) {
				out.writeBytes( PREF + boundary + CRLF );

				out.writeBytes( "Content-Type: text/plain;charset=utf-8" + CRLF );
				// out.writeBytes( "Content-Transfer-Encoding: application/x-www-form-urlencoded" + CRLF );

				// out.writeBytes( "Content-Type: text/plain;charset=utf-8" + CRLF );
				// out.writeBytes( "Content-Transfer-Encoding: quoted-printable" + CRLF );

				out.writeBytes( "Content-disposition: form-data; name=\"" + entry.getKey() + "\"" + CRLF );
				out.writeBytes( CRLF );
				byte[] valueBytes = entry.getValue().toString().getBytes( "UTF-8" );
				out.write( valueBytes );
				out.writeBytes( CRLF );
			}

			out.writeBytes( PREF + boundary + CRLF );
			out.writeBytes( "Content-Type: image" + CRLF );
			out.writeBytes( "Content-disposition: form-data; filename=\"" + fileName + "\"" + CRLF );
			// out.writeBytes("Content-Transfer-Encoding: binary" + CRLF); // not necessary

			// Write the file
			out.writeBytes( CRLF );
			byte buf[] = new byte[UPLOAD_BUFFER_SIZE];
			int len = 0;
			while ( len >= 0 ) {
				out.write( buf, 0, len );
				len = fileStream.read( buf );
			}

			out.writeBytes( CRLF + PREF + boundary + PREF + CRLF );
			out.flush();
			in = con.getInputStream();
			return BasicClientHelper.getResponse( method, in );
		}
		finally {
			BasicClientHelper.close( urlOut );
			BasicClientHelper.close( in );
			BasicClientHelper.disconnect( con );
		}
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
