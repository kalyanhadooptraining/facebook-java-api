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
 * Base class for interacting with the Facebook Application Programming Interface (API).
 * Most Facebook API methods map directly to function calls of this class.
 * <br/>
 * Instances of FacebookRestClient should be initialized via calls to
 * {@link #auth_createToken}, followed by {@link #auth_getSession}.
 * <br/>
 * For continually updated documentation, please refer to the
 * <a href="http://wiki.developers.facebook.com/index.php/API">
 * Developer Wiki</a>.
 */
public abstract class ExtensibleClient<T>
  implements IFacebookRestClient<T> {

  public static URL SERVER_URL = null;
  public static URL HTTPS_SERVER_URL = null;
  static {
    try {
      SERVER_URL = new URL(SERVER_ADDR);
      HTTPS_SERVER_URL = new URL(HTTPS_SERVER_ADDR);
    } catch (MalformedURLException e) {
      System.err.println("MalformedURLException: " + e.getMessage());
      System.exit(1);
    }
  }
  
  protected final String _secret;
  protected final String _apiKey;
  protected final URL _serverUrl;
  protected String rawResponse;
  protected Long _expires;
  protected int _timeout;
  protected boolean batchMode;
  protected List<BatchQuery> queries;

  protected String _sessionKey;
  protected boolean _isDesktop = false;
  protected long _userId = -1;

  /** 
   * filled in when session is established
   * only used for desktop apps
   */
  protected String _sessionSecret;

  /**
   * The number of parameters required for every request.
   * @see #callMethod(IFacebookMethod,Collection)
   */
  public static int NUM_AUTOAPPENDED_PARAMS = 6;

  private static boolean DEBUG = false;
  protected Boolean _debug = null;

  protected File _uploadFile = null;
  protected static final String CRLF = "\r\n";
  protected static final String PREF = "--";
  protected static final int UPLOAD_BUFFER_SIZE = 512;

  public static final String MARKETPLACE_STATUS_DEFAULT     = "DEFAULT";
  public static final String MARKETPLACE_STATUS_NOT_SUCCESS = "NOT_SUCCESS";
  public static final String MARKETPLACE_STATUS_SUCCESS     = "SUCCESS";


  protected ExtensibleClient(URL serverUrl, String apiKey, String secret, String sessionKey) {
    _sessionKey = sessionKey;
    _apiKey = apiKey;
    _secret = secret;
    _serverUrl = (null != serverUrl) ? serverUrl : SERVER_URL;
    _timeout = -1;
    batchMode = false;
    queries = new ArrayList<BatchQuery>();
  }
  
  protected ExtensibleClient(URL serverUrl, String apiKey, String secret, String sessionKey, int timeout) {
      this(serverUrl, apiKey, secret, sessionKey);
      _timeout = timeout;
    }

  /**
   * The response format in which results to FacebookMethod calls are returned
   * @return the format: either XML, JSON, or null (API default)
   */
  public String getResponseFormat() {
    return null;
  }

  /**
   * Retrieves whether two users are friends.
   * @param userId1 
   * @param userId2
   * @return T
   * @see <a href="http://wiki.developers.facebook.com/index.php/Friends.areFriends">
   *      Developers Wiki: Friends.areFriends</a>
   */
  public T friends_areFriends(long userId1, long userId2)
    throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.FRIENDS_ARE_FRIENDS,
                           new Pair<String, CharSequence>("uids1", Long.toString(userId1)),
                           new Pair<String, CharSequence>("uids2", Long.toString(userId2)));
  }

  /**
   * Retrieves whether pairs of users are friends.
   * Returns whether the first user in <code>userIds1</code> is friends with the first user in
   * <code>userIds2</code>, the second user in <code>userIds1</code> is friends with the second user in
   * <code>userIds2</code>, etc.
   * @param userIds1
   * @param userIds2
   * @return T
   * @throws IllegalArgumentException if one of the collections is null, or empty, or if the 
   *         collection sizes differ.
   * @see <a href="http://wiki.developers.facebook.com/index.php/Friends.areFriends">
   *      Developers Wiki: Friends.areFriends</a>
   */
  public T friends_areFriends(Collection<Long> userIds1, Collection<Long> userIds2)
    throws FacebookException, IOException {
    if (userIds1 == null || userIds2 == null || userIds1.isEmpty() || userIds2.isEmpty()) {
      throw new IllegalArgumentException("Collections passed to friends_areFriends should not be null or empty");
    }
    if (userIds1.size() != userIds2.size()) {
      throw new IllegalArgumentException(String.format("Collections should be same size: got userIds1: %d elts; userIds2: %d elts",
                                                       userIds1.size(), userIds2.size()));
    }

    return this.callMethod(FacebookMethod.FRIENDS_ARE_FRIENDS,
                           new Pair<String, CharSequence>("uids1", delimit(userIds1)),
                           new Pair<String, CharSequence>("uids2", delimit(userIds2)));
  }

  /**
   * Gets the FBML for a user's profile, including the content for both the profile box
   * and the profile actions.
   * @param userId - the user whose profile FBML to set
   * @return a T containing FBML markup
   */
  public T profile_getFBML(Long userId)
    throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.PROFILE_GET_FBML,
                           new Pair<String, CharSequence>("uid", Long.toString(userId)));

  }

  /**
   * Recaches the referenced url.
   * @param url string representing the URL to refresh
   * @return boolean indicating whether the refresh succeeded
   */
  public boolean fbml_refreshRefUrl(String url)
    throws FacebookException, IOException {
    return fbml_refreshRefUrl(new URL(url));
  }

  /**
   * Helper function: assembles the parameters used by feed_publishActionOfUser and
   * feed_publishStoryToUser
   * @param feedMethod feed_publishStoryToUser / feed_publishActionOfUser
   * @param title title of the story
   * @param body body of the story
   * @param images optional images to be included in he story
   * @param priority
   * @return whether the call to <code>feedMethod</code> was successful
   */
  protected boolean feedHandler(IFacebookMethod feedMethod, CharSequence title, CharSequence body,
                                Collection<? extends IPair<? extends Object, URL>> images, Integer priority)
    throws FacebookException, IOException {
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(feedMethod.numParams());

    params.add(new Pair<String, CharSequence>("title", title));
    if (null != body)
      params.add(new Pair<String, CharSequence>("body", body));
    if (null != priority)
      params.add(new Pair<String, CharSequence>("priority", priority.toString()));

    handleFeedImages(params, images);

    return extractBoolean(this.callMethod(feedMethod, params));
  }

  /**
   * Adds image parameters 
   * @param params
   * @param images
   */
  protected void handleFeedImages(List<Pair<String, CharSequence>> params, Collection<? extends IPair<? extends Object, URL>> images) {
    if (images != null && images.size() > 4) {
      throw new IllegalArgumentException("At most four images are allowed, got " + Integer.toString(images.size()));
    }
    if (null != images && !images.isEmpty()) {
      int image_count = 0;
      for (IPair image : images) {
        ++image_count;
        assert null != image.getFirst() : "Image URL must be provided";
        params.add(new Pair<String, CharSequence>(String.format("image_%d", image_count),
                                                  image.getFirst().toString()));
        if (null != image.getSecond())
          params.add(new Pair<String, CharSequence>(String.format("image_%d_link", image_count),
                                                    image.getSecond().toString()));
      }
    }
  }

  /**
   * Publish the notification of an action taken by a user to newsfeed.
   * @param title the title of the feed story (up to 60 characters, excluding tags)
   * @param body (optional) the body of the feed story (up to 200 characters, excluding tags)
   * @param images (optional) up to four pairs of image URLs and (possibly null) link URLs
   * @return whether the story was successfully published; false in case of permission error
   * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishActionOfUser">
   *      Developers Wiki: Feed.publishActionOfUser</a>
   */
  public boolean feed_publishActionOfUser(CharSequence title, CharSequence body,
                                          Collection<? extends IPair<? extends Object, URL>> images)
    throws FacebookException, IOException {
    return feedHandler(FacebookMethod.FEED_PUBLISH_ACTION_OF_USER, title, body, images, null);
  }

  /**
   * Publish the notification of an action taken by a user to newsfeed.
   * @param title the title of the feed story (up to 60 characters, excluding tags)
   * @param body (optional) the body of the feed story (up to 200 characters, excluding tags)
   * @return whether the story was successfully published; false in case of permission error
   * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishActionOfUser">
   *      Developers Wiki: Feed.publishActionOfUser</a>
   */
  public boolean feed_publishActionOfUser(CharSequence title, CharSequence body)
    throws FacebookException, IOException {
    return feed_publishActionOfUser(title, body, null);
  }
  
  /**
   * Call this function to retrieve the session information after your user has
   * logged in.
   * @param authToken the token returned by auth_createToken or passed back to your callback_url.
   */
  public abstract String auth_getSession(String authToken)
    throws FacebookException, IOException;

  /**
   * Publish a story to the logged-in user's newsfeed.
   * @param title the title of the feed story
   * @param body the body of the feed story
   * @return whether the story was successfully published; false in case of permission error
   * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser">
   *      Developers Wiki: Feed.publishStoryToUser</a>
   */
  public boolean feed_publishStoryToUser(CharSequence title, CharSequence body)
    throws FacebookException, IOException {
    return feed_publishStoryToUser(title, body, null, null);
  }

  /**
   * Publish a story to the logged-in user's newsfeed.
   * @param title the title of the feed story
   * @param body the body of the feed story
   * @param images (optional) up to four pairs of image URLs and (possibly null) link URLs
   * @return whether the story was successfully published; false in case of permission error
   * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser">
   *      Developers Wiki: Feed.publishStoryToUser</a>
   */
  public boolean feed_publishStoryToUser(CharSequence title, CharSequence body,
                                         Collection<? extends IPair<? extends Object, URL>> images)
    throws FacebookException, IOException {
    return feed_publishStoryToUser(title, body, images, null);
  }

  /**
   * Publish a story to the logged-in user's newsfeed.
   * @param title the title of the feed story
   * @param body the body of the feed story
   * @param priority
   * @return whether the story was successfully published; false in case of permission error
   * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser">
   *      Developers Wiki: Feed.publishStoryToUser</a>
   */
  public boolean feed_publishStoryToUser(CharSequence title, CharSequence body, Integer priority)
    throws FacebookException, IOException {
    return feed_publishStoryToUser(title, body, null, priority);
  }

  /**
   * Publish a story to the logged-in user's newsfeed.
   * @param title the title of the feed story
   * @param body the body of the feed story
   * @param images (optional) up to four pairs of image URLs and (possibly null) link URLs
   * @param priority
   * @return whether the story was successfully published; false in case of permission error
   * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser">
   *      Developers Wiki: Feed.publishStoryToUser</a>
   */
  public boolean feed_publishStoryToUser(CharSequence title, CharSequence body,
                                   Collection<? extends IPair<? extends Object, URL>> images, Integer priority)
    throws FacebookException, IOException {
    return feedHandler(FacebookMethod.FEED_PUBLISH_STORY_TO_USER, title, body, images, priority);
  }

  /**
   * Publishes a Mini-Feed story describing an action taken by a user, and
   * publishes aggregating News Feed stories to the friends of that user.
   * Stories are identified as being combinable if they have matching templates and substituted values.
   * @param actorId the user into whose mini-feed the story is being published.
   * @param titleTemplate markup (up to 60 chars, tags excluded) for the feed story's title
   *        section. Must include the token <code>{actor}</code>.
   * @return whether the action story was successfully published; false in case
   *         of a permission error
   * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction">
   *      Developers Wiki: Feed.publishTemplatizedAction</a> 
   */
  public boolean feed_publishTemplatizedAction(Long actorId, CharSequence titleTemplate)
    throws FacebookException, IOException {
    return feed_publishTemplatizedAction(actorId, titleTemplate, null, null, null, null, null, null );    
  }

  /**
   * Publishes a Mini-Feed story describing an action taken by a user, and
   * publishes aggregating News Feed stories to the friends of that user.
   * Stories are identified as being combinable if they have matching templates and substituted values.
   * @param actorId the user into whose mini-feed the story is being published.
   * @param titleTemplate markup (up to 60 chars, tags excluded) for the feed story's title
   *        section. Must include the token <code>{actor}</code>.
   * @param titleData (optional) contains token-substitution mappings for tokens that appear in
   *        titleTemplate. Should not contain mappings for the <code>{actor}</code> or 
   *        <code>{target}</code> tokens. Required if tokens other than <code>{actor}</code> 
   *        or <code>{target}</code> appear in the titleTemplate. 
   * @param bodyTemplate (optional) markup to be displayed in the feed story's body section.
   *        can include tokens, of the form <code>{token}</code>, to be substituted using
   *        bodyData.
   * @param bodyData (optional) contains token-substitution mappings for tokens that appear in
   *        bodyTemplate. Required if the bodyTemplate contains tokens other than <code>{actor}</code>
   *        and <code>{target}</code>.
   * @param bodyGeneral (optional) additional body markup that is not aggregated. If multiple instances
   *        of this templated story are combined together, the markup in the bodyGeneral of
   *        one of their stories may be displayed.
   * @param targetIds The user ids of friends of the actor, used for stories about a direct action between 
   *        the actor and these targets of his/her action. Required if either the titleTemplate or bodyTemplate
   *        includes the token <code>{target}</code>.
   * @param images (optional) additional body markup that is not aggregated. If multiple instances
   *        of this templated story are combined together, the markup in the bodyGeneral of
   *        one of their stories may be displayed.
   * @return whether the action story was successfully published; false in case
   *         of a permission error
   * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction">
   *      Developers Wiki: Feed.publishTemplatizedAction</a>
   */
  public boolean feed_publishTemplatizedAction(Long actorId, CharSequence titleTemplate,
                                               Map<String,CharSequence> titleData, 
                                               CharSequence bodyTemplate,
                                               Map<String,CharSequence> bodyData,
                                               CharSequence bodyGeneral,
                                               Collection<Long> targetIds,
                                               Collection<? extends IPair<? extends Object, URL>> images
                                              )
    throws FacebookException, IOException {
    return this.feed_publishTemplatizedAction(titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images, null);
  }
  
  /**
   * @deprecated Use the version that takes a Long for the actorId paramter.
   */
  public boolean feed_publishTemplatizedAction(Integer actorId, CharSequence titleTemplate,
          Map<String,CharSequence> titleData, 
          CharSequence bodyTemplate,
          Map<String,CharSequence> bodyData,
          CharSequence bodyGeneral,
          Collection<Long> targetIds,
          Collection<? extends IPair<? extends Object, URL>> images
         )
  throws FacebookException, IOException {
      return this.feed_publishTemplatizedAction((long)(actorId.intValue()), titleTemplate, 
              titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images);
  }


  /**
   * Retrieves the membership list of a group
   * @param groupId the group id
   * @return a T containing four membership lists of
   * 'members', 'admins', 'officers', and 'not_replied'
   */
  public T groups_getMembers(Number groupId)
    throws FacebookException, IOException {
    assert (null != groupId);
    return this.callMethod(FacebookMethod.GROUPS_GET_MEMBERS,
                           new Pair<String, CharSequence>("gid", groupId.toString()));
  }

  private static String encode(CharSequence target) {
    if (target == null) {
        return "";
    }
    String result = target.toString();
    try {
      result = URLEncoder.encode(result, "UTF8");
    } catch (UnsupportedEncodingException e) {
      System.err.printf("Unsuccessful attempt to encode '%s' into UTF8", result);
    }
    return result;
  }

  /**
   * Retrieves the membership list of an event
   * @param eventId event id
   * @return T consisting of four membership lists corresponding to RSVP status, with keys
   * 'attending', 'unsure', 'declined', and 'not_replied'
   */
  public T events_getMembers(Number eventId)
    throws FacebookException, IOException {
    assert (null != eventId);
    return this.callMethod(FacebookMethod.EVENTS_GET_MEMBERS,
                           new Pair<String, CharSequence>("eid", eventId.toString()));
  }

  /**
   * Retrieves the friends of the currently logged in user, who are also users
   * of the calling application.
   * @return array of friends
   */
  public T friends_getAppUsers()
    throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.FRIENDS_GET_APP_USERS);
  }

  /**
   * Retrieves the results of a Facebook Query Language query
   * @param query : the FQL query statement
   * @return varies depending on the FQL query
   */
  public T fql_query(CharSequence query)
    throws FacebookException, IOException {
    assert (null != query);
    return this.callMethod(FacebookMethod.FQL_QUERY,
                           new Pair<String, CharSequence>("query", query));
  }

  private String generateSignature(List<String> params, boolean requiresSession) {
    String secret = (isDesktop() && requiresSession) ? this._sessionSecret : this._secret;
    return FacebookSignatureUtil.generateSignature(params, secret);
  }

  public static void setDebugAll(boolean isDebug) {
    ExtensibleClient.DEBUG = isDebug;
  }

  private static CharSequence delimit(Collection iterable) {
    // could add a thread-safe version that uses StringBuffer as well
    if (iterable == null || iterable.isEmpty())
      return null;

    StringBuilder buffer = new StringBuilder();
    boolean notFirst = false;
    for (Object item : iterable) {
      if (notFirst)
        buffer.append(",");
      else
        notFirst = true;
      buffer.append(item.toString());
    }
    return buffer;
  }

  /**
   * Call the specified method, with the given parameters, and return a DOM tree with the results.
   *
   * @param method the fieldName of the method
   * @param paramPairs a list of arguments to the method
   * @throws Exception with a description of any errors given to us by the server.
   */
  protected T callMethod(IFacebookMethod method, Pair<String, CharSequence>... paramPairs)
    throws FacebookException, IOException {
    return callMethod(method, Arrays.asList(paramPairs));
  }

  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param photoIds retrieve from this list of photos (optional)
   * @return an T of photo objects.
   * @see #photos_get(Long, Long, Collection)
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get">
   *      Developers Wiki: Photos.get</a>
   */
  public T photos_get(Collection<Long> photoIds)
    throws FacebookException, IOException {
    return photos_get(null /*subjId*/, null /*albumId*/, photoIds);
  }

  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param subjId retrieve from photos associated with this user (optional).
   * @param albumId retrieve from photos from this album (optional)
   * @return an T of photo objects.
   * @see #photos_get(Long, Long, Collection)
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get">
   *      Developers Wiki: Photos.get</a> 
   */
  public T photos_get(Long subjId, Long albumId)
    throws FacebookException, IOException {
    return photos_get(subjId, albumId, null /*photoIds*/);
  }

  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param subjId retrieve from photos associated with this user (optional).
   * @param photoIds retrieve from this list of photos (optional)
   * @return an T of photo objects.
   * @see #photos_get(Long, Long, Collection)
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get">
   *      Developers Wiki: Photos.get</a> 
   */
  public T photos_get(Long subjId, Collection<Long> photoIds)
    throws FacebookException, IOException {
    return photos_get(subjId, null /*albumId*/, photoIds);
  }

  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param subjId retrieve from photos associated with this user (optional).
   * @return an T of photo objects.
   * @see #photos_get(Long, Long, Collection)
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get">
   *      Developers Wiki: Photos.get</a> 
   */
  public T photos_get(Long subjId)
    throws FacebookException, IOException {
    return photos_get(subjId, null /*albumId*/, null /*photoIds*/); 
  }

  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param subjId retrieve from photos associated with this user (optional).
   * @param albumId retrieve from photos from this album (optional)
   * @param photoIds retrieve from this list of photos (optional)
   * @return an T of photo objects.
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get">
   *      Developers Wiki: Photos.get</a> 
   */
  public T photos_get(Long subjId, Long albumId, Collection<Long> photoIds)
    throws FacebookException, IOException {
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.PHOTOS_GET.numParams());

    boolean hasUserId = null != subjId && 0 != subjId;
    boolean hasAlbumId = null != albumId && 0 != albumId;
    boolean hasPhotoIds = null != photoIds && !photoIds.isEmpty();
    if (!hasUserId && !hasAlbumId && !hasPhotoIds) {
      throw new IllegalArgumentException("At least one of photoIds, albumId, or subjId must be provided");
    }

    if (hasUserId)
      params.add(new Pair<String, CharSequence>("subj_id", Long.toString(subjId)));
    if (hasAlbumId)
      params.add(new Pair<String, CharSequence>("aid", Long.toString(albumId)));
    if (hasPhotoIds)
      params.add(new Pair<String, CharSequence>("pids", delimit(photoIds)));

    return this.callMethod(FacebookMethod.PHOTOS_GET, params);
  }

  /**
   * Retrieves the requested info fields for the requested set of users.
   * @param userIds a collection of user IDs for which to fetch info
   * @param fields a set of strings describing the info fields desired, such as "last_name", "sex"
   * @return a T consisting of a list of users, with each user element
   * containing the requested fields.
   */
  public T users_getInfo(Collection<Long> userIds, Set<CharSequence> fields)
    throws FacebookException, IOException {
    // assertions test for invalid params
    if (null == userIds) {
      throw new IllegalArgumentException("userIds cannot be null");
    }
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("fields should not be empty");
    }

    return this.callMethod(FacebookMethod.USERS_GET_INFO,
                           new Pair<String, CharSequence>("uids", delimit(userIds)),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }

  /**
   * Retrieves the tags for the given set of photos.
   * @param photoIds The list of photos from which to extract photo tags.
   * @return the created album
   */
  public T photos_getTags(Collection<Long> photoIds)
    throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.PHOTOS_GET_TAGS,
                           new Pair<String, CharSequence>("pids", delimit(photoIds)));
  }

  /**
   * Retrieves the groups associated with a user
   * @param userId Optional: User associated with groups.
   * A null parameter will default to the session user.
   * @param groupIds Optional: group ids to query.
   * A null parameter will get all groups for the user.
   * @return array of groups
   */
  public T groups_get(Long userId, Collection<Long> groupIds)
    throws FacebookException, IOException {
    boolean hasGroups = (null != groupIds && !groupIds.isEmpty());
    if (null != userId)
      return hasGroups ?
             this.callMethod(FacebookMethod.GROUPS_GET, new Pair<String, CharSequence>("uid",
                                                                                       userId.toString()),
                             new Pair<String, CharSequence>("gids", delimit(groupIds))) :
             this.callMethod(FacebookMethod.GROUPS_GET,
                             new Pair<String, CharSequence>("uid", userId.toString()));
    else
      return hasGroups ?
             this.callMethod(FacebookMethod.GROUPS_GET, new Pair<String, CharSequence>("gids",
                                                                                       delimit(groupIds))) :
             this.callMethod(FacebookMethod.GROUPS_GET);
  }

  /**
   * Call the specified method, with the given parameters, and return a DOM tree with the results.
   *
   * @param method the fieldName of the method
   * @param paramPairs a list of arguments to the method
   * @throws Exception with a description of any errors given to us by the server.
   */
  protected T callMethod(IFacebookMethod method, Collection<Pair<String, CharSequence>> paramPairs)
    throws FacebookException, IOException {
    this.rawResponse = null;
    HashMap<String, CharSequence> params =
      new HashMap<String, CharSequence>(2 * method.numTotalParams());

    params.put("method", method.methodName());
    params.put("api_key", _apiKey);
    params.put("v", TARGET_API_VERSION);
    
    String format = getResponseFormat();
    if (null != format) {
      params.put("format", format);
    }
    
    if (method.requiresSession()) {
      params.put("call_id", Long.toString(System.currentTimeMillis()));
      params.put("session_key", _sessionKey);
    }
    CharSequence oldVal;
    for (Pair<String, CharSequence> p : paramPairs) {
      oldVal = params.put(p.first, p.second);
      if (oldVal != null)
        System.err.printf("For parameter %s, overwrote old value %s with new value %s.", p.first,
                          oldVal, p.second);
    }

    assert (!params.containsKey("sig"));
    String signature =
      generateSignature(FacebookSignatureUtil.convert(params.entrySet()), method.requiresSession());
    params.put("sig", signature);
    
    if (this.batchMode) {
        //if we are running in batch mode, don't actually execute the query now, just add it to the list
        boolean addToBatch = true;
        if (method.methodName().equals(FacebookMethod.USERS_GET_LOGGED_IN_USER.methodName())) {
            Exception trace = new Exception();
            StackTraceElement[] traceElems = trace.getStackTrace();
            int index = 0;
            for (StackTraceElement elem : traceElems) {
                if (elem.getMethodName().indexOf("_") != -1) {
                    StackTraceElement caller = traceElems[index + 1];
                    if ((caller.getClassName().equals(ExtensibleClient.class.getName())) && (! caller.getMethodName().startsWith("auth_"))) {
                        addToBatch = false;
                    }
                    break;
                }
                index++;
            }
        }
        if (addToBatch) {
            this.queries.add(new BatchQuery(method, params));
        }
        return null;
    }

    boolean doHttps = this.isDesktop() && FacebookMethod.AUTH_GET_SESSION.equals(method);
    
    InputStream data = method.takesFile()? postFileRequest(method.methodName(), params, /* doEncode */
            true): postRequest(method.methodName(), params, doHttps, /* doEncode */true);
    
    BufferedReader in = new BufferedReader(new InputStreamReader(data, "UTF-8"));
    StringBuffer buffer = new StringBuffer();
    String line;
    while ((line = in.readLine()) != null) {
      buffer.append(line);
    }
     
    String xmlResp = new String(buffer);
    this.rawResponse = xmlResp;
      
    return parseCallResult(new ByteArrayInputStream(xmlResp.getBytes("UTF-8")), method);
  }

  /**
   * Parses the result of an API call into a T.
   * @param data an InputStream with the results of a request to the Facebook servers
   * @param method the method called
   * @throws FacebookException if <code>data</code> represents an error
   * @throws IOException if <code>data</code> is not readable
   * @return a T
   */
  protected abstract T parseCallResult(InputStream data, IFacebookMethod method)
    throws FacebookException, IOException;

  /**
   * Recaches the referenced url.
   * @param url the URL to refresh
   * @return boolean indicating whether the refresh succeeded
   */
  public boolean fbml_refreshRefUrl(URL url)
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.FBML_REFRESH_REF_URL,
                                          new Pair<String, CharSequence>("url", url.toString())));
  }

  /**
   * Retrieves the outstanding notifications for the session user.
   * @return a T containing
   * notification count pairs for 'messages', 'pokes' and 'shares',
   * a uid list of 'friend_requests', a gid list of 'group_invites',
   * and an eid list of 'event_invites'
   */
  public T notifications_get()
    throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.NOTIFICATIONS_GET);
  }

  /**
   * Retrieves the requested info fields for the requested set of users.
   * @param userIds a collection of user IDs for which to fetch info
   * @param fields a set of ProfileFields
   * @return a T consisting of a list of users, with each user element
   * containing the requested fields.
   */
  public T users_getInfo(Collection<Long> userIds, EnumSet<ProfileField> fields)
    throws FacebookException, IOException {
    // assertions test for invalid params
    assert (userIds != null);
    assert (fields != null);
    assert (!fields.isEmpty());

    return this.callMethod(FacebookMethod.USERS_GET_INFO,
                           new Pair<String, CharSequence>("uids", delimit(userIds)),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }

  /**
   * Retrieves the user ID of the user logged in to this API session
   * @return the Facebook user ID of the logged-in user
   */
  public long users_getLoggedInUser() throws FacebookException, IOException {
    T result = this.callMethod(FacebookMethod.USERS_GET_LOGGED_IN_USER);
    return extractLong(result);
  }

  /**
   * Call this function to get the user ID.
   *
   * @return The ID of the current session's user, or -1 if none.
   */
  public long auth_getUserId(String authToken)
    throws FacebookException, IOException {
    /*
     * Get the session information if we don't have it; this will populate
       * the user ID as well.
       */
    if (null == this._sessionKey)
      auth_getSession(authToken);
    return this._userId;
  }

  public boolean isDesktop() {
    return this._isDesktop;
  }

  private boolean photos_addTag(Long photoId, Double xPct, Double yPct, Long taggedUserId,
                                CharSequence tagText)
    throws FacebookException, IOException {
    assert (null != photoId && !photoId.equals(0));
    assert (null != taggedUserId || null != tagText);
    assert (null != xPct && xPct >= 0 && xPct <= 100);
    assert (null != yPct && yPct >= 0 && yPct <= 100);
    Pair<String, CharSequence> tagData;
    if (taggedUserId != null) {
        tagData = new Pair<String, CharSequence>("tag_uid", taggedUserId.toString());
    }
    else {
        tagData = new Pair<String, CharSequence>("tag_text", tagText.toString());
    }
    T d =
      this.callMethod(FacebookMethod.PHOTOS_ADD_TAG, new Pair<String, CharSequence>("pid", photoId.toString()),
                      tagData,
                      new Pair<String, CharSequence>("x", xPct.toString()),
                      new Pair<String, CharSequence>("y", yPct.toString()));
    return extractBoolean(d);
  }

  /**
   * Retrieves an indicator of whether the logged-in user has installed the
   * application associated with the _apiKey.
   * @return boolean indicating whether the user has installed the app
   */
  public boolean users_isAppAdded()
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.USERS_IS_APP_ADDED));
  }

  /**
   * Retrieves whether the logged-in user has granted the specified permission
   * to this application.
   * @param permission an extended permission (e.g. FacebookExtendedPerm.MARKETPLACE,
   *        "photo_upload")
   * @return boolean indicating whether the user has the permission
   * @see FacebookExtendedPerm
   * @see <a href="http://wiki.developers.facebook.com/index.php/Users.hasAppPermission">
   *      Developers Wiki: Users.hasAppPermission</a> 
   */
  public boolean users_hasAppPermission(CharSequence permission)
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.USERS_HAS_APP_PERMISSION,
                                          new Pair<String, CharSequence>("ext_perm", permission)));
  }

  /**
   * Sets the logged-in user's Facebook status.
   * Requires the status_update extended permission.
   * @return whether the status was successfully set
   * @see #users_hasAppPermission
   * @see FacebookExtendedPerm#STATUS_UPDATE
   * @see <a href="http://wiki.developers.facebook.com/index.php/Users.setStatus">
   *      Developers Wiki: Users.setStatus</a> 
   */
  public boolean users_setStatus(String status)
    throws FacebookException, IOException {
    return this.users_setStatus(status, false, false);
  }

  /**
   * Clears the logged-in user's Facebook status.
   * Requires the status_update extended permission.
   * @return whether the status was successfully cleared
   * @see #users_hasAppPermission
   * @see FacebookExtendedPerm#STATUS_UPDATE
   * @see <a href="http://wiki.developers.facebook.com/index.php/Users.setStatus">
   *      Developers Wiki: Users.setStatus</a> 
   */
  public boolean users_clearStatus()
    throws FacebookException, IOException {
      return extractBoolean(this.callMethod(FacebookMethod.USERS_SET_STATUS,
              new Pair<String, CharSequence>("clear", "1")));
  }

  /**
   * Adds a tag to a photo.
   * @param photoId The photo id of the photo to be tagged.
   * @param xPct The horizontal position of the tag, as a percentage from 0 to 100, from the left of the photo.
   * @param yPct The list of photos from which to extract photo tags.
   * @param tagText The text of the tag.
   * @return whether the tag was successfully added.
   */
  public boolean photos_addTag(Long photoId, CharSequence tagText, Double xPct, Double yPct)
    throws FacebookException, IOException {
    return photos_addTag(photoId, xPct, yPct, null, tagText);
  }
  
  /**
   * Helper function for posting a request that includes raw file data, eg
   * {@link #photos_upload(File)}.
   * 
   * @param methodName the name of the method
   * @param params request parameters (not including the file)
   * @return an InputStream with the request response
   * @see #photos_upload(File)
   */
  protected InputStream postFileRequest(String methodName, Map<String,CharSequence> params)
          throws IOException {
      return postFileRequest(methodName, params, /* doEncode */true);
  }

  /**
   * Helper function for posting a request that includes raw file data, eg {@link #photos_upload}.
   * @param methodName the name of the method
   * @param params request parameters (not including the file)
   * @param doEncode whether to UTF8-encode the parameters
   * @return an InputStream with the request response  
   * @see #photos_upload
   */
  protected InputStream postFileRequest(String methodName, Map<String, CharSequence> params, boolean doEncode)
    throws IOException {
    assert (null != _uploadFile);
    try {
      BufferedInputStream bufin = new BufferedInputStream(new FileInputStream(_uploadFile));

      String boundary = Long.toString(System.currentTimeMillis(), 16);
      URLConnection con = SERVER_URL.openConnection();
      con.setDoInput(true);
      con.setDoOutput(true);
      con.setUseCaches(false);
      con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      con.setRequestProperty("MIME-version", "1.0");

      DataOutputStream out = new DataOutputStream(con.getOutputStream());

      for (Map.Entry<String, CharSequence> entry : params.entrySet()) {
        out.writeBytes(PREF + boundary + CRLF);
        out.writeBytes("Content-disposition: form-data; name=\"" + entry.getKey() + "\"");
        out.writeBytes(CRLF + CRLF);
        out.writeBytes(doEncode? encode(entry.getValue()): entry.getValue().toString());
        out.writeBytes(CRLF);
      }

      out.writeBytes(PREF + boundary + CRLF);
      out.writeBytes("Content-disposition: form-data; filename=\"" + _uploadFile.getName() + "\"" +
                     CRLF);
      out.writeBytes("Content-Type: image/jpeg" + CRLF);
      // out.writeBytes("Content-Transfer-Encoding: binary" + CRLF); // not necessary

      // Write the file
      out.writeBytes(CRLF);
      byte b[] = new byte[UPLOAD_BUFFER_SIZE];
      int byteCounter = 0;
      int i;
      while (-1 != (i = bufin.read(b))) {
        byteCounter += i;
        out.write(b, 0, i);
      }
      out.writeBytes(CRLF + PREF + boundary + PREF + CRLF);

      out.flush();
      out.close();

      InputStream is = con.getInputStream();
      return is;
    } catch (Exception e) {
      logException(e);
      return null;
    }
  }

  /**
   * Logs an exception with default message
   * @param e the exception
   */
  protected final void logException(Exception e) {
    logException("exception", e);
  }

  /**
   * Logs an exception with an introductory message in addition to the 
   * exception's getMessage().
   * @param msg message
   * @param e   exception
   * @see Exception#getMessage
   */
  protected void logException(CharSequence msg, Exception e) {
    System.err.println(msg + ":" + e.getMessage());
    e.printStackTrace();
  }

  /**
   * Logs a message. Override this for more detailed logging. 
   * @param message
   */
  protected void log(CharSequence message) {
    System.out.println(message);
  }

  /**
   * @return whether debugging is activated
   */
  public boolean isDebug() {
    return (null == _debug) ? DEBUG : _debug.booleanValue();
  }

  /**
   * @deprecated
   */
  public URL notifications_send(Collection<Long> recipientIds, CharSequence notification,
                                CharSequence email)
    throws FacebookException, IOException {
    this.notifications_send(recipientIds, notification);
    return null;
  }

  /**
   * Extracts a URL from a result that consists of a URL only. 
   * @param result
   * @return the URL
   */
  protected abstract URL extractURL(T result)
    throws IOException;

  /**
   * Recaches the image with the specified imageUrl.
   * @param imageUrl String representing the image URL to refresh
   * @return boolean indicating whether the refresh succeeded
   */
  public boolean fbml_refreshImgSrc(String imageUrl)
    throws FacebookException, IOException {
    return fbml_refreshImgSrc(new URL(imageUrl));
  }

  /**
   * Uploads a photo to Facebook.
   * @param photo an image file
   * @return a T with the standard Facebook photo information
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload">
   *      Developers wiki: Photos.upload</a>
   */
  public T photos_upload(File photo)
    throws FacebookException, IOException {
    return photos_upload(photo, null /* caption */ , null /* albumId */);
  }

  /**
   * Uploads a photo to Facebook.
   * @param photo an image file
   * @param caption a description of the image contents
   * @return a T with the standard Facebook photo information
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload">
   *      Developers wiki: Photos.upload</a>
   */
  public T photos_upload(File photo, String caption)
    throws FacebookException, IOException {
    return photos_upload(photo, caption, null /* albumId */);
  }

  /**
   * Uploads a photo to Facebook.
   * @param photo an image file
   * @param albumId the album into which the photo should be uploaded
   * @return a T with the standard Facebook photo information
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload">
   *      Developers wiki: Photos.upload</a>
   */
  public T photos_upload(File photo, Long albumId)
    throws FacebookException, IOException {
    return photos_upload(photo, null /* caption */, albumId);
  }

  /**
   * Uploads a photo to Facebook.
   * @param photo an image file
   * @param caption a description of the image contents
   * @param albumId the album into which the photo should be uploaded
   * @return a T with the standard Facebook photo information
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload">
   * Developers wiki: Photos.upload</a>
   */
  public T photos_upload(File photo, String caption, Long albumId)
    throws FacebookException, IOException {
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.PHOTOS_UPLOAD.numParams());
    assert (photo.exists() && photo.canRead());
    this._uploadFile = photo;
    if (null != albumId)
      params.add(new Pair<String, CharSequence>("aid", Long.toString(albumId)));
    if (null != caption)
      params.add(new Pair<String, CharSequence>("caption", caption));
    return callMethod(FacebookMethod.PHOTOS_UPLOAD, params);
  }

  /**
   * Creates an album.
   * @param albumName The list of photos from which to extract photo tags.
   * @return the created album
   */
  public T photos_createAlbum(String albumName)
    throws FacebookException, IOException {
    return this.photos_createAlbum(albumName, null /*description*/, null /*location*/); 
  }

  /**
   * Adds a tag to a photo.
   * @param photoId The photo id of the photo to be tagged.
   * @param xPct The horizontal position of the tag, as a percentage from 0 to 100, from the left of the photo.
   * @param yPct The vertical position of the tag, as a percentage from 0 to 100, from the top of the photo.
   * @param taggedUserId The list of photos from which to extract photo tags.
   * @return whether the tag was successfully added.
   */
  public boolean photos_addTag(Long photoId, Long taggedUserId, Double xPct, Double yPct)
    throws FacebookException, IOException {
    return photos_addTag(photoId, xPct, yPct, taggedUserId, null);
  }

  /**
   * Adds several tags to a photo.
   * @param photoId The photo id of the photo to be tagged.
   * @param tags A list of PhotoTags.
   * @return a list of booleans indicating whether the tag was successfully added.
   */
  public T photos_addTags(Long photoId, Collection<PhotoTag> tags)
    throws FacebookException, IOException {
    assert (photoId > 0);
    assert (null != tags && !tags.isEmpty());
    
    JSONArray jsonTags=new JSONArray();
    for (PhotoTag tag : tags) {
      jsonTags.put(tag.jsonify());
    }

    return this.callMethod(FacebookMethod.PHOTOS_ADD_TAG,
                           new Pair<String, CharSequence>("pid", photoId.toString()),
                           new Pair<String, CharSequence>("tags", jsonTags.toString()));
  }

  public void setIsDesktop(boolean isDesktop) {
    this._isDesktop = isDesktop;
  }

  /**
   * Returns all visible events according to the filters specified. This may be used to find all events of a user, or to query specific eids.
   * @param eventIds filter by these event ID's (optional)
   * @param userId filter by this user only (optional)
   * @param startTime UTC lower bound (optional)
   * @param endTime UTC upper bound (optional)
   * @return T of events
   */
  public T events_get(Long userId, Collection<Long> eventIds, Long startTime, Long endTime)
    throws FacebookException, IOException {
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.EVENTS_GET.numParams());

    boolean hasUserId = null != userId && 0 != userId;
    boolean hasEventIds = null != eventIds && !eventIds.isEmpty();
    boolean hasStart = null != startTime && 0 != startTime;
    boolean hasEnd = null != endTime && 0 != endTime;

    if (hasUserId)
      params.add(new Pair<String, CharSequence>("uid", Long.toString(userId)));
    if (hasEventIds)
      params.add(new Pair<String, CharSequence>("eids", delimit(eventIds)));
    if (hasStart)
      params.add(new Pair<String, CharSequence>("start_time", startTime.toString()));
    if (hasEnd)
      params.add(new Pair<String, CharSequence>("end_time", endTime.toString()));
    return this.callMethod(FacebookMethod.EVENTS_GET, params);
  }

  /**
   * Sets the FBML for a user's profile, including the content for both the profile box
   * and the profile actions.
   * @param userId - the user whose profile FBML to set
   * @param fbmlMarkup - refer to the FBML documentation for a description of the markup and its role in various contexts
   * @return a boolean indicating whether the FBML was successfully set
   * 
   * @deprecated Facebook will remove support for this version of the API call on 1/17/2008, please use the alternate version instead.
   */
  public boolean profile_setFBML(CharSequence fbmlMarkup, Long userId)
    throws FacebookException, IOException {

    return extractBoolean(this.callMethod(FacebookMethod.PROFILE_SET_FBML,
                                          new Pair<String, CharSequence>("uid",
                                                                         Long.toString(userId)),
                                          new Pair<String, CharSequence>("markup", fbmlMarkup)));

  }

  protected static CharSequence delimit(Collection<Map.Entry<String, CharSequence>> entries,
                                        CharSequence delimiter, CharSequence equals,
                                        boolean doEncode) {
    if (entries == null || entries.isEmpty())
      return null;

    StringBuilder buffer = new StringBuilder();
    boolean notFirst = false;
    for (Map.Entry<String, CharSequence> entry : entries) {
      if (notFirst)
        buffer.append(delimiter);
      else
        notFirst = true;
      CharSequence value = entry.getValue();
      buffer.append(entry.getKey()).append(equals).append(doEncode ? encode(value) : value);
    }
    return buffer;
  }

  /**
   * Creates an album.
   * @param name The album name.
   * @param location The album location (optional).
   * @param description The album description (optional).
   * @return an array of photo objects.
   */
  public T photos_createAlbum(String name, String description, String location)
    throws FacebookException, IOException {
    assert (null != name && !"".equals(name));
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.PHOTOS_CREATE_ALBUM.numParams());
    params.add(new Pair<String, CharSequence>("name", name));
    if (null != description)
      params.add(new Pair<String, CharSequence>("description", description));
    if (null != location)
      params.add(new Pair<String, CharSequence>("location", location));
    return this.callMethod(FacebookMethod.PHOTOS_CREATE_ALBUM, params);
  }

  public void setDebug(boolean isDebug) {
    _debug = isDebug;
  }

  /**
   * Extracts a Boolean from a result that consists of a Boolean only.
   * @param result
   * @return the Boolean
   */
  protected boolean extractBoolean(T result) {
      if (result == null) {
          return false;
      }
      return 1 == extractInt(result);
  }

  /**
   * Extracts an Long from a result that consists of an Long only.
   * @param result
   * @return the Long
   */
  protected abstract int extractInt(T result);
  
  /**
   * Extracts an Long from a result that consists of a Long only.
   * @param result
   * @return the Long
   */
  protected abstract Long extractLong(T result);

  /**
   * Retrieves album metadata for a list of album IDs.
   * @param albumIds the ids of albums whose metadata is to be retrieved
   * @return album objects
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.getAlbums">
   *      Developers Wiki: Photos.getAlbums</a>
   */
  public T photos_getAlbums(Collection<Long> albumIds)
    throws FacebookException, IOException {
    return photos_getAlbums(null /*userId*/, albumIds);
  }

  /**
   * Retrieves album metadata for albums owned by a user.
   * @param userId   (optional) the id of the albums' owner (optional)
   * @return album objects
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.getAlbums">
   *      Developers Wiki: Photos.getAlbums</a>
   */
  public T photos_getAlbums(Long userId)
    throws FacebookException, IOException {
    return photos_getAlbums(userId, null /*albumIds*/); 
  }

  /**
   * Retrieves album metadata. Pass a user id and/or a list of album ids to specify the albums
   * to be retrieved (at least one must be provided)
   *
   * @param userId   (optional) the id of the albums' owner (optional)
   * @param albumIds (optional) the ids of albums whose metadata is to be retrieved
   * @return album objects
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.getAlbums">
   *      Developers Wiki: Photos.getAlbums</a>
   */
  public T photos_getAlbums(Long userId, Collection<Long> albumIds)
    throws FacebookException, IOException {
    boolean hasUserId = null != userId && userId != 0;
    boolean hasAlbumIds = null != albumIds && !albumIds.isEmpty();
    assert (hasUserId || hasAlbumIds); // one of the two must be provided

    if (hasUserId)
      return (hasAlbumIds) ?
             this.callMethod(FacebookMethod.PHOTOS_GET_ALBUMS, new Pair<String, CharSequence>("uid",
                                                                                              Long.toString(userId)),
                             new Pair<String, CharSequence>("aids", delimit(albumIds))) :
             this.callMethod(FacebookMethod.PHOTOS_GET_ALBUMS,
                             new Pair<String, CharSequence>("uid", Long.toString(userId)));
    else
      return this.callMethod(FacebookMethod.PHOTOS_GET_ALBUMS,
                             new Pair<String, CharSequence>("aids", delimit(albumIds)));
  }

  /**
   * Recaches the image with the specified imageUrl.
   * @param imageUrl the image URL to refresh
   * @return boolean indicating whether the refresh succeeded
   */
  public boolean fbml_refreshImgSrc(URL imageUrl)
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.FBML_REFRESH_IMG_SRC,
                                          new Pair<String, CharSequence>("url",
                                                                         imageUrl.toString())));
  }

  /**
   * Retrieves the friends of the currently logged in user.
   * @return array of friends
   */
  public T friends_get()
    throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.FRIENDS_GET);
  }

  private InputStream postRequest(CharSequence method, Map<String, CharSequence> params,
                                  boolean doHttps, boolean doEncode)
    throws IOException {
    CharSequence buffer = (null == params) ? "" : delimit(params.entrySet(), "&", "=", doEncode);
    URL serverUrl = (doHttps) ? HTTPS_SERVER_URL : _serverUrl;
    if (isDebug()) {
      StringBuilder debugMsg = new StringBuilder()
        .append(method)
        .append(" POST: ")
        .append(serverUrl.toString())
        .append("?");
      debugMsg.append(buffer);
      log(debugMsg);
    }

    HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
    if (this._timeout != -1) {
        conn.setConnectTimeout(this._timeout);
    }
    try {
      conn.setRequestMethod("POST");
    } catch (ProtocolException ex) {
      logException(ex);
    }
    conn.setDoOutput(true);
    conn.connect();
    conn.getOutputStream().write(buffer.toString().getBytes());

    return conn.getInputStream();
  }

  /**
   * Call this function and store the result, using it to generate the
   * appropriate login url and then to retrieve the session information.
   * @return an authentication token
   */
  public String auth_createToken()
    throws FacebookException, IOException {
    T d = this.callMethod(FacebookMethod.AUTH_CREATE_TOKEN);
    return extractString(d);
  }

  /**
   * Extracts a String from a T consisting entirely of a String.
   * @param result
   * @return the String
   */
  protected abstract String extractString(T result);

  /**
   * Create a marketplace listing
   * @param showOnProfile whether the listing can be shown on the user's profile
   * @param attrs the properties of the listing
   * @return the id of the created listing
   * @see MarketplaceListing
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.createListing">
   *      Developers Wiki: marketplace.createListing</a>
   */
  public Long marketplace_createListing(Boolean showOnProfile, MarketplaceListing attrs)
    throws FacebookException, IOException {
    T result = this.callMethod(FacebookMethod.MARKETPLACE_CREATE_LISTING,
                               new Pair<String, CharSequence>("show_on_profile", showOnProfile ? "1" : "0"),
                               new Pair<String, CharSequence>("listing_id", "0"),
                               new Pair<String, CharSequence>("listing_attrs", attrs.jsonify().toString()));
    return this.extractLong(result);
  }
  
  /**
   * Modify a marketplace listing
   * @param listingId identifies the listing to be modified
   * @param showOnProfile whether the listing can be shown on the user's profile
   * @param attrs the properties of the listing
   * @return the id of the edited listing
   * @see MarketplaceListing
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.createListing">
   *      Developers Wiki: marketplace.createListing</a>
   */
  public Long marketplace_editListing(Long listingId, Boolean showOnProfile, MarketplaceListing attrs)
    throws FacebookException, IOException {
    T result = this.callMethod(FacebookMethod.MARKETPLACE_CREATE_LISTING,
                               new Pair<String, CharSequence>("show_on_profile", showOnProfile ? "1" : "0"),
                               new Pair<String, CharSequence>("listing_id", listingId.toString()),
                               new Pair<String, CharSequence>("listing_attrs", attrs.jsonify().toString()));
    return this.extractLong(result);
  }
  
  /**
   * Remove a marketplace listing
   * @param listingId the listing to be removed
   * @return boolean indicating whether the listing was removed
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.removeListing">
   *      Developers Wiki: marketplace.removeListing</a>
   */
  public boolean marketplace_removeListing(Long listingId)
    throws FacebookException, IOException {
    return marketplace_removeListing(listingId, MARKETPLACE_STATUS_DEFAULT);
  }

  /**
   * Remove a marketplace listing
   * @param listingId the listing to be removed
   * @param status MARKETPLACE_STATUS_DEFAULT, MARKETPLACE_STATUS_SUCCESS, or MARKETPLACE_STATUS_NOT_SUCCESS
   * @return boolean indicating whether the listing was removed
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.removeListing">
   *      Developers Wiki: marketplace.removeListing</a>
   */
  public boolean marketplace_removeListing(Long listingId, CharSequence status)
    throws FacebookException, IOException {
    assert MARKETPLACE_STATUS_DEFAULT.equals(status) || MARKETPLACE_STATUS_SUCCESS.equals(status)
           || MARKETPLACE_STATUS_NOT_SUCCESS.equals(status) : "Invalid status: " + status;
    
    T result = this.callMethod(FacebookMethod.MARKETPLACE_REMOVE_LISTING,
                               new Pair<String, CharSequence>("listing_id", listingId.toString()),
                               new Pair<String, CharSequence>("status", status));
    return this.extractBoolean(result);
  }
  
  /**
   * Get the categories available in marketplace.
   * @return a T listing the marketplace categories
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getCategories">
   *      Developers Wiki: marketplace.getCategories</a>
   */
  public List<String> marketplace_getCategories()
    throws FacebookException, IOException {
    T temp = this.callMethod(FacebookMethod.MARKETPLACE_GET_CATEGORIES);
    if (temp == null) {
        return null;
    }
    List<String> results = new ArrayList<String>();
    if (temp instanceof Document) {
        Document d = (Document)temp;
        NodeList cats = d.getElementsByTagName("marketplace_category");
        for (int count = 0; count < cats.getLength(); count++) {
            results.add(cats.item(count).getFirstChild().getTextContent());
        }
    }
    else {
        JSONObject j = (JSONObject)temp;
        Iterator it = j.keys();
        while (it.hasNext()) {
            try {
                results.add(j.get((String)it.next()).toString());
            }
            catch (Exception ignored) {  }
        }
    }
    return results;
  }

  /**
   * Get the subcategories available for a category.
   * @param category a category, e.g. "HOUSING"
   * @return a T listing the marketplace sub-categories
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getSubCategories">
   *      Developers Wiki: marketplace.getSubCategories</a>
   */
  public T marketplace_getSubCategories(CharSequence category)
    throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.MARKETPLACE_GET_SUBCATEGORIES,
                           new Pair<String, CharSequence>("category", category));
  }

  /**
   * Fetch marketplace listings, filtered by listing IDs and/or the posting users' IDs.
   * @param listingIds listing identifiers (required if uids is null/empty)
   * @param userIds posting user identifiers (required if listingIds is null/empty)
   * @return a T of marketplace listings
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getListings">
   *      Developers Wiki: marketplace.getListings</a>
   */
  public T marketplace_getListings(Collection<Long> listingIds, Collection<Long> userIds)
    throws FacebookException, IOException {
    
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.MARKETPLACE_GET_LISTINGS.numParams());
    if (null != listingIds && !listingIds.isEmpty()) {
      params.add(new Pair<String, CharSequence>("listing_ids", delimit(listingIds)));
    }
    if (null != userIds && !userIds.isEmpty()) {
      params.add(new Pair<String, CharSequence>("uids", delimit(userIds)));
    }

    assert !params.isEmpty() : "Either listingIds or userIds should be provided";
    return this.callMethod(FacebookMethod.MARKETPLACE_GET_LISTINGS, params);
  }

  /**
   * Search for marketplace listings, optionally by category, subcategory, and/or query string.
   * @param category the category of listings desired (optional except if subcategory is provided)
   * @param subCategory the subcategory of listings desired (optional)
   * @param query a query string (optional)
   * @return a T of marketplace listings
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.search">
   *      Developers Wiki: marketplace.search</a>
   */
  public T marketplace_search(CharSequence category, CharSequence subCategory, CharSequence query)
    throws FacebookException, IOException {
    
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.MARKETPLACE_SEARCH.numParams());
    if (null != category && !"".equals(category)) {
      params.add(new Pair<String, CharSequence>("category", category));
      if (null != subCategory && !"".equals(subCategory)) {
        params.add(new Pair<String, CharSequence>("subcategory", subCategory));
      }
    }
    if (null != query && !"".equals(query)) {
      params.add(new Pair<String, CharSequence>("query", category));
    }

    return this.callMethod(FacebookMethod.MARKETPLACE_SEARCH, params);
  }
  
  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param albumId retrieve from photos from this album (optional)
   * @param photoIds retrieve from this list of photos (optional)
   * @return an T of photo objects.
   * @see #photos_get(Integer, Long, Collection)
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get">
   *      Developers Wiki: Photos.get</a> 
   */
  public T photos_getByAlbum(Long albumId, Collection<Long> photoIds)
    throws FacebookException, IOException {
    return photos_get(null /*subjId*/, albumId, photoIds);
  }
  
  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param albumId retrieve from photos from this album (optional)
   * @return an T of photo objects.
   * @see #photos_get(Integer, Long, Collection)
   * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get">
   *      Developers Wiki: Photos.get</a> 
   */
  public T photos_getByAlbum(Long albumId)
    throws FacebookException, IOException {
    return photos_get(null /*subjId*/, albumId, null /*photoIds*/);
  }
  
  /**
   * Get the categories available in marketplace.
   * @return a T listing the marketplace categories
   * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getCategories">
   *      Developers Wiki: marketplace.getCategories</a>
   */
  public T marketplace_getCategoriesObject()
    throws FacebookException, IOException {
    T temp = this.callMethod(FacebookMethod.MARKETPLACE_GET_CATEGORIES);
    return temp;
  }
  
  public String getRawResponse() {
      return this.rawResponse;
  }
  
  public Object getResponsePOJO() {
      if (this.rawResponse == null) {
          return null;
      }
      if ((this.getResponseFormat() != null) && (! "xml".equals(this.getResponseFormat().toLowerCase()))) {
          //JAXB will not work with JSON
          throw new RuntimeException("You can only generate a response POJO when using XML formatted API responses!  JSON users go elsewhere!");
      }
      JAXBContext jc;
      Object pojo = null;
      try {
          jc = JAXBContext.newInstance("com.facebook.api.schema");
          Unmarshaller unmarshaller = jc.createUnmarshaller();
          pojo =  unmarshaller.unmarshal(new ByteArrayInputStream(this.rawResponse.getBytes("UTF-8")));
      } catch (JAXBException e) {
          System.err.println("getResponsePOJO() - Could not unmarshall XML stream into POJO");
          e.printStackTrace();
      }
      catch (NullPointerException e) {
          System.err.println("getResponsePOJO() - Could not unmarshall XML stream into POJO.");
          e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
          System.err.println("getResponsePOJO() - Could not unmarshall XML stream into POJO.");
          e.printStackTrace();
      }
      return pojo;
  }
  
  public boolean feed_PublishTemplatizedAction(TemplatizedAction action) throws FacebookException, IOException{
      return this.templatizedFeedHandler(FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, action.getTitleTemplate(), action.getTitleParams(), 
              action.getBodyTemplate(), action.getBodyParams(), action.getBodyGeneral(), action.getPictures(), action.getTargetIds(), action.getPageActorId());
  }
  
  public boolean feed_publishTemplatizedAction(String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral, Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds) throws FacebookException, IOException {
      return this.templatizedFeedHandler(FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, titleTemplate, titleData, 
              bodyTemplate, bodyData, bodyGeneral, pictures, targetIds, null);
  }
  
  protected boolean templatizedFeedHandler(FacebookMethod method, String titleTemplate, String titleData, String bodyTemplate,
          String bodyData, String bodyGeneral, Collection<? extends IPair<? extends Object, URL>> pictures, String targetIds, Long pageId) throws FacebookException, IOException {
      assert (pictures == null || pictures.size() <= 4);

      ArrayList<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>(method.numParams());

      //these are always required parameters
      params.add(new Pair<String, CharSequence>("title_template", titleTemplate));

      //these are optional parameters
      if (titleData != null) {
          params.add(new Pair<String, CharSequence>("title_data", titleData));
      }
      if (bodyTemplate != null) {
          params.add(new Pair<String, CharSequence>("body_template", bodyTemplate));
          if (bodyData != null) {
              params.add(new Pair<String, CharSequence>("body_data", bodyData));
          }
      }
      if (bodyGeneral != null) {
          params.add(new Pair<String, CharSequence>("body_general", bodyGeneral));
      }
      if (pictures != null) {
          int count = 1;
          for (IPair picture : pictures) {
                params.add(new Pair<String, CharSequence>("image_" + count, picture.getFirst().toString()));
                if (picture.getSecond() != null) {
                    params.add(new Pair<String, CharSequence>("image_" + count + "_link", picture.getSecond().toString()));
                }
                count++;
          }
      }
      if (targetIds != null) {
          params.add(new Pair<String, CharSequence>("target_ids", targetIds));
      }
      if (pageId != null) {
          params.add(new Pair<String, CharSequence>("page_actor_id", Long.toString(pageId)));
      }
      this.callMethod(method, params);
      if (this.rawResponse == null) {
          return false;
      }
      return this.rawResponse.contains(">1<"); //a code of '1' indicates success
  }
  
  public boolean users_hasAppPermission(Permission perm) throws FacebookException, IOException {
      return this.users_hasAppPermission(perm.getName());
  }
  
  public Long marketplace_createListing(Long listingId, boolean showOnProfile, String attributes) throws FacebookException, IOException {
      T result = this.callMethod(FacebookMethod.MARKETPLACE_CREATE_LISTING,
              new Pair<String, CharSequence>("show_on_profile", showOnProfile ? "1" : "0"),
              new Pair<String, CharSequence>("listing_id", "0"),
              new Pair<String, CharSequence>("listing_attrs", attributes));
      return this.extractLong(result);
  }
  
  public Long marketplace_createListing(Long listingId, boolean showOnProfile, MarketListing listing) throws FacebookException, IOException {
      return this.marketplace_createListing(listingId, showOnProfile, listing.getAttribs());
  }
  
  public Long marketplace_createListing(boolean showOnProfile, MarketListing listing) throws FacebookException, IOException {
      return this.marketplace_createListing(null, showOnProfile, listing.getAttribs());
  }
  
  public boolean marketplace_removeListing(Long listingId, MarketListingStatus status) throws FacebookException, IOException {
      return this.marketplace_removeListing(listingId, status.getName());
  }
  
  public Long marketplace_editListing(Long listingId, Boolean showOnProfile, MarketListing attrs)
    throws FacebookException, IOException {
      T result = this.callMethod(FacebookMethod.MARKETPLACE_CREATE_LISTING,
              new Pair<String, CharSequence>("show_on_profile", showOnProfile ? "1" : "0"),
              new Pair<String, CharSequence>("listing_id", listingId.toString()),
              new Pair<String, CharSequence>("listing_attrs", attrs.getAttribs()));
      return this.extractLong(result);
  }
  
  public boolean users_setStatus(String newStatus, boolean clear) throws FacebookException, IOException {
      return this.users_setStatus(newStatus, clear, false);
  }
  
  /**
   * Retrieves the requested profile fields for the Facebook Pages with the given 
   * <code>pageIds</code>. Can be called for pages that have added the application 
   * without establishing a session.
   * @param pageIds the page IDs
   * @param fields a set of page profile fields
   * @return a T consisting of a list of pages, with each page element
   *     containing the requested fields.
   * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo">
   *      Developers Wiki: Pages.getInfo</a>
   */
  public T pages_getInfo(Collection<Long> pageIds, EnumSet<PageProfileField> fields)
    throws FacebookException, IOException {
    if (pageIds == null || pageIds.isEmpty()) {
      throw new IllegalArgumentException("pageIds cannot be empty or null");
    }
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("fields cannot be empty or null");
    }
    IFacebookMethod method =
      null == this._sessionKey ? FacebookMethod.PAGES_GET_INFO_NO_SESSION : FacebookMethod.PAGES_GET_INFO;
    return this.callMethod(method,
                           new Pair<String, CharSequence>("page_ids", delimit(pageIds)),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }
  
  /**
   * Retrieves the requested profile fields for the Facebook Pages with the given 
   * <code>pageIds</code>. Can be called for pages that have added the application 
   * without establishing a session.
   * @param pageIds the page IDs
   * @param fields a set of page profile fields
   * @return a T consisting of a list of pages, with each page element
   *     containing the requested fields.
   * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo">
   *      Developers Wiki: Pages.getInfo</a>
   */
  public T pages_getInfo(Collection<Long> pageIds, Set<CharSequence> fields)
    throws FacebookException, IOException {
    if (pageIds == null || pageIds.isEmpty()) {
      throw new IllegalArgumentException("pageIds cannot be empty or null");
    }
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("fields cannot be empty or null");
    }
    IFacebookMethod method =
      null == this._sessionKey ? FacebookMethod.PAGES_GET_INFO_NO_SESSION : FacebookMethod.PAGES_GET_INFO;
    return this.callMethod(method,
                           new Pair<String, CharSequence>("page_ids", delimit(pageIds)),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }
  
  /**
   * Retrieves the requested profile fields for the Facebook Pages of the user with the given 
   * <code>userId</code>.
   * @param userId the ID of a user about whose pages to fetch info (defaulted to the logged-in user)
   * @param fields a set of PageProfileFields
   * @return a T consisting of a list of pages, with each page element
   *     containing the requested fields.
   * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo">
   *      Developers Wiki: Pages.getInfo</a>
   */
  public T pages_getInfo(Long userId, EnumSet<PageProfileField> fields)
    throws FacebookException, IOException {
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("fields cannot be empty or null");
    }
    if (userId == null) {
      userId = this._userId;
    }
    return this.callMethod(FacebookMethod.PAGES_GET_INFO,
                           new Pair<String, CharSequence>("uid",    userId.toString()),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }

  /**
   * Retrieves the requested profile fields for the Facebook Pages of the user with the given
   * <code>userId</code>.
   * @param userId the ID of a user about whose pages to fetch info (defaulted to the logged-in user)
   * @param fields a set of page profile fields
   * @return a T consisting of a list of pages, with each page element
   *     containing the requested fields.
   * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo">
   *      Developers Wiki: Pages.getInfo</a>
   */
  public T pages_getInfo(Long userId, Set<CharSequence> fields)
    throws FacebookException, IOException {
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("fields cannot be empty or null");
    }
    if (userId == null) {
      userId = this._userId;
    }
    return this.callMethod(FacebookMethod.PAGES_GET_INFO,
                           new Pair<String, CharSequence>("uid",    userId.toString()),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }

  /**
   * Checks whether a page has added the application
   * @param pageId the ID of the page
   * @return true if the page has added the application
   * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isAppAdded">
   *      Developers Wiki: Pages.isAppAdded</a>
   */
  public boolean pages_isAppAdded(Long pageId)
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.PAGES_IS_APP_ADDED,
                                          new Pair<String,CharSequence>("page_id", pageId.toString())));
  }
  
  /**
   * Checks whether a user is a fan of the page with the given <code>pageId</code>.
   * @param pageId the ID of the page
   * @param userId the ID of the user (defaults to the logged-in user if null)
   * @return true if the user is a fan of the page
   * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isFan">
   *      Developers Wiki: Pages.isFan</a>
   */
  public boolean pages_isFan(Long pageId, Long userId)
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.PAGES_IS_FAN,
                                          new Pair<String,CharSequence>("page_id", pageId.toString()),
                                          new Pair<String,CharSequence>("uid", userId.toString())));
  }
  
  /**
   * Checks whether the logged-in user is a fan of the page with the given <code>pageId</code>.
   * @param pageId the ID of the page
   * @return true if the logged-in user is a fan of the page
   * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isFan">
   *      Developers Wiki: Pages.isFan</a>
   */
  public boolean pages_isFan(Long pageId)
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.PAGES_IS_FAN,
                                          new Pair<String,CharSequence>("page_id", pageId.toString())));
  }

  /**
   * Checks whether the logged-in user for this session is an admin of the page
   * with the given <code>pageId</code>.
   * @param pageId the ID of the page
   * @return true if the logged-in user is an admin
   * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isAdmin">
   *      Developers Wiki: Pages.isAdmin</a>
   */
  public boolean pages_isAdmin(Long pageId)
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.PAGES_IS_ADMIN,
                                          new Pair<String, CharSequence>("page_id",
                                                                         pageId.toString())));
  }
  
  /**
   * Associates a "<code>handle</code>" with FBML markup so that the handle can be used within the
   * <a href="http://wiki.developers.facebook.com/index.php/Fb:ref">fb:ref</a> FBML tag.
   * A handle is unique within an application and allows an application to publish identical FBML
   * to many user profiles and do subsequent updates without having to republish FBML for each user.
   *
   * @param handle - a string, unique within the application, that
   * @param fbmlMarkup - refer to the FBML documentation for a description of the markup and its role in various contexts
   * @return a boolean indicating whether the FBML was successfully set
   * @see <a href="http://wiki.developers.facebook.com/index.php/Fbml.setRefHandle">
   *      Developers Wiki: Fbml.setRefHandle</a>
   */
  public boolean fbml_setRefHandle(String handle, String fbmlMarkup)
    throws FacebookException, IOException {

    return extractBoolean(this.callMethod(FacebookMethod.FBML_SET_REF_HANDLE,
                                          new Pair<String, CharSequence>("handle", handle),
                                          new Pair<String, CharSequence>("fbml", fbmlMarkup)));

  }
  
  public boolean sms_canSend() throws FacebookException, IOException {
      return this.sms_canSend(this.users_getLoggedInUser());      
  }

  /**
   * Determines whether this application can send SMS to the user identified by <code>userId</code>
   * @param userId a user ID
   * @return true if sms can be sent to the user
   * @see FacebookExtendedPerm#SMS
   * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Application_generated_messages">
   *      Developers Wiki: Mobile: Application Generated Messages</a>
   */
  public boolean sms_canSend(Long userId)
    throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.SMS_CAN_SEND,
                                          new Pair<String, CharSequence>("uid",
                                                                         userId.toString())));
  }

  /**
   * Sends a message via SMS to the user identified by <code>userId</code> in response 
   * to a user query associated with <code>mobileSessionId</code>.
   *
   * @param userId a user ID
   * @param response the message to be sent via SMS
   * @param mobileSessionId the mobile session
   * @throws FacebookException in case of error
   * @throws IOException
   * @see FacebookExtendedPerm#SMS
   * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Application_generated_messages">
   * Developers Wiki: Mobile: Application Generated Messages</a>
   * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Workflow">
   * Developers Wiki: Mobile: Workflow</a>
   */
  public void sms_sendResponse(Integer userId, CharSequence response, Integer mobileSessionId)
    throws FacebookException, IOException {
    this.callMethod(FacebookMethod.SMS_SEND_MESSAGE,
                    new Pair<String, CharSequence>("uid", userId.toString()),
                    new Pair<String, CharSequence>("message", response),
                    new Pair<String, CharSequence>("session_id", mobileSessionId.toString()));
  }

  /**
   * Sends a message via SMS to the user identified by <code>userId</code>.
   * The SMS extended permission is required for success.
   *
   * @param userId a user ID
   * @param message the message to be sent via SMS
   * @throws FacebookException in case of error
   * @throws IOException
   * @see FacebookExtendedPerm#SMS
   * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Application_generated_messages">
   * Developers Wiki: Mobile: Application Generated Messages</a>
   * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Workflow">
   * Developers Wiki: Mobile: Workflow</a>
   */
  public void sms_sendMessage(Long userId, CharSequence message)
    throws FacebookException, IOException {
    this.callMethod(FacebookMethod.SMS_SEND_MESSAGE,
                    new Pair<String, CharSequence>("uid", userId.toString()),
                    new Pair<String, CharSequence>("message", message),
                    new Pair<String, CharSequence>("req_session", "0"));
  }

  /**
   * Sends a message via SMS to the user identified by <code>userId</code>, with
   * the expectation that the user will reply. The SMS extended permission is required for success.
   * The returned mobile session ID can be stored and used in {@link #sms_sendResponse} when
   * the user replies.
   *
   * @param userId a user ID
   * @param message the message to be sent via SMS
   * @return a mobile session ID (can be used in {@link #sms_sendResponse})
   * @throws FacebookException in case of error, e.g. SMS is not enabled
   * @throws IOException
   * @see FacebookExtendedPerm#SMS
   * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Application_generated_messages">
   *      Developers Wiki: Mobile: Application Generated Messages</a>
   * @see <a href="http://wiki.developers.facebook.com/index.php/Mobile#Workflow">
   *      Developers Wiki: Mobile: Workflow</a>
   */
  public int sms_sendMessageWithSession(Long userId, CharSequence message)
    throws FacebookException, IOException {
    return extractInt(this.callMethod(FacebookMethod.SMS_SEND_MESSAGE,
                               new Pair<String, CharSequence>("uid", userId.toString()),
                               new Pair<String, CharSequence>("message", message),
                               new Pair<String, CharSequence>("req_session", "1")));
  }
  
  public void notifications_send(Collection<Long> recipientIds, CharSequence notification) throws FacebookException, IOException {
      if (null == notification || "".equals(notification)) {
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "You cannot send an empty notification!");
      }
      if ((recipientIds != null) && (! recipientIds.isEmpty())) {
          this.callMethod(FacebookMethod.NOTIFICATIONS_SEND,
                  new Pair<String, CharSequence>("to_ids", delimit(recipientIds)),
                  new Pair<String, CharSequence>("notification", notification));
      }
      else {
          this.callMethod(FacebookMethod.NOTIFICATIONS_SEND,
                  new Pair<String, CharSequence>("notification", notification));
      }
  }
  
  private T notifications_sendEmail(CharSequence recipients, CharSequence subject, CharSequence email, CharSequence fbml) throws FacebookException, IOException {
      if (null == recipients || "".equals(recipients)) {
          //we throw an exception here because returning a sensible result (like an empty list) is problematic due to the use of Document as the return type
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "You must specify at least one recipient when sending an email!");
      }
      if ((null == email || "".equals(email)) && (null == fbml || "".equals(fbml))){
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "You cannot send an empty email!");
      }
      T d;
      String paramName = "text";
      String paramValue;
      if ((email == null) || ("".equals(email.toString()))) {
          paramValue = fbml.toString();
          paramName = "fbml";
      }
      else {
          paramValue = email.toString();
      }

      //session is only required to send email from a desktop app
      FacebookMethod method = this.isDesktop() ? FacebookMethod.NOTIFICATIONS_SEND_EMAIL_SESSION : FacebookMethod.NOTIFICATIONS_SEND_EMAIL;
      if ((subject != null) && (! "".equals(subject))) {
          d = this.callMethod(method,
                        new Pair<String, CharSequence>("recipients", recipients),
                        new Pair<String, CharSequence>("subject", subject),
                        new Pair<String, CharSequence>(paramName, paramValue));
      }
      else {
          d = this.callMethod(method,
                  new Pair<String, CharSequence>("recipients", recipients),
                  new Pair<String, CharSequence>(paramName, paramValue));
      }
      
      return d;
  }

  public T notifications_sendEmail(Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml) throws FacebookException, IOException {
      return this.notifications_sendEmail(delimit(recipients), subject, email, fbml);
  }

  public T notifications_sendEmailToCurrentUser(String subject, String email, String fbml) throws FacebookException, IOException {
      Long currentUser = this.users_getLoggedInUser();
      return this.notifications_sendEmail(currentUser.toString(), subject, email, fbml);
  }

  public T notifications_sendFbmlEmail(Collection<Long> recipients, String subject, String fbml) throws FacebookException, IOException {
      return this.notifications_sendEmail(delimit(recipients), subject, null, fbml);
  }

  public T notifications_sendFbmlEmailToCurrentUser(String subject, String fbml) throws FacebookException, IOException {
      Long currentUser = this.users_getLoggedInUser();
      return this.notifications_sendEmail(currentUser.toString(), subject, null, fbml);
  }

  public T notifications_sendTextEmail(Collection<Long> recipients, String subject, String email) throws FacebookException, IOException {
      return this.notifications_sendEmail(delimit(recipients), subject, email, null);
  }

  public T notifications_sendTextEmailToCurrentUser(String subject, String email) throws FacebookException, IOException {
      Long currentUser = this.users_getLoggedInUser();
      return this.notifications_sendEmail(currentUser.toString(), subject, email, null);
  }
  
  public boolean users_setStatus(String newStatus, boolean clear, boolean statusIncludesVerb) throws FacebookException, IOException {
      Collection<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();

      if (newStatus != null) {
          params.add(new Pair<String, CharSequence>("status", newStatus));
      }
      if (clear) {
          this.users_clearStatus();
      }
      if (statusIncludesVerb) {
          params.add(new Pair<String, CharSequence>("status_includes_verb", "true"));
      }

      return this.extractBoolean(this.callMethod(FacebookMethod.USERS_SET_STATUS, params));
  }
  
  /**
   * Send a notification message to the logged-in user.
   *
   * @param notification the FBML to be displayed on the notifications page; only a stripped-down 
   *    set of FBML tags that result in text and links is allowed
   * @return a URL, possibly null, to which the user should be redirected to finalize
   * the sending of the email
   * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.sendEmail">
   *      Developers Wiki: notifications.send</a>
   */
  public void notifications_send(CharSequence notification)
    throws FacebookException, IOException {
      Long currentUser = this.users_getLoggedInUser();
      Collection<Long> coll = new ArrayList<Long>();
      coll.add(currentUser);
      notifications_send(coll, notification);
  }

  /**
   * Sends a notification email to the specified users, who must have added your application.
   * You can send five (5) emails to a user per day. Requires a session key for desktop applications, which may only 
   * send email to the person whose session it is. This method does not require a session for Web applications. 
   * Either <code>fbml</code> or <code>text</code> must be specified.
   * 
   * @param recipientIds up to 100 user ids to which the message is to be sent
   * @param subject the subject of the notification email (optional)
   * @param fbml markup to be sent to the specified users via email; only a stripped-down set of FBML tags
   *    that result in text, links and linebreaks is allowed
   * @param text the plain text to send to the specified users via email
   * @return a comma-separated list of the IDs of the users to whom the email was successfully sent
   * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.send">
   *      Developers Wiki: notifications.sendEmail</a>
   *      
   * @deprecated provided for legacy support only, please use one of the alternate notifications_sendEmail calls.
   */
  public String notifications_sendEmailStr(Collection<Long> recipientIds, CharSequence subject, CharSequence fbml, CharSequence text)
    throws FacebookException, IOException {
    if (null == recipientIds || recipientIds.isEmpty()) {
      throw new IllegalArgumentException("List of email recipients cannot be empty");
    }
    boolean hasText = null != text && (0 != text.length());
    boolean hasFbml = null != fbml && (0 != fbml.length());
    if (!hasText && !hasFbml) {
      throw new IllegalArgumentException("Text and/or fbml must not be empty");
    }
    ArrayList<Pair<String, CharSequence>> args = new ArrayList<Pair<String, CharSequence>>(4);
    args.add(new Pair<String, CharSequence>("recipients", delimit(recipientIds)));
    args.add(new Pair<String, CharSequence>("subject", subject));
    if (hasText) {
      args.add(new Pair<String, CharSequence>("text", text));
    }
    if (hasFbml) {
      args.add(new Pair<String, CharSequence>("fbml", fbml));
    }
    // this method requires a session only if we're dealing with a desktop app
    T result = this.callMethod(this.isDesktop() ? FacebookMethod.NOTIFICATIONS_SEND_EMAIL_SESSION
                 : FacebookMethod.NOTIFICATIONS_SEND_EMAIL, args);
    return extractString(result);
  }

  /**
   * Sends a notification email to the specified users, who must have added your application.
   * You can send five (5) emails to a user per day. Requires a session key for desktop applications, which may only
   * send email to the person whose session it is. This method does not require a session for Web applications.
   *
   * @param recipientIds up to 100 user ids to which the message is to be sent
   * @param subject the subject of the notification email (optional)
   * @param fbml markup to be sent to the specified users via email; only a stripped-down set of FBML
   *    that allows only tags that result in text, links and linebreaks is allowed
   * @return a comma-separated list of the IDs of the users to whom the email was successfully sent
   * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.send">
   *      Developers Wiki: notifications.sendEmail</a>
   *      
   * @deprecated provided for legacy support only, please use one of the alternate notifications_sendEmail calls.
   */
  public String notifications_sendEmail(Collection<Long> recipientIds, CharSequence subject, CharSequence fbml)
    throws FacebookException, IOException {
    return notifications_sendEmailStr(recipientIds, subject, fbml, /*text*/null);
  }

  /**
   * Sends a notification email to the specified users, who must have added your application.
   * You can send five (5) emails to a user per day. Requires a session key for desktop applications, which may only
   * send email to the person whose session it is. This method does not require a session for Web applications.
   *
   * @param recipientIds up to 100 user ids to which the message is to be sent
   * @param subject the subject of the notification email (optional)
   * @param text the plain text to send to the specified users via email
   * @return a comma-separated list of the IDs of the users to whom the email was successfully sent
   * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.sendEmail">
   *      Developers Wiki: notifications.sendEmail</a>
   *      
   * @deprecated provided for legacy support only, please use one of the alternate notifications_sendEmail calls.
   */
  public String notifications_sendEmailPlain(Collection<Long> recipientIds, CharSequence subject, CharSequence text)
    throws FacebookException, IOException {
    return notifications_sendEmailStr(recipientIds, subject, /*fbml*/null, text);
  }
  
  public boolean profile_setFBML(Long userId, String profileFbml, String actionFbml, String mobileFbml) throws FacebookException, IOException {
      Collection<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
      params.add(new Pair<String, CharSequence>("uid", Long.toString(userId)));
      if ((profileFbml != null) && (! "".equals(profileFbml))) {
          params.add(new Pair<String, CharSequence>("profile", profileFbml));
      }
      if ((actionFbml != null) && (! "".equals(actionFbml))) {
          params.add(new Pair<String, CharSequence>("profile_action", actionFbml));
      }
      if ((mobileFbml != null) && (! "".equals(mobileFbml))) {
          params.add(new Pair<String, CharSequence>("mobile_fbml", mobileFbml));
      }
      
      return extractBoolean(this.callMethod(FacebookMethod.PROFILE_SET_FBML, params));
  }
  
  /* (non-Javadoc)
   * @see com.facebook.api.IFacebookRestClient#sms_send(java.lang.String, java.lang.Integer, boolean)
   */
  public Integer sms_send(String message, Integer smsSessionId, boolean makeNewSession) throws FacebookException, IOException {
      if ((smsSessionId == null) || (smsSessionId <= 0)) {
          return this.sms_sendMessageWithSession(this.users_getLoggedInUser(), message);
      }
      else {
          this.sms_sendResponse((int)this.users_getLoggedInUser(), message, smsSessionId);
          return smsSessionId;
      }
  }
  
  /* (non-Javadoc)
   * @see com.facebook.api.IFacebookRestClient#sms_send(java.lang.Long, java.lang.String, java.lang.Integer, boolean)
   */
  public Integer sms_send(Long userId, String message, Integer smsSessionId, boolean makeNewSession) throws FacebookException, IOException {
      if ((smsSessionId == null) || (smsSessionId <= 0)) {
          return this.sms_sendMessageWithSession(userId, message);
      }
      else {
          this.sms_sendResponse(userId.intValue(), message, smsSessionId);
          return smsSessionId;
      }
  }
  
  public T data_getCookies() throws FacebookException, IOException {
      return this.data_getCookies(this.users_getLoggedInUser(), null);
  }

  public T data_getCookies(Long userId) throws FacebookException, IOException {
      return this.data_getCookies(userId, null);
  }

  public T data_getCookies(String name) throws FacebookException, IOException {
      return this.data_getCookies(this.users_getLoggedInUser(), name);
  }

  public T data_getCookies(Long userId, CharSequence name) throws FacebookException, IOException {
      ArrayList<Pair<String, CharSequence>> args = new ArrayList<Pair<String, CharSequence>>();
      args.add(new Pair<String, CharSequence>("uid", Long.toString(userId)));
      if ((name != null) && (! "".equals(name))) {
          args.add(new Pair<String, CharSequence>("name", name));
      }
      
      return this.callMethod(FacebookMethod.DATA_GET_COOKIES, args);
  }

  public boolean data_setCookie(String name, String value) throws FacebookException, IOException {
      return this.data_setCookie(this.users_getLoggedInUser(), name, value, null, null);
  }

  public boolean data_setCookie(String name, String value, String path) throws FacebookException, IOException {
      return this.data_setCookie(this.users_getLoggedInUser(), name, value, null, path);
  }

  public boolean data_setCookie(Long userId, CharSequence name, CharSequence value) throws FacebookException, IOException {
      return this.data_setCookie(userId, name, value, null, null);
  }

  public boolean data_setCookie(Long userId, CharSequence name, CharSequence value, CharSequence path) throws FacebookException, IOException {
      return this.data_setCookie(userId, name, value, null, path);
  }

  public boolean data_setCookie(String name, String value, Long expires) throws FacebookException, IOException {
      return this.data_setCookie(this.users_getLoggedInUser(), name, value, expires, null);
  }

  public boolean data_setCookie(String name, String value, Long expires, String path) throws FacebookException, IOException {
      return this.data_setCookie(this.users_getLoggedInUser(), name, value, expires, path);
  }

  public boolean data_setCookie(Long userId, CharSequence name, CharSequence value, Long expires) throws FacebookException, IOException {
      return this.data_setCookie(userId, name, value, expires, null);
  }

  public boolean data_setCookie(Long userId, CharSequence name, CharSequence value, Long expires, CharSequence path) throws FacebookException, IOException {
      if ((name == null) || ("".equals(name))) {
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The cookie name cannot be null or empty!");
      }
      if (value == null) {
          value = "";
      }
      
      T doc;
      ArrayList<Pair<String, CharSequence>> args = new ArrayList<Pair<String, CharSequence>>();
      args.add(new Pair<String, CharSequence>("uid", Long.toString(userId)));
      args.add(new Pair<String, CharSequence>("name", name));
      args.add(new Pair<String, CharSequence>("value", value));
      if ((expires != null) && (expires > 0)) {
          args.add(new Pair<String, CharSequence>("expires", expires.toString()));
      }
      if ((path != null) && (! "".equals(path))) {
          args.add(new Pair<String, CharSequence>("path", path));
      }
      doc = this.callMethod(FacebookMethod.DATA_SET_COOKIE, args);
      
      return extractBoolean(doc);
  }
  
  public boolean admin_setAppProperties(Map<ApplicationProperty,String> properties) throws FacebookException, IOException {
      if ((properties == null) || (properties.isEmpty())) {
          //nothing to do
          return true;
      }
      
      //Facebook is nonspecific about how they want the parameters encoded in JSON, so we make two attempts
      JSONObject encoding1 = new JSONObject();
      JSONArray encoding2 = new JSONArray();
      for (ApplicationProperty property : properties.keySet()) {
          JSONObject temp = new JSONObject();
          if (property.getType().equals("string")) {
              //simple case, just treat it as a literal string
              try {
                  encoding1.put(property.getName(), properties.get(property));
                  temp.put(property.getName(), properties.get(property));
                  encoding2.put(temp);
              }
              catch (JSONException ignored) {}
          }
          else {
              //we need to parse a boolean value
              String val = properties.get(property);
              if ((val == null) || (val.equals("")) || (val.equalsIgnoreCase("false")) || (val.equals("0"))) {
                  //false
                  val = "0";
              }
              else {
                  //true
                  val = "1";
              }
              try {
                  encoding1.put(property.getName(), val);
                  temp.put(property.getName(), val);
                  encoding2.put(temp);
              }
              catch (JSONException ignored) {}
          }
      }
      
      //now we've built our JSON-encoded parameter, so attempt to set the properties
      try {
          //first assume that Facebook is sensible enough to be able to undestand an associative array
          T d = this.callMethod(FacebookMethod.ADMIN_SET_APP_PROPERTIES,
                  new Pair<String, CharSequence>("properties", encoding1.toString()));
          return extractBoolean(d);
      }
      catch (FacebookException e) {
          //if that didn't work, try the more convoluted encoding (which matches what they send back in response to admin_getAppProperties calls)
          T d = this.callMethod(FacebookMethod.ADMIN_SET_APP_PROPERTIES,
                  new Pair<String, CharSequence>("properties", encoding2.toString()));
          return extractBoolean(d);
      }
  }
  
  /**
   * @deprecated use admin_getAppPropertiesMap() instead
   */
  public JSONObject admin_getAppProperties(Collection<ApplicationProperty> properties) throws FacebookException, IOException {
      String json = this.admin_getAppPropertiesAsString(properties);
      if (json == null) {
          return null;
      }
      try {
          if (json.matches("\\{.*\\}")) {
              return new JSONObject(json);
          }
          else {
              JSONArray temp = new JSONArray(json);
              JSONObject result = new JSONObject();
              for (int count = 0; count < temp.length(); count++) {
                  JSONObject obj = (JSONObject)temp.get(count);
                  Iterator it = obj.keys();
                  while (it.hasNext()) {
                      String next = (String)it.next();
                      result.put(next, obj.get(next));
                  }
              }
              return result;
          }
      }
      catch (Exception e) {
          //response failed to parse
          throw new FacebookException(ErrorCode.GEN_SERVICE_ERROR, "Failed to parse server response:  " + json);
      }
  }
  
  public Map<ApplicationProperty, String> admin_getAppPropertiesMap(Collection<ApplicationProperty> properties) throws FacebookException, IOException {
      Map<ApplicationProperty, String> result = new LinkedHashMap<ApplicationProperty, String>();
      String json = this.admin_getAppPropertiesAsString(properties);
      if (json == null) {
          return null;
      }
      if (json.matches("\\{.*\\}")) {
          json = json.substring(1, json.lastIndexOf("}"));
      }
      else {
          json = json.substring(1, json.lastIndexOf("]"));
      }
      String[] parts = json.split("\\,");
      for (String part : parts) {
          parseFragment(part, result);
      }
      
      return result;
  }
  
  static Map<ApplicationProperty, String> parseProperties(String json) {
      Map<ApplicationProperty, String> result = new HashMap<ApplicationProperty, String>();
      if (json == null) {
          return null;
      }
      if (json.matches("\\{.*\\}")) {
          json = json.substring(1, json.lastIndexOf("}"));
      }
      else {
          json = json.substring(1, json.lastIndexOf("]"));
      }
      String[] parts = json.split("\\,");
      for (String part : parts) {
          parseFragment(part, result);
      }
      
      return result;
  }
  
  private static void parseFragment(String fragment, Map<ApplicationProperty, String> result) {
      if (fragment.startsWith("{")) {
          fragment = fragment.substring(1, fragment.lastIndexOf("}"));
      }
      String keyString = fragment.substring(1);
      keyString = keyString.substring(0, keyString.indexOf('"'));
      ApplicationProperty key = ApplicationProperty.getPropertyForString(keyString);
      String value = fragment.substring(fragment.indexOf(":") + 1).replaceAll("\\\\", "");  //strip escape characters
      if (key.getType().equals("string")) {
          result.put(key, value.substring(1, value.lastIndexOf('"')));
      }
      else {
          if (value.equals("1")) {
              result.put(key, "true");
          }
          else {
              result.put(key, "false");
          }
      }
  }
  
  public boolean feed_publishTemplatizedAction(CharSequence titleTemplate) throws FacebookException, IOException {
      return this.feed_publishTemplatizedAction(titleTemplate, null);
  }

  public boolean feed_publishTemplatizedAction(CharSequence titleTemplate, Long pageActorId) throws FacebookException, IOException {
      return this.feed_publishTemplatizedAction(titleTemplate, null, null, null, null, null, null, pageActorId);
  }

  public boolean feed_publishTemplatizedAction(CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate, Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object, URL>> images, Long pageActorId) throws FacebookException, IOException {
      assert null != titleTemplate && !"".equals(titleTemplate); 
      
      FacebookMethod method = FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION;
      ArrayList<Pair<String, CharSequence>> params =
        new ArrayList<Pair<String, CharSequence>>(method.numParams());

      params.add(new Pair<String, CharSequence>("title_template", titleTemplate));
      if (null != titleData && !titleData.isEmpty()) {
        JSONObject titleDataJson = new JSONObject();
        for (String key : titleData.keySet()) {
            try {
                titleDataJson.put(key, titleData.get(key));
            }
            catch (Exception ignored) {}
        }
        params.add(new Pair<String, CharSequence>("title_data", titleDataJson.toString()));
      }
      
      if (null != bodyTemplate && !"".equals(bodyTemplate)) {
        params.add(new Pair<String, CharSequence>("body_template", bodyTemplate));
        if (null != bodyData && !bodyData.isEmpty()) {
          JSONObject bodyDataJson = new JSONObject();
          for (String key : bodyData.keySet()) {
              try {
                  bodyDataJson.put(key, bodyData.get(key));
              }
              catch (Exception ignored) {}
          }
          params.add(new Pair<String, CharSequence>("body_data", bodyDataJson.toString()));
        }
      }

      if (null != bodyTemplate && !"".equals(bodyTemplate)) {
        params.add(new Pair<String, CharSequence>("body_template", bodyTemplate));
      }
      
      if (null != targetIds && !targetIds.isEmpty()) {
        params.add(new Pair<String, CharSequence>("target_ids", delimit(targetIds)));      
      }
      
      if (bodyGeneral != null) {
          params.add(new Pair<String, CharSequence>("body_general", bodyGeneral));
      }
      
      if (pageActorId != null) {
          params.add(new Pair<String, CharSequence>("page_actor_id", Long.toString(pageActorId)));
      }
      
      handleFeedImages(params, images);

      return extractBoolean(this.callMethod(method, params));
  }

  public boolean profile_setFBML(CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup) throws FacebookException, IOException {
      return this.profile_setFBML(this.users_getLoggedInUser(), profileFbmlMarkup == null ? null : profileFbmlMarkup.toString(), profileActionFbmlMarkup == null ? null : profileActionFbmlMarkup.toString(), null);
  }

  public boolean profile_setFBML(CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, Long profileId) throws FacebookException, IOException {
      return this.profile_setFBML(profileId, profileFbmlMarkup == null ? null : profileFbmlMarkup.toString(), profileActionFbmlMarkup == null ? null : profileActionFbmlMarkup.toString(), null);
  }

  public boolean profile_setFBML(CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup) throws FacebookException, IOException {
      return this.profile_setFBML(this.users_getLoggedInUser(), profileFbmlMarkup == null ? null : profileFbmlMarkup.toString(), profileActionFbmlMarkup == null ? null : profileActionFbmlMarkup.toString(), mobileFbmlMarkup == null ? null : mobileFbmlMarkup.toString());
  }

  public boolean profile_setFBML(CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup, Long profileId) throws FacebookException, IOException {
      return this.profile_setFBML(profileId, profileFbmlMarkup == null ? null : profileFbmlMarkup.toString(), profileActionFbmlMarkup == null ? null : profileActionFbmlMarkup.toString(), mobileFbmlMarkup == null ? null : mobileFbmlMarkup.toString());
  }

  public boolean profile_setMobileFBML(CharSequence fbmlMarkup) throws FacebookException, IOException {
      return this.profile_setFBML(this.users_getLoggedInUser(), null, null, fbmlMarkup == null ? null : fbmlMarkup.toString());
  }

  public boolean profile_setMobileFBML(CharSequence fbmlMarkup, Long profileId) throws FacebookException, IOException {
      return this.profile_setFBML(profileId, null, null, fbmlMarkup == null ? null : fbmlMarkup.toString());
  }

  public boolean profile_setProfileActionFBML(CharSequence fbmlMarkup) throws FacebookException, IOException {
      return this.profile_setFBML(this.users_getLoggedInUser(), null, fbmlMarkup == null ? null : fbmlMarkup.toString(), null);
  }

  public boolean profile_setProfileActionFBML(CharSequence fbmlMarkup, Long profileId) throws FacebookException, IOException {
      return this.profile_setFBML(profileId, null, fbmlMarkup == null ? null : fbmlMarkup.toString(), null);
  }

  public boolean profile_setProfileFBML(CharSequence fbmlMarkup) throws FacebookException, IOException {
      return this.profile_setFBML(this.users_getLoggedInUser(), fbmlMarkup == null ? null : fbmlMarkup.toString(), null, null);
  }

  public boolean profile_setProfileFBML(CharSequence fbmlMarkup, Long profileId) throws FacebookException, IOException {
      return this.profile_setFBML(profileId, fbmlMarkup == null ? null : fbmlMarkup.toString(), null, null);
  }
  
  /**
   * Retrieves the friends of the currently logged in user that are members of the friends list
   * with ID <code>friendListId</code>.
   * 
   * @param friendListId the friend list for which friends should be fetched. if <code>null</code>,
   *            all friends will be retrieved.
   * @return T of friends
   * @see <a href="http://wiki.developers.facebook.com/index.php/Friends.get"> Developers Wiki:
   *      Friends.get</a>
   */
  public T friends_get(Long friendListId) throws FacebookException, IOException {
      FacebookMethod method = FacebookMethod.FRIENDS_GET;
      Collection<Pair<String,CharSequence>> params = new ArrayList<Pair<String,CharSequence>>(
              method.numParams());
      if (null != friendListId) {
          if (0L >= friendListId) {
              throw new IllegalArgumentException("given invalid friendListId "
                      + friendListId.toString());
          }
          params.add(new Pair<String,CharSequence>("flid", friendListId.toString()));
      }
      return this.callMethod(method, params);
  }

  /**
   * Retrieves the friend lists of the currently logged in user.
   * 
   * @return T of friend lists
   * @see <a href="http://wiki.developers.facebook.com/index.php/Friends.getLists"> Developers
   *      Wiki: Friends.getLists</a>
   */
  public T friends_getLists() throws FacebookException, IOException {
      return this.callMethod(FacebookMethod.FRIENDS_GET_LISTS);
  }
  
  /**
   * Sets several property values for an application. The properties available are analogous to
   * the ones editable via the Facebook Developer application. A session is not required to use
   * this method.
   * 
   * @param properties an ApplicationPropertySet that is translated into a single JSON String.
   * @return a boolean indicating whether the properties were successfully set
   */
  public boolean admin_setAppProperties(ApplicationPropertySet properties)
          throws FacebookException, IOException {
      if (null == properties || properties.isEmpty()) {
          throw new IllegalArgumentException(
                  "expecting a non-empty set of application properties");
      }
      return extractBoolean(this.callMethod(FacebookMethod.ADMIN_SET_APP_PROPERTIES,
              new Pair<String,CharSequence>("properties", properties.toJsonString())));
  }

  /**
   * Gets property values previously set for an application on either the Facebook Developer
   * application or the with the <code>admin.setAppProperties</code> call. A session is not
   * required to use this method.
   * 
   * @param properties an enumeration of the properties to get
   * @return an ApplicationPropertySet
   * @see ApplicationProperty
   * @see <a href="http://wiki.developers.facebook.com/index.php/Admin.getAppProperties">
   *      Developers Wiki: Admin.getAppProperties</a>
   */
  public ApplicationPropertySet admin_getAppPropertiesAsSet(EnumSet<ApplicationProperty> properties)
          throws FacebookException, IOException {
      String propJson = this.admin_getAppPropertiesAsString(properties);
      return new ApplicationPropertySet(propJson);
  }
  
  /**
   * Starts a batch of queries.  Any API calls made after invoking 'beginBatch' will be deferred 
   * until the next time you call 'executeBatch', at which time they will be processed as a 
   * batch query.  All API calls made in the interim will return null as their result.
   */
  public void beginBatch() {
      this.batchMode = true;
      this.queries = new ArrayList<BatchQuery>();
  }
  
  protected String encodeMethods(List<BatchQuery> queries) throws FacebookException {
      JSONArray result = new JSONArray();
      for (BatchQuery query : queries) {
          if (query.getMethod().takesFile()) {
              throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "File upload API calls cannot be batched:  " 
                      + query.getMethod().methodName());
          }
          result.put(delimit(query.getParams().entrySet(), "&", "=", true));
      }
      
      return result.toString();
  }
  
  /**
   * Executes a batch of queries.  It is your responsibility to encode the method feed 
   * correctly.  It is not recommended that you call this method directly.  Instead use 
   * 'beginBatch' and 'executeBatch', which will take care of the hard parts for you.
   * 
   * @param methods A JSON encoded array of strings. Each element in the array should contain 
   *        the full parameters for a method, including method name, sig, etc. Currently, there 
   *        is a maximum limit of 15 elements in the array.
   * @param serial An optional parameter to indicate whether the methods in the method_feed 
   *               must be executed in order. The default value is false.
   * 
   * @return a result containing the response to each individual query in the batch.
   */
  public T batch_run(String methods, boolean serial) throws FacebookException,
  IOException {
      ArrayList<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
      params.add(new Pair<String, CharSequence>("method_feed", methods));
      if (serial)
          params.add(new Pair<String, CharSequence>("serial_only", "1"));
        
      return this.callMethod(FacebookMethod.BATCH_RUN, params);
  }
  
  /**
   * Gets the public information about the specified application.  Only one of the 3 parameters needs to be 
   * specified.  
   * 
   * @param applicationId the id of the application to get the info for.
   * @param applicationKey the public API key of the application to get the info for.
   * @param applicationCanvas the canvas-page name of the application to get the info for.
   * 
   * @return the public information for the specified application
   */
  public T application_getPublicInfo(Long applicationId, String applicationKey, String applicationCanvas) throws FacebookException,
  IOException {
      ArrayList<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
      
      if ((applicationId != null) && (applicationId > 0)) {
          params.add(new Pair<String, CharSequence>("application_id", Long.toString(applicationId)));
      }
      else if ((applicationKey != null) && (! "".equals(applicationKey))) {
          params.add(new Pair<String, CharSequence>("application_api_key", applicationKey));
      }
      else if ((applicationCanvas != null) && (! "".equals(applicationCanvas))) {
          params.add(new Pair<String, CharSequence>("application_canvas_name", applicationCanvas));
      }
      else {
          //we need at least one of them to be valid
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "You must specify at least on of {applicationId, applicationKey, applicationCanvas}");
      }
      
      return this.callMethod(FacebookMethod.APPLICATION_GET_PUBLIC_INFO, params);
  }
  
  /**
   * Gets the public information about the specified application, by application id.
   * 
   * @param applicationId the id of the application to get the info for.
   * 
   * @return the public information for the specified application
   */
  public T application_getPublicInfoById(Long applicationId) throws FacebookException,
  IOException {
      return application_getPublicInfo(applicationId, null, null);
  }
  
  /**
   * Gets the public information about the specified application, by API key.
   * 
   * @param applicationKey the public API key of the application to get the info for.
   * 
   * @return the public information for the specified application
   */
  public T application_getPublicInfoByApiKey(String applicationKey) throws FacebookException,
  IOException {
      return application_getPublicInfo(null, applicationKey, null);
  }
  
  /**
   * Gets the public information about the specified application, by canvas-page name.
   * 
   * @param applicationCanvas the canvas-page name of the application to get the info for.
   * 
   * @return the public information for the specified application
   */
  public T application_getPublicInfoByCanvasName(String applicationCanvas) throws FacebookException,
  IOException {
      return application_getPublicInfo(null, null, applicationCanvas);
  }
  
  public int admin_getAllocation(String allocationType) throws FacebookException, IOException {
      return extractInt(this.callMethod(FacebookMethod.ADMIN_GET_ALLOCATION,
              new Pair<String,CharSequence>("integration_point_name ", allocationType)));
  }

  public int admin_getNotificationAllocation() throws FacebookException, IOException {
      return this.admin_getAllocation("notifications_per_day");
  }

  public int admin_getRequestAllocation() throws FacebookException, IOException {
      return this.admin_getAllocation("requests_per_day");
  }
}
