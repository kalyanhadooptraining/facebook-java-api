package com.google.code.facebookapi;

import javax.annotation.Generated;

@Generated(value="com.google.code.facebookapi.apt.FacebookReturnTypeProcessor", date="2009-01-02T19:23:00.023+0000")
@SuppressWarnings("unchecked")
public class FacebookJaxbRestClient extends FacebookJaxbRestClientBase {

    public FacebookJaxbRestClient( com.google.code.facebookapi.ExtensibleClient client )  {
        super( client );
    }

    public FacebookJaxbRestClient( java.lang.String apiKey, java.lang.String secret )  {
        super( apiKey, secret );
    }

    public FacebookJaxbRestClient( java.lang.String apiKey, java.lang.String secret, int connectionTimeout )  {
        super( apiKey, secret, connectionTimeout );
    }

    public FacebookJaxbRestClient( java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey )  {
        super( apiKey, secret, sessionKey );
    }

    public FacebookJaxbRestClient( java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey, int connectionTimeout )  {
        super( apiKey, secret, sessionKey, connectionTimeout );
    }

    public FacebookJaxbRestClient( java.lang.String serverAddr, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey ) throws java.net.MalformedURLException {
        super( serverAddr, apiKey, secret, sessionKey );
    }

    public FacebookJaxbRestClient( java.lang.String serverAddr, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey, int connectionTimeout ) throws java.net.MalformedURLException {
        super( serverAddr, apiKey, secret, sessionKey, connectionTimeout );
    }

    public FacebookJaxbRestClient( java.net.URL serverUrl, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey )  {
        super( serverUrl, apiKey, secret, sessionKey );
    }

    public FacebookJaxbRestClient( java.net.URL serverUrl, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey, int connectionTimeout )  {
        super( serverUrl, apiKey, secret, sessionKey, connectionTimeout );
    }

    public FacebookJaxbRestClient( java.net.URL serverUrl, java.lang.String apiKey, java.lang.String secret, java.lang.String sessionKey, int connectionTimeout, int readTimeout )  {
        super( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout );
    }

    public java.util.List<com.google.code.facebookapi.schema.Photo> photos_getByAlbum( java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getByAlbum( albumId );
        return (java.util.List<com.google.code.facebookapi.schema.Photo>)parseCallResult( rawResponse );
    }

