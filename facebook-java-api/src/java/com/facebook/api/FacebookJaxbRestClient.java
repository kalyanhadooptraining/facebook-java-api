/*
 * Copyright 2007, BigTribe Corporation. All rights reserved.
 *
 * This software is an unpublished work subject to a confidentiality agreement
 * and protected by copyright and trade secret law.  Unauthorized copying,
 * redistribution or other use of this work is prohibited.  All copies must
 * retain this copyright notice.  Any use or exploitation of this work without
 * authorization could subject the perpetrator to criminal and civil liability.
 * 
 * Redistribution and use in source and binary forms, with or without        
 * modification, are permitted provided that the following conditions        
 * are met:                                                                  
 *                                                                           
 * 1. Redistributions of source code must retain the above copyright         
 *    notice, this list of conditions and the following disclaimer.          
 * 2. Redistributions in binary form must reproduce the above copyright      
 *    notice, this list of conditions and the following disclaimer in the    
 *    documentation and/or other materials provided with the distribution.   
 *
 * The information in this software is subject to change without notice
 * and should not be construed as a commitment by BigTribe Corporation.
 *
 * The above copyright notice does not indicate actual or intended publication
 * of this source code.
 *
 * $Id: bigtribetemplates.xml 5524 2006-04-06 09:40:52 -0700 (Thu, 06 Apr 2006) greening $
 */
package com.facebook.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import com.facebook.api.schema.FacebookApiException;
import com.facebook.api.schema.Listing;
import com.facebook.api.schema.MarketplaceGetListingsResponse;
import com.facebook.api.schema.MarketplaceGetSubCategoriesResponse;
import com.facebook.api.schema.MarketplaceSearchResponse;
import com.facebook.api.schema.SessionInfo;

/**
 * A FacebookRestClient that JAXB response objects. This means 
 * results from calls to the Facebook API are returned as XML 
 * and transformed into JAXB Java objects.
 */
public class FacebookJaxbRestClient extends ExtensibleClient<Object> {
    /**
     * Constructor.
     * 
     * @param apiKey your Facebook API key
     * @param secret your 'secret' Facebook key
     */
    public FacebookJaxbRestClient(String apiKey, String secret) {
      this(SERVER_URL, apiKey, secret, null);
    }
    
    /**
     * Constructor.
     * 
     * @param apiKey your Facebook API key
     * @param secret your 'secret' Facebook key
     * @param timeout the timeout to apply when making API requests to Facebook, in milliseconds
     */
    public FacebookJaxbRestClient(String apiKey, String secret, int timeout) {
      this(SERVER_URL, apiKey, secret, null, timeout);
    }

    /**
     * Constructor.
     * 
     * @param apiKey your Facebook API key
     * @param secret your 'secret' Facebook key
     * @param sessionKey the session-id to use
     */
    public FacebookJaxbRestClient(String apiKey, String secret, String sessionKey) {
      this(SERVER_URL, apiKey, secret, sessionKey);
    }
    
