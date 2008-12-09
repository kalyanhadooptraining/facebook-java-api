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
package com.google.code.facebookapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.code.facebookapi.schema.AdminGetMetricsResponse;
import com.google.code.facebookapi.schema.Album;
import com.google.code.facebookapi.schema.ApplicationGetPublicInfoResponse;
import com.google.code.facebookapi.schema.ConnectRegisterUsersResponse;
import com.google.code.facebookapi.schema.ConnectUnregisterUsersResponse;
import com.google.code.facebookapi.schema.DataGetAssociationDefinitionsResponse;
import com.google.code.facebookapi.schema.DataGetCookiesResponse;
import com.google.code.facebookapi.schema.DataGetObjectTypeResponse;
import com.google.code.facebookapi.schema.DataGetObjectTypesResponse;
import com.google.code.facebookapi.schema.DataGetObjectsResponse;
import com.google.code.facebookapi.schema.DataGetUserPreferencesResponse;
import com.google.code.facebookapi.schema.EventsGetResponse;
import com.google.code.facebookapi.schema.FacebookApiException;
import com.google.code.facebookapi.schema.FeedGetRegisteredTemplateBundlesResponse;
import com.google.code.facebookapi.schema.FriendsAreFriendsResponse;
import com.google.code.facebookapi.schema.FriendsGetAppUsersResponse;
import com.google.code.facebookapi.schema.FriendsGetListsResponse;
import com.google.code.facebookapi.schema.FriendsGetResponse;
import com.google.code.facebookapi.schema.GroupsGetResponse;
import com.google.code.facebookapi.schema.Listing;
import com.google.code.facebookapi.schema.MarketplaceGetCategoriesResponse;
import com.google.code.facebookapi.schema.MarketplaceGetListingsResponse;
import com.google.code.facebookapi.schema.MarketplaceGetSubCategoriesResponse;
import com.google.code.facebookapi.schema.MarketplaceSearchResponse;
import com.google.code.facebookapi.schema.PagesGetInfoResponse;
import com.google.code.facebookapi.schema.PermissionsCheckAvailableApiAccessResponse;
import com.google.code.facebookapi.schema.PermissionsCheckGrantedApiAccessResponse;
import com.google.code.facebookapi.schema.Photo;
import com.google.code.facebookapi.schema.PhotosGetAlbumsResponse;
import com.google.code.facebookapi.schema.PhotosGetResponse;
import com.google.code.facebookapi.schema.PhotosGetTagsResponse;
import com.google.code.facebookapi.schema.ProfileGetInfoResponse;
import com.google.code.facebookapi.schema.UsersGetInfoResponse;
import com.google.code.facebookapi.schema.UsersGetStandardInfoResponse;

/**
 * A FacebookRestClient that JAXB response objects. This means results from calls to the Facebook API are returned as XML and transformed into JAXB Java objects.
 */
public class FacebookJaxbRestClient extends SpecificReturnTypeAdapter implements IFacebookRestClient<Object> {

	protected static Log log = LogFactory.getLog( FacebookJaxbRestClient.class );

	// used so that executeBatch can return the correct types in its list, without killing efficiency.
	private static final Map<FacebookMethod,Class> RETURN_TYPES;
	static {
		RETURN_TYPES = new HashMap<FacebookMethod,Class>();
		Method[] candidates = FacebookJaxbRestClient.class.getMethods();
		// this loop is inefficient, but it only executes once per JVM, so it doesn't really matter
		for ( FacebookMethod method : EnumSet.allOf( FacebookMethod.class ) ) {
			String name = method.methodName();
			name = name.substring( name.indexOf( "." ) + 1 );
			name = name.replace( ".", "_" );
			for ( Method candidate : candidates ) {
				if ( candidate.getName().equalsIgnoreCase( name ) ) {
					Class returnType = candidate.getReturnType();
					RETURN_TYPES.put(method, returnType);
				}
			}
		}
	}

