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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.facebook.api.schema.Listing;
import com.facebook.api.schema.MarketplaceGetListingsResponse;
import com.facebook.api.schema.MarketplaceGetSubCategoriesResponse;
import com.facebook.api.schema.MarketplaceSearchResponse;

/**
 * A FacebookRestClient that uses the XML result format. This means 
 * results from calls to the Facebook API are returned as XML and 
 * transformed into instances of {@link org.w3c.dom.Document}.
 * 
 * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
 *             to use the Facebook Platform XML API. 
 */
public class FacebookXmlRestClient extends ExtensibleClient<Document> {

  /**
   * Constructor.  Don't use this, use FacebookRestClient instead.
   * 
   * @param apiKey
   * @param secret
   * 
   * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
   *             to use the Facebook Platform XML API. 
   */
  public FacebookXmlRestClient(String apiKey, String secret) {
    this(SERVER_URL, apiKey, secret, null);
  }
  
  /**
   * Constructor.  Don't use this, use FacebookRestClient instead.
   * 
   * @param apiKey
   * @param secret
   * @param timeout the timeout to apply when making API requests to Facebook, in milliseconds
   * 
   * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
   *             to use the Facebook Platform XML API. 
   */
  public FacebookXmlRestClient(String apiKey, String secret, int timeout) {
    this(SERVER_URL, apiKey, secret, null, timeout);
  }

  /**
   * Constructor.  Don't use this, use FacebookRestClient instead.
   * 
   * @param apiKey
   * @param secret
   * @param sessionKey
   * 
   * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
   *             to use the Facebook Platform XML API. 
   */
  public FacebookXmlRestClient(String apiKey, String secret, String sessionKey) {
    this(SERVER_URL, apiKey, secret, sessionKey);
  }
  
  /**
   * Constructor.  Don't use this, use FacebookRestClient instead.
   * 
   * @param apiKey
   * @param secret
   * @param sessionKey
   * @param timeout the timeout to apply when making API requests to Facebook, in milliseconds
   * 
   * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
   *             to use the Facebook Platform XML API. 
   */
  public FacebookXmlRestClient(String apiKey, String secret, String sessionKey, int timeout) {
    this(SERVER_URL, apiKey, secret, sessionKey, timeout);
  }

  /**
   * Constructor.  Don't use this, use FacebookRestClient instead.
   * 
   * @param serverAddr
   * @param apiKey
   * @param secret
   * @param sessionKey
   * 
   * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
   *             to use the Facebook Platform XML API. 
   */
  public FacebookXmlRestClient(String serverAddr, String apiKey, String secret,
                            String sessionKey) throws MalformedURLException {
    this(new URL(serverAddr), apiKey, secret, sessionKey);
  }
  
  /**
   * Constructor.  Don't use this, use FacebookRestClient instead.
   * 
   * @param serverAddr
   * @param apiKey
   * @param secret
   * @param sessionKey
   * @param timeout the timeout to apply when making API requests to Facebook, in milliseconds
   * 
   * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
   *             to use the Facebook Platform XML API. 
   */
  public FacebookXmlRestClient(String serverAddr, String apiKey, String secret,
                            String sessionKey, int timeout) throws MalformedURLException {
    this(new URL(serverAddr), apiKey, secret, sessionKey, timeout);
  }

  /**
   * Constructor.  Don't use this, use FacebookRestClient instead.
   * 
   * @param serverUrl
   * @param apiKey
   * @param secret
   * @param sessionKey
   * 
   * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
   *             to use the Facebook Platform XML API. 
   */
  public FacebookXmlRestClient(URL serverUrl, String apiKey, String secret,
                            String sessionKey) {
    super(serverUrl, apiKey, secret, sessionKey);
  }
  
