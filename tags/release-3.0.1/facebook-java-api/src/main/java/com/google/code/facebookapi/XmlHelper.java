package com.google.code.facebookapi;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlHelper {

	/**
	 * Prints out the DOM tree.
	 * 
	 * @param n
	 *            the parent node to start printing from
	 * @param prefix
	 *            string to append to output, should not be null
	 */
	public static void printDom( Node n, String prefix, StringBuilder sb ) {
		String outString = prefix;
		if ( n.getNodeType() == Node.TEXT_NODE ) {
			outString += "'" + n.getTextContent().trim() + "'";
		} else {
			outString += n.getNodeName();
		}
		sb.append( outString );
		sb.append( "\n" );
		NodeList children = n.getChildNodes();
		int length = children.getLength();
		for ( int i = 0; i < length; i++ ) {
			printDom( children.item( i ), prefix + "  ", sb );
		}
	}

	/**
	 * Extracts a String from a Document consisting entirely of a String.
	 * 
	 * @return the String
	 */
	public static String extractString( Node d ) {
		if ( d == null ) {
			return null;
		}
		return d.getFirstChild().getTextContent();
	}

	/**
	 * Extracts an Integer from a document that consists of an Integer only.
	 * 
	 * @param doc
	 * @return the Integer
	 */
	public static int extractInt( Node doc ) {
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
	public static Long extractLong( Node doc ) {
		if ( doc == null ) {
			return 0l;
		}
		return Long.parseLong( doc.getFirstChild().getTextContent() );
	}

	/**
	 * Extracts a Boolean from a result that consists of a Boolean only.
	 * 
	 * @param result
	 * @return the Boolean
	 */
	public static boolean extractBoolean( Node result ) {
		if ( result == null ) {
			return false;
		}
		return 1 == extractInt( result );
	}

	public static Document parseCallResult( Object rawResponse, DocumentBuilderFactory factory ) throws FacebookException {
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
	 * Hack...since DOM reads newlines as textnodes we want to strip out those nodes to make it easier to use the tree.
	 */
	public static void stripEmptyTextNodes( Node n ) {
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
	 * Used in the context of optimisation. If we already have an XML snippet, it's not sensible to convert it to a string and then convert it back to a document.
	 * 
	 * @param doc
	 * @return
	 * @throws FacebookException
	 */
	public static Document parseCallResult( Document doc ) throws FacebookException {
		NodeList errors = doc.getElementsByTagName( IFacebookRestClient.ERROR_TAG );
		if ( errors.getLength() > 0 ) {
			int errorCode = Integer.parseInt( errors.item( 0 ).getFirstChild().getFirstChild().getTextContent() );
			String message = errors.item( 0 ).getFirstChild().getNextSibling().getTextContent();
			throw new FacebookException( errorCode, message );
		}
		return doc;
	}

}
