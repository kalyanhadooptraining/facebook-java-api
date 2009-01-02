package com.google.code.facebookapi;

import javax.annotation.Generated;

@Generated(value="com.google.code.facebookapi.apt.FacebookReturnTypeProcessor", date="2009-01-02T19:23:00.023+0000")
public class FacebookXmlRestClient extends FacebookXmlRestClientBase {

    public FacebookXmlRestClient( java.lang.String apiKey, java.lang.String secret )  {
        super( apiKey, secret );
    }

    public FacebookXmlRestClient( java.lang.String apiKey, java.lang.String secret, int connectionTimeout )  {
        super( apiKey, secret, connectionTimeout );
    }

    public FacebookXmlRestClient( java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey )  {
        super( apiKey, secret, sessionKey );
    }

    public FacebookXmlRestClient( java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey, int connectionTimeout )  {
        super( apiKey, secret, sessionKey, connectionTimeout );
    }

    public FacebookXmlRestClient( java.lang.String serverAddr, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey ) throws java.net.MalformedURLException {
        super( serverAddr, apiKey, secret, sessionKey );
    }

    public FacebookXmlRestClient( java.lang.String serverAddr, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey, int connectionTimeout ) throws java.net.MalformedURLException {
        super( serverAddr, apiKey, secret, sessionKey, connectionTimeout );
    }

    public FacebookXmlRestClient( java.net.URL serverUrl, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey )  {
        super( serverUrl, apiKey, secret, sessionKey );
    }

    public FacebookXmlRestClient( java.net.URL serverUrl, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey, int connectionTimeout )  {
        super( serverUrl, apiKey, secret, sessionKey, connectionTimeout );
    }

    public FacebookXmlRestClient( java.net.URL serverUrl, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey, int connectionTimeout, int readTimeout )  {
        super( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout );
    }

