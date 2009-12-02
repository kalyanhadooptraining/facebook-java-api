package com.google.code.facebookapi;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

/**
 * Eventually want this to have no methods in it whatsoever. This base adapter covers the cases where we want to do a simple proxy to the ExtensibleClient because the
 * return type on the ExtensibleClient is not Object or void.
 */
public abstract class SpecificReturnTypeAdapter<T> extends BaseAdapter implements IFacebookRestClient<T> {

	protected SpecificReturnTypeAdapter( String responseFormat, ExtensibleClient client ) {
		super( responseFormat, client );
	}

	public int admin_getAllocation( String allocationType ) throws FacebookException {
		return getClient().admin_getAllocation( allocationType );
	}

	public int admin_getAllocation( AllocationType allocationType ) throws FacebookException {
		return getClient().admin_getAllocation( allocationType );
	}

	public int admin_getAllocation( String allocationType, Long userId ) throws FacebookException {
		return getClient().admin_getAllocation( allocationType, userId );
	}

	public int admin_getAllocation( AllocationType allocationType, Long userId ) throws FacebookException {
		return getClient().admin_getAllocation( allocationType, userId );
	}

	public String admin_getAppPropertiesAsString( Collection<ApplicationProperty> properties ) throws FacebookException {
		return getClient().admin_getAppPropertiesAsString( properties );
	}

	@Deprecated
	public JSONObject admin_getAppProperties( Collection<ApplicationProperty> properties ) throws FacebookException {
		return getClient().admin_getAppProperties( properties );
	}

	public ApplicationPropertySet admin_getAppPropertiesAsSet( Collection<ApplicationProperty> properties ) throws FacebookException {
		return getClient().admin_getAppPropertiesAsSet( properties );
	}

	public Map<ApplicationProperty,String> admin_getAppPropertiesMap( Collection<ApplicationProperty> properties ) throws FacebookException {
		return getClient().admin_getAppPropertiesMap( properties );
	}

	@Deprecated
	public int admin_getNotificationAllocation() throws FacebookException {
		return getClient().admin_getNotificationAllocation();
	}

	@Deprecated
	public int admin_getRequestAllocation() throws FacebookException {
		return getClient().admin_getRequestAllocation();
	}

	public boolean admin_setAppProperties( Map<ApplicationProperty,String> properties ) throws FacebookException {
		return getClient().admin_setAppProperties( properties );
	}

	public boolean admin_setAppProperties( ApplicationPropertySet properties ) throws FacebookException {
		return getClient().admin_setAppProperties( properties );
	}

	public String auth_createToken() throws FacebookException {
		return getClient().auth_createToken();
	}

	public boolean auth_expireSession() throws FacebookException {
		return getClient().auth_expireSession();
	}

	public String auth_getSession( String authToken ) throws FacebookException {
		return getClient().auth_getSession( authToken );
	}

	public String auth_getSession( String authToken, boolean generateSessionSecret ) throws FacebookException {
		return getClient().auth_getSession( authToken, generateSessionSecret );
	}

	public String auth_promoteSession() throws FacebookException {
		return getClient().auth_promoteSession();
	}

	public boolean auth_revokeAuthorization() throws FacebookException {
		return getClient().auth_revokeAuthorization();
	}

	public boolean auth_revokeExtendedPermission( Permission perm ) throws FacebookException {
		return getClient().auth_revokeExtendedPermission( perm );
	}

	public boolean auth_revokeExtendedPermission( Permission perm, Long userId ) throws FacebookException {
		return getClient().auth_revokeExtendedPermission( perm, userId );
	}

	public int connect_getUnconnectedFriendsCount() throws FacebookException {
		return getClient().connect_getUnconnectedFriendsCount();
	}

	public long data_createObject( String objectType, Map<String,String> properties ) throws FacebookException {
		return getClient().data_createObject( objectType, properties );
	}

	public long data_getAssociatedObjectCount( String associationName, long objectId ) throws FacebookException {
		return getClient().data_getAssociatedObjectCount( associationName, objectId );
	}

