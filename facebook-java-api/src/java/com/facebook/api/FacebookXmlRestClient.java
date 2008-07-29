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

package com.facebook.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.facebook.api.schema.Listing;
import com.facebook.api.schema.MarketplaceGetListingsResponse;
import com.facebook.api.schema.MarketplaceGetSubCategoriesResponse;
import com.facebook.api.schema.MarketplaceSearchResponse;

/**
 * A FacebookRestClient that uses the XML result format. This means results from calls to the Facebook API are returned as XML and transformed into instances of
 * {@link org.w3c.dom.Document}.
 * 
 * @deprecated this is provided for legacy support only. Please use FacebookRestClient instead if you want to use the Facebook Platform XML API.
 */
@Deprecated
public class FacebookXmlRestClient extends ExtensibleClient<Document> {

	protected boolean namespaceAware = true;

	public boolean isNamespaceAware() {
		return namespaceAware;
	}

	public void setNamespaceAware( boolean v ) {
		this.namespaceAware = v;
	}

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

	public FacebookXmlRestClient( String apiKey, String secret ) {
		super( apiKey, secret );
	}

	public FacebookXmlRestClient( String apiKey, String secret, int connectionTimeout ) {
		super( apiKey, secret, connectionTimeout );
	}

	public FacebookXmlRestClient( String apiKey, String secret, String sessionKey ) {
		super( apiKey, secret, sessionKey );
	}

	public FacebookXmlRestClient( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		super( apiKey, secret, sessionKey, connectionTimeout );
	}

	public FacebookXmlRestClient( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		super( serverAddr, apiKey, secret, sessionKey );
	}

	public FacebookXmlRestClient( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		super( serverAddr, apiKey, secret, sessionKey, connectionTimeout );
	}

	public FacebookXmlRestClient( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		super( serverUrl, apiKey, secret, sessionKey );
	}

