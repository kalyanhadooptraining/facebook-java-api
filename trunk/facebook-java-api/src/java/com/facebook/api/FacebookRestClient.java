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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.facebook.api.schema.MarketplaceGetCategoriesResponse;
import com.facebook.api.schema.MarketplaceGetSubCategoriesResponse;

/**
 * Facebook API client.  Allocate an instance of this class to make Facebook API requests.
 */
public class FacebookRestClient {
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
  static {
    try {
      SERVER_URL = new URL(SERVER_ADDR);
      HTTPS_SERVER_URL = new URL(HTTPS_SERVER_ADDR);
    }
    catch (MalformedURLException e) {
      System.err.println("MalformedURLException: " + e.getMessage());
      System.exit(1);
    }
  }

  private final String _secret;
  private final String _apiKey;
  private final URL _serverUrl;
  private String rawResponse;

  private String _sessionKey; // filled in when session is established
  private boolean _isDesktop = false;
  private String _sessionSecret; // only used for desktop apps
  private long _userId;

  /**
   * number of params that the client automatically appends to every API call
   */
  public static int NUM_AUTOAPPENDED_PARAMS = 5;
  private static boolean DEBUG = false;
  private Boolean _debug = null;

  private File _uploadFile = null;

  /**
   * Constructor
   * 
   * @param apiKey the developer's API key
   * @param secret the developer's secret key
   */
  public FacebookRestClient(String apiKey, String secret) {
    this(SERVER_URL, apiKey, secret, null);
  }

  /**
   * Constructor
   * 
   * @param apiKey the developer's API key
   * @param secret the developer's secret key
   * @param sessionKey the session-id to use
   */
  public FacebookRestClient(String apiKey, String secret, String sessionKey) {
    this(SERVER_URL, apiKey, secret, sessionKey);
  }

  /**
   * Constructor
   * 
   * @param serverAddr the URL of the Facebook API server to use, allows overriding of the default API server.
   * @param apiKey the developer's API key
   * @param secret the developer's secret key
   * @param sessionKey the session-id to use
   * 
   * @throws MalformedURLException if the specified serverAddr is invalid
   */
  public FacebookRestClient(String serverAddr, String apiKey, String secret,
                            String sessionKey) throws MalformedURLException {
    this(new URL(serverAddr), apiKey, secret, sessionKey);
  }

  /**
   * Constructor
   * 
   * @param serverUrl the URL of the Facebook API server to use, allows overriding of the default API server.
   * @param apiKey the developer's API key
   * @param secret the developer's secret key
   * @param sessionKey the session-id to use
   */
  public FacebookRestClient(URL serverUrl, String apiKey, String secret, String sessionKey) {
    _sessionKey = sessionKey;
    _apiKey = apiKey;
    _secret = secret;
    _serverUrl = (null != serverUrl) ? serverUrl : SERVER_URL;
  }

  /**
   * Set global debugging on.
   * 
   * @param isDebug true to enable debugging
   *                false to disable debugging
   */
  public static void setDebugAll(boolean isDebug) {
    FacebookRestClient.DEBUG = isDebug;
  }

  /**
   * Set debugging on for this instance only.
   * 
   * @param isDebug true to enable debugging
   *                false to disable debugging
   */
  //FIXME:  do we really need both of these?
  public void setDebug(boolean isDebug) {
    _debug = isDebug;
  }

  /**
   * Check to see if debug mode is enabled.
   * 
   * @return true if debugging is enabled
   *         false otherwise
   */
  public boolean isDebug() {
    return (null == _debug) ? FacebookRestClient.DEBUG : _debug.booleanValue();
  }

  /**
   * Check to see if the client is running in desktop mode.
   * 
   * @return true if the client is running in desktop mode
   *         false otherwise
   */
  public boolean isDesktop() {
    return this._isDesktop;
  }

  /**
   * Enable/disable desktop mode.
   * 
   * @param isDesktop true to enable desktop application mode
   *                  false to disable desktop application mode
   */
  public void setIsDesktop(boolean isDesktop) {
    this._isDesktop = isDesktop;
  }

  /**
   * Prints out the DOM tree.
   * 
   * @param n the parent node to start printing from
   * @param prefix string to append to output, should not be null
   */
  public static void printDom(Node n, String prefix) {
    String outString = prefix;
    if (n.getNodeType() == Node.TEXT_NODE) {
      outString += "'" + n.getTextContent().trim() + "'";
    }
    else {
      outString += n.getNodeName();
    }
    if (DEBUG) {
        System.out.println(outString);
    }
    NodeList children = n.getChildNodes();
    int length = children.getLength();
    for (int i = 0; i < length; i++) {
      FacebookRestClient.printDom(children.item(i), prefix + "  ");
    }
  }

  private static CharSequence delimit(Collection<?> iterable) {
    // could add a thread-safe version that uses StringBuffer as well
    if (iterable == null || iterable.isEmpty())
      return null;

    StringBuilder buffer = new StringBuilder();
    boolean notFirst = false;
    for (Object item: iterable) {
      if (notFirst)
        buffer.append(",");
      else
        notFirst = true;
      buffer.append(item.toString());
    }
    return buffer;
  }

