package com.google.code.facebookapi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Base class for interacting with the Facebook Application Programming Interface (API). Most Facebook API methods map directly to function calls of this class. <br/>
 * For continually updated documentation, please refer to the <a href="http://wiki.developers.facebook.com/index.php/API"> Developer Wiki</a>.
 */
@SuppressWarnings("unchecked")
// To stop all the warnings caused by varargs in callMethod(...)
public class ExtensibleClient implements IFacebookRestClient<Object> {

	protected static Log log = LogFactory.getLog( ExtensibleClient.class );

	private static final int MAX_DASHBOARD_NEW_ITEMS = 8;

	protected DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	protected URL _serverUrl;
	private CommunicationStrategy _communicationStrategy;
	private String responseFormat;

	protected final String _apiKey;
	protected String _secret;
	protected boolean _isDesktop;

	protected String cacheSessionKey;
	protected Long cacheUserId;
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


	protected ExtensibleClient( String responseFormat, String apiKey, String secret ) {
		this( responseFormat, apiKey, secret, null );
	}

	protected ExtensibleClient( String responseFormat, String apiKey, String secret, String sessionKey ) {
		this( responseFormat, apiKey, secret, sessionKey, false );
	}

	protected ExtensibleClient( String responseFormat, String apiKey, String secret, String sessionKey, boolean sessionSecret ) {
		this( responseFormat, apiKey, secret, sessionKey, sessionSecret, new DefaultCommunicationStrategy() );
	}

	protected ExtensibleClient( String responseFormat, String apiKey, String secret, String sessionKey, boolean sessionSecret, CommunicationStrategy communicationStrategy ) {
		this._serverUrl = FacebookApiUrls.getDefaultServerUrl();
		this._communicationStrategy = communicationStrategy;
		this.responseFormat = responseFormat;

		this._apiKey = apiKey;
		this._secret = secret;
		this.cacheSessionKey = sessionKey;
		this._isDesktop = ( sessionSecret || secret.endsWith( "__" ) );

		this.batchMode = false;
		this.queries = new ArrayList<BatchQuery>();
	}

	public URL getServerUrl() {
		return _serverUrl;
	}

	public void setServerUrl( URL url ) {
		_serverUrl = url;
	}