	public String data_getUserPreference( int prefId ) throws FacebookException {
		return getClient().data_getUserPreference( prefId );
	}

	public boolean data_setCookie( String name, String value ) throws FacebookException {
		return getClient().data_setCookie( name, value );
	}

	public boolean data_setCookie( String name, String value, String path ) throws FacebookException {
		return getClient().data_setCookie( name, value, path );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value ) throws FacebookException {
		return getClient().data_setCookie( userId, name, value );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, CharSequence path ) throws FacebookException {
		return getClient().data_setCookie( userId, name, value, path );
	}

	public boolean data_setCookie( String name, String value, Long expires ) throws FacebookException {
		return getClient().data_setCookie( name, value, expires );
	}

	public boolean data_setCookie( String name, String value, Long expires, String path ) throws FacebookException {
		return getClient().data_setCookie( name, value, expires, path );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires ) throws FacebookException {
		return getClient().data_setCookie( userId, name, value, expires );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires, CharSequence path ) throws FacebookException {
		return getClient().data_setCookie( userId, name, value, expires, path );
	}

	public boolean events_cancel( Long eid, String cancel_message ) throws FacebookException {
		return getClient().events_cancel( eid, cancel_message );
	}

	public Long events_create( Map<String,String> event_info ) throws FacebookException {
		return getClient().events_create( event_info );
	}

	public boolean events_edit( Long eid, Map<String,String> event_info ) throws FacebookException {
		return getClient().events_edit( eid, event_info );
	}

	public boolean events_rsvp( Long eid, String rsvp_status ) throws FacebookException {
		return getClient().events_rsvp( eid, rsvp_status );
	}

	public boolean fbml_refreshImgSrc( String imageUrl ) throws FacebookException {
		return getClient().fbml_refreshImgSrc( imageUrl );
	}

	public boolean fbml_refreshImgSrc( URL imageUrl ) throws FacebookException {
		return getClient().fbml_refreshImgSrc( imageUrl );
	}

	public boolean fbml_refreshRefUrl( String url ) throws FacebookException {
		return getClient().fbml_refreshRefUrl( url );
	}

	public boolean fbml_refreshRefUrl( URL url ) throws FacebookException {
		return getClient().fbml_refreshRefUrl( url );
	}

	public boolean fbml_setRefHandle( String handle, String markup ) throws FacebookException {
		return getClient().fbml_setRefHandle( handle, markup );
	}

	@Deprecated
	public boolean feed_PublishTemplatizedAction( TemplatizedAction action ) throws FacebookException {
		return getClient().feed_PublishTemplatizedAction( action );
	}

