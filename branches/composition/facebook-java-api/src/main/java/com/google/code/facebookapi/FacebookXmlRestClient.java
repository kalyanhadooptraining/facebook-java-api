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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A FacebookRestClient that uses the XML result format. This means results from calls to the Facebook API are returned as XML and transformed into instances of
 * {@link org.w3c.dom.Document}.
 */
public class FacebookXmlRestClient extends SpecificReturnTypeAdapter implements IFacebookRestClient<Document> {

	protected static Log log = LogFactory.getLog( FacebookXmlRestClient.class );

	// used so that executeBatch can return the correct types in its list, without killing efficiency.
	private static final Map<FacebookMethod,String> RETURN_TYPES;
	static {
		RETURN_TYPES = new HashMap<FacebookMethod,String>();
		Method[] candidates = FacebookXmlRestClient.class.getMethods();
		// this loop is inefficient, but it only executes once per JVM, so it doesn't really matter
		for ( FacebookMethod method : EnumSet.allOf( FacebookMethod.class ) ) {
			String name = method.methodName();
			name = name.substring( name.indexOf( "." ) + 1 );
			name = name.replace( ".", "_" );
			for ( Method candidate : candidates ) {
				if ( candidate.getName().equalsIgnoreCase( name ) ) {
					String typeName = candidate.getReturnType().getName().toLowerCase();
					// possible types are Document, String, Boolean, Integer, Long, void
					if ( typeName.indexOf( "document" ) != -1 ) {
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

	public FacebookXmlRestClient( String apiKey, String secret ) {
		super( "xml" );
		client = new ExtensibleClient( apiKey, secret );
	}

	public FacebookXmlRestClient( String apiKey, String secret, int connectionTimeout ) {
		super( "xml" );
		client = new ExtensibleClient( apiKey, secret, connectionTimeout );
	}

	public FacebookXmlRestClient( String apiKey, String secret, String sessionKey ) {
		super( "xml" );
		client = new ExtensibleClient( apiKey, secret, sessionKey );
	}

	public FacebookXmlRestClient( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		super( "xml" );
		client = new ExtensibleClient( apiKey, secret, sessionKey, connectionTimeout );
	}

	public FacebookXmlRestClient( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		super( "xml" );
		client = new ExtensibleClient( serverAddr, apiKey, secret, sessionKey );
	}

	public FacebookXmlRestClient( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		super( "xml" );
		client = new ExtensibleClient( serverAddr, apiKey, secret, sessionKey, connectionTimeout );
	}

	public FacebookXmlRestClient( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		super( "xml" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey );
	}

	public FacebookXmlRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		super( "xml" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 );
	}

	public FacebookXmlRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
		super( "xml" );
		client = new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout );
	}

	/**
	 * The response format in which results to FacebookMethod calls are returned
	 * 
	 * @return the format: either XML, JSON, or null (API default)
	 */
	public String getResponseFormat() {
		return "xml";
	}

	/**
	 * Extracts a String from a T consisting entirely of a String.
	 * 
	 * @return the String
	 */
	public static String extractString( Document d ) {
		if ( d == null ) {
			return null;
		}
		return d.getFirstChild().getTextContent();
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

	static Document parseCallResult( String rawResponse ) throws FacebookException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware( true );
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse( new InputSource(new StringReader(rawResponse)));
			doc.normalizeDocument();
			stripEmptyTextNodes( doc );
			//printDom( doc, method.methodName() + "| " );
			NodeList errors = doc.getElementsByTagName( ERROR_TAG );
			if ( errors.getLength() > 0 ) {
				int errorCode = Integer.parseInt( errors.item( 0 ).getFirstChild().getFirstChild().getTextContent() );
				String message = errors.item( 0 ).getFirstChild().getNextSibling().getTextContent();
				throw new FacebookException( errorCode, message );
			}
			return doc;
		}
		catch ( ParserConfigurationException ex ) {
			throw new RuntimeException( "Trouble configuring XML Parser", ex );
		}
		catch ( SAXException ex ) {
			throw new RuntimeException( "Trouble parsing XML from facebook", ex );
		}
		catch ( IOException ex ) {
			throw new RuntimeException( "Trouble parsing XML from facebook", ex );
		}
	}

	/**
	 * Extracts a URL from a document that consists of a URL only.
	 * 
	 * @param doc
	 * @return the URL
	 */
	static URL extractURL( Document doc ) throws IOException {
		if ( doc == null ) {
			return null;
		}
		String url = doc.getFirstChild().getTextContent();
		return ( null == url || "".equals( url ) ) ? null : new URL( url );
	}

	/**
	 * Extracts an Integer from a document that consists of an Integer only.
	 * 
	 * @param doc
	 * @return the Integer
	 */
	static int extractInt( Document doc ) {
		if ( doc == null ) {
			return 0;
		}
		return Integer.parseInt( doc.getFirstChild().getTextContent() );
	}

	/**
	 * Extracts a Long from a document that consists of a Long only.
	 * 
	 * @param doc
	 * @return the Long
	 */
	static Long extractLong( Document doc ) {
		if ( doc == null ) {
			return 0l;
		}
		return Long.parseLong( doc.getFirstChild().getTextContent() );
	}

	/**
	 * Hack...since DOM reads newlines as textnodes we want to strip out those nodes to make it easier to use the tree.
	 */
	private static void stripEmptyTextNodes( Node n ) {
		NodeList children = n.getChildNodes();
		int length = children.getLength();
		for ( int i = 0; i < length; i++ ) {
			Node c = children.item( i );
			if ( !c.hasChildNodes() && c.getNodeType() == Node.TEXT_NODE && c.getTextContent().trim().length() == 0 ) {
				n.removeChild( c );
				i-- ;
				length-- ;
				children = n.getChildNodes();
			} else {
				stripEmptyTextNodes( c );
			}
		}
	}

	/**
	 * Prints out the DOM tree.
	 */
	public void printDom( Node n, String prefix ) {
		if ( log.isDebugEnabled() ) {
			StringBuilder sb = new StringBuilder( "\n" );
			ExtensibleClient.printDom( n, prefix, sb );
			log.debug( sb.toString() );
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
		
		List<? extends Object> clientResults = client.executeBatch( serial );
		
		List<Object> result = new ArrayList<Object>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware( true );
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			int outerBatchCount = 0;
			
			for(Object clientResult : clientResults) {
				Document doc = builder.parse( new InputSource(clientResult.toString()) );
				NodeList responses = doc.getElementsByTagName( "batch_run_response_elt" );
				for ( int count = 0; count < responses.getLength(); count++ ) {
					String response = extractNodeString( responses.item( count ) );
					try {
						Document respDoc = builder.parse( new ByteArrayInputStream( response.getBytes( "UTF-8" ) ) );
						String type = RETURN_TYPES.get( queries.get( outerBatchCount++ ).getMethod() );
						// possible types are document, string, bool, int, long, void
						if ( type.equals( "default" ) ) {
							result.add( respDoc );
						} else if ( type.equals( "string" ) ) {
							result.add( extractString( respDoc ) );
						} else if ( type.equals( "bool" ) ) {
							result.add( extractBoolean( respDoc ) );
						} else if ( type.equals( "int" ) ) {
							result.add( extractInt( respDoc ) );
						} else if ( type.equals( "long" ) ) {
							result.add( (long) extractInt( respDoc ) );
						} else {
							// void
							result.add( null );
						}
					}
					catch ( Exception ignored ) {
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

	/**
	 * Extracts a Boolean from a result that consists of a Boolean only.
	 * 
	 * @param result
	 * @return the Boolean
	 */
	static boolean extractBoolean( Document result ) {
		if ( result == null ) {
			return false;
		}
		return 1 == extractInt( result );
	}
	
	public static String extractNodeString( Node d ) {
		if ( d == null ) {
			return null;
		}
		return d.getFirstChild().getTextContent();
	}

	/**
	 * Return the object's 'friendsList' property. This method does not call the Facebook API server.
	 * 
	 * @return the friends-list stored in the API client.
	 */
	public Document getCacheFriendsList() {
		return toFriendsGetResponse(client.getCacheFriendsList());
	}

	/**
	 * Set/override the list of friends stored in the client.
	 * 
	 * @param friendsList
	 *            the new list to use.
	 */
	public void setCacheFriendsList( List<Long> ids ) {
		client.setCacheFriendsList( ids );
	}

	public static Document toFriendsGetResponse( List<Long> ids ) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElementNS( "http://api.facebook.com/1.0/", "friends_get_response" );
			root.setAttributeNS( "http://api.facebook.com/1.0/", "friends_get_response", "http://api.facebook.com/1.0/ http://api.facebook.com/1.0/facebook.xsd" );
			root.setAttribute( "list", "true" );
			for ( Long id : ids ) {
				Element uid = doc.createElement( "uid" );
				uid.appendChild( doc.createTextNode( Long.toString( id ) ) );
				root.appendChild( uid );
			}
			doc.appendChild( root );
			return doc;
		}
		catch ( ParserConfigurationException ex ) {
			throw new RuntimeException( ex );
		}
	}

	public Document friends_get() throws FacebookException {
		if ( client.isBatchMode() ) {
			client.friends_get(); //Will return null
			return null;
		}
		if(client.getCacheFriendsList() == null) {
			client.setResponseFormat( "xml" );
			Object rawResponse = client.friends_get();
			return parseCallResult( rawResponse.toString() );
		}
		return getCacheFriendsList();
	}

	public Document admin_getDailyMetrics( Set<Metric> metrics, Date start, Date end ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
		return parseCallResult( (String)rawResponse );
	}

	public Document admin_getDailyMetrics( Set<Metric> metrics, long start, long end ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.admin_getDailyMetrics( metrics, start, end );
		return parseCallResult( (String)rawResponse );
	}

	public Document admin_getMetrics( Set<Metric> metrics, Date start, Date end, long period ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.admin_getMetrics( metrics, start, end, period);
		return parseCallResult( (String)rawResponse );
	}

	public Document admin_getMetrics( Set<Metric> metrics, long start, long end, long period ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.admin_getMetrics( metrics, start, end, period );
		return parseCallResult( (String)rawResponse );
	}

	public Document application_getPublicInfo( Long applicationId, String applicationKey, String applicationCanvas ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.application_getPublicInfo( applicationId, applicationKey, applicationCanvas );
		return parseCallResult( (String)rawResponse );
	}

	public Document application_getPublicInfoByApiKey( String applicationKey ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.application_getPublicInfoByApiKey( applicationKey );
		return parseCallResult( (String)rawResponse );
	}

	public Document application_getPublicInfoByCanvasName( String applicationCanvas ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.application_getPublicInfoByCanvasName( applicationCanvas );
		return parseCallResult( (String)rawResponse );
	}

	public Document application_getPublicInfoById( Long applicationId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.application_getPublicInfoById( applicationId );
		return parseCallResult( (String)rawResponse );
	}

	public Document batch_run( String methods, boolean serial ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.batch_run( methods, serial );
		return parseCallResult( (String)rawResponse );
	}

	public Document connect_registerUsers( Collection<Map<String,String>> accounts ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.connect_registerUsers( accounts );
		return parseCallResult( (String)rawResponse );
	}

	public Document connect_unregisterUsers( Collection<String> email_hashes ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = connect_unregisterUsers( email_hashes );
		return parseCallResult( (String)rawResponse );
	}

	public Document data_getAssociationDefinition( String associationName ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getAssociationDefinition( associationName );
		return parseCallResult( (String)rawResponse );
	}

	public Document data_getAssociationDefinitions() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getAssociationDefinitions();
		return parseCallResult( (String)rawResponse );
	}

	public Document data_getCookies() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getCookies();
		return parseCallResult( (String)rawResponse );
	}

	public Document data_getCookies( Long userId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getCookies( userId );
		return parseCallResult( (String)rawResponse );
	}

	public Document data_getCookies( String name ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getCookies( name );
		return parseCallResult( (String)rawResponse );
	}

	public Document data_getCookies( Long userId, CharSequence name ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getCookies( userId, name );
		return parseCallResult( (String)rawResponse );
	}

	public Document data_getObject( long objectId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObject( objectId );
		return parseCallResult( (String)rawResponse );	
	}

	public Document data_getObjectProperty( long objectId, String propertyName ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObjectProperty( objectId, propertyName );
		return parseCallResult( (String)rawResponse );	
	}

	public Document data_getObjectType( String objectType ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObjectType( objectType );
		return parseCallResult( (String)rawResponse );		
	}

	public Document data_getObjectTypes() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObjectTypes();
		return parseCallResult( (String)rawResponse );	
	}

	public Document data_getObjects( Collection<Long> objectIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getObjects( objectIds );
		return parseCallResult( (String)rawResponse );	
	}

	public Document data_getUserPreferences() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.data_getUserPreferences();
		return parseCallResult( (String)rawResponse );
	}

