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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;

import org.json.JSONObject;

import com.google.code.facebookapi.schema.Listing;

/**
 * Generic interface for a FacebookRestClient, parameterized by output format. For continually updated documentation, please refer to the <a
 * href="http://wiki.developers.facebook.com/index.php/API"> Developer Wiki</a>.
 */
public interface IFacebookRestClient<T> {

	public static final String TARGET_API_VERSION = "1.0";
	public static final String ERROR_TAG = "error_response";
	public static final String FB_SERVER = "api.new.facebook.com/restserver.php";
	public static final String SERVER_ADDR = "http://" + FB_SERVER;
	public static final String HTTPS_SERVER_ADDR = "https://" + FB_SERVER;

	/**
	 * Toggle debug mode.
	 * 
	 * @param isDebug
	 *            set to true to enable debug set to false to disable debug
	 * @deprecated we are now using commons-logging
	 */
	public void setDebug( boolean isDebug );

	/**
	 * Check to see if debug mode is enabled.
	 * 
	 * @return true if debug is enabled false otherwise
	 * @deprecated we are now using commons-logging
	 */
	public boolean isDebug();

	/**
	 * Check to see if the client is running in desktop-app mode
	 * 
	 * @return true if the app is running in desktop mode. false otherwise
	 */
	public boolean isDesktop();

	/**
	 * Set the client to run in desktop-app mode.
	 * 
	 * @param isDesktop
	 *            set to true to enable desktop mode set to false to disable desktop mode
	 */
	public void setIsDesktop( boolean isDesktop );

	public T getCacheFriendsList();

	public void setCacheFriendsList( List<Long> friendIds );

	public Boolean getCacheAppAdded();

	public void setCacheAppAdded( Boolean appAdded );

	public void setCacheSession( String cacheSessionKey, Long cacheUserId, Long cacheSessionExpires );

	/**
	 * Sets the FBML for a user's profile, including the content for both the profile box and the profile actions.
	 * 
	 * @param userId
	 *            the user whose profile FBML to set
	 * @param fbmlMarkup
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @return a boolean indicating whether the FBML was successfully set
	 * @deprecated Use {@link FacebookRestClient#profile_setFBML(CharSequence,CharSequence,Long)} instead.
	 * @see #profile_setFBML(CharSequence,CharSequence,Long)
	 */
	@Deprecated
	public boolean profile_setFBML( CharSequence fbmlMarkup, Long userId ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for a user's profile, including the content for both the profile box and the profile actions.
	 * 
	 * @param userId
	 *            the user whose profile FBML to set.
	 * @param profileFbml -
	 *            FBML markup for the user's profile page.
	 * @param actionFbml -
	 *            FBML markup for the user's profile-actions section.
	 * @param mobileFbml -
	 *            FBML markup for mobile visitors to the user's profile page.
	 * 
	 * @return a boolean indicating whether the FBML was successfully set
	 */
	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for a profile box on the logged-in user's profile.
	 * 
	 * @param fbmlMarkup
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFbml</a>
	 */
	public boolean profile_setProfileFBML( CharSequence fbmlMarkup ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for profile actions for the logged-in user.
	 * 
	 * @param fbmlMarkup
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFBML</a>
	 */
	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for the logged-in user's profile on mobile devices.
	 * 
	 * @param fbmlMarkup
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFBML</a>
	 */
	public boolean profile_setMobileFBML( CharSequence fbmlMarkup ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for a profile box on the user or page profile with ID <code>profileId</code>.
	 * 
	 * @param fbmlMarkup
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @param profileId
	 *            a page or user ID (null for the logged-in user)
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFbml</a>
	 */
	public boolean profile_setProfileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for profile actions for the user or page profile with ID <code>profileId</code>.
	 * 
	 * @param fbmlMarkup
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @param profileId
	 *            a page or user ID (null for the logged-in user)
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFBML</a>
	 */
	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for the user or page profile with ID <code>profileId</code> on mobile devices.
	 * 
	 * @param fbmlMarkup
	 *            refer to the FBML documentation for a description of the markup and its role in various contexts
	 * @param profileId
	 *            a page or user ID (null for the logged-in user)
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFBML</a>
	 */
	public boolean profile_setMobileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for the profile box and profile actions for the logged-in user. Refer to the FBML documentation for a description of the markup and its role in
	 * various contexts.
	 * 
	 * @param profileFbmlMarkup
	 *            the FBML for the profile box
	 * @param profileActionFbmlMarkup
	 *            the FBML for the profile actions
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFBML</a>
	 */
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for the profile box and profile actions for the user or page profile with ID <code>profileId</code>. Refer to the FBML documentation for a
	 * description of the markup and its role in various contexts.
	 * 
	 * @param profileFbmlMarkup
	 *            the FBML for the profile box
	 * @param profileActionFbmlMarkup
	 *            the FBML for the profile actions
	 * @param profileId
	 *            a page or user ID (null for the logged-in user)
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFBML</a>
	 */
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, Long profileId ) throws FacebookException, IOException;

	/**
	 * Sets the FBML for the profile box, profile actions, and mobile devices for the user or page profile with ID <code>profileId</code>. Refer to the FBML
	 * documentation for a description of the markup and its role in various contexts.
	 * 
	 * @param profileFbmlMarkup
	 *            the FBML for the profile box
	 * @param profileActionFbmlMarkup
	 *            the FBML for the profile actions
	 * @param mobileFbmlMarkup
	 *            the FBML for mobile devices
	 * @param profileId
	 *            a page or user ID (null for the logged-in user)
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFBML</a>
	 */
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup, Long profileId )
			throws FacebookException, IOException;

	/**
	 * Sets the FBML for the profile box, profile actions, and mobile devices for the current user. Refer to the FBML documentation for a description of the markup and
	 * its role in various contexts.
	 * 
	 * @param profileFbmlMarkup
	 *            the FBML for the profile box
	 * @param profileActionFbmlMarkup
	 *            the FBML for the profile actions
	 * @param mobileFbmlMarkup
	 *            the FBML for mobile devices
	 * @param profileId
	 *            a page or user ID (null for the logged-in user)
	 * @return a boolean indicating whether the FBML was successfully set
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Profile.setFBML"> Developers wiki: Profile.setFBML</a>
	 */
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup ) throws FacebookException,
			IOException;

	/**
	 * Gets the FBML for a user's profile, including the content for both the profile box and the profile actions.
	 * 
	 * @param userId -
	 *            the user whose profile FBML to set
	 * @return a T containing FBML markup
	 */
	public T profile_getFBML( Long userId ) throws FacebookException, IOException;

	/**
	 * Gets the FBML for the current user's profile, including the content for both the profile box and the profile actions.
	 * 
	 * @param userId -
	 *            the user whose profile FBML to get
	 * @return a T containing FBML markup
	 */
	public T profile_getFBML() throws FacebookException, IOException;

	/**
	 * Recaches the referenced url.
	 * 
	 * @param url
	 *            string representing the URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshRefUrl( String url ) throws FacebookException, IOException;

	/**
	 * Recaches the referenced url.
	 * 
	 * @param url
	 *            the URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshRefUrl( URL url ) throws FacebookException, IOException;

	/**
	 * Recaches the image with the specified imageUrl.
	 * 
	 * @param imageUrl
	 *            String representing the image URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshImgSrc( String imageUrl ) throws FacebookException, IOException;

	/**
	 * Recaches the image with the specified imageUrl.
	 * 
	 * @param imageUrl
	 *            the image URL to refresh
	 * @return boolean indicating whether the refresh succeeded
	 */
	public boolean fbml_refreshImgSrc( URL imageUrl ) throws FacebookException, IOException;

