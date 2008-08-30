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

package com.google.code.facebookapi;

import java.util.EnumSet;

/**
 * Enumaration that maps API method names to the (maximal) number of parameters that each method will send.
 * 
 * There are arguably better ways to do this.
 */
public enum FacebookMethod implements IFacebookMethod, CharSequence {

	// Authentication
	AUTH_CREATE_TOKEN("facebook.auth.createToken"),
	AUTH_GET_SESSION("facebook.auth.getSession", 1),
	AUTH_EXPIRE_SESSION("facebook.auth.expireSession", 1),
	AUTH_REVOKE_AUTHORIZATION("facebook.auth.revokeAuthorization", 1),
	AUTH_PROMOTE_SESSION("facebook.auth.promoteSession", 1),
	// FQL Query
	FQL_QUERY("facebook.fql.query", 1),
	// Events
	EVENTS_GET("facebook.events.get", 5),
	EVENTS_GET_MEMBERS("facebook.events.getMembers", 1),
	// Friends
	FRIENDS_GET_APP_USERS("facebook.friends.getAppUsers"),
	FRIENDS_GET_REQUESTS("facebook.friends.getRequests"), // deprectaed/unofficial
	FRIENDS_ARE_FRIENDS("facebook.friends.areFriends", 2),
	FRIENDS_GET("facebook.friends.get", 1),
	FRIENDS_GET_LISTS("facebook.friends.getLists"),
	// Users
	USERS_GET_INFO("facebook.users.getInfo", 2),
	USERS_GET_STANDARD_INFO("facebook.users.getStandardInfo", 2),
	USERS_GET_LOGGED_IN_USER("facebook.users.getLoggedInUser"),
	USERS_IS_APP_ADDED("facebook.users.isAppAdded"),
	USERS_IS_APP_ADDED_NOSESSION("facebook.users.isAppAdded", 1),
	USERS_HAS_PERMISSION("facebook.users.hasAppPermission", 2),
	USERS_HAS_PERMISSION_NOSESSION("facebook.users.hasAppPermission", 2),
	/**
	 * @deprecated provided for legacy support only. Please use USERS_HAS_PERMISSION instead.
	 */
	@Deprecated
	USERS_HAS_APP_PERMISSION("facebook.users.hasAppPermission", 2), // duplicated above
	USERS_SET_STATUS("facebook.users.setStatus", 4),
	USERS_SET_STATUS_NOSESSION("facebook.users.setStatus", 4),
	// Photos
	PHOTOS_GET("facebook.photos.get", 2),
	PHOTOS_GET_ALBUMS("facebook.photos.getAlbums", 1),
	PHOTOS_GET_TAGS("facebook.photos.getTags", 1),
	// PhotoUploads
	PHOTOS_CREATE_ALBUM("facebook.photos.createAlbum", 3),
	PHOTOS_CREATE_ALBUM_NOSESSION("facebook.photos.createAlbum", 3),
	PHOTOS_ADD_TAG("facebook.photos.addTag", 5),
	PHOTOS_ADD_TAG_NOSESSION("facebook.photos.addTag", 5),
	PHOTOS_UPLOAD("facebook.photos.upload", 3),
	PHOTOS_UPLOAD_NOSESSION("facebook.photos.upload", 3),
	// Notifications
	NOTIFICATIONS_GET("facebook.notifications.get"),
	NOTIFICATIONS_SEND("facebook.notifications.send", 4),
	@Deprecated
	NOTIFICATIONS_SEND_REQUEST("facebook.notifications.sendRequest", 5),
	NOTIFICATIONS_SEND_EMAIL("facebook.notifications.sendEmail", 5),
	NOTIFICATIONS_SEND_EMAIL_SESSION("facebook.notifications.sendEmail", 5),
	// Groups
	GROUPS_GET("facebook.groups.get", 1),
	GROUPS_GET_MEMBERS("facebook.groups.getMembers", 1),
	// Profile
	PROFILE_SET_FBML("facebook.profile.setFBML", 4),
	PROFILE_SET_FBML_NOSESSION("facebook.profile.setFBML", 4),
	PROFILE_GET_FBML("facebook.profile.getFBML", 1),
	PROFILE_GET_FBML_NOSESSION("facebook.profile.getFBML", 1),
	PROFILE_SET_INFO("facebook.profile.setInfo", 5),
	PROFILE_SET_INFO_OPTIONS("facebook.profile.setInfoOptions", 3),
	PROFILE_GET_INFO("facebook.profile.getInfo", 2),
	PROFILE_GET_INFO_OPTIONS("facebook.profile.getInfoOptions", 2),
	// FBML
	FBML_REFRESH_REF_URL("facebook.fbml.refreshRefUrl", 1),
	FBML_REFRESH_IMG_SRC("facebook.fbml.refreshImgSrc", 1),
	FBML_SET_REF_HANDLE("facebook.fbml.setRefHandle", 2),
	// Feed
	FEED_PUBLISH_ACTION_OF_USER("facebook.feed.publishActionOfUser", 11), // deprecated
	FEED_PUBLISH_STORY_TO_USER("facebook.feed.publishStoryToUser", 11),
	FEED_PUBLISH_TEMPLATIZED_ACTION("facebook.feed.publishTemplatizedAction", 15),
	FEED_REGISTER_TEMPLATE("facebook.feed.registerTemplateBundle", 4),
	FEED_GET_TEMPLATES("facebook.feed.getRegisteredTemplateBundles", 1),
	FEED_GET_TEMPLATE_BY_ID("facebook.feed.getRegisteredTemplateBundleByID", 2),
	FEED_PUBLISH_USER_ACTION("facebook.feed.publishUserAction", 5),
	FEED_DEACTIVATE_TEMPLATE_BUNDLE("facebook.feed.deactivateTemplateBundleByID", 2),
	// Marketplace
	MARKET_CREATE_LISTING("facebook.marketplace.createListing", 4),
	MARKET_CREATE_LISTING_NOSESSION("facebook.marketplace.createListing", 4),
	MARKET_GET_CATEGORIES("facebook.marketplace.getCategories", 1),
	MARKET_GET_SUBCATEGORIES("facebook.marketplace.getSubCategories", 1),
	MARKET_GET_LISTINGS("facebook.marketplace.getListings", 3),
	MARKET_REMOVE_LISTING("facebook.marketplace.removeListing", 3),
	MARKET_REMOVE_LISTING_NOSESSION("facebook.marketplace.removeListing", 3),
	MARKET_SEARCH("facebook.marketplace.search", 4),
	/**
	 * @deprecated provided for legacy support only. Please use MARKET_GET_CATEGORIES instead.
	 */
	@Deprecated
	MARKETPLACE_GET_CATEGORIES("facebook.marketplace.getCategories", 1),
	/**
	 * @deprecated provided for legacy support only. Please use MARKET_GET_SUBCATEGORIES instead.
	 */
	@Deprecated
	MARKETPLACE_GET_SUBCATEGORIES("facebook.marketplace.getSubCategories", 1),
	/**
	 * @deprecated provided for legacy support only. Please use MARKET_GET_LISTINGS instead.
	 */
	@Deprecated
	MARKETPLACE_GET_LISTINGS("facebook.marketplace.getListings", 3),
	/**
	 * @deprecated provided for legacy support only. Please use MARKET_CREATE_LISTING instead.
	 */
	@Deprecated
	MARKETPLACE_CREATE_LISTING("facebook.marketplace.createListing", 4),
	/**
	 * @deprecated provided for legacy support only. Please use MARKET_SEARCH instead.
	 */
	@Deprecated
	MARKETPLACE_SEARCH("facebook.marketplace.search", 4),
	/**
	 * @deprecated provided for legacy support only. Please use MARKET_REMOVE_LISTING instead.
	 */
	@Deprecated
	MARKETPLACE_REMOVE_LISTING("facebook.marketplace.removeListing", 3),

