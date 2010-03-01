package com.google.code.facebookapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FacebookNamespaceContextTest {

	@Test
	public void testGetPrefixes() {
		FacebookNamespaceContext fnc = new FacebookNamespaceContext();
		Iterator<String> it = fnc.getPrefixes( "http://api.facebook.com/1.0/" );
		assertTrue( it.hasNext() );
		assertEquals( "fbapi", it.next() );

		try {
			it.remove();
			fail( "Iterator should not allow removal of prefixes" );
		}
		catch ( UnsupportedOperationException ex ) {
			// empty
		}
		assertFalse( it.hasNext() );
	}

	@Test
	public void testUse() throws Exception {
		FacebookNamespaceContext fnc = new FacebookNamespaceContext();
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.newDocument();

		Element fqlQueryResponse = document.createElementNS( "http://api.facebook.com/1.0/", "fql_query_response" );
		document.appendChild( fqlQueryResponse );

		String xpathexpression = "count(/fbapi:fql_query_response)";
		String count = xpath.evaluate( xpathexpression, document );
		assertEquals( "No elements will be found because the processor " + "doesn't yet know about the fbapi prefix", "0", count );
		xpath.setNamespaceContext( fnc );
		count = xpath.evaluate( xpathexpression, document );
		assertEquals( "1 element will be found because the processor " + "now knows about the fbapi prefix", "1", count );
	}

}
