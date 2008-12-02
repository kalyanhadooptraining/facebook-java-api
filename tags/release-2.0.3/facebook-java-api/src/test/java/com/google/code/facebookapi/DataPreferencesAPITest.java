package com.google.code.facebookapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.w3c.dom.Document;

import com.google.code.facebookapi.schema.DataGetUserPreferencesResponse;
import com.google.code.facebookapi.schema.Preference;

/**
 * Uses preferences 161 and 162 (overwrites them) to test the data preferences functions.
 * 
 * @author david.j.boden
 */
public class DataPreferencesAPITest {

	private static final String illegal129Chars = "12345678901234567890123456789012345678901234567890" + // 50
			"12345678901234567890123456789012345678901234567890" + // 100
			"12345678901234567890123456789"; // 129

	@Test
	public void testDataPreferencesXML() throws Exception {
		IFacebookRestClient<Document> client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		Document pinkBlack = testCycle( client );
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext( new FacebookNamespaceContext() );

		/*
		 * <?xml version="1.0" encoding="UTF-8" standalone="no"?> <data_getUserPreferences_response xmlns="http://api.facebook.com/1.0/"
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" list="true" xsi:schemaLocation="http://api.facebook.com/1.0/ http://api.facebook.com/1.0/facebook.xsd">
		 * <preference><pref_id>161</pref_id><value>Pink</value></preference> <preference><pref_id>162</pref_id><value>Black</value></preference>
		 * </data_getUserPreferences_response>
		 */

		assertEquals( "Pink", xpath.evaluate( "//fbapi:preference[fbapi:pref_id=161]/fbapi:value", pinkBlack ) );
		assertEquals( "Black", xpath.evaluate( "//fbapi:preference[fbapi:pref_id=162]/fbapi:value", pinkBlack ) );
	}

	@Test
	public void testDataPreferencesJSON() throws Exception {
		IFacebookRestClient<Object> client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );
		JSONArray pinkBlack = (JSONArray) testCycle( client );

		// [{"value":"Pink","pref_id":161},{"value":"Black","pref_id":162}]

		boolean found161 = false;
		boolean found162 = false;
		for ( int i = 0; i < pinkBlack.length(); i++ ) {
			JSONObject pref = pinkBlack.getJSONObject( i );
			if ( pref.getInt( "pref_id" ) == 161 ) {
				found161 = true;
				assertEquals( "Pink", pref.getString( "value" ) );
			} else if ( pref.getInt( "pref_id" ) == 162 ) {
				found162 = true;
				assertEquals( "Black", pref.getString( "value" ) );
			}
		}
		assertTrue( found161 );
		assertTrue( found162 );
	}

	@Test
	public void testDataPreferencesJAXB() throws Exception {
		IFacebookRestClient<Object> client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );
		Object pinkBlack = testCycle( client );
		DataGetUserPreferencesResponse response = (DataGetUserPreferencesResponse) pinkBlack;
		List<Preference> preferences = response.getPreference();
		boolean found161 = false;
		boolean found162 = false;
		for ( Preference pref : preferences ) {
			if ( pref.getPrefId() == 161 ) {
				found161 = true;
				assertEquals( "Pink", pref.getValue() );
			} else if ( pref.getPrefId() == 162 ) {
				found162 = true;
				assertEquals( "Black", pref.getValue() );
			}
		}
		assertTrue( found161 );
		assertTrue( found162 );
	}

	private <T> T testCycle( IFacebookRestClient<T> client ) throws Exception {
		try {
			client.data_setUserPreference( 161, illegal129Chars );
			fail( "Maximum size of preference string is 127. " + "Trying to set a larger value should result in an error." );
		}
		catch ( FacebookException ex ) {
			// empty
		}

		try {
			client.data_setUserPreference( 201, "Illegal preference number" );
			fail( "Preference numbers must be between 0 and 200" );
		}
		catch ( FacebookException ex ) {
			// empty
		}

		client.data_setUserPreference( 161, "Green" );
		client.data_setUserPreference( 162, "Blue" );

		assertEquals( "Green", client.data_getUserPreference( 161 ) );
		assertEquals( "Blue", client.data_getUserPreference( 162 ) );

		Map<Integer,String> morePrefs = new HashMap<Integer,String>();
		morePrefs.put( 161, "Pink" );
		morePrefs.put( 162, "Black" );

		client.data_setUserPreferences( morePrefs, false );

		assertEquals( "Pink", client.data_getUserPreference( 161 ) );
		assertEquals( "Black", client.data_getUserPreference( 162 ) );

		return client.data_getUserPreferences();
	}

}
