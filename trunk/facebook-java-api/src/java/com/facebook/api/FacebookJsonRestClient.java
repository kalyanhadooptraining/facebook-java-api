package com.facebook.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

 /**
  * A FacebookRestClient that uses the JSON result format. This means 
  * results from calls to the Facebook API are returned as 
  * <a href="http://www.json.org/">JSON</a> and 
  * transformed into Java <code>Object</code>'s.
  */
public class FacebookJsonRestClient extends ExtensibleClient<Object> {
  /**
   * Constructor.
   * 
   * @param apiKey your Facebook API key
   * @param secret your 'secret' Facebook key
   */
  public FacebookJsonRestClient(String apiKey, String secret) {
    this(SERVER_URL, apiKey, secret, null);
  }

  /**
   * Constructor.
   * 
   * @param apiKey your Facebook API key
   * @param secret your 'secret' Facebook key
   * @param sessionKey the session-id to use
   */
  public FacebookJsonRestClient(String apiKey, String secret, String sessionKey) {
    this(SERVER_URL, apiKey, secret, sessionKey);
  }

  
  /**
   * Constructor.
   * 
   * @param serverAddr the URL of the Facebook API server to use 
   * @param apiKey your Facebook API key
   * @param secret your 'secret' Facebook key
   * @param sessionKey the session-id to use
   * 
   * @throws MalformedURLException if you specify an invalid URL 
   */
  public FacebookJsonRestClient(String serverAddr, String apiKey, String secret,
                            String sessionKey) throws MalformedURLException {
    this(new URL(serverAddr), apiKey, secret, sessionKey);
  }

  
  /**
   * Constructor.
   * 
   * @param serverUrl the URL of the Facebook API server to use 
   * @param apiKey your Facebook API key
   * @param secret your 'secret' Facebook key
   * @param sessionKey the session-id to use
   */
  public FacebookJsonRestClient(URL serverUrl, String apiKey, String secret,
                            String sessionKey) {
    super(serverUrl, apiKey, secret, sessionKey);
  }

  /**
   * The response format in which results to FacebookMethod calls are returned
   * @return the format: either XML, JSON, or null (API default)
   */
  public String getResponseFormat() {
    return "json";
  }

  /**
   * Extracts a String from a result consisting entirely of a String.
   * @param val
   * @return the String
   */
  public String extractString(Object val) {
    try {
      return (String) val;
    } catch (ClassCastException cce) {
      logException(cce);
      return null;
    }
  }

  /**
   * Sets the session information (sessionKey) using the token from auth_createToken. 
   *
   * @param authToken the token returned by auth_createToken or passed back to your callback_url.
   * @return the session key
   * @throws FacebookException
   * @throws IOException
   */
  public String auth_getSession(String authToken) throws FacebookException,
                                                         IOException {
    if (null != this._sessionKey) {
      return this._sessionKey;
    }
    JSONObject d = (JSONObject)
      this.callMethod(FacebookMethod.AUTH_GET_SESSION, 
                      new Pair<String, CharSequence>("auth_token", authToken.toString()));
    try {
        this._sessionKey = (String) d.get("session_key");
        Object uid = d.get("uid");
        try {
          this._userId = (Integer) uid;
        } catch (ClassCastException cce) {
          this._userId = Integer.parseInt((String) uid);
        } 
        if (this.isDesktop()) {
          this._sessionSecret = (String) d.get("secret");
        }
    }
    catch (Exception ignored) {}
    return this._sessionKey;
  }

  /**
   * Parses the result of an API call from JSON into Java Objects.
   * @param data an InputStream with the results of a request to the Facebook servers
   * @param method the method
   * @return a Java Object
   * @throws FacebookException if <code>data</code> represents an error
   * @throws IOException if <code>data</code> is not readable
   * @see JSONObject
   */
  protected Object parseCallResult(InputStream data, IFacebookMethod method) throws FacebookException, IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(data, "UTF-8"));
    StringBuffer buffer = new StringBuffer();
    String line;
    while ((line = in.readLine()) != null) {
      buffer.append(line);
    }

    String jsonResp = new String(buffer);
    
    Object json = null;
    try {
        json = new JSONObject(jsonResp);
    }
    catch (Exception ignored) {}
    if (isDebug()) {
      log(method.methodName() + ": " + (null != json ? json.toString() : "null"));
    }

    if (json instanceof JSONObject) {
      JSONObject jsonObj = (JSONObject) json;
      try {
          if (jsonObj.get("error_code") != null) {
            Integer errorCode = (Integer) jsonObj.get("error_code");
            String message = (String) jsonObj.get("error_msg");
            throw new FacebookException(errorCode, message);
          }
      }
      catch (JSONException e) {
          throw new FacebookException(ErrorCode.GEN_UNKNOWN_ERROR, e.getClass().getName() + ":  " + e.getMessage());
      }
    }
    return json;
  }

  /**
   * Extracts a URL from a result that consists of a URL only.
   * For JSON, that result is simply a String.
   * @param url
   * @return the URL
   */
  protected URL extractURL(Object url) throws IOException {
    if (!(url instanceof String)) {
      return null;
    }
    return (null == url || "".equals(url)) ? null : new URL( (String) url);
  }

  /**
   * Extracts an Integer from a result that consists of an Integer only.
   * @param val
   * @return the Integer
   */
  protected int extractInt(Object val) {
    try {
      return (Integer) val;
    } catch (ClassCastException cce) {
      logException(cce);
      return 0;
    }
  }

  /**
   * Extracts a Boolean from a result that consists of a Boolean only.
   * @param val
   * @return the Boolean
   */
  protected boolean extractBoolean(Object val) {
    try {
      return (Boolean) val;
    } catch (ClassCastException cce) {
      logException(cce);
      return false;
    }
  }

  /**
   * Extracts a Long from a result that consists of an Long only.
   * @param val
   * @return the Integer
   */
  protected Long extractLong(Object val) {
    try {
      return (Long) val;
    } catch (ClassCastException cce) {
      logException(cce);
      return null;
    }
  }

}
