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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.code.facebookapi.schema.AdminGetMetricsResponse;
import com.google.code.facebookapi.schema.FacebookApiException;
import com.google.code.facebookapi.schema.FriendsGetResponse;
import com.google.code.facebookapi.schema.Listing;
import com.google.code.facebookapi.schema.MarketplaceGetListingsResponse;
import com.google.code.facebookapi.schema.MarketplaceGetSubCategoriesResponse;
import com.google.code.facebookapi.schema.MarketplaceSearchResponse;
import com.google.code.facebookapi.schema.SessionInfo;

/**
 * A FacebookRestClient that JAXB response objects. This means results from calls to the Facebook API are returned as XML and transformed into JAXB Java objects.
 */
public class FacebookJaxbRestClient extends SpecificReturnTypeAdapter implements IFacebookRestClient<Object> {

	protected static Log log = LogFactory.getLog( FacebookJaxbRestClient.class );

	// used so that executeBatch can return the correct types in its list, without killing efficiency.
	private static final Map<FacebookMethod,String> RETURN_TYPES;
	static {
		RETURN_TYPES = new HashMap<FacebookMethod,String>();
		Method[] candidates = FacebookJaxbRestClient.class.getMethods();
		// this loop is inefficient, but it only executes once per JVM, so it doesn't really matter
		for ( FacebookMethod method : EnumSet.allOf( FacebookMethod.class ) ) {
			String name = method.methodName();
			name = name.substring( name.indexOf( "." ) + 1 );
			name = name.replace( ".", "_" );
			for ( Method candidate : candidates ) {
				if ( candidate.getName().equalsIgnoreCase( name ) ) {
					String typeName = candidate.getReturnType().getName().toLowerCase();
					// possible types are Document, String, Boolean, Integer, Long, void
					if ( typeName.indexOf( "object" ) != -1 ) {
						RETURN_TYPES.put( method, "default" );
					} else if ( typeName.indexOf( "string" ) != -1 ) {
						RETURN_TYPES.put( method, "string" );
					} else if ( typeName.indexOf( "bool" ) != -1 ) {
						RETURN_TYPES.put( method, "bool" );
					} else if ( typeName.indexOf( "long" ) != -1 ) {
						RETURN_TYPES.put( method, "long" );
					} else if ( typeName.indexOf( "int" ) != -1 ) {
						RETURN_TYPES.put( method, "int" );
					} else if ( ( typeName.indexOf( "applicationpropertyset" ) != -1 ) || ( typeName.indexOf( "list" ) != -1 ) || ( typeName.indexOf( "url" ) != -1 )
							|| ( typeName.indexOf( "map" ) != -1 ) || ( typeName.indexOf( "object" ) != -1 ) ) {
						// we don't autobox these for now, the user can parse them on their own
						RETURN_TYPES.put( method, "default" );
					} else {
						RETURN_TYPES.put( method, "void" );
					}
					break;
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
	
	/*
	public Object getResponsePOJO() {
		if ( JAXB_CONTEXT == null ) {
			return null;
		}
		if ( ( client.getResponseFormat() != null ) && ( !"xml".equalsIgnoreCase( getResponseFormat() ) ) ) {
			// JAXB will not work with JSON
			throw new RuntimeException( "You can only generate a response POJO when using XML formatted API responses! JSON users go elsewhere!" );
		}
		try {
			Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
			return unmarshaller.unmarshal( new ByteArrayInputStream( rawResponse.getBytes( "UTF-8" ) ) );
		}
		catch ( Exception ex ) {
			throw runtimeException( ex );
		}
	}
	*/
	
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
	protected Object parseCallResult( String rawResponse ) throws FacebookException {
		log.debug( "Facebook response:  " + rawResponse );
		Object res = getResponsePOJO();
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

	public List<Listing> marketplace_getListings( List<Long> listingIds, List<Long> uids ) throws FacebookException {
		MarketplaceGetListingsResponse resp = (MarketplaceGetListingsResponse) marketplace_getListings( listingIds, uids );
		return resp.getListing();
	}

	public List<String> marketplace_getSubCategories() throws FacebookException {
		MarketplaceGetSubCategoriesResponse resp = (MarketplaceGetSubCategoriesResponse) marketplace_getSubCategories( null );
		return resp.getMarketplaceSubcategory();
	}

	public List<Listing> marketplace_search( MarketListingCategory category, MarketListingSubcategory subcategory, String searchTerm ) throws FacebookException {
		MarketplaceSearchResponse resp = (MarketplaceSearchResponse) marketplace_search( category.getName(), subcategory.getName(), searchTerm );
		return resp.getListing();
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
		List<BatchQuery> queries = new ArrayList<BatchQuery>();
		Collections.copy( queries, client.getQueries() );
		
		List<? extends Object> clientResults = client.executeBatch( serial );
		
		List<Object> result = new ArrayList<Object>();
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
			int outerBatchCount = 0;
			
			for(Object clientResult : clientResults) {
				Document doc = builder.parse( new InputSource(clientResult.toString()) );
				NodeList responses = doc.getElementsByTagName( "batch_run_response_elt" );
				for ( int count = 0; count < responses.getLength(); count++ ) {
					String response = extractNodeString( responses.item( count ) );
					try {
						Object pojo = parseCallResult( response );
						String type = RETURN_TYPES.get( queries.get( outerBatchCount++ ).getMethod() );
						// possible types are document, string, bool, int, long, void
						if ( type.equals( "default" ) ) {
							result.add( pojo );
						} else if ( type.equals( "string" ) ) {
							result.add( extractString( pojo ) );
						} else if ( type.equals( "bool" ) ) {
							result.add( extractBoolean( pojo ) );
						} else if ( type.equals( "int" ) ) {
							result.add( extractInt( pojo ) );
						} else if ( type.equals( "long" ) ) {
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
		return (AdminGetMetricsResponse)parseCallResult( (String)rawResponse );
	}
	public Object admin_getDailyMetrics( Set<Metric> metrics, long start, long end ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object admin_getMetrics( Set<Metric> metrics, Date start, Date end, long period ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object admin_getMetrics( Set<Metric> metrics, long start, long end, long period ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object application_getPublicInfo( Long applicationId, String applicationKey, String applicationCanvas ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object application_getPublicInfoByApiKey( String applicationKey ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object application_getPublicInfoByCanvasName( String applicationCanvas ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object application_getPublicInfoById( Long applicationId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object batch_run( String methods, boolean serial ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object connect_registerUsers( Collection<Map<String,String>> accounts ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object connect_unregisterUsers( Collection<String> email_hashes ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getAssociationDefinition( String associationName ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getAssociationDefinitions() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getCookies() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getCookies( Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getCookies( String name ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getCookies( Long userId, CharSequence name ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getObject( long objectId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getObjectProperty( long objectId, String propertyName ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getObjectType( String objectType ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getObjectTypes() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getObjects( Collection<Long> objectIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object data_getUserPreferences() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime, String rsvp_status ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object events_getMembers( Long eventId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object feed_getRegisteredTemplateBundleByID( Long id ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object feed_getRegisteredTemplateBundles() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object fql_query( CharSequence query ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object friends_areFriends( long userId1, long userId2 ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object friends_areFriends( Collection<Long> userIds1, Collection<Long> userIds2 ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object friends_get( Long uid ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object friends_getAppUsers() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object friends_getList( Long friendListId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object friends_getLists() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object getResponsePOJO() {
		// TODO Auto-generated method stub
		return null;
	}
	public Object groups_get( Long userId, Collection<Long> groupIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object groups_getMembers( Number groupId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object marketplace_getCategoriesObject() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object marketplace_getListings( Collection<Long> listingIds, Collection<Long> userIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object marketplace_getSubCategories( CharSequence category ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object marketplace_search( CharSequence category, CharSequence subCategory, CharSequence query ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object notifications_get() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object pages_getInfo( Collection<Long> pageIds, EnumSet<PageProfileField> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object pages_getInfo( Collection<Long> pageIds, Set<CharSequence> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object pages_getInfo( Long userId, EnumSet<PageProfileField> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object pages_getInfo( Long userId, Set<CharSequence> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object permissions_checkAvailableApiAccess( String apiKey ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object permissions_checkGrantedApiAccess( String apiKey ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_addTags( Long photoId, Collection<PhotoTag> tags ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_addTags( Long photoId, Collection<PhotoTag> tags, Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_createAlbum( String albumName ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_createAlbum( String name, String description, String location ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_createAlbum( String albumName, Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_createAlbum( String name, String description, String location, Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_get( Long subjId, Long albumId, Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_get( Long subjId, Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_get( Long subjId, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_get( Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_get( Long subjId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_getAlbums( Long userId, Collection<Long> albumIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_getAlbums( Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_getAlbums( Collection<Long> albumIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_getByAlbum( Long albumId, Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_getByAlbum( Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_getTags( Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( File photo ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( File photo, String caption ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( File photo, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( File photo, String caption, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( Long userId, File photo ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( Long userId, File photo, String caption ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( Long userId, File photo, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( Long userId, File photo, String caption, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object photos_upload( Long userId, String caption, Long albumId, String fileName, InputStream fileStream ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object profile_getFBML() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object profile_getFBML( Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object profile_getFBML( int type ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object profile_getFBML( int type, Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object profile_getInfo( Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object profile_getInfoOptions( String field ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object users_getInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object users_getInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object users_getStandardInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
	public Object users_getStandardInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}
}