	@Deprecated
	public boolean feed_deactivateTemplateBundleByID( Long bundleId ) throws FacebookException {
		return getClient().feed_deactivateTemplateBundleByID( bundleId );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate ) throws FacebookException {
		return getClient().feed_publishTemplatizedAction( actorId, titleTemplate );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate ) throws FacebookException {
		return getClient().feed_publishTemplatizedAction( titleTemplate );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Long pageActorId ) throws FacebookException {
		return getClient().feed_publishTemplatizedAction( titleTemplate, pageActorId );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images,
			Long pageActorId ) throws FacebookException {
		return getClient().feed_publishTemplatizedAction( titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images, pageActorId );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException {
		return getClient().feed_publishTemplatizedAction( actorId, titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds ) throws FacebookException {
		return getClient().feed_publishTemplatizedAction( titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, pictures, targetIds );
	}

	@Deprecated
	public boolean feed_publishTemplatizedAction( Integer actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException {
		return getClient().feed_publishTemplatizedAction( actorId, titleTemplate, titleData, bodyTemplate, bodyData, bodyGeneral, targetIds, images );
	}

	@Deprecated
	public Boolean feed_publishUserAction( Long bundleId ) throws FacebookException {
		return getClient().feed_publishUserAction( bundleId );
	}

	@Deprecated
	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<Long> targetIds, String bodyGeneral ) throws FacebookException {
		return getClient().feed_publishUserAction( bundleId, templateData, targetIds, bodyGeneral );
	}

	@Deprecated
	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<IFeedImage> images, List<Long> targetIds, String bodyGeneral,
			int storySize ) throws FacebookException {
		return getClient().feed_publishUserAction( bundleId, templateData, images, targetIds, bodyGeneral, storySize );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( String template ) throws FacebookException {
		return getClient().feed_registerTemplateBundle( template );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( Collection<String> templates ) throws FacebookException {
		return getClient().feed_registerTemplateBundle( templates );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( String template, String shortTemplate, String longTemplate ) throws FacebookException {
		return getClient().feed_registerTemplateBundle( template, shortTemplate, longTemplate );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( Collection<String> templates, Collection<BundleStoryTemplate> shortTemplates, BundleStoryTemplate longTemplate,
			List<BundleActionLink> actionLinks ) throws FacebookException {
		return getClient().feed_registerTemplateBundle( templates, shortTemplates, longTemplate, actionLinks );
	}

	@Deprecated
	public Long feed_registerTemplateBundle( Collection<String> templates, Collection<BundleStoryTemplate> shortTemplates, BundleStoryTemplate longTemplate )
			throws FacebookException {
		return getClient().feed_registerTemplateBundle( templates, shortTemplates, longTemplate );
	}

	@Deprecated
	public Boolean getCacheAppAdded() {
		return getClient().getCacheAppAdded();
	}

	public Boolean getCacheAppUser() {
		return getClient().getCacheAppUser();
	}

	public Long getCacheSessionExpires() {
		return getClient().getCacheSessionExpires();
	}

	public String getCacheSessionKey() {
		return getClient().getCacheSessionKey();
	}

	public String getCacheSessionSecret() {
		return getClient().getCacheSessionSecret();
	}

	public Long getCacheUserId() {
		return getClient().getCacheUserId();
	}

	public boolean isDesktop() {
		return getClient().isDesktop();
	}

	public Boolean liveMessage_send( Long recipient, String eventName, JSONObject message ) throws FacebookException {
		return getClient().liveMessage_send( recipient, eventName, message );
	}

	@Deprecated
	public Collection<String> notifications_send( Collection<Long> recipientIds, CharSequence notification ) throws FacebookException {
		return getClient().notifications_send( recipientIds, notification );
	}

	@Deprecated
	public Collection<String> notifications_send( CharSequence notification ) throws FacebookException {
		return getClient().notifications_send( notification );
	}

	@Deprecated
	public Collection<String> notifications_send( Collection<Long> recipientIds, String notification, boolean isAppToUser ) throws FacebookException {
		return getClient().notifications_send( recipientIds, notification, isAppToUser );
	}

	public Collection<String> notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException {
		return getClient().notifications_sendEmailToCurrentUser( subject, email, fbml );
	}

	public Collection<String> notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml )
			throws FacebookException {
		return getClient().notifications_sendEmail( recipients, subject, email, fbml );
	}

	public Collection<String> notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException {
		return getClient().notifications_sendTextEmailToCurrentUser( subject, email );
	}

	public Collection<String> notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException {
		return getClient().notifications_sendTextEmail( recipients, subject, email );
	}

	public Collection<String> notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException {
		return getClient().notifications_sendFbmlEmailToCurrentUser( subject, fbml );
	}

	public Collection<String> notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException {
		return getClient().notifications_sendFbmlEmail( recipients, subject, fbml );
	}

	@Deprecated
	public URL notifications_send( Collection<Long> recipientIds, CharSequence notification, CharSequence email ) throws FacebookException {
		return getClient().notifications_send( recipientIds, notification, email );
	}

	@Deprecated
	public String notifications_sendEmail( Collection<Long> recipientIds, CharSequence subject, CharSequence fbml ) throws FacebookException {
		return getClient().notifications_sendEmail( recipientIds, subject, fbml );
	}

	@Deprecated
	public String notifications_sendEmailPlain( Collection<Long> recipientIds, CharSequence subject, CharSequence text ) throws FacebookException {
		return getClient().notifications_sendEmailPlain( recipientIds, subject, text );
	}

	@Deprecated
	public String notifications_sendEmailStr( Collection<Long> recipientIds, CharSequence subject, CharSequence fbml, CharSequence text ) throws FacebookException {
		return getClient().notifications_sendEmailStr( recipientIds, subject, fbml, text );
	}

	public boolean pages_isAdmin( Long pageId ) throws FacebookException {
		return getClient().pages_isAdmin( pageId );
	}

	public boolean pages_isAppAdded( Long pageId ) throws FacebookException {
		return getClient().pages_isAppAdded( pageId );
	}

	public boolean pages_isFan( Long pageId, Long userId ) throws FacebookException {
		return getClient().pages_isFan( pageId, userId );
	}

	public boolean pages_isFan( Long pageId ) throws FacebookException {
		return getClient().pages_isFan( pageId );
	}

	public boolean permissions_grantApiAccess( String apiKey, Set<FacebookMethod> methods ) throws FacebookException {
		return getClient().permissions_grantApiAccess( apiKey, methods );
	}

	public boolean permissions_grantFullApiAccess( String apiKey ) throws FacebookException {
		return getClient().permissions_grantFullApiAccess( apiKey );
	}

	public boolean permissions_revokeApiAccess( String apiKey ) throws FacebookException {
		return getClient().permissions_revokeApiAccess( apiKey );
	}

	public boolean photos_addTag( String photoId, Long taggedUserId, Double pct, Double pct2 ) throws FacebookException {
		return getClient().photos_addTag( photoId, taggedUserId, pct, pct2 );
	}

	public boolean photos_addTag( String photoId, CharSequence tagText, Double pct, Double pct2 ) throws FacebookException {
		return getClient().photos_addTag( photoId, tagText, pct, pct2 );
	}

	public boolean photos_addTag( String photoId, Long taggedUserId, Double pct, Double pct2, Long userId ) throws FacebookException {
		return getClient().photos_addTag( photoId, taggedUserId, pct, pct2, userId );
	}

	public boolean photos_addTag( String photoId, CharSequence tagText, Double pct, Double pct2, Long userId ) throws FacebookException {
		return getClient().photos_addTag( photoId, tagText, pct, pct2, userId );
	}

	@Deprecated
	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml, String profileMain ) throws FacebookException {
		return getClient().profile_setFBML( userId, profileFbml, actionFbml, mobileFbml, profileMain );
	}

	@Deprecated
	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml ) throws FacebookException {
		return getClient().profile_setFBML( userId, profileFbml, actionFbml, mobileFbml );
	}

	@Deprecated
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup ) throws FacebookException {
		return getClient().profile_setFBML( profileFbmlMarkup, profileActionFbmlMarkup );
	}

	@Deprecated
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, Long profileId ) throws FacebookException {
		return getClient().profile_setFBML( profileFbmlMarkup, profileActionFbmlMarkup, profileId );
	}