    public java.lang.String profile_getFBML( java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getFBML( userId );
        return (java.lang.String)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse application_getPublicInfoById( java.lang.Long applicationId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.application_getPublicInfoById( applicationId );
        return (com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse)parseCallResult( rawResponse );
    }

    public java.lang.String data_getObjectProperty( long objectId, java.lang.String propertyName ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObjectProperty( objectId, propertyName );
        return (java.lang.String)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.Long> notifications_sendEmailToCurrentUser( java.lang.String subject, java.lang.String email, java.lang.String fbml ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendEmailToCurrentUser( subject, email, fbml );
        return (java.util.List<java.lang.Long>)parseCallResult( rawResponse );
    }

    public Object events_get( java.lang.Long userId, java.util.Collection<java.lang.Long> eventIds, java.lang.Long startTime, java.lang.Long endTime ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.events_get( userId, eventIds, startTime, endTime );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.lang.Long userId, java.lang.String caption, java.lang.Long albumId, java.lang.String fileName, java.io.InputStream fileStream ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, caption, albumId, fileName, fileStream );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public Object data_getUserPreferences(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getUserPreferences(  );
        return (Object)parseCallResult( rawResponse );
    }

    public Object notifications_get(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_get(  );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.DataGetAssociationDefinitionsResponse data_getAssociationDefinitions(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getAssociationDefinitions(  );
        return (com.google.code.facebookapi.schema.DataGetAssociationDefinitionsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.UsersGetInfoResponse users_getInfo( java.util.Collection<java.lang.Long> userIds, java.util.Collection<com.google.code.facebookapi.ProfileField> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.users_getInfo( userIds, fields );
        return (com.google.code.facebookapi.schema.UsersGetInfoResponse)parseCallResult( rawResponse );
    }

    @Deprecated
    public Object marketplace_search( java.lang.CharSequence category, java.lang.CharSequence subCategory, java.lang.CharSequence query ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.marketplace_search( category, subCategory, query );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.MarketplaceGetSubCategoriesResponse marketplace_getSubCategories( java.lang.CharSequence category ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.marketplace_getSubCategories( category );
        return (com.google.code.facebookapi.schema.MarketplaceGetSubCategoriesResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.ConnectRegisterUsersResponse connect_registerUsers( java.util.Collection<java.util.Map<java.lang.String,java.lang.String>> accounts ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.connect_registerUsers( accounts );
        return (com.google.code.facebookapi.schema.ConnectRegisterUsersResponse)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.Long> notifications_sendTextEmail( java.util.Collection<java.lang.Long> recipients, java.lang.String subject, java.lang.String email ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendTextEmail( recipients, subject, email );
        return (java.util.List<java.lang.Long>)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.FriendsAreFriendsResponse friends_areFriends( java.util.Collection<java.lang.Long> userIds1, java.util.Collection<java.lang.Long> userIds2 ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_areFriends( userIds1, userIds2 );
        return (com.google.code.facebookapi.schema.FriendsAreFriendsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.FriendsGetListsResponse friends_getList( java.lang.Long friendListId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_getList( friendListId );
        return (com.google.code.facebookapi.schema.FriendsGetListsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.FriendsGetResponse friends_get(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_get(  );
        return (com.google.code.facebookapi.schema.FriendsGetResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.AssociationInfo data_getAssociationDefinition( java.lang.String associationName ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getAssociationDefinition( associationName );
        return (com.google.code.facebookapi.AssociationInfo)parseCallResult( rawResponse );
    }

    public java.lang.String profile_getFBML( int type ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getFBML( type );
        return (java.lang.String)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.Long> notifications_sendTextEmailToCurrentUser( java.lang.String subject, java.lang.String email ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendTextEmailToCurrentUser( subject, email );
        return (java.util.List<java.lang.Long>)parseCallResult( rawResponse );
    }

    @Deprecated
    public Object admin_getDailyMetrics( java.util.Set<com.google.code.facebookapi.Metric> metrics, long start, long end ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetResponse photos_get( java.lang.Long subjId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( subjId );
        return (com.google.code.facebookapi.schema.PhotosGetResponse)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.String> permissions_checkGrantedApiAccess( java.lang.String apiKey ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.permissions_checkGrantedApiAccess( apiKey );
        return (java.util.List<java.lang.String>)parseCallResult( rawResponse );
    }

    @Deprecated
    public Object marketplace_getCategoriesObject(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.marketplace_getCategoriesObject(  );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.FriendsGetAppUsersResponse friends_getAppUsers(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_getAppUsers(  );
        return (com.google.code.facebookapi.schema.FriendsGetAppUsersResponse)parseCallResult( rawResponse );
    }

    public Object photos_createAlbum( java.lang.String albumName, java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_createAlbum( albumName, userId );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetResponse photos_get( java.lang.Long subjId, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( subjId, albumId );
        return (com.google.code.facebookapi.schema.PhotosGetResponse)parseCallResult( rawResponse );
    }

    public java.lang.String profile_getFBML( int type, java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getFBML( type, userId );
        return (java.lang.String)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetResponse photos_get( java.lang.Long subjId, java.lang.Long albumId, java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( subjId, albumId, photoIds );
        return (com.google.code.facebookapi.schema.PhotosGetResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.BatchRunResponse batch_run( java.lang.String methods, boolean serial ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.batch_run( methods, serial );
        return (com.google.code.facebookapi.schema.BatchRunResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.DataGetObjectTypesResponse data_getObjectTypes(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObjectTypes(  );
        return (com.google.code.facebookapi.schema.DataGetObjectTypesResponse)parseCallResult( rawResponse );
    }

    public Object photos_createAlbum( java.lang.String name, java.lang.String description, java.lang.String location ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_createAlbum( name, description, location );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.UsersGetInfoResponse users_getInfo( java.util.Collection<java.lang.Long> userIds, java.util.Set<java.lang.CharSequence> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.users_getInfo( userIds, fields );
        return (com.google.code.facebookapi.schema.UsersGetInfoResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.lang.Long userId, java.io.File photo, java.lang.String caption, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, photo, caption, albumId );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.FriendsGetListsResponse friends_getLists(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_getLists(  );
        return (com.google.code.facebookapi.schema.FriendsGetListsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PagesGetInfoResponse pages_getInfo( java.lang.Long userId, java.util.Set<java.lang.CharSequence> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.pages_getInfo( userId, fields );
        return (com.google.code.facebookapi.schema.PagesGetInfoResponse)parseCallResult( rawResponse );
    }

    @Deprecated
    public Object admin_getDailyMetrics( java.util.Set<com.google.code.facebookapi.Metric> metrics, java.util.Date start, java.util.Date end ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
        return (Object)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.String> permissions_checkAvailableApiAccess( java.lang.String apiKey ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.permissions_checkAvailableApiAccess( apiKey );
        return (java.util.List<java.lang.String>)parseCallResult( rawResponse );
    }

    public Object photos_createAlbum( java.lang.String albumName ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_createAlbum( albumName );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.MarketplaceGetListingsResponse marketplace_getListings( java.util.Collection<java.lang.Long> listingIds, java.util.Collection<java.lang.Long> userIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.marketplace_getListings( listingIds, userIds );
        return (com.google.code.facebookapi.schema.MarketplaceGetListingsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.DataGetCookiesResponse data_getCookies( java.lang.Long userId, java.lang.CharSequence name ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getCookies( userId, name );
        return (com.google.code.facebookapi.schema.DataGetCookiesResponse)parseCallResult( rawResponse );
    }

    public Object groups_getMembers( java.lang.Number groupId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.groups_getMembers( groupId );
        return (Object)parseCallResult( rawResponse );
    }

    public Object photos_createAlbum( java.lang.String name, java.lang.String description, java.lang.String location, java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_createAlbum( name, description, location, userId );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.FriendsAreFriendsResponse friends_areFriends( long userId1, long userId2 ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_areFriends( userId1, userId2 );
        return (com.google.code.facebookapi.schema.FriendsAreFriendsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.UsersGetStandardInfoResponse users_getStandardInfo( java.util.Collection<java.lang.Long> userIds, java.util.Set<java.lang.CharSequence> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.users_getStandardInfo( userIds, fields );
        return (com.google.code.facebookapi.schema.UsersGetStandardInfoResponse)parseCallResult( rawResponse );
    }

    public Object events_getMembers( java.lang.Long eventId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.events_getMembers( eventId );
        return (Object)parseCallResult( rawResponse );
    }

    public java.util.List<com.google.code.facebookapi.schema.Photo> photos_getByAlbum( java.lang.Long albumId, java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getByAlbum( albumId, photoIds );
        return (java.util.List<com.google.code.facebookapi.schema.Photo>)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.DataGetCookiesResponse data_getCookies( java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getCookies( userId );
        return (com.google.code.facebookapi.schema.DataGetCookiesResponse)parseCallResult( rawResponse );
    }

    public Object feed_getRegisteredTemplateBundles(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.feed_getRegisteredTemplateBundles(  );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetResponse photos_get( java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( photoIds );
        return (com.google.code.facebookapi.schema.PhotosGetResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse application_getPublicInfo( java.lang.Long applicationId, java.lang.String applicationKey, java.lang.String applicationCanvas ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.application_getPublicInfo( applicationId, applicationKey, applicationCanvas );
        return (com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse)parseCallResult( rawResponse );
    }

    public Object photos_addTags( java.lang.Long photoId, java.util.Collection<com.google.code.facebookapi.PhotoTag> tags, java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_addTags( photoId, tags, userId );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.GroupsGetResponse groups_get( java.lang.Long userId, java.util.Collection<java.lang.Long> groupIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.groups_get( userId, groupIds );
        return (com.google.code.facebookapi.schema.GroupsGetResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.io.File photo, java.lang.String caption ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( photo, caption );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.ConnectUnregisterUsersResponse connect_unregisterUsers( java.util.Collection<java.lang.String> email_hashes ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.connect_unregisterUsers( email_hashes );
        return (com.google.code.facebookapi.schema.ConnectUnregisterUsersResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.lang.Long userId, java.io.File photo, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, photo, albumId );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.DataGetCookiesResponse data_getCookies( java.lang.String name ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getCookies( name );
        return (com.google.code.facebookapi.schema.DataGetCookiesResponse)parseCallResult( rawResponse );
    }

    public Object data_getObject( long objectId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObject( objectId );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.DataGetObjectTypeResponse data_getObjectType( java.lang.String objectType ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObjectType( objectType );
        return (com.google.code.facebookapi.schema.DataGetObjectTypeResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PagesGetInfoResponse pages_getInfo( java.util.Collection<java.lang.Long> pageIds, java.util.Set<java.lang.CharSequence> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.pages_getInfo( pageIds, fields );
        return (com.google.code.facebookapi.schema.PagesGetInfoResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.UsersGetStandardInfoResponse users_getStandardInfo( java.util.Collection<java.lang.Long> userIds, java.util.Collection<com.google.code.facebookapi.ProfileField> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.users_getStandardInfo( userIds, fields );
        return (com.google.code.facebookapi.schema.UsersGetStandardInfoResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.lang.Long userId, java.io.File photo, java.lang.String caption ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, photo, caption );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.AdminGetMetricsResponse admin_getMetrics( java.util.Set<com.google.code.facebookapi.Metric> metrics, long start, long end, long period ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
        return (com.google.code.facebookapi.schema.AdminGetMetricsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PagesGetInfoResponse pages_getInfo( java.lang.Long userId, java.util.EnumSet<com.google.code.facebookapi.PageProfileField> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.pages_getInfo( userId, fields );
        return (com.google.code.facebookapi.schema.PagesGetInfoResponse)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.Boolean> photos_addTags( java.lang.Long photoId, java.util.Collection<com.google.code.facebookapi.PhotoTag> tags ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_addTags( photoId, tags );
        return (java.util.List<java.lang.Boolean>)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetAlbumsResponse photos_getAlbums( java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getAlbums( userId );
        return (com.google.code.facebookapi.schema.PhotosGetAlbumsResponse)parseCallResult( rawResponse );
    }

    public Object events_get( java.lang.Long userId, java.util.Collection<java.lang.Long> eventIds, java.lang.Long startTime, java.lang.Long endTime, java.lang.String rsvp_status ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.events_get( userId, eventIds, startTime, endTime, rsvp_status );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse application_getPublicInfoByCanvasName( java.lang.String applicationCanvas ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.application_getPublicInfoByCanvasName( applicationCanvas );
        return (com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetAlbumsResponse photos_getAlbums( java.lang.Long userId, java.util.Collection<java.lang.Long> albumIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getAlbums( userId, albumIds );
        return (com.google.code.facebookapi.schema.PhotosGetAlbumsResponse)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.Long> notifications_sendEmail( java.util.Collection<java.lang.Long> recipients, java.lang.CharSequence subject, java.lang.CharSequence email, java.lang.CharSequence fbml ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendEmail( recipients, subject, email, fbml );
        return (java.util.List<java.lang.Long>)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.io.File photo, java.lang.String caption, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( photo, caption, albumId );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.Long> notifications_sendFbmlEmail( java.util.Collection<java.lang.Long> recipients, java.lang.String subject, java.lang.String fbml ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendFbmlEmail( recipients, subject, fbml );
        return (java.util.List<java.lang.Long>)parseCallResult( rawResponse );
    }

    public Object fql_query( java.lang.CharSequence query ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.fql_query( query );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetResponse photos_get( java.lang.Long subjId, java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_get( subjId, photoIds );
        return (com.google.code.facebookapi.schema.PhotosGetResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse application_getPublicInfoByApiKey( java.lang.String applicationKey ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.application_getPublicInfoByApiKey( applicationKey );
        return (com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.AdminGetMetricsResponse admin_getMetrics( java.util.Set<com.google.code.facebookapi.Metric> metrics, java.util.Date start, java.util.Date end, long period ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
        return (com.google.code.facebookapi.schema.AdminGetMetricsResponse)parseCallResult( rawResponse );
    }

    public Object feed_getRegisteredTemplateBundleByID( java.lang.Long id ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.feed_getRegisteredTemplateBundleByID( id );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.io.File photo, java.lang.Long albumId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( photo, albumId );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.lang.Long userId, java.io.File photo ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( userId, photo );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.DataGetObjectsResponse data_getObjects( java.util.Collection<java.lang.Long> objectIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getObjects( objectIds );
        return (com.google.code.facebookapi.schema.DataGetObjectsResponse)parseCallResult( rawResponse );
    }

    public Object profile_getInfo( java.lang.Long userId ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getInfo( userId );
        return (Object)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.FriendsGetResponse friends_get( java.lang.Long uid ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.friends_get( uid );
        return (com.google.code.facebookapi.schema.FriendsGetResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetTagsResponse photos_getTags( java.util.Collection<java.lang.Long> photoIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getTags( photoIds );
        return (com.google.code.facebookapi.schema.PhotosGetTagsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PhotosGetAlbumsResponse photos_getAlbums( java.util.Collection<java.lang.Long> albumIds ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_getAlbums( albumIds );
        return (com.google.code.facebookapi.schema.PhotosGetAlbumsResponse)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.DataGetCookiesResponse data_getCookies(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.data_getCookies(  );
        return (com.google.code.facebookapi.schema.DataGetCookiesResponse)parseCallResult( rawResponse );
    }

    public java.lang.String profile_getFBML(  ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getFBML(  );
        return (java.lang.String)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.Photo photos_upload( java.io.File photo ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.photos_upload( photo );
        return (com.google.code.facebookapi.schema.Photo)parseCallResult( rawResponse );
    }

    public com.google.code.facebookapi.schema.PagesGetInfoResponse pages_getInfo( java.util.Collection<java.lang.Long> pageIds, java.util.EnumSet<com.google.code.facebookapi.PageProfileField> fields ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.pages_getInfo( pageIds, fields );
        return (com.google.code.facebookapi.schema.PagesGetInfoResponse)parseCallResult( rawResponse );
    }

    public java.util.List<java.lang.Long> notifications_sendFbmlEmailToCurrentUser( java.lang.String subject, java.lang.String fbml ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.notifications_sendFbmlEmailToCurrentUser( subject, fbml );
        return (java.util.List<java.lang.Long>)parseCallResult( rawResponse );
    }

    public Object profile_getInfoOptions( java.lang.String field ) throws com.google.code.facebookapi.FacebookException {
        client.setResponseFormat("xml");
        Object rawResponse = client.profile_getInfoOptions( field );
        return (Object)parseCallResult( rawResponse );
    }

}