	public Document events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.events_get( userId, eventIds, startTime, endTime );
		return parseCallResult( (String)rawResponse );
	}

	public Document events_get( Long userId, Collection<Long> eventIds, Long startTime, Long endTime, String rsvp_status ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.events_get( userId, eventIds, startTime, endTime, rsvp_status );
		return parseCallResult( (String)rawResponse );
	}

	public Document events_getMembers( Long eventId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.events_getMembers( eventId );
		return parseCallResult( (String)rawResponse );
	}

	public Document feed_getRegisteredTemplateBundleByID( Long id ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.feed_getRegisteredTemplateBundleByID( id );
		return parseCallResult( (String)rawResponse );
	}

	public Document feed_getRegisteredTemplateBundles() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.feed_getRegisteredTemplateBundles();
		return parseCallResult( (String)rawResponse );
	}

	public Document fql_query( CharSequence query ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.fql_query( query );
		return parseCallResult( (String)rawResponse );
	}

	public Document friends_areFriends( long userId1, long userId2 ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_areFriends( userId1, userId2 );
		return parseCallResult( (String)rawResponse );
	}

	public Document friends_areFriends( Collection<Long> userIds1, Collection<Long> userIds2 ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_areFriends( userIds1, userIds2 );
		return parseCallResult( (String)rawResponse );
	}

	public Document friends_get( Long uid ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_get( uid );
		return parseCallResult( (String)rawResponse );
	}

	public Document friends_getAppUsers() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_getAppUsers();
		return parseCallResult( (String)rawResponse );
	}

	public Document friends_getList( Long friendListId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_getList( friendListId );
		return parseCallResult( (String)rawResponse );
	}

	public Document friends_getLists() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.friends_getLists();
		return parseCallResult( (String)rawResponse );
	}

	public Document groups_get( Long userId, Collection<Long> groupIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.groups_get( userId, groupIds );
		return parseCallResult( (String)rawResponse );
	}

	public Document groups_getMembers( Number groupId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.groups_getMembers( groupId );
		return parseCallResult( (String)rawResponse );
	}

	public Document marketplace_getCategoriesObject() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.marketplace_getCategoriesObject();
		return parseCallResult( (String)rawResponse );
	}

	public Document marketplace_getListings( Collection<Long> listingIds, Collection<Long> userIds ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.marketplace_getListings( listingIds, userIds );
		return parseCallResult( (String)rawResponse );
	}

	public Document marketplace_getSubCategories( CharSequence category ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.marketplace_getSubCategories( category );
		return parseCallResult( (String)rawResponse );
	}

	public Document marketplace_search( CharSequence category, CharSequence subCategory, CharSequence query ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.marketplace_search( category, subCategory, query );
		return parseCallResult( (String)rawResponse );
	}

	public Document notifications_get() throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_get();
		return parseCallResult( (String)rawResponse );
	}

	public Document notifications_sendEmail( Collection<Long> recipients, CharSequence subject, CharSequence email, CharSequence fbml ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_sendEmail( recipients, subject, email, fbml );
		return parseCallResult( (String)rawResponse );
	}

	public Document notifications_sendEmailToCurrentUser( String subject, String email, String fbml ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.notifications_sendEmailToCurrentUser( subject, email, fbml );
		return parseCallResult( (String)rawResponse );
	}

	public Document notifications_sendFbmlEmail( Collection<Long> recipients, String subject, String fbml ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document notifications_sendFbmlEmailToCurrentUser( String subject, String fbml ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document notifications_sendTextEmail( Collection<Long> recipients, String subject, String email ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document notifications_sendTextEmailToCurrentUser( String subject, String email ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document pages_getInfo( Collection<Long> pageIds, EnumSet<PageProfileField> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document pages_getInfo( Collection<Long> pageIds, Set<CharSequence> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document pages_getInfo( Long userId, EnumSet<PageProfileField> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document pages_getInfo( Long userId, Set<CharSequence> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document permissions_checkAvailableApiAccess( String apiKey ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document permissions_checkGrantedApiAccess( String apiKey ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_addTags( Long photoId, Collection<PhotoTag> tags ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_addTags( Long photoId, Collection<PhotoTag> tags, Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_createAlbum( String albumName ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_createAlbum( String name, String description, String location ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_createAlbum( String albumName, Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_createAlbum( String name, String description, String location, Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_get( Long subjId, Long albumId, Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_get( Long subjId, Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_get( Long subjId, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_get( Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_get( Long subjId ) throws FacebookException {
		client.setResponseFormat( "xml" );
		Object rawResponse = client.photos_get( subjId );
		return parseCallResult( (String)rawResponse );
	}

	public Document photos_getAlbums( Long userId, Collection<Long> albumIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_getAlbums( Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_getAlbums( Collection<Long> albumIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_getByAlbum( Long albumId, Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_getByAlbum( Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_getTags( Collection<Long> photoIds ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( File photo ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( File photo, String caption ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( File photo, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( File photo, String caption, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( Long userId, File photo ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( Long userId, File photo, String caption ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( Long userId, File photo, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( Long userId, File photo, String caption, Long albumId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document photos_upload( Long userId, String caption, Long albumId, String fileName, InputStream fileStream ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document profile_getFBML() throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document profile_getFBML( Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document profile_getFBML( int type ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document profile_getFBML( int type, Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document profile_getInfo( Long userId ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document profile_getInfoOptions( String field ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document users_getInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document users_getInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document users_getStandardInfo( Collection<Long> userIds, Collection<ProfileField> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

	public Document users_getStandardInfo( Collection<Long> userIds, Set<CharSequence> fields ) throws FacebookException {
		// TODO Auto-generated method stub
		return null;
	}

}