	private ExtensibleClient client;
	public ExtensibleClient getClient() {
		return client;
	}
	public void setClient(ExtensibleClient client) {
		this.client = client;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 */
	public FacebookJaxbRestClient( String apiKey, String secret ) {
		super( "xml" );
		client = new ExtensibleClient( apiKey, secret );
		initJaxbSupport();
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookJaxbRestClient( String apiKey, String secret, int connectionTimeout ) {
		super( "xml" );
		client = new ExtensibleClient( apiKey, secret, connectionTimeout );
		initJaxbSupport();
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 */
	public FacebookJaxbRestClient( String apiKey, String secret, String sessionKey ) {
		super( "xml" );
		client = new ExtensibleClient( apiKey, secret, sessionKey );
		initJaxbSupport();
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookJaxbRestClient( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		super( "xml" );
		client = new ExtensibleClient( apiKey, secret, sessionKey, connectionTimeout );
		initJaxbSupport();
	}


	/**
	 * Constructor.
	 * 
	 * @param serverAddr
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * 
	 * @throws MalformedURLException
	 *             if you specify an invalid URL
	 */
	public FacebookJaxbRestClient( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		super( "xml" );
		client = new ExtensibleClient( serverAddr, apiKey, secret, sessionKey );
		initJaxbSupport();
	}

	/**
	 * Constructor.
	 * 
	 * @param serverAddr
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 * 
	 * @throws MalformedURLException
	 *             if you specify an invalid URL
	 */
	public FacebookJaxbRestClient( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		super( "xml" );
		client = new ExtensibleClient( serverAddr, apiKey, secret, sessionKey, connectionTimeout );
		initJaxbSupport();
	}

	/**
	 * Constructor.
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 */
	public FacebookJaxbRestClient( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		super( "xml" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey );
		initJaxbSupport();
	}

	/**
	 * Constructor.
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookJaxbRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		super( "xml" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 );
		initJaxbSupport();
	}

	/**
	 * Constructor.
	 * 
	 * @param serverUrl
	 *            the URL of the Facebook API server to use
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 * @param connectionTimeout
	 *            the connection timeout to apply when making API requests to Facebook, in milliseconds
	 * @param readTimeout
	 *            the read timeout to apply when making API requests to Facebook, in milliseconds
	 */
	public FacebookJaxbRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
		super( "xml" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout );
		initJaxbSupport();
	}
	
	protected static JAXBContext JAXB_CONTEXT;
	
	public static void initJaxbSupport() {
		if ( JAXB_CONTEXT == null ) {
			try {
				JAXB_CONTEXT = JAXBContext.newInstance( "com.google.code.facebookapi.schema" );
			}
			catch ( JAXBException ex ) {
				log.error( "MalformedURLException: " + ex.getMessage(), ex );
			}
		}
	}
	
	public Object getResponsePOJO( String rawResponse ) {
		if ( JAXB_CONTEXT == null ) {
			return null;
		}
		/*
		if ( ( client.getResponseFormat() != null ) && ( !"xml".equalsIgnoreCase( getResponseFormat() ) ) ) {
			// JAXB will not work with JSON
			throw new RuntimeException( "You can only generate a response POJO when using XML formatted API responses! JSON users go elsewhere!" );
		}
		*/
		try {
			Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
			return unmarshaller.unmarshal( new StringReader( rawResponse ) );
		}
		catch ( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}
	
	public JAXBContext getJaxbContext() {
		return JAXB_CONTEXT;
	}

	private String parse(String val) {
		String xml = val;
		if ( ( xml == null ) || ( "".equals( xml ) ) ) {
			return null;
		}
		if ( !xml.contains( "</" ) ) {
			return null;
		}
		xml = xml.substring( 0, xml.indexOf( "</" ) );
		xml = xml.substring( xml.lastIndexOf( ">" ) + 1 );
		return xml;
	}

	/**
	 * Extracts a String from a result consisting entirely of a String.
	 * 
	 * @param val
	 * @return the String
	 */
	public String extractString( Object val ) {
		return parse((String)val);
	}

	public FriendsGetResponse friends_get() throws FacebookException {
		if ( client.isBatchMode() ) {
			client.friends_get();
			return null;
		}
		if ( client.getCacheFriendsList() == null ) {
			return (FriendsGetResponse) parseCallResult( (String)client.friends_get() );
		}
		return toFriendsGetResponse( client.getCacheFriendsList() );
	}

	public FriendsGetResponse getCacheFriendsList() {
		return toFriendsGetResponse( client.getCacheFriendsList() );
	}

	public void setCacheFriendsList( List<Long> ids ) {
		client.setCacheFriendsList( ids );
	}

	public static FriendsGetResponse toFriendsGetResponse( List<Long> ids ) {
		FriendsGetResponse out = new FriendsGetResponse();
		out.setList( true );
		out.getUid().addAll( ids );
		return out;
	}

	/**
	 * Call this function to retrieve the session information after your user has logged in.
	 * 
	 * @param authToken
	 *            the token returned by auth_createToken or passed back to your callback_url.
	 */
	public String auth_getSession( String authToken ) throws FacebookException {
		return client.auth_getSession( authToken );
	}

	/**
	 * Parses the result of an API call from XML into JAXB Objects.
	 * 
	 * @param data
	 *            an InputStream with the results of a request to the Facebook servers
	 * @param method
	 *            the method
	 * @return a JAXB Object
	 * @throws FacebookException
	 *             if <code>data</code> represents an error
	 * @throws IOException
	 *             if <code>data</code> is not readable
	 */
	protected Object parseCallResult( Object rawResponse ) throws FacebookException {
		log.debug( "Facebook response:  " + rawResponse );
		Object res = getResponsePOJO( (String)rawResponse );
		if ( res instanceof FacebookApiException ) {
			FacebookApiException error = (FacebookApiException) res;
			int errorCode = error.getErrorCode();
			String message = error.getErrorMsg();
			throw new FacebookException( errorCode, message );
		} else if ( res instanceof JAXBElement ) {
			JAXBElement jbe = (JAXBElement) res;
			if ( FacebookApiException.class.equals( jbe.getDeclaredType() ) ) {
				FacebookApiException error = (FacebookApiException) jbe.getValue();
				int errorCode = error.getErrorCode();
				String message = error.getErrorMsg();
				throw new FacebookException( errorCode, message );
			}
		}
		return res;
	}

	/**
	 * Extracts a URL from a result that consists of a URL only. For JSON, that result is simply a String.
	 * 
	 * @param url
	 * @return the URL
	 */
	protected URL extractURL( Object url ) throws IOException {
		String result = parse((String)url);
		if ( result != null ) {
			return new URL( result );
		}
		return null;
	}

	/**
	 * Extracts an Integer from a result that consists of an Integer only.
	 * 
	 * @param val
	 * @return the Integer
	 */
	protected int extractInt( Object val ) {
		try {
			return Integer.parseInt( parse((String)val) );
		}
		catch ( Exception cce ) {
			return 0;
		}
	}

	/**
	 * Extracts a Boolean from a result that consists of a Boolean only.
	 * 
	 * @param val
	 * @return the Boolean
	 */
	protected boolean extractBoolean( Object val ) {
		String result = parse((String)val);
		if ( ( "1".equals( result ) ) || ( "true".equalsIgnoreCase( result ) ) ) {
			return true;
		}
		return false;
	}

	/**
	 * Extracts a Long from a result that consists of an Long only.
	 * 
	 * @param val
	 * @return the Integer
	 */
	protected Long extractLong( Object val ) {
		try {
			return Long.parseLong( parse((String)val) );
		}
		catch ( Exception cce ) {
			return 0l;
		}
	}

	public String admin_getAppPropertiesAsString( Collection<ApplicationProperty> properties ) throws FacebookException {
		return client.admin_getAppPropertiesAsString( properties );
	}

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
	public List<? extends Object> executeBatch( boolean serial ) throws FacebookException {
		client.setResponseFormat( "xml" );
		//Take a copy of the queries being run so that we can associate them
		//with the correct return type later.
		List<BatchQuery> queries = new ArrayList<BatchQuery>(client.getQueries());
		
		List<String> clientResults = client.executeBatch( serial );
		
		List<Object> result = new ArrayList<Object>();
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
			int outerBatchCount = 0;
			
			for(String clientResult : clientResults) {
				Document doc = builder.parse( new InputSource( new StringReader( clientResult ) ) );
				NodeList responses = doc.getElementsByTagName( "batch_run_response_elt" );
				for ( int count = 0; count < responses.getLength(); count++ ) {
					String response = extractNodeString( responses.item( count ) );
					try {
						Object pojo = parseCallResult( response );
						Class type = RETURN_TYPES.get( queries.get( outerBatchCount++ ).getMethod() );
						if(!type.isPrimitive()) {
							result.add( pojo );
						} else if ( type.equals( String.class ) ) {
							result.add( extractString( pojo ) );
						} else if ( type.equals( Boolean.class ) ) {
							result.add( extractBoolean( pojo ) );
						} else if ( type.equals( Integer.class ) ) {
							result.add( extractInt( pojo ) );
						} else if ( type.equals( Long.class ) ) {
							result.add( (long) extractLong( pojo ) );
						} else {
							// void
							result.add( null );
						}
					}
					catch ( Exception e ) {
						if ( result.size() < count + 1 ) {
							result.add( null );
						}
					}
				}
			}
		} catch(ParserConfigurationException ex) {
			throw new RuntimeException("Error parsing batch response", ex);
		}
		catch ( SAXException ex ) {
			throw new RuntimeException("Error parsing batch response", ex);
		}
		catch ( IOException ex ) {
			throw new RuntimeException("Error parsing batch response", ex);
		}

		return result;
	}

	public static String extractNodeString( Node d ) {
		if ( d == null ) {
			return null;
		}
		return d.getFirstChild().getTextContent();
	}
	
	public AdminGetMetricsResponse admin_getDailyMetrics( Set<Metric> metrics, Date start, Date end ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
		return (AdminGetMetricsResponse)parseCallResult( rawResponse );
	}
	public AdminGetMetricsResponse admin_getDailyMetrics( Set<Metric> metrics, long start, long end ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
		return (AdminGetMetricsResponse)parseCallResult( rawResponse );
	}
	public AdminGetMetricsResponse admin_getMetrics( Set<Metric> metrics, Date start, Date end, long period ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
		return (AdminGetMetricsResponse)parseCallResult( rawResponse );
	}
	public AdminGetMetricsResponse admin_getMetrics( Set<Metric> metrics, long start, long end, long period ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
		return (AdminGetMetricsResponse)parseCallResult( rawResponse );
	}
	public ApplicationGetPublicInfoResponse application_getPublicInfo( Long applicationId, String applicationKey, String applicationCanvas ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.application_getPublicInfo( applicationId, applicationKey, applicationCanvas );
		return (ApplicationGetPublicInfoResponse)parseCallResult( rawResponse );
	}
	public ApplicationGetPublicInfoResponse application_getPublicInfoByApiKey( String applicationKey ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.application_getPublicInfoByApiKey( applicationKey );
		return (ApplicationGetPublicInfoResponse)parseCallResult( rawResponse );
	}
	public ApplicationGetPublicInfoResponse application_getPublicInfoByCanvasName( String applicationCanvas ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.application_getPublicInfoByCanvasName( applicationCanvas );
		return (ApplicationGetPublicInfoResponse)parseCallResult( rawResponse );
	}
	public ApplicationGetPublicInfoResponse application_getPublicInfoById( Long applicationId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.application_getPublicInfoById( applicationId );
		return (ApplicationGetPublicInfoResponse)parseCallResult( rawResponse );
	}
	public Object batch_run( String methods, boolean serial ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.batch_run( methods, serial );
		return (Object)parseCallResult( rawResponse );
	}
	public ConnectRegisterUsersResponse connect_registerUsers( Collection<Map<String,String>> accounts ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.connect_registerUsers( accounts );
		return (ConnectRegisterUsersResponse)parseCallResult( rawResponse );
	}
	public ConnectUnregisterUsersResponse connect_unregisterUsers( Collection<String> email_hashes ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.connect_unregisterUsers( email_hashes );
		return (ConnectUnregisterUsersResponse)parseCallResult( rawResponse );
	}
	public DataGetAssociationDefinitionsResponse data_getAssociationDefinition( String associationName ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getAssociationDefinition( associationName );
		return (DataGetAssociationDefinitionsResponse)parseCallResult( rawResponse );
	}
	public DataGetAssociationDefinitionsResponse data_getAssociationDefinitions() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getAssociationDefinitions();
		return (DataGetAssociationDefinitionsResponse)parseCallResult( rawResponse );
	}
	public DataGetCookiesResponse data_getCookies() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getCookies();
		return (DataGetCookiesResponse)parseCallResult( rawResponse );
	}
	public DataGetCookiesResponse data_getCookies( Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getCookies( userId );
		return (DataGetCookiesResponse)parseCallResult( rawResponse );
	}
	public DataGetCookiesResponse data_getCookies( String name ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getCookies( name );
		return (DataGetCookiesResponse)parseCallResult( rawResponse );
	}
	public DataGetCookiesResponse data_getCookies( Long userId, CharSequence name ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getCookies( userId, name );
		return (DataGetCookiesResponse)parseCallResult( rawResponse );
	}
	public Object data_getObject( long objectId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObject( objectId );
		return (Object)parseCallResult( rawResponse );
	}
	public String data_getObjectProperty( long objectId, String propertyName ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObjectProperty( objectId, propertyName );
		return (String)parseCallResult( rawResponse );
	}
	public DataGetObjectTypeResponse data_getObjectType( String objectType ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObjectType( objectType );
		return (DataGetObjectTypeResponse)parseCallResult( rawResponse );
	}
	public DataGetObjectTypesResponse data_getObjectTypes() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObjectTypes();
		return (DataGetObjectTypesResponse)parseCallResult( rawResponse );
	}
	public DataGetObjectsResponse data_getObjects( Collection<Long> objectIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObjects( objectIds );
		return (DataGetObjectsResponse)parseCallResult( rawResponse );
	}
	public DataGetUserPreferencesResponse data_getUserPreferences() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getUserPreferences();
		return (DataGetUserPreferencesResponse)parseCallResult( rawResponse );		
	}
	public EventsGetResponse events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.events_get( userId, eventIds, startTime, endTime );
		return (EventsGetResponse)parseCallResult( rawResponse );
	}
	public EventsGetResponse events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime, String rsvp_status ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.events_get( userId, eventIds, startTime, endTime, rsvp_status );
		return (EventsGetResponse)parseCallResult( rawResponse );
	}
	public EventsGetResponse events_getMembers( Long eventId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.events_getMembers( eventId );
		return (EventsGetResponse)parseCallResult( rawResponse );
	}
	public FeedGetRegisteredTemplateBundlesResponse feed_getRegisteredTemplateBundleByID( Long id ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.feed_getRegisteredTemplateBundleByID( id );
		return (FeedGetRegisteredTemplateBundlesResponse)parseCallResult( rawResponse );
	}
	public FeedGetRegisteredTemplateBundlesResponse feed_getRegisteredTemplateBundles() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.feed_getRegisteredTemplateBundles();
		return (FeedGetRegisteredTemplateBundlesResponse)parseCallResult( rawResponse );
	}
	public Object fql_query( CharSequence query ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.fql_query( query );
		return parseCallResult( rawResponse );
	}
	public FriendsAreFriendsResponse friends_areFriends( long userId1, long userId2 ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_areFriends( userId1, userId2 );
		return (FriendsAreFriendsResponse)parseCallResult( rawResponse );
	}
	public FriendsAreFriendsResponse friends_areFriends( Collection<Long> userIds1, Collection<Long> userIds2 ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_areFriends( userIds1, userIds2 );
		return (FriendsAreFriendsResponse)parseCallResult( rawResponse );
	}
	public FriendsGetResponse friends_get( Long uid ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_get( uid );
		return (FriendsGetResponse)parseCallResult( rawResponse );
	}
	public FriendsGetAppUsersResponse friends_getAppUsers() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_getAppUsers();
		return (FriendsGetAppUsersResponse)parseCallResult( rawResponse );
	}
	public FriendsGetResponse friends_getList( Long friendListId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_getList( friendListId );
		return (FriendsGetResponse)parseCallResult( rawResponse );
	}
	public FriendsGetListsResponse friends_getLists() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_getLists();
		return (FriendsGetListsResponse)parseCallResult( rawResponse );
	}
	public GroupsGetResponse groups_get( Long userId, Collection<Long> groupIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.groups_get( userId, groupIds );
		return (GroupsGetResponse)parseCallResult( rawResponse );
	}
	public GroupsGetResponse groups_getMembers( Number groupId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.groups_getMembers( groupId );
		return (GroupsGetResponse)parseCallResult( rawResponse );
	}
	public MarketplaceGetCategoriesResponse marketplace_getCategoriesObject() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.marketplace_getCategoriesObject();
		return (MarketplaceGetCategoriesResponse)parseCallResult( rawResponse );
	}
	public MarketplaceGetListingsResponse marketplace_getListings( Collection<Long> listingIds, Collection<Long> userIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.marketplace_getListings( listingIds, userIds );
		return (MarketplaceGetListingsResponse)parseCallResult( rawResponse );
	}
	public MarketplaceGetSubCategoriesResponse marketplace_getSubCategories( CharSequence category ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.marketplace_getSubCategories( category );
		return (MarketplaceGetSubCategoriesResponse)parseCallResult( rawResponse );
	}
	public MarketplaceSearchResponse marketplace_search( CharSequence category, CharSequence subCategory, CharSequence query ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.marketplace_search( category, subCategory, query );
		return (MarketplaceSearchResponse)parseCallResult( rawResponse );
	}
	public Object notifications_get() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_get();
		return parseCallResult( rawResponse );
	}
	public List<Long> notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_sendEmail( recipients, subject, email, fbml );
		return (List<Long>)parseCallResult( rawResponse );
	}
	public Object notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_sendEmailToCurrentUser( subject, email, fbml );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_sendFbmlEmail( recipients, subject, fbml );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_sendFbmlEmailToCurrentUser( subject, fbml );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_sendTextEmail( recipients, subject, email );
		return parseCallResult( rawResponse );
	}
	public Object notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_sendTextEmailToCurrentUser( subject, email );
		return parseCallResult( rawResponse );
	}
	public PagesGetInfoResponse pages_getInfo( Collection<Long> pageIds, EnumSet<PageProfileField> fields ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.pages_getInfo( pageIds, fields );
		return (PagesGetInfoResponse)parseCallResult( rawResponse );
	}
	public PagesGetInfoResponse pages_getInfo( Collection<Long> pageIds, Set<CharSequence> fields ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.pages_getInfo( pageIds, fields );
		return (PagesGetInfoResponse)parseCallResult( rawResponse );
	}
	public PagesGetInfoResponse pages_getInfo( Long userId, EnumSet<PageProfileField> fields ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.pages_getInfo( userId, fields );
		return (PagesGetInfoResponse)parseCallResult( rawResponse );
	}
	public PagesGetInfoResponse pages_getInfo( Long userId, Set<CharSequence> fields ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.pages_getInfo( userId, fields );
		return (PagesGetInfoResponse)parseCallResult( rawResponse );
	}
	public PermissionsCheckAvailableApiAccessResponse permissions_checkAvailableApiAccess( String apiKey ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.permissions_checkAvailableApiAccess( apiKey );
		return (PermissionsCheckAvailableApiAccessResponse)parseCallResult( rawResponse );
	}
	public PermissionsCheckGrantedApiAccessResponse permissions_checkGrantedApiAccess( String apiKey ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.permissions_checkGrantedApiAccess( apiKey );
		return (PermissionsCheckGrantedApiAccessResponse)parseCallResult( rawResponse );
	}
	public Boolean photos_addTags( Long photoId, Collection<PhotoTag> tags ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_addTags( photoId, tags );
		return (Boolean)parseCallResult( rawResponse );
	}
	public Boolean photos_addTags( Long photoId, Collection<PhotoTag> tags, Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_addTags( photoId, tags, userId );
		return (Boolean)parseCallResult( rawResponse );
	}
	public Album photos_createAlbum( String albumName ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_createAlbum( albumName );
		return (Album)parseCallResult( rawResponse );
	}
	public Album photos_createAlbum( String name, String description, String location ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_createAlbum( name, description, location );
		return (Album)parseCallResult( rawResponse );
	}
	public Album photos_createAlbum( String albumName, Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_createAlbum( albumName, userId );
		return (Album)parseCallResult( rawResponse );
	}
	public Album photos_createAlbum( String name, String description, String location, Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_createAlbum( name, description, location, userId );
		return (Album)parseCallResult( rawResponse );
	}
	public PhotosGetResponse photos_get( Long subjId, Long albumId, Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_get( subjId, photoIds );
		return (PhotosGetResponse)parseCallResult( rawResponse );
	}
	public PhotosGetResponse photos_get( Long subjId, Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_get( subjId, photoIds );
		return (PhotosGetResponse)parseCallResult( rawResponse );
	}
	public PhotosGetResponse photos_get( Long subjId, Long albumId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_get( subjId, albumId );
		return (PhotosGetResponse)parseCallResult( rawResponse );
	}
	public PhotosGetResponse photos_get( Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_get( photoIds );
		return (PhotosGetResponse)parseCallResult( rawResponse );
	}
	public PhotosGetResponse photos_get( Long subjId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_get( subjId );
		return (PhotosGetResponse)parseCallResult( rawResponse );
	}
	public PhotosGetAlbumsResponse photos_getAlbums( Long userId, Collection<Long> albumIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_getAlbums( userId, albumIds );
		return (PhotosGetAlbumsResponse)parseCallResult( rawResponse );
	}
	public PhotosGetAlbumsResponse photos_getAlbums( Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_getAlbums( userId );
		return (PhotosGetAlbumsResponse)parseCallResult( rawResponse );
	}
	public PhotosGetAlbumsResponse photos_getAlbums( Collection<Long> albumIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_getAlbums( albumIds );
		return (PhotosGetAlbumsResponse)parseCallResult( rawResponse );
	}
	public PhotosGetResponse photos_getByAlbum( Long albumId, Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_getByAlbum( albumId, photoIds );
		return (PhotosGetResponse)parseCallResult( rawResponse );
	}
	public PhotosGetResponse photos_getByAlbum( Long albumId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_getByAlbum( albumId );
		return (PhotosGetResponse)parseCallResult( rawResponse );
	}
	public PhotosGetTagsResponse photos_getTags( Collection<Long> photoIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_getTags( photoIds );
		return (PhotosGetTagsResponse)parseCallResult( rawResponse );
	}
	public Photo photos_upload( File photo ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( photo );
		return (Photo)parseCallResult( rawResponse );
	}
	public Photo photos_upload( File photo, String caption ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( photo, caption );
		return (Photo)parseCallResult( rawResponse );
	}
	public Photo photos_upload( File photo, Long albumId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( photo, albumId );
		return (Photo)parseCallResult( rawResponse );
	}
	public Photo photos_upload( File photo, String caption, Long albumId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( photo, caption, albumId );
		return (Photo)parseCallResult( rawResponse );
	}
	public Photo photos_upload( Long userId, File photo ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( userId, photo );
		return (Photo)parseCallResult( rawResponse );
	}
	public Photo photos_upload( Long userId, File photo, String caption ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( userId, photo, caption );
		return (Photo)parseCallResult( rawResponse );
	}
	public Photo photos_upload( Long userId, File photo, Long albumId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( userId, photo, albumId );
		return (Photo)parseCallResult( rawResponse );
	}
	public Photo photos_upload( Long userId, File photo, String caption, Long albumId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( userId, photo, caption, albumId );
		return (Photo)parseCallResult( rawResponse );
	}
	public Photo photos_upload( Long userId, String caption, Long albumId, String fileName, InputStream fileStream ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_upload( userId, caption, albumId, fileName, fileStream );
		return (Photo)parseCallResult( rawResponse );
	}
	public String profile_getFBML() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.profile_getFBML();
		return (String)parseCallResult( rawResponse );
	}
	public String profile_getFBML( Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.profile_getFBML( userId );
		return (String)parseCallResult( rawResponse );
	}
	public String profile_getFBML( int type ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.profile_getFBML( type );
		return (String)parseCallResult( rawResponse );
	}
	public String profile_getFBML( int type, Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.profile_getFBML( type, userId );
		return (String)parseCallResult( rawResponse );
	}
	public String profile_getInfo( Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.profile_getInfo( userId );
		return (String)parseCallResult( rawResponse );
	}
	public ProfileGetInfoResponse profile_getInfoOptions( String field ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.profile_getInfoOptions( field );
		return (ProfileGetInfoResponse)parseCallResult( rawResponse );
	}
	public UsersGetInfoResponse users_getInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.users_getInfo( userIds, fields );
		return (UsersGetInfoResponse)parseCallResult( rawResponse );
	}
	public UsersGetInfoResponse users_getInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.users_getInfo( userIds, fields );
		return (UsersGetInfoResponse)parseCallResult( rawResponse );
	}
	public UsersGetStandardInfoResponse users_getStandardInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.users_getStandardInfo( userIds, fields );
		return (UsersGetStandardInfoResponse)parseCallResult( rawResponse );
	}
	public UsersGetStandardInfoResponse users_getStandardInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.users_getStandardInfo( userIds, fields );
		return (UsersGetStandardInfoResponse)parseCallResult( rawResponse );
	}
}
