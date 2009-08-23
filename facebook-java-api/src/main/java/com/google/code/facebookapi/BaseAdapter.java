package com.google.code.facebookapi;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * Covers all the void return type methods where we don't care which return type we're asking Facebook to provide.
 * 
 * All methods in this class must begin "public void". Doing this saves us repeating all the delegation in each of the XML, JSON and JAXB adapters.
 * 
 * @author dboden
 */
public abstract class BaseAdapter {

	private String responseFormat;

	protected BaseAdapter( String responseFormat ) {
		this.responseFormat = responseFormat;
	}

	protected abstract ExtensibleClient getClient();

	public String getApiKey() {
		return getClient().getApiKey();
	}

	public String getSecret() {
		return getClient().getSecret();
	}

	public void beginBatch() {
		getClient().setResponseFormat( responseFormat );
		getClient().beginBatch();
	}

	public void beginPermissionsMode( String apiKey ) {
		getClient().setResponseFormat( responseFormat );
		getClient().beginPermissionsMode( apiKey );
	}

	public void data_createObjectType( String name ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_createObjectType( name );
	}

	public void data_defineAssociation( String associationName, AssociationType associationType, AssociationInfo associationInfo1, AssociationInfo associationInfo2,
			String inverseName ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_defineAssociation( associationName, associationType, associationInfo1, associationInfo2, inverseName );
	}

	public void data_defineObjectProperty( String objectType, String propertyName, PropertyType propertyType ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_defineObjectProperty( objectType, propertyName, propertyType );
	}

	public void data_deleteObject( long objectId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_deleteObject( objectId );
	}

	public void data_deleteObjects( Collection<Long> objectIds ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_deleteObjects( objectIds );
	}

	public void data_dropObjectType( String objectType ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_dropObjectType( objectType );
	}

	public void data_removeAssociatedObjects( String associationName, long objectId ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_removeAssociatedObjects( associationName, objectId );
	}

	public void data_removeAssociation( String associationName, long object1Id, long object2Id ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_removeAssociation( associationName, object1Id, object2Id );
	}

	public void data_renameAssociation( String name, String newName, String newAlias1, String newAlias2 ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_renameAssociation( name, newName, newAlias1, newAlias2 );
	}

	public void data_renameObjectProperty( String objectType, String propertyName, String newPropertyName ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_renameObjectProperty( objectType, propertyName, newPropertyName );
	}

	public void data_renameObjectType( String name, String newName ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_renameObjectType( name, newName );
	}

	public void data_setAssociation( String associationName, long object1Id, long object2Id, String data, Date associationTime ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_setAssociation( associationName, object1Id, object2Id, data, associationTime );
	}

	public void data_setObjectProperty( long objectId, String propertyName, String value ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_setObjectProperty( objectId, propertyName, value );
	}

	public void data_setUserPreference( int prefId, String value ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_setUserPreference( prefId, value );
	}

	public void data_setUserPreferences( Map<Integer,String> values, boolean replace ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_setUserPreferences( values, replace );
	}

	public void data_undefineAssociation( String name ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_undefineAssociation( name );
	}

	public void data_undefineObjectProperty( String objectType, String propertyName ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_undefineObjectProperty( objectType, propertyName );
	}

	public void data_updateObject( long objectId, Map<String,String> properties, boolean replace ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().data_updateObject( objectId, properties, replace );
	}

	public void endPermissionsMode() {
		getClient().setResponseFormat( responseFormat );
		getClient().endPermissionsMode();
	}

	public void fbml_deleteCustomTags( Collection<String> names ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().fbml_deleteCustomTags( names );
	}

	public void fbml_registerCustomTags( Collection<JSONObject> tags ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().fbml_registerCustomTags( tags );
	}

	public void profile_setInfo( Long userId, String title, boolean textOnly, List<ProfileInfoField> fields ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().profile_setInfo( userId, title, textOnly, fields );
	}

	public void profile_setInfoOptions( ProfileInfoField field ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().profile_setInfoOptions( field );
	}

	public void setCacheFriendsList( List<Long> ids ) {
		getClient().setResponseFormat( responseFormat );
		getClient().setCacheFriendsList( ids );
	}

	@Deprecated
	public void setCacheAppAdded( Boolean appAdded ) {
		getClient().setResponseFormat( responseFormat );
		getClient().setCacheAppAdded( appAdded );
	}

	public void setCacheAppUser( Boolean appUser ) {
		getClient().setResponseFormat( responseFormat );
		getClient().setCacheAppUser( appUser );
	}

	public void setCacheSession( String cacheSessionKey, Long cacheUserId, Long cacheSessionExpires ) {
		getClient().setResponseFormat( responseFormat );
		getClient().setCacheSession( cacheSessionKey, cacheUserId, cacheSessionExpires );
	}

	public void setDefaultServerUrl( URL url ) {
		getClient().setResponseFormat( responseFormat );
		getClient().setDefaultServerUrl( url );
	}

	public void setServerUrl( String newUrl ) {
		getClient().setResponseFormat( responseFormat );
		getClient().setServerUrl( newUrl );
	}

	public void sms_sendMessage( Long userId, CharSequence message ) throws FacebookException {
		getClient().setResponseFormat( responseFormat );
		getClient().sms_sendMessage( userId, message );
	}

}
