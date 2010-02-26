package com.google.code.facebookapi;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 * Covers all the void return type methods where we don't care which return type we're asking Facebook to provide.
 * 
 * All methods in this class must begin "public void". Doing this saves us repeating all the delegation in each of the XML, JSON and JAXB adapters.
 */
public abstract class BaseAdapter {

	protected final String responseFormat;
	protected final ExtensibleClient client;

	protected BaseAdapter( String responseFormat, ExtensibleClient client ) {
		this.responseFormat = responseFormat;
		this.client = client;
		if ( !StringUtils.equals( client.getResponseFormat(), responseFormat ) ) {
			throw new IllegalStateException( String.format( "Given ExtensibleClient (%s) does not support desired responseFormat (%s)", client.getResponseFormat(),
					responseFormat ) );
		}
	}

	protected ExtensibleClient getClient() {
		return client;
	}

	public String getApiKey() {
		return getClient().getApiKey();
	}

	public String getSecret() {
		return getClient().getSecret();
	}

	public String getRawResponse() {
		return client.getRawResponse();
	}

	public void beginBatch() {
		getClient().beginBatch();
	}

	public void beginPermissionsMode( String apiKey ) {
		getClient().beginPermissionsMode( apiKey );
	}

	public void data_createObjectType( String name ) throws FacebookException {
		getClient().data_createObjectType( name );
	}

	public void data_defineAssociation( String associationName, AssociationType associationType, AssociationInfo associationInfo1, AssociationInfo associationInfo2,
			String inverseName ) throws FacebookException {
		getClient().data_defineAssociation( associationName, associationType, associationInfo1, associationInfo2, inverseName );
	}

	public void data_defineObjectProperty( String objectType, String propertyName, PropertyType propertyType ) throws FacebookException {
		getClient().data_defineObjectProperty( objectType, propertyName, propertyType );
	}

	public void data_deleteObject( long objectId ) throws FacebookException {
		getClient().data_deleteObject( objectId );
	}

	public void data_deleteObjects( Collection<Long> objectIds ) throws FacebookException {
		getClient().data_deleteObjects( objectIds );
	}

	public void data_dropObjectType( String objectType ) throws FacebookException {
		getClient().data_dropObjectType( objectType );
	}

	public void data_removeAssociatedObjects( String associationName, long objectId ) throws FacebookException {
		getClient().data_removeAssociatedObjects( associationName, objectId );
	}

	public void data_removeAssociation( String associationName, long object1Id, long object2Id ) throws FacebookException {
		getClient().data_removeAssociation( associationName, object1Id, object2Id );
	}

	public void data_renameAssociation( String name, String newName, String newAlias1, String newAlias2 ) throws FacebookException {
		getClient().data_renameAssociation( name, newName, newAlias1, newAlias2 );
	}

	public void data_renameObjectProperty( String objectType, String propertyName, String newPropertyName ) throws FacebookException {
		getClient().data_renameObjectProperty( objectType, propertyName, newPropertyName );
	}

	public void data_renameObjectType( String name, String newName ) throws FacebookException {
		getClient().data_renameObjectType( name, newName );
	}

	public void data_setAssociation( String associationName, long object1Id, long object2Id, String data, Date associationTime ) throws FacebookException {
		getClient().data_setAssociation( associationName, object1Id, object2Id, data, associationTime );
	}

	public void data_setObjectProperty( long objectId, String propertyName, String value ) throws FacebookException {
		getClient().data_setObjectProperty( objectId, propertyName, value );
	}

	public void data_setUserPreference( int prefId, String value ) throws FacebookException {
		getClient().data_setUserPreference( prefId, value );
	}

	public void data_setUserPreferences( Map<Integer,String> values, boolean replace ) throws FacebookException {
		getClient().data_setUserPreferences( values, replace );
	}

	public void data_undefineAssociation( String name ) throws FacebookException {
		getClient().data_undefineAssociation( name );
	}

	public void data_undefineObjectProperty( String objectType, String propertyName ) throws FacebookException {
		getClient().data_undefineObjectProperty( objectType, propertyName );
	}

	public void data_updateObject( long objectId, Map<String,String> properties, boolean replace ) throws FacebookException {
		getClient().data_updateObject( objectId, properties, replace );
	}

	public void endPermissionsMode() {
		getClient().endPermissionsMode();
	}

	public void fbml_deleteCustomTags( Collection<String> names ) throws FacebookException {
		getClient().fbml_deleteCustomTags( names );
	}

	public void fbml_registerCustomTags( Collection<JSONObject> tags ) throws FacebookException {
		getClient().fbml_registerCustomTags( tags );
	}

	@Deprecated
	public void profile_setInfo( Long userId, String title, boolean textOnly, List<ProfileInfoField> fields ) throws FacebookException {
		getClient().profile_setInfo( userId, title, textOnly, fields );
	}

	@Deprecated
	public void profile_setInfoOptions( ProfileInfoField field ) throws FacebookException {
		getClient().profile_setInfoOptions( field );
	}

	@Deprecated
	public void setCacheAppUser( Boolean appUser ) {
		getClient().setCacheAppUser( appUser );
	}

	public void setCacheSession( String cacheSessionKey, Long cacheUserId, Long cacheSessionExpires ) {
		getClient().setCacheSession( cacheSessionKey, cacheUserId, cacheSessionExpires );
	}

	public URL getServerUrl() {
		return getClient().getServerUrl();
	}

	public void setServerUrl( URL url ) {
		getClient().setServerUrl( url );
	}

	public void setServerUrl( String url ) {
		getClient().setServerUrl( url );
	}

	@Deprecated
	public int getConnectTimeout() {
		return getClient().getConnectTimeout();
	}

	@Deprecated
	public void setConnectTimeout( int connectTimeout ) {
		getClient().setConnectTimeout( connectTimeout );
	}

	@Deprecated
	public int getReadTimeout() {
		return getClient().getReadTimeout();
	}

	@Deprecated
	public void setReadTimeout( int readTimeout ) {
		getClient().setReadTimeout( readTimeout );
	}

	public String getResponseFormat() {
		return getClient().getResponseFormat();
	}

	public void sms_sendMessage( Long userId, CharSequence message ) throws FacebookException {
		getClient().sms_sendMessage( userId, message );
	}

	public void setCommunicationStrategy( CommunicationStrategy communicationStrategy ) {
		getClient().setCommunicationStrategy( communicationStrategy );
	}
}