    public org.w3c.dom.Document photos_getByAlbum( java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getByAlbum( albumId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document profile_getFBML( java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getFBML( userId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document application_getPublicInfoById( java.lang.Long applicationId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.application_getPublicInfoById( applicationId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getObjectProperty( long objectId, java.lang.String propertyName ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObjectProperty( objectId, propertyName );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document notifications_sendEmailToCurrentUser( java.lang.String subject, java.lang.String email, java.lang.String fbml ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendEmailToCurrentUser( subject, email, fbml );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document events_get( java.lang.Long userId, java.util.Collection<java.lang.Long> eventIds, java.lang.Long startTime, java.lang.Long endTime ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.events_get( userId, eventIds, startTime, endTime );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.lang.Long userId, java.lang.String caption, java.lang.Long albumId, java.lang.String fileName, java.io.InputStream fileStream ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, caption, albumId, fileName, fileStream );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getUserPreferences(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getUserPreferences(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document notifications_get(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_get(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getAssociationDefinitions(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getAssociationDefinitions(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document users_getInfo( java.util.Collection<java.lang.Long> userIds, java.util.Collection<com.google.code.facebookapi.ProfileField> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.users_getInfo( userIds, fields );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    @Deprecated
    public org.w3c.dom.Document marketplace_search( java.lang.CharSequence category, java.lang.CharSequence subCategory, java.lang.CharSequence query ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.marketplace_search( category, subCategory, query );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document marketplace_getSubCategories( java.lang.CharSequence category ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.marketplace_getSubCategories( category );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document connect_registerUsers( java.util.Collection<java.util.Map<java.lang.String,java.lang.String>> accounts ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.connect_registerUsers( accounts );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document notifications_sendTextEmail( java.util.Collection<java.lang.Long> recipients, java.lang.String subject, java.lang.String email ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendTextEmail( recipients, subject, email );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document friends_areFriends( java.util.Collection<java.lang.Long> userIds1, java.util.Collection<java.lang.Long> userIds2 ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_areFriends( userIds1, userIds2 );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document friends_getList( java.lang.Long friendListId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_getList( friendListId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document friends_get(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_get(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getAssociationDefinition( java.lang.String associationName ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getAssociationDefinition( associationName );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document profile_getFBML( int type ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getFBML( type );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document notifications_sendTextEmailToCurrentUser( java.lang.String subject, java.lang.String email ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendTextEmailToCurrentUser( subject, email );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    @Deprecated
    public org.w3c.dom.Document admin_getDailyMetrics( java.util.Set<com.google.code.facebookapi.Metric> metrics, long start, long end ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_get( java.lang.Long subjId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( subjId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document permissions_checkGrantedApiAccess( java.lang.String apiKey ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.permissions_checkGrantedApiAccess( apiKey );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    @Deprecated
    public org.w3c.dom.Document marketplace_getCategoriesObject(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.marketplace_getCategoriesObject(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document friends_getAppUsers(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_getAppUsers(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_createAlbum( java.lang.String albumName, java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_createAlbum( albumName, userId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_get( java.lang.Long subjId, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( subjId, albumId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document profile_getFBML( int type, java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getFBML( type, userId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_get( java.lang.Long subjId, java.lang.Long albumId, java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( subjId, albumId, photoIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document batch_run( java.lang.String methods, boolean serial ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.batch_run( methods, serial );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getObjectTypes(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObjectTypes(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_createAlbum( java.lang.String name, java.lang.String description, java.lang.String location ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_createAlbum( name, description, location );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document users_getInfo( java.util.Collection<java.lang.Long> userIds, java.util.Set<java.lang.CharSequence> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.users_getInfo( userIds, fields );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.lang.Long userId, java.io.File photo, java.lang.String caption, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, photo, caption, albumId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document friends_getLists(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_getLists(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document pages_getInfo( java.lang.Long userId, java.util.Set<java.lang.CharSequence> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.pages_getInfo( userId, fields );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    @Deprecated
    public org.w3c.dom.Document admin_getDailyMetrics( java.util.Set<com.google.code.facebookapi.Metric> metrics, java.util.Date start, java.util.Date end ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document permissions_checkAvailableApiAccess( java.lang.String apiKey ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.permissions_checkAvailableApiAccess( apiKey );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_createAlbum( java.lang.String albumName ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_createAlbum( albumName );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document marketplace_getListings( java.util.Collection<java.lang.Long> listingIds, java.util.Collection<java.lang.Long> userIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.marketplace_getListings( listingIds, userIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getCookies( java.lang.Long userId, java.lang.CharSequence name ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getCookies( userId, name );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document groups_getMembers( java.lang.Number groupId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.groups_getMembers( groupId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_createAlbum( java.lang.String name, java.lang.String description, java.lang.String location, java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_createAlbum( name, description, location, userId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document friends_areFriends( long userId1, long userId2 ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_areFriends( userId1, userId2 );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document users_getStandardInfo( java.util.Collection<java.lang.Long> userIds, java.util.Set<java.lang.CharSequence> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.users_getStandardInfo( userIds, fields );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document events_getMembers( java.lang.Long eventId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.events_getMembers( eventId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_getByAlbum( java.lang.Long albumId, java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getByAlbum( albumId, photoIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getCookies( java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getCookies( userId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document feed_getRegisteredTemplateBundles(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.feed_getRegisteredTemplateBundles(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_get( java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( photoIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document application_getPublicInfo( java.lang.Long applicationId, java.lang.String applicationKey, java.lang.String applicationCanvas ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.application_getPublicInfo( applicationId, applicationKey, applicationCanvas );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_addTags( java.lang.Long photoId, java.util.Collection<com.google.code.facebookapi.PhotoTag> tags, java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_addTags( photoId, tags, userId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document groups_get( java.lang.Long userId, java.util.Collection<java.lang.Long> groupIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.groups_get( userId, groupIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.io.File photo, java.lang.String caption ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( photo, caption );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document connect_unregisterUsers( java.util.Collection<java.lang.String> email_hashes ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.connect_unregisterUsers( email_hashes );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.lang.Long userId, java.io.File photo, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, photo, albumId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getCookies( java.lang.String name ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getCookies( name );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getObject( long objectId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObject( objectId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getObjectType( java.lang.String objectType ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObjectType( objectType );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document pages_getInfo( java.util.Collection<java.lang.Long> pageIds, java.util.Set<java.lang.CharSequence> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.pages_getInfo( pageIds, fields );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document users_getStandardInfo( java.util.Collection<java.lang.Long> userIds, java.util.Collection<com.google.code.facebookapi.ProfileField> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.users_getStandardInfo( userIds, fields );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.lang.Long userId, java.io.File photo, java.lang.String caption ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, photo, caption );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document admin_getMetrics( java.util.Set<com.google.code.facebookapi.Metric> metrics, long start, long end, long period ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document pages_getInfo( java.lang.Long userId, java.util.EnumSet<com.google.code.facebookapi.PageProfileField> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.pages_getInfo( userId, fields );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_addTags( java.lang.Long photoId, java.util.Collection<com.google.code.facebookapi.PhotoTag> tags ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_addTags( photoId, tags );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_getAlbums( java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getAlbums( userId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document events_get( java.lang.Long userId, java.util.Collection<java.lang.Long> eventIds, java.lang.Long startTime, java.lang.Long endTime, java.lang.String rsvp_status ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.events_get( userId, eventIds, startTime, endTime, rsvp_status );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document application_getPublicInfoByCanvasName( java.lang.String applicationCanvas ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.application_getPublicInfoByCanvasName( applicationCanvas );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_getAlbums( java.lang.Long userId, java.util.Collection<java.lang.Long> albumIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getAlbums( userId, albumIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document notifications_sendEmail( java.util.Collection<java.lang.Long> recipients, java.lang.CharSequence subject, java.lang.CharSequence email, java.lang.CharSequence fbml ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendEmail( recipients, subject, email, fbml );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.io.File photo, java.lang.String caption, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( photo, caption, albumId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document notifications_sendFbmlEmail( java.util.Collection<java.lang.Long> recipients, java.lang.String subject, java.lang.String fbml ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendFbmlEmail( recipients, subject, fbml );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document fql_query( java.lang.CharSequence query ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.fql_query( query );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_get( java.lang.Long subjId, java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( subjId, photoIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document application_getPublicInfoByApiKey( java.lang.String applicationKey ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.application_getPublicInfoByApiKey( applicationKey );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document admin_getMetrics( java.util.Set<com.google.code.facebookapi.Metric> metrics, java.util.Date start, java.util.Date end, long period ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document feed_getRegisteredTemplateBundleByID( java.lang.Long id ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.feed_getRegisteredTemplateBundleByID( id );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.io.File photo, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( photo, albumId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.lang.Long userId, java.io.File photo ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, photo );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getObjects( java.util.Collection<java.lang.Long> objectIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObjects( objectIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document profile_getInfo( java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getInfo( userId );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document friends_get( java.lang.Long uid ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_get( uid );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_getTags( java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getTags( photoIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_getAlbums( java.util.Collection<java.lang.Long> albumIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getAlbums( albumIds );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document data_getCookies(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getCookies(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document profile_getFBML(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getFBML(  );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document photos_upload( java.io.File photo ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( photo );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document pages_getInfo( java.util.Collection<java.lang.Long> pageIds, java.util.EnumSet<com.google.code.facebookapi.PageProfileField> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.pages_getInfo( pageIds, fields );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document notifications_sendFbmlEmailToCurrentUser( java.lang.String subject, java.lang.String fbml ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendFbmlEmailToCurrentUser( subject, fbml );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

    public org.w3c.dom.Document profile_getInfoOptions( java.lang.String field ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getInfoOptions( field );
        return (org.w3c.dom.Document)parseCallResult( rawResponse );
    }

}
