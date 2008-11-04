package com.google.code.facebookapi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This test requires that you've setup your data store to have: * An object called "footballteam" which has properties * name (string) * stadium (string) * An
 * association called "supports" which has aliases: * user (Any) * footballteam (footballteam)
 * 
 * You currently need to have an API_KEY and SECRET system property. In the future, we should setup a dedicated API_KEY (i.e. facebook user) which will be the test
 * environment for the API. Once this test is actually setting up and deleting the object definitions and association definitions then this will be easier to do. At that
 * stage, we can probably just check the API_KEY and SECRET of this dummy applicaion into source control.
 * 
 * @author dave@daveboden.com
 */
public class IFacebookRestClientTest {

	@Test
	public void testNOOP() {
		// empty
	}

	private String API_KEY = System.getProperty( "API_KEY" );
	private String SECRET = System.getProperty( "SECRET" );

	private String getValidSessionID() throws Exception {
		IFacebookRestClient<Document> client = new FacebookXmlRestClient( API_KEY, SECRET );
		String token = client.auth_createToken();

		if ( true ) {
			throw new UnsupportedOperationException( "Feature currently turned off, we have to support java5." );
		} else {
			// below was code originally submitted, but required Java 6, so I'm disabling for now.
			/*
			 * URI hitURL = new URI( "http://api.new.facebook.com/login.php?api_key=" + API_KEY + "&v=1.0&auth_token=" + token ); // Uses Java 6 API to register the token
			 * using your browser. Desktop.getDesktop().browse( hitURL ); Thread.sleep( 2000 ); // Give the token registration a 2 second headstart
			 */
		}

		String sessionID = client.auth_getSession( token );
		return sessionID;
	}

	// @Test
	public void test_dataStore() throws Exception {
		final String SESSION_PREFERENCE = "/com/google/code/facebookapi/test/sessionID";

		IFacebookRestClient<Document> client = null;

		String sessionID = Preferences.userRoot().get( SESSION_PREFERENCE, null );
		if ( sessionID != null ) {
			try {
				client = new FacebookXmlRestClient( API_KEY, SECRET, sessionID );
				// Test out the session ID
				Map<String,String> testTeam = new HashMap<String,String>();
				testTeam.put( "name", "Test Name" );
				testTeam.put( "stadium", "Test Stadium" );
				long testTeamID = client.data_createObject( "footballteam", testTeam );
				client.data_deleteObject( testTeamID );
			}
			catch ( FacebookException ex ) {
				Preferences.userRoot().remove( SESSION_PREFERENCE );
				client = null;
				System.out.println( "Session ID is out of date; generate a new one" );
			}
		}

		if ( client == null ) {
			sessionID = getValidSessionID();
			Preferences.userRoot().put( SESSION_PREFERENCE, sessionID );
			client = new FacebookXmlRestClient( API_KEY, SECRET, sessionID );
		}

		long andrewHewitt = 42908996689L;
		long davidBoden = 536286910L;

		// ***BEGIN CLEANUP FROM PREVIOUS FAILED RUNS***
		Document removeOld = client.fql_query( "select _id from app.footballteam where _id in " + "(select footballteam from app.supports where user = " + andrewHewitt
				+ " or user = " + davidBoden + ")" );

		List<Long> oldIDs = parseIDs( removeOld );
		for ( Long oldID : oldIDs ) {
			try {
				client.data_deleteObject( oldID );
			}
			catch ( FacebookException ex ) {
				System.out.println( "Didn't delete object " + oldID );
			}
		}
		// Delete multiple objects doesn't seem to work...
		// client.data_deleteObjects(oldIDs);
		client.data_removeAssociatedObjects( "supports", andrewHewitt );
		client.data_removeAssociatedObjects( "supports", davidBoden );
		// ***END CLEANUP FROM PREVIOUS FAILED RUNS***

		Map<String,String> teamProperties = new HashMap<String,String>();

		teamProperties.put( "name", "Manchester United" );
		teamProperties.put( "stadium", "Old Trafford" );
		long manUnited = client.data_createObject( "footballteam", teamProperties );

		teamProperties.put( "name", "Chelsea" );
		teamProperties.put( "stadium", "Stamford Bridge" );
		long chelsea = client.data_createObject( "footballteam", teamProperties );

		teamProperties.put( "name", "Arsenal" );
		teamProperties.put( "stadium", "Emirates Stadium" );
		long arsenal = client.data_createObject( "footballteam", teamProperties );

		client.data_setAssociation( "supports", andrewHewitt, chelsea, "Has a scarf and everything", null );
		client.data_setAssociation( "supports", andrewHewitt, arsenal, "Got an expensive season ticket", null );

		client.data_setAssociation( "supports", davidBoden, chelsea, "Just goes to the odd game", null );
		client.data_setAssociation( "supports", davidBoden, manUnited, "Can't afford the petrol for the drive up most weekends", null );

		Document fqlDoc = client.fql_query( "select name from app.footballteam where _id in " + "(select footballteam from app.supports where user = " + andrewHewitt
				+ ")" );

		List<String> teamNames = parseTeamNames( fqlDoc );

		assertTrue( teamNames.contains( "Chelsea" ) );
		assertTrue( teamNames.contains( "Arsenal" ) );

		client.data_removeAssociation( "supports", davidBoden, manUnited );
		assertEquals( "We removed man united, so Dave should now only " + "support Chelsea", 1, client.data_getAssociatedObjectCount( "supports", davidBoden ) );


		Map<String,String> updateProperties = new HashMap<String,String>();
		updateProperties.put( "stadium", "Russian Stadium!" );
		client.data_updateObject( chelsea, updateProperties, true );

		// Future: Check that the value got updated.

		client.data_removeAssociatedObjects( "supports", davidBoden );
		client.data_removeAssociatedObjects( "supports", andrewHewitt );
		// Only the associations are deleted, not the objects themselves yet.
		client.data_deleteObject( manUnited );
		client.data_deleteObject( chelsea );
		client.data_deleteObject( arsenal );

		// All data is now deleted
	}

	List<String> parseTeamNames( Document doc ) throws XPathException {
		return parseResults( doc, "name" );
	}

	List<Long> parseIDs( Document doc ) throws XPathException {
		List<String> idStrings = parseResults( doc, "_id" );
		List<Long> ids = new ArrayList<Long>();
		for ( String idString : idStrings ) {
			ids.add( Long.valueOf( idString ) );
		}
		return ids;
	}

	List<String> parseResults( Document doc, String property ) throws XPathException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		// Come on Sun, make the XPath API Java 5 and iterable so that this code
		// doesn't have to be so ugly!
		NodeList nodes = (NodeList) xpath.evaluate( "//*[local-name()='" + property + "']", doc, XPathConstants.NODESET );
		List<String> values = new ArrayList<String>();
		for ( int i = 0; i < nodes.getLength(); i++ ) {
			values.add( nodes.item( i ).getTextContent() );
		}
		return values;
	}

}