  /**
   * Constructor.  Don't use this, use FacebookRestClient instead.
   * 
   * @param serverUrl
   * @param apiKey
   * @param secret
   * @param sessionKey
   * @param timeout the timeout to apply when making API requests to Facebook, in milliseconds
   * 
   * @deprecated this is provided for legacy support only.  Please use FacebookRestClient instead if you want 
   *             to use the Facebook Platform XML API. 
   */
  public FacebookXmlRestClient(URL serverUrl, String apiKey, String secret,
                            String sessionKey, int timeout) {
    super(serverUrl, apiKey, secret, sessionKey, timeout);
  }

  /**
   * The response format in which results to FacebookMethod calls are returned
   * @return the format: either XML, JSON, or null (API default)
   */
  public String getResponseFormat() {
    return "xml";
  }

  /**
   * Extracts a String from a T consisting entirely of a String.
   * @return the String
   */
  public String extractString(Document d) {
    return d.getFirstChild().getTextContent();
  }

  /**
   * Call this function to retrieve the session information after your user has
   * logged in.
   * @param authToken the token returned by auth_createToken or passed back to your callback_url.
   */
  public String auth_getSession(String authToken) throws FacebookException,
                                                         IOException {
    if (null != this._sessionKey) {
      return this._sessionKey;
    }
    Document d =
      this.callMethod(FacebookMethod.AUTH_GET_SESSION, 
                      new Pair<String, CharSequence>("auth_token", authToken.toString()));
    this._sessionKey =
        d.getElementsByTagName("session_key").item(0).getFirstChild().getTextContent();
    this._userId = Integer.parseInt(d.getElementsByTagName("uid").item(0).getFirstChild().getTextContent());
    this._expires =
        Long.parseLong(d.getElementsByTagName("expires").item(0).getFirstChild().getTextContent());
    if (this._isDesktop) {
      this._sessionSecret =
          d.getElementsByTagName("secret").item(0).getFirstChild().getTextContent();
    }
    return this._sessionKey;
  }