	@Deprecated
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup, Long profileId )
			throws FacebookException {
		return getClient().profile_setFBML( profileFbmlMarkup, profileActionFbmlMarkup, mobileFbmlMarkup, profileId );
	}

	@Deprecated
	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup ) throws FacebookException {
		return getClient().profile_setFBML( profileFbmlMarkup, profileActionFbmlMarkup, mobileFbmlMarkup );
	}

	@Deprecated
	public boolean profile_setMobileFBML( CharSequence fbmlMarkup ) throws FacebookException {
		return getClient().profile_setMobileFBML( fbmlMarkup );
	}

	@Deprecated
	public boolean profile_setMobileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		return getClient().profile_setMobileFBML( fbmlMarkup, profileId );
	}

	@Deprecated
	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup ) throws FacebookException {
		return getClient().profile_setProfileActionFBML( fbmlMarkup );
	}

	@Deprecated
	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		return getClient().profile_setProfileActionFBML( fbmlMarkup, profileId );
	}

	@Deprecated
	public boolean profile_setProfileFBML( CharSequence fbmlMarkup ) throws FacebookException {
		return getClient().profile_setProfileActionFBML( fbmlMarkup );
	}

	@Deprecated
	public boolean profile_setProfileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		return getClient().profile_setProfileFBML( fbmlMarkup, profileId );
	}

	public boolean sms_canSend() throws FacebookException {
		return getClient().sms_canSend();
	}

	public boolean sms_canSend( Long userId ) throws FacebookException {
		return getClient().sms_canSend( userId );
	}

	public Integer sms_send( String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException {
		return getClient().sms_send( message, smsSessionId, makeNewSession );
	}

	public Integer sms_send( Long userId, String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException {
		return getClient().sms_send( userId, message, smsSessionId, makeNewSession );
	}

	public int sms_sendMessageWithSession( Long userId, CharSequence message ) throws FacebookException {
		return getClient().sms_sendMessageWithSession( userId, message );
	}

	public String stream_publish( final String message, final Attachment attachment, final Collection<BundleActionLink> actionLinks, final Long targetId, final Long userId )
			throws FacebookException {
		return getClient().stream_publish( message, attachment, actionLinks, targetId, userId );
	}

	public boolean stream_remove( final String postId, final Long userId ) throws FacebookException {
		return getClient().stream_remove( postId, userId );
	}

	public String stream_addComment( String postId, String comment, Long userId ) throws FacebookException {
		return getClient().stream_addComment( postId, comment, userId );
	}

	public boolean stream_removeComment( String commentId, Long userId ) throws FacebookException {
		return getClient().stream_removeComment( commentId, userId );
	}

	public boolean stream_addLike( String postId, Long userId ) throws FacebookException {
		return getClient().stream_addLike( postId, userId );
	}

	public boolean stream_removeLike( String postId, Long userId ) throws FacebookException {
		return getClient().stream_removeLike( postId, userId );
	}

	public boolean users_clearStatus() throws FacebookException {
		return getClient().users_clearStatus();
	}

	public long users_getLoggedInUser() throws FacebookException {
		return getClient().users_getLoggedInUser();
	}

	public boolean users_hasAppPermission( Permission perm ) throws FacebookException {
		return getClient().users_hasAppPermission( perm );
	}

	public boolean users_hasAppPermission( Permission perm, Long userId ) throws FacebookException {
		return getClient().users_hasAppPermission( perm, userId );
	}

	@Deprecated
	public boolean users_isAppAdded() throws FacebookException {
		return getClient().users_isAppAdded();
	}

	@Deprecated
	public boolean users_isAppAdded( Long userId ) throws FacebookException {
		return getClient().users_isAppAdded( userId );
	}

	public boolean users_isAppUser() throws FacebookException {
		return getClient().users_isAppUser();
	}

	public boolean users_isAppUser( Long userId ) throws FacebookException {
		return getClient().users_isAppUser( userId );
	}

	public boolean users_setStatus( String status ) throws FacebookException {
		return getClient().users_setStatus( status );
	}

	public boolean users_setStatus( String status, Long userId ) throws FacebookException {
		return getClient().users_setStatus( status, userId );
	}

	public boolean users_setStatus( String newStatus, boolean clear ) throws FacebookException {
		return getClient().users_setStatus( newStatus, clear );
	}

	public boolean users_setStatus( String newStatus, boolean clear, Long userId ) throws FacebookException {
		return getClient().users_setStatus( newStatus, clear, userId );
	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb ) throws FacebookException {
		return getClient().users_setStatus( newStatus, clear, statusIncludesVerb );
	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb, Long userId ) throws FacebookException {
		return getClient().users_setStatus( newStatus, clear, statusIncludesVerb, userId );
	}

	public Long links_post( Long userId, String url, String comment ) throws FacebookException {
		return getClient().links_post( userId, url, comment );
	}

	public int intl_uploadNativeStrings( Map<String,String> native_strings ) throws FacebookException {
		return getClient().intl_uploadNativeStrings( native_strings );
	}

}