	public void setServerUrl( String url ) {
		try {
			_serverUrl = new URL( url );
		}
		catch ( MalformedURLException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	@Deprecated
	public int getConnectTimeout() {
		return getCommunicationStrategy().getConnectionTimeout();
	}

	@Deprecated
	public void setConnectTimeout( int connectTimeout ) {
		getCommunicationStrategy().setConnectionTimeout( connectTimeout );
	}

	@Deprecated
	public int getReadTimeout() {
		return getCommunicationStrategy().getReadTimeout();
	}

	@Deprecated
	public void setReadTimeout( int readTimeout ) {
		getCommunicationStrategy().setReadTimeout( readTimeout );
	}

	public CommunicationStrategy getCommunicationStrategy() {
		return _communicationStrategy;
	}

	public void setCommunicationStrategy( CommunicationStrategy communicationStrategy ) {
		_communicationStrategy = communicationStrategy;
	}

	/**
	 * The response format in which results to FacebookMethod calls are returned
	 * 
	 * @return the format: either XML, JSON, or null (API default)
	 */
	public String getResponseFormat() {
		return responseFormat;
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

	@Deprecated
	protected Boolean cacheAppUser; // to save making the users.isAppAdded api call, this will get prepopulated on canvas pages

	@Deprecated
	public Boolean getCacheAppUser() {
		return cacheAppUser;
	}

	@Deprecated
	public void setCacheAppUser( Boolean cacheAppUser ) {
		this.cacheAppUser = cacheAppUser;
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

	public Object friends_areFriends( long userId1, long userId2 ) throws FacebookException {
		return callMethod( FacebookMethod.FRIENDS_ARE_FRIENDS, Pairs.newPair( "uids1", userId1 ), Pairs.newPair( "uids2", userId2 ) );
	}

	public Object friends_areFriends( Collection<Long> userIds1, Collection<Long> userIds2 ) throws FacebookException {
		if ( userIds1 == null || userIds2 == null || userIds1.isEmpty() || userIds2.isEmpty() ) {
			throw new IllegalArgumentException( "Collections passed to friends_areFriends should not be null or empty" );
		}
		if ( userIds1.size() != userIds2.size() ) {
			throw new IllegalArgumentException( String.format( "Collections should be same size: got userIds1: %d elts; userIds2: %d elts", userIds1.size(), userIds2
					.size() ) );
		}
		return callMethod( FacebookMethod.FRIENDS_ARE_FRIENDS, Pairs.newPair( "uids1", BasicClientHelper.delimit( userIds1 ) ), Pairs.newPair( "uids2", BasicClientHelper
				.delimit( userIds2 ) ) );
	}

	public boolean fbml_refreshRefUrl( String url ) throws FacebookException {
		try {
			return fbml_refreshRefUrl( new URL( url ) );
		}
		catch ( MalformedURLException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	/**
	 * Adds image parameters
	 * 
	 * @param params
	 * @param images
	 */
	protected void handleFeedImages( List<Pair<String,CharSequence>> params, Collection<? extends IPair<? extends Object,URL>> images ) {
		if ( images != null && images.size() > 4 ) {
			throw new IllegalArgumentException( "At most four images are allowed, got " + images.size() );
		}
		if ( null != images && !images.isEmpty() ) {
			int image_count = 0;
			for ( IPair<? extends Object,URL> image : images ) {
				++image_count;
				assert null != image.getFirst() : "Image URL must be provided";
				String name = "image_" + image_count;
				params.add( Pairs.newPair( name, image.getFirst() ) );
				if ( null != image.getSecond() ) {
					params.add( Pairs.newPair( ( name + "_link" ), image.getSecond() ) );
				}
			}
		}
	}

	/**
	 * Call this function to retrieve the session information after your user has logged in.
	 * 
	 * @param authToken
	 *            the token returned by auth_createToken or passed back to your callback_url.
	 */
	public String auth_getSession( String authToken ) throws FacebookException {
		return auth_getSession( authToken, false );
	}

	public String auth_getSession( String authToken, boolean generateSessionSecret ) throws FacebookException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		Pairs.addParam( "auth_token", authToken, params );
		if ( generateSessionSecret ) {
			Pairs.addParam( "generate_session_secret", "true", params );
		}
		String rawResponse = callMethod( FacebookMethod.AUTH_GET_SESSION, params );
		log.debug( "Facebook response:  " + rawResponse );
		if ( "json".equals( getResponseFormat() ) ) {
			try {
				JSONObject json = new JSONObject( rawResponse );
				this.cacheSessionKey = json.getString( "session_key" );
				this.cacheUserId = json.getLong( "uid" );
				this.cacheSessionExpires = json.getLong( "expires" );
				if ( json.has( "secret" ) ) {
					this._secret = json.getString( "secret" );
					this._isDesktop = true;
				}
			}
			catch ( JSONException ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
		} else {
			// Catch errors and return as FacebookException
			Document d = XmlHelper.parseCallResult( rawResponse, factory );
			this.cacheSessionKey = XmlHelper.extractString( d.getElementsByTagName( "session_key" ).item( 0 ) );
			this.cacheUserId = XmlHelper.extractLong( d.getElementsByTagName( "uid" ).item( 0 ) );
			this.cacheSessionExpires = XmlHelper.extractLong( d.getElementsByTagName( "expires" ).item( 0 ) );
			NodeList secretList = d.getElementsByTagName( "secret" );
			if ( secretList.getLength() > 0 ) {
				this._secret = XmlHelper.extractString( secretList.item( 0 ) );
				this._isDesktop = true;
			}
		}
		return this.cacheSessionKey;
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate ) throws FacebookException {
		return feed_publishTemplatizedAction( actorId, titleTemplate, null, null, null, null, null, null );
	}

	/**
	 * Publishes a Mini-Feed story describing an action taken by a user, and publishes aggregating News Feed stories to the friends of that user. Stories are identified
	 * as being combinable if they have matching templates and substituted values.
	 * 
	 * @param actorId
	 *            the user into whose mini-feed the story is being published.
	 * @param titleTemplate
	 *            markup (up to 60 chars, tags excluded) for the feed story's title section. Must include the token <code>{actor}</code>.
	 * @param titleData
	 *            (optional) contains token-substitution mappings for tokens that appear in titleTemplate. Should not contain mappings for the <code>{actor}</code> or
	 *            <code>{target}</code> tokens. Required if tokens other than <code>{actor}</code> or <code>{target}</code> appear in the titleTemplate.
	 * @param bodyTemplate
	 *            (optional) markup to be displayed in the feed story's body section. can include tokens, of the form <code>{token}</code>, to be substituted using
	 *            bodyData.
	 * @param bodyData
	 *            (optional) contains token-substitution mappings for tokens that appear in bodyTemplate. Required if the bodyTemplate contains tokens other than
	 *            <code>{actor}</code> and <code>{target}</code>.
	 * @param bodyGeneral
	 *            (optional) additional body markup that is not aggregated. If multiple instances of this templated story are combined together, the markup in the
	 *            bodyGeneral of one of their stories may be displayed.
	 * @param targetIds
	 *            The user ids of friends of the actor, used for stories about a direct action between the actor and these targets of his/her action. Required if either
	 *            the titleTemplate or bodyTemplate includes the token <code>{target}</code>.
	 * @param images
	 *            (optional) additional body markup that is not aggregated. If multiple instances of this templated story are combined together, the markup in the
	 *            bodyGeneral of one of their stories may be displayed.
	 * @return whether the action story was successfully published; false in case of a permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction"> Developers Wiki: Feed.publishTemplatizedAction</a>
	 */
	@Deprecated
	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException {
		return feed_publishTemplatizedAction( titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images, null );
	}

	/**
	 * @deprecated Use the version that takes a Long for the actorId paramter.
	 */
	@Deprecated
	public boolean feed_publishTemplatizedAction( Integer actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException {
		return feed_publishTemplatizedAction( (long) ( actorId.intValue() ), titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images );
	}

	public Object groups_getMembers( Number groupId ) throws FacebookException {
		assert ( null != groupId );
		return callMethod( FacebookMethod.GROUPS_GET_MEMBERS, Pairs.newPair( "gid", groupId ) );
	}

	public Object friends_getAppUsers() throws FacebookException {
		return callMethod( FacebookMethod.FRIENDS_GET_APP_USERS );
	}

	public Object fql_query( CharSequence query ) throws FacebookException {
		assert ( null != query );
		return callMethod( FacebookMethod.FQL_QUERY, Pairs.newPair( "query", query ) );
	}

	public Object groups_get( Long userId, Collection<Long> groupIds ) throws FacebookException {
		boolean hasGroups = ( null != groupIds && !groupIds.isEmpty() );
		if ( null != userId ) {
			return hasGroups ? callMethod( FacebookMethod.GROUPS_GET, Pairs.newPair( "uid", userId ), Pairs.newPair( "gids", BasicClientHelper.delimit( groupIds ) ) )
					: callMethod( FacebookMethod.GROUPS_GET, Pairs.newPair( "uid", userId ) );
		} else {
			return hasGroups ? callMethod( FacebookMethod.GROUPS_GET, Pairs.newPair( "gids", BasicClientHelper.delimit( groupIds ) ) )
					: callMethod( FacebookMethod.GROUPS_GET );
		}
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
	protected String callMethod( IFacebookMethod method, Pair<String,CharSequence>... paramPairs ) throws FacebookException {
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
	protected String callMethod( IFacebookMethod method, Collection<Pair<String,CharSequence>> paramPairs ) throws FacebookException {
		return callMethod( responseFormat, method, paramPairs, null, null );
	}

	protected String callMethod( IFacebookMethod method, Collection<Pair<String,CharSequence>> paramPairs, String fileName, InputStream fileStream )
			throws FacebookException {
		return callMethod( responseFormat, method, paramPairs, fileName, fileStream );
	}

	protected String callMethod( String format, IFacebookMethod method, Collection<Pair<String,CharSequence>> paramPairs, String fileName, InputStream fileStream )
			throws FacebookException {
		rawResponse = null;

		SortedMap<String,String> params = new TreeMap<String,String>();

		if ( permissionsApiKey != null ) {
			params.put( "call_as_apikey", permissionsApiKey );
		}

		if ( isDesktop() ) {
			params.put( "ss", "1" );
		}

		params.put( "method", method.methodName() );
		params.put( "api_key", _apiKey );
		params.put( "v", TARGET_API_VERSION );

		if ( null != format ) {
			params.put( "format", format );
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
			// FIXME what the heck is going on here??
			if ( method.methodName().equals( FacebookMethod.USERS_GET_LOGGED_IN_USER.methodName() ) ) {
				Exception trace = new Exception();
				StackTraceElement[] traceElems = trace.getStackTrace();
				int index = 0;
				for ( StackTraceElement elem : traceElems ) {
					if ( elem.getMethodName().indexOf( "_" ) != -1 ) {
						StackTraceElement caller = traceElems[index + 1];
						final boolean calledFromSelf = caller.getClassName().equals( ExtensibleClient.class.getName() );
						final boolean calledFromAuth = caller.getMethodName().startsWith( "auth_" );
						if ( calledFromSelf && !calledFromAuth ) {
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

	/**
	 * The ExtensibleClient shouldn't be responsible for error checking. Instead, it should just return the raw response that Facebook gives it.
	 * 
	 * However, because "void" and non-Object return types are currently defined on this class and within IFacebookRestClient, we need to have a way of generating a
	 * FacebookException. Apart from the Exception mechanism, there's no way of notifying a class that has called a void method that the response from Facebook was an
	 * error.
	 * 
	 * The correct end state for ExtensibleClient is to have this validateResponse method removed and for every method to return an Object which holds the raw response
	 * from Facebook.
	 * 
	 * @param rawResponse
	 * @throws FacebookException
	 */
	private void validateVoidResponse( String rawResponse ) throws FacebookException {
		if ( "json".equals( responseFormat ) ) {
			JsonHelper.parseCallResult( rawResponse );
		} else {
			XmlHelper.parseCallResult( rawResponse, factory );
		}
	}

	private String postRequest( IFacebookMethod method, SortedMap<String,String> params, boolean doHttps ) throws IOException {
		URL serverUrl = ( doHttps ) ? FacebookApiUrls.getDefaultHttpsServerUrl() : _serverUrl;
		if ( log.isDebugEnabled() ) {
			log.debug( method.methodName() + ": POST: " + serverUrl.toString() + ": " + params );
		}
		return getCommunicationStrategy().postRequest( serverUrl, params );
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
	protected String postFileRequest( IFacebookMethod method, SortedMap<String,String> params, String fileName, InputStream fileStream ) throws IOException {
		if ( log.isDebugEnabled() ) {
			log.debug( method.methodName() + ": POST-FILE: " + _serverUrl.toString() + ": " + params );
		}
		return getCommunicationStrategy().postRequest( _serverUrl, params, fileName, fileStream );
	}

	public boolean fbml_refreshRefUrl( URL url ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.FBML_REFRESH_REF_URL, Pairs.newPair( "url", url ) ) );
	}

	public Object users_getStandardInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_STANDARD_INFO, Pairs.newPair( "uids", BasicClientHelper.delimit( userIds ) ), Pairs.newPair( "fields",
				BasicClientHelper.delimit( fields ) ) );
	}

	public Object users_getStandardInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_STANDARD_INFO, Pairs.newPair( "uids", BasicClientHelper.delimit( userIds ) ), Pairs.newPair( "fields",
				BasicClientHelper.delimit( fields ) ) );
	}

	public Object users_getInfo( Iterable<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_INFO, Pairs.newPair( "uids", BasicClientHelper.delimit( userIds ) ), Pairs.newPair( "fields", BasicClientHelper
				.delimit( fields ) ) );
	}

	public Object users_getInfo( Iterable<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_INFO, Pairs.newPair( "uids", BasicClientHelper.delimit( userIds ) ), Pairs.newPair( "fields", BasicClientHelper
				.delimit( fields ) ) );
	}

	/**
	 * Retrieves the user ID of the user logged in to this API session
	 * 
	 * @return the Facebook user ID of the logged-in user
	 */
	public long users_getLoggedInUser() throws FacebookException {
		if ( cacheUserId == null || cacheUserId == -1 || batchMode ) {
			cacheUserId = extractLong( callMethod( FacebookMethod.USERS_GET_LOGGED_IN_USER ) );
		}
		return cacheUserId;
	}

	public boolean isDesktop() {
		return _isDesktop;
	}

	public boolean users_isAppUser() throws FacebookException {
		if ( cacheAppUser == null ) {
			cacheAppUser = extractBoolean( callMethod( FacebookMethod.USERS_IS_APP_USER ) );
		}
		return cacheAppUser;
	}

	public boolean users_isAppUser( Long userId ) throws FacebookException {
		if ( userId != null ) {
			return extractBoolean( callMethod( FacebookMethod.USERS_IS_APP_USER_NOSESSION, Pairs.newPair( "uid", userId ) ) );
		} else {
			return extractBoolean( callMethod( FacebookMethod.USERS_IS_APP_USER ) );
		}
	}

	public boolean users_setStatus( String status ) throws FacebookException {
		return users_setStatus( status, false, false );
	}

	public boolean users_clearStatus() throws FacebookException {
		return users_setStatus( null, true );
	}

	public boolean fbml_refreshImgSrc( String imageUrl ) throws FacebookException {
		try {
			return fbml_refreshImgSrc( new URL( imageUrl ) );
		}
		catch ( MalformedURLException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	public boolean fbml_refreshImgSrc( URL imageUrl ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.FBML_REFRESH_IMG_SRC, Pairs.newPair( "url", imageUrl ) ) );
	}

	public Object friends_get() throws FacebookException {
		return callMethod( FacebookMethod.FRIENDS_GET );
	}

	public Object friends_get( Long uid ) throws FacebookException {
		if ( uid != null ) {
			return callMethod( FacebookMethod.FRIENDS_GET_NOSESSION, Pairs.newPair( "uid", uid ) );
		} else {
			return friends_get();
		}
	}

	public Object friends_getMutualFriends( Long targetId ) throws FacebookException {
		return callMethod( FacebookMethod.FRIENDS_GET_MUTUAL_FRIENDS, Pairs.newPair( "targetId", targetId ) );
	}

	public Object friends_getMutualFriends( Long sourceId, Long targetId ) throws FacebookException {
		if ( sourceId != null ) {
			return callMethod( FacebookMethod.FRIENDS_GET_NOSESSION, Pairs.newPair( "sourceId", sourceId ), Pairs.newPair( "targetId", targetId ) );
		} else {
			return friends_getMutualFriends( targetId );
		}
	}

	public String auth_createToken() throws FacebookException {
		String d = callMethod( FacebookMethod.AUTH_CREATE_TOKEN );
		return extractString( d );
	}

	public String getRawResponse() {
		return rawResponse;
	}

	@Deprecated
	public boolean feed_PublishTemplatizedAction( TemplatizedAction action ) throws FacebookException {
		return templatizedFeedHandler( action.getTitleTemplate(), action.getTitleParams(), action.getBodyTemplate(), action.getBodyParams(), action.getBodyGeneral(),
				action.getPictures(), action.getTargetIds(), action.getPageActorId() );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds ) throws FacebookException {
		return templatizedFeedHandler( titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, pictures, targetIds, null );
	}

	@Deprecated
	protected boolean templatizedFeedHandler( String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<?,URL>> pictures, String targetIds, Long pageId ) throws FacebookException {
		assert ( pictures == null || pictures.size() <= 4 );

		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 15 );

		// these are always required parameters
		Pairs.addParam( "title_template", titleTemplate, params );

		// these are optional parameters
		Pairs.addParamIfNotBlank( "title_data", titleData, params );
		boolean hasBody = Pairs.addParamIfNotBlank( "body_template", bodyTemplate, params );
		if ( hasBody ) {
			Pairs.addParamIfNotBlank( "body_data", bodyData, params );
		}
		Pairs.addParamIfNotBlank( "body_general", bodyGeneral, params );
		if ( pictures != null ) {
			int count = 1;
			for ( IPair<?,URL> picture : pictures ) {
				String url = picture.getFirst().toString();
				if ( url.startsWith( TemplatizedAction.UID_TOKEN ) ) {
					url = url.substring( TemplatizedAction.UID_TOKEN.length() );
				}
				Pairs.addParam( ( "image_" + count ), url, params );
				if ( picture.getSecond() != null ) {
					Pairs.addParam( ( "image_" + count + "_link" ), picture.getSecond().toString(), params );
				}
				count++ ;
			}
		}
		Pairs.addParamIfNotBlank( "target_ids", targetIds, params );
		Pairs.addParamIfNotBlank( "page_actor_id", pageId, params );
		return extractBoolean( callMethod( FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, params ) );
	}

	public boolean users_hasAppPermission( Permission perm ) throws FacebookException {
		return users_hasAppPermission( perm, null );
	}

	public boolean users_hasAppPermission( Permission perm, Long userId ) throws FacebookException {
		if ( userId != null ) {
			return extractBoolean10( callMethod( FacebookMethod.USERS_HAS_APP_PERMISSION_NOSESSION, Pairs.newPair( "ext_perm", perm.getName() ), Pairs.newPair( "uid",
					userId ) ) );
		} else {
			return extractBoolean10( callMethod( FacebookMethod.USERS_HAS_APP_PERMISSION, Pairs.newPair( "ext_perm", perm.getName() ) ) );
		}
	}

	public boolean users_setStatus( String newStatus, boolean clear ) throws FacebookException {
		return users_setStatus( newStatus, clear, false );
	}

	/**
	 * Retrieves the requested profile fields for the Facebook Pages with the given <code>pageIds</code>. Can be called for pages that have added the application without
	 * establishing a session.
	 * 
	 * @param pageIds
	 *            the page IDs
	 * @param fields
	 *            a set of page profile fields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo"> Developers Wiki: Pages.getInfo</a>
	 */
	public Object pages_getInfo( Collection<Long> pageIds, Collection<PageProfileField> fields ) throws FacebookException {
		if ( pageIds == null || pageIds.isEmpty() ) {
			throw new IllegalArgumentException( "pageIds cannot be empty or null" );
		}
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		return callMethod( FacebookMethod.PAGES_GET_INFO, Pairs.newPair( "page_ids", BasicClientHelper.delimit( pageIds ) ), Pairs.newPair( "fields", BasicClientHelper
				.delimit( fields ) ) );
	}

	/**
	 * Retrieves the requested profile fields for the Facebook Pages with the given <code>pageIds</code>. Can be called for pages that have added the application without
	 * establishing a session.
	 * 
	 * @param pageIds
	 *            the page IDs
	 * @param fields
	 *            a set of page profile fields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo"> Developers Wiki: Pages.getInfo</a>
	 */
	public Object pages_getInfo( Collection<Long> pageIds, Set<CharSequence> fields ) throws FacebookException {
		if ( pageIds == null || pageIds.isEmpty() ) {
			throw new IllegalArgumentException( "pageIds cannot be empty or null" );
		}
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		return callMethod( FacebookMethod.PAGES_GET_INFO, Pairs.newPair( "page_ids", BasicClientHelper.delimit( pageIds ) ), Pairs.newPair( "fields", BasicClientHelper
				.delimit( fields ) ) );
	}

	/**
	 * Retrieves the requested profile fields for the Facebook Pages of the user with the given <code>userId</code>.
	 * 
	 * @param userId
	 *            the ID of a user about whose pages to fetch info (defaulted to the logged-in user)
	 * @param fields
	 *            a set of PageProfileFields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see http://wiki.developers.facebook.com/index.php/Pages.getInfo
	 */
	public Object pages_getInfo( Long userId, Collection<PageProfileField> fields ) throws FacebookException {
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		if ( userId == null ) {
			userId = cacheUserId;
		}
		if ( userId == null ) {
			return callMethod( FacebookMethod.PAGES_GET_INFO, Pairs.newPair( "fields", BasicClientHelper.delimit( fields ) ) );
		}
		return callMethod( FacebookMethod.PAGES_GET_INFO, Pairs.newPair( "uid", userId ), Pairs.newPair( "fields", BasicClientHelper.delimit( fields ) ) );
	}

	/**
	 * Retrieves the requested profile fields for the Facebook Pages of the user with the given <code>userId</code>.
	 * 
	 * @param userId
	 *            the ID of a user about whose pages to fetch info (defaulted to the logged-in user)
	 * @param fields
	 *            a set of page profile fields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see http://wiki.developers.facebook.com/index.php/Pages.getInfo
	 */
	public Object pages_getInfo( Long userId, Set<CharSequence> fields ) throws FacebookException {
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		if ( userId == null ) {
			userId = cacheUserId;
		}
		if ( userId == null ) {
			return callMethod( FacebookMethod.PAGES_GET_INFO, Pairs.newPair( "fields", BasicClientHelper.delimit( fields ) ) );
		}
		return callMethod( FacebookMethod.PAGES_GET_INFO, Pairs.newPair( "uid", userId ), Pairs.newPair( "fields", BasicClientHelper.delimit( fields ) ) );
	}

	/**
	 * Checks whether a page has added the application
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the page has added the application
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isAppAdded"> Developers Wiki: Pages.isAppAdded</a>
	 */
	public boolean pages_isAppAdded( Long pageId ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_APP_ADDED, Pairs.newPair( "page_id", pageId ) ) );
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
	public boolean pages_isFan( Long pageId, Long userId ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_FAN, Pairs.newPair( "page_id", pageId ), Pairs.newPair( "uid", userId ) ) );
	}

	/**
	 * Checks whether the logged-in user is a fan of the page with the given <code>pageId</code>.
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the logged-in user is a fan of the page
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isFan"> Developers Wiki: Pages.isFan</a>
	 */
	public boolean pages_isFan( Long pageId ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_FAN, Pairs.newPair( "page_id", pageId ) ) );
	}

	/**
	 * Checks whether the logged-in user for this session is an admin of the page with the given <code>pageId</code>.
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the logged-in user is an admin
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isAdmin"> Developers Wiki: Pages.isAdmin</a>
	 */
	public boolean pages_isAdmin( Long pageId ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_ADMIN, Pairs.newPair( "page_id", pageId ) ) );
	}