	// Data
	DATA_SET_COOKIE("facebook.data.setCookie", 5),
	DATA_GET_COOKIES("facebook.data.getCookies", 2),
	DATA_SET_USER_PREFERENCE("facebook.data.setUserPreference", 3),
	DATA_SET_USER_PREFERENCES("facebook.data.setUserPreferences", 3),
	DATA_GET_USER_PREFERENCE("facebook.data.getUserPreference", 2),
	DATA_GET_USER_PREFERENCES("facebook.data.getUserPreferences", 1),

	// SMS - Mobile
	SMS_CAN_SEND("facebook.sms.canSend", 2),
	/**
	 * @deprecated use SMS_SEND_MESSAGE instead.
	 */
	@Deprecated
	SMS_SEND("facebook.sms.send", 4),
	SMS_SEND_MESSAGE("facebook.sms.send", 3),
	// Facebook Pages
	PAGES_IS_APP_ADDED("facebook.pages.isAppAdded", 1),
	PAGES_IS_ADMIN("facebook.pages.isAdmin", 1),
	PAGES_IS_FAN("facebook.pages.isFan", 2),
	PAGES_GET_INFO("facebook.pages.getInfo", 2),
	PAGES_GET_INFO_NO_SESSION("facebook.pages.getInfo", 2),

	// Admin
	ADMIN_GET_APP_PROPERTIES("facebook.admin.getAppProperties", 2),
	ADMIN_SET_APP_PROPERTIES("facebook.admin.setAppProperties", 2),
	ADMIN_GET_ALLOCATION("facebook.admin.getAllocation", 2),
	@Deprecated
	ADMIN_GET_DAILY_METRICS("facebook.admin.getDailyMetrics", 4),
	ADMIN_GET_METRICS("facebook.admin.getMetrics", 5),

