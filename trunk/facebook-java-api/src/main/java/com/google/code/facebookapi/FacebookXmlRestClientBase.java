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

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

/**
 * A FacebookRestClient that uses the XML result format. This means results from calls to the Facebook API are returned as XML and transformed into instances of
 * {@link org.w3c.dom.Document}.
 */
public abstract class FacebookXmlRestClientBase extends SpecificReturnTypeAdapter implements IFacebookRestClient<Document> {

	protected static Log log = LogFactory.getLog( FacebookXmlRestClientBase.class );

	protected DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public boolean isNamespaceAware() {
		return factory.isNamespaceAware();
	}

	public void setNamespaceAware( boolean v ) {
		factory.setNamespaceAware( v );
	}

	protected ExtensibleClient client;

	public ExtensibleClient getClient() {
		return client;
	}

	public void setClient( ExtensibleClient client ) {
		this.client = client;
	}

	public FacebookXmlRestClientBase( ExtensibleClient client ) {
		super( "xml" );
		factory.setNamespaceAware( true );
		this.client = client;
	}

	public FacebookXmlRestClientBase( String apiKey, String secret ) {
		this( new ExtensibleClient( apiKey, secret ) );
	}

	public FacebookXmlRestClientBase( String apiKey, String secret, int connectionTimeout ) {
		this( new ExtensibleClient( apiKey, secret, connectionTimeout ) );
	}

	public FacebookXmlRestClientBase( String apiKey, String secret, String sessionKey ) {
		this( new ExtensibleClient( apiKey, secret, sessionKey ) );
	}

	public FacebookXmlRestClientBase( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( new ExtensibleClient( apiKey, secret, sessionKey, connectionTimeout ) );
	}

	public FacebookXmlRestClientBase( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		this( new ExtensibleClient( serverAddr, apiKey, secret, sessionKey ) );
	}

	public FacebookXmlRestClientBase( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		this( new ExtensibleClient( serverAddr, apiKey, secret, sessionKey, connectionTimeout ) );
	}

	public FacebookXmlRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey ) );
	}

	public FacebookXmlRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 ) );
	}

	public FacebookXmlRestClientBase( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
		this( new ExtensibleClient( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout ) );
	}

	Document parseCallResult( Object rawResponse ) throws FacebookException {
		if ( rawResponse == null ) {
			return null;
		}
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse( new InputSource( new StringReader( (String) rawResponse ) ) );
			doc.normalizeDocument();
			stripEmptyTextNodes( doc );
			return parseCallResult( doc );
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
	 * Used in the context of optimisation. If we already have an XML snippet, it's not sensible to convert it to a string and then convert it back to a document.
	 * 
	 * @param doc
	 * @return
	 * @throws FacebookException
	 */
	Document parseCallResult( Document doc ) throws FacebookException {
		NodeList errors = doc.getElementsByTagName( ERROR_TAG );
		if ( errors.getLength() > 0 ) {
			int errorCode = Integer.parseInt( errors.item( 0 ).getFirstChild().getFirstChild().getTextContent() );
			String message = errors.item( 0 ).getFirstChild().getNextSibling().getTextContent();
			throw new FacebookException( errorCode, message );
		}
		return doc;
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
	public List<Document> executeBatch( boolean serial ) throws FacebookException {
		client.setResponseFormat( "xml" );

		List<String> clientResults = client.executeBatch( serial );

		List<Document> result = new ArrayList<Document>();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			for ( String clientResult : clientResults ) {
				Document doc = builder.parse( new InputSource( new StringReader( clientResult ) ) );
				NodeList responses = doc.getElementsByTagName( "batch_run_response_elt" );
				for ( int count = 0; count < responses.getLength(); count++ ) {
					Node responseNode = responses.item( count );
					Document respDoc = builder.newDocument();
					responseNode = respDoc.importNode( responseNode, true );
					respDoc.appendChild( responseNode );
					try {
						respDoc = parseCallResult( respDoc );
						result.add( respDoc );
					}
					catch ( FacebookException ignored ) {
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

	public String getRawResponse() {
		return client.getRawResponse();
	}

}