    /**
     * Constructor.
     * 
     * @param apiKey your Facebook API key
     * @param secret your 'secret' Facebook key
     * @param sessionKey the session-id to use
     * @param timeout the timeout to apply when making API requests to Facebook, in milliseconds
     */
    public FacebookJaxbRestClient(String apiKey, String secret, String sessionKey, int timeout) {
      this(SERVER_URL, apiKey, secret, sessionKey, timeout);
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
    public FacebookJaxbRestClient(String serverAddr, String apiKey, String secret,
                              String sessionKey) throws MalformedURLException {
      this(new URL(serverAddr), apiKey, secret, sessionKey);
    }
    
    /**
     * Constructor.
     * 
     * @param serverAddr the URL of the Facebook API server to use 
     * @param apiKey your Facebook API key
     * @param secret your 'secret' Facebook key
     * @param sessionKey the session-id to use
     * @param timeout the timeout to apply when making API requests to Facebook, in milliseconds
     * 
     * @throws MalformedURLException if you specify an invalid URL 
     */
    public FacebookJaxbRestClient(String serverAddr, String apiKey, String secret,
                              String sessionKey, int timeout) throws MalformedURLException {
      this(new URL(serverAddr), apiKey, secret, sessionKey, timeout);
    }

    
    /**
     * Constructor.
     * 
     * @param serverUrl the URL of the Facebook API server to use 
     * @param apiKey your Facebook API key
     * @param secret your 'secret' Facebook key
     * @param sessionKey the session-id to use
     */
    public FacebookJaxbRestClient(URL serverUrl, String apiKey, String secret,
                              String sessionKey) {
      super(serverUrl, apiKey, secret, sessionKey);
    }
    
    /**
     * Constructor.
     * 
     * @param serverUrl the URL of the Facebook API server to use 
     * @param apiKey your Facebook API key
     * @param secret your 'secret' Facebook key
     * @param sessionKey the session-id to use
     * @param timeout the timeout to apply when making API requests to Facebook, in milliseconds
     */
    public FacebookJaxbRestClient(URL serverUrl, String apiKey, String secret,
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
    
    private String parse() {
        String xml = this.rawResponse;
        if ((xml == null) || ("".equals(xml))) {
            return null;
        }
        if (! xml.contains("</")) {
            return null;
        }
        xml = xml.substring(0, xml.indexOf("</"));
        xml = xml.substring(xml.lastIndexOf(">") + 1);
        return xml;
    }

    /**
     * Extracts a String from a result consisting entirely of a String.
     * @param val
     * @return the String
     */
    public String extractString(Object val) {
      return parse();
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
      SessionInfo d =
        (SessionInfo) this.callMethod(FacebookMethod.AUTH_GET_SESSION, 
                        new Pair<String, CharSequence>("auth_token", authToken.toString()));
      this._sessionKey = d.getSessionKey();
      this._userId = d.getUid();
      this._expires = (long)d.getExpires();
      if (this._isDesktop) {
        this._sessionSecret =
            d.getSecret();
      }
      return this._sessionKey;
    }

    /**
     * Parses the result of an API call from XML into JAXB Objects.
     * @param data an InputStream with the results of a request to the Facebook servers
     * @param method the method
     * @return a JAXB Object
     * @throws FacebookException if <code>data</code> represents an error
     * @throws IOException if <code>data</code> is not readable
     */
    protected Object parseCallResult(InputStream data, IFacebookMethod method) throws FacebookException, IOException {
      if (isDebug()) {
          System.out.println("Facebook response:  " + this.rawResponse);
      }
      Object res = this.getResponsePOJO();
      if (res instanceof FacebookApiException) {
          FacebookApiException error = (FacebookApiException)res;
          int errorCode = error.getErrorCode();
          String message = error.getErrorMsg();
          throw new FacebookException(errorCode, message);
          
      }
        return this.getResponsePOJO();
    }

    /**
     * Extracts a URL from a result that consists of a URL only.
     * For JSON, that result is simply a String.
     * @param url
     * @return the URL
     */
    protected URL extractURL(Object url) throws IOException {
      String result = parse();
      if (result != null) {
          return new URL(result);
      }
      return null;
    }

    /**
     * Extracts an Integer from a result that consists of an Integer only.
     * @param val
     * @return the Integer
     */
    protected int extractInt(Object val) {
      try {
        return Integer.parseInt(parse());
      } catch (Exception cce) {
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
        String result = parse();
        if (("1".equals(result)) || ("true".equalsIgnoreCase(result))) {
            return true;
        }
        return false;
    }

    /**
     * Extracts a Long from a result that consists of an Long only.
     * @param val
     * @return the Integer
     */
    protected Long extractLong(Object val) {
        try {
            return Long.parseLong(parse());
          } catch (Exception cce) {
            logException(cce);
            return null;
          }
    }

      /* (non-Javadoc)
       * @see com.facebook.api.IFacebookRestClient#data_getUserPreference(java.lang.Integer)
       */
      public String data_getUserPreference(Integer prefId) throws FacebookException, IOException {
          throw new FacebookException(ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  Please use an instance of FacebookRestClient instead.");
      }
      
      /* (non-Javadoc)
       * @see com.facebook.api.IFacebookRestClient#data_getUserPreferences()
       */
      public Map<Integer,String> data_getUserPreferences() throws FacebookException, IOException {
          throw new FacebookException(ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  Please use an instance of FacebookRestClient instead.");
      }
      
      /* (non-Javadoc)
       * @see com.facebook.api.IFacebookRestClient#data_setUserPreference(java.lang.Integer, java.lang.String)
       */
      public void data_setUserPreference(Integer prefId, String value) throws FacebookException, IOException {
          throw new FacebookException(ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  Please use an instance of FacebookRestClient instead.");
          
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
          MarketplaceGetListingsResponse resp = (MarketplaceGetListingsResponse)this.marketplace_getListings(listingIds, uids);
          return resp.getListing();
      }
      
      /* (non-Javadoc)
       * @see com.facebook.api.IFacebookRestClient#marketplace_getSubCategories()
       */
      public List<String> marketplace_getSubCategories() throws FacebookException, IOException {
          MarketplaceGetSubCategoriesResponse resp = (MarketplaceGetSubCategoriesResponse)this.marketplace_getSubCategories(null);
          return resp.getMarketplaceSubcategory();
      }
      
      /* (non-Javadoc)
       * @see com.facebook.api.IFacebookRestClient#marketplace_search(com.facebook.api.MarketListingCategory, com.facebook.api.MarketListingSubcategory, java.lang.String)
       */
      public List<Listing> marketplace_search(MarketListingCategory category, MarketListingSubcategory subcategory, String searchTerm) throws FacebookException, IOException {
          MarketplaceSearchResponse resp = (MarketplaceSearchResponse)this.marketplace_search(category.getName(), subcategory.getName(), searchTerm);
          return resp.getListing();
      }

      public String admin_getAppPropertiesAsString(Collection<ApplicationProperty> properties) throws FacebookException, IOException {
          JSONArray props = new JSONArray();
          for (ApplicationProperty property : properties) {
              props.put(property.getName());
          }
          this.callMethod(FacebookMethod.ADMIN_GET_APP_PROPERTIES,
                  new Pair<String, CharSequence>("properties", props.toString()));
          return extractString(null);
      }
}