	// Permissions
	PERM_GRANT_API_ACCESS("facebook.permissions.grantApiAccess", 3),
	PERM_CHECK_AVAILABLE_API_ACCESS("facebook.permissions.checkAvailableApiAccess", 2),
	PERM_REVOKE_API_ACCESS("facebook.permissions.revokeApiAccess", 2),
	// facebook.permissions.checkGrantedApiAccess
	PERM_CHECK_GRANTED_API_ACCESS("facebook.permissions.checkGrantedApiAccess", 2),

	// Application
	APPLICATION_GET_PUBLIC_INFO("facebook.application.getPublicInfo", 1),

	// LiveMessage
	LIVEMESSAGE_SEND("facebook.livemessage.send", 4),

	// Batch
	BATCH_RUN("facebook.batch.run", 3);

	private String methodName;
	private int numParams;
	private int maxParamsWithSession;

	private static final EnumSet<FacebookMethod> listSessionOptional;
	private static final EnumSet<FacebookMethod> listSessionRequired;
	private static final EnumSet<FacebookMethod> listTakesFile;

	static {
		/* , APPLICATION_GET_PUBLIC_INFO */
		listSessionOptional = EnumSet.of( AUTH_CREATE_TOKEN, AUTH_GET_SESSION, SMS_SEND, SMS_SEND_MESSAGE, NOTIFICATIONS_SEND_EMAIL, PERM_CHECK_AVAILABLE_API_ACCESS,
				PERM_GRANT_API_ACCESS, PERM_CHECK_GRANTED_API_ACCESS, PERM_REVOKE_API_ACCESS, ADMIN_GET_APP_PROPERTIES, ADMIN_SET_APP_PROPERTIES, DATA_SET_COOKIE,
				DATA_GET_COOKIES, FBML_REFRESH_IMG_SRC, FBML_REFRESH_REF_URL, FBML_SET_REF_HANDLE, MARKETPLACE_GET_CATEGORIES, MARKET_GET_CATEGORIES,
				MARKETPLACE_GET_SUBCATEGORIES, MARKET_GET_SUBCATEGORIES, PAGES_IS_APP_ADDED, PROFILE_SET_FBML_NOSESSION, PROFILE_GET_FBML_NOSESSION,
				USERS_SET_STATUS_NOSESSION, MARKET_CREATE_LISTING_NOSESSION, MARKET_REMOVE_LISTING_NOSESSION, PHOTOS_ADD_TAG_NOSESSION, PHOTOS_CREATE_ALBUM_NOSESSION,
				PHOTOS_UPLOAD_NOSESSION, USERS_HAS_PERMISSION_NOSESSION, USERS_IS_APP_ADDED_NOSESSION, PAGES_GET_INFO_NO_SESSION, FEED_REGISTER_TEMPLATE,
				FEED_GET_TEMPLATES, FEED_GET_TEMPLATE_BY_ID, PROFILE_GET_INFO, PROFILE_GET_INFO_OPTIONS, PROFILE_SET_INFO, PROFILE_SET_INFO_OPTIONS,
				ADMIN_GET_METRICS /* , USERS_GET_STANDARD_INFO */, FEED_DEACTIVATE_TEMPLATE_BUNDLE );
		listSessionRequired = EnumSet.complementOf( preAuthMethods() );
		listTakesFile = EnumSet.of( PHOTOS_UPLOAD, PHOTOS_UPLOAD_NOSESSION );
	}

	/**
	 * Set of methods that can be called without authenticating the user/getting a session-id first.
	 * 
	 * @return a set listing all such methods.
	 */
	public static EnumSet<FacebookMethod> preAuthMethods() {
		return listSessionOptional;
	}

	/**
	 * Set of methods that *cannot* be called without authenticating the user/getting a session-id first.
	 * 
	 * @return a set listing all such methods.
	 */
	public static EnumSet<FacebookMethod> postAuthMethods() {
		return listSessionRequired;
	}

	FacebookMethod( String name ) {
		this( name, 0 );
	}

	FacebookMethod( String name, int maxParams ) {
		assert ( name != null && 0 != name.length() );
		this.methodName = name;
		this.numParams = maxParams;
		this.maxParamsWithSession = maxParams + ExtensibleClient.NUM_AUTOAPPENDED_PARAMS;
	}

	/**
	 * Get the Facebook method name
	 * 
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
	 * @return true if the method requires a session-id prior to use false otherwise
	 */
	public boolean requiresSession() {
		return postAuthMethods().contains( this );
	}

	/**
	 * @return the max number of params this method will send, plus the number that will be auto-appended by the client
	 */
	public int numTotalParams() {
		return requiresSession() ? this.maxParamsWithSession : this.numParams;
	}

	/**
	 * @return true if this API call requires a file-stream as a parameter false otherwise
	 */
	public boolean takesFile() {
		return listTakesFile.contains( this );
	}

	/* Implementing CharSequence */
	public char charAt( int index ) {
		return this.methodName.charAt( index );
	}

	public int length() {
		return this.methodName.length();
	}

	public CharSequence subSequence( int start, int end ) {
		return this.methodName.subSequence( start, end );
	}

	public String toString() {
		return this.methodName;
	}
}
