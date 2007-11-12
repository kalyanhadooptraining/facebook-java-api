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

import java.util.EnumSet;

/**
 * Enumaration that maps API method names to the (maximal) number of 
 * parameters that each method will send.
 * 
 * There are arguably better ways to do this.
 */
public enum FacebookMethod
  implements IFacebookMethod, CharSequence {
  // Authentication
  AUTH_CREATE_TOKEN("facebook.auth.createToken"),
  AUTH_GET_SESSION("facebook.auth.getSession", 1),
  // FQL Query
  FQL_QUERY("facebook.fql.query",1),
  // Events
  EVENTS_GET("facebook.events.get", 5),
  EVENTS_GET_MEMBERS("facebook.events.getMembers", 1),
  // Friends
  FRIENDS_GET("facebook.friends.get"),
  FRIENDS_GET_APP_USERS("facebook.friends.getAppUsers"),
  FRIENDS_GET_REQUESTS("facebook.friends.getRequests"),  //deprectaed/unofficial
  FRIENDS_ARE_FRIENDS("facebook.friends.areFriends", 2),
  // Users
  USERS_GET_INFO("facebook.users.getInfo", 2),
  USERS_GET_LOGGED_IN_USER("facebook.users.getLoggedInUser"),
  USERS_IS_APP_ADDED("facebook.users.isAppAdded"),
  USERS_HAS_PERMISSION("facebook.users.hasAppPermission", 2),
  /**
   * @deprecated provided for legacy support only.  Please use USERS_HAS_PERMISSION instead.
   */
  USERS_HAS_APP_PERMISSION("facebook.users.hasAppPermission", 2),  //duplicated above
  USERS_SET_STATUS("facebook.users.setStatus", 3),
  // Photos
  PHOTOS_GET("facebook.photos.get", 2),
  PHOTOS_GET_ALBUMS("facebook.photos.getAlbums", 1),
  PHOTOS_GET_TAGS("facebook.photos.getTags", 1),
  // PhotoUploads
  PHOTOS_CREATE_ALBUM("facebook.photos.createAlbum",3),
  PHOTOS_ADD_TAG("facebook.photos.addTag", 5),
  PHOTOS_UPLOAD("facebook.photos.upload", 3, true),
  // Notifications
  NOTIFICATIONS_GET("facebook.notifications.get"),
  NOTIFICATIONS_SEND("facebook.notifications.send",5),
  NOTIFICATIONS_SEND_REQUEST("facebook.notifications.sendRequest",5),
  // Groups
  GROUPS_GET("facebook.groups.get", 1),
  GROUPS_GET_MEMBERS("facebook.groups.getMembers", 1),
  // FBML
  PROFILE_SET_FBML("facebook.profile.setFBML", 2),
  PROFILE_GET_FBML("facebook.profile.getFBML", 1),
  FBML_REFRESH_REF_URL("facebook.fbml.refreshRefUrl", 1),
  FBML_REFRESH_IMG_SRC("facebook.fbml.refreshImgSrc", 1),
  FBML_SET_REF_HANDLE("facebook.fbml.setRefHandle", 3),
  // Feed
  FEED_PUBLISH_ACTION_OF_USER("facebook.feed.publishActionOfUser", 11),  //deprecated
  FEED_PUBLISH_STORY_TO_USER("facebook.feed.publishStoryToUser", 11),
  FEED_PUBLISH_TEMPLATIZED_ACTION("facebook.feed.publishTemplatizedAction", 15),
  //Marketplace
  MARKET_CREATE_LISTING("facebook.marketplace.createListing", 4),
  MARKET_GET_CATEGORIES("facebook.marketplace.getCategories", 1),
  MARKET_GET_SUBCATEGORIES("facebook.marketplace.getSubCategories", 1),
  MARKET_GET_LISTINGS("facebook.marketplace.getListings", 3),
  MARKET_REMOVE_LISTING("facebook.marketplace.removeListing", 3),
  MARKET_SEARCH("facebook.marketplace.search", 4),
  /**
   * @deprecated provided for legacy support only.  Please use MARKET_GET_CATEGORIES instead.
   */
  MARKETPLACE_GET_CATEGORIES("facebook.marketplace.getCategories", 1),
  /**
   * @deprecated provided for legacy support only.  Please use MARKET_GET_SUBCATEGORIES instead.
   */
  MARKETPLACE_GET_SUBCATEGORIES("facebook.marketplace.getSubCategories", 1),  
  /**
   * @deprecated provided for legacy support only.  Please use MARKET_GET_LISTINGS instead.
   */
  MARKETPLACE_GET_LISTINGS("facebook.marketplace.getListings", 3),  
  /**
   * @deprecated provided for legacy support only.  Please use MARKET_CREATE_LISTING instead.
   */
  MARKETPLACE_CREATE_LISTING("facebook.marketplace.createListing", 4),  
  /**
   * @deprecated provided for legacy support only.  Please use MARKET_SEARCH instead.
   */
  MARKETPLACE_SEARCH("facebook.marketplace.search", 4),  
  /**
   * @deprecated provided for legacy support only.  Please use MARKET_REMOVE_LISTING instead.
   */
  MARKETPLACE_REMOVE_LISTING("facebook.marketplace.removeListing", 3), 
  
  //Data
  DATA_SET_USER_PREFERENCE("facebook.data.setUserPreference", 3),
  DATA_SET_USER_PREFERENCES("facebook.data.setUserPreferences", 3),
  DATA_GET_USER_PREFERENCE("facebook.data.getUserPreference", 2),
  DATA_GET_USER_PREFERENCES("facebook.data.getUserPreferences", 1),
  //SMS - Mobile
  SMS_CAN_SEND("facebook.sms.canSend", 2),
  /**
   * @deprecated use SMS_SEND_MESSAGE instead.
   */
  SMS_SEND("facebook.sms.send", 4),
  SMS_SEND_MESSAGE("facebook.sms.sendMessage", 3),
  // Facebook Pages
  PAGES_IS_APP_ADDED("facebook.pages.isAppAdded", 1),
  PAGES_IS_ADMIN("facebook.pages.isAdmin", 1),
  PAGES_IS_FAN("facebook.pages.isFan", 2),
  PAGES_GET_INFO("facebook.pages.getInfo", 2),
  PAGES_GET_INFO_NO_SESSION("facebook.pages.getInfo", 2),
  ;

  private String methodName;
  private int numParams;
  private int maxParamsWithSession;
  private boolean takesFile;

  private static EnumSet<FacebookMethod> preAuth = null;
  private static EnumSet<FacebookMethod> postAuth = null;

  /**
   * Set of methods that can be called without authenticating the user/getting a session-id first.
   * 
   * @return a set listing all such methods.
   */
  public static EnumSet<FacebookMethod> preAuthMethods() {
    if (null == preAuth)
      preAuth = EnumSet.of(AUTH_CREATE_TOKEN, AUTH_GET_SESSION, SMS_SEND, SMS_SEND_MESSAGE, PAGES_GET_INFO_NO_SESSION);
    return preAuth;
  }

  /**
   * Set of methods that *cannot* be called without authenticating the user/getting a session-id first.
   * 
   * @return a set listing all such methods.
   */
  public static EnumSet<FacebookMethod> postAuthMethods() {
    if (null == postAuth)
      postAuth = EnumSet.complementOf(preAuthMethods());
    return postAuth;
  }

  FacebookMethod(String name) {
    this(name, 0, false);
  }

  FacebookMethod(String name, int maxParams ) {
    this(name, maxParams, false);
  }

  FacebookMethod(String name, int maxParams, boolean takesFile ) {
    assert (name != null && 0 != name.length());
    this.methodName = name;
    this.numParams = maxParams;
    this.maxParamsWithSession = maxParams + FacebookRestClient.NUM_AUTOAPPENDED_PARAMS;
    this.takesFile = takesFile;
  }

  /**
   * Get the Facebook method name
   * @return the Facebook method name
   */
  public String methodName() {
    return this.methodName;
  }

  /**
   * @return the maximum number of parameters this method will send
   */
  public int numParams() {
    return this.numParams;
  }

  /**
   * Check whether or not this method requires a valid session-id in order to be used.
   * 
   * @return true if the method requires a session-id prior to use
   *         false otherwise
   */
  public boolean requiresSession() {
    return postAuthMethods().contains(this);
  }

  /**
   * @return the max number of params this method will send, plus the number that will be auto-appended by the client
   */
  public int numTotalParams() {
    return requiresSession() ? this.maxParamsWithSession : this.numParams;
  }

  /**
   * @return true if this API call requires a file-stream as a parameter
   *         false otherwise
   */
  public boolean takesFile() {
    return this.takesFile;
  }

  /* Implementing CharSequence */
  public char charAt(int index) {
    return this.methodName.charAt(index);
  }

  public int length() {
    return this.methodName.length();
  }

  public CharSequence subSequence(int start, int end) {
    return this.methodName.subSequence(start, end);
  }

  public String toString() {
    return this.methodName;
  }
}