  protected static CharSequence delimit(Collection<Map.Entry<String, CharSequence>> entries,
                                        CharSequence delimiter, CharSequence equals,
                                        boolean doEncode) {
    if (entries == null || entries.isEmpty())
      return null;

    StringBuilder buffer = new StringBuilder();
    boolean notFirst = false;
    for (Map.Entry<String, CharSequence> entry: entries) {
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
   * Call the specified method, with the given parameters, and return a DOM tree with the results.
   *
   * @param method the fieldName of the method
   * @param paramPairs a list of arguments to the method
   * @throws Exception with a description of any errors given to us by the server.
   */
  protected Document callMethod(FacebookMethod method,
                                Pair<String, CharSequence>... paramPairs) throws FacebookException,
                                                                                 IOException {
    return callMethod(method, Arrays.asList(paramPairs));
  }

  /**
   * Call the specified method, with the given parameters, and return a DOM tree with the results.
   *
   * @param method the fieldName of the method
   * @param paramPairs a list of arguments to the method
   * @throws Exception with a description of any errors given to us by the server.
   */
  protected Document callMethod(FacebookMethod method,
                                Collection<Pair<String, CharSequence>> paramPairs) throws FacebookException,
                                                                                          IOException {
    this.rawResponse = null;
    HashMap<String, CharSequence> params =
      new HashMap<String, CharSequence>(2 * method.numTotalParams());

    params.put("method", method.methodName());
    params.put("api_key", _apiKey);
    params.put("v", TARGET_API_VERSION);
    if (method.requiresSession()) {
      params.put("call_id", Long.toString(System.currentTimeMillis()));
      params.put("session_key", _sessionKey);
    }
    CharSequence oldVal;
    for (Pair<String, CharSequence> p: paramPairs) {
      oldVal = params.put(p.first, p.second);
      if (oldVal != null)
          System.out.println("For parameter " + p.first + ", overwrote old value " + oldVal +
                " with new value " + p.second + ".");
    }

    assert (!params.containsKey("sig"));
    String signature = generateSignature(FacebookSignatureUtil.convert(params.entrySet()), method.requiresSession());
    params.put("sig", signature);

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      boolean doHttps = this.isDesktop() && FacebookMethod.AUTH_GET_SESSION.equals(method);
      InputStream data =
        method.takesFile() ? postFileRequest(method.methodName(), params) : postRequest(method.methodName(),
                                                                                        params,
                                                                                        doHttps,
                                                                                        true);
      /*int current = 0;
      StringBuffer buffer = new StringBuffer();
      while (current != -1) {
          current = data.read();
          if (current != -1) {
              buffer.append((char)current);
          }
      }*/
      
      BufferedReader in = new BufferedReader(new InputStreamReader(data, "UTF-8"));
      StringBuffer buffer = new StringBuffer();
      String line;
      while ((line = in.readLine()) != null) {
        buffer.append(line);
      }
      
      
      String xmlResp = new String(buffer);
      this.rawResponse = xmlResp;

      Document doc = builder.parse(new ByteArrayInputStream(xmlResp.getBytes("UTF-8")));
      doc.normalizeDocument();
      stripEmptyTextNodes(doc);

      if (isDebug())
        FacebookRestClient.printDom(doc, method.methodName() + "| "); // TEST
      NodeList errors = doc.getElementsByTagName(ERROR_TAG);
      if (errors.getLength() > 0) {
        int errorCode =
          Integer.parseInt(errors.item(0).getFirstChild().getFirstChild().getTextContent());
        String message = errors.item(0).getFirstChild().getNextSibling().getTextContent();
        // FIXME: additional printing done for debugging only
        System.out.println("Facebook returns error code " + errorCode);
        for (Map.Entry<String,CharSequence> entry : params.entrySet())
            System.out.println("  - " + entry.getKey() + " -> " + entry.getValue());
        throw new FacebookException(errorCode, message);
      }
      return doc;
    }
    catch (java.net.SocketException ex) {
        System.err.println("Socket exception when calling facebook method: " + ex.getMessage());
    }
    catch (javax.xml.parsers.ParserConfigurationException ex) {
        System.err.println("huh?");
        ex.printStackTrace();
    }
    catch (org.xml.sax.SAXException ex) {
      throw new IOException("error parsing xml");
    }
    return null;
  }

  /**
   * Returns a string representation for the last API response recieved from Facebook, exactly as sent by the API server.
   * 
   * Note that calling this method consumes the data held in the internal buffer, and thus it may only be called once per API 
   * call.
   * 
   * @return a String representation of the last API response sent by Facebook
   */
  public String getRawResponse() {
      String result = this.rawResponse;
      this.rawResponse = null;
      return result;
  }

  /**
   * Hack...since DOM reads newlines as textnodes we want to strip out those
   * nodes to make it easier to use the tree.
   */
  private static void stripEmptyTextNodes(Node n) {
    NodeList children = n.getChildNodes();
    int length = children.getLength();
    for (int i = 0; i < length; i++) {
      Node c = children.item(i);
      if (!c.hasChildNodes() && c.getNodeType() == Node.TEXT_NODE &&
          c.getTextContent().trim().length() == 0) {
        n.removeChild(c);
        i--;
        length--;
        children = n.getChildNodes();
      }
      else {
        stripEmptyTextNodes(c);
      }
    }
  }

  private String generateSignature(List<String> params, boolean requiresSession) {
    String secret = (isDesktop() && requiresSession) ? this._sessionSecret : this._secret;
    return FacebookSignatureUtil.generateSignature(params, secret);
  }

  private static String encode(CharSequence target) {
    String result = (target != null) ? target.toString() : "";
    try {
      result = URLEncoder.encode(result, "UTF8");
    }
    catch (UnsupportedEncodingException e) {
        System.err.println("Unsuccessful attempt to encode '" + result + "' into UTF8");
    }
    return result;
  }

  private InputStream postRequest(CharSequence method, Map<String, CharSequence> params,
                                  boolean doHttps, boolean doEncode) throws IOException {
    CharSequence buffer = (null == params) ? "" : delimit(params.entrySet(), "&", "=", doEncode);
    URL serverUrl = (doHttps) ? HTTPS_SERVER_URL : _serverUrl;
    if (isDebug() && DEBUG) {
        System.out.println(method);
        System.out.println(" POST: ");
        System.out.println(serverUrl.toString());
        System.out.println("/");
        System.out.println(buffer);
    }

    HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
    try {
      conn.setRequestMethod("POST");
    }
    catch (ProtocolException ex) {
        System.err.println("huh?");
        ex.printStackTrace();
    }
    conn.setDoOutput(true);
    conn.connect();
    conn.getOutputStream().write(buffer.toString().getBytes());

    return conn.getInputStream();
  }

  /**
   * Sets the FBML for a user's profile, including the content for both the profile box
   * and the profile actions.
   * @param userId - the user whose profile FBML to set
   * @param fbmlMarkup - refer to the FBML documentation for a description of the markup and its role in various contexts
   * @return a boolean indicating whether the FBML was successfully set
   */
  public boolean profile_setFBML(CharSequence fbmlMarkup, Long userId) throws FacebookException, IOException {

    return extractBoolean(this.callMethod(FacebookMethod.PROFILE_SET_FBML,
                          new Pair<String, CharSequence>("uid", Long.toString(userId)),
                          new Pair<String, CharSequence>("markup", fbmlMarkup)));

  }

  /**
   * Gets the FBML for a user's profile, including the content for both the profile box
   * and the profile actions.
   * @param userId - the user whose profile FBML to set
   * @return a Document containing FBML markup
   */
  public Document profile_getFBML(Long userId) throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.PROFILE_GET_FBML,
                          new Pair<String, CharSequence>("uid", Long.toString(userId)));

  }

  /**
   * Recaches the referenced url.
   * @param url string representing the URL to refresh
   * @return boolean indicating whether the refresh succeeded
   */
  public boolean fbml_refreshRefUrl(String url) throws FacebookException, IOException {
    return fbml_refreshRefUrl(new URL(url));
  }

  /**
   * Recaches the referenced url.
   * @param url the URL to refresh
   * @return boolean indicating whether the refresh succeeded
   */
  public boolean fbml_refreshRefUrl(URL url) throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.FBML_REFRESH_REF_URL,
                                          new Pair<String, CharSequence>("url", url.toString())));
  }

  /**
   * Recaches the image with the specified imageUrl.
   * @param imageUrl String representing the image URL to refresh
   * @return boolean indicating whether the refresh succeeded
   */
  public boolean fbml_refreshImgSrc(String imageUrl) throws FacebookException, IOException {
    return fbml_refreshImgSrc(new URL(imageUrl));
  }

  /**
   * Recaches the image with the specified imageUrl.
   * @param imageUrl the image URL to refresh
   * @return boolean indicating whether the refresh succeeded
   */
  public boolean fbml_refreshImgSrc(URL imageUrl) throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.FBML_REFRESH_IMG_SRC,
                          new Pair<String, CharSequence>("url", imageUrl.toString())));
  }
  
  /**
   * Publishes a templatized action for the current user.  The action will appear in their minifeed, 
   * and may appear in their friends' newsfeeds depending upon a number of different factors.  When 
   * a template match exists between multiple distinct users (like "Bob recommends Bizou" and "Sally 
   * recommends Bizou"), the feed entries may be combined in the newfeed (to something like "Bob and sally 
   * recommend Bizou").  This happens automatically, and *only* if the template match between the two
   * feed entries is identical.<br />
   * <br />
   * Feed entries are not aggregated for a single user (so "Bob recommends Bizou" and "Bob recommends Le 
   * Charm" *will not* become "Bob recommends Bizou and Le Charm").<br />
   * <br />
   * If the user's action involves one or more of their friends, list them in the 'targetIds' parameter.  
   * For example, if you have "Bob says hi to Sally and Susie", and Sally's UID is 1, and Susie's UID is 2, 
   * then pass a 'targetIds' paramters of "1,2".  If you pass this parameter, you can use the "{target}" token 
   * in your templates.  Probably it also makes it more likely that Sally and Susie will see the feed entry 
   * in their newsfeed, relative to any other friends Bob might have.  It may be a good idea to always send 
   * a list of all the user's friends, and avoid using the "{target}" token, to maximize distribution of the 
   * story through the newsfeed.<br />
   * <br />
   * The only strictly required parameter is 'titleTemplate', which must contain the "{actor}" token somewhere 
   * inside of it.  All other parameters, options, and tokens are optional, and my be set to null if 
   * being omitted.<br />
   * <br />
   * Not that stories will only be aggregated if *all* templates match and *all* template parameters match, so 
   * if two entries have the same templateTitle and titleData, but a different bodyTemplate, they will not 
   * aggregate.  Probably it's better to use bodyGeneral instead of bodyTemplate, for the extra flexibility 
   * it provides.<br />
   * <br />
   * <br />
   * Note that this method is replacing 'feed_publishActionOfUser', which has been deprecated by Facebook.
   * For specific details, visit http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction
   * 
   * 
   * @param titleTemplate the template for the title of the feed entry, this must contain the "(actor}" token.  
   *                      Any other tokens are optional, i.e. "{actor} recommends {place}".
   * @param titleData JSON-formatted values for any tokens used in titleTemplate, with the exception of "{actor}" 
   *                  and "{target}", which Facebook populates automatically, i.e. "{place: "<a href='http://www.bizou.com'>Bizou</a>"}".
   * @param bodyTemplate the template for the body of the feed entry, works the same as 'titleTemplate', but 
   *                     is not required to contain the "{actor}" token.
   * @param bodyData works the same as titleData
   * @param bodyGeneral non-templatized content for the body, may contain markup, may not contain tokens.
   * @param pictures a list of up to 4 images to display, with optional hyperlinks for each one.
   * @param targetIds a comma-seperated list of the UID's of any friend(s) who are involved in this feed 
   *                  action (if there are any), this specifies the value of the "{target}" token.  If you 
   *                  use this token in any of your templates, you must specify a value for this parameter.
   * 
   * @return a Document representing the XML response returned from the Facebook API server.
   * 
   * @throws FacebookException if any number of bad things happen
   * @throws IOException
   */
  public Document feed_publishTemplatizedAction(String titleTemplate, String titleData, String bodyTemplate, 
          String bodyData, String bodyGeneral, Collection<Pair<URL, URL>> pictures, String targetIds) throws FacebookException, IOException {
      
      return templatizedFeedHandler(FacebookMethod.FEED_PUBLISH_TEMPLATIZED_ACTION, titleTemplate, titleData, bodyTemplate, 
              bodyData, bodyGeneral, pictures, targetIds);
  }
  
  /**
   * Publishes a templatized action for the current user.  The action will appear in their minifeed, 
   * and may appear in their friends' newsfeeds depending upon a number of different factors.  When 
   * a template match exists between multiple distinct users (like "Bob recommends Bizou" and "Sally 
   * recommends Bizou"), the feed entries may be combined in the newfeed (to something like "Bob and sally 
   * recommend Bizou").  This happens automatically, and *only* if the template match between the two
   * feed entries is identical.<br />
   * <br />
   * Feed entries are not aggregated for a single user (so "Bob recommends Bizou" and "Bob recommends Le 
   * Charm" *will not* become "Bob recommends Bizou and Le Charm").<br />
   * <br />
   * If the user's action involves one or more of their friends, list them in the 'targetIds' parameter.  
   * For example, if you have "Bob says hi to Sally and Susie", and Sally's UID is 1, and Susie's UID is 2, 
   * then pass a 'targetIds' paramters of "1,2".  If you pass this parameter, you can use the "{target}" token 
   * in your templates.  Probably it also makes it more likely that Sally and Susie will see the feed entry 
   * in their newsfeed, relative to any other friends Bob might have.  It may be a good idea to always send 
   * a list of all the user's friends, and avoid using the "{target}" token, to maximize distribution of the 
   * story through the newsfeed.<br />
   * <br />
   * The only strictly required parameter is 'titleTemplate', which must contain the "{actor}" token somewhere 
   * inside of it.  All other parameters, options, and tokens are optional, and my be set to null if 
   * being omitted.<br />
   * <br />
   * Not that stories will only be aggregated if *all* templates match and *all* template parameters match, so 
   * if two entries have the same templateTitle and titleData, but a different bodyTemplate, they will not 
   * aggregate.  Probably it's better to use bodyGeneral instead of bodyTemplate, for the extra flexibility 
   * it provides.<br />
   * <br />
   * <br />
   * Note that this method is replacing 'feed_publishActionOfUser', which has been deprecated by Facebook.
   * For specific details, visit http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction
   * 
   * 
   * @param action a TemplatizedAction instance that represents the feed data to publish
   * 
   * @return a Document representing the XML response returned from the Facebook API server.
   * 
   * @throws FacebookException if any number of bad things happen
   * @throws IOException
   */
  public Document feed_PublishTemplatizedAction(TemplatizedAction action) throws FacebookException, IOException {
      return this.feed_publishTemplatizedAction(action.getTitleTemplate(), action.getTitleParams(), action.getBodyTemplate(), action.getBodyParams(), action.getBodyGeneral(), action.getPictures(), action.getTargetIds());
  }

  /**
   * Publish the notification of an action taken by a user to newsfeed.
   * @param title the title of the feed story
   * @param body the body of the feed story
   * @param images (optional) up to four pairs of image URLs and (possibly null) link URLs
   * @param priority
   * @return a document object containing the server response
   * 
   * @deprecated Facebook will be removing this API call (it is to be replaced with feed_publishTemplatizedAction)
   */
  public Document feed_publishActionOfUser(CharSequence title, CharSequence body,
                                           Collection<Pair<URL, URL>> images,
                                           Integer priority) throws FacebookException,
                                                                    IOException {
    return feedHandler(FacebookMethod.FEED_PUBLISH_ACTION_OF_USER, title, body, images, priority);
  }

  /**
   * @see FacebookRestClient#feed_publishActionOfUser(CharSequence,CharSequence,Collection,Integer)
   * 
   * @deprecated Facebook will be removing this API call (it is to be replaced with feed_publishTemplatizedAction)
   */
  public Document feed_publishActionOfUser(CharSequence title,
                                           CharSequence body) throws FacebookException,
                                                                     IOException {
    return feed_publishActionOfUser(title, body, null, null);
  }

  /**
   * @see FacebookRestClient#feed_publishActionOfUser(CharSequence,CharSequence,Collection,Integer)
   * 
   * @deprecated Facebook will be removing this API call (it is to be replaced with feed_publishTemplatizedAction)
   */
  public Document feed_publishActionOfUser(CharSequence title, CharSequence body,
                                           Integer priority) throws FacebookException,
                                                                    IOException {
    return feed_publishActionOfUser(title, body, null, priority);
  }

  /**
   * @see FacebookRestClient#feed_publishActionOfUser(CharSequence,CharSequence,Collection,Integer)
   * 
   * @deprecated Facebook will be removing this API call (it is to be replaced with feed_publishTemplatizedAction)
   */
  public Document feed_publishActionOfUser(CharSequence title, CharSequence body,
                                           Collection<Pair<URL, URL>> images) throws FacebookException,
                                                                                     IOException {
    return feed_publishActionOfUser(title, body, images, null);
  }


  /**
   * Publish a story to the logged-in user's newsfeed.
   * @param title the title of the feed story
   * @param body the body of the feed story
   * @param images (optional) up to four pairs of image URLs and (possibly null) link URLs
   * @param priority
   * @return a Document object containing the server response
   */
  public Document feed_publishStoryToUser(CharSequence title, CharSequence body,
                                          Collection<Pair<URL, URL>> images,
                                          Integer priority) throws FacebookException, IOException {
    return feedHandler(FacebookMethod.FEED_PUBLISH_STORY_TO_USER, title, body, images, priority);
  }

  /**
   * @see FacebookRestClient#feed_publishStoryToUser(CharSequence,CharSequence,Collection,Integer)
   */
  public Document feed_publishStoryToUser(CharSequence title,
                                          CharSequence body) throws FacebookException,
                                                                    IOException {
    return feed_publishStoryToUser(title, body, null, null);
  }

  /**
   * @see FacebookRestClient#feed_publishStoryToUser(CharSequence,CharSequence,Collection,Integer)
   */
  public Document feed_publishStoryToUser(CharSequence title, CharSequence body,
                                          Integer priority) throws FacebookException, IOException {
    return feed_publishStoryToUser(title, body, null, priority);
  }

  /**
   * @see FacebookRestClient#feed_publishStoryToUser(CharSequence,CharSequence,Collection,Integer)
   */
  public Document feed_publishStoryToUser(CharSequence title, CharSequence body,
                                          Collection<Pair<URL, URL>> images) throws FacebookException,
                                                                                    IOException {
    return feed_publishStoryToUser(title, body, images, null);
  }

  protected Document feedHandler(FacebookMethod feedMethod, CharSequence title, CharSequence body,
                                 Collection<Pair<URL, URL>> images,
                                 Integer priority) throws FacebookException, IOException {
    assert (images == null || images.size() <= 4);

    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(feedMethod.numParams());

    params.add(new Pair<String, CharSequence>("title", title));
    if (null != body)
    params.add(new Pair<String, CharSequence>("body", body));
    if (null != priority)
      params.add(new Pair<String, CharSequence>("priority", priority.toString()));
    if (null != images && !images.isEmpty()) {
      int image_count = 0;
      for (Pair<URL, URL> image: images) {
        ++image_count;
        assert (image.first != null);
        params.add(new Pair<String, CharSequence>(String.format("image_%d", image_count),
                                                  image.first.toString()));
        if (image.second != null)
          params.add(new Pair<String, CharSequence>(String.format("image_%d_link", image_count),
                                                    image.second.toString()));
      }
    }
    return this.callMethod(feedMethod, params);
  }
  
  protected Document templatizedFeedHandler(FacebookMethod method, String titleTemplate, String titleData, String bodyTemplate, 
          String bodyData, String bodyGeneral, Collection<Pair<URL, URL>> pictures, String targetIds) throws FacebookException, IOException {
      assert (pictures == null || pictures.size() <= 4);
      
      long actorId = this.users_getLoggedInUser();
      ArrayList<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>(method.numParams());
      
      //these are always required parameters
      params.add(new Pair<String, CharSequence>("actor_id", Long.toString(actorId)));
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
          for (Pair<URL, URL> picture : pictures) {
                params.add(new Pair<String, CharSequence>("image_" + count, picture.first.toString()));
                if (picture.second != null) {
                    params.add(new Pair<String, CharSequence>("image_" + count + "_link", picture.second.toString()));
                }
                count++;
          }
      }
      if (targetIds != null) {
          params.add(new Pair<String, CharSequence>("target_ids", targetIds));
      }
      return this.callMethod(method, params);
  }

  /**
   * Returns all visible events according to the filters specified. This may be used to find all events of a user, or to query specific eids.
   * @param eventIds filter by these event ID's (optional)
   * @param userId filter by this user only (optional)
   * @param startTime UTC lower bound (optional)
   * @param endTime UTC upper bound (optional)
   * @return Document of events
   */
  public Document events_get(Long userId, Collection<Long> eventIds, Long startTime,
                             Long endTime) throws FacebookException, IOException {
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
   * Retrieves the membership list of an event
   * @param eventId event id
   * @return Document consisting of four membership lists corresponding to RSVP status, with keys
   *  'attending', 'unsure', 'declined', and 'not_replied'
   */
  public Document events_getMembers(Number eventId) throws FacebookException, IOException {
    assert (null != eventId);
    return this.callMethod(FacebookMethod.EVENTS_GET_MEMBERS,
                           new Pair<String, CharSequence>("eid", eventId.toString()));
  }


  /**
   * Retrieves the friends of the currently logged in user.
   * @return array of friends
   */
  public Document friends_areFriends(long userId1, long userId2) throws FacebookException,
                                                                      IOException {
    return this.callMethod(FacebookMethod.FRIENDS_ARE_FRIENDS,
                           new Pair<String, CharSequence>("uids1", Long.toString(userId1)),
                           new Pair<String, CharSequence>("uids2", Long.toString(userId2)));
  }

  public Document friends_areFriends(Collection<Long> userIds1,
                                     Collection<Long> userIds2) throws FacebookException,
                                                                          IOException {
    assert (userIds1 != null && userIds2 != null);
    assert (!userIds1.isEmpty() && !userIds2.isEmpty());
    assert (userIds1.size() == userIds2.size());

    return this.callMethod(FacebookMethod.FRIENDS_ARE_FRIENDS,
                           new Pair<String, CharSequence>("uids1", delimit(userIds1)),
                           new Pair<String, CharSequence>("uids2", delimit(userIds2)));
  }

  /**
   * Retrieves the friends of the currently logged in user.
   * @return array of friends
   */
  public Document friends_get() throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.FRIENDS_GET);
  }

  /**
   * Retrieves the friends of the currently logged in user, who are also users
   * of the calling application.
   * @return array of friends
   */
  public Document friends_getAppUsers() throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.FRIENDS_GET_APP_USERS);
  }

  /**
   * Retrieves the requested info fields for the requested set of users.
   * @param userIds a collection of user IDs for which to fetch info
   * @param fields a set of ProfileFields
   * @return a Document consisting of a list of users, with each user element
   *   containing the requested fields.
   */
  public Document users_getInfo(Collection<Long> userIds,
                                EnumSet<ProfileField> fields) throws FacebookException,
                                                                     IOException {
    // assertions test for invalid params
    assert (userIds != null);
    assert (fields != null);
    assert (!fields.isEmpty());

    return this.callMethod(FacebookMethod.USERS_GET_INFO,
                           new Pair<String, CharSequence>("uids", delimit(userIds)),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }

  /**
   * Retrieves the requested info fields for the requested set of users.
   * @param userIds a collection of user IDs for which to fetch info
   * @param fields a set of strings describing the info fields desired, such as "last_name", "sex"
   * @return a Document consisting of a list of users, with each user element
   *   containing the requested fields.
   */
  public Document users_getInfo(Collection<Long> userIds,
                                Set<CharSequence> fields) throws FacebookException, IOException {
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
    Document d = this.callMethod(FacebookMethod.USERS_GET_LOGGED_IN_USER);
    return Long.parseLong(d.getFirstChild().getTextContent());
  }

  /**
   * Retrieves an indicator of whether the logged-in user has installed the
   * application associated with the _apiKey.
   * @return boolean indicating whether the user has installed the app
   */
  public boolean users_isAppAdded() throws FacebookException, IOException {
    return extractBoolean(this.callMethod(FacebookMethod.USERS_IS_APP_ADDED));
  }

  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param subjId retrieve from photos associated with this user (optional).
   * @param albumId retrieve from photos from this album (optional)
   * @param photoIds retrieve from this list of photos (optional)
   *
   * @return an Document of photo objects.
   */
  public Document photos_get(Long subjId, Long albumId,
                             Collection<Long> photoIds) throws FacebookException, IOException {
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.PHOTOS_GET.numParams());

    boolean hasUserId = null != subjId && 0 != subjId;
    boolean hasAlbumId = null != albumId && 0 != albumId;
    boolean hasPhotoIds = null != photoIds && !photoIds.isEmpty();
    assert (hasUserId || hasAlbumId || hasPhotoIds);

    if (hasUserId)
      params.add(new Pair<String, CharSequence>("subj_id", Long.toString(subjId)));
    if (hasAlbumId)
      params.add(new Pair<String, CharSequence>("aid", Long.toString(albumId)));
    if (hasPhotoIds)
      params.add(new Pair<String, CharSequence>("pids", delimit(photoIds)));

    return this.callMethod(FacebookMethod.PHOTOS_GET, params);
  }

  public Document photos_get(Long albumId, Collection<Long> photoIds, boolean album) throws FacebookException,
                                                                             IOException {
    return photos_get(null/*subjId*/, albumId, photoIds);
  }

  public Document photos_get(Long subjId, Collection<Long> photoIds) throws FacebookException,
                                                                               IOException {
    return photos_get(subjId, null/*albumId*/, photoIds);
  }

  public Document photos_get(Long subjId, Long albumId) throws FacebookException, IOException {
    return photos_get(subjId, albumId, null/*photoIds*/);
  }

  public Document photos_get(Collection<Long> photoIds) throws FacebookException, IOException {
    return photos_get(null/*subjId*/, null/*albumId*/, photoIds);
  }

  public Document photos_get(Long albumId, boolean album) throws FacebookException, IOException {
    return photos_get(null/*subjId*/, albumId, null/*photoIds*/);
  }

  public Document photos_get(Long subjId) throws FacebookException, IOException {
    return photos_get(subjId, null/*albumId*/, null/*photoIds*/);
  }

  /**
   * Retrieves album metadata. Pass a user id and/or a list of album ids to specify the albums
   * to be retrieved (at least one must be provided)
   *
   * @param userId retrieve metadata for albums created the id of the user whose album you wish  (optional).
   * @param albumIds the ids of albums whose metadata is to be retrieved
   * @return album objects.
   */
  public Document photos_getAlbums(Long userId,
                                   Collection<Long> albumIds) throws FacebookException,
                                                                     IOException {
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

  public Document photos_getAlbums(Long userId) throws FacebookException, IOException {
    return photos_getAlbums(userId, null /*albumIds*/);
  }

  public Document photos_getAlbums(Collection<Long> albumIds) throws FacebookException,
                                                                     IOException {
    return photos_getAlbums(null /*userId*/, albumIds);
  }

  /**
   * Retrieves the tags for the given set of photos.
   * @param photoIds The list of photos from which to extract photo tags.
   * @return the created album
   */
  public Document photos_getTags(Collection<Long> photoIds) throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.PHOTOS_GET_TAGS,
                           new Pair<String, CharSequence>("pids", delimit(photoIds)));
  }

  /**
   * Creates an album.
   * @param albumName The list of photos from which to extract photo tags.
   * @return the created album
   */
  public Document photos_createAlbum(String albumName) throws FacebookException, IOException {
    return this.photos_createAlbum(albumName, null/*description*/, null/*location*/);
  }

  /**
   * Creates an album.
   * @param name The album name.
   * @param location The album location (optional).
   * @param description The album description (optional).
   * @return an array of photo objects.
   */
  public Document photos_createAlbum(String name, String description,
                                     String location) throws FacebookException, IOException {
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

  /**
   * Adds several tags to a photo.
   * @param photoId The photo id of the photo to be tagged.
   * @param tags A list of PhotoTags.
   * @return a list of booleans indicating whether the tag was successfully added.
   */
  public Document photos_addTags(Long photoId, Collection<PhotoTag> tags)
    throws FacebookException, IOException, JSONException {
    assert (photoId > 0);
    assert (null != tags && !tags.isEmpty());
    JSONWriter tagsJSON = new JSONStringer().array();
    for (PhotoTag tag: tags)
      tagsJSON = tag.jsonify(tagsJSON);
    String tagStr = tagsJSON.endArray().toString();

    return this.callMethod(FacebookMethod.PHOTOS_ADD_TAG,
                           new Pair<String, CharSequence>("pid", photoId.toString()),
                           new Pair<String, CharSequence>("tags", tagStr));
  }

  /**
   * Adds a tag to a photo.
   * @param photoId The photo id of the photo to be tagged.
   * @param xPct The horizontal position of the tag, as a percentage from 0 to 100, from the left of the photo.
   * @param yPct The vertical position of the tag, as a percentage from 0 to 100, from the top of the photo.
   * @param taggedUserId The list of photos from which to extract photo tags.
   * @return whether the tag was successfully added.
   */
  public boolean photos_addTag(Long photoId, Long taggedUserId, Double xPct,
                               Double yPct) throws FacebookException, IOException {
    return photos_addTag(photoId, xPct, yPct, taggedUserId, null);
  }

  /**
   * Adds a tag to a photo.
   * @param photoId The photo id of the photo to be tagged.
   * @param xPct The horizontal position of the tag, as a percentage from 0 to 100, from the left of the photo.
   * @param yPct The list of photos from which to extract photo tags.
   * @param tagText The text of the tag.
   * @return whether the tag was successfully added.
   */
  public boolean photos_addTag(Long photoId, CharSequence tagText, Double xPct,
                               Double yPct) throws FacebookException, IOException {
    return photos_addTag(photoId, xPct, yPct, null, tagText);
  }

  private boolean photos_addTag(Long photoId, Double xPct, Double yPct, Long taggedUserId,
                                CharSequence tagText) throws FacebookException, IOException {
    assert (null != photoId && !photoId.equals(0));
    assert (null != taggedUserId || null != tagText);
    assert (null != xPct && xPct >= 0 && xPct <= 100);
    assert (null != yPct && yPct >= 0 && yPct <= 100);
    Document d =
      this.callMethod(FacebookMethod.PHOTOS_ADD_TAG, new Pair<String, CharSequence>("pid",
                                                                                    photoId.toString()),
                      new Pair<String, CharSequence>("tag_uid", taggedUserId.toString()),
                      new Pair<String, CharSequence>("x", xPct.toString()),
                      new Pair<String, CharSequence>("y", yPct.toString()));
    return extractBoolean(d);
  }

  public Document photos_upload(File photo) throws FacebookException, IOException {
    return /* caption */ /* albumId */photos_upload(photo, null, null);
  }

  public Document photos_upload(File photo, String caption) throws FacebookException, IOException {
    return /* albumId */photos_upload(photo, caption, null);
  }

  public Document photos_upload(File photo, Long albumId) throws FacebookException, IOException {
    return /* caption */photos_upload(photo, null, albumId);
  }

  public Document photos_upload(File photo, String caption, Long albumId) throws FacebookException,
                                                                                 IOException {
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
   * Retrieves the groups associated with a user
   * @param userId Optional: User associated with groups.
   *  A null parameter will default to the session user.
   * @param groupIds Optional: group ids to query.
   *   A null parameter will get all groups for the user.
   * @return array of groups
   */
  public Document groups_get(Long userId, Collection<Long> groupIds) throws FacebookException,
                                                                               IOException {
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
   * Retrieves the membership list of a group
   * @param groupId the group id
   * @return a Document containing four membership lists of
   *  'members', 'admins', 'officers', and 'not_replied'
   */
  public Document groups_getMembers(Number groupId) throws FacebookException, IOException {
    assert (null != groupId);
    return this.callMethod(FacebookMethod.GROUPS_GET_MEMBERS,
                           new Pair<String, CharSequence>("gid", groupId.toString()));
  }

  /**
   * Retrieves the results of a Facebook Query Language query
   * @param query : the FQL query statement
   * @return varies depending on the FQL query
   */
  public Document fql_query(CharSequence query) throws FacebookException, IOException {
    assert (null != query);
    return this.callMethod(FacebookMethod.FQL_QUERY,
                           new Pair<String, CharSequence>("query", query));
  }

  /**
   * Retrieves the outstanding notifications for the session user.
   * @return a Document containing
   *  notification count pairs for 'messages', 'pokes' and 'shares',
   *  a uid list of 'friend_requests', a gid list of 'group_invites',
   *  and an eid list of 'event_invites'
   */
  public Document notifications_get() throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.NOTIFICATIONS_GET);
  }

  /**
   * Send a request or invitations to the specified users.
   * @param recipientIds the user ids to which the request is to be sent
   * @param type the type of request/invitation - e.g. the word "event" in "1 event invitation."
   * @param content Content of the request/invitation. This should be FBML containing only links and the
   *   special tag &lt;fb:req-choice url="" label="" /&gt; to specify the buttons to be included in the request.
   * @param image URL of an image to show beside the request. It will be resized to be 100 pixels wide.
   * @param isInvite whether this is a "request" or an "invite"
   * @return a URL, possibly null, to which the user should be redirected to finalize
   *    the sending of the message
   *    
   * @deprecated this method has been removed from the Facebook API server
   */
  public URL notifications_sendRequest(Collection<Long> recipientIds, CharSequence type,
  CharSequence content, URL image, boolean isInvite) throws FacebookException, IOException {
    assert (null != recipientIds && !recipientIds.isEmpty());
    assert (null != type);
    assert (null != content);
    assert (null != image);

    Document d =
      this.callMethod(FacebookMethod.NOTIFICATIONS_SEND_REQUEST,
                      new Pair<String, CharSequence>("to_ids", delimit(recipientIds)),
                      new Pair<String, CharSequence>("type", type),
                      new Pair<String, CharSequence>("content", content),
                      new Pair<String, CharSequence>("image", image.toString()),
                      new Pair<String, CharSequence>("invite", isInvite ? "1" : "0"));
    String url = d.getFirstChild().getTextContent();
    return (null == url || "".equals(url)) ? null : new URL(url);
  }

  /**
   * Send a notification message to the specified users.
   * @param recipientIds the user ids to which the message is to be sent
   * @param notification the notification to send, this is delivered to the targets' Facebook account(s)
   * @param email the email to send, this is delivered to the targets' external e-mail account(s)
   * @return a URL, possibly null, to which the user should be redirected to finalize
   *    the sending of the message
   */
  public URL notifications_send(Collection<Long> recipientIds,
                                CharSequence notification,
                                CharSequence email) throws FacebookException, IOException {
    assert (null != recipientIds && !recipientIds.isEmpty());
    assert (null != notification);
    Document d;
    
    if (email != null) {
        d = this.callMethod(FacebookMethod.NOTIFICATIONS_SEND,
                      new Pair<String, CharSequence>("to_ids", delimit(recipientIds)),
                      new Pair<String, CharSequence>("notification", notification),
                      new Pair<String, CharSequence>("email", email));
    }
    else {
        d = this.callMethod(FacebookMethod.NOTIFICATIONS_SEND,
                new Pair<String, CharSequence>("to_ids", delimit(recipientIds)),
                new Pair<String, CharSequence>("notification", notification));
    }
    String url = d.getFirstChild().getTextContent();
    return (null == url || "".equals(url)) ? null : new URL(url);
  }

  protected static boolean extractBoolean(Document doc) {
    String content = doc.getFirstChild().getTextContent();
    return 1 == Integer.parseInt(content);
  }

  protected static final String CRLF = "\r\n";
  protected static final String PREF = "--";
  protected static final int UPLOAD_BUFFER_SIZE = 512;

  public InputStream postFileRequest(String methodName,
                                     Map<String, CharSequence> params) {
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

      for (Map.Entry<String, CharSequence> entry: params.entrySet()) {
        out.writeBytes(PREF + boundary + CRLF);
        out.writeBytes("Content-disposition: form-data; name=\"" + entry.getKey() + "\"");
        out.writeBytes(CRLF + CRLF);
        out.writeBytes(entry.getValue().toString());
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
    }
    catch (Exception e) {
        System.err.println("caught exception: " + e);
        e.printStackTrace();
        return null;
    }
  }

  /**
   * Call this function and store the result, using it to generate the
   * appropriate login url and then to retrieve the session information.
   * @return String the auth_token string
   */
  public String auth_createToken() throws FacebookException, IOException {
    Document d = this.callMethod(FacebookMethod.AUTH_CREATE_TOKEN);
    return d.getFirstChild().getTextContent();
  }

  /**
   * Call this function to retrieve the session information after your user has
   * logged in.
   * @param authToken the token returned by auth_createToken or passed back to your callback_url.
   */
  public String auth_getSession(String authToken) throws FacebookException, IOException {
    Document d =
      this.callMethod(FacebookMethod.AUTH_GET_SESSION, new Pair<String, CharSequence>("auth_token",
                                                                                      authToken.toString()));
    this._sessionKey =
        d.getElementsByTagName("session_key").item(0).getFirstChild().getTextContent();
    this._userId =
        Long.parseLong(d.getElementsByTagName("uid").item(0).getFirstChild().getTextContent());
    if (this._isDesktop)
      this._sessionSecret =
          d.getElementsByTagName("secret").item(0).getFirstChild().getTextContent();
    return this._sessionKey;
  }
  
  /**
   * Returns a JAXB object of the type that corresponds to the last API call made on the client.  Each 
   * Facebook Platform API call that returns a Document object has a JAXB response object associated 
   * with it.  The naming convention is generally intuitive.  For example, if you invoke the 
   * 'user_getInfo' API call, the associated JAXB response object is 'UsersGetInfoResponse'.<br />
   * <br />
   * An example of how to use this method:<br />
   *  <br />
   *    FacebookRestClient client = new FacebookRestClient("apiKey", "secretKey", "sessionId");<br />
   *    client.friends_get();<br />
   *    FriendsGetResponse response = (FriendsGetResponse)client.getResponsePOJO();<br />
   *    List<Long> friends = response.getUid(); <br />
   * <br />
   * This is particularly useful in the case of API calls that return a Document object, as working 
   * with the JAXB response object is generally much simple than trying to walk/parse the DOM by 
   * hand.<br />
   * <br />
   * This method can be safely called multiple times, though note that it will only return the 
   * response-object corresponding to the most recent Facebook Platform API call made.<br />
   * <br />
   * Note that you must cast the return value of this method to the correct type in order to do anything 
   * useful with it.
   * 
   * @return a JAXB POJO ("Plain Old Java Object") of the type that corresponds to the last API call made on 
   *         the client.  Note that you must cast this object to its proper type before you will be able to 
   *         do anything useful with it.
   */
  public Object getResponsePOJO(){
      if (this.rawResponse == null) {
          return null;
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
  
  /**
   * Lookup a single preference value for the current user.
   * 
   * @param prefId the id of the preference to lookup.  This should be an integer value from 0-200.
   * 
   * @return The value of that preference, or null if it is not yet set.
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public String data_getUserPreference(Integer prefId) throws FacebookException, IOException {
      if ((prefId < 0) || (prefId > 200)) {
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The preference id must be an integer value from 0-200.");
      }
      this.callMethod(FacebookMethod.DATA_GET_USER_PREFERENCE, new Pair<String, CharSequence>("pref_id", Integer.toString(prefId)));
      this.checkError();
      
      if (! this.rawResponse.contains("</data_getUserPreference_response>")) {
          //there is no value set for this preference yet
          return null;
      }
      String result = this.rawResponse.substring(0, this.rawResponse.indexOf("</data_getUserPreference_response>"));
      result = result.substring(result.indexOf("facebook.xsd\">") + "facebook.xsd\">".length());
      
      return reconstructValue(result);
  }
  
  /**
   * Get a map containing all preference values set for the current user.
   * 
   * @return a map of preference values, keyed by preference id.  The map will contain all 
   *         preferences that have been set for the current user.  If there are no preferences
   *         currently set, the map will be empty.  The map returned will never be null.
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public Map<Integer, String> data_getUserPreferences() throws FacebookException, IOException {
      Document response = this.callMethod(FacebookMethod.DATA_GET_USER_PREFERENCES);
      this.checkError();
      
      Map<Integer, String> results = new HashMap<Integer, String>();
      NodeList ids = response.getElementsByTagName("pref_id");
      NodeList values = response.getElementsByTagName("value");
      for (int count = 0; count < ids.getLength(); count++) {
          results.put(Integer.parseInt(ids.item(count).getFirstChild().getTextContent()), 
                  reconstructValue(values.item(count).getFirstChild().getTextContent()));
      }
      
      return results;
  }
  
  private void checkError() throws FacebookException {
      if (this.rawResponse.contains("error_response")) {
          //<error_code>xxx</error_code>
          Integer code = Integer.parseInt(this.rawResponse.substring(this.rawResponse.indexOf("<error_code>") + "<error_code>".length(), 
                  this.rawResponse.indexOf("</error_code>") + "</error_code>".length()));
          throw new FacebookException(code, "The request could not be completed!");
      }
  }
  
  private String reconstructValue(String input) {
      if ((input == null) || ("".equals(input))) {
          return null;
      }
      if (input.charAt(0) == '_') {
          return input.substring(1);
      }
      return input;
  }
  
  /**
   * Set a user-preference value.  The value can be any string up to 127 characters in length, 
   * while the preference id can only be an integer between 0 and 200.  Any preference set applies 
   * only to the current user of the application.
   * 
   * To clear a user-preference, specify null as the value parameter.  The values of "0" and "" will 
   * be stored as user-preferences with a literal value of "0" and "" respectively.
   * 
   * @param prefId the id of the preference to set, an integer between 0 and 200.
   * @param value the value to store, a String of up to 127 characters in length.
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public void data_setUserPreference(Integer prefId, String value) throws FacebookException, IOException {
      if ((prefId < 0) || (prefId > 200)) {
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The preference id must be an integer value from 0-200.");
      }
      if ((value != null) && (value.length() > 127)) {
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The preference value cannot be longer than 128 characters.");
      }
      
      value = normalizePreferenceValue(value);
      
      Collection<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
      params.add(new Pair<String, CharSequence>("pref_id", Integer.toString(prefId)));
      params.add(new Pair<String, CharSequence>("value", value));
      this.callMethod(FacebookMethod.DATA_SET_USER_PREFERENCE, params);
      this.checkError();
  }
  
  /**
   * Set multiple user-preferences values.  The values can be strings up to 127 characters in length, 
   * while the preference id can only be an integer between 0 and 200.  Any preferences set apply 
   * only to the current user of the application.
   * 
   * To clear a user-preference, specify null as its value in the map.  The values of "0" and "" will 
   * be stored as user-preferences with a literal value of "0" and "" respectively.
   * 
   * @param value the values to store, specified in a map. The keys should be preference-id values from 0-200, and 
   *              the values should be strings of up to 127 characters in length.
   * @param replace set to true if you want to remove any pre-existing preferences before writing the new ones
   *                set to false if you want the new preferences to be merged with any pre-existing preferences
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public void data_setUserPreferences(Map<Integer, String> values, boolean replace) throws FacebookException, IOException {
      JSONObject map = new JSONObject();
      
      for (Integer key : values.keySet()) {
          if ((key < 0) || (key > 200)) {
              throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The preference id must be an integer value from 0-200.");
          }
          if ((values.get(key) != null) && (values.get(key).length() > 127)) {
              throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The preference value cannot be longer than 128 characters.");
          }
          try {
              map.put(Integer.toString(key), normalizePreferenceValue(values.get(key)));
          }
          catch (JSONException e) {
              FacebookException ex = new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "Error when translating {key=" 
                      + key + ", value=" + values.get(key) + "}to JSON!");
              ex.setStackTrace(e.getStackTrace());
              throw ex;
          }
      }
      
      Collection<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
      params.add(new Pair<String, CharSequence>("values", map.toString()));
      if (replace) {
          params.add(new Pair<String, CharSequence>("replace", "true"));
      }
      
      this.callMethod(FacebookMethod.DATA_SET_USER_PREFERENCES, params);
      this.checkError();
  }
  
  private String normalizePreferenceValue(String input) {
      if (input == null) {
          return "0";
      }
      return "_" + input;
  }
  
  /**
   * Check to see if the application is permitted to send SMS messages to the current application user.
   * 
   * @return true if the application is presently able to send SMS messages to the current user
   *         false otherwise
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public boolean sms_canSend() throws FacebookException, IOException {
      return sms_canSend(this.users_getLoggedInUser());
  }
  
  /**
   * Check to see if the application is permitted to send SMS messages to the specified user.
   * 
   * @param userId the UID of the user to check permissions for
   * 
   * @return true if the application is presently able to send SMS messages to the specified user
   *         false otherwise
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public boolean sms_canSend(Long userId) throws FacebookException, IOException {
      this.callMethod(FacebookMethod.SMS_CAN_SEND, new Pair<String, CharSequence>("uid", userId.toString()));
      return this.rawResponse.contains(">0<");  //a status code of "0" indicates that the app can send messages
  }
  
  /**
   * Send an SMS message to the current application user.
   * 
   * @param message the message to send.
   * @param smsSessionId the SMS session id to use, note that that is distinct from the user's facebook session id.  It is used to 
   *                     allow applications to keep track of individual SMS conversations/threads for a single user.  Specify 
   *                     null if you do not want/need to use a session for the current message.
   * @param makeNewSession set to true to request that Facebook allocate a new SMS session id for this message.  The allocated 
   *                       id will be returned as the result of this API call.  You should only set this to true if you are 
   *                       passing a null 'smsSessionId' value.  Otherwise you already have a SMS session id, and do not need a new one.
   * 
   * @return an integer specifying the value of the session id alocated by Facebook, if one was requested.  If a new session id was 
   *                    not requested, this method will return null.
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public Integer sms_send(String message, Integer smsSessionId, boolean makeNewSession) throws FacebookException, IOException {
      return sms_send(this.users_getLoggedInUser(), message, smsSessionId, makeNewSession);
  }
  
  /**
   * Send an SMS message to the specified user.
   * 
   * @param userId the id of the user to send the message to.
   * @param message the message to send.
   * @param smsSessionId the SMS session id to use, note that that is distinct from the user's facebook session id.  It is used to 
   *                     allow applications to keep track of individual SMS conversations/threads for a single user.  Specify 
   *                     null if you do not want/need to use a session for the current message.
   * @param makeNewSession set to true to request that Facebook allocate a new SMS session id for this message.  The allocated 
   *                       id will be returned as the result of this API call.  You should only set this to true if you are 
   *                       passing a null 'smsSessionId' value.  Otherwise you already have a SMS session id, and do not need a new one.
   * 
   * @return an integer specifying the value of the session id alocated by Facebook, if one was requested.  If a new session id was 
   *                    not requested, this method will return null.
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public Integer sms_send(Long userId, String message, Integer smsSessionId, boolean makeNewSession) throws FacebookException, IOException {
      Collection<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
      params.add(new Pair<String, CharSequence>("uid", userId.toString()));
      params.add(new Pair<String, CharSequence>("message", message));
      if (smsSessionId != null) {
          params.add(new Pair<String, CharSequence>("session_id", smsSessionId.toString()));
      }
      if (makeNewSession) {
          params.add(new Pair<String, CharSequence>("req_session", "true"));
      }
      
      this.callMethod(FacebookMethod.SMS_SEND, params);
      
      //XXX:  needs testing to make sure it's correct (Facebook always gives me a code 270 permissions error no matter what I do)
      Integer response = null;
      if ((this.rawResponse.indexOf("</sms") != -1) && (makeNewSession)) {
          String result = this.rawResponse.substring(0, this.rawResponse.indexOf("</sms"));
          result = result.substring(result.lastIndexOf(">") + 1);
          response = Integer.parseInt(result);
      }
      
      return response;
  }
  
  /**
   * Check to see if the user has granted the app a specific external permission.  In order to be granted a 
   * permission, an application must direct the user to a URL of the form:
   * 
   * http://www.facebook.com/authorize.php?api_key=[YOUR_API_KEY]&v=1.0&ext_perm=[PERMISSION NAME]
   * 
   * @param perm the permission to check for
   * 
   * @return true if the user has granted the application the specified permission
   *         false otherwise
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public boolean users_hasAppPermission(Permission perm) throws FacebookException, IOException {
      this.callMethod(FacebookMethod.USERS_HAS_PERMISSION, new Pair<String, CharSequence>("ext_perm", perm.getName()));
      return this.rawResponse.contains(">1<");  //a code of '1' is sent back to indicate that the user has the request permission
  }
  
  /**
   * Set the user's profile status message.  This requires that the user has granted the application the 
   * 'status_update' permission, otherwise the call will return an error.  You can use 'users_hasAppPermission' 
   * to check to see if the user has granted your app the abbility to update their status.
   *
   * @param newStatus the new status message to set.
   * @param clear whether or not to clear the old status message.
   * 
   * @return true if the call succeeds
   *         false otherwise 
   *          
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public boolean users_setStatus(String newStatus, boolean clear) throws FacebookException, IOException {
      Collection<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
      
      if (newStatus != null) {
          params.add(new Pair<String, CharSequence>("status", newStatus));
      }
      if (clear) {
          params.add(new Pair<String, CharSequence>("clear", "true"));
      }
      
      this.callMethod(FacebookMethod.USERS_SET_STATUS, params);
      
      return this.rawResponse.contains(">1<"); //a code of '1' is sent back to indicate that the request was successful, any other response indicates error   
  }
  
  /**
   * Associates the specified FBML markup with the specified handle/id.  The markup can then be referenced using the fb:ref FBML 
   * tag, to allow a given snippet to be reused easily across multiple users, and also to allow the application to update 
   * the fbml for multiple users more easily without having to make a seperate call for each user, by just changing the FBML 
   * markup that is associated with the handle/id. 
   * 
   * @param handle the id to associate the specified markup with.  Put this in fb:ref FBML tags to reference your markup.
   * @param markup the FBML markup to store.
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public void fbml_setRefHandle(String handle, String markup) throws FacebookException, IOException {
      if ((handle == null) || ("".equals(handle))) {
          throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The FBML handle may not be null or empty!");
      }
      if (markup == null) {
          markup = "";
      }
      Collection<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
      params.add(new Pair<String, CharSequence>("handle", handle));
      params.add(new Pair<String, CharSequence>("fbml", markup));
      
      this.callMethod(FacebookMethod.FBML_SET_REF_HANDLE, params);
  }
  
  /**
   * Create a new marketplace listing, or modify an existing one.
   * 
   * @param listingId the id of the listing to modify, set to 0 (or null) to create a new listing.
   * @param showOnProfile set to true to show the listing on the user's profile (Facebook appears to ignore this setting).
   * @param attributes JSON-encoded attributes for this listing.
   * 
   * @return the id of the listing created (or modified).
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public Long marketplace_createListing(Long listingId, boolean showOnProfile, String attributes) throws FacebookException, IOException {
     if (listingId == null) {
         listingId = 0l;
     }
     MarketListing test = new MarketListing(attributes);
     if (!test.verify()) {
         throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The specified listing is invalid!");
     }
     
     Collection<Pair<String, CharSequence>> params = new ArrayList<Pair<String, CharSequence>>();
     params.add(new Pair<String, CharSequence>("listing_id", listingId.toString()));
     if (showOnProfile) {
         params.add(new Pair<String, CharSequence>("show_on_profile", "true"));
     }
     params.add(new Pair<String, CharSequence>("listing_attrs", attributes));
     
     this.callMethod(FacebookMethod.MARKET_CREATE_LISTING, params);
     String result = this.rawResponse.substring(0, this.rawResponse.indexOf("</marketplace"));
     result = result.substring(result.lastIndexOf(">") + 1);
     return Long.parseLong(result);
  }
  
  /**
   * Create a new marketplace listing, or modify an existing one.
   * 
   * @param listingId the id of the listing to modify, set to 0 (or null) to create a new listing.
   * @param showOnProfile set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
   * @param listing the listing to publish.
   * 
   * @return the id of the listing created (or modified).
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public Long marketplace_createListing(Long listingId, boolean showOnProfile, MarketListing listing) throws FacebookException, IOException {
      return this.marketplace_createListing(listingId, showOnProfile, listing.getAttribs());
  }
  
  /**
   * Create a new marketplace listing.
   * 
   * @param showOnProfile set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
   * @param listing the listing to publish.
   * 
   * @return the id of the listing created (or modified).
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public Long marketplace_createListing(boolean showOnProfile, MarketListing listing) throws FacebookException, IOException {
      return this.marketplace_createListing(0l, showOnProfile, listing.getAttribs());
  }
  
  /**
   * Create a new marketplace listing, or modify an existing one.
   * 
   * @param listingId the id of the listing to modify, set to 0 (or null) to create a new listing.
   * @param showOnProfile set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
   * @param listing the listing to publish.
   * 
   * @return the id of the listing created (or modified).
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public Long marketplace_createListing(Long listingId, boolean showOnProfile, JSONObject listing) throws FacebookException, IOException {
      return this.marketplace_createListing(listingId, showOnProfile, listing.toString());
  }
  
  /**
   * Create a new marketplace listing.
   * 
   * @param showOnProfile set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
   * @param listing the listing to publish.
   * 
   * @return the id of the listing created (or modified).
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public Long marketplace_createListing(boolean showOnProfile, JSONObject listing) throws FacebookException, IOException {
      return this.marketplace_createListing(0l, showOnProfile, listing.toString());
  }
  
  /**
   * Return a list of all valid Marketplace categories.
   * 
   * @return a list of marketplace categories allowed by Facebook.
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public List<String> marketplace_getCategories() throws FacebookException, IOException{
      this.callMethod(FacebookMethod.MARKET_GET_CATEGORIES);
      MarketplaceGetCategoriesResponse resp = (MarketplaceGetCategoriesResponse)this.getResponsePOJO();
      return resp.getMarketplaceCategory();
  }
  
  /**
   * Return a list of all valid Marketplace subcategories.
   * 
   * @return a list of marketplace subcategories allowed by Facebook.
   * 
   * @throws FacebookException if an error happens when executing the API call.
   * @throws IOException if a communication/network error happens.
   */
  public List<String> marketplace_getSubCategories() throws FacebookException, IOException{
      this.callMethod(FacebookMethod.MARKET_GET_SUBCATEGORIES);
      MarketplaceGetSubCategoriesResponse resp = (MarketplaceGetSubCategoriesResponse)this.getResponsePOJO();
      return resp.getMarketplaceSubcategory();
  }
}
