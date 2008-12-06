package com.google.code.facebookapi;

import java.net.URL;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

/**
 * Eventually want this to have no methods in it whatsoever. This base adapter covers the cases where we want to do a simple proxy to the ExtensibleClient because the
 * return type on the ExtensibleClient is not Object or void.
 * 
 * @author dboden
 */
public abstract class SpecificReturnTypeAdapter extends BaseAdapter {

	private String responseFormat;

	protected SpecificReturnTypeAdapter( String responseFormat ) {
		super( responseFormat );
		this.responseFormat = responseFormat;
	}

	public int admin_getAllocation( String allocationType ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_getAllocation( allocationType );
	}

	public int admin_getAllocation( AllocationType allocationType ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_getAllocation( allocationType );
	}

	public JSONObject admin_getAppProperties( Collection<ApplicationProperty> properties ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_getAppProperties( properties );
	}

	public ApplicationPropertySet admin_getAppPropertiesAsSet( EnumSet<ApplicationProperty> properties ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_getAppPropertiesAsSet( properties );
	}

	public Map<ApplicationProperty,String> admin_getAppPropertiesMap( Collection<ApplicationProperty> properties ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_getAppPropertiesMap( properties );
	}

	public int admin_getNotificationAllocation() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_getNotificationAllocation();
	}

	public int admin_getRequestAllocation() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_getRequestAllocation();
	}

	public boolean admin_setAppProperties( Map<ApplicationProperty,String> properties ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_setAppProperties( properties );
	}

	public boolean admin_setAppProperties( ApplicationPropertySet properties ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().admin_setAppProperties( properties );
	}

	public String auth_createToken() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().auth_createToken();
	}

	public boolean auth_expireSession() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().auth_expireSession();
	}

	public long auth_getUserId( String authToken ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().auth_getUserId( authToken );
	}

	public String auth_promoteSession() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().auth_promoteSession();
	}

	public boolean auth_revokeAuthorization() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().auth_revokeAuthorization();
	}

	public int connect_getUnconnectedFriendsCount() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().connect_getUnconnectedFriendsCount();
	}

	public long data_createObject( String objectType, Map<String,String> properties ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_createObject( objectType, properties );
	}

	public long data_getAssociatedObjectCount( String associationName, long objectId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_getAssociatedObjectCount( associationName, objectId );
	}

	public String data_getUserPreference( int prefId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_getUserPreference( prefId );
	}

	public boolean data_setCookie( String name, String value ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_setCookie( name, value );
	}

	public boolean data_setCookie( String name, String value, String path ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_setCookie( name, value, path );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_setCookie( userId, name, value );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, CharSequence path ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_setCookie( userId, name, value, path );
	}

	public boolean data_setCookie( String name, String value, Long expires ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_setCookie( name, value, expires );
	}

	public boolean data_setCookie( String name, String value, Long expires, String path ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_setCookie( name, value, expires, path );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_setCookie( userId, name, value, expires );
	}

	public boolean data_setCookie( Long userId, CharSequence name, CharSequence value, Long expires, CharSequence path ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().data_setCookie( userId, name, value, expires, path );
	}

	public boolean events_cancel( Long eid, String cancel_message ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().events_cancel( eid, cancel_message );
	}

	public Long events_create( Map<String,String> event_info ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().events_create( event_info );
	}

	public boolean events_edit( Long eid, Map<String,String> event_info ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().events_edit( eid, event_info );
	}

	public boolean events_rsvp( Long eid, String rsvp_status ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().events_rsvp( eid, rsvp_status );
	}

	public boolean fbml_refreshImgSrc( String imageUrl ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().fbml_refreshImgSrc( imageUrl );
	}

	public boolean fbml_refreshImgSrc( URL imageUrl ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().fbml_refreshImgSrc( imageUrl );
	}

	public boolean fbml_refreshRefUrl( String url ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().fbml_refreshRefUrl( url );
	}

	public boolean fbml_refreshRefUrl( URL url ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().fbml_refreshRefUrl( url );
	}

	public boolean fbml_setRefHandle( String handle, String markup ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().fbml_setRefHandle( handle, markup );
	}

	public boolean feed_PublishTemplatizedAction( TemplatizedAction action ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().feed_PublishTemplatizedAction( action );
	}

	public boolean feed_deactivateTemplateBundleByID( Long bundleId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().feed_deactivateTemplateBundleByID( bundleId );
	}

	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().feed_publishTemplatizedAction( actorId, titleTemplate );
	}

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().feed_publishTemplatizedAction( titleTemplate );
	}

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Long pageActorId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().feed_publishTemplatizedAction( titleTemplate, pageActorId );
	}

	public boolean feed_publishTemplatizedAction( CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images,
			Long pageActorId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean feed_publishTemplatizedAction( Long actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean feed_publishTemplatizedAction( String titleTemplate, String titleData, String bodyTemplate, String bodyData, String bodyGeneral,
			Collection<? extends IPair<? extends Object,URL>> pictures, String targetIds ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean feed_publishTemplatizedAction( Integer actorId, CharSequence titleTemplate, Map<String,CharSequence> titleData, CharSequence bodyTemplate,
			Map<String,CharSequence> bodyData, CharSequence bodyGeneral, Collection<Long> targetIds, Collection<? extends IPair<? extends Object,URL>> images )
			throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public Boolean feed_publishUserAction( Long bundleId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<Long> targetIds, String bodyGeneral ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean feed_publishUserAction( Long bundleId, Map<String,String> templateData, List<IFeedImage> images, List<Long> targetIds, String bodyGeneral,
			int storySize ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long feed_registerTemplateBundle( String template ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long feed_registerTemplateBundle( Collection<String> templates ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long feed_registerTemplateBundle( String template, String shortTemplate, String longTemplate ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long feed_registerTemplateBundle( Collection<String> templates, Collection<BundleStoryTemplate> shortTemplates, BundleStoryTemplate longTemplate,
			List<BundleActionLink> actionLinks ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long feed_registerTemplateBundle( Collection<String> templates, Collection<BundleStoryTemplate> shortTemplates, BundleStoryTemplate longTemplate )
			throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean getCacheAppAdded() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean getCacheAppUser() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long getCacheSessionExpires() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public String getCacheSessionKey() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public String getCacheSessionSecret() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long getCacheUserId() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public URL getDefaultServerUrl() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public String getRawResponse() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDesktop() {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public Boolean liveMessage_send( Long recipient, String eventName, JSONObject message ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_createListing( Boolean showOnProfile, MarketplaceListing attrs ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_createListing( Long listingId, boolean showOnProfile, String attributes ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_createListing( Long listingId, boolean showOnProfile, MarketListing listing ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_createListing( boolean showOnProfile, MarketListing listing ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_createListing( Long listingId, boolean showOnProfile, String attributes, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_createListing( Long listingId, boolean showOnProfile, MarketListing listing, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_createListing( boolean showOnProfile, MarketListing listing, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_editListing( Long listingId, Boolean showOnProfile, MarketplaceListing attrs ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Long marketplace_editListing( Long listingId, Boolean showOnProfile, MarketListing attrs ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> marketplace_getCategories() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public boolean marketplace_removeListing( Long listingId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean marketplace_removeListing( Long listingId, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean marketplace_removeListing( Long listingId, CharSequence status ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean marketplace_removeListing( Long listingId, MarketListingStatus status ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean marketplace_removeListing( Long listingId, MarketListingStatus status, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public URL notifications_send( Collection<Long> recipientIds, CharSequence notification, CharSequence email ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public String notifications_sendEmail( Collection<Long> recipientIds, CharSequence subject, CharSequence fbml ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public String notifications_sendEmailPlain( Collection<Long> recipientIds, CharSequence subject, CharSequence text ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public String notifications_sendEmailStr( Collection<Long> recipientIds, CharSequence subject, CharSequence fbml, CharSequence text ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public boolean pages_isAdmin( Long pageId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean pages_isAppAdded( Long pageId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean pages_isFan( Long pageId, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean pages_isFan( Long pageId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean permissions_grantApiAccess( String apiKey, Set<FacebookMethod> methods ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean permissions_grantFullApiAccess( String apiKey ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean permissions_revokeApiAccess( String apiKey ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean photos_addTag( Long photoId, Long taggedUserId, Double pct, Double pct2 ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean photos_addTag( Long photoId, CharSequence tagText, Double pct, Double pct2 ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean photos_addTag( Long photoId, Long taggedUserId, Double pct, Double pct2, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean photos_addTag( Long photoId, CharSequence tagText, Double pct, Double pct2, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml, String profileMain ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setFBML( Long userId, String profileFbml, String actionFbml, String mobileFbml ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, Long profileId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup, Long profileId )
			throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setFBML( CharSequence profileFbmlMarkup, CharSequence profileActionFbmlMarkup, CharSequence mobileFbmlMarkup ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setMobileFBML( CharSequence fbmlMarkup ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setMobileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setProfileActionFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setProfileFBML( CharSequence fbmlMarkup ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean profile_setProfileFBML( CharSequence fbmlMarkup, Long profileId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean sms_canSend() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean sms_canSend( Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public Integer sms_send( String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public Integer sms_send( Long userId, String message, Integer smsSessionId, boolean makeNewSession ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return null;
	}

	public int sms_sendMessageWithSession( Long userId, CharSequence message ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean users_clearStatus() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public long users_getLoggedInUser() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		return getClient().users_getLoggedInUser();
	}

	public boolean users_hasAppPermission( Permission perm ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_hasAppPermission( Permission perm, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_isAppAdded() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_isAppAdded( Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_isAppUser() throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_isAppUser( Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_setStatus( String status ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_setStatus( String status, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_setStatus( String newStatus, boolean clear ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_setStatus( String newStatus, boolean clear, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

	public boolean users_setStatus( String newStatus, boolean clear, boolean statusIncludesVerb, Long userId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		// TODO Auto-generated method stub
		return false;
	}

}
