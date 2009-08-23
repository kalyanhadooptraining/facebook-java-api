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

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

import com.google.code.facebookapi.schema.FacebookApiException;

/**
 * A FacebookRestClient that JAXB response objects. This means results from calls to the Facebook API are returned as XML and transformed into JAXB Java objects.
 */
public abstract class FacebookJaxbRestClientBase extends SpecificReturnTypeAdapter implements IFacebookRestClient<Object> {

	protected static Log log = LogFactory.getLog( FacebookJaxbRestClientBase.class );

	protected ExtensibleClient client;

	public ExtensibleClient getClient() {
		return client;
	}

	public void setClient( ExtensibleClient client ) {
		this.client = client;
	}

	public FacebookJaxbRestClientBase( ExtensibleClient client ) {
		super( "xml" );
		this.client = client;
		initJaxbSupport();
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 */
	public FacebookJaxbRestClientBase( String apiKey, String secret ) {
		this( new ExtensibleClient( apiKey, secret ) );
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
	public FacebookJaxbRestClientBase( String apiKey, String secret, int connectionTimeout ) {
		this( new ExtensibleClient( apiKey, secret, connectionTimeout ) );
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
	public FacebookJaxbRestClientBase( String apiKey, String secret, String sessionKey ) {
		this( new ExtensibleClient( apiKey, secret, sessionKey ) );
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
	public FacebookJaxbRestClientBase( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( new ExtensibleClient( apiKey, secret, sessionKey, connectionTimeout ) );
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
	public FacebookJaxbRestClientBase( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		this( new ExtensibleClient( serverAddr, apiKey, secret, sessionKey ) );
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
	public FacebookJaxbRestClientBase( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		this( new ExtensibleClient( serverAddr, apiKey, secret, sessionKey, connectionTimeout ) );
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
	public FacebookJaxbRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey ) );
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
	public FacebookJaxbRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 ) );
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
	public FacebookJaxbRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout ) );
	}

	protected static JAXBContext JAXB_CONTEXT;

	public JAXBContext getJaxbContext() {
		return JAXB_CONTEXT;
	}

	public void setJaxbContext( JAXBContext context ) {
		JAXB_CONTEXT = context;
	}

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

	/**
	 * Returns a JAXB object of the type that corresponds to the last API call made on the client. Each Facebook Platform API call that returns a Document object has a
	 * JAXB response object associated with it. The naming convention is generally intuitive. For example, if you invoke the 'user_getInfo' API call, the associated JAXB
	 * response object is 'UsersGetInfoResponse'.<br />
	 * <br />
	 * An example of how to use this method:<br />
	 * <br />
	 * FacebookRestClient client = new FacebookRestClient("apiKey", "secretKey", "sessionId");<br />
	 * client.friends_get();<br />
	 * FriendsGetResponse response = (FriendsGetResponse)client.getResponsePOJO();<br />
	 * List<Long> friends = response.getUid(); <br />
	 * <br />
	 * This is particularly useful in the case of API calls that return a Document object, as working with the JAXB response object is generally much simple than trying
	 * to walk/parse the DOM by hand.<br />
	 * <br />
	 * This method can be safely called multiple times, though note that it will only return the response-object corresponding to the most recent Facebook Platform API
	 * call made.<br />
	 * <br />
	 * Note that you must cast the return value of this method to the correct type in order to do anything useful with it.
	 * 
	 * @return a JAXB POJO ("Plain Old Java Object") of the type that corresponds to the last API call made on the client. Note that you must cast this object to its
	 *         proper type before you will be able to do anything useful with it.
	 */
	public Object getResponsePOJO() {
		return getResponsePOJO( getRawResponse() );
	}

	public Object getResponsePOJO( String rawResponse ) {
		if ( JAXB_CONTEXT == null ) {
			return null;
		}
		try {
			Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
			return unmarshaller.unmarshal( new StringReader( rawResponse ) );
		}
		catch ( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}

	public String getRawResponse() {
		return client.getRawResponse();
	}

	private String parse( String val ) {
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
		return parse( (String) val );
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
		if ( rawResponse == null ) {
			return null;
		}
		log.debug( "Facebook response:  " + rawResponse );
		Object res = getResponsePOJO( (String) rawResponse );
		if ( res instanceof FacebookApiException ) {
			FacebookApiException error = (FacebookApiException) res;
			int errorCode = error.getErrorCode();
			String message = error.getErrorMsg();
			throw new FacebookException( errorCode, message );
		} else if ( res instanceof JAXBElement<?> ) {
			JAXBElement<?> jbe = (JAXBElement<?>) res;
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
		String result = parse( (String) url );
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
			return Integer.parseInt( parse( (String) val ) );
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
		String result = parse( (String) val );
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
			return Long.parseLong( parse( (String) val ) );
		}
		catch ( Exception cce ) {
			return 0l;
		}
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

		List<String> clientResults = client.executeBatch( serial );

		List<Object> result = new ArrayList<Object>();

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			for ( String clientResult : clientResults ) {
				Document doc = builder.parse( new InputSource( new StringReader( clientResult ) ) );
				NodeList responses = doc.getElementsByTagName( "batch_run_response_elt" );
				for ( int count = 0; count < responses.getLength(); count++ ) {
					String response = extractNodeString( responses.item( count ) );
					try {
						Object pojo = parseCallResult( response );
						result.add( pojo );
					}
					catch ( Exception e ) {
						result.add( null );
					}
				}
			}
		}
		catch ( ParserConfigurationException ex ) {
			throw new RuntimeException( "Error parsing batch response", ex );
		}
		catch ( SAXException ex ) {
			throw new RuntimeException( "Error parsing batch response", ex );
		}
		catch ( IOException ex ) {
			throw new RuntimeException( "Error parsing batch response", ex );
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