	public FacebookXmlRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		super( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 );
	}

	public FacebookXmlRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
		super( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout );
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
	public String extractString( Document d ) {
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
	public String auth_getSession( String authToken ) throws FacebookException, IOException {
		if ( null != this._sessionKey ) {
			return this._sessionKey;
		}
		Document d = callMethod( FacebookMethod.AUTH_GET_SESSION, new Pair<String,CharSequence>( "auth_token", authToken.toString() ) );
		this._sessionKey = d.getElementsByTagName( "session_key" ).item( 0 ).getFirstChild().getTextContent();
		this._userId = Integer.parseInt( d.getElementsByTagName( "uid" ).item( 0 ).getFirstChild().getTextContent() );
		this._expires = Long.parseLong( d.getElementsByTagName( "expires" ).item( 0 ).getFirstChild().getTextContent() );
		if ( this._isDesktop ) {
			this._sessionSecret = d.getElementsByTagName( "secret" ).item( 0 ).getFirstChild().getTextContent();
		}
		return this._sessionKey;
	}

	protected Document parseCallResult( InputStream data, IFacebookMethod method ) throws FacebookException, IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware( namespaceAware );
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse( data );
			doc.normalizeDocument();
			stripEmptyTextNodes( doc );

			if ( isDebug() ) {
				printDom( doc, method.methodName() + "| " );
			}
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
	}

	/**
	 * Extracts a URL from a document that consists of a URL only.
	 * 
	 * @param doc
	 * @return the URL
	 */
	protected URL extractURL( Document doc ) throws IOException {
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
	protected int extractInt( Document doc ) {
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
	protected Long extractLong( Document doc ) {
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
	public static void printDom( Node n, String prefix ) {
		String outString = prefix;
		if ( n.getNodeType() == Node.TEXT_NODE ) {
			outString += "'" + n.getTextContent().trim() + "'";
		} else {
			outString += n.getNodeName();
		}
		if ( DEBUG ) {
			System.out.println( outString );
		}
		NodeList children = n.getChildNodes();
		int length = children.getLength();
		for ( int i = 0; i < length; i++ ) {
			FacebookXmlRestClient.printDom( children.item( i ), prefix + "  " );
		}
	}

	public String data_getUserPreference( Integer prefId ) throws FacebookException, IOException {
		throw new FacebookException( ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  "
				+ "Please use an instance of FacebookRestClient instead." );
	}

	public Map<Integer,String> data_getUserPreferences() throws FacebookException, IOException {
		throw new FacebookException( ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  "
				+ "Please use an instance of FacebookRestClient instead." );
	}

	public void data_setUserPreference( Integer prefId, String value ) throws FacebookException, IOException {
		throw new FacebookException( ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  "
				+ "Please use an instance of FacebookRestClient instead." );

	}

	public void data_setUserPreferences( Map<Integer,String> values, boolean replace ) throws FacebookException, IOException {
		throw new FacebookException( ErrorCode.GEN_UNKNOWN_METHOD, "The FacebookJsonRestClient does not support this API call.  "
				+ "Please use an instance of FacebookRestClient instead." );
	}

	public List<Listing> marketplace_getListings( List<Long> listingIds, List<Long> uids ) throws FacebookException, IOException {
		marketplace_getListings( listingIds, uids );
		MarketplaceGetListingsResponse resp = (MarketplaceGetListingsResponse) getResponsePOJO();
		return resp.getListing();
	}

	public List<String> marketplace_getSubCategories() throws FacebookException, IOException {
		marketplace_getSubCategories( null );
		MarketplaceGetSubCategoriesResponse resp = (MarketplaceGetSubCategoriesResponse) getResponsePOJO();
		return resp.getMarketplaceSubcategory();
	}

	public List<Listing> marketplace_search( MarketListingCategory category, MarketListingSubcategory subcategory, String searchTerm ) throws FacebookException,
			IOException {
		marketplace_search( category.getName(), subcategory.getName(), searchTerm );
		MarketplaceSearchResponse resp = (MarketplaceSearchResponse) getResponsePOJO();
		return resp.getListing();
	}

	public String admin_getAppPropertiesAsString( Collection<ApplicationProperty> properties ) throws FacebookException, IOException {
		if ( this._isDesktop ) {
			// this method cannot be called from a desktop app
			throw new FacebookException( ErrorCode.GEN_PERMISSIONS_ERROR, "Desktop applications cannot use 'admin.getAppProperties'" );
		}
		JSONArray props = new JSONArray();
		for ( ApplicationProperty property : properties ) {
			props.put( property.getName() );
		}
		Document d = callMethod( FacebookMethod.ADMIN_GET_APP_PROPERTIES, new Pair<String,CharSequence>( "properties", props.toString() ) );
		return extractString( d );
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
	public List<? extends Object> executeBatch( boolean serial ) throws FacebookException, IOException {
		this.batchMode = false;
		List<Object> result = new ArrayList<Object>();
		List<BatchQuery> buffer = new ArrayList<BatchQuery>();
		while ( !this.queries.isEmpty() ) {
			buffer.add( this.queries.remove( 0 ) );
			if ( ( buffer.size() == 15 ) || ( this.queries.isEmpty() ) ) {
				// we can only actually batch up to 15 at once
				Document doc = batch_run( encodeMethods( buffer ), serial );
				NodeList responses = doc.getElementsByTagName( "batch_run_response_elt" );
				for ( int count = 0; count < responses.getLength(); count++ ) {
					String response = extractNodeString( responses.item( count ) );
					try {
						DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document respDoc = builder.parse( new ByteArrayInputStream( response.getBytes( "UTF-8" ) ) );
						String type = RETURN_TYPES.get( buffer.get( count ).getMethod() );
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
		}

		return result;
	}

	public static String extractNodeString( Node d ) {
		if ( d == null ) {
			return null;
		}
		return d.getFirstChild().getTextContent();
	}
}