	/**
	 * Publishes a Mini-Feed story describing an action taken by a user, and publishes aggregating News Feed stories to the friends of that user. Stories are identified
	 * as being combinable if they have matching templates and substituted values.
	 * 
	 * @param actorId
	 *            deprecated
	 * @param titleTemplate
	 *            markup (up to 60 chars, tags excluded) for the feed story's title section. Must include the token <code>{actor}</code>.
	 * @return whether the action story was successfully published; false in case of a permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction"> Developers Wiki: Feed.publishTemplatizedAction</a>
	 * @see <a href="http://developers.facebook.com/tools.php?feed"> Developers Resources: Feed Preview Console </a>
	 * @deprecated since 01/18/2008
	 */
	@Deprecated
	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate ) throws FacebookException, IOException;

	/**
	 * Publishes a Mini-Feed story describing an action taken by the logged-in user, and publishes aggregating News Feed stories to their friends. Stories are identified
	 * as being combinable if they have matching templates and substituted values.
	 * 
	 * @param titleTemplate
	 *            markup (up to 60 chars, tags excluded) for the feed story's title section. Must include the token <code>{actor}</code>.
	 * @return whether the action story was successfully published; false in case of a permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction"> Developers Wiki: Feed.publishTemplatizedAction</a>
	 * @see <a href="http://developers.facebook.com/tools.php?feed"> Developers Resources: Feed Preview Console </a>
	 */
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate ) throws FacebookException, IOException;

	/**
	 * Publishes a Mini-Feed story describing an action taken by the logged-in user (or, if <code>pageActorId</code> is provided, page), and publishes aggregating News
	 * Feed stories to the user's friends/page's fans. Stories are identified as being combinable if they have matching templates and substituted values.
	 * 
	 * @param titleTemplate
	 *            markup (up to 60 chars, tags excluded) for the feed story's title section. Must include the token <code>{actor}</code>.
	 * @param pageActorId
	 *            (optional) the ID of the page into whose mini-feed the story is being published
	 * @return whether the action story was successfully published; false in case of a permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction"> Developers Wiki: Feed.publishTemplatizedAction</a>
	 * @see <a href="http://developers.facebook.com/tools.php?feed"> Developers Resources: Feed Preview Console </a>
	 */
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Long pageActorId ) throws FacebookException, IOException;

	/**
	 * Publishes a Mini-Feed story describing an action taken by the logged-in user (or, if <code>pageActorId</code> is provided, page), and publishes aggregating News
	 * Feed stories to the user's friends/page's fans. Stories are identified as being combinable if they have matching templates and substituted values.
	 * 
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
	 * @param pageActorId
	 *            (optional) the ID of the page into whose mini-feed the story is being published
	 * @return whether the action story was successfully published; false in case of a permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction"> Developers Wiki: Feed.publishTemplatizedAction</a>
	 * @see <a href="http://developers.facebook.com/tools.php?feed"> Developers Resources: Feed Preview Console </a>
	 */
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images,
			Long pageActorId ) throws FacebookException, IOException;

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
			throws FacebookException, IOException;

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
	 * 
	 * @deprecated Facebook will be removing this API call. Please use feed_publishTemplatizedAction instead.
	 */
	@Deprecated
	public boolean feed_publishActionOfUser( CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images ) throws FacebookException,
			IOException;

	/**
	 * Publish the notification of an action taken by a user to newsfeed.
	 * 
	 * @param title
	 *            the title of the feed story (up to 60 characters, excluding tags)
	 * @param body
	 *            (optional) the body of the feed story (up to 200 characters, excluding tags)
	 * @return whether the story was successfully published; false in case of permission error
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Feed.publishActionOfUser"> Developers Wiki: Feed.publishActionOfUser</a>
	 * 
	 * @deprecated Facebook will be removing this API call. Please use feed_publishTemplatizedAction instead.
	 */
	@Deprecated
	public boolean feed_publishActionOfUser( CharSequence title, CharSequence body ) throws FacebookException, IOException;

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
			throws FacebookException, IOException;

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
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body ) throws FacebookException, IOException;

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
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body, Integer priority ) throws FacebookException, IOException;

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
	 * @see http://wiki.developers.facebook.com/index.php/Feed.publishStoryToUser
	 */
	public boolean feed_publishStoryToUser( CharSequence title, CharSequence body, Collection<? extends IPair<? extends Object,URL>> images ) throws FacebookException,
			IOException;

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
	 */
	public T events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime ) throws FacebookException, IOException;

	/**
	 * Retrieves the membership list of an event
	 * 
	 * @param eventId
	 *            event id
	 * @return T consisting of four membership lists corresponding to RSVP status, with keys 'attending', 'unsure', 'declined', and 'not_replied'
	 */
	public T events_getMembers( Number eventId ) throws FacebookException, IOException;

	/**
	 * Retrieves whether two users are friends.
	 * 
	 * @param userId1
	 * @param userId2
	 * @see http://wiki.developers.facebook.com/index.php/Friends.areFriends
	 */
	public T friends_areFriends( long userId1, long userId2 ) throws FacebookException, IOException;

	/**
	 * Retrieves whether pairs of users are friends. Returns whether the first user in <code>userIds1</code> is friends with the first user in <code>userIds2</code>,
	 * the second user in <code>userIds1</code> is friends with the second user in <code>userIds2</code>, etc.
	 * 
	 * @param userIds1
	 * @param userIds2
	 * @see http://wiki.developers.facebook.com/index.php/Friends.areFriends
	 */
	public T friends_areFriends( Collection<Long> userIds1, Collection<Long> userIds2 ) throws FacebookException, IOException;

	/**
	 * Retrieves the friends of the currently logged in user.
	 * 
	 * @see http://wiki.developers.facebook.com/index.php/Friends.get
	 */
	public T friends_get() throws FacebookException, IOException;

	/**
	 * Retrieves the friend lists of the currently logged in user.
	 * 
	 * @see http://wiki.developers.facebook.com/index.php/Friends.getLists
	 */
	public T friends_getLists() throws FacebookException, IOException;

	/**
	 * Retrieves the friends of the currently logged in user that are members of the friends list with ID <code>friendListId</code>.
	 * 
	 * @param friendListId
	 *            the friend list for which friends should be fetched. if <code>null</code>, all friends will be retrieved.
	 * @see http://wiki.developers.facebook.com/index.php/Friends.get
	 */
	public T friends_get( Long friendListId ) throws FacebookException, IOException;

	/**
	 * Retrieves the friends of the currently logged in user, who are also users of the calling application.
	 * 
	 * @return array of friends
	 */
	public T friends_getAppUsers() throws FacebookException, IOException;

	/**
	 * Retrieves the requested info fields for the requested set of users.
	 * 
	 * @param userIds
	 *            a collection of user IDs for which to fetch info
	 * @param fields
	 *            a set of ProfileFields
	 * @return a T consisting of a list of users, with each user element containing the requested fields.
	 * @see http://wiki.developers.facebook.com/index.php/Users.getInfo
	 */
	public T users_getInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException, IOException;

	/**
	 * Retrieves the requested info fields for the requested set of users.
	 * 
	 * @param userIds
	 *            a collection of user IDs for which to fetch info
	 * @param fields
	 *            a set of strings describing the info fields desired, such as "last_name", "sex"
	 * @return a T consisting of a list of users, with each user element containing the requested fields.
	 * @see http://wiki.developers.facebook.com/index.php/Users.getInfo
	 */
	public T users_getInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException, IOException;

	/**
	 * Returns an array of user-specific information for each user identifier passed, limited by the view of the current user. The information you can get from this call
	 * is limited to: uid, first_name, last_name, name, timezone, birthday, sex, affiliations (regional type only)
	 * 
	 * @param userIds
	 *            a collection of user IDs for which to fetch info
	 * @param fields
	 *            a set of ProfileFields
	 * @return a T consisting of a list of users, with each user element containing the requested fields.
	 * @see http://wiki.developers.facebook.com/index.php/Users.getStandardInfo
	 */
	public T users_getStandardInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException, IOException;

	/**
	 * Returns an array of user-specific information for each user identifier passed, limited by the view of the current user. The information you can get from this call
	 * is limited to: uid, first_name, last_name, name, timezone, birthday, sex, affiliations (regional type only)
	 * 
	 * @param userIds
	 *            a collection of user IDs for which to fetch info
	 * @param fields
	 *            a set of strings describing the info fields desired, such as "last_name", "sex"
	 * @return a T consisting of a list of users, with each user element containing the requested fields.
	 * @see http://wiki.developers.facebook.com/index.php/Users.getStandardInfo
	 */
	public T users_getStandardInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException, IOException;

	/**
	 * Retrieves the user ID of the user logged in to this API session
	 * 
	 * @return the Facebook user ID of the logged-in user
	 */
	public long users_getLoggedInUser() throws FacebookException, IOException;

	/**
	 * Retrieves an indicator of whether the logged-in user has added the application associated with the _apiKey.
	 * 
	 * @return boolean indicating whether the user has added the app
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Users.isAppAdded"> Developers Wiki: Users.isAppAdded</a>
	 */
	public boolean users_isAppAdded() throws FacebookException, IOException;

	/**
	 * Retrieves an indicator of whether the specified user has added the application associated with the _apiKey.
	 * 
	 * @param userId
	 *            the if of the user to check for.
	 * @return boolean indicating whether the user has added the app
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Users.isAppAdded"> Developers Wiki: Users.isAppAdded</a>
	 */
	public boolean users_isAppAdded( Long userId ) throws FacebookException, IOException;

	/**
	 * Retrieves whether the logged-in user has granted the specified permission to this application.
	 * 
	 * @param permission
	 *            an extended permission (e.g. FacebookExtendedPerm.MARKETPLACE, "photo_upload")
	 * @return boolean indicating whether the user has the permission
	 * @see FacebookExtendedPerm
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Users.hasAppPermission"> Developers Wiki: Users.hasAppPermission</a>
	 * 
	 * @deprecated provided for legacy support only. Please use the alternate version.
	 */
	@Deprecated
	public boolean users_hasAppPermission( CharSequence permission ) throws FacebookException, IOException;

	/**
	 * Sets the logged-in user's Facebook status. Requires the status_update extended permission.
	 * 
	 * @return whether the status was successfully set
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#STATUS_UPDATE
	 * @see http://wiki.developers.facebook.com/index.php/Users.setStatus
	 */
	public boolean users_setStatus( String status ) throws FacebookException, IOException;

	/**
	 * Sets the spedified user's Facebook status. Requires the status_update extended permission.
	 * 
	 * @return whether the status was successfully set
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#STATUS_UPDATE
	 * @see http://wiki.developers.facebook.com/index.php/Users.setStatus
	 */
	public boolean users_setStatus( String status, Long userId ) throws FacebookException, IOException;

	/**
	 * Set the user's profile status message. This requires that the user has granted the application the 'status_update' permission, otherwise the call will return an
	 * error. You can use 'users_hasAppPermission' to check to see if the user has granted your app the abbility to update their status.
	 * 
	 * @param newStatus
	 *            the new status message to set.
	 * @param clear
	 *            whether or not to clear the old status message.
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 * @see http://wiki.developers.facebook.com/index.php/Users.setStatus
	 */
	public boolean users_setStatus( String newStatus, boolean clear ) throws FacebookException, IOException;

	/**
	 * Set the user's profile status message. This requires that the user has granted the application the 'status_update' permission, otherwise the call will return an
	 * error. You can use 'users_hasAppPermission' to check to see if the user has granted your app the abbility to update their status.
	 * 
	 * @param newStatus
	 *            the new status message to set.
	 * @param clear
	 *            whether or not to clear the old status message.
	 * @param userId
	 *            the id of the user to set the status for.
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 * @see http://wiki.developers.facebook.com/index.php/Users.setStatus
	 */
	public boolean users_setStatus( String newStatus, boolean clear, Long userId ) throws FacebookException, IOException;

	/**
	 * Set the user's profile status message. This requires that the user has granted the application the 'status_update' permission, otherwise the call will return an
	 * error. You can use 'users_hasAppPermission' to check to see if the user has granted your app the abbility to update their status
	 * 
	 * @param newStatus
	 *            the new status message to set.
	 * @param clear
	 *            whether or not to clear the old status message.
	 * @param statusIncludesVerb
	 *            set to true if you do not want the Facebook Platform to automatically prepend "is " to your status message set to false if you want the "is " prepended
	 *            (default behavior)
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 * @see http://wiki.developers.facebook.com/index.php/Users.setStatus
	 */
	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb ) throws FacebookException, IOException;

	/**
	 * Set the user's profile status message. This requires that the user has granted the application the 'status_update' permission, otherwise the call will return an
	 * error. You can use 'users_hasAppPermission' to check to see if the user has granted your app the abbility to update their status
	 * 
	 * @param newStatus
	 *            the new status message to set.
	 * @param clear
	 *            whether or not to clear the old status message.
	 * @param statusIncludesVerb
	 *            set to true if you do not want the Facebook Platform to automatically prepend "is " to your status message set to false if you want the "is " prepended
	 *            (default behavior)
	 * @param userId
	 *            the id of the user to set the status for.
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 * @see http://wiki.developers.facebook.com/index.php/Users.setStatus
	 */
	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb, Long userId ) throws FacebookException, IOException;

	/**
	 * Clears the logged-in user's Facebook status. Requires the status_update extended permission.
	 * 
	 * @return whether the status was successfully cleared
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#STATUS_UPDATE
	 * @see http://wiki.developers.facebook.com/index.php/Users.setStatus
	 */
	public boolean users_clearStatus() throws FacebookException, IOException;

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param subjId
	 *            retrieve from photos associated with this user (optional).
	 * @param albumId
	 *            retrieve from photos from this album (optional)
	 * @param photoIds
	 *            retrieve from this list of photos (optional)
	 * 
	 * @return an T of photo objects.
	 */
	public T photos_get( Long subjId, Long albumId, Collection<Long> photoIds ) throws FacebookException, IOException;

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
	public T photos_get( Long subjId, Collection<Long> photoIds ) throws FacebookException, IOException;

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
	public T photos_get( Long subjId, Long albumId ) throws FacebookException, IOException;

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param photoIds
	 *            retrieve from this list of photos (optional)
	 * @return an T of photo objects.
	 * @see #photos_get(Long, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public T photos_get( Collection<Long> photoIds ) throws FacebookException, IOException;

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param subjId
	 *            retrieve from photos associated with this user (optional).
	 * @return an T of photo objects.
	 * @see #photos_get(Long, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public T photos_get( Long subjId ) throws FacebookException, IOException;

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
	public T photos_getAlbums( Long userId, Collection<Long> albumIds ) throws FacebookException, IOException;

	/**
	 * Retrieves album metadata for albums owned by a user.
	 * 
	 * @param userId
	 *            (optional) the id of the albums' owner (optional)
	 * @return album objects
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.getAlbums"> Developers Wiki: Photos.getAlbums</a>
	 */
	public T photos_getAlbums( Long userId ) throws FacebookException, IOException;

	/**
	 * Retrieves album metadata for a list of album IDs.
	 * 
	 * @param albumIds
	 *            the ids of albums whose metadata is to be retrieved
	 * @return album objects
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.getAlbums"> Developers Wiki: Photos.getAlbums</a>
	 */
	public T photos_getAlbums( Collection<Long> albumIds ) throws FacebookException, IOException;

	/**
	 * Retrieves the tags for the given set of photos.
	 * 
	 * @param photoIds
	 *            The list of photos from which to extract photo tags.
	 * @return the created album
	 */
	public T photos_getTags( Collection<Long> photoIds ) throws FacebookException, IOException;

	/**
	 * Creates an album.
	 * 
	 * @param albumName
	 *            The list of photos from which to extract photo tags.
	 * @return the created album
	 */
	public T photos_createAlbum( String albumName ) throws FacebookException, IOException;

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
	public T photos_createAlbum( String name, String description, String location ) throws FacebookException, IOException;

	/**
	 * Creates an album.
	 * 
	 * @param albumName
	 *            The list of photos from which to extract photo tags.
	 * @param userId
	 *            the id of the user creating the album.
	 * @return the created album
	 */
	public T photos_createAlbum( String albumName, Long userId ) throws FacebookException, IOException;

	/**
	 * Creates an album.
	 * 
	 * @param name
	 *            The album name.
	 * @param location
	 *            The album location (optional).
	 * @param description
	 *            The album description (optional).
	 * @param userId
	 *            the id of the user creating the album.
	 * @return an array of photo objects.
	 */
	public T photos_createAlbum( String name, String description, String location, Long userId ) throws FacebookException, IOException;

	/**
	 * Adds several tags to a photo.
	 * 
	 * @param photoId
	 *            The photo id of the photo to be tagged.
	 * @param tags
	 *            A list of PhotoTags.
	 * @return a list of booleans indicating whether the tag was successfully added.
	 */
	public T photos_addTags( Long photoId, Collection<PhotoTag> tags ) throws FacebookException, IOException;

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
	public boolean photos_addTag( Long photoId, Long taggedUserId, Double xPct, Double yPct ) throws FacebookException, IOException;

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
	public boolean photos_addTag( Long photoId, CharSequence tagText, Double xPct, Double yPct ) throws FacebookException, IOException;

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
	 * @param userId
	 *            the user tagging the photo.
	 * @return whether the tag was successfully added.
	 */
	public boolean photos_addTag( Long photoId, Long taggedUserId, Double xPct, Double yPct, Long userId ) throws FacebookException, IOException;

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
	 * @param userId
	 *            the user tagging the photo.
	 * @return whether the tag was successfully added.
	 */
	public boolean photos_addTag( Long photoId, CharSequence tagText, Double xPct, Double yPct, Long userId ) throws FacebookException, IOException;

	/**
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( File photo ) throws FacebookException, IOException;

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
	public T photos_upload( File photo, String caption ) throws FacebookException, IOException;

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
	public T photos_upload( File photo, Long albumId ) throws FacebookException, IOException;

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
	public T photos_upload( File photo, String caption, Long albumId ) throws FacebookException, IOException;

	/**
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @param userId
	 *            the id of the user uploading the photo
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( Long userId, File photo ) throws FacebookException, IOException;

	/**
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @param caption
	 *            a description of the image contents
	 * @param userId
	 *            the id of the user uploading the photo
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( Long userId, File photo, String caption ) throws FacebookException, IOException;

	/**
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @param albumId
	 *            the album into which the photo should be uploaded
	 * @param userId
	 *            the id of the user uploading the photo
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( Long userId, File photo, Long albumId ) throws FacebookException, IOException;

	/**
	 * Uploads a photo to Facebook.
	 * 
	 * @param photo
	 *            an image file
	 * @param caption
	 *            a description of the image contents
	 * @param albumId
	 *            the album into which the photo should be uploaded
	 * @param userId
	 *            the id of the user uploading the photo
	 * @return a T with the standard Facebook photo information
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.upload"> Developers wiki: Photos.upload</a>
	 */
	public T photos_upload( Long userId, File photo, String caption, Long albumId ) throws FacebookException, IOException;

	/**
	 * Retrieves the groups associated with a user
	 * 
	 * @param userId
	 *            Optional: User associated with groups. A null parameter will default to the session user.
	 * @param groupIds
	 *            Optional: group ids to query. A null parameter will get all groups for the user.
	 * @return array of groups
	 */
	public T groups_get( Long userId, Collection<Long> groupIds ) throws FacebookException, IOException;

	/**
	 * Retrieves the membership list of a group
	 * 
	 * @param groupId
	 *            the group id
	 * @return a T containing four membership lists of 'members', 'admins', 'officers', and 'not_replied'
	 */
	public T groups_getMembers( Number groupId ) throws FacebookException, IOException;

	/**
	 * Retrieves the results of a Facebook Query Language query
	 * 
	 * @param query :
	 *            the FQL query statement
	 * @return varies depending on the FQL query
	 */
	public T fql_query( CharSequence query ) throws FacebookException, IOException;

	/**
	 * Retrieves the outstanding notifications for the session user.
	 * 
	 * @return a T containing notification count pairs for 'messages', 'pokes' and 'shares', a uid list of 'friend_requests', a gid list of 'group_invites', and an eid
	 *         list of 'event_invites'
	 */
	public T notifications_get() throws FacebookException, IOException;

	/**
	 * Send a notification message to the specified users.
	 * 
	 * @param recipientIds
	 *            the user ids to which the message is to be sent
	 * @param notification
	 *            the FBML to display on the notifications page
	 * @param email
	 *            the FBML to send to the specified users via email, or null if no email should be sent
	 * @return a URL, possibly null, to which the user should be redirected to finalize the sending of the email
	 * 
	 * @deprecated notifications.send can no longer be used for sending e-mails, use notifications.sendEmail intead when sending e-mail, or the alternate version of
	 *             notifications.send if all you want to send is a notification.
	 */
	@Deprecated
	public URL notifications_send( Collection<Long> recipientIds, CharSequence notification, CharSequence email ) throws FacebookException, IOException;

	/**
	 * Send a notification message to the specified users.
	 * 
	 * @param recipientIds
	 *            the user ids to which the message is to be sent.
	 * @param notification
	 *            the FBML to display on the notifications page.
	 */
	public void notifications_send( Collection<Long> recipientIds, CharSequence notification ) throws FacebookException, IOException;

	/**
	 * Call this function and store the result, using it to generate the appropriate login url and then to retrieve the session information.
	 * 
	 * @return an authentication token
	 * @see http://wiki.developers.facebook.com/index.php/Auth.createToken
	 */
	public String auth_createToken() throws FacebookException, IOException;

	/**
	 * Call this function to retrieve the session information after your user has logged in.
	 * 
	 * @param authToken
	 *            the token returned by auth_createToken or passed back to your callback_url.
	 * @see http://wiki.developers.facebook.com/index.php/Auth.getSession
	 */
	public String auth_getSession( String authToken ) throws FacebookException, IOException;

	/**
	 * Call this function to get the user ID.
	 * 
	 * @return The ID of the current session's user, or -1 if none.
	 */
	@Deprecated
	public long auth_getUserId( String authToken ) throws FacebookException, IOException;

	public String getCacheSessionKey();

	public Long getCacheUserId();

	public Long getCacheSessionExpires();

	public String getCacheSessionSecret();

	/**
	 * Create a marketplace listing. The create_listing extended permission is required.
	 * 
	 * @param showOnProfile
	 *            whether
	 * @return the id of the created listing
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#MARKETPLACE
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.createListing"> Developers Wiki: marketplace.createListing</a>
	 * 
	 * @deprecated provided for legacy support only. Please use the version that takes a MarketListing instead.
	 */
	@Deprecated
	public Long marketplace_createListing( Boolean showOnProfile, MarketplaceListing attrs ) throws FacebookException, IOException;

	/**
	 * Modify a marketplace listing. The create_listing extended permission is required.
	 * 
	 * @return the id of the edited listing
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.createListing"> Developers Wiki: marketplace.createListing</a>
	 * 
	 * @deprecated provided for legacy support only. Please use the version that takes a MarketListing instead.
	 */
	@Deprecated
	public Long marketplace_editListing( Long listingId, Boolean showOnProfile, MarketplaceListing attrs ) throws FacebookException, IOException;

	/**
	 * Remove a marketplace listing. The create_listing extended permission is required.
	 * 
	 * @param listingId
	 *            the listing to be removed
	 * @return boolean indicating whether the listing was removed
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#MARKETPLACE
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.removeListing"> Developers Wiki: marketplace.removeListing</a>
	 */
	public boolean marketplace_removeListing( Long listingId ) throws FacebookException, IOException;

	/**
	 * Remove a marketplace listing. The create_listing extended permission is required.
	 * 
	 * @param listingId
	 *            the listing to be removed
	 * @param userId
	 *            the id of the user removing the listing
	 * @return boolean indicating whether the listing was removed
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#MARKETPLACE
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.removeListing"> Developers Wiki: marketplace.removeListing</a>
	 */
	public boolean marketplace_removeListing( Long listingId, Long userId ) throws FacebookException, IOException;

	/**
	 * Remove a marketplace listing. The create_listing extended permission is required.
	 * 
	 * @param listingId
	 *            the listing to be removed
	 * @param status
	 *            MARKETPLACE_STATUS_DEFAULT, MARKETPLACE_STATUS_SUCCESS, or MARKETPLACE_STATUS_NOT_SUCCESS
	 * @return boolean indicating whether the listing was removed
	 * @see #users_hasAppPermission
	 * @see FacebookExtendedPerm#MARKETPLACE
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.removeListing"> Developers Wiki: marketplace.removeListing</a>
	 * 
	 * @deprecated provided for legacy support only. Please use the version that takes a MarketListingStatus instead.
	 */
	@Deprecated
	public boolean marketplace_removeListing( Long listingId, CharSequence status ) throws FacebookException, IOException;

	/**
	 * Get the categories available in marketplace.
	 * 
	 * @return a T listing the marketplace categories
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getCategories"> Developers Wiki: marketplace.getCategories</a>
	 */
	public List<String> marketplace_getCategories() throws FacebookException, IOException;

	/**
	 * Get the subcategories available for a category.
	 * 
	 * @param category
	 *            a category, e.g. "HOUSING"
	 * @return a T listing the marketplace sub-categories
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getSubCategories"> Developers Wiki: marketplace.getSubCategories</a>
	 */
	public T marketplace_getSubCategories( CharSequence category ) throws FacebookException, IOException;

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
	public T marketplace_getListings( Collection<Long> listingIds, Collection<Long> userIds ) throws FacebookException, IOException;

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
	 * 
	 * @deprecated provided for legacy support only. Please use the alternate version instead.
	 */
	@Deprecated
	public T marketplace_search( CharSequence category, CharSequence subCategory, CharSequence query ) throws FacebookException, IOException;

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
	public T photos_getByAlbum( Long albumId, Collection<Long> photoIds ) throws FacebookException, IOException;

	/**
	 * Used to retrieve photo objects using the search parameters (one or more of the parameters must be provided).
	 * 
	 * @param albumId
	 *            retrieve from photos from this album (optional)
	 * @return an T of photo objects.
	 * @see #photos_get(Integer, Long, Collection)
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Photos.get"> Developers Wiki: Photos.get</a>
	 */
	public T photos_getByAlbum( Long albumId ) throws FacebookException, IOException;

	/**
	 * Get the categories available in marketplace.
	 * 
	 * @return a T listing the marketplace categories
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Marketplace.getCategories"> Developers Wiki: marketplace.getCategories</a>
	 * 
	 * @deprecated use the version that returns a List<String> instead.
	 */
	@Deprecated
	public T marketplace_getCategoriesObject() throws FacebookException, IOException;

	/**
	 * Returns a string representation for the last API response recieved from Facebook, exactly as sent by the API server.
	 * 
	 * Note that calling this method consumes the data held in the internal buffer, and thus it may only be called once per API call.
	 * 
	 * @return a String representation of the last API response sent by Facebook
	 */
	public String getRawResponse();

	/**
	 * Returns a JAXB object of the type that corresponds to the last API call made on the client. Each Facebook Platform API call that returns a Document object has a
	 * JAXB response object associated with it. The naming convention is generally intuitive. For example, if you invoke the 'user_getInfo' API call, the associated JAXB
	 * response object is 'UsersGetInfoResponse'.<br />
	 * <br />
	 * An example of how to use this method:<br />
	 * <br />
	 * FacebookRestClient client = new FacebookRestClient("apiKey", "secretKey", "sessionId");<br />
	 * client.friends_get();<br />
	 * FriendsGetResponse response = (FriendsGetResponse)client.getResponsePOJO();<br />
	 * List<Long> friends = response.getUid(); <br />
	 * <br />
	 * This is particularly useful in the case of API calls that return a Document object, as working with the JAXB response object is generally much simple than trying
	 * to walk/parse the DOM by hand.<br />
	 * <br />
	 * This method can be safely called multiple times, though note that it will only return the response-object corresponding to the most recent Facebook Platform API
	 * call made.<br />
	 * <br />
	 * Note that you must cast the return value of this method to the correct type in order to do anything useful with it.
	 * 
	 * @return a JAXB POJO ("Plain Old Java Object") of the type that corresponds to the last API call made on the client. Note that you must cast this object to its
	 *         proper type before you will be able to do anything useful with it.
	 */
	public Object getResponsePOJO();

	/**
	 * Publishes a templatized action for the current user. The action will appear in their minifeed, and may appear in their friends' newsfeeds depending upon a number
	 * of different factors. When a template match exists between multiple distinct users (like "Bob recommends Bizou" and "Sally recommends Bizou"), the feed entries may
	 * be combined in the newfeed (to something like "Bob and sally recommend Bizou"). This happens automatically, and *only* if the template match between the two feed
	 * entries is identical.<br />
	 * <br />
	 * Feed entries are not aggregated for a single user (so "Bob recommends Bizou" and "Bob recommends Le Charm" *will not* become "Bob recommends Bizou and Le Charm").<br />
	 * <br />
	 * If the user's action involves one or more of their friends, list them in the 'targetIds' parameter. For example, if you have "Bob says hi to Sally and Susie", and
	 * Sally's UID is 1, and Susie's UID is 2, then pass a 'targetIds' paramters of "1,2". If you pass this parameter, you can use the "{target}" token in your templates.
	 * Probably it also makes it more likely that Sally and Susie will see the feed entry in their newsfeed, relative to any other friends Bob might have. It may be a
	 * good idea to always send a list of all the user's friends, and avoid using the "{target}" token, to maximize distribution of the story through the newsfeed.<br />
	 * <br />
	 * The only strictly required parameter is 'titleTemplate', which must contain the "{actor}" token somewhere inside of it. All other parameters, options, and tokens
	 * are optional, and my be set to null if being omitted.<br />
	 * <br />
	 * Not that stories will only be aggregated if *all* templates match and *all* template parameters match, so if two entries have the same templateTitle and titleData,
	 * but a different bodyTemplate, they will not aggregate. Probably it's better to use bodyGeneral instead of bodyTemplate, for the extra flexibility it provides.<br />
	 * <br />
	 * <br />
	 * Note that this method is replacing 'feed_publishActionOfUser', which has been deprecated by Facebook. For specific details, visit
	 * http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction
	 * 
	 * 
	 * @param action
	 *            a TemplatizedAction instance that represents the feed data to publish
	 * 
	 * @return a Document representing the XML response returned from the Facebook API server.
	 * 
	 * @throws FacebookException
	 *             if any number of bad things happen
	 * @throws IOException
	 */
	public boolean feed_PublishTemplatizedAction( TemplatizedAction action ) throws FacebookException, IOException;

	/**
	 * Lookup a single preference value for the current user.
	 * 
	 * @param prefId
	 *            the id of the preference to lookup. This should be an integer value from 0-200.
	 * 
	 * @return The value of that preference, or null if it is not yet set.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public String data_getUserPreference( Integer prefId ) throws FacebookException, IOException;

	/**
	 * Get a map containing all preference values set for the current user.
	 * 
	 * @return a map of preference values, keyed by preference id. The map will contain all preferences that have been set for the current user. If there are no
	 *         preferences currently set, the map will be empty. The map returned will never be null.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Map<Integer,String> data_getUserPreferences() throws FacebookException, IOException;

	/**
	 * Set a user-preference value. The value can be any string up to 127 characters in length, while the preference id can only be an integer between 0 and 200. Any
	 * preference set applies only to the current user of the application.
	 * 
	 * To clear a user-preference, specify null as the value parameter. The values of "0" and "" will be stored as user-preferences with a literal value of "0" and ""
	 * respectively.
	 * 
	 * @param prefId
	 *            the id of the preference to set, an integer between 0 and 200.
	 * @param value
	 *            the value to store, a String of up to 127 characters in length.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public void data_setUserPreference( Integer prefId, String value ) throws FacebookException, IOException;

	/**
	 * Set multiple user-preferences values. The values can be strings up to 127 characters in length, while the preference id can only be an integer between 0 and 200.
	 * Any preferences set apply only to the current user of the application.
	 * 
	 * To clear a user-preference, specify null as its value in the map. The values of "0" and "" will be stored as user-preferences with a literal value of "0" and ""
	 * respectively.
	 * 
	 * @param values
	 *            the values to store, specified in a map. The keys should be preference-id values from 0-200, and the values should be strings of up to 127 characters in
	 *            length.
	 * @param replace
	 *            set to true if you want to remove any pre-existing preferences before writing the new ones set to false if you want the new preferences to be merged
	 *            with any pre-existing preferences
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public void data_setUserPreferences( Map<Integer,String> values, boolean replace ) throws FacebookException, IOException;

	/**
	 * Check to see if the application is permitted to send SMS messages to the current application user.
	 * 
	 * @return true if the application is presently able to send SMS messages to the current user false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean sms_canSend() throws FacebookException, IOException;

	/**
	 * Check to see if the application is permitted to send SMS messages to the specified user.
	 * 
	 * @param userId
	 *            the UID of the user to check permissions for
	 * 
	 * @return true if the application is presently able to send SMS messages to the specified user false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean sms_canSend( Long userId ) throws FacebookException, IOException;

	/**
	 * Send an SMS message to the current application user.
	 * 
	 * @param message
	 *            the message to send.
	 * @param smsSessionId
	 *            the SMS session id to use, note that that is distinct from the user's facebook session id. It is used to allow applications to keep track of individual
	 *            SMS conversations/threads for a single user. Specify null if you do not want/need to use a session for the current message.
	 * @param makeNewSession
	 *            set to true to request that Facebook allocate a new SMS session id for this message. The allocated id will be returned as the result of this API call.
	 *            You should only set this to true if you are passing a null 'smsSessionId' value. Otherwise you already have a SMS session id, and do not need a new one.
	 * 
	 * @return an integer specifying the value of the session id alocated by Facebook, if one was requested. If a new session id was not requested, this method will
	 *         return null.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Integer sms_send( String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException, IOException;

	/**
	 * Send an SMS message to the specified user.
	 * 
	 * @param userId
	 *            the id of the user to send the message to.
	 * @param message
	 *            the message to send.
	 * @param smsSessionId
	 *            the SMS session id to use, note that that is distinct from the user's facebook session id. It is used to allow applications to keep track of individual
	 *            SMS conversations/threads for a single user. Specify null if you do not want/need to use a session for the current message.
	 * @param makeNewSession
	 *            set to true to request that Facebook allocate a new SMS session id for this message. The allocated id will be returned as the result of this API call.
	 *            You should only set this to true if you are passing a null 'smsSessionId' value. Otherwise you already have a SMS session id, and do not need a new one.
	 * 
	 * @return an integer specifying the value of the session id alocated by Facebook, if one was requested. If a new session id was not requested, this method will
	 *         return null.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Integer sms_send( Long userId, String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException, IOException;

	/**
	 * Check to see if the user has granted the app a specific external permission. In order to be granted a permission, an application must direct the user to a URL of
	 * the form:
	 * 
	 * http://www.facebook.com/authorize.php?api_key=[YOUR_API_KEY]&v=1.0&ext_perm=[PERMISSION NAME]
	 * 
	 * @param perm
	 *            the permission to check for
	 * 
	 * @return true if the user has granted the application the specified permission false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean users_hasAppPermission( Permission perm ) throws FacebookException, IOException;

	/**
	 * Check to see if the user has granted the app a specific external permission. In order to be granted a permission, an application must direct the user to a URL of
	 * the form:
	 * 
	 * http://www.facebook.com/authorize.php?api_key=[YOUR_API_KEY]&v=1.0&ext_perm=[PERMISSION NAME]
	 * 
	 * @param perm
	 *            the permission to check for
	 * @param userId
	 *            the id of the user to check for
	 * 
	 * @return true if the user has granted the application the specified permission false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean users_hasAppPermission( Permission perm, Long userId ) throws FacebookException, IOException;

	/**
	 * Publishes a templatized action for the current user. The action will appear in their minifeed, and may appear in their friends' newsfeeds depending upon a number
	 * of different factors. When a template match exists between multiple distinct users (like "Bob recommends Bizou" and "Sally recommends Bizou"), the feed entries may
	 * be combined in the newfeed (to something like "Bob and sally recommend Bizou"). This happens automatically, and *only* if the template match between the two feed
	 * entries is identical.<br />
	 * <br />
	 * Feed entries are not aggregated for a single user (so "Bob recommends Bizou" and "Bob recommends Le Charm" *will not* become "Bob recommends Bizou and Le Charm").<br />
	 * <br />
	 * If the user's action involves one or more of their friends, list them in the 'targetIds' parameter. For example, if you have "Bob says hi to Sally and Susie", and
	 * Sally's UID is 1, and Susie's UID is 2, then pass a 'targetIds' paramters of "1,2". If you pass this parameter, you can use the "{target}" token in your templates.
	 * Probably it also makes it more likely that Sally and Susie will see the feed entry in their newsfeed, relative to any other friends Bob might have. It may be a
	 * good idea to always send a list of all the user's friends, and avoid using the "{target}" token, to maximize distribution of the story through the newsfeed.<br />
	 * <br />
	 * The only strictly required parameter is 'titleTemplate', which must contain the "{actor}" token somewhere inside of it. All other parameters, options, and tokens
	 * are optional, and my be set to null if being omitted.<br />
	 * <br />
	 * Not that stories will only be aggregated if *all* templates match and *all* template parameters match, so if two entries have the same templateTitle and titleData,
	 * but a different bodyTemplate, they will not aggregate. Probably it's better to use bodyGeneral instead of bodyTemplate, for the extra flexibility it provides.<br />
	 * <br />
	 * <br />
	 * Note that this method is replacing 'feed_publishActionOfUser', which has been deprecated by Facebook. For specific details, visit
	 * http://wiki.developers.facebook.com/index.php/Feed.publishTemplatizedAction
	 * 
	 * 
	 * @param titleTemplate
	 *            the template for the title of the feed entry, this must contain the "(actor}" token. Any other tokens are optional, i.e. "{actor} recommends {place}".
	 * @param titleData
	 *            JSON-formatted values for any tokens used in titleTemplate, with the exception of "{actor}" and "{target}", which Facebook populates automatically, i.e.
	 *            "{place: "<a href='http://www.bizou.com'>Bizou</a>"}".
	 * @param bodyTemplate
	 *            the template for the body of the feed entry, works the same as 'titleTemplate', but is not required to contain the "{actor}" token.
	 * @param bodyData
	 *            works the same as titleData
	 * @param bodyGeneral
	 *            non-templatized content for the body, may contain markup, may not contain tokens.
	 * @param pictures
	 *            a list of up to 4 images to display, with optional hyperlinks for each one.
	 * @param targetIds
	 *            a comma-seperated list of the UID's of any friend(s) who are involved in this feed action (if there are any), this specifies the value of the "{target}"
	 *            token. If you use this token in any of your templates, you must specify a value for this parameter.
	 * 
	 * @return a Document representing the XML response returned from the Facebook API server.
	 * 
	 * @throws FacebookException
	 *             if any number of bad things happen
	 * @throws IOException
	 */
	public boolean feed_publishTemplatizedAction( String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds ) throws FacebookException, IOException;

	/**
	 * Associates the specified FBML markup with the specified handle/id. The markup can then be referenced using the fb:ref FBML tag, to allow a given snippet to be
	 * reused easily across multiple users, and also to allow the application to update the fbml for multiple users more easily without having to make a seperate call for
	 * each user, by just changing the FBML markup that is associated with the handle/id.
	 * 
	 * This method cannot be called by desktop apps.
	 * 
	 * @param handle
	 *            the id to associate the specified markup with. Put this in fb:ref FBML tags to reference your markup.
	 * @param markup
	 *            the FBML markup to store.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean fbml_setRefHandle( String handle, String markup ) throws FacebookException, IOException;

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
	 * @see <a href="http://developers.facebook.com/tools.php?feed"> Developers Resources: Feed Preview Console </a>
	 * 
	 * @deprecated use the version that specified the actorId as a Long instead. UID's *are not ever to be* expressed as Integers.
	 */
	@Deprecated
	public boolean feed_publishTemplatizedAction( Integer actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException, IOException;

	/**
	 * Create a new marketplace listing, or modify an existing one.
	 * 
	 * @param listingId
	 *            the id of the listing to modify, set to 0 (or null) to create a new listing.
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile (Facebook appears to ignore this setting).
	 * @param attributes
	 *            JSON-encoded attributes for this listing.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( Long listingId, boolean showOnProfile, String attributes ) throws FacebookException, IOException;

	/**
	 * Create a new marketplace listing, or modify an existing one.
	 * 
	 * @param listingId
	 *            the id of the listing to modify, set to 0 (or null) to create a new listing.
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
	 * @param listing
	 *            the listing to publish.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( Long listingId, boolean showOnProfile, MarketListing listing ) throws FacebookException, IOException;

	/**
	 * Create a new marketplace listing.
	 * 
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
	 * @param listing
	 *            the listing to publish.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( boolean showOnProfile, MarketListing listing ) throws FacebookException, IOException;

	/**
	 * Create a new marketplace listing, or modify an existing one.
	 * 
	 * @param listingId
	 *            the id of the listing to modify, set to 0 (or null) to create a new listing.
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile (Facebook appears to ignore this setting).
	 * @param attributes
	 *            JSON-encoded attributes for this listing.
	 * @param userId
	 *            the id of the user to create the listing for.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( Long listingId, boolean showOnProfile, String attributes, Long userId ) throws FacebookException, IOException;

	/**
	 * Create a new marketplace listing, or modify an existing one.
	 * 
	 * @param listingId
	 *            the id of the listing to modify, set to 0 (or null) to create a new listing.
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
	 * @param listing
	 *            the listing to publish.
	 * @param userId
	 *            the id of the user to create the listing for.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( Long listingId, boolean showOnProfile, MarketListing listing, Long userId ) throws FacebookException, IOException;

	/**
	 * Create a new marketplace listing.
	 * 
	 * @param showOnProfile
	 *            set to true to show the listing on the user's profile, set to false to prevent the listing from being shown on the profile.
	 * @param listing
	 *            the listing to publish.
	 * @param userId
	 *            the id of the user to create the listing for.
	 * 
	 * @return the id of the listing created (or modified).
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public Long marketplace_createListing( boolean showOnProfile, MarketListing listing, Long userId ) throws FacebookException, IOException;

	/**
	 * Return a list of all valid Marketplace subcategories.
	 * 
	 * @return a list of marketplace subcategories allowed by Facebook.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public List<String> marketplace_getSubCategories() throws FacebookException, IOException;

	/**
	 * Retrieve listings from the marketplace. The listings can be filtered by listing-id or user-id (or both).
	 * 
	 * @param listingIds
	 *            the ids of listings to filter by, only listings matching the specified ids will be returned.
	 * @param uids
	 *            the ids of users to filter by, only listings submitted by those users will be returned.
	 * 
	 * @return A list of marketplace listings that meet the specified filter criteria.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public List<Listing> marketplace_getListings( List<Long> listingIds, List<Long> uids ) throws FacebookException, IOException;

	/**
	 * Search the marketplace listings by category, subcategory, and keyword.
	 * 
	 * @param category
	 *            the category to search in, optional (unless subcategory is specified).
	 * @param subcategory
	 *            the subcategory to search in, optional.
	 * @param searchTerm
	 *            the keyword to search for, optional.
	 * 
	 * @return a list of marketplace entries that match the specified search parameters.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public List<Listing> marketplace_search( MarketListingCategory category, MarketListingSubcategory subcategory, String searchTerm ) throws FacebookException,
			IOException;

	/**
	 * Remove a listing from the marketplace by id.
	 * 
	 * @param listingId
	 *            the id of the listing to remove.
	 * @param status
	 *            the status to apply when removing the listing. Should be one of MarketListingStatus.SUCCESS or MarketListingStatus.NOT_SUCCESS.
	 * 
	 * @return true if the listing was successfully removed false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean marketplace_removeListing( Long listingId, MarketListingStatus status ) throws FacebookException, IOException;

	/**
	 * Remove a listing from the marketplace by id.
	 * 
	 * @param listingId
	 *            the id of the listing to remove.
	 * @param status
	 *            the status to apply when removing the listing. Should be one of MarketListingStatus.SUCCESS or MarketListingStatus.NOT_SUCCESS.
	 * @param userId
	 *            the id of the user removing the listing.
	 * 
	 * @return true if the listing was successfully removed false otherwise
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public boolean marketplace_removeListing( Long listingId, MarketListingStatus status, Long userId ) throws FacebookException, IOException;

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
	public Long marketplace_editListing( Long listingId, Boolean showOnProfile, MarketListing attrs ) throws FacebookException, IOException;

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
	public int sms_sendMessageWithSession( Long userId, CharSequence message ) throws FacebookException, IOException;

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
	public void sms_sendMessage( Long userId, CharSequence message ) throws FacebookException, IOException;

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
	public T pages_getInfo( Collection<Long> pageIds, EnumSet<PageProfileField> fields ) throws FacebookException, IOException;

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
	public T pages_getInfo( Collection<Long> pageIds, Set<CharSequence> fields ) throws FacebookException, IOException;

	/**
	 * Retrieves the requested profile fields for the Facebook Pages of the user with the given <code>userId</code>.
	 * 
	 * @param userId
	 *            the ID of a user about whose pages to fetch info
	 * @param fields
	 *            a set of PageProfileFields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo"> Developers Wiki: Pages.getInfo</a>
	 */
	public T pages_getInfo( Long userId, EnumSet<PageProfileField> fields ) throws FacebookException, IOException;

	/**
	 * Retrieves the requested profile fields for the Facebook Pages of the user with the given <code>userId</code>.
	 * 
	 * @param userId
	 *            the ID of a user about whose pages to fetch info
	 * @param fields
	 *            a set of page profile fields
	 * @return a T consisting of a list of pages, with each page element containing the requested fields.
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.getInfo"> Developers Wiki: Pages.getInfo</a>
	 */
	public T pages_getInfo( Long userId, Set<CharSequence> fields ) throws FacebookException, IOException;

	/**
	 * Checks whether a page has added the application
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the page has added the application
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isAppAdded"> Developers Wiki: Pages.isAppAdded</a>
	 */
	public boolean pages_isAppAdded( Long pageId ) throws FacebookException, IOException;

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
	public boolean pages_isFan( Long pageId, Long userId ) throws FacebookException, IOException;

	/**
	 * Checks whether the logged-in user is a fan of the page with the given <code>pageId</code>.
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the logged-in user is a fan of the page
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isFan"> Developers Wiki: Pages.isFan</a>
	 */
	public boolean pages_isFan( Long pageId ) throws FacebookException, IOException;

	/**
	 * Checks whether the logged-in user for this session is an admin of the page with the given <code>pageId</code>.
	 * 
	 * @param pageId
	 *            the ID of the page
	 * @return true if the logged-in user is an admin
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Pages.isAdmin"> Developers Wiki: Pages.isAdmin</a>
	 */
	public boolean pages_isAdmin( Long pageId ) throws FacebookException, IOException;

	/**
	 * Send an e-mail to the currently logged-in user. The e-mail content can be specified as either plaintext or FBML. In either case, only a limited subset of markup is
	 * supported (only tags that result in text and links are allowed).
	 * 
	 * You must include at least one of either the fbml or email parameters, but you do not ever need to specify both at once (the other can be null, or ""). If you
	 * specify both a text version and a fbml version of your e-mail, the text version will be used.
	 * 
	 * @param subject
	 *            the subject of the email message.
	 * @param email
	 *            a plaintext version of the email to send.
	 * @param fbml
	 *            an FBML version of the email to send, the fbml parameter is a stripped-down set of FBML that allows only tags that result in text, links and linebreaks.
	 * 
	 * @return a list of user-ids specifying which users were successfully emailed.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public T notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException, IOException;

	/**
	 * Send an e-mail to a set of app-users. You can only e-mail users who have already added your application. The e-mail content can be specified as either plaintext or
	 * FBML. In either case, only a limited subset of markup is supported (only tags that result in text and links are allowed).
	 * 
	 * You must include at least one of either the fbml or email parameters, but you do not ever need to specify both at once (the other can be null, or ""). If you
	 * specify both a text version and a fbml version of your e-mail, the text version will be used.
	 * 
	 * @param recipients
	 *            the uid's of the users to send to.
	 * @param subject
	 *            the subject of the email message.
	 * @param email
	 *            a plaintext version of the email to send.
	 * @param fbml
	 *            an FBML version of the email to send, the fbml parameter is a stripped-down set of FBML that allows only tags that result in text, links and linebreaks.
	 * 
	 * @return a list of user-ids specifying which users were successfully emailed.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public T notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException, IOException;

	/**
	 * Send an e-mail to the currently logged-in user. The e-mail must be specified as plaintext, and can contain a limited subset of HTML tags (specifically, only tags
	 * that result in text and links).
	 * 
	 * @param subject
	 *            the subject of the email message.
	 * @param email
	 *            a plaintext version of the email to send.
	 * 
	 * @return a list of user-ids specifying which users were successfully emailed.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public T notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException, IOException;

	/**
	 * Send an e-mail to a set of app-users. You can only e-mail users who have already added your application. The e-mail content can be specified as either plaintext or
	 * FBML. In either case, only a limited subset of markup is supported (only tags that result in text and links are allowed).
	 * 
	 * @param recipients
	 *            the uid's of the users to send to.
	 * @param subject
	 *            the subject of the email message.
	 * @param email
	 *            a plaintext version of the email to send.
	 * 
	 * @return a list of user-ids specifying which users were successfully emailed.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public T notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException, IOException;

	/**
	 * Send an e-mail to the currently logged-in user. The e-mail must be specified as fbml, and can contain a limited subset of FBML tags (specifically, only tags that
	 * result in text and links).
	 * 
	 * @param subject
	 *            the subject of the email message.
	 * @param fbml
	 *            the FBML version of the email to send, the fbml parameter is a stripped-down set of FBML that allows only tags that result in text, links and
	 *            linebreaks.
	 * 
	 * @return a list of user-ids specifying which users were successfully emailed.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public T notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException, IOException;

	/**
	 * Send an e-mail to a set of app-users. You can only e-mail users who have already added your application. The e-mail content can be specified as either plaintext or
	 * FBML. In either case, only a limited subset of markup is supported (only tags that result in text and links are allowed).
	 * 
	 * @param recipients
	 *            the uid's of the users to send to.
	 * @param subject
	 *            the subject of the email message.
	 * @param fbml
	 *            the FBML version of the email to send, the fbml parameter is a stripped-down set of FBML that allows only tags that result in text, links and
	 *            linebreaks.
	 * 
	 * @return a list of user-ids specifying which users were successfully emailed.
	 * 
	 * @throws FacebookException
	 *             if an error happens when executing the API call.
	 * @throws IOException
	 *             if a communication/network error happens.
	 */
	public T notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException, IOException;

	/**
	 * Send a notification message to the logged-in user.
	 * 
	 * @param notification
	 *            the FBML to be displayed on the notifications page; only a stripped-down set of FBML tags that result in text and links is allowed
	 * @return a URL, possibly null, to which the user should be redirected to finalize the sending of the email
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.send"> Developers Wiki: notifications.send</a>
	 */
	public void notifications_send( CharSequence notification ) throws FacebookException, IOException;

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
	public String notifications_sendEmail( Collection<Long> recipientIds, CharSequence subject, CharSequence fbml ) throws FacebookException, IOException;

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
	public String notifications_sendEmailPlain( Collection<Long> recipientIds, CharSequence subject, CharSequence text ) throws FacebookException, IOException;

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
	 * @see <a href="http://wiki.developers.facebook.com/index.php/Notifications.sendEmail"> Developers Wiki: notifications.sendEmail</a>
	 * 
	 * @deprecated provided for legacy support only, please use one of the alternate notifications_sendEmail calls.
	 */
	@Deprecated
	public String notifications_sendEmailStr( Collection<Long> recipientIds, CharSequence subject, CharSequence fbml, CharSequence text ) throws FacebookException,
			IOException;

	/**
	 * Set application properties. The properties are used by Facebook to describe the configuration of your application.
	 * 
	 * This method cannot be called by desktop apps.
	 * 
	 * @param properties
	 *            a Map containing the properties to set.
	 * 
	 * @return true if the properties are set successfully false otherwise
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean admin_setAppProperties( Map<ApplicationProperty,String> properties ) throws FacebookException, IOException;

	/**
	 * Retrieve application properties. The properties are used by Facebook to describe the configuration of your application.
	 * 
	 * This method cannot be called by desktop apps.
	 * 
	 * @param properties
	 *            a collection indicating the properties you are interested in retrieving.
	 * 
	 * @return a JSONObject that maps ApplicationProperty names to their corresponding values.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 * 
	 * @deprecated use admin_getAppPropertiesMap() instead
	 */
	@Deprecated
	public JSONObject admin_getAppProperties( Collection<ApplicationProperty> properties ) throws FacebookException, IOException;

	/**
	 * Retrieve application properties. The properties are used by Facebook to describe the configuration of your application.
	 * 
	 * This method cannot be called by desktop apps.
	 * 
	 * @param properties
	 *            a collection indicating the properties you are interested in retrieving.
	 * 
	 * @return a mapping of ApplicationProperty's to the corresponding values that are set for those properties. Properties are represented as strings, so properties that
	 *         are of boolean type will have a value of "true" when true, and "false" when false. The properties returned will never be null, an unset property is
	 *         represented by an empty string.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public Map<ApplicationProperty,String> admin_getAppPropertiesMap( Collection<ApplicationProperty> properties ) throws FacebookException, IOException;

	/**
	 * Retrieve application properties. The properties are used by Facebook to describe the configuration of your application.
	 * 
	 * This method cannot be called by desktop apps.
	 * 
	 * @param properties
	 *            a collection indicating the properties you are interested in retrieving.
	 * 
	 * @return a JSON-encoded string containing the properties. It is your responsibility to parse the string. Details can be found at
	 *         http://wiki.developers.facebook.com/index.php/Admin.getAppProperties
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public String admin_getAppPropertiesAsString( Collection<ApplicationProperty> properties ) throws FacebookException, IOException;

	/**
	 * Get all cookies for the currently logged-in user.
	 * 
	 * @return all cookies for the current user.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public T data_getCookies() throws FacebookException, IOException;

	/**
	 * Get all cookies for the specified user.
	 * 
	 * @param userId
	 *            the id of the user to get the cookies for.
	 * 
	 * @return all cookies for the specified user.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public T data_getCookies( Long userId ) throws FacebookException, IOException;

	/**
	 * Get a specific cookie for the currently logged-in user.
	 * 
	 * @param name
	 *            the name of the cookie to retrieve.
	 * 
	 * @return the specified cookie for the current user.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public T data_getCookies( String name ) throws FacebookException, IOException;

	/**
	 * Get a specific cookie for the specified user.
	 * 
	 * @param userId
	 *            the id of the user to get the cookies for.
	 * @param name
	 *            the name of the cookie to retrieve.
	 * 
	 * @return the specified cookie for the specified user.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public T data_getCookies( Long userId, CharSequence name ) throws FacebookException, IOException;

	/**
	 * Set a cookie for the current user. It will use the default expiry (never), and the default path ("/").
	 * 
	 * @param name
	 *            the name of the cookie to set
	 * @param value
	 *            the value of the cookie
	 * 
	 * @return true if the cookie is set successfully, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean data_setCookie( String name, String value ) throws FacebookException, IOException;

	/**
	 * Set a cookie for the current user, under the specified path. It will use the default expiry (never).
	 * 
	 * @param name
	 *            the name of the cookie to set
	 * @param value
	 *            the value of the cookie
	 * @param path
	 *            the path relative to the application's callback URL, with which the cookie should be associated. (default is "/")
	 * 
	 * @return true if the cookie is set successfully, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean data_setCookie( String name, String value, String path ) throws FacebookException, IOException;

	/**
	 * Set a cookie for the specified user. The cookie will use the default expiry (never) and the default path ("/").
	 * 
	 * @param userId
	 *            the id of the user to set the cookie for.
	 * @param name
	 *            the name of the cookie to set
	 * @param value
	 *            the value of the cookie
	 * 
	 * @return true if the cookie is set successfully, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value ) throws FacebookException, IOException;

	/**
	 * Set a cookie for the specified user, with the specified path. The cookie will use the default expiry (never).
	 * 
	 * @param userId
	 *            the id of the user to set the cookie for.
	 * @param name
	 *            the name of the cookie to set
	 * @param value
	 *            the value of the cookie
	 * @param path
	 *            the path relative to the application's callback URL, with which the cookie should be associated. (default is "/")
	 * 
	 * @return true if the cookie is set successfully, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, CharSequence path ) throws FacebookException, IOException;

	/**
	 * Set a cookie for the current user, with the specified expiration date. It will use the default path ("/").
	 * 
	 * @param name
	 *            the name of the cookie to set
	 * @param value
	 *            the value of the cookie
	 * @param expires
	 *            the timestamp at which the cookie expires
	 * 
	 * @return true if the cookie is set successfully, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean data_setCookie( String name, String value, Long expires ) throws FacebookException, IOException;

	/**
	 * Set a cookie for the current user, with the specified expiration date and path.
	 * 
	 * @param name
	 *            the name of the cookie to set
	 * @param value
	 *            the value of the cookie
	 * @param expires
	 *            the timestamp at which the cookie expires
	 * @param path
	 *            the path relative to the application's callback URL, with which the cookie should be associated. (default is "/")
	 * 
	 * @return true if the cookie is set successfully, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean data_setCookie( String name, String value, Long expires, String path ) throws FacebookException, IOException;

	/**
	 * Set a cookie for the specified user, with the specified expiration date. The cookie will use the default path ("/").
	 * 
	 * @param userId
	 *            the id of the user to set the cookie for.
	 * @param name
	 *            the name of the cookie to set
	 * @param value
	 *            the value of the cookie
	 * @param expires
	 *            the timestamp at which the cookie expires
	 * 
	 * @return true if the cookie is set successfully, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires ) throws FacebookException, IOException;

	/**
	 * Set a cookie for the specified user, with the specified expiration date and path.
	 * 
	 * @param userId
	 *            the id of the user to set the cookie for.
	 * @param name
	 *            the name of the cookie to set
	 * @param value
	 *            the value of the cookie
	 * @param expires
	 *            the timestamp at which the cookie expires
	 * @param path
	 *            the path relative to the application's callback URL, with which the cookie should be associated. (default is "/")
	 * 
	 * @return true if the cookie is set successfully, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires, CharSequence path ) throws FacebookException, IOException;

	/**
	 * Sets several property values for an application. The properties available are analogous to the ones editable via the Facebook Developer application. A session is
	 * not required to use this method.
	 * 
	 * This method cannot be called by desktop apps.
	 * 
	 * @param properties
	 *            an ApplicationPropertySet that is translated into a single JSON String.
	 * @return a boolean indicating whether the properties were successfully set
	 * @see http://wiki.developers.facebook.com/index.php/Admin.setAppProperties
	 */
	public boolean admin_setAppProperties( ApplicationPropertySet properties ) throws FacebookException, IOException;

	/**
	 * Gets property values previously set for an application on either the Facebook Developer application or the with the <code>admin.setAppProperties</code> call. A
	 * session is not required to use this method.
	 * 
	 * This method cannot be called by desktop apps.
	 * 
	 * @param properties
	 *            an enumeration of the properties to get
	 * @return an ApplicationPropertySet
	 * @see ApplicationProperty
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getAppProperties
	 */
	public ApplicationPropertySet admin_getAppPropertiesAsSet( EnumSet<ApplicationProperty> properties ) throws FacebookException, IOException;

	/**
	 * Starts a batch of queries. Any API calls made after invoking 'beginBatch' will be deferred until the next time you call 'executeBatch', at which time they will be
	 * processed as a batch query. All API calls made in the interim will return null as their result.
	 */
	public void beginBatch();

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
	public T batch_run( String methods, boolean serial ) throws FacebookException, IOException;

	/**
	 * Executes a batch of queries. You define the queries to execute by calling 'beginBatch' and then invoking the desired API methods that you want to execute as part
	 * of your batch as normal. Invoking this method will then execute the API calls you made in the interim as a single batch query.
	 * 
	 * @param serial
	 *            set to true, and your batch queries will always execute serially, in the same order in which your specified them. If set to false, the Facebook API
	 *            server may execute your queries in parallel and/or out of order in order to improve performance.
	 * 
	 * @return a list containing the results of the batch execution. The list will be ordered such that the first element corresponds to the result of the first query in
	 *         the batch, and the second element corresponds to the result of the second query, and so on. The types of the objects in the list will match the type
	 *         normally returned by the API call being invoked (so calling users_getLoggedInUser as part of a batch will place a Long in the list, and calling friends_get
	 *         will place a Document in the list, etc.).
	 * 
	 * The list may be empty, it will never be null.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public List<? extends Object> executeBatch( boolean serial ) throws FacebookException, IOException;

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
	 * @see http://wiki.developers.facebook.com/index.php/Application.getPublicInfo
	 */
	public T application_getPublicInfo( Long applicationId, String applicationKey, String applicationCanvas ) throws FacebookException, IOException;

	/**
	 * Gets the public information about the specified application, by application id.
	 * 
	 * @param applicationId
	 *            the id of the application to get the info for.
	 * 
	 * @return the public information for the specified application
	 * @see http://wiki.developers.facebook.com/index.php/Application.getPublicInfo
	 */
	public T application_getPublicInfoById( Long applicationId ) throws FacebookException, IOException;

	/**
	 * Gets the public information about the specified application, by API key.
	 * 
	 * @param applicationKey
	 *            the public API key of the application to get the info for.
	 * 
	 * @return the public information for the specified application
	 * @see http://wiki.developers.facebook.com/index.php/Application.getPublicInfo
	 */
	public T application_getPublicInfoByApiKey( String applicationKey ) throws FacebookException, IOException;

	/**
	 * Gets the public information about the specified application, by canvas-page name.
	 * 
	 * @param applicationCanvas
	 *            the canvas-page name of the application to get the info for.
	 * 
	 * @return the public information for the specified application
	 * @see http://wiki.developers.facebook.com/index.php/Application.getPublicInfo
	 */
	public T application_getPublicInfoByCanvasName( String applicationCanvas ) throws FacebookException, IOException;

	/**
	 * Get your application's current allocation of the specified type of request (i.e. the number of requests that it is currently allowed to send per user per day).
	 * 
	 * @param allocationType
	 *            the type of request to check the allocation for. Currently: "notifications_per_day" and "requests_per_day", "emails_per_day",
	 *            "email_disable_message_location"
	 * 
	 * @return the number of the specified type of requests that the application is permitted to send per user per day.
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getAllocation
	 */
	public int admin_getAllocation( String allocationType ) throws FacebookException, IOException;

	/**
	 * Get your application's current allocation of the specified type of request (i.e. the number of requests that it is currently allowed to send per user per day).
	 * 
	 * @param allocationType
	 *            the type of request to check the allocation for. Currently: "notifications_per_day" and "requests_per_day", "emails_per_day",
	 *            "email_disable_message_location"
	 * 
	 * @return the number of the specified type of requests that the application is permitted to send per user per day.
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getAllocation
	 */
	public int admin_getAllocation( AllocationType allocationType ) throws FacebookException, IOException;

	/**
	 * Get your application's current allocation for invites/requests (i.e. the total number of invites/requests that it is allowed to send per user, per day).
	 * 
	 * @return the number of invites/requests that the application is permitted to send per user per day.
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getAllocation
	 */
	@Deprecated
	public int admin_getRequestAllocation() throws FacebookException, IOException;

	/**
	 * Get your application's current allocation for notifications (i.e. the total number of notifications that it is allowed to send per user, per day).
	 * 
	 * @return the number of notifications that the application is permitted to send per user per day.
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getAllocation
	 */
	@Deprecated
	public int admin_getNotificationAllocation() throws FacebookException, IOException;

	/**
	 * Retrieve the daily metrics for the current application.
	 * 
	 * @param metrics
	 *            a set specifying the specific metrics to retrieve
	 * @param start
	 *            the starting date to retrieve data for (range must not exceed 30 days)
	 * @param end
	 *            the ending to to retrive data for (range must not exceed 30 days)
	 * 
	 * @return daily metrics for your app, for each day in the specified range
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getDailyMetrics
	 */
	@Deprecated
	public T admin_getDailyMetrics( Set<Metric> metrics, Date start, Date end ) throws FacebookException, IOException;

	/**
	 * Retrieve metrics for the current application.
	 * 
	 * @param metrics
	 *            a set specifying the specific metrics to retrieve
	 * @param start
	 *            the starting date to retrieve data for (range must not exceed 30 days)
	 * @param end
	 *            the ending to to retrive data for (range must not exceed 30 days)
	 * @param period
	 *            a number specifying the desired period to group the metrics by, in seconds, Facebook currently only supports Metric.PERIOD_DAY, Metric.PERIOD_WEEK, and
	 *            Metric.PERIOD_MONTH
	 * 
	 * @return daily metrics for your app, for each day in the specified range
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getMetrics
	 */
	public T admin_getMetrics( Set<Metric> metrics, Date start, Date end, long period ) throws FacebookException, IOException;

	/**
	 * Retrieve the daily metrics for the current application.
	 * 
	 * @param metrics
	 *            a set specifying the specific metrics to retrieve
	 * @param start
	 *            the starting date to retrieve data for (range must not exceed 30 days), the accepted unit of time is milliseconds, NOT seconds
	 * @param end
	 *            the ending to to retrive data for (range must not exceed 30 days), the accepted unit of time is milliseconds, NOT seconds
	 * 
	 * @return daily metrics for your app, for each day in the specified range
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getDailyMetrics
	 */
	@Deprecated
	public T admin_getDailyMetrics( Set<Metric> metrics, long start, long end ) throws FacebookException, IOException;

	/**
	 * Retrieve the daily metrics for the current application.
	 * 
	 * @param metrics
	 *            a set specifying the specific metrics to retrieve
	 * @param start
	 *            the starting date to retrieve data for (range must not exceed 30 days), the accepted unit of time is milliseconds, NOT seconds
	 * @param end
	 *            the ending to to retrive data for (range must not exceed 30 days), the accepted unit of time is milliseconds, NOT seconds
	 * @param period
	 *            a number specifying the desired period to group the metrics by, in seconds, Facebook currently only supports Metric.PERIOD_DAY, Metric.PERIOD_WEEK, and
	 *            Metric.PERIOD_MONTH
	 * 
	 * @return daily metrics for your app, for each day in the specified range
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 * @see http://wiki.developers.facebook.com/index.php/Admin.getMetrics
	 */
	public T admin_getMetrics( Set<Metric> metrics, long start, long end, long period ) throws FacebookException, IOException;

	/**
	 * Grant permission to an external app to make API calls on behalf of the current application.
	 * 
	 * @param apiKey
	 *            the API-key of the application to grant permission to.
	 * @param methods
	 *            the API methods to allow the other application to call. If the set is empty or null, permission is granted for all API methods.
	 * 
	 * @return true if the operation succeeds false otherwise
	 */
	public boolean permissions_grantApiAccess( String apiKey, Set<FacebookMethod> methods ) throws FacebookException, IOException;

	/**
	 * Grant permission to an external app to make API calls on behalf of the current application. Access is granted to the full set of allowed API methods.
	 * 
	 * @param apiKey
	 *            the API-key of the application to grant permission to.
	 * 
	 * @return true if the operation succeeds false otherwise
	 */
	public boolean permissions_grantFullApiAccess( String apiKey ) throws FacebookException, IOException;

	/**
	 * Check to see what permissions have been granted to current app by the specified external application.
	 * 
	 * For example:
	 * 
	 * Application A grants permission on users.getInfo to Application B, Applicatio B can then call permissions_checkAvailableApiAccess(A) and will recieve
	 * "users.getInfo" as a result.
	 * 
	 * @param apiKey
	 *            the API key of the application to check for permissions from.
	 * 
	 * @return a list of all API methods that the specified application has permission to use.
	 */
	public T permissions_checkAvailableApiAccess( String apiKey ) throws FacebookException, IOException;

	/**
	 * Revokes the specified application's permission to call API methods on behalf of the current app.
	 * 
	 * @param apiKey
	 *            the API key of the application to remove permissions for.
	 * 
	 * @return true if the operation succeeds false otherwise
	 */
	public boolean permissions_revokeApiAccess( String apiKey ) throws FacebookException, IOException;

	/**
	 * Check to see what permissions have been granted to specified external application by the current application.
	 * 
	 * For example:
	 * 
	 * Application A grants permission on users.getInfo to Application B, Applicatio A can then call permissions_checkGrantedApiAccess(B) and will recieve "users.getInfo"
	 * as a result.
	 * 
	 * @param apiKey
	 *            the API key of the application to check permissions for.
	 * 
	 * @return a list of all API methods that the specified application has permission to use.
	 */
	public T permissions_checkGrantedApiAccess( String apiKey ) throws FacebookException, IOException;

	/**
	 * Expires the curently active session.
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean auth_expireSession() throws FacebookException, IOException;

	/**
	 * If this method is called for the logged in user, then no further API calls can be made on that user's behalf until the user decides to authorize the application
	 * again.
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean auth_revokeAuthorization() throws FacebookException, IOException;

	/**
	 * Begins permissions mode, and allows the current application to begin making requests on behalf of the application associated with the specified API key.
	 * 
	 * This method must be invoked prior to making an API request on behalf of another application. When you are done, be sure to call endPermissionsMode().
	 * 
	 * @param apiKey
	 *            the API key of the application to being making requests for.
	 */
	public void beginPermissionsMode( String apiKey );

	/**
	 * Terminates permissions mode. After calling this, the current application will be unable to make requests on behalf of another app, until beginPermissionsMode is
	 * called again.
	 */
	public void endPermissionsMode();

	/**
	 * Get the JAXB context that is being used by the client.
	 * 
	 * @return the JAXB context object.
	 */
	public JAXBContext getJaxbContext();

	/**
	 * Set the JAXB context that the client will use.
	 * 
	 * @param context
	 *            the context to use.
	 */
	public void setJaxbContext( JAXBContext context );

	/**
	 * Generate a key for the current session that can be used to authenticate client-side components.
	 * 
	 * @return the key.
	 */
	public String auth_promoteSession() throws FacebookException, IOException;

	/**
	 * Registers a feed template.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Feed.registerTemplateBundle
	 * 
	 * @param template
	 *            the template to store
	 * 
	 * @return the id which Facebook assigns to your template
	 */
	public Long feed_registerTemplateBundle( String template ) throws FacebookException, IOException;

	/**
	 * Registers a feed template.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Feed.registerTemplateBundle
	 * 
	 * @param templates
	 *            the templates to store
	 * 
	 * @return the id which Facebook assigns to your template
	 */
	public Long feed_registerTemplateBundle( Collection<String> templates ) throws FacebookException, IOException;

	/**
	 * Registers a feed template.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Feed.registerTemplateBundle
	 * 
	 * @param template
	 *            the template to store.
	 * @param shortTemplate
	 *            the short template to store.
	 * @param longTemplate
	 *            the long template to store.
	 * 
	 * @return the id which Facebook assigns to your template
	 * 
	 * @deprecated Facebook has greatly modified the syntax required for the 'shortTemplate' and 'longTemplate' parameters. As such this method will now ignore those
	 *             parameters. You are encouraged to use one of the alternate versions.
	 */
	@Deprecated
	public Long feed_registerTemplateBundle( String template, String shortTemplate, String longTemplate ) throws FacebookException, IOException;

	/**
	 * Registers a feed template.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Feed.registerTemplateBundle
	 * 
	 * @param template
	 *            the template to store.
	 * @param shortTemplate
	 *            the short template to store.
	 * @param longTemplate
	 *            the long template to store.
	 * 
	 * @return the id which Facebook assigns to your template
	 */
	public Long feed_registerTemplateBundle( Collection<String> templates, Collection<BundleStoryTemplate> shortTemplates, BundleStoryTemplate longTemplate )
			throws FacebookException, IOException;

	/**
	 * Get a list of all registered template bundles for your application.
	 * 
	 * @return a list describing all registered feed templates.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public T feed_getRegisteredTemplateBundles() throws FacebookException, IOException;

	/**
	 * Retrieve a template bundle by id.
	 * 
	 * @param id
	 *            the id to retrieve.
	 * 
	 * @return the specified template bundle definition.
	 * @throws FacebookException
	 * @throws IOException
	 */
	public T feed_getRegisteredTemplateBundleByID( Long id ) throws FacebookException, IOException;

	/**
	 * Publishes a user action to the feed.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Feed.publishUserAction
	 * 
	 * @param bundleId
	 *            the template bundle-id to use to render the feed.
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public Boolean feed_publishUserAction( Long bundleId ) throws FacebookException, IOException;

	/**
	 * Publishes a user action to the feed.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Feed.publishUserAction
	 * 
	 * @param bundleId
	 *            the template bundle-id to use to render the feed.
	 * @param templateData
	 *            a map of name-value pairs to substitute into the template being rendered.
	 * @param targetIds
	 *            the ids of individuals that are the target of this action.
	 * @param bodyGeneral
	 *            additional markup to include in the feed story.
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<Long> targetIds, String bodyGeneral ) throws FacebookException,
			IOException;

	/**
	 * Get the specified user's application-info section.
	 * 
	 * @param userId
	 *            the id of the user to get the info section for.
	 * 
	 * @return the user's application-info section.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public T profile_getInfo( Long userId ) throws FacebookException, IOException;

	/**
	 * Get the options associated with the specified field for an application info section.
	 * 
	 * @param field
	 *            the field to get the options for.
	 * 
	 * @return the options associated with the specified field for an application info section.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public T profile_getInfoOptions( String field ) throws FacebookException, IOException;

	/**
	 * Configures an application info section that the specified user can install on the Info tab of her profile.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Profile.setInfo
	 * 
	 * @param userId
	 *            the user to set the info section for.
	 * @param title
	 *            the title to use for the section.
	 * @param textOnly
	 *            set to true if your info fields are text only. set to false for thumbnail mode.
	 * @param fields
	 *            the fields to set.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public void profile_setInfo( Long userId, String title, boolean textOnly, List<ProfileInfoField> fields ) throws FacebookException, IOException;

	/**
	 * Specifies the objects for a field for an application info section. These options populate the typeahead for a thumbnail.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Profile.setInfoOptions
	 * 
	 * @param field
	 *            the field to set.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public void profile_setInfoOptions( ProfileInfoField field ) throws FacebookException, IOException;

	/**
	 * Adds several tags to a photo.
	 * 
	 * @param photoId
	 *            The photo id of the photo to be tagged.
	 * @param tags
	 *            A list of PhotoTags.
	 * @param userId
	 *            the id of the user adding the tags.
	 * 
	 * @return a list of booleans indicating whether the tag was successfully added.
	 */
	public T photos_addTags( Long photoId, Collection<PhotoTag> tags, Long userId ) throws FacebookException, IOException;

	/**
	 * Override the default Facebook API server used for making requests. Can be used to tell the client to run against the
	 * 
	 * @param newUrl
	 *            the new URL to use, for example: "http://api.new.facebook.com/restserver.php"
	 * @throws MalformedURLException
	 */
	public void setServerUrl( String newUrl );

	public URL getDefaultServerUrl();

	public void setDefaultServerUrl( URL url );

	/**
	 * Sends a message using the LiveMessage API. Note that for the message to be recieved by the recipent, you must set up a FBJS handler function. See
	 * http://wiki.developers.facebook.com/index.php/LiveMessage for details.
	 * 
	 * @param recipient
	 *            the id of the user to send the message to.
	 * @param eventName
	 *            the name associated with the FBJS handler you want to recieve your message.
	 * @param message
	 *            the JSON-object to send, the object will be passed to the FBJS handler that you have mapped to 'eventName'. See
	 *            http://wiki.developers.facebook.com/index.php/LiveMessage for details.
	 * 
	 * @return true if the message is sent, false otherwise
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public Boolean liveMessage_send( Long recipient, String eventName, JSONObject message ) throws FacebookException, IOException;

	/**
	 * Sends a notification.
	 * 
	 * @param recipientIds
	 *            the ids of the users to send the notification to.
	 * @param notification
	 *            the notification to send.
	 * @param announcement
	 *            set to 'true' to send an "announcement" notification, otherwise set to false to send a "general" notification.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 * @see http://wiki.developers.facebook.com/index.php/Notifications.send
	 */
	public void notifications_send( Collection<Long> recipientIds, String notification, boolean isAppToUser ) throws FacebookException, IOException;

	/**
	 * Deactivates the specified template bundle.
	 * 
	 * @param bundleId
	 *            the id of the bundle to deactivate.
	 * 
	 * @return true if the call succeeds, false otherwise.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public boolean feed_deactivateTemplateBundleByID( Long bundleId ) throws FacebookException, IOException;

	/**
	 * Publishes a user action to the feed.
	 * 
	 * See: http://wiki.developers.facebook.com/index.php/Feed.publishUserAction
	 * 
	 * @param bundleId
	 *            the template bundle-id to use to render the feed.
	 * @param templateData
	 *            a map of name-value pairs to substitute into the template being rendered.
	 * @param images
	 *            the images to associate with this feed entry
	 * @param targetIds
	 *            the ids of individuals that are the target of this action.
	 * @param bodyGeneral
	 *            additional markup to include in the feed story.
	 * 
	 * @return true if the call succeeds false otherwise
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<IFeedImage> images, List<Long> targetIds, String bodyGeneral )
			throws FacebookException, IOException;

}
