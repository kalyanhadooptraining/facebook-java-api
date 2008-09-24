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

package com.facebook.api;

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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Base class for interacting with the Facebook Application Programming Interface (API). Most Facebook API methods map directly to function calls of this class. <br/>
 * Instances of FacebookRestClient should be initialized via calls to {@link #auth_createToken}, followed by {@link #auth_getSession}. <br/> For continually updated
 * documentation, please refer to the <a href="http://wiki.developers.facebook.com/index.php/API"> Developer Wiki</a>.
 */
public abstract class ExtensibleClient<T> implements IFacebookRestClient<T> {

	public static URL SERVER_URL = null;
	public static URL HTTPS_SERVER_URL = null;
	protected static JAXBContext JAXB_CONTEXT;
	static {
		try {
			JAXB_CONTEXT = JAXBContext.newInstance( "com.facebook.api.schema" );
			SERVER_URL = new URL( SERVER_ADDR );
			HTTPS_SERVER_URL = new URL( HTTPS_SERVER_ADDR );
		}
		catch ( MalformedURLException e ) {
			System.err.println( "MalformedURLException: " + e.getMessage() );
			System.exit( 1 );
		}
		catch ( JAXBException e ) {
			JAXB_CONTEXT = null;
			System.err.println( "Could not get JAXB context:  " + e.getMessage() );
			e.printStackTrace();
		}
	}

	protected final String _secret;
	protected final String _apiKey;
	protected URL _serverUrl;
	protected String rawResponse;
	protected Long _expires;
	protected int _timeout;
	protected int _readTimeout;
	protected boolean batchMode;
	protected List<BatchQuery> queries;

	protected String _sessionKey;
	protected boolean _isDesktop = false;
	protected long _userId = -1;
	protected String permissionsApiKey = null;

	/**
	 * filled in when session is established only used for desktop apps
	 */
	protected String _sessionSecret;

	/**
	 * The number of parameters required for every request.
	 * 
	 * @see #callMethod(IFacebookMethod,Collection)
	 */
	public static final int NUM_AUTOAPPENDED_PARAMS = 6;

	protected static boolean DEBUG = false;
	protected Boolean _debug = null;

	protected File _uploadFile = null;
	protected static final String CRLF = "\r\n";
	protected static final String PREF = "--";
	protected static final int UPLOAD_BUFFER_SIZE = 512;

	public static final String MARKETPLACE_STATUS_DEFAULT = "DEFAULT";
	public static final String MARKETPLACE_STATUS_NOT_SUCCESS = "NOT_SUCCESS";
	public static final String MARKETPLACE_STATUS_SUCCESS = "SUCCESS";

	protected ExtensibleClient( String apiKey, String secret ) {
		this( SERVER_URL, apiKey, secret, null );
	}

	protected ExtensibleClient( String apiKey, String secret, int timeout ) {
		this( SERVER_URL, apiKey, secret, null, timeout );
	}

	public ExtensibleClient( String apiKey, String secret, String sessionKey ) {
		this( SERVER_URL, apiKey, secret, sessionKey );
	}

	public ExtensibleClient( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( SERVER_URL, apiKey, secret, sessionKey, connectionTimeout );
	}

	public ExtensibleClient( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		this( new URL( serverAddr ), apiKey, secret, sessionKey );
	}

	public ExtensibleClient( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		this( new URL( serverAddr ), apiKey, secret, sessionKey, connectionTimeout );
	}

	protected ExtensibleClient( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		_sessionKey = sessionKey;
		_apiKey = apiKey;
		_secret = secret;
		_serverUrl = ( null != serverUrl ) ? serverUrl : SERVER_URL;
		_timeout = -1;
		_readTimeout = -1;
		batchMode = false;
		queries = new ArrayList<BatchQuery>();
	}

	protected ExtensibleClient( URL serverUrl, String apiKey, String secret, String sessionKey, int timeout ) {
		this( serverUrl, apiKey, secret, sessionKey );
		_timeout = timeout;
	}

	protected ExtensibleClient( URL serverUrl, String apiKey, String secret, String sessionKey, int timeout, int readTimeout ) {
		this( serverUrl, apiKey, secret, sessionKey );
		_timeout = timeout;
		_readTimeout = readTimeout;
	}

	public void beginPermissionsMode( String apiKey ) {
		this.permissionsApiKey = apiKey;
	}

	public void endPermissionsMode() {
		this.permissionsApiKey = null;
	}

	/**
	 * The response format in which results to FacebookMethod calls are returned
	 * 
	 * @return the format: either XML, JSON, or null (API default)
	 */
	public String getResponseFormat() {
		return null;
	}

	/**
	 * Gets the session-token used by Facebook to authenticate a desktop application. If your application does not run in desktop mode, than this field is not relevent to
	 * you.
	 * 
	 * @return the desktop-app session token.
	 */
	public String getSessionSecret() {
		return _sessionSecret;
	}

	public JAXBContext getJaxbContext() {
		return JAXB_CONTEXT;
	}

	public void setJaxbContext( JAXBContext context ) {
		JAXB_CONTEXT = context;
	}

	/**
	 * Allows the session-token to be manually overridden when running a desktop application. If your application does not run in desktop mode, then setting this field
	 * will have no effect. If you set an incorrect value here, your application will probably fail to run.
	 * 
	 * @param key
	 *            the new value to set. Incorrect values may cause your application to fail to run.
	 */
	public void setSessionSecret( String key ) {
		_sessionSecret = key;
	}

	public static void setDebugAll( boolean isDebug ) {
		DEBUG = isDebug;
	}

	private static CharSequence delimit( Iterable iterable ) {
		if ( iterable == null ) {
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

	/**
	 * Retrieves whether two users are friends.
	 * 
	 * @param userId1
	 * @param userId2
	 * @return T
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Friends.areFriends"> Developers Wiki: Friends.areFriends</a>
	 */
	public T friends_areFriends( long userId1, long userId2 ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.FRIENDS_ARE_FRIENDS, newPair( "uids1", userId1 ), newPair( "uids2", userId2 ) );
	}

	/**
	 * Retrieves whether pairs of users are friends. Returns whether the first user in <code>userIds1</code> is friends with the first user in <code>userIds2</code>,
	 * the second user in <code>userIds1</code> is friends with the second user in <code>userIds2</code>, etc.
	 * 
	 * @param userIds1
	 * @param userIds2
	 * @return T
	 * @throws IllegalArgumentException
	 *             if one of the collections is null, or empty, or if the collection sizes differ.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Friends.areFriends"> Developers Wiki: Friends.areFriends</a>
	 */
	public T friends_areFriends( Collection<Long> userIds1, Collection<Long> userIds2 ) throws FacebookException, IOException {
		if ( userIds1 == null || userIds2 == null || userIds1.isEmpty() || userIds2.isEmpty() ) {
			throw new IllegalArgumentException( "Collections passed to friends_areFriends should not be null or empty" );
		}
		if ( userIds1.size() != userIds2.size() ) {
			throw new IllegalArgumentException( String.format( "Collections should be same size: got userIds1: %d elts; userIds2: %d elts", userIds1.size(), userIds2
					.size() ) );
		}
		return callMethod( FacebookMethod.FRIENDS_ARE_FRIENDS, newPair( "uids1", delimit( userIds1 ) ), newPair( "uids2", delimit( userIds2 ) ) );
	}

	/**
	 * Gets the FBML for a user's profile, including the content for both the profile box and the profile actions.
	 * 
	 * @param userId -
	 *            the user whose profile FBML to set
	 * @return a T containing FBML markup
	 */
	public T profile_getFBML( Long userId ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PROFILE_GET_FBML, newPair( "uid", userId ) );
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
	 * Helper function: assembles the parameters used by feed_publishActionOfUser and feed_publishStoryToUser
	 * 
	 * @param feedMethod
	 *            feed_publishStoryToUser / feed_publishActionOfUser
	 * @param title
	 *            title of the story
	 * @param body
	 *            body of the story
	 * @param images
	 *            optional images to be included in he story
	 * @param priority
	 * @return whether the call to <code>feedMethod</code> was successful
	 */
	protected boolean feedHandler( IFacebookMethod feedMethod, CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images,
			Integer priority ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( feedMethod.numParams() );
		params.add( newPair( "title", title ) );
		if ( null != body ) {
			params.add( newPair( "body", body ) );
		}
		if ( null != priority ) {
			params.add( newPair( "priority", priority.toString() ) );
		}
		handleFeedImages( params, images );
		return extractBoolean( callMethod( feedMethod, params ) );
	}

	/**
	 * Adds image parameters
	 * 
	 * @param params
	 * @param images
	 */
	protected void handleFeedImages( List<Pair<String,CharSequence>> params, Collection<? extends IPair<? extends Object,URL>> images ) {
		if ( images != null && images.size() > 4 ) {
			throw new IllegalArgumentException( "At most four images are allowed, got " + Integer.toString( images.size() ) );
		}
		if ( null != images && !images.isEmpty() ) {
			int image_count = 0;
			for ( IPair image : images ) {
				++image_count;
				assert null != image.getFirst() : "Image URL must be provided";
				params.add( newPair( String.format( "image_%d", image_count ), image.getFirst().toString() ) );
				if ( null != image.getSecond() )
					params.add( newPair( String.format( "image_%d_link", image_count ), image.getSecond().toString() ) );
			}
		}
	}

	/**
	 * Publish the notification of an action taken by a user to newsfeed.
	 * 
	 * @param title
	 *            the title of the feed story (up to 60 characters, excluding tags)
	 * @param body
	 *            (optional) the body of the feed story (up to 200 characters, excluding tags)
	 * @param images
	 *            (optional) up to four pairs of image URLs and (possibly null) link URLs
	 * @return whether the story was successfully published; false in case of permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishActionOfUser"> Developers Wiki: Feed.publishActionOfUser</a>
	 */
	public boolean feed_publishActionOfUser( CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images ) throws FacebookException,
			IOException {
		return feedHandler( FacebookMethod.FEED_PUBLISH_ACTION_OF_USER, title, body, images, null );
	}

	/**
	 * Publish the notification of an action taken by a user to newsfeed.
	 * 
	 * @param title
	 *            the title of the feed story (up to 60 characters, excluding tags)
	 * @param body
	 *            (optional) the body of the feed story (up to 200 characters, excluding tags)
	 * @return whether the story was successfully published; false in case of permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishActionOfUser"> Developers Wiki: Feed.publishActionOfUser</a>
	 */
	public boolean feed_publishActionOfUser( CharSequence title, CharSequence body ) throws FacebookException, IOException {
		return feed_publishActionOfUser( title, body, null );
	}

	/**
	 * Call this function to retrieve the session information after your user has logged in.
	 * 
	 * @param authToken
	 *            the token returned by auth_createToken or passed back to your callback_url.
	 */
	public abstract String auth_getSession( String authToken ) throws FacebookException, IOException;

	/**
	 * Publish a story to the logged-in user's newsfeed.
	 * 
	 * @param title
	 *            the title of the feed story
	 * @param body
	 *            the body of the feed story
	 * @return whether the story was successfully published; false in case of permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser"> Developers Wiki: Feed.publishStoryToUser</a>
	 */
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body ) throws FacebookException, IOException {
		return feed_publishStoryToUser( title, body, null, null );
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
	 * Publish a story to the logged-in user's newsfeed.
	 * 
	 * @param title
	 *            the title of the feed story
	 * @param body
	 *            the body of the feed story
	 * @param priority
	 * @return whether the story was successfully published; false in case of permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser"> Developers Wiki: Feed.publishStoryToUser</a>
	 */
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body, Integer priority ) throws FacebookException, IOException {
		return feed_publishStoryToUser( title, body, null, priority );
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
	 * @return whether the story was successfully published; false in case of permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser"> Developers Wiki: Feed.publishStoryToUser</a>
	 */
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images, Integer priority )
			throws FacebookException, IOException {
		return feedHandler( FacebookMethod.FEED_PUBLISH_STORY_TO_USER, title, body, images, priority );
	}

	/**
	 * Publishes a Mini-Feed story describing an action taken by a user, and publishes aggregating News Feed stories to the friends of that user. Stories are identified
	 * as being combinable if they have matching templates and substituted values.
	 * 
	 * @param actorId
	 *            the user into whose mini-feed the story is being published.
	 * @param titleTemplate
	 *            markup (up to 60 chars, tags excluded) for the feed story's title section. Must include the token <code>{actor}</code>.
	 * @return whether the action story was successfully published; false in case of a permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction"> Developers Wiki: Feed.publishTemplatizedAction</a>
	 */
	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate ) throws FacebookException, IOException {
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
	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException, IOException {
		return feed_publishTemplatizedAction( titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images, null );
	}

	/**
	 * @deprecated Use the version that takes a Long for the actorId paramter.
	 */
	@Deprecated
	public boolean feed_publishTemplatizedAction( Integer actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException, IOException {
		return feed_publishTemplatizedAction( (long) ( actorId.intValue() ), titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images );
	}


	/**
	 * Retrieves the membership list of a group
	 * 
	 * @param groupId
	 *            the group id
	 * @return a T containing four membership lists of 'members', 'admins', 'officers', and 'not_replied'
	 */
	public T groups_getMembers( Number groupId ) throws FacebookException, IOException {
		assert ( null != groupId );
		return callMethod( FacebookMethod.GROUPS_GET_MEMBERS, newPair( "gid", groupId.toString() ) );
	}

	private static String encode( CharSequence target ) {
		if ( target == null ) {
			return "";
		}
		String result = target.toString();
		try {
			result = URLEncoder.encode( result, "UTF8" );
		}
		catch ( UnsupportedEncodingException e ) {
			System.err.printf( "Unsuccessful attempt to encode '%s' into UTF8", result );
		}
		return result;
	}

	/**
	 * Retrieves the membership list of an event
	 * 
	 * @param eventId
	 *            event id
	 * @return T consisting of four membership lists corresponding to RSVP status, with keys 'attending', 'unsure', 'declined', and 'not_replied'
	 */
	public T events_getMembers( Number eventId ) throws FacebookException, IOException {
		assert ( null != eventId );
		return callMethod( FacebookMethod.EVENTS_GET_MEMBERS, newPair( "eid", eventId.toString() ) );
	}

	/**
	 * Retrieves the friends of the currently logged in user, who are also users of the calling application.
	 * 
	 * @return array of friends
	 */
	public T friends_getAppUsers() throws FacebookException, IOException {
		return callMethod( FacebookMethod.FRIENDS_GET_APP_USERS );
	}

	/**
	 * Retrieves the results of a Facebook Query Language query
	 * 
	 * @param query :
	 *            the FQL query statement
	 * @return varies depending on the FQL query
	 */
	public T fql_query( CharSequence query ) throws FacebookException, IOException {
		assert ( null != query );
		return callMethod( FacebookMethod.FQL_QUERY, newPair( "query", query ) );
	}

	private String generateSignature( List<String> params, boolean requiresSession ) {
		String secret = ( isDesktop() && requiresSession ) ? this._sessionSecret : this._secret;
		return FacebookSignatureUtil.generateSignature( params, secret );
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
	protected T callMethod( IFacebookMethod method, Pair<String,CharSequence>... paramPairs ) throws FacebookException, IOException {
		return callMethod( method, Arrays.asList( paramPairs ) );
	}

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param photoIds
	 *            retrieve from this list of photos (optional)
	 * @return an T of photo objects.
	 * @see #photos_get(Long, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public T photos_get( Collection<Long> photoIds ) throws FacebookException, IOException {
		return photos_get( null /* subjId */, null /* albumId */, photoIds );
	}

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param subjId
	 *            retrieve from photos associated with this user (optional).
	 * @param albumId
	 *            retrieve from photos from this album (optional)
	 * @return an T of photo objects.
	 * @see #photos_get(Long, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public T photos_get( Long subjId, Long albumId ) throws FacebookException, IOException {
		return photos_get( subjId, albumId, null /* photoIds */);
	}

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param subjId
	 *            retrieve from photos associated with this user (optional).
	 * @param photoIds
	 *            retrieve from this list of photos (optional)
	 * @return an T of photo objects.
	 * @see #photos_get(Long, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public T photos_get( Long subjId, Collection<Long> photoIds ) throws FacebookException, IOException {
		return photos_get( subjId, null /* albumId */, photoIds );
	}

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param subjId
	 *            retrieve from photos associated with this user (optional).
	 * @return an T of photo objects.
	 * @see #photos_get(Long, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public T photos_get( Long subjId ) throws FacebookException, IOException {
		return photos_get( subjId, null /* albumId */, null /* photoIds */);
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
	 * @return an T of photo objects.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public T photos_get( Long subjId, Long albumId, Collection<Long> photoIds ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_GET.numParams() );

		boolean hasUserId = null != subjId && 0 != subjId;
		boolean hasAlbumId = null != albumId && 0 != albumId;
		boolean hasPhotoIds = null != photoIds && !photoIds.isEmpty();
		if ( !hasUserId && !hasAlbumId && !hasPhotoIds ) {
			throw new IllegalArgumentException( "At least one of photoIds, albumId, or subjId must be provided" );
		}

		if ( hasUserId ) {
			params.add( newPair( "subj_id", subjId ) );
		}
		if ( hasAlbumId ) {
			params.add( newPair( "aid", albumId ) );
		}
		if ( hasPhotoIds ) {
			params.add( newPair( "pids", delimit( photoIds ) ) );
		}

		return callMethod( FacebookMethod.PHOTOS_GET, params );
	}

	/**
	 * Retrieves the tags for the given set of photos.
	 * 
	 * @param photoIds
	 *            The list of photos from which to extract photo tags.
	 * @return the created album
	 */
	public T photos_getTags( Collection<Long> photoIds ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PHOTOS_GET_TAGS, newPair( "pids", delimit( photoIds ) ) );
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
	public T groups_get( Long userId, Collection<Long> groupIds ) throws FacebookException, IOException {
		boolean hasGroups = ( null != groupIds && !groupIds.isEmpty() );
		if ( null != userId ) {
			return hasGroups ? callMethod( FacebookMethod.GROUPS_GET, newPair( "uid", userId.toString() ), newPair( "gids", delimit( groupIds ) ) ) : callMethod(
					FacebookMethod.GROUPS_GET, newPair( "uid", userId.toString() ) );
		} else {
			return hasGroups ? callMethod( FacebookMethod.GROUPS_GET, newPair( "gids", delimit( groupIds ) ) ) : callMethod( FacebookMethod.GROUPS_GET );
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
	protected T callMethod( IFacebookMethod method, Collection<Pair<String,CharSequence>> paramPairs ) throws FacebookException, IOException {
		this.rawResponse = null;
		Map<String,CharSequence> params = new HashMap<String,CharSequence>( 2 * method.numTotalParams() );
		if ( this.permissionsApiKey != null ) {
			params.put( "call_as_apikey", permissionsApiKey );
		}
		params.put( "method", method.methodName() );
		params.put( "api_key", _apiKey );
		params.put( "v", TARGET_API_VERSION );

		String format = getResponseFormat();
		if ( null != format ) {
			params.put( "format", format );
		}

		params.put( "call_id", Long.toString( System.currentTimeMillis() ) );
		boolean includeSession = method.requiresSession() && _sessionKey != null;
		if ( includeSession ) {
			params.put( "session_key", _sessionKey );
		}

		CharSequence oldVal;
		for ( Pair<String,CharSequence> p : paramPairs ) {
			oldVal = params.put( p.first, p.second );
			if ( oldVal != null ) {
				System.err.printf( "For parameter %s, overwrote old value %s with new value %s.", p.first, oldVal, p.second );
			}
		}

		assert ( !params.containsKey( "sig" ) );
		String signature = generateSignature( FacebookSignatureUtil.convert( params.entrySet() ), includeSession );
		params.put( "sig", signature );

		if ( this.batchMode ) {
			// if we are running in batch mode, don't actually execute the query now, just add it to the list
			boolean addToBatch = true;
			if ( method.methodName().equals( FacebookMethod.USERS_GET_LOGGED_IN_USER.methodName() ) ) {
				Exception trace = new Exception();
				StackTraceElement[] traceElems = trace.getStackTrace();
				int index = 0;
				for ( StackTraceElement elem : traceElems ) {
					if ( elem.getMethodName().indexOf( "_" ) != -1 ) {
						StackTraceElement caller = traceElems[index + 1];
						if ( ( caller.getClassName().equals( ExtensibleClient.class.getName() ) ) && ( !caller.getMethodName().startsWith( "auth_" ) ) ) {
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

		boolean doHttps = isDesktop() && FacebookMethod.AUTH_GET_SESSION.equals( method );
		boolean doEncode = true;
		InputStream data = method.takesFile() ? postFileRequest( method.methodName(), params, doEncode ) : postRequest( method.methodName(), params, doHttps, doEncode );

		BufferedReader in = new BufferedReader( new InputStreamReader( data, "UTF-8" ) );
		StringBuilder buffer = new StringBuilder();
		String line;
		boolean insideTagBody = false;
		while ( ( line = in.readLine() ) != null ) {
			/*
			 * is the last char a close ('>')? if not, we need to add a comma to the string as FB (unfortunately) lets people enter profile information and use hard
			 * returns, which are stripped out For example, this is a "valid" XML from FB: <?xml version="1.0" encoding="UTF-8"?> <users_getInfo_response
			 * xmlns="http://api.facebook.com/1.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://api.facebook.com/1.0/
			 * http://api.facebook.com/1.0/facebook.xsd" list="true"> <user> <uid>12345678</uid> <first_name>Bob</first_name> <music>My Morning Jacket, Libertines The
			 * Clash</music> </user> </users_getInfo_response>
			 * 
			 * When the buffer is built, <music> ends up like this: "My Morning Jacket, LibertinesTheClash" which makes it impossible to parse as the delimiters are
			 * destroyed
			 */
			if ( "xml".equalsIgnoreCase( getResponseFormat() ) && method != FacebookMethod.BATCH_RUN ) {
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
		return parseCallResult( new ByteArrayInputStream( xmlResp.getBytes( "UTF-8" ) ), method );
	}

	/**
	 * Parses the result of an API call into a T.
	 * 
	 * @param data
	 *            an InputStream with the results of a request to the Facebook servers
	 * @param method
	 *            the method called
	 * @throws FacebookException
	 *             if <code>data</code> represents an error
	 * @throws IOException
	 *             if <code>data</code> is not readable
	 * @return a T
	 */
	protected abstract T parseCallResult( InputStream data, IFacebookMethod method ) throws FacebookException, IOException;

	/**
	 * Recaches the referenced url.
	 * 
	 * @param url
	 *            the URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshRefUrl( URL url ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.FBML_REFRESH_REF_URL, newPair( "url", url.toString() ) ) );
	}

	/**
	 * Retrieves the outstanding notifications for the session user.
	 * 
	 * @return a T containing notification count pairs for 'messages', 'pokes' and 'shares', a uid list of 'friend_requests', a gid list of 'group_invites', and an eid
	 *         list of 'event_invites'
	 */
	public T notifications_get() throws FacebookException, IOException {
		return callMethod( FacebookMethod.NOTIFICATIONS_GET );
	}

	public T users_getStandardInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException, IOException {
		useBetaApiServer();
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_STANDARD_INFO, newPair( "uids", delimit( userIds ) ), newPair( "fields", delimit( fields ) ) );
	}

	public T users_getStandardInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException, IOException {
		useBetaApiServer();
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_STANDARD_INFO, newPair( "uids", delimit( userIds ) ), newPair( "fields", delimit( fields ) ) );
	}

	public T users_getInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException, IOException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_INFO, newPair( "uids", delimit( userIds ) ), newPair( "fields", delimit( fields ) ) );
	}

	public T users_getInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException, IOException {
		assert ( userIds != null );
		assert ( fields != null );
		assert ( !fields.isEmpty() );
		return callMethod( FacebookMethod.USERS_GET_INFO, newPair( "uids", delimit( userIds ) ), newPair( "fields", delimit( fields ) ) );
	}

	/**
	 * Retrieves the user ID of the user logged in to this API session
	 * 
	 * @return the Facebook user ID of the logged-in user
	 */
	public long users_getLoggedInUser() throws FacebookException, IOException {
		if ( this._userId == -1 || this.batchMode ) {
			T result = callMethod( FacebookMethod.USERS_GET_LOGGED_IN_USER );
			this._userId = extractLong( result );
		}
		return this._userId;
	}

	/**
	 * Call this function to get the user ID.
	 * 
	 * @return The ID of the current session's user, or -1 if none.
	 */
	public long auth_getUserId( String authToken ) throws FacebookException, IOException {
		/*
		 * Get the session information if we don't have it; this will populate the user ID as well.
		 */
		if ( null == this._sessionKey ) {
			auth_getSession( authToken );
		}
		return users_getLoggedInUser();
	}

	public boolean isDesktop() {
		return this._isDesktop;
	}

	private boolean photos_addTag( Long photoId, Double xPct, Double yPct, Long taggedUserId, CharSequence tagText ) throws FacebookException, IOException {
		assert ( null != photoId && !photoId.equals( 0 ) );
		assert ( null != taggedUserId || null != tagText );
		assert ( null != xPct && xPct >= 0 && xPct <= 100 );
		assert ( null != yPct && yPct >= 0 && yPct <= 100 );
		Pair<String,CharSequence> tagData;
		if ( taggedUserId != null ) {
			tagData = newPair( "tag_uid", taggedUserId.toString() );
		} else {
			tagData = newPair( "tag_text", tagText.toString() );
		}
		T d = callMethod( FacebookMethod.PHOTOS_ADD_TAG, newPair( "pid", photoId.toString() ), tagData, newPair( "x", xPct.toString() ), newPair( "y", yPct.toString() ) );
		return extractBoolean( d );
	}

	/**
	 * Retrieves an indicator of whether the logged-in user has installed the application associated with the _apiKey.
	 * 
	 * @return boolean indicating whether the user has installed the app
	 */
	public boolean users_isAppAdded() throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.USERS_IS_APP_ADDED ) );
	}

	/**
	 * Retrieves whether the logged-in user has granted the specified permission to this application.
	 * 
	 * @param permission
	 *            an extended permission (e.g. FacebookExtendedPerm.MARKETPLACE, "photo_upload")
	 * @return boolean indicating whether the user has the permission
	 * @see FacebookExtendedPerm
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Users.hasAppPermission"> Developers Wiki: Users.hasAppPermission</a>
	 */
	public boolean users_hasAppPermission( CharSequence permission ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.USERS_HAS_APP_PERMISSION, newPair( "ext_perm", permission ) ) );
	}

	/**
	 * Sets the logged-in user's Facebook status. Requires the status_update extended permission.
	 * 
	 * @return whether the status was successfully set
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#STATUS_UPDATE
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Users.setStatus"> Developers Wiki: Users.setStatus</a>
	 */
	public boolean users_setStatus( String status ) throws FacebookException, IOException {
		return users_setStatus( status, false, false );
	}

	/**
	 * Clears the logged-in user's Facebook status. Requires the status_update extended permission.
	 * 
	 * @return whether the status was successfully cleared
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#STATUS_UPDATE
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Users.setStatus"> Developers Wiki: Users.setStatus</a>
	 */
	public boolean users_clearStatus() throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.USERS_SET_STATUS, newPair( "clear", "1" ) ) );
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

	/**
	 * Helper function for posting a request that includes raw file data, eg {@link #photos_upload(File)}.
	 * 
	 * @param methodName
	 *            the name of the method
	 * @param params
	 *            request parameters (not including the file)
	 * @return an InputStream with the request response
	 * @see #photos_upload(File)
	 */
	protected InputStream postFileRequest( String methodName, Map<String,CharSequence> params ) throws IOException {
		return postFileRequest( methodName, params, /* doEncode */true );
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
	protected InputStream postFileRequest( String methodName, Map<String,CharSequence> params, boolean doEncode ) throws IOException {
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

			for ( Map.Entry<String,CharSequence> entry : params.entrySet() ) {
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
		catch ( Exception e ) {
			logException( e );
			return null;
		}
	}

	/**
	 * Logs an exception with default message
	 * 
	 * @param e
	 *            the exception
	 */
	protected final void logException( Exception e ) {
		logException( "exception", e );
	}

	/**
	 * Logs an exception with an introductory message in addition to the exception's getMessage().
	 * 
	 * @param msg
	 *            message
	 * @param e
	 *            exception
	 * @see Exception#getMessage
	 */
	protected void logException( CharSequence msg, Exception e ) {
		System.err.println( msg + ":" + e.getMessage() );
		e.printStackTrace();
	}

	/**
	 * Logs a message. Override this for more detailed logging.
	 * 
	 * @param message
	 */
	protected void log( CharSequence message ) {
		if ( isDebug() ) {
			System.out.println( message );
		}
	}

	/**
	 * @return whether debugging is activated
	 */
	public boolean isDebug() {
		return ( null == _debug ) ? DEBUG : _debug.booleanValue();
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public URL notifications_send( Collection<Long> recipientIds, CharSequence notification, CharSequence email ) throws FacebookException, IOException {
		notifications_send( recipientIds, notification );
		return null;
	}

	/**
	 * Extracts a URL from a result that consists of a URL only.
	 * 
	 * @param result
	 * @return the URL
	 */
	protected abstract URL extractURL( T result ) throws IOException;

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
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( File photo ) throws FacebookException, IOException {
		return photos_upload( photo, null /* caption */, null /* albumId */);
	}

	/**
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @param caption
	 *            a description of the image contents
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( File photo, String caption ) throws FacebookException, IOException {
		return photos_upload( photo, caption, null /* albumId */);
	}

	/**
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @param albumId
	 *            the album into which the photo should be uploaded
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( File photo, Long albumId ) throws FacebookException, IOException {
		return photos_upload( photo, null /* caption */, albumId );
	}

	/**
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @param caption
	 *            a description of the image contents
	 * @param albumId
	 *            the album into which the photo should be uploaded
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( File photo, String caption, Long albumId ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_UPLOAD.numParams() );
		assert ( photo.exists() && photo.canRead() );
		this._uploadFile = photo;
		if ( null != albumId ) {
			params.add( newPair( "aid", albumId ) );
		}
		if ( null != caption ) {
			params.add( newPair( "caption", caption ) );
		}
		return callMethod( FacebookMethod.PHOTOS_UPLOAD, params );
	}

	/**
	 * Creates an album.
	 * 
	 * @param albumName
	 *            The list of photos from which to extract photo tags.
	 * @return the created album
	 */
	public T photos_createAlbum( String albumName ) throws FacebookException, IOException {
		return photos_createAlbum( albumName, null /* description */, null /* location */);
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
	 * Adds several tags to a photo.
	 * 
	 * @param photoId
	 *            The photo id of the photo to be tagged.
	 * @param tags
	 *            A list of PhotoTags.
	 * @return a list of booleans indicating whether the tag was successfully added.
	 */
	public T photos_addTags( Long photoId, Collection<PhotoTag> tags ) throws FacebookException, IOException {
		assert ( photoId > 0 );
		assert ( null != tags && !tags.isEmpty() );

		JSONArray jsonTags = new JSONArray();
		for ( PhotoTag tag : tags ) {
			jsonTags.put( tag.jsonify() );
		}

		return callMethod( FacebookMethod.PHOTOS_ADD_TAG, newPair( "pid", photoId.toString() ), newPair( "tags", jsonTags.toString() ) );
	}

	public void setIsDesktop( boolean isDesktop ) {
		this._isDesktop = isDesktop;
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
	 * @return T of events
	 */
	public T events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.EVENTS_GET.numParams() );
		boolean hasUserId = null != userId && 0 != userId;
		boolean hasEventIds = null != eventIds && !eventIds.isEmpty();
		boolean hasStart = null != startTime && 0 != startTime;
		boolean hasEnd = null != endTime && 0 != endTime;
		if ( hasUserId ) {
			params.add( newPair( "uid", userId ) );
		}
		if ( hasEventIds ) {
			params.add( newPair( "eids", delimit( eventIds ) ) );
		}
		if ( hasStart ) {
			params.add( newPair( "start_time", startTime ) );
		}
		if ( hasEnd ) {
			params.add( newPair( "end_time", endTime ) );
		}
		return callMethod( FacebookMethod.EVENTS_GET, params );
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

		return extractBoolean( callMethod( FacebookMethod.PROFILE_SET_FBML, newPair( "uid", userId ), newPair( "markup", fbmlMarkup ) ) );

	}

	protected static CharSequence delimit( Collection<Map.Entry<String,CharSequence>> entries, CharSequence delimiter, CharSequence equals, boolean doEncode ) {
		if ( entries == null || entries.isEmpty() )
			return null;

		StringBuilder buffer = new StringBuilder();
		boolean notFirst = false;
		for ( Map.Entry<String,CharSequence> entry : entries ) {
			if ( notFirst )
				buffer.append( delimiter );
			else
				notFirst = true;
			CharSequence value = entry.getValue();
			buffer.append( entry.getKey() ).append( equals ).append( doEncode ? encode( value ) : value );
		}
		return buffer;
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
	public T photos_createAlbum( String name, String description, String location ) throws FacebookException, IOException {
		assert ( null != name && !"".equals( name ) );
		ArrayList<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_CREATE_ALBUM.numParams() );
		params.add( newPair( "name", name ) );
		if ( null != description )
			params.add( newPair( "description", description ) );
		if ( null != location )
			params.add( newPair( "location", location ) );
		return callMethod( FacebookMethod.PHOTOS_CREATE_ALBUM, params );
	}

	public void setDebug( boolean isDebug ) {
		_debug = isDebug;
	}

	/**
	 * Extracts a Boolean from a result that consists of a Boolean only.
	 * 
	 * @param result
	 * @return the Boolean
	 */
	protected boolean extractBoolean( T result ) {
		if ( result == null ) {
			return false;
		}
		return 1 == extractInt( result );
	}

	/**
	 * Extracts an Long from a result that consists of an Long only.
	 * 
	 * @param result
	 * @return the Long
	 */
	protected abstract int extractInt( T result );

	/**
	 * Extracts an Long from a result that consists of a Long only.
	 * 
	 * @param result
	 * @return the Long
	 */
	protected abstract Long extractLong( T result );

	/**
	 * Retrieves album metadata for a list of album IDs.
	 * 
	 * @param albumIds
	 *            the ids of albums whose metadata is to be retrieved
	 * @return album objects
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.getAlbums"> Developers Wiki: Photos.getAlbums</a>
	 */
	public T photos_getAlbums( Collection<Long> albumIds ) throws FacebookException, IOException {
		return photos_getAlbums( null /* userId */, albumIds );
	}

	/**
	 * Retrieves album metadata for albums owned by a user.
	 * 
	 * @param userId
	 *            (optional) the id of the albums' owner (optional)
	 * @return album objects
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.getAlbums"> Developers Wiki: Photos.getAlbums</a>
	 */
	public T photos_getAlbums( Long userId ) throws FacebookException, IOException {
		return photos_getAlbums( userId, null /* albumIds */);
	}

	/**
	 * Retrieves album metadata. Pass a user id and/or a list of album ids to specify the albums to be retrieved (at least one must be provided)
	 * 
	 * @param userId
	 *            (optional) the id of the albums' owner (optional)
	 * @param albumIds
	 *            (optional) the ids of albums whose metadata is to be retrieved
	 * @return album objects
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.getAlbums"> Developers Wiki: Photos.getAlbums</a>
	 */
	public T photos_getAlbums( Long userId, Collection<Long> albumIds ) throws FacebookException, IOException {
		boolean hasUserId = null != userId && userId != 0;
		boolean hasAlbumIds = null != albumIds && !albumIds.isEmpty();
		assert ( hasUserId || hasAlbumIds ); // one of the two must be provided
		if ( hasUserId ) {
			return ( hasAlbumIds ) ? callMethod( FacebookMethod.PHOTOS_GET_ALBUMS, newPair( "uid", userId ), newPair( "aids", delimit( albumIds ) ) ) : callMethod(
					FacebookMethod.PHOTOS_GET_ALBUMS, newPair( "uid", userId ) );
		} else {
			return callMethod( FacebookMethod.PHOTOS_GET_ALBUMS, newPair( "aids", delimit( albumIds ) ) );
		}
	}

	/**
	 * Recaches the image with the specified imageUrl.
	 * 
	 * @param imageUrl
	 *            the image URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshImgSrc( URL imageUrl ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.FBML_REFRESH_IMG_SRC, newPair( "url", imageUrl.toString() ) ) );
	}

	/**
	 * Retrieves the friends of the currently logged in user.
	 * 
	 * @return array of friends
	 */
	public T friends_get() throws FacebookException, IOException {
		return callMethod( FacebookMethod.FRIENDS_GET );
	}

	private InputStream postRequest( CharSequence method, Map<String,CharSequence> params, boolean doHttps, boolean doEncode ) throws IOException {
		CharSequence buffer = ( null == params ) ? "" : delimit( params.entrySet(), "&", "=", doEncode );
		URL serverUrl = ( doHttps ) ? HTTPS_SERVER_URL : _serverUrl;
		if ( isDebug() ) {
			StringBuilder debugMsg = new StringBuilder().append( method ).append( " POST: " ).append( serverUrl.toString() ).append( "?" );
			debugMsg.append( buffer );
			log( debugMsg );
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
			logException( ex );
		}
		conn.setDoOutput( true );
		conn.connect();
		conn.getOutputStream().write( buffer.toString().getBytes() );

		return conn.getInputStream();
	}

	/**
	 * Call this function and store the result, using it to generate the appropriate login url and then to retrieve the session information.
	 * 
	 * @return an authentication token
	 */
	public String auth_createToken() throws FacebookException, IOException {
		T d = callMethod( FacebookMethod.AUTH_CREATE_TOKEN );
		return extractString( d );
	}

	/**
	 * Extracts a String from a T consisting entirely of a String.
	 * 
	 * @param result
	 * @return the String
	 */
	protected abstract String extractString( T result );

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
	 */
	public Long marketplace_createListing( Boolean showOnProfile, MarketplaceListing attrs ) throws FacebookException, IOException {
		T result = callMethod( FacebookMethod.MARKETPLACE_CREATE_LISTING, newPair( "show_on_profile", showOnProfile ? "1" : "0" ), newPair( "listing_id", "0" ), newPair(
				"listing_attrs", attrs.jsonify().toString() ) );
		return extractLong( result );
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
	public Long marketplace_editListing( Long listingId, Boolean showOnProfile, MarketplaceListing attrs ) throws FacebookException, IOException {
		T result = callMethod( FacebookMethod.MARKETPLACE_CREATE_LISTING, newPair( "show_on_profile", showOnProfile ? "1" : "0" ), newPair( "listing_id", listingId
				.toString() ), newPair( "listing_attrs", attrs.jsonify().toString() ) );
		return extractLong( result );
	}

	/**
	 * Remove a marketplace listing
	 * 
	 * @param listingId
	 *            the listing to be removed
	 * @return boolean indicating whether the listing was removed
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.removeListing"> Developers Wiki: marketplace.removeListing</a>
	 */
	public boolean marketplace_removeListing( Long listingId ) throws FacebookException, IOException {
		return marketplace_removeListing( listingId, MARKETPLACE_STATUS_DEFAULT );
	}

	/**
	 * Remove a marketplace listing
	 * 
	 * @param listingId
	 *            the listing to be removed
	 * @param status
	 *            MARKETPLACE_STATUS_DEFAULT, MARKETPLACE_STATUS_SUCCESS, or MARKETPLACE_STATUS_NOT_SUCCESS
	 * @return boolean indicating whether the listing was removed
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.removeListing"> Developers Wiki: marketplace.removeListing</a>
	 */
	public boolean marketplace_removeListing( Long listingId, CharSequence status ) throws FacebookException, IOException {
		assert MARKETPLACE_STATUS_DEFAULT.equals( status ) || MARKETPLACE_STATUS_SUCCESS.equals( status ) || MARKETPLACE_STATUS_NOT_SUCCESS.equals( status ) : "Invalid status: "
				+ status;
		T result = callMethod( FacebookMethod.MARKETPLACE_REMOVE_LISTING, newPair( "listing_id", listingId.toString() ), newPair( "status", status ) );
		return extractBoolean( result );
	}

	/**
	 * Get the categories available in marketplace.
	 * 
	 * @return a T listing the marketplace categories
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getCategories"> Developers Wiki: marketplace.getCategories</a>
	 */
	public List<String> marketplace_getCategories() throws FacebookException, IOException {
		T temp = callMethod( FacebookMethod.MARKETPLACE_GET_CATEGORIES );
		if ( temp == null ) {
			return null;
		}
		List<String> results = new ArrayList<String>();
		if ( temp instanceof Document ) {
			Document d = (Document) temp;
			NodeList cats = d.getElementsByTagName( "marketplace_category" );
			for ( int count = 0; count < cats.getLength(); count++ ) {
				results.add( cats.item( count ).getFirstChild().getTextContent() );
			}
		} else {
			JSONObject j = (JSONObject) temp;
			Iterator it = j.keys();
			while ( it.hasNext() ) {
				try {
					results.add( j.get( (String) it.next() ).toString() );
				}
				catch ( Exception ignored ) {
					// ignore
				}
			}
		}
		return results;
	}

	/**
	 * Get the subcategories available for a category.
	 * 
	 * @param category
	 *            a category, e.g. "HOUSING"
	 * @return a T listing the marketplace sub-categories
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getSubCategories"> Developers Wiki: marketplace.getSubCategories</a>
	 */
	public T marketplace_getSubCategories( CharSequence category ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.MARKETPLACE_GET_SUBCATEGORIES, newPair( "category", category ) );
	}

	/**
	 * Fetch marketplace listings, filtered by listing IDs and/or the posting users' IDs.
	 * 
	 * @param listingIds
	 *            listing identifiers (required if uids is null/empty)
	 * @param userIds
	 *            posting user identifiers (required if listingIds is null/empty)
	 * @return a T of marketplace listings
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getListings"> Developers Wiki: marketplace.getListings</a>
	 */
	public T marketplace_getListings( Collection<Long> listingIds, Collection<Long> userIds ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.MARKETPLACE_GET_LISTINGS.numParams() );
		if ( null != listingIds && !listingIds.isEmpty() ) {
			params.add( newPair( "listing_ids", delimit( listingIds ) ) );
		}
		if ( null != userIds && !userIds.isEmpty() ) {
			params.add( newPair( "uids", delimit( userIds ) ) );
		}
		assert !params.isEmpty() : "Either listingIds or userIds should be provided";
		return callMethod( FacebookMethod.MARKETPLACE_GET_LISTINGS, params );
	}

	/**
	 * Search for marketplace listings, optionally by category, subcategory, and/or query string.
	 * 
	 * @param category
	 *            the category of listings desired (optional except if subcategory is provided)
	 * @param subCategory
	 *            the subcategory of listings desired (optional)
	 * @param query
	 *            a query string (optional)
	 * @return a T of marketplace listings
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.search"> Developers Wiki: marketplace.search</a>
	 */
	public T marketplace_search( CharSequence category, CharSequence subCategory, CharSequence query ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.MARKETPLACE_SEARCH.numParams() );
		boolean hasCategory = addParamIfNotBlank( "category", category, params );
		if ( hasCategory ) {
			addParamIfNotBlank( "subcategory", subCategory, params );
		}
		addParamIfNotBlank( "query", query, params );
		return callMethod( FacebookMethod.MARKETPLACE_SEARCH, params );
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
	public T photos_getByAlbum( Long albumId, Collection<Long> photoIds ) throws FacebookException, IOException {
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
	public T photos_getByAlbum( Long albumId ) throws FacebookException, IOException {
		return photos_get( null /* subjId */, albumId, null /* photoIds */);
	}

	/**
	 * Get the categories available in marketplace.
	 * 
	 * @return a T listing the marketplace categories
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getCategories"> Developers Wiki: marketplace.getCategories</a>
	 */
	public T marketplace_getCategoriesObject() throws FacebookException, IOException {
		return callMethod( FacebookMethod.MARKETPLACE_GET_CATEGORIES );
	}

	public String getRawResponse() {
		return this.rawResponse;
	}

	public Object getResponsePOJO() {
		if ( this.rawResponse == null ) {
			return null;
		}
		if ( JAXB_CONTEXT == null ) {
			return null;
		}
		if ( ( getResponseFormat() != null ) && ( !"xml".equalsIgnoreCase( getResponseFormat() ) ) ) {
			// JAXB will not work with JSON
			throw new RuntimeException( "You can only generate a response POJO when using XML formatted API responses!  JSON users go elsewhere!" );
		}
		Object pojo = null;

    byte[] rawBytes = null;
    try {
      rawBytes = this.rawResponse.getBytes("UTF-8");
      Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
			pojo = unmarshaller.unmarshal( new ByteArrayInputStream( rawBytes ) );
		}
		catch ( JAXBException e ) {
      System.err.println( "getResponsePOJO() - Could not unmarshall XML stream into POJO:" );
      System.err.println(rawBytes);
      e.printStackTrace();
		}
		catch ( NullPointerException e ) {
			System.err.println( "getResponsePOJO() - Could not unmarshall XML stream into POJO:" );
      System.err.println(rawBytes);
      e.printStackTrace();
		}
		catch ( UnsupportedEncodingException e ) {
			System.err.println( "getResponsePOJO() - Could not unmarshall XML stream into POJO:" );
      System.err.println(rawBytes);
      e.printStackTrace();
		}
		return pojo;
	}

	public boolean feed_PublishTemplatizedAction( TemplatizedAction action ) throws FacebookException, IOException {
		return templatizedFeedHandler( FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, action.getTitleTemplate(), action.getTitleParams(), action.getBodyTemplate(),
				action.getBodyParams(), action.getBodyGeneral(), action.getPictures(), action.getTargetIds(), action.getPageActorId() );
	}

	public boolean feed_publishTemplatizedAction( String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds ) throws FacebookException, IOException {
		return templatizedFeedHandler( FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, pictures,
				targetIds, null );
	}

	protected boolean templatizedFeedHandler( FacebookMethod method, String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds, Long pageId ) throws FacebookException, IOException {
		assert ( pictures == null || pictures.size() <= 4 );

		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( method.numParams() );

		// these are always required parameters
		addParam( "title_template", titleTemplate, params );

		// these are optional parameters
		addParamIfNotBlank( "title_data", titleData, params );
		boolean hasBody = addParamIfNotBlank( "body_template", bodyTemplate, params );
		if ( hasBody ) {
			addParamIfNotBlank( "body_data", bodyData, params );
		}
		addParamIfNotBlank( "body_general", bodyGeneral, params );
		if ( pictures != null ) {
			int count = 1;
			for ( IPair picture : pictures ) {
				String url = picture.getFirst().toString();
				if ( url.startsWith( TemplatizedAction.UID_TOKEN ) ) {
					url = url.substring( TemplatizedAction.UID_TOKEN.length() );
				}
				addParam( "image_" + count, url, params );
				if ( picture.getSecond() != null ) {
					addParam( "image_" + count + "_link", picture.getSecond().toString(), params );
				}
				count++ ;
			}
		}
		addParamIfNotBlank( "target_ids", targetIds, params );
		addParamIfNotBlank( "page_actor_id", pageId, params );
		return extractBoolean( callMethod( method, params ) );
	}

	public boolean users_hasAppPermission( Permission perm ) throws FacebookException, IOException {
		return users_hasAppPermission( perm.getName() );
	}

	public Long marketplace_createListing( Long listingId, boolean showOnProfile, String attributes ) throws FacebookException, IOException {
		T result = callMethod( FacebookMethod.MARKETPLACE_CREATE_LISTING, newPair( "show_on_profile", showOnProfile ? "1" : "0" ), newPair( "listing_id", "0" ), newPair(
				"listing_attrs", attributes ) );
		return extractLong( result );
	}

	public Long marketplace_createListing( Long listingId, boolean showOnProfile, MarketListing listing ) throws FacebookException, IOException {
		return marketplace_createListing( listingId, showOnProfile, listing.getAttribs() );
	}

	public Long marketplace_createListing( boolean showOnProfile, MarketListing listing ) throws FacebookException, IOException {
		return marketplace_createListing( null, showOnProfile, listing.getAttribs() );
	}

	public boolean marketplace_removeListing( Long listingId, MarketListingStatus status ) throws FacebookException, IOException {
		return marketplace_removeListing( listingId, status.getName() );
	}

	public Long marketplace_editListing( Long listingId, Boolean showOnProfile, MarketListing attrs ) throws FacebookException, IOException {
		T result = callMethod( FacebookMethod.MARKETPLACE_CREATE_LISTING, newPair( "show_on_profile", showOnProfile ? "1" : "0" ), newPair( "listing_id", listingId
				.toString() ), newPair( "listing_attrs", attrs.getAttribs() ) );
		return extractLong( result );
	}

	public boolean users_setStatus( String newStatus, boolean clear ) throws FacebookException, IOException {
		return users_setStatus( newStatus, clear, false );
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
	public T pages_getInfo( Collection<Long> pageIds, EnumSet<PageProfileField> fields ) throws FacebookException, IOException {
		if ( pageIds == null || pageIds.isEmpty() ) {
			throw new IllegalArgumentException( "pageIds cannot be empty or null" );
		}
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		IFacebookMethod method = ( null == this._sessionKey ) ? FacebookMethod.PAGES_GET_INFO_NO_SESSION : FacebookMethod.PAGES_GET_INFO;
		return callMethod( method, newPair( "page_ids", delimit( pageIds ) ), newPair( "fields", delimit( fields ) ) );
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
	public T pages_getInfo( Collection<Long> pageIds, Set<CharSequence> fields ) throws FacebookException, IOException {
		if ( pageIds == null || pageIds.isEmpty() ) {
			throw new IllegalArgumentException( "pageIds cannot be empty or null" );
		}
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		IFacebookMethod method = null == this._sessionKey ? FacebookMethod.PAGES_GET_INFO_NO_SESSION : FacebookMethod.PAGES_GET_INFO;
		return callMethod( method, newPair( "page_ids", delimit( pageIds ) ), newPair( "fields", delimit( fields ) ) );
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
	public T pages_getInfo( Long userId, EnumSet<PageProfileField> fields ) throws FacebookException, IOException {
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		if ( userId == null ) {
			userId = this._userId;
		}
		return callMethod( FacebookMethod.PAGES_GET_INFO, newPair( "uid", userId.toString() ), newPair( "fields", delimit( fields ) ) );
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
	public T pages_getInfo( Long userId, Set<CharSequence> fields ) throws FacebookException, IOException {
		if ( fields == null || fields.isEmpty() ) {
			throw new IllegalArgumentException( "fields cannot be empty or null" );
		}
		if ( userId == null ) {
			userId = this._userId;
		}
		return callMethod( FacebookMethod.PAGES_GET_INFO, newPair( "uid", userId.toString() ), newPair( "fields", delimit( fields ) ) );
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
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_APP_ADDED, newPair( "page_id", pageId.toString() ) ) );
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
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_FAN, newPair( "page_id", pageId.toString() ), newPair( "uid", userId.toString() ) ) );
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
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_FAN, newPair( "page_id", pageId.toString() ) ) );
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
		return extractBoolean( callMethod( FacebookMethod.PAGES_IS_ADMIN, newPair( "page_id", pageId.toString() ) ) );
	}

	/**
	 * Associates a "<code>handle</code>" with FBML markup so that the handle can be used within the <a
	 * href="http://wiki.developers.facebook.com/index.php/Fb:ref">fb:ref</a> FBML tag. A handle is unique within an application and allows an application to publish
	 * identical FBML to many user profiles and do subsequent updates without having to republish FBML for each user.
	 * 
	 * @param handle -
	 *            a string, unique within the application, that
	 * @param fbmlMarkup -
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Fbml.setRefHandle"> Developers Wiki: Fbml.setRefHandle</a>
	 */
	public boolean fbml_setRefHandle( String handle, String fbmlMarkup ) throws FacebookException, IOException {
		if ( this._isDesktop ) {
			// this method cannot be called from a desktop app
			return false;
		}
		return extractBoolean( callMethod( FacebookMethod.FBML_SET_REF_HANDLE, newPair( "handle", handle ), newPair( "fbml", fbmlMarkup ) ) );

	}

	public boolean sms_canSend() throws FacebookException, IOException {
		return sms_canSend( users_getLoggedInUser() );
	}

	/**
	 * Determines whether this application can send SMS to the user identified by <code>userId</code>
	 * 
	 * @param userId
	 *            a user ID
	 * @return true if sms can be sent to the user
	 * @see FacebookExtendedPerm#SMS
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Application_generated_messages"> Developers Wiki: Mobile: Application Generated Messages</a>
	 */
	public boolean sms_canSend( Long userId ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.SMS_CAN_SEND, newPair( "uid", userId.toString() ) ) );
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
		callMethod( FacebookMethod.SMS_SEND_MESSAGE, newPair( "uid", userId.toString() ), newPair( "message", response ), newPair( "session_id", mobileSessionId
				.toString() ) );
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
		callMethod( FacebookMethod.SMS_SEND_MESSAGE, newPair( "uid", userId.toString() ), newPair( "message", message ), newPair( "req_session", "0" ) );
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
		return extractInt( callMethod( FacebookMethod.SMS_SEND_MESSAGE, newPair( "uid", userId.toString() ), newPair( "message", message ), newPair( "req_session", "1" ) ) );
	}

	public void notifications_send( Collection<Long> recipientIds, CharSequence notification ) throws FacebookException, IOException {
		this.notifications_send( recipientIds, notification.toString(), false );
	}

	private T notifications_sendEmail( CharSequence recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException, IOException {
		if ( null == recipients || "".equals( recipients ) ) {
			// we throw an exception here because returning a sensible result (like an empty list) is problematic due to the use of Document as the return type
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You must specify at least one recipient when sending an email!" );
		}
		if ( ( null == email || "".equals( email ) ) && ( null == fbml || "".equals( fbml ) ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You cannot send an empty email!" );
		}
		T d;
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
			d = callMethod( method, newPair( "recipients", recipients ), newPair( "subject", subject ), newPair( paramName, paramValue ) );
		} else {
			d = callMethod( method, newPair( "recipients", recipients ), newPair( paramName, paramValue ) );
		}

		return d;
	}

	public T notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException, IOException {
		return notifications_sendEmail( delimit( recipients ), subject, email, fbml );
	}

	public T notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException, IOException {
		Long currentUser = users_getLoggedInUser();
		return notifications_sendEmail( currentUser.toString(), subject, email, fbml );
	}

	public T notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException, IOException {
		return notifications_sendEmail( delimit( recipients ), subject, null, fbml );
	}

	public T notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException, IOException {
		Long currentUser = users_getLoggedInUser();
		return notifications_sendEmail( currentUser.toString(), subject, null, fbml );
	}

	public T notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException, IOException {
		return notifications_sendEmail( delimit( recipients ), subject, email, null );
	}

	public T notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException, IOException {
		Long currentUser = users_getLoggedInUser();
		return notifications_sendEmail( currentUser.toString(), subject, email, null );
	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();

		if ( newStatus != null ) {
			params.add( newPair( "status", newStatus ) );
		}
		if ( clear ) {
			users_clearStatus();
		}
		if ( statusIncludesVerb ) {
			params.add( newPair( "status_includes_verb", "true" ) );
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
		args.add( newPair( "recipients", delimit( recipientIds ) ) );
		args.add( newPair( "subject", subject ) );
		if ( hasText ) {
			args.add( newPair( "text", text ) );
		}
		if ( hasFbml ) {
			args.add( newPair( "fbml", fbml ) );
		}
		// this method requires a session only if we're dealing with a desktop app
		T result = callMethod( isDesktop() ? FacebookMethod.NOTIFICATIONS_SEND_EMAIL_SESSION : FacebookMethod.NOTIFICATIONS_SEND_EMAIL, args );
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.facebook.api.IFacebookRestClient#sms_send(java.lang.String, java.lang.Integer, boolean)
	 */
	public Integer sms_send( String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException, IOException {
		if ( ( smsSessionId == null ) || ( smsSessionId <= 0 ) ) {
			return sms_sendMessageWithSession( users_getLoggedInUser(), message );
		} else {
			sms_sendResponse( (int) users_getLoggedInUser(), message, smsSessionId );
			return smsSessionId;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.facebook.api.IFacebookRestClient#sms_send(java.lang.Long, java.lang.String, java.lang.Integer, boolean)
	 */
	public Integer sms_send( Long userId, String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException, IOException {
		if ( ( smsSessionId == null ) || ( smsSessionId <= 0 ) ) {
			return sms_sendMessageWithSession( userId, message );
		} else {
			sms_sendResponse( userId.intValue(), message, smsSessionId );
			return smsSessionId;
		}
	}

	public T data_getCookies() throws FacebookException, IOException {
		return data_getCookies( users_getLoggedInUser(), null );
	}

	public T data_getCookies( Long userId ) throws FacebookException, IOException {
		return data_getCookies( userId, null );
	}

	public T data_getCookies( String name ) throws FacebookException, IOException {
		return data_getCookies( users_getLoggedInUser(), name );
	}

	public T data_getCookies( Long userId, CharSequence name ) throws FacebookException, IOException {
		ArrayList<Pair<String,CharSequence>> args = new ArrayList<Pair<String,CharSequence>>();
		args.add( newPair( "uid", userId ) );
		if ( ( name != null ) && ( !"".equals( name ) ) ) {
			args.add( newPair( "name", name ) );
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

		T doc;
		List<Pair<String,CharSequence>> args = new ArrayList<Pair<String,CharSequence>>();
		args.add( newPair( "uid", userId ) );
		args.add( newPair( "name", name ) );
		args.add( newPair( "value", value ) );
		if ( ( expires != null ) && ( expires > 0 ) ) {
			args.add( newPair( "expires", expires.toString() ) );
		}
		if ( ( path != null ) && ( !"".equals( path ) ) ) {
			args.add( newPair( "path", path ) );
		}
		doc = callMethod( FacebookMethod.DATA_SET_COOKIE, args );

		return extractBoolean( doc );
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
			T d = callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, newPair( "properties", encoding1.toString() ) );
			return extractBoolean( d );
		}
		catch ( FacebookException e ) {
			// if that didn't work, try the more convoluted encoding (which matches what they send back in response to admin_getAppProperties calls)
			T d = callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, newPair( "properties", encoding2.toString() ) );
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

	static Map<ApplicationProperty,String> parseProperties( String json ) {
		Map<ApplicationProperty,String> result = new HashMap<ApplicationProperty,String>();
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

	private static void parseFragment( String fragment, Map<ApplicationProperty,String> result ) {
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

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate ) throws FacebookException, IOException {
		return feed_publishTemplatizedAction( titleTemplate, null );
	}

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Long pageActorId ) throws FacebookException, IOException {
		return feed_publishTemplatizedAction( titleTemplate, null, null, null, null, null, null, pageActorId );
	}

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images,
			Long pageActorId ) throws FacebookException, IOException {
		assert null != titleTemplate && !"".equals( titleTemplate );

		FacebookMethod method = FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION;
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( method.numParams() );

		params.add( newPair( "title_template", titleTemplate ) );
		if ( null != titleData && !titleData.isEmpty() ) {
			JSONObject titleDataJson = new JSONObject();
			for ( String key : titleData.keySet() ) {
				try {
					titleDataJson.put( key, titleData.get( key ) );
				}
				catch ( Exception ignored ) {
					// ignore
				}
			}
			params.add( newPair( "title_data", titleDataJson.toString() ) );
		}

		if ( null != bodyTemplate && !"".equals( bodyTemplate ) ) {
			params.add( newPair( "body_template", bodyTemplate ) );
			if ( null != bodyData && !bodyData.isEmpty() ) {
				JSONObject bodyDataJson = new JSONObject();
				for ( String key : bodyData.keySet() ) {
					try {
						bodyDataJson.put( key, bodyData.get( key ) );
					}
					catch ( Exception ignored ) {
						// ignore
					}
				}
				params.add( newPair( "body_data", bodyDataJson.toString() ) );
			}
		}

		if ( null != bodyTemplate && !"".equals( bodyTemplate ) ) {
			params.add( newPair( "body_template", bodyTemplate ) );
		}

		if ( null != targetIds && !targetIds.isEmpty() ) {
			params.add( newPair( "target_ids", delimit( targetIds ) ) );
		}

		if ( bodyGeneral != null ) {
			params.add( newPair( "body_general", bodyGeneral ) );
		}

		if ( pageActorId != null ) {
			params.add( newPair( "page_actor_id", pageActorId ) );
		}

		handleFeedImages( params, images );

		return extractBoolean( callMethod( method, params ) );
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

	/**
	 * Retrieves the friends of the currently logged in user that are members of the friends list with ID <code>friendListId</code>.
	 * 
	 * @param friendListId
	 *            the friend list for which friends should be fetched. if <code>null</code>, all friends will be retrieved.
	 * @return T of friends
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Friends.get"> Developers Wiki: Friends.get</a>
	 */
	public T friends_get( Long friendListId ) throws FacebookException, IOException {
		FacebookMethod method = FacebookMethod.FRIENDS_GET;
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( method.numParams() );
		if ( null != friendListId ) {
			if ( 0L >= friendListId ) {
				throw new IllegalArgumentException( "given invalid friendListId " + friendListId.toString() );
			}
			params.add( newPair( "flid", friendListId.toString() ) );
		}
		return callMethod( method, params );
	}

	/**
	 * Retrieves the friend lists of the currently logged in user.
	 * 
	 * @return T of friend lists
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Friends.getLists"> Developers Wiki: Friends.getLists</a>
	 */
	public T friends_getLists() throws FacebookException, IOException {
		return callMethod( FacebookMethod.FRIENDS_GET_LISTS );
	}

	/**
	 * Sets several property values for an application. The properties available are analogous to the ones editable via the Facebook Developer application. A session is
	 * not required to use this method.
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
		return extractBoolean( callMethod( FacebookMethod.ADMIN_SET_APP_PROPERTIES, newPair( "properties", properties.toJsonString() ) ) );
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
	 * Starts a batch of queries. Any API calls made after invoking 'beginBatch' will be deferred until the next time you call 'executeBatch', at which time they will be
	 * processed as a batch query. All API calls made in the interim will return null as their result.
	 */
	public void beginBatch() {
		this.batchMode = true;
		this.queries = new ArrayList<BatchQuery>();
	}

	protected String encodeMethods( List<BatchQuery> queries ) throws FacebookException {
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
	public T batch_run( String methods, boolean serial ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( newPair( "method_feed", methods ) );
		if ( serial ) {
			params.add( newPair( "serial_only", "1" ) );
		}
		return callMethod( FacebookMethod.BATCH_RUN, params );
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
	public T application_getPublicInfo( Long applicationId, String applicationKey, String applicationCanvas ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( ( applicationId != null ) && ( applicationId > 0 ) ) {
			params.add( newPair( "application_id", applicationId ) );
		} else if ( ( applicationKey != null ) && ( !"".equals( applicationKey ) ) ) {
			params.add( newPair( "application_api_key", applicationKey ) );
		} else if ( ( applicationCanvas != null ) && ( !"".equals( applicationCanvas ) ) ) {
			params.add( newPair( "application_canvas_name", applicationCanvas ) );
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
	public T application_getPublicInfoById( Long applicationId ) throws FacebookException, IOException {
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
	public T application_getPublicInfoByApiKey( String applicationKey ) throws FacebookException, IOException {
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
	public T application_getPublicInfoByCanvasName( String applicationCanvas ) throws FacebookException, IOException {
		return application_getPublicInfo( null, null, applicationCanvas );
	}

	public int admin_getAllocation( String allocationType ) throws FacebookException, IOException {
		return extractInt( callMethod( FacebookMethod.ADMIN_GET_ALLOCATION, newPair( "integration_point_name", allocationType ) ) );
	}

	public int admin_getNotificationAllocation() throws FacebookException, IOException {
		return admin_getAllocation( "notifications_per_day" );
	}

	public int admin_getRequestAllocation() throws FacebookException, IOException {
		return admin_getAllocation( "requests_per_day" );
	}

	public T admin_getDailyMetrics( Set<Metric> metrics, Date start, Date end ) throws FacebookException, IOException {
		return admin_getDailyMetrics( metrics, start.getTime(), end.getTime() );
	}

	public T admin_getDailyMetrics( Set<Metric> metrics, long start, long end ) throws FacebookException, IOException {
		int size = 2 + ( ( metrics != null ) ? metrics.size() : 0 );
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( size );
		if ( metrics != null ) {
			metrics.remove( Metric.ACTIVE_USERS );
			if ( !metrics.isEmpty() ) {
				JSONArray metricsJson = new JSONArray();
				for ( Metric metric : metrics ) {
					metricsJson.put( metric.getName() );
				}
				params.add( newPair( "metrics", metricsJson.toString() ) );
			}
		}
		params.add( newPair( "start_date", ( start / 1000 ) ) );
		params.add( newPair( "end_date", ( end / 1000 ) ) );
		return callMethod( FacebookMethod.ADMIN_GET_DAILY_METRICS, params );
	}

	public T permissions_checkGrantedApiAccess( String apiKey ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PERM_CHECK_GRANTED_API_ACCESS, newPair( "permissions_apikey", apiKey ) );
	}

	public T permissions_checkAvailableApiAccess( String apiKey ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PERM_CHECK_AVAILABLE_API_ACCESS, newPair( "permissions_apikey", apiKey ) );
	}

	public boolean permissions_grantApiAccess( String apiKey, Set<FacebookMethod> methods ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( ( methods != null ) && ( !methods.isEmpty() ) ) {
			JSONArray methodsJson = new JSONArray();
			for ( FacebookMethod method : methods ) {
				methodsJson.put( method.methodName() );
			}
			params.add( newPair( "method_arr", methodsJson.toString() ) );
		}
		params.add( newPair( "permissions_apikey", apiKey ) );
		return extractBoolean( callMethod( FacebookMethod.PERM_GRANT_API_ACCESS, params ) );
	}

	public boolean permissions_grantFullApiAccess( String apiKey ) throws FacebookException, IOException {
		return permissions_grantApiAccess( apiKey, null );
	}

	public boolean permissions_revokeApiAccess( String apiKey ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.PERM_REVOKE_API_ACCESS, newPair( "permissions_apikey", apiKey ) ) );
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
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 4 );
		params.add( newPair( "listing_id", listingId.toString() ) );
		if ( showOnProfile ) {
			params.add( newPair( "show_on_profile", "true" ) );
		}
		params.add( newPair( "listing_attrs", attributes ) );
		params.add( newPair( "uid", listingId ) );
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
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 3 );
		params.add( newPair( "listing_id", listingId ) );
		params.add( newPair( "status", status.getName() ) );
		params.add( newPair( "uid", userId ) );
		return marketplace_removeListing( FacebookMethod.MARKET_REMOVE_LISTING_NOSESSION, params );
	}

	private boolean photos_addTag( Long photoId, Double xPct, Double yPct, Long taggedUserId, CharSequence tagText, Long userId ) throws FacebookException, IOException {
		assert ( null != photoId && !photoId.equals( 0 ) );
		assert ( null != taggedUserId || null != tagText );
		assert ( null != xPct && xPct >= 0 && xPct <= 100 );
		assert ( null != yPct && yPct >= 0 && yPct <= 100 );
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 5 );
		if ( taggedUserId != null ) {
			params.add( newPair( "tag_uid", taggedUserId.toString() ) );
		} else {
			params.add( newPair( "tag_text", tagText.toString() ) );
		}
		params.add( newPair( "x", xPct.toString() ) );
		params.add( newPair( "y", yPct.toString() ) );
		params.add( newPair( "pid", photoId ) );
		params.add( newPair( "owner_uid", userId ) );
		return photos_addTag( FacebookMethod.PHOTOS_ADD_TAG_NOSESSION, params );
	}

	public boolean photos_addTag( Long photoId, Long taggedUserId, Double pct, Double pct2, Long userId ) throws FacebookException, IOException {
		return photos_addTag( photoId, pct, pct2, taggedUserId, null, userId );
	}

	public boolean photos_addTag( Long photoId, CharSequence tagText, Double pct, Double pct2, Long userId ) throws FacebookException, IOException {
		return photos_addTag( photoId, pct, pct2, null, tagText );
	}

	public T photos_createAlbum( String albumName, Long userId ) throws FacebookException, IOException {
		return photos_createAlbum( albumName, null, null, userId );
	}

	public T photos_createAlbum( String name, String description, String location, Long userId ) throws FacebookException, IOException {
		assert ( null != name && !"".equals( name ) );
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_CREATE_ALBUM.numParams() );
		params.add( newPair( "name", name ) );
		if ( null != description ) {
			params.add( newPair( "description", description ) );
		}
		if ( null != location ) {
			params.add( newPair( "location", location ) );
		}
		params.add( newPair( "uid", userId ) );
		return photos_createAlbum( FacebookMethod.PHOTOS_CREATE_ALBUM_NOSESSION, params );
	}

	public T photos_upload( Long userId, File photo ) throws FacebookException, IOException {
		return photos_upload( userId, photo, null, null );
	}

	public T photos_upload( Long userId, File photo, String caption ) throws FacebookException, IOException {
		return photos_upload( userId, photo, caption, null );
	}

	public T photos_upload( Long userId, File photo, Long albumId ) throws FacebookException, IOException {
		return photos_upload( userId, photo, null, albumId );
	}

	public T photos_upload( Long userId, File photo, String caption, Long albumId ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( FacebookMethod.PHOTOS_UPLOAD.numParams() );
		assert ( photo.exists() && photo.canRead() );
		this._uploadFile = photo;
		if ( null != albumId ) {
			params.add( newPair( "aid", albumId ) );
		}
		if ( null != caption ) {
			params.add( newPair( "caption", caption ) );
		}
		params.add( newPair( "uid", userId ) );
		return photos_upload( FacebookMethod.PHOTOS_UPLOAD_NOSESSION, params );
	}

	public T profile_getFBML() throws FacebookException, IOException {
		return callMethod( FacebookMethod.PROFILE_GET_FBML );
	}

	public boolean users_hasAppPermission( Permission perm, Long userId ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.USERS_HAS_PERMISSION_NOSESSION, newPair( "ext_perm", perm.getName() ), newPair( "uid", Long.toString( userId ) ) ) );
	}

	public boolean users_isAppAdded( Long userId ) throws FacebookException, IOException {
		return extractBoolean( callMethod( FacebookMethod.USERS_IS_APP_ADDED_NOSESSION, newPair( "uid", userId ) ) );
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
			params.add( newPair( "status", newStatus ) );
		}
		if ( clear ) {
			params.add( newPair( "clear", "true" ) );
		}
		if ( statusIncludesVerb ) {
			params.add( newPair( "status_includes_verb", "true" ) );
		}
		params.add( newPair( "uid", userId.toString() ) );

		return users_setStatus( FacebookMethod.USERS_SET_STATUS_NOSESSION, params );
	}

	public T feed_getRegisteredTemplateBundleByID( Long id ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.FEED_GET_TEMPLATE_BY_ID, newPair( "template_bundle_id", id ) );
	}

	public T feed_getRegisteredTemplateBundles() throws FacebookException, IOException {
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
		params.add( newPair( "one_line_story_templates", templateArray.toString() ) );
		if ( shortTemplates != null && !shortTemplates.isEmpty() ) {
			JSONArray shortArray = new JSONArray();
			for ( BundleStoryTemplate template : shortTemplates ) {
				shortArray.put( template.toJson() );
			}
			params.add( newPair( "short_story_templates", shortArray.toString() ) );
		}
		if ( longTemplate != null ) {
			params.add( newPair( "full_story_template", longTemplate.toJsonString() ) );
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

	public T profile_getInfo( Long userId ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PROFILE_GET_INFO, newPair( "uid", userId ) );
	}

	public T profile_getInfoOptions( String field ) throws FacebookException, IOException {
		return callMethod( FacebookMethod.PROFILE_GET_INFO_OPTIONS, newPair( "field", field ) );
	}

	public void profile_setInfo( Long userId, String title, boolean textOnly, List<ProfileInfoField> fields ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		JSONArray json = new JSONArray();
		params.add( newPair( "uid", userId ) );
		params.add( newPair( "title", title ) );
		if ( textOnly ) {
			params.add( newPair( "type", "1" ) );
		} else {
			params.add( newPair( "type", "5" ) );
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
		params.add( newPair( "info_fields", json.toString() ) );
		callMethod( FacebookMethod.PROFILE_SET_INFO, params );
	}

	public void profile_setInfoOptions( ProfileInfoField field ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 2 );
		addParam( "field", field.getFieldName(), params );
		JSONArray json = new JSONArray();
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
		addParam( "options", json.toString(), params );
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
	public T photos_addTags( Long photoId, Collection<PhotoTag> tags, Long userId ) throws FacebookException, IOException {
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
		return callMethod( FacebookMethod.PHOTOS_ADD_TAG_NOSESSION, newPair( "pid", photoId.toString() ), newPair( "tags", tagStr ), newPair( "uid", Long
				.toString( userId ) ) );
	}

	private Long marketplace_createListing( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		return extractLong( callMethod( method, params ) );
	}

	private boolean marketplace_removeListing( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		return extractBoolean( callMethod( method, params ) );
	}

	private boolean photos_addTag( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		T d = callMethod( method, params );
		return extractBoolean( d );
	}

	private T photos_createAlbum( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		return callMethod( method, params );
	}

	private T photos_upload( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		return callMethod( method, params );
	}

	private boolean users_setStatus( IFacebookMethod method, Collection<Pair<String,CharSequence>> params ) throws FacebookException, IOException {
		return extractBoolean( callMethod( method, params ) );
	}

	public static boolean addParam( String name, Long value, Collection<Pair<String,CharSequence>> params ) {
		params.add( newPair( name, value ) );
		return true;
	}

	public static boolean addParamIfNotBlank( String name, Long value, Collection<Pair<String,CharSequence>> params ) {
		if ( value != null ) {
			return addParam( name, value, params );
		}
		return false;
	}

	public static boolean addParam( String name, CharSequence value, Collection<Pair<String,CharSequence>> params ) {
		params.add( newPair( name, value ) );
		return true;
	}

	public static boolean addParamIfNotBlank( String name, CharSequence value, Collection<Pair<String,CharSequence>> params ) {
		if ( ( value != null ) && ( !"".equals( value ) ) ) {
			params.add( newPair( name, value ) );
			return true;
		}
		return false;
	}

	private static Pair<String,CharSequence> newPair( String name, CharSequence value ) {
		return new Pair<String,CharSequence>( name, value );
	}

	private static Pair<String,CharSequence> newPair( String name, Long value ) {
		return new Pair<String,CharSequence>( name, Long.toString( value ) );
	}

	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml ) throws FacebookException, IOException {
		return profile_setFBML( userId, profileFbml, actionFbml, mobileFbml, null );
	}

	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml, String profileMain ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 5 );
		addParam( "uid", userId, params );
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

	public Boolean liveMessage_send( Long recipient, String eventName, JSONObject message ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>( 3 );
		params.add( newPair( "uid", recipient ) );
		params.add( newPair( "event_name", eventName ) );
		params.add( newPair( "message", message.toString() ) );
		return extractBoolean( callMethod( FacebookMethod.LIVEMESSAGE_SEND, params ) );
	}

	public T admin_getMetrics( Set<Metric> metrics, Date start, Date end, long period ) throws FacebookException, IOException {
		return admin_getMetrics( metrics, start.getTime(), end.getTime(), period );
	}

	public T admin_getMetrics( Set<Metric> metrics, long start, long end, long period ) throws FacebookException, IOException {
		List<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		if ( metrics != null ) {
			metrics.remove( Metric.DAILY_ACTIVE_USERS );
			if ( !metrics.isEmpty() ) {
				JSONArray metricsJson = new JSONArray();
				for ( Metric metric : metrics ) {
					metricsJson.put( metric.getName() );
				}
				params.add( newPair( "metrics", metricsJson.toString() ) );
			}
		}
		params.add( newPair( "start_time", ( start / 1000 ) ) );
		params.add( newPair( "end_time", ( end / 1000 ) ) );
		params.add( newPair( "period", ( period ) ) );
		return callMethod( FacebookMethod.ADMIN_GET_METRICS, params );
	}
	
	public boolean feed_deactivateTemplateBundleByID( Long bundleId ) throws FacebookException, IOException {
		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String, CharSequence>("template_bundle_id", Long.toString( bundleId )) );
		return extractBoolean(this.callMethod( FacebookMethod.FEED_DEACTIVATE_TEMPLATE_BUNDLE, params ));
	}

	public void notifications_send( Collection<Long> recipientIds, String notification, boolean isAppToUser ) throws FacebookException, IOException {
		if ( null == notification || "".equals( notification ) ) {
			throw new FacebookException( ErrorCode.GEN_INVALID_PARAMETER, "You cannot send an empty notification!" );
		}
		Pair<String, CharSequence> type = new Pair<String, CharSequence>("type", isAppToUser ? "app_to_user" : "user_to_user");
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
	public Boolean feed_publishUserAction(Long bundleId, 
									      Map<String,String> templateData,
									      List<IFeedImage> images,
									      List<Long> targetIds, 
									      String bodyGeneral) 
		throws FacebookException, IOException {
		
		// validate maximum of 4 images
		if (images != null && images.size() > 4) {
			throw new IllegalArgumentException("Maximum of 4 images allowed per feed item.");
		}
		
 		Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>();
		params.add( new Pair<String,CharSequence>("template_bundle_id", Long.toString(bundleId)));
		
		if (targetIds != null && !targetIds.isEmpty()) {
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
					throw new RuntimeException("Error constructing JSON object", exception);
 				}
 			}
 		}
 
		/*
		 * Associate images to "images" label in the form of: 
		 * 
		 * "images":[{"src":"http:\/\/www.facebook.com\/images\/image1.gif",
		 * 		      "href":"http:\/\/www.facebook.com"},
		 * 		     {"src":"http:\/\/www.facebook.com\/images\/image2.gif",
		 * 			  "href":"http:\/\/www.facebook.com"}] 
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
				jsonTemplateData.put("images", jsonArray);
			}
			catch ( Exception exception ) {
				throw new RuntimeException("Error constructing JSON object", exception);
			}
		}

		// associate to param
		if (jsonTemplateData.length() > 0) {
			params.add( new Pair<String,CharSequence>( "template_data", 
													   jsonTemplateData.toString() ) );
		}
		
 		return extractBoolean( callMethod( FacebookMethod.FEED_PUBLISH_USER_ACTION, params ) );
 	}
}