	/**
	 * Associates a "<code>handle</code>" with FBML markup so that the handle can be used within the <a
	 * href="http://wiki.developers.facebook.com/index.php/Fb:ref">fb:ref</a> FBML tag. A handle is unique within an application and allows an application to publish
	 * identical FBML to many user profiles and do subsequent updates without having to republish FBML for each user.
	 * 
	 * @param handle
	 *            - a string, unique within the application, that
	 * @param fbmlMarkup
	 *            - refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Fbml.setRefHandle"> Developers Wiki: Fbml.setRefHandle</a>
	 */
	public boolean fbml_setRefHandle( String handle, String fbmlMarkup ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.FBML_SET_REF_HANDLE, Pairs.newPair( "handle", handle ), Pairs.newPair( "fbml", fbmlMarkup ) ) );

	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( newStatus != null ) {
			params.add( Pairs.newPair( "status", newStatus ) );
		}
		if ( clear ) {
			params.add( Pairs.newPair( "clear", "true" ) );
		}
		if ( statusIncludesVerb ) {
			params.add( Pairs.newPair( "status_includes_verb", "true" ) );
		}
		return extractBoolean( callMethod( FacebookMethod.USERS_SET_STATUS, params ) );
	}

	public Object data_getCookies() throws FacebookException {
		return data_getCookies( users_getLoggedInUser(), null );
	}

	public Object data_getCookies( Long userId ) throws FacebookException {
		return data_getCookies( userId, null );
	}

	public Object data_getCookies( String name ) throws FacebookException {
		return data_getCookies( users_getLoggedInUser(), name );
	}

	public Object data_getCookies( Long userId, CharSequence name ) throws FacebookException {
		if ( name == null ) {
			return callMethod( FacebookMethod.DATA_GET_COOKIES, Pairs.newPair( "uid", userId ) );
		} else {
			return callMethod( FacebookMethod.DATA_GET_COOKIES, Pairs.newPair( "uid", userId ), Pairs.newPair( "name", name ) );
		}
	}

	public boolean data_setCookie( String name, String value ) throws FacebookException {
		return data_setCookie( users_getLoggedInUser(), name, value, null, null );
	}

	public boolean data_setCookie( String name, String value, String path ) throws FacebookException {
		return data_setCookie( users_getLoggedInUser(), name, value, null, path );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value ) throws FacebookException {
		return data_setCookie( userId, name, value, null, null );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, CharSequence path ) throws FacebookException {
		return data_setCookie( userId, name, value, null, path );
	}

	public boolean data_setCookie( String name, String value, Long expires ) throws FacebookException {
		return data_setCookie( users_getLoggedInUser(), name, value, expires, null );
	}

	public boolean data_setCookie( String name, String value, Long expires, String path ) throws FacebookException {
		return data_setCookie( users_getLoggedInUser(), name, value, expires, path );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires ) throws FacebookException {
		return data_setCookie( userId, name, value, expires, null );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires, CharSequence path ) throws FacebookException {
		if ( ( name == null ) || ( "".equals( name ) ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "The cookie name cannot be null or empty!" );
		}
		if ( value == null ) {
			value = "";
		}
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 5 );
		Pairs.addParam( "uid", userId, params );
		Pairs.addParam( "name", name, params );
		Pairs.addParam( "value", value, params );
		Pairs.addParamIfNotBlankZero( "expires", expires, params );
		Pairs.addParamIfNotBlank( "path", path, params );
		String doc = callMethod( FacebookMethod.DATA_SET_COOKIE, params );
		return extractBoolean( doc );
	}

	public String data_getUserPreference( int prefId ) throws FacebookException {
		return extractString( callMethod( FacebookMethod.DATA_GET_USER_PREFERENCE, Pairs.newPair( "pref_id", prefId ) ) );
	}

	public Object data_getUserPreferences() throws FacebookException {
		return callMethod( FacebookMethod.DATA_GET_USER_PREFERENCES );
	}

	public void data_setUserPreference( int prefId, String value ) throws FacebookException {
		if ( value != null && value.length() > 128 ) {
			throw new FacebookException(
					ErrorCode.GEN_INVALID_PARAMETER,
					String
							.format(
									"Attempt to set a preference which hold a maximum of 128 characters to a value with %d characters. The Facebook API silently truncates this value to 128 characters which can lead to unpredictable results. If you want the truncation behaviour, please truncate the string in your Java code.",
									value.length() ) );
		}
		validateVoidResponse( callMethod( FacebookMethod.DATA_SET_USER_PREFERENCE, Pairs.newPair( "pref_id", prefId ), Pairs.newPair( "value", value ) ) );
	}

	public void data_setUserPreferences( Map<Integer,String> values, boolean replace ) throws FacebookException {
		JSONObject prefs = new JSONObject();
		for ( Integer key : values.keySet() ) {
			try {
				prefs.put( key.toString(), values.get( key ) );
			}
			catch ( JSONException ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
		}

		validateVoidResponse( callMethod( FacebookMethod.DATA_SET_USER_PREFERENCES, Pairs.newPair( "values", prefs.toString() ), Pairs.newPairTF( "replace", replace ) ) );
	}

	public long data_createObject( String objectType, Map<String,String> properties ) throws FacebookException {
		return extractLong( callMethod( FacebookMethod.DATA_CREATE_OBJECT, Pairs.newPair( "obj_type", objectType ), Pairs.newPair( "properties", JsonHelper
				.toJson( properties ) ) ) );
	}

	public void data_updateObject( long objectId, Map<String,String> properties, boolean replace ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_UPDATE_OBJECT, Pairs.newPair( "obj_id", String.valueOf( objectId ) ), Pairs.newPair( "properties",
				JsonHelper.toJson( properties ) ), Pairs.newPairTF( "replace", replace ) ) );
	}

	public void data_deleteObject( long objectId ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_DELETE_OBJECT, Pairs.newPair( "obj_id", objectId ) ) );
	}

	public void data_deleteObjects( Collection<Long> objectIds ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_DELETE_OBJECTS, Pairs.newPair( "obj_ids", BasicClientHelper.delimit( objectIds ) ) ) );
	}

	public Object data_getObject( long objectId ) throws FacebookException {
		return callMethod( FacebookMethod.DATA_GET_OBJECT, Pairs.newPair( "obj_id", objectId ) );
	}

	public Object data_getObjects( Collection<Long> objectIds ) throws FacebookException {
		return callMethod( FacebookMethod.DATA_GET_OBJECTS, Pairs.newPair( "obj_ids", BasicClientHelper.delimit( objectIds ) ) );
	}

	public Object data_getObjectProperty( long objectId, String propertyName ) throws FacebookException {
		return callMethod( FacebookMethod.DATA_GET_OBJECT_PROPERTY, Pairs.newPair( "obj_id", objectId ), Pairs.newPair( "prop_name", propertyName ) );
	}

	public void data_setObjectProperty( long objectId, String propertyName, String value ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_SET_OBJECT_PROPERTY, Pairs.newPair( "obj_id", objectId ), Pairs.newPair( "prop_name", propertyName ), Pairs
				.newPair( "value", value ) ) );
	}

	public void data_createObjectType( String name ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_CREATE_OBJECT_TYPE, Pairs.newPair( "name", name ) ) );
	}

	public void data_dropObjectType( String objectType ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_DROP_OBJECT_TYPE, Pairs.newPair( "obj_type", objectType ) ) );
	}

	public void data_renameObjectType( String objectType, String newName ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_RENAME_OBJECT_TYPE, Pairs.newPair( "obj_type", objectType ), Pairs.newPair( "new_name", newName ) ) );

	}

	public void data_defineObjectProperty( String objectType, String propertyName, PropertyType propertyType ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_DEFINE_OBJECT_PROPERTY, Pairs.newPair( "obj_type", objectType ),
				Pairs.newPair( "prop_name", propertyName ), Pairs.newPair( "prop_type", propertyType.getValue() ) ) );
	}

	public void data_undefineObjectProperty( String objectType, String propertyName ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_UNDEFINE_OBJECT_PROPERTY, Pairs.newPair( "obj_type", objectType ), Pairs
				.newPair( "prop_name", propertyName ) ) );
	}

	public void data_renameObjectProperty( String objectType, String propertyName, String newPropertyName ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_RENAME_OBJECT_PROPERTY, Pairs.newPair( "obj_type", objectType ),
				Pairs.newPair( "prop_name", propertyName ), Pairs.newPair( "new_name", newPropertyName ) ) );
	}

	public Object data_getObjectTypes() throws FacebookException {
		return callMethod( FacebookMethod.DATA_GET_OBJECT_TYPES );
	}

	public Object data_getObjectType( String objectType ) throws FacebookException {
		return callMethod( FacebookMethod.DATA_GET_OBJECT_TYPE, Pairs.newPair( "obj_type", objectType ) );
	}

	public void data_defineAssociation( String associationName, AssociationType associationType, AssociationInfo associationInfo1, AssociationInfo associationInfo2,
			String inverseName ) throws FacebookException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 5 );
		Pairs.addParam( "name", associationName, params );
		Pairs.addParam( "assoc_type", associationType.getValue(), params );
		JSONObject assocInfo1 = new JSONObject();
		try {
			assocInfo1.put( "alias", associationInfo1.getAlias() );
			assocInfo1.put( "object_type", associationInfo1.getObjectType() );
			assocInfo1.put( "unique", associationInfo1.isUnique() );
		}
		catch ( JSONException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
		Pairs.addParam( "assoc_info1", assocInfo1.toString(), params );
		JSONObject assocInfo2 = new JSONObject();
		try {
			assocInfo2.put( "alias", associationInfo2.getAlias() );
			assocInfo2.put( "object_type", associationInfo2.getObjectType() );
			assocInfo2.put( "unique", associationInfo2.isUnique() );
		}
		catch ( JSONException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
		Pairs.addParam( "assoc_info2", assocInfo2, params );
		Pairs.addParamIfNotBlank( "inverse", inverseName, params );

		validateVoidResponse( callMethod( FacebookMethod.DATA_DEFINE_ASSOCIATION, params ) );
	}

	public void data_undefineAssociation( String name ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_UNDEFINE_ASSOCIATION, Pairs.newPair( "name", name ) ) );
	}

	public void data_renameAssociation( String name, String newName, String newAlias1, String newAlias2 ) throws FacebookException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 4 );
		Pairs.addParam( "name", name, params );
		Pairs.addParam( "new_name", newName, params );
		Pairs.addParamIfNotBlank( "new_alias1", newAlias1, params );
		Pairs.addParamIfNotBlank( "new_alias2", newAlias2, params );

		validateVoidResponse( callMethod( FacebookMethod.DATA_RENAME_ASSOCIATION, params ) );
	}

	public Object data_getAssociationDefinition( String name ) throws FacebookException {
		return callMethod( FacebookMethod.DATA_GET_ASSOCIATION_DEFINITION, Pairs.newPair( "name", name ) );
	}

	public Object data_getAssociationDefinitions() throws FacebookException {
		return callMethod( FacebookMethod.DATA_GET_ASSOCIATION_DEFINITIONS );
	}


	public void data_setAssociation( String associationName, long object1Id, long object2Id, String data, Date associationTime ) throws FacebookException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 5 );
		Pairs.addParam( "name", associationName, params );
		Pairs.addParam( "obj_id1", object1Id, params );
		Pairs.addParam( "obj_id2", object2Id, params );
		Pairs.addParamIfNotBlank( "data", data, params );
		Pairs.addParamSecondsIfNotBlank( "assoc_time", associationTime, params );
		validateVoidResponse( callMethod( FacebookMethod.DATA_SET_ASSOCIATION, params ) );
	}

	public void data_removeAssociation( String associationName, long object1Id, long object2Id ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_REMOVE_ASSOCIATION, Pairs.newPair( "name", associationName ), Pairs.newPair( "obj_id1", object1Id ), Pairs
				.newPair( "obj_id2", object2Id ) ) );
	}

	public void data_removeAssociatedObjects( String associationName, long objectId ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.DATA_REMOVE_ASSOCIATED_OBJECTS, Pairs.newPair( "name", associationName ), Pairs.newPair( "obj_id", objectId ) ) );
	}

	public long data_getAssociatedObjectCount( String associationName, long objectId ) throws FacebookException {
		return extractLong( callMethod( FacebookMethod.DATA_GET_ASSOCIATED_OBJECT_COUNT, Pairs.newPair( "name", associationName ), Pairs.newPair( "obj_id", objectId ) ) );
	}

	public boolean admin_setAppProperties( Map<ApplicationProperty,String> properties ) throws FacebookException {
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
				catch ( JSONException ex ) {
					throw BasicClientHelper.runtimeException( ex );
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
				catch ( JSONException ex ) {
					throw BasicClientHelper.runtimeException( ex );
				}
			}
		}

		// now we've built our JSON-encoded parameter, so attempt to set the properties
		try {
			// first assume that Facebook is sensible enough to be able to undestand an associative array
			String d = callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, Pairs.newPair( "properties", encoding1 ) );
			return extractBoolean( d );
		}
		catch ( FacebookException e ) {
			// if that didn't work, try the more convoluted encoding (which matches what they send back in response to admin_getAppProperties calls)
			String d = callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, Pairs.newPair( "properties", encoding2 ) );
			return extractBoolean( d );
		}
	}

	/**
	 * @deprecated use admin_getAppPropertiesMap() instead
	 */
	@Deprecated
	public JSONObject admin_getAppProperties( Collection<ApplicationProperty> properties ) throws FacebookException {
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
			throw new FacebookException( -1, "Failed to parse server response:  " + json );
		}
	}

	public Map<ApplicationProperty,String> admin_getAppPropertiesMap( Collection<ApplicationProperty> properties ) throws FacebookException {
		return parseAppProperties( admin_getAppPropertiesAsString( properties ) );
	}

	protected static Map<ApplicationProperty,String> parseAppProperties( String json ) {
		Map<ApplicationProperty,String> out = new TreeMap<ApplicationProperty,String>();
		if ( json == null ) {
			return null;
		}
		if ( json.startsWith( "{" ) ) {
			try {
				JSONObject obj = new JSONObject( json );
				Iterator<String> keys = obj.keys();
				while ( keys.hasNext() ) {
					String key = keys.next();
					String val = obj.getString( key );
					out.put( ApplicationProperty.valueOf( key.toUpperCase() ), val );
				}
			}
			catch ( JSONException ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
		}
		return out;
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate ) throws FacebookException {
		return feed_publishTemplatizedAction( titleTemplate, null );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Long pageActorId ) throws FacebookException {
		return feed_publishTemplatizedAction( titleTemplate, null, null, null, null, null, null, pageActorId );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images,
			Long pageActorId ) throws FacebookException {
		assert null != titleTemplate && !"".equals( titleTemplate );

		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 15 );

		params.add( Pairs.newPair( "title_template", titleTemplate ) );
		if ( null != titleData && !titleData.isEmpty() ) {
			JSONObject titleDataJson = new JSONObject();
			try {
				for ( String key : titleData.keySet() ) {
					titleDataJson.put( key, titleData.get( key ) );
				}
			}
			catch ( Exception ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
			params.add( Pairs.newPair( "title_data", titleDataJson ) );
		}

		if ( null != bodyTemplate && !"".equals( bodyTemplate ) ) {
			params.add( Pairs.newPair( "body_template", bodyTemplate ) );
			if ( null != bodyData && !bodyData.isEmpty() ) {
				JSONObject bodyDataJson = new JSONObject();
				try {
					for ( String key : bodyData.keySet() ) {
						bodyDataJson.put( key, bodyData.get( key ) );
					}
				}
				catch ( Exception ex ) {
					throw BasicClientHelper.runtimeException( ex );
				}
				params.add( Pairs.newPair( "body_data", bodyDataJson ) );
			}
		}

		if ( null != bodyTemplate && !"".equals( bodyTemplate ) ) {
			params.add( Pairs.newPair( "body_template", bodyTemplate ) );
		}

		if ( null != targetIds && !targetIds.isEmpty() ) {
			params.add( Pairs.newPair( "target_ids", BasicClientHelper.delimit( targetIds ) ) );
		}

		if ( bodyGeneral != null ) {
			params.add( Pairs.newPair( "body_general", bodyGeneral ) );
		}

		if ( pageActorId != null ) {
			params.add( Pairs.newPair( "page_actor_id", pageActorId ) );
		}

		handleFeedImages( params, images );

		return extractBoolean( callMethod( FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, params ) );
	}

	public Object friends_getList( Long friendListId ) throws FacebookException {
		if ( null != friendListId && 0L <= friendListId ) {
			throw new IllegalArgumentException( "given invalid friendListId " + friendListId );
		}
		return callMethod( FacebookMethod.FRIENDS_GET, Pairs.newPair( "flid", friendListId ) );
	}

	public Object friends_getLists() throws FacebookException {
		return callMethod( FacebookMethod.FRIENDS_GET_LISTS );
	}

	public boolean admin_setAppProperties( ApplicationPropertySet properties ) throws FacebookException {
		if ( null == properties || properties.isEmpty() ) {
			throw new IllegalArgumentException( "expecting a non-empty set of application properties" );
		}
		return extractBoolean( callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, Pairs.newPair( "properties", properties.toJson() ) ) );
	}

	public ApplicationPropertySet admin_getAppPropertiesAsSet( Collection<ApplicationProperty> properties ) throws FacebookException {
		String propJson = admin_getAppPropertiesAsString( properties );
		return new ApplicationPropertySet( propJson );
	}

	public void beginBatch() {
		batchMode = true;
		queries = new ArrayList<BatchQuery>();
	}

	protected String encodeMethods( List<BatchQuery> queryList ) throws FacebookException {
		JSONArray result = new JSONArray();
		for ( BatchQuery query : queryList ) {
			if ( query.getMethod().takesFile() ) {
				throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "File upload API calls cannot be batched:  " + query.getMethod().methodName() );
			}
			result.put( BasicClientHelper.delimit( query.getParams().entrySet(), "&", "=", true ) );
		}
		return result.toString();
	}

	public String batch_run( String methods, boolean serial ) throws FacebookException {
		if ( !serial ) {
			return callMethod( FacebookMethod.BATCH_RUN, Pairs.newPair( "method_feed", methods ) );
		} else {
			return callMethod( FacebookMethod.BATCH_RUN, Pairs.newPair( "method_feed", methods ), Pairs.newPair( "serial_only", "1" ) );
		}
	}

	public Object application_getPublicInfo( Long applicationId, String applicationKey, String applicationCanvas ) throws FacebookException {
		Pair<String,CharSequence> pair = null;
		if ( ( applicationId != null ) && ( applicationId > 0 ) ) {
			pair = Pairs.newPair( "application_id", applicationId );
		} else if ( ( applicationKey != null ) && ( !"".equals( applicationKey ) ) ) {
			pair = Pairs.newPair( "application_api_key", applicationKey );
		} else if ( ( applicationCanvas != null ) && ( !"".equals( applicationCanvas ) ) ) {
			pair = Pairs.newPair( "application_canvas_name", applicationCanvas );
		} else {
			// we need at least one of them to be valid
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You must specify at least/most one of {applicationId, applicationKey, applicationCanvas}" );
		}
		return callMethod( FacebookMethod.APPLICATION_GET_PUBLIC_INFO, pair );
	}

	public Object application_getPublicInfoById( Long applicationId ) throws FacebookException {
		return application_getPublicInfo( applicationId, null, null );
	}

	public Object application_getPublicInfoByApiKey( String applicationKey ) throws FacebookException {
		return application_getPublicInfo( null, applicationKey, null );
	}

	public Object application_getPublicInfoByCanvasName( String applicationCanvas ) throws FacebookException {
		return application_getPublicInfo( null, null, applicationCanvas );
	}

	public int admin_getAllocation( String allocationType ) throws FacebookException {
		return extractInt( callMethod( FacebookMethod.ADMIN_GET_ALLOCATION, Pairs.newPair( "integration_point_name", allocationType ) ) );
	}

	public int admin_getAllocation( String allocationType, Long userId ) throws FacebookException {
		if ( userId != null ) {
			return extractInt( callMethod( FacebookMethod.ADMIN_GET_ALLOCATION, Pairs.newPair( "integration_point_name", allocationType ), Pairs.newPair( "user", userId ) ) );
		}
		return extractInt( callMethod( FacebookMethod.ADMIN_GET_ALLOCATION, Pairs.newPair( "integration_point_name", allocationType ) ) );
	}

	public int admin_getAllocation( AllocationType allocationType ) throws FacebookException {
		return admin_getAllocation( allocationType.getName() );
	}

	public int admin_getAllocation( AllocationType allocationType, Long userId ) throws FacebookException {
		return admin_getAllocation( allocationType.getName(), userId );
	}

	@Deprecated
	public int admin_getNotificationAllocation() throws FacebookException {
		return admin_getAllocation( "notifications_per_day" );
	}

	@Deprecated
	public int admin_getRequestAllocation() throws FacebookException {
		return admin_getAllocation( "requests_per_day" );
	}

	@Deprecated
	public Object admin_getDailyMetrics( Set<Metric> metrics, Date start, Date end ) throws FacebookException {
		return admin_getDailyMetrics( metrics, start.getTime(), end.getTime() );
	}

	@Deprecated
	public Object admin_getDailyMetrics( Set<Metric> metrics, long start, long end ) throws FacebookException {
		int size = 2 + ( ( metrics != null ) ? metrics.size() : 0 );
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( size );
		if ( metrics != null ) {
			metrics.remove( Metric.ACTIVE_USERS );
			if ( !metrics.isEmpty() ) {
				JSONArray metricsJson = new JSONArray();
				for ( Metric metric : metrics ) {
					metricsJson.put( metric.getName() );
				}
				params.add( Pairs.newPair( "metrics", metricsJson ) );
			}
		}
		params.add( Pairs.newPair( "start_date", ( start / 1000 ) ) );
		params.add( Pairs.newPair( "end_date", ( end / 1000 ) ) );
		return callMethod( FacebookMethod.ADMIN_GET_DAILY_METRICS, params );
	}

	public Object permissions_checkGrantedApiAccess( String apiKey ) throws FacebookException {
		return callMethod( FacebookMethod.PERM_CHECK_GRANTED_API_ACCESS, Pairs.newPair( "permissions_apikey", apiKey ) );
	}

	public Object permissions_checkAvailableApiAccess( String apiKey ) throws FacebookException {
		return callMethod( FacebookMethod.PERM_CHECK_AVAILABLE_API_ACCESS, Pairs.newPair( "permissions_apikey", apiKey ) );
	}

	public boolean permissions_grantApiAccess( String apiKey, Set<FacebookMethod> methods ) throws FacebookException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( ( methods != null ) && ( !methods.isEmpty() ) ) {
			JSONArray methodsJson = new JSONArray();
			for ( FacebookMethod method : methods ) {
				methodsJson.put( method.methodName() );
			}
			params.add( Pairs.newPair( "method_arr", methodsJson ) );
		}
		params.add( Pairs.newPair( "permissions_apikey", apiKey ) );
		return extractBoolean( callMethod( FacebookMethod.PERM_GRANT_API_ACCESS, params ) );
	}

	public boolean permissions_grantFullApiAccess( String apiKey ) throws FacebookException {
		return permissions_grantApiAccess( apiKey, null );
	}

	public boolean permissions_revokeApiAccess( String apiKey ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.PERM_REVOKE_API_ACCESS, Pairs.newPair( "permissions_apikey", apiKey ) ) );
	}

	public String auth_promoteSession() throws FacebookException {
		return extractString( callMethod( FacebookMethod.AUTH_PROMOTE_SESSION ) );
	}

	public boolean auth_revokeAuthorization() throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.AUTH_REVOKE_AUTHORIZATION ) );
	}

	public boolean auth_revokeExtendedPermission( Permission perm ) throws FacebookException {
		return auth_revokeExtendedPermission( perm, null );
	}

	public boolean auth_revokeExtendedPermission( Permission perm, Long userId ) throws FacebookException {
		if ( userId != null ) {
			return extractBoolean( callMethod( FacebookMethod.AUTH_REVOKE_EXTENDED_PERMISSION_NOSESSION, Pairs.newPair( "perm", perm.getName() ), Pairs.newPair( "uid",
					userId ) ) );
		} else {
			return extractBoolean( callMethod( FacebookMethod.AUTH_REVOKE_EXTENDED_PERMISSION, Pairs.newPair( "perm", perm.getName() ) ) );
		}
	}

	public boolean auth_expireSession() throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.AUTH_EXPIRE_SESSION ) );
	}

	public boolean users_setStatus( String status, Long userId ) throws FacebookException {
		return users_setStatus( status, false, userId );
	}

	public boolean users_setStatus( String newStatus, boolean clear, Long userId ) throws FacebookException {
		return users_setStatus( newStatus, clear, false, userId );
	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb, Long userId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( newStatus != null ) {
			params.add( Pairs.newPair( "status", newStatus ) );
		}
		if ( clear ) {
			params.add( Pairs.newPair( "clear", "true" ) );
		}
		if ( statusIncludesVerb ) {
			params.add( Pairs.newPair( "status_includes_verb", "true" ) );
		}
		params.add( Pairs.newPair( "uid", userId ) );
		return extractBoolean( callMethod( FacebookMethod.USERS_SET_STATUS_NOSESSION, params ) );
	}

	@Deprecated
	public Object feed_getRegisteredTemplateBundleByID( Long id ) throws FacebookException {
		return callMethod( FacebookMethod.FEED_GET_TEMPLATE_BY_ID, Pairs.newPair( "template_bundle_id", id ) );
	}

	@Deprecated
	public Object feed_getRegisteredTemplateBundles() throws FacebookException {
		return callMethod( FacebookMethod.FEED_GET_TEMPLATES );
	}

	@Deprecated
	public Boolean feed_publishUserAction( Long bundleId ) throws FacebookException {
		return feed_publishUserAction( bundleId, null, null, null );
	}

	@Deprecated
	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<Long> targetIds, String bodyGeneral ) throws FacebookException {
		return feed_publishUserAction( bundleId, templateData, null, targetIds, bodyGeneral, 0 );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( String template ) throws FacebookException {
		List<String> temp = new ArrayList<String>();
		temp.add( template );
		return feed_registerTemplateBundle( temp );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( Collection<String> templates ) throws FacebookException {
		return feed_registerTemplateBundle( templates, null, null );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( Collection<String> templates, Collection<BundleStoryTemplate> shortTemplates, BundleStoryTemplate longTemplate )
			throws FacebookException {
		return feed_registerTemplateBundle( templates, shortTemplates, longTemplate, null );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( Collection<String> templates, Collection<BundleStoryTemplate> shortTemplates, BundleStoryTemplate longTemplate,
			List<BundleActionLink> actionLinks ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		JSONArray templateArray = new JSONArray();
		for ( String template : templates ) {
			templateArray.put( template );
		}
		params.add( Pairs.newPair( "one_line_story_templates", templateArray ) );
		if ( shortTemplates != null && !shortTemplates.isEmpty() ) {
			JSONArray shortArray = new JSONArray();
			for ( BundleStoryTemplate template : shortTemplates ) {
				shortArray.put( template.toJson() );
			}
			params.add( Pairs.newPair( "short_story_templates", shortArray ) );
		}
		if ( longTemplate != null ) {
			params.add( Pairs.newPair( "full_story_template", longTemplate.toJson() ) );
		}

		if ( actionLinks != null && !actionLinks.isEmpty() ) {
			JSONArray actionLinkArray = new JSONArray();
			for ( BundleActionLink actionLink : actionLinks ) {
				actionLinkArray.put( actionLink.toJson() );
			}
			params.add( Pairs.newPair( "action_links", actionLinkArray ) );
		}

		return extractLong( callMethod( FacebookMethod.FEED_REGISTER_TEMPLATE, params ) );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( String template, String shortTemplate, String longTemplate ) throws FacebookException {
		List<String> templates = new ArrayList<String>();
		templates.add( template );
		return feed_registerTemplateBundle( templates, null, null );
	}

	@Deprecated
	public Object profile_getFBML() throws FacebookException {
		return callMethod( FacebookMethod.PROFILE_GET_FBML );
	}

	@Deprecated
	public Object profile_getFBML( Long userId ) throws FacebookException {
		if ( userId != null ) {
			return callMethod( FacebookMethod.PROFILE_GET_FBML_NOSESSION, Pairs.newPair( "uid", userId ) );
		} else {
			return callMethod( FacebookMethod.PROFILE_GET_FBML );
		}
	}

	@Deprecated
	public Object profile_getFBML( int type ) throws FacebookException {
		return callMethod( FacebookMethod.PROFILE_GET_FBML, Pairs.newPair( "type", type ) );
	}

	@Deprecated
	public Object profile_getFBML( int type, Long userId ) throws FacebookException {
		if ( userId != null ) {
			return callMethod( FacebookMethod.PROFILE_GET_FBML_NOSESSION, Pairs.newPair( "type", type ), Pairs.newPair( "uid", userId ) );
		} else {
			return callMethod( FacebookMethod.PROFILE_GET_FBML, Pairs.newPair( "type", type ) );
		}
	}

	@Deprecated
	public Object profile_getInfo( Long userId ) throws FacebookException {
		return callMethod( FacebookMethod.PROFILE_GET_INFO, Pairs.newPair( "uid", userId ) );
	}

	@Deprecated
	public Object profile_getInfoOptions( String field ) throws FacebookException {
		return callMethod( FacebookMethod.PROFILE_GET_INFO_OPTIONS, Pairs.newPair( "field", field ) );
	}

	@Deprecated
	public void profile_setInfo( Long userId, String title, boolean textOnly, List<ProfileInfoField> fields ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		JSONArray json = new JSONArray();
		params.add( Pairs.newPair( "uid", userId ) );
		params.add( Pairs.newPair( "title", title ) );
		if ( textOnly ) {
			params.add( Pairs.newPair( "type", "1" ) );
		} else {
			params.add( Pairs.newPair( "type", "5" ) );
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
			catch ( Exception ex ) {
				throw BasicClientHelper.runtimeException( ex );
			}
		}
		params.add( Pairs.newPair( "info_fields", json ) );
		validateVoidResponse( callMethod( FacebookMethod.PROFILE_SET_INFO, params ) );
	}

	@Deprecated
	public void profile_setInfoOptions( ProfileInfoField field ) throws FacebookException {
		JSONArray json = new JSONArray();
		for ( ProfileFieldItem item : field.getItems() ) {
			JSONObject itemJSON = new JSONObject();
			for ( String key : item.getMap().keySet() ) {
				try {
					itemJSON.put( key, item.getMap().get( key ) );
				}
				catch ( Exception ex ) {
					throw BasicClientHelper.runtimeException( ex );
				}
			}
			json.put( itemJSON );
		}
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 2 );
		Pairs.addParam( "field", field.getFieldName(), params );
		Pairs.addParam( "options", json.toString(), params );
		validateVoidResponse( callMethod( FacebookMethod.PROFILE_SET_INFO_OPTIONS, params ) );
	}

	@Deprecated
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup ) throws FacebookException {
		return profile_setFBML( null, toString( profileFbmlMarkup ), toString( profileActionFbmlMarkup ), null, null );
	}

	@Deprecated
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, Long profileId ) throws FacebookException {
		return profile_setFBML( profileId, toString( profileFbmlMarkup ), toString( profileActionFbmlMarkup ), null, null );
	}

	@Deprecated
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup ) throws FacebookException {
		return profile_setFBML( null, toString( profileFbmlMarkup ), toString( profileActionFbmlMarkup ), toString( mobileFbmlMarkup ), null );
	}

	@Deprecated
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup, Long profileId )
			throws FacebookException {
		return profile_setFBML( profileId, toString( profileFbmlMarkup ), toString( profileActionFbmlMarkup ), toString( mobileFbmlMarkup ), null );
	}

	@Deprecated
	public boolean profile_setMobileFBML( CharSequence fbmlMarkup ) throws FacebookException {
		return profile_setFBML( null, null, null, toString( fbmlMarkup ), null );
	}

	@Deprecated
	public boolean profile_setMobileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		return profile_setFBML( profileId, null, null, toString( fbmlMarkup ), null );
	}

	@Deprecated
	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup ) throws FacebookException {
		return profile_setFBML( null, null, toString( fbmlMarkup ), null, null );
	}

	@Deprecated
	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		return profile_setFBML( profileId, null, toString( fbmlMarkup ), null, null );
	}

	@Deprecated
	public boolean profile_setProfileFBML( CharSequence fbmlMarkup ) throws FacebookException {
		return profile_setFBML( null, toString( fbmlMarkup ), null, null, null );
	}

	@Deprecated
	public boolean profile_setProfileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		return profile_setFBML( profileId, toString( fbmlMarkup ), null, null, null );
	}

	@Deprecated
	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml ) throws FacebookException {
		return profile_setFBML( userId, profileFbml, actionFbml, mobileFbml, null );
	}

	@Deprecated
	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml, String profileMain ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 5 );
		Pairs.addParamIfNotBlank( "uid", userId, params );
		Pairs.addParamIfNotBlank( "profile", profileFbml, params );
		Pairs.addParamIfNotBlank( "profile_action", actionFbml, params );
		Pairs.addParamIfNotBlank( "mobile_fbml", mobileFbml, params );
		Pairs.addParamIfNotBlank( "profile_main", profileMain, params );
		FacebookMethod method = ( isDesktop() || userId == null ) ? FacebookMethod.PROFILE_SET_FBML : FacebookMethod.PROFILE_SET_FBML_NOSESSION;
		return extractBoolean( callMethod( method, params ) );
	}

	public Boolean liveMessage_send( Long recipient, String eventName, JSONObject message ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.LIVEMESSAGE_SEND, Pairs.newPair( "recipient", recipient ), Pairs.newPair( "event_name", eventName ), Pairs
				.newPair( "message", message ) ) );
	}

	public Long links_post( Long userId, String url, String comment ) throws FacebookException {
		return extractLong( callMethod( FacebookMethod.LINKS_POST, Pairs.newPair( "uid", userId ), Pairs.newPair( "url", url ), Pairs.newPair( "comment", comment ) ) );
	}

	public Object admin_getMetrics( Set<Metric> metrics, Date start, Date end, long period ) throws FacebookException {
		return admin_getMetrics( metrics, start.getTime(), end.getTime(), period );
	}

	public Object admin_getMetrics( Set<Metric> metrics, long start, long end, long period ) throws FacebookException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( metrics != null && !metrics.isEmpty() ) {
			JSONArray metricsJson = new JSONArray();
			for ( Metric metric : metrics ) {
				metricsJson.put( metric.getName() );
			}
			Pairs.addParam( "metrics", metricsJson, params );
		}
		Pairs.addParam( "start_time", ( start / 1000 ), params );
		Pairs.addParam( "end_time", ( end / 1000 ), params );
		Pairs.addParam( "period", period, params );
		return callMethod( FacebookMethod.ADMIN_GET_METRICS, params );
	}

	@Deprecated
	public boolean feed_deactivateTemplateBundleByID( Long bundleId ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.FEED_DEACTIVATE_TEMPLATE_BUNDLE, Pairs.newPair( "template_bundle_id", bundleId ) ) );
	}

	@Deprecated
	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<IFeedImage> images, List<Long> targetIds, String bodyGeneral,
			int storySize ) throws FacebookException {

		// validate maximum of 4 images
		if ( images != null && images.size() > 4 ) {
			throw new IllegalArgumentException( "Maximum of 4 images allowed per feed item." );
		}

		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( Pairs.newPair( "template_bundle_id", bundleId ) );

		if ( targetIds != null && !targetIds.isEmpty() ) {
			params.add( Pairs.newPair( "target_ids", BasicClientHelper.delimit( targetIds ) ) );
		}

		if ( bodyGeneral != null && !"".equals( bodyGeneral ) ) {
			params.add( Pairs.newPair( "body_general", bodyGeneral ) );
		}

		if ( storySize == 1 || storySize == 2 || storySize == 4 ) {
			params.add( Pairs.newPair( "story_size", storySize ) );
		}

		JSONObject jsonTemplateData = new JSONObject();
		if ( templateData != null && !templateData.isEmpty() ) {
			for ( String key : templateData.keySet() ) {
				try {
					jsonTemplateData.put( key, templateData.get( key ) );
				}
				catch ( Exception exception ) {
					throw BasicClientHelper.runtimeException( exception );
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
				throw BasicClientHelper.runtimeException( exception );
			}
		}

		// associate to param
		if ( jsonTemplateData.length() > 0 ) {
			params.add( Pairs.newPair( "template_data", jsonTemplateData ) );
		}

		return extractBoolean( callMethod( FacebookMethod.FEED_PUBLISH_USER_ACTION, params ) );
	}

	public Object stream_get( final Long viewerId, final List<Long> sourceIds, final Date start, final Date end, final Integer limit, final String filterKey,
			final List<String> metadata ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();

		if ( viewerId != null ) {
			params.add( Pairs.newPair( "viewer_id", viewerId ) );
		}

		if ( sourceIds != null && !sourceIds.isEmpty() ) {
			params.add( Pairs.newPair( "source_ids", BasicClientHelper.delimit( sourceIds ) ) );
		}

		if ( start != null ) {
			params.add( Pairs.newPair( "start_time", ( start.getTime() / 1000 ) ) );
		}

		if ( end != null ) {
			params.add( Pairs.newPair( "end_time", ( end.getTime() / 1000 ) ) );
		}

		if ( limit != null ) {
			params.add( Pairs.newPair( "limit", limit ) );
		}

		if ( !StringUtils.isEmpty( filterKey ) ) {
			params.add( Pairs.newPair( "filter_key", filterKey ) );
		}

		// A JSON-encoded array in which you can specify one or more of 'albums', 'profiles', and 'photo_tags'
		JSONArray jsonMetadata = new JSONArray();
		if ( metadata != null && !metadata.isEmpty() ) {
			for ( String key : metadata ) {
				jsonMetadata.put( key );
			}
		}

		// associate to param
		if ( jsonMetadata.length() > 0 ) {
			params.add( Pairs.newPair( "metadata", jsonMetadata ) );
		}

		return callMethod( FacebookMethod.STREAM_GET, params );
	}

	public String stream_publish( final String message, final Attachment attachment, final Collection<BundleActionLink> actionLinks, final Long targetId,
			final Long userId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();

		if ( isDesktop() ) {
			params.add( Pairs.newPair( "session_key", getCacheSessionKey() ) );
		} else {
			if ( userId != null ) {
				params.add( Pairs.newPair( "uid", userId ) );
			}
		}

		Pairs.addParamIfNotBlank( "message", message, params );

		// A JSON-encoded object containing the text of the post, relevant links, a media type (image, video, mp3, flash), as well as any other key/value pairs you may
		// want to add.
		Pairs.addParamJsonIfNotBlank( "attachment", attachment, params );

		// An array of action link objects, containing the link text and a hyperlink.
		JSONArray jsonActionLinks = BundleActionLink.toJsonArray( actionLinks );
		Pairs.addParamIfNotBlank( "action_links", jsonActionLinks, params );

		Pairs.addParamIfNotBlank( "target_id", targetId, params );
		return extractString( callMethod( FacebookMethod.STREAM_PUBLISH, params ) );
	}

	public boolean stream_remove( final String postId, final Long userId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( isDesktop() ) {
			params.add( Pairs.newPair( "session_key", getCacheSessionKey() ) );
		} else {
			if ( userId != null ) {
				params.add( Pairs.newPair( "uid", userId ) );
			}
		}
		Pairs.addParamIfNotBlank( "post_id", postId, params );
		return extractBoolean( callMethod( FacebookMethod.STREAM_REMOVE, params ) );
	}

	public Object stream_getComments( final String postId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		Pairs.addParamIfNotBlank( "post_id", postId, params );
		return callMethod( FacebookMethod.STREAM_GET_COMMENTS, params );
	}

	public String stream_addComment( final String postId, final String comment, final Long userId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		Pairs.addParamIfNotBlank( "post_id", postId, params );
		Pairs.addParamIfNotBlank( "comment", comment, params );
		Pairs.addParamIfNotBlank( "uid", userId, params );
		return extractString( callMethod( FacebookMethod.STREAM_ADD_COMMENT, params ) );
	}

	public boolean stream_removeComment( final String commentId, final Long userId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( isDesktop() ) {
			params.add( Pairs.newPair( "session_key", getCacheSessionKey() ) );
		} else {
			if ( userId != null ) {
				params.add( Pairs.newPair( "uid", userId ) );
			}
		}
		Pairs.addParamIfNotBlank( "comment_id", commentId, params );
		return extractBoolean( callMethod( FacebookMethod.STREAM_REMOVE_COMMENT, params ) );
	}

	public boolean stream_addLike( final String postId, final Long userId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( isDesktop() ) {
			params.add( Pairs.newPair( "session_key", getCacheSessionKey() ) );
		} else {
			if ( userId != null ) {
				params.add( Pairs.newPair( "uid", userId ) );
			}
		}
		Pairs.addParamIfNotBlank( "post_id", postId, params );
		return extractBoolean( callMethod( FacebookMethod.STREAM_ADD_LIKE, params ) );
	}

	public boolean stream_removeLike( final String postId, final Long userId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( isDesktop() ) {
			params.add( Pairs.newPair( "session_key", getCacheSessionKey() ) );
		} else {
			if ( userId != null ) {
				params.add( Pairs.newPair( "uid", userId ) );
			}
		}
		Pairs.addParamIfNotBlank( "post_id", postId, params );
		return extractBoolean( callMethod( FacebookMethod.STREAM_REMOVE_LIKE, params ) );
	}

	public Object stream_getFilters( final Long userId ) throws FacebookException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( isDesktop() ) {
			params.add( Pairs.newPair( "session_key", getCacheSessionKey() ) );
		} else {
			if ( userId != null ) {
				params.add( Pairs.newPair( "uid", userId ) );
			}
		}
		return callMethod( FacebookMethod.STREAM_GET_FILTERS, params );
	}

	// ========== HELPERS ==========

	protected static String toString( CharSequence cs ) {
		return BasicClientHelper.toString( cs );
	}

	/**
	 * Extracts a Boolean from a result that consists of a Boolean only.
	 * 
	 * @param result
	 * @return the Boolean
	 */
	protected boolean extractBoolean( String result ) throws FacebookException {
		if ( "json".equals( responseFormat ) ) {
			Object out = JsonHelper.parseCallResult( result );
			if ( out instanceof Boolean ) {
				return (Boolean) out;
			}
			return Boolean.parseBoolean( String.valueOf( out ) );
		} else {
			return XmlHelper.extractBoolean( XmlHelper.parseCallResult( result, factory ) );
		}
	}

	protected boolean extractBoolean10( String result ) throws FacebookException {
		return extractInt( result ) == 1;
	}

	/**
	 * Extracts an Long from a result that consists of an Long only.
	 * 
	 * @param result
	 * @return the Long
	 */
	protected int extractInt( String result ) throws FacebookException {
		if ( "json".equals( responseFormat ) ) {
			return ( (Number) JsonHelper.parseCallResult( result ) ).intValue();
		} else {
			return XmlHelper.extractInt( XmlHelper.parseCallResult( result, factory ) );
		}
	}

	/**
	 * Extracts an Long from a result that consists of a Long only.
	 * 
	 * @param result
	 * @return the Long
	 */
	protected long extractLong( String result ) throws FacebookException {
		if ( "json".equals( responseFormat ) ) {
			return ( (Number) JsonHelper.parseCallResult( result ) ).longValue();
		} else {
			return XmlHelper.extractLong( XmlHelper.parseCallResult( result, factory ) );
		}
	}


	/**
	 * Extracts a String from a T consisting entirely of a String.
	 * 
	 * @param result
	 * @return the String
	 */
	protected String extractString( String result ) throws FacebookException {
		if ( "json".equals( responseFormat ) ) {
			return String.valueOf( JsonHelper.parseCallResult( result ) );
		} else {
			return XmlHelper.extractString( XmlHelper.parseCallResult( result, factory ) );
		}
	}

	// ========== EVENTS ==========

	public Object events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime ) throws FacebookException {
		return events_get( userId, eventIds, startTime, endTime, null );
	}

	public Object events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime, String rsvp_status ) throws FacebookException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 4 );
		Pairs.addParamIfNotBlankZero( "uid", userId, params );
		Pairs.addParamDelimitIfNotBlankEmpty( "eids", eventIds, params );
		Pairs.addParamIfNotBlankZero( "start_time", startTime, params );
		Pairs.addParamIfNotBlankZero( "end_time", endTime, params );
		return callMethod( FacebookMethod.EVENTS_GET, params );
	}

	public Object events_getMembers( Long eventId ) throws FacebookException {
		return callMethod( FacebookMethod.EVENTS_GET_MEMBERS, Pairs.newPair( "eid", eventId ) );
	}

	public Long events_create( Map<String,String> event_info ) throws FacebookException {
		return extractLong( callMethod( FacebookMethod.EVENTS_GET_CREATE, Pairs.newPair( "event_info", new JSONObject( event_info ) ) ) );
	}

	public boolean events_edit( Long eid, Map<String,String> event_info ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.EVENTS_GET_EDIT, Pairs.newPair( "eid", eid ), Pairs.newPair( "event_info", new JSONObject( event_info ) ) ) );
	}

	public boolean events_cancel( Long eid, String cancel_message ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.EVENTS_GET_CANCEL, Pairs.newPair( "eid", eid ), Pairs.newPair( "cancel_message", cancel_message ) ) );
	}

	public boolean events_rsvp( Long eid, String rsvp_status ) throws FacebookException {
		return extractBoolean( callMethod( FacebookMethod.EVENTS_GET_RSVP, Pairs.newPair( "eid", eid ), Pairs.newPair( "rsvp_status", rsvp_status ) ) );
	}


	// ========== MOBILE ==========

	public boolean sms_canSend() throws FacebookException {
		return sms_canSend( users_getLoggedInUser() );
	}

	public boolean sms_canSend( Long userId ) throws FacebookException {
		int out = extractInt( callMethod( FacebookMethod.SMS_CAN_SEND, Pairs.newPair( "uid", userId ) ) );
		return out == 0;
	}

	public Integer sms_send( String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException {
		return sms_send( users_getLoggedInUser(), message, smsSessionId, makeNewSession );
	}

	public Integer sms_send( Long userId, String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException {
		if ( smsSessionId != null && smsSessionId != 0 ) {
			return extractInt( callMethod( FacebookMethod.SMS_SEND_MESSAGE, Pairs.newPair( "uid", userId ), Pairs.newPair( "message", message ), Pairs.newPair(
					"session_id", smsSessionId ), Pairs.newPair10( "req_session", makeNewSession ) ) );
		}
		return extractInt( callMethod( FacebookMethod.SMS_SEND_MESSAGE, Pairs.newPair( "uid", userId ), Pairs.newPair( "message", message ), Pairs.newPair10(
				"req_session", makeNewSession ) ) );
	}

	public void sms_sendMessage( Long userId, CharSequence message ) throws FacebookException {
		sms_send( userId, message.toString(), null, false );
	}

	public int sms_sendMessageWithSession( Long userId, CharSequence message ) throws FacebookException {
		return sms_send( userId, message.toString(), null, true );
	}


	// ========== CONNECT ==========

	public Object connect_registerUsers( Collection<Map<String,String>> accounts ) throws FacebookException {
		return callMethod( FacebookMethod.CONNECT_REGISTER_USERS, Pairs.newPair( "accounts", JsonHelper.toJsonListOfMaps( accounts ) ) );
	}

	public Object connect_unregisterUsers( Collection<String> email_hashes ) throws FacebookException {
		return callMethod( FacebookMethod.CONNECT_UNREGISTER_USERS, Pairs.newPair( "email_hashes", JsonHelper.toJsonListOfStrings( email_hashes ) ) );
	}

	public int connect_getUnconnectedFriendsCount() throws FacebookException {
		return extractInt( callMethod( FacebookMethod.CONNECT_GET_UNCONNECTED_FRIENDS_COUNT ) );
	}

	// ========== PHOTOS ==========

	public Object photos_get( Collection<String> photoIds ) throws FacebookException {
		return photos_get( null /* subjId */, null /* albumId */, photoIds );
	}

	public Object photos_get( Long subjId, String albumId ) throws FacebookException {
		return photos_get( subjId, albumId, null /* photoIds */);
	}

	public Object photos_get( Long subjId, Collection<String> photoIds ) throws FacebookException {
		return photos_get( subjId, null /* albumId */, photoIds );
	}

	public Object photos_get( Long subjId ) throws FacebookException {
		return photos_get( subjId, null /* albumId */, null /* photoIds */);
	}

	public Object photos_get( Long subjId, String albumId, Collection<String> photoIds ) throws FacebookException {
		boolean hasUserId = null != subjId && 0 != subjId;
		boolean hasAlbumId = albumId != null;
		boolean hasPhotoIds = null != photoIds && !photoIds.isEmpty();
		if ( !hasUserId && !hasAlbumId && !hasPhotoIds ) {
			throw new IllegalArgumentException( "At least one of photoIds, albumId, or subjId must be provided" );
		}
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 3 );
		if ( hasUserId ) {
			params.add( Pairs.newPair( "subj_id", subjId ) );
		}
		if ( hasAlbumId ) {
			params.add( Pairs.newPair( "aid", albumId ) );
		}
		if ( hasPhotoIds ) {
			params.add( Pairs.newPair( "pids", BasicClientHelper.delimit( photoIds ) ) );
		}
		return callMethod( FacebookMethod.PHOTOS_GET, params );
	}

	public Object photos_getTags( Collection<String> photoIds ) throws FacebookException {
		return callMethod( FacebookMethod.PHOTOS_GET_TAGS, Pairs.newPair( "pids", BasicClientHelper.delimit( photoIds ) ) );
	}

	public boolean photos_addTag( String photoId, CharSequence tagText, Double xPct, Double yPct ) throws FacebookException {
		return photos_addTag( photoId, xPct, yPct, null, tagText );
	}

	private boolean photos_addTag( String photoId, Double xPct, Double yPct, Long taggedUserId, CharSequence tagText ) throws FacebookException {
		assert ( null != photoId && !photoId.equals( 0 ) );
		assert ( null != taggedUserId || null != tagText );
		assert ( null != xPct && xPct >= 0 && xPct <= 100 );
		assert ( null != yPct && yPct >= 0 && yPct <= 100 );
		Pair<String,CharSequence> tagData;
		if ( taggedUserId != null ) {
			tagData = Pairs.newPair( "tag_uid", taggedUserId );
		} else {
			tagData = Pairs.newPair( "tag_text", tagText );
		}
		String d = callMethod( FacebookMethod.PHOTOS_ADD_TAG, Pairs.newPair( "pid", photoId ), tagData, Pairs.newPair( "x", xPct ), Pairs.newPair( "y", yPct ) );
		return extractBoolean( d );
	}

	public Object photos_createAlbum( String albumName ) throws FacebookException {
		return photos_createAlbum( albumName, null /* description */, null /* location */);
	}

	public boolean photos_addTag( String photoId, Long taggedUserId, Double xPct, Double yPct ) throws FacebookException {
		return photos_addTag( photoId, xPct, yPct, taggedUserId, null );
	}

	public Object photos_addTags( String photoId, Collection<PhotoTag> tags ) throws FacebookException {
		assert ( photoId != null );
		assert ( null != tags && !tags.isEmpty() );

		JSONArray jsonTags = new JSONArray();
		for ( PhotoTag tag : tags ) {
			jsonTags.put( tag.jsonify() );
		}

		return callMethod( FacebookMethod.PHOTOS_ADD_TAG, Pairs.newPair( "pid", photoId ), Pairs.newPair( "tags", jsonTags ) );
	}

	public Object photos_createAlbum( String name, String description, String location ) throws FacebookException {
		assert ( null != name && !"".equals( name ) );
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 3 );
		params.add( Pairs.newPair( "name", name ) );
		if ( null != description ) {
			params.add( Pairs.newPair( "description", description ) );
		}
		if ( null != location ) {
			params.add( Pairs.newPair( "location", location ) );
		}
		return callMethod( FacebookMethod.PHOTOS_CREATE_ALBUM, params );
	}

	public Object photos_getAlbums( Collection<String> albumIds ) throws FacebookException {
		return photos_getAlbums( null /* userId */, albumIds );
	}

	public Object photos_getAlbums( Long userId ) throws FacebookException {
		return photos_getAlbums( userId, null /* albumIds */);
	}

	public Object photos_getAlbums( Long userId, Collection<String> albumIds ) throws FacebookException {
		boolean hasUserId = null != userId && userId != 0;
		boolean hasAlbumIds = null != albumIds && !albumIds.isEmpty();
		if ( hasUserId && hasAlbumIds ) {
			return callMethod( FacebookMethod.PHOTOS_GET_ALBUMS, Pairs.newPair( "uid", userId ), Pairs.newPair( "aids", BasicClientHelper.delimit( albumIds ) ) );
		}
		if ( hasUserId ) {
			return callMethod( FacebookMethod.PHOTOS_GET_ALBUMS, Pairs.newPair( "uid", userId ) );
		}
		if ( hasAlbumIds ) {
			return callMethod( FacebookMethod.PHOTOS_GET_ALBUMS, Pairs.newPair( "aids", BasicClientHelper.delimit( albumIds ) ) );
		}
		throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "Atleast one of userId or albumIds is required." );
	}

	public Object photos_getByAlbum( String albumId, Collection<String> photoIds ) throws FacebookException {
		return photos_get( null /* subjId */, albumId, photoIds );
	}

	public Object photos_getByAlbum( String albumId ) throws FacebookException {
		return photos_get( null /* subjId */, albumId, null /* photoIds */);
	}

	private boolean photos_addTag( String photoId, Double xPct, Double yPct, Long taggedUserId, CharSequence tagText, Long userId ) throws FacebookException {
		assert ( null != photoId && !photoId.equals( 0 ) );
		assert ( null != taggedUserId || null != tagText );
		assert ( null != xPct && xPct >= 0 && xPct <= 100 );
		assert ( null != yPct && yPct >= 0 && yPct <= 100 );
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 5 );
		if ( taggedUserId != null ) {
			params.add( Pairs.newPair( "tag_uid", taggedUserId ) );
		} else {
			params.add( Pairs.newPair( "tag_text", tagText ) );
		}
		params.add( Pairs.newPair( "x", xPct ) );
		params.add( Pairs.newPair( "y", yPct ) );
		params.add( Pairs.newPair( "pid", photoId ) );
		params.add( Pairs.newPair( "owner_uid", userId ) );
		return extractBoolean( callMethod( FacebookMethod.PHOTOS_ADD_TAG_NOSESSION, params ) );
	}

	public boolean photos_addTag( String photoId, Long taggedUserId, Double pct, Double pct2, Long userId ) throws FacebookException {
		return photos_addTag( photoId, pct, pct2, taggedUserId, null, userId );
	}

	public boolean photos_addTag( String photoId, CharSequence tagText, Double pct, Double pct2, Long userId ) throws FacebookException {
		return photos_addTag( photoId, pct, pct2, null, tagText );
	}

	public Object photos_createAlbum( String albumName, Long userId ) throws FacebookException {
		return photos_createAlbum( albumName, null, null, userId );
	}

	public Object photos_createAlbum( String name, String description, String location, Long userId ) throws FacebookException {
		assert ( null != name && !"".equals( name ) );
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 4 );
		params.add( Pairs.newPair( "name", name ) );
		if ( null != description ) {
			params.add( Pairs.newPair( "description", description ) );
		}
		if ( null != location ) {
			params.add( Pairs.newPair( "location", location ) );
		}
		params.add( Pairs.newPair( "uid", userId ) );
		return callMethod( FacebookMethod.PHOTOS_CREATE_ALBUM_NOSESSION, params );
	}

	public Object photos_addTags( String photoId, Collection<PhotoTag> tags, Long userId ) throws FacebookException {
		assert ( photoId != null );
		assert ( null != tags && !tags.isEmpty() );
		String tagStr = null;
		try {
			JSONArray jsonTags = new JSONArray();
			for ( PhotoTag tag : tags ) {
				jsonTags.put( tag.jsonify() );
			}
			tagStr = jsonTags.toString();
		}
		catch ( Exception ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
		return callMethod( FacebookMethod.PHOTOS_ADD_TAG_NOSESSION, Pairs.newPair( "pid", photoId ), Pairs.newPair( "tags", tagStr ), Pairs.newPair( "uid", userId ) );
	}

	public Object photos_upload( File photo ) throws FacebookException {
		return photos_upload( photo, null /* caption */, null /* albumId */);
	}

	public Object photos_uploadWithCaption( File photo, String caption ) throws FacebookException {
		return photos_upload( photo, caption, null /* albumId */);
	}

	public Object photos_uploadToAlbum( File photo, String albumId ) throws FacebookException {
		return photos_upload( photo, null /* caption */, albumId );
	}

	public Object photos_upload( File photo, String caption, String albumId ) throws FacebookException {
		return photos_upload( null, photo, caption, albumId );
	}

	public Object photos_upload( Long userId, File photo ) throws FacebookException {
		return photos_upload( userId, photo, null, null );
	}

	public Object photos_uploadWithCaption( Long userId, File photo, String caption ) throws FacebookException {
		return photos_upload( userId, photo, caption, null );
	}

	public Object photos_uploadToAlbum( Long userId, File photo, String albumId ) throws FacebookException {
		return photos_upload( userId, photo, null, albumId );
	}

	public Object photos_upload( Long userId, File photo, String caption, String albumId ) throws FacebookException {
		try {
			FileInputStream fileInputStream = new FileInputStream( photo );
			BufferedInputStream fileStream = new BufferedInputStream( fileInputStream );
			try {
				return photos_upload( userId, caption, albumId, photo.getName(), fileStream );
			}
			finally {
				BasicClientHelper.close( fileStream );
				BasicClientHelper.close( fileInputStream );
			}
		}
		catch ( IOException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	public Object photos_upload( Long userId, String caption, String albumId, String fileName, InputStream fileStream ) throws FacebookException {
		if ( fileStream == null ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "fileStream specified was null. fileName was specified as " + fileName );
		}
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 3 );
		Pairs.addParamIfNotBlank( "aid", albumId, params );
		Pairs.addParamIfNotBlank( "caption", caption, params );
		boolean uid = Pairs.addParamIfNotBlankZero( "uid", userId, params );
		FacebookMethod method = uid ? FacebookMethod.PHOTOS_UPLOAD_NOSESSION : FacebookMethod.PHOTOS_UPLOAD;
		return callMethod( method, params, fileName, fileStream );
	}

	public Object payments_getOrders( String status, long startTime, long endTime ) throws FacebookException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 3 );
		Pairs.addParam( "status", status, params );
		Pairs.addParam( "start_time", startTime, params );
		Pairs.addParam( "end_time", endTime, params );
		Pairs.addParam( "test_mode", "1", params );
		return callMethod( FacebookMethod.PAYMENTS_GET_ORDERS, params );
	}

	// ========== SEND NOTIFICATIONS ==========

	@Deprecated
	public Object notifications_get() throws FacebookException {
		return callMethod( FacebookMethod.NOTIFICATIONS_GET );
	}

	@Deprecated
	public URL notifications_send( Collection<Long> recipientIds, CharSequence notification, CharSequence email ) throws FacebookException {
		notifications_send( recipientIds, notification );
		return null;
	}

	@Deprecated
	public Collection<String> notifications_send( Collection<Long> recipientIds, CharSequence notification ) throws FacebookException {
		return notifications_send( recipientIds, notification.toString(), false );
	}

	@Deprecated
	public Collection<String> notifications_send( CharSequence notification ) throws FacebookException {
		return notifications_send( Arrays.asList( users_getLoggedInUser() ), notification );
	}

	@Deprecated
	public Collection<String> notifications_send( Collection<Long> recipientIds, String notification, boolean isAppToUser ) throws FacebookException {
		FacebookMethod method = FacebookMethod.NOTIFICATIONS_SEND;
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 3 );
		Pairs.addParam( "type", ( isAppToUser ? "app_to_user" : "user_to_user" ), params );
		Pairs.addParam( "notification", notification, params );
		Pairs.addParamDelimitIfNotBlankEmpty( "to_ids", recipientIds, params );
		String outString = extractString( callMethod( method, params ) );
		if ( outString.trim().length() == 0 ) {
			return Collections.emptySet();
		}
		return new TreeSet( Arrays.asList( outString.split( "," ) ) );
	}

	// ========== SEND EMAIL ==========

	public Collection<String> notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence text, CharSequence fbml ) throws FacebookException {
		// this method requires a session only if we're dealing with a desktop app
		FacebookMethod method = isDesktop() ? FacebookMethod.NOTIFICATIONS_SEND_EMAIL_SESSION : FacebookMethod.NOTIFICATIONS_SEND_EMAIL_NOSESSION;
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 4 );
		Pairs.addParam( "recipients", BasicClientHelper.delimit( recipients ), params );
		Pairs.addParam( "subject", subject, params );
		Pairs.addParamIfNotBlank( "text", text, params );
		Pairs.addParamIfNotBlank( "fbml", fbml, params );
		String outString = extractString( callMethod( method, params ) );
		if ( outString.trim().length() == 0 ) {
			return Collections.emptySet();
		}
		return new TreeSet( Arrays.asList( outString.split( "," ) ) );
	}

	public Collection<String> notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException {
		return notifications_sendEmail( recipients, subject, null, fbml );
	}

	public Collection<String> notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException {
		return notifications_sendEmail( recipients, subject, email, null );
	}

	public Collection<String> notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException {
		return notifications_sendEmail( Arrays.asList( users_getLoggedInUser() ), subject, email, fbml );
	}

	public Collection<String> notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException {
		return notifications_sendEmailToCurrentUser( subject, null, fbml );
	}

	public Collection<String> notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException {
		return notifications_sendEmailToCurrentUser( subject, email, null );
	}

	@Deprecated
	public String notifications_sendEmailStr( Collection<Long> recipients, CharSequence subject, CharSequence fbml, CharSequence text ) throws FacebookException {
		return BasicClientHelper.delimit( notifications_sendEmail( recipients, subject, text, fbml ) ).toString();
	}

	@Deprecated
	public String notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence fbml ) throws FacebookException {
		return notifications_sendEmailStr( recipients, subject, fbml, null );
	}

	@Deprecated
	public String notifications_sendEmailPlain( Collection<Long> recipients, CharSequence subject, CharSequence text ) throws FacebookException {
		return notifications_sendEmailStr( recipients, subject, null, text );
	}

	public String admin_getAppPropertiesAsString( Collection<ApplicationProperty> properties ) throws FacebookException {
		JSONArray props = new JSONArray();
		for ( ApplicationProperty property : properties ) {
			props.put( property.getName() );
		}
		String rawResponse = callMethod( FacebookMethod.ADMIN_GET_APP_PROPERTIES, Pairs.newPair( "properties", props ) );
		if ( log.isDebugEnabled() ) {
			log.debug( "Facebook response: " + rawResponse );
		}
		if ( "json".equals( getResponseFormat() ) ) {
			return JsonHelper.parseCallResult( rawResponse ).toString();
		} else {
			return XmlHelper.extractString( XmlHelper.parseCallResult( rawResponse, factory ) );
		}
	}

	/**
	 * Returns a list of String raw responses which will be further broken down by the adapters into the actual individual responses. One string is returned per 20
	 * methods in the batch.
	 */
	public List<String> executeBatch( boolean serial ) throws FacebookException {
		int BATCH_LIMIT = 20;
		this.batchMode = false;
		List<String> result = new ArrayList<String>();
		List<BatchQuery> buffer = new ArrayList<BatchQuery>();

		while ( !this.queries.isEmpty() ) {
			buffer.add( this.queries.remove( 0 ) );
			if ( ( buffer.size() == BATCH_LIMIT ) || ( this.queries.isEmpty() ) ) {
				// we can only actually batch up to 20 at once

				String batchRawResponse = batch_run( encodeMethods( buffer ), serial );
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

	// CUSTOM TAGS

	public void fbml_deleteCustomTags( Collection<String> names ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.FBML_DELETE_CUSTOM_TAGS, Pairs.newPair( "names", JsonHelper.toJsonListOfStrings( names ) ) ) );
	}

	public Object fbml_getCustomTags( String appId ) throws FacebookException {
		if ( StringUtils.isBlank( appId ) ) {
			return callMethod( FacebookMethod.FBML_GET_CUSTOM_TAGS );
		} else {
			return callMethod( FacebookMethod.FBML_GET_CUSTOM_TAGS, Pairs.newPair( "app_id", appId ) );
		}
	}

	public void fbml_registerCustomTags( Collection<JSONObject> tags ) throws FacebookException {
		validateVoidResponse( callMethod( FacebookMethod.FBML_REGISTER_CUSTOM_TAGS, Pairs.newPair( "tags", new JSONArray( tags ) ) ) );
	}

	// ========== INTL ==========

	public int intl_uploadNativeStrings( Map<String,String> native_strings ) throws FacebookException {
		JSONArray array = new JSONArray();
		try {
			for ( Entry<String,String> entry : native_strings.entrySet() ) {
				JSONObject obj = new JSONObject();
				obj.put( "text", entry.getKey() );
				obj.put( "description", entry.getValue() );
				array.put( obj );
			}
		}
		catch ( JSONException ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
		return extractInt( callMethod( FacebookMethod.INTL_UPLOAD_NATIVE_STRINGS, Pairs.newPair( "native_strings", array ) ) );
	}

	public Object dashboard_multiAddNews( Collection<Long> userIds, Collection<DashboardNewsItem> newsItems ) throws FacebookException {
		return dashboard_multiAddNews( userIds, newsItems, null );
	}

	public Object dashboard_multiAddNews( Collection<Long> userIds, Collection<DashboardNewsItem> newsItems, String imageUrl ) throws FacebookException {
		// validation
		if ( userIds == null || userIds.isEmpty() || newsItems == null || newsItems.isEmpty() ) {
			return null;
		}

		validateNewsItemsForDashboard( newsItems, imageUrl );

		// build parameters
		JSONArray idsJSONArray = new JSONArray( userIds );

		JSONArray newsJSONArray = buildDashboardNewsItemJSONArray( newsItems );

		Pair userIdsParameter = Pairs.newPair( "uids", idsJSONArray );
		Pair newsParameter = Pairs.newPair( "news", newsJSONArray );
		Pair imageUrlParameter = null;
		if ( imageUrl != null ) {
			imageUrlParameter = Pairs.newPair( "image", imageUrl );
		}

		Collection<Pair<String,CharSequence>> parameters = new ArrayList<Pair<String,CharSequence>>();
		parameters.add( userIdsParameter );
		parameters.add( newsParameter );
		if ( imageUrlParameter != null ) {
			parameters.add( imageUrlParameter );
		}

		// invoke API call
		return callMethod( FacebookMethod.DASHBOARD_MULTI_ADD_NEWS, parameters );
	}

	public Object dashboard_multiClearNews( Collection<Long> userIds ) throws FacebookException {
		Map<Long,Collection<Long>> parameterMap = new HashMap<Long,Collection<Long>>();
		for ( Long userId : userIds ) {
			parameterMap.put( userId, Collections.EMPTY_LIST );
		}
		return dashboard_multiClearNews( parameterMap );
	}

	public Object dashboard_multiClearNews( Map<Long,Collection<Long>> userIdToNewsIdMap ) throws FacebookException {
		if ( userIdToNewsIdMap == null || userIdToNewsIdMap.size() == 0 ) {
			return null;
		}

		// build parameter
		JSONObject parameterJSON = new JSONObject();
		for ( Entry<Long,Collection<Long>> entry : userIdToNewsIdMap.entrySet() ) {

			JSONArray newsIdArray = new JSONArray( entry.getValue() );
			try {
				parameterJSON.put( entry.getKey().toString(), newsIdArray );
			}
			catch ( JSONException exception ) {
				BasicClientHelper.runtimeException( exception );
			}
		}

		Pair singleParameter = Pairs.newPair( "ids", parameterJSON );

		// invoke API call
		return callMethod( FacebookMethod.DASHBOARD_MULTI_CLEAR_NEWS, singleParameter );
	}

	public Long dashboard_addGlobalNews( Collection<DashboardNewsItem> newsItems ) throws FacebookException {
		return dashboard_addGlobalNews( newsItems, null );
	}

	public Long dashboard_addGlobalNews( Collection<DashboardNewsItem> newsItems, String imageUrl ) throws FacebookException {
		// validation
		validateNewsItemsForDashboard( newsItems, imageUrl );

		JSONArray newsJSONArray = buildDashboardNewsItemJSONArray( newsItems );

		Pair newsParameter = Pairs.newPair( "news", newsJSONArray );
		Pair imageUrlParameter = null;
		if ( imageUrl != null ) {
			imageUrlParameter = Pairs.newPair( "image", imageUrl );
		}

		Collection<Pair<String,CharSequence>> parameters = new ArrayList<Pair<String,CharSequence>>();
		parameters.add( newsParameter );
		if ( imageUrlParameter != null ) {
			parameters.add( imageUrlParameter );
		}

		// invoke API call
		long response = extractLong( callMethod( FacebookMethod.DASHBOARD_ADD_GLOBAL_NEWS, parameters ) );

		return response == 0 ? null : response;
	}

	public Long dashboard_publishActivity( DashboardActivityItem activityItem ) throws FacebookException {
		return dashboard_publishActivity( activityItem, null );
	}

	public Long dashboard_publishActivity( DashboardActivityItem activityItem, String imageUrl ) throws FacebookException {
		// validation
		validateDashboardItem( activityItem );
		validateImageUrl( imageUrl );

		// build parameters
		Pair newsParameter = Pairs.newPair( "activity", activityItem.toJSON() );
		Pair imageUrlParameter = null;
		if ( imageUrl != null ) {
			imageUrlParameter = Pairs.newPair( "image", imageUrl );
		}

		Collection<Pair<String,CharSequence>> parameters = new ArrayList<Pair<String,CharSequence>>();
		parameters.add( newsParameter );
		if ( imageUrlParameter != null ) {
			parameters.add( imageUrlParameter );
		}

		// invoke API call
		long response = extractLong( callMethod( FacebookMethod.DASHBOARD_PUBLISH_ACTIVITY, parameters ) );

		return response == 0 ? null : response;
	}

	public Set<Long> dashboard_multiIncrementCount( Collection<Long> userIds ) throws FacebookException {
		// validation
		if ( userIds == null || userIds.isEmpty() ) {
			return Collections.EMPTY_SET;
		}

		// build parameters
		JSONArray idsJSONArray = new JSONArray( userIds );

		// invoke API call
		callMethod( FacebookMethod.DASHBOARD_MULTI_INCREMENT_COUNT, Pairs.newPair( "uids", idsJSONArray ) );

		/*
		 * FIXME: Facebook bug report against return values for this call. FB BUG REPORT: http://bugs.developers.facebook.com/show_bug.cgi?id=8557
		 * 
		 * For now, assuming all ids were successfully processed.
		 */
		return new HashSet<Long>( userIds );
	}

	public boolean dashboard_clearGlobalNews() throws FacebookException {
		return dashboard_clearGlobalNews( null );
	}

	public boolean dashboard_clearGlobalNews( Collection<Long> newsIds ) throws FacebookException {
		if ( newsIds != null && !newsIds.isEmpty() ) {
			JSONArray newsIdsJSONArray = new JSONArray( newsIds );
			return extractBoolean( callMethod( FacebookMethod.DASHBOARD_CLEAR_GLOBAL_NEWS, Pairs.newPair( "news_ids", newsIdsJSONArray ) ) );
		} else {
			return extractBoolean( callMethod( FacebookMethod.DASHBOARD_CLEAR_GLOBAL_NEWS ) );
		}
	}

	/**
	 * Builds a JSONArray consisting of the specified DashboardNewsItem instances.
	 */
	private JSONArray buildDashboardNewsItemJSONArray( Collection<DashboardNewsItem> newsItems ) {
		JSONArray newsJSONArray = new JSONArray();
		for ( DashboardNewsItem newsItem : newsItems ) {
			newsJSONArray.put( newsItem.toJSON() );
		}
		return newsJSONArray;
	}

	/**
	 * Builds a JSONArray consisting of the specified DashboardActivityItem instances.
	 */
	private JSONArray buildDashboardActivityItemJSONArray( Collection<DashboardActivityItem> activityItems ) {
		JSONArray activityJSONArray = new JSONArray();
		for ( DashboardActivityItem activityItem : activityItems ) {
			activityJSONArray.put( activityItem.toJSON() );
		}
		return activityJSONArray;
	}

	/**
	 * Dashboard-specific news items validation logic.
	 */
	private void validateNewsItemsForDashboard( Collection<DashboardNewsItem> newsItems, String imageUrl ) throws FacebookException {
		if ( newsItems.size() > MAX_DASHBOARD_NEW_ITEMS ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "Exceeded maximum of " + MAX_DASHBOARD_NEW_ITEMS + " news items allowed by API." );
		}
		for ( DashboardNewsItem newsItem : newsItems ) {
			validateDashboardItem( newsItem );
		}
		validateImageUrl( imageUrl );
	}

	/**
	 * Validates image URL for dashboard if defined.
	 * 
	 * @param imageUrl
	 *            Url for image appearing alongside dashboard items.
	 * @throws FacebookException
	 */
	private void validateImageUrl( String imageUrl ) throws FacebookException {
		if ( imageUrl != null && "".equals( imageUrl.trim() ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "Image url cannot be empty when specified." );
		}
	}

	/**
	 * Validates a single DashboardItem object for presence of mandatory and optional attributes.
	 * 
	 * @param item
	 *            DashboardItem object to validate.
	 */
	private void validateDashboardItem( DashboardItem item ) throws FacebookException {
		if ( item.getMessage() == null || "".equals( item.getMessage().trim() ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "Message parameter is mandatory in DashboardItem." );
		}
		BundleActionLink actionLink = item.getActionLink();
		if ( actionLink != null ) {
			if ( actionLink.getHref() == null || "".equals( actionLink.getHref().trim() ) ) {
				throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "ActionLink 'href' cannot be empty." );
			}
			if ( actionLink.getText() == null || "".equals( actionLink.getText().trim() ) ) {
				throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "ActionLink 'text' cannot be empty." );
			}
		}
	}

}