  protected Document parseCallResult(InputStream data, IFacebookMethod method) throws FacebookException, IOException {
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(data);
      doc.normalizeDocument();
      stripEmptyTextNodes(doc);

      if (isDebug()) {
        FacebookXmlRestClient.printDom(doc, method.methodName() + "| ");
      }
      NodeList errors = doc.getElementsByTagName(ERROR_TAG);
      if (errors.getLength() > 0) {
        int errorCode =
          Integer.parseInt(errors.item(0).getFirstChild().getFirstChild().getTextContent());
        String message = errors.item(0).getFirstChild().getNextSibling().getTextContent();
        throw new FacebookException(errorCode, message);
      }
      return doc;
    } catch (ParserConfigurationException ex) {
      System.err.println("huh?" + ex);
    } catch (SAXException ex) {
      throw new IOException("error parsing xml");
    }
    return null;
  }

  /**
   * Extracts a URL from a document that consists of a URL only.
   * @param doc
   * @return the URL
   */
  protected URL extractURL(Document doc) throws IOException {
    String url = doc.getFirstChild().getTextContent();
    return (null == url || "".equals(url)) ? null : new URL(url);
  }

  /**
   * Extracts an Integer from a document that consists of an Integer only.
   * @param doc
   * @return the Integer
   */
  protected int extractInt(Document doc) {
    return Integer.parseInt(doc.getFirstChild().getTextContent());
  }

  /**
   * Extracts a Long from a document that consists of a Long only.
   * @param doc
   * @return the Long
   */
  protected Long extractLong(Document doc) {
    return Long.parseLong(doc.getFirstChild().getTextContent());
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
      } else {
        stripEmptyTextNodes(c);
      }
    }
  }
  
  /**
   * Prints out the DOM tree.
   */
  public static void printDom(Node n, String prefix) {
    String outString = prefix;
    if (n.getNodeType() == Node.TEXT_NODE) {
      outString += "'" + n.getTextContent().trim() + "'";
    } else {
      outString += n.getNodeName();
    }
    System.out.println(outString);
    NodeList children = n.getChildNodes();
    int length = children.getLength();
    for (int i = 0; i < length; i++) {
      FacebookXmlRestClient.printDom(children.item(i), prefix + "  ");
    }
  }

    /* (non-Javadoc)
     * @see com.facebook.api.IFacebookRestClient#data_getUserPreference(java.lang.Integer)
     */
    public String data_getUserPreference(Integer prefId) throws FacebookException, IOException {
        throw new FacebookException(ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  " +
        "Please use an instance of FacebookRestClient instead.");
    }
    
    /* (non-Javadoc)
     * @see com.facebook.api.IFacebookRestClient#data_getUserPreferences()
     */
    public Map<Integer,String> data_getUserPreferences() throws FacebookException, IOException {
        throw new FacebookException(ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  " +
        "Please use an instance of FacebookRestClient instead.");
    }
    
    /* (non-Javadoc)
     * @see com.facebook.api.IFacebookRestClient#data_setUserPreference(java.lang.Integer, java.lang.String)
     */
    public void data_setUserPreference(Integer prefId, String value) throws FacebookException, IOException {
        throw new FacebookException(ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  " +
        "Please use an instance of FacebookRestClient instead.");
        
    }
    
    /* (non-Javadoc)
     * @see com.facebook.api.IFacebookRestClient#data_setUserPreferences(java.util.Map, boolean)
     */
    public void data_setUserPreferences(Map<Integer,String> values, boolean replace) throws FacebookException, IOException {
        throw new FacebookException(ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  " +
        "Please use an instance of FacebookRestClient instead.");
        
    }
    
    /* (non-Javadoc)
     * @see com.facebook.api.IFacebookRestClient#marketplace_getListings(java.util.List, java.util.List)
     */
    public List<Listing> marketplace_getListings(List<Long> listingIds, List<Long> uids) throws FacebookException, IOException {
        this.marketplace_getListings(listingIds, uids);
        MarketplaceGetListingsResponse resp = (MarketplaceGetListingsResponse)this.getResponsePOJO();
        return resp.getListing();
    }
    
    /* (non-Javadoc)
     * @see com.facebook.api.IFacebookRestClient#marketplace_getSubCategories()
     */
    public List<String> marketplace_getSubCategories() throws FacebookException, IOException {
        this.marketplace_getSubCategories(null);
        MarketplaceGetSubCategoriesResponse resp = (MarketplaceGetSubCategoriesResponse)this.getResponsePOJO();
        return resp.getMarketplaceSubcategory();
    }
    
    /* (non-Javadoc)
     * @see com.facebook.api.IFacebookRestClient#marketplace_search(com.facebook.api.MarketListingCategory, com.facebook.api.MarketListingSubcategory, java.lang.String)
     */
    public List<Listing> marketplace_search(MarketListingCategory category, MarketListingSubcategory subcategory, String searchTerm) throws FacebookException, IOException {
        this.marketplace_search(category.getName(), subcategory.getName(), searchTerm);
        MarketplaceSearchResponse resp = (MarketplaceSearchResponse)this.getResponsePOJO();
        return resp.getListing();
    }
    
    public JSONArray admin_getAppProperties(Collection<ApplicationProperty> properties) throws FacebookException, IOException {
        String json = this.admin_getAppPropertiesAsString(properties);
        try {
            return new JSONArray(json);
        }
        catch (Exception e) {
            //response failed to parse
            throw new FacebookException(ErrorCode.GEN_SERVICE_ERROR, "Failed to parse server response:  " + json);
        }
    }

    public String admin_getAppPropertiesAsString(Collection<ApplicationProperty> properties) throws FacebookException, IOException {
        JSONArray props = new JSONArray();
        for (ApplicationProperty property : properties) {
            props.put(property.getName());
        }
        Document d = this.callMethod(FacebookMethod.ADMIN_GET_APP_PROPERTIES,
                new Pair<String, CharSequence>("properties", props.toString()));
        return extractString(d);
    }
}
