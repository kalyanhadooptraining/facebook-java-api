package com.google.code.facebookapi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class DataStoreAPITest {
	
	XPath xpath;
	public DataStoreAPITest() {
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		xpath.setNamespaceContext(new FacebookNamespaceContext());
	}
	
	@Test
	public void test_dataStore() throws Exception {
		IFacebookRestClient<Document> client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );

		long andrewHewitt = 42908996689L;
		long davidBoden = 536286910L;

		// ***BEGIN CLEANUP FROM PREVIOUS FAILED RUNS***
		cleanObjectType(client, "footballteam");
		cleanAssociation(client, "supports");
		// ***END CLEANUP FROM PREVIOUS FAILED RUNS***
		
		client.data_createObjectType("footballteam");
		client.data_defineObjectProperty("footballteam", "name", PropertyType.STRING);
		client.data_defineObjectProperty("footballteam", "stadium", PropertyType.STRING);
		client.data_defineAssociation("supports", AssociationType.ONE_WAY,
				                      new AssociationInfo("user", null, false),
				                      new AssociationInfo("footballteam", "footballteam", false),
				                      null); //The inverse name isn't used because we've only defined a 1 way relationship
		
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
		
		client.data_dropObjectType("footballteam");
		client.data_undefineAssociation("supports");
		
		//All metadata is now removed
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
		// Come on Sun, make the XPath API Java 5 and iterable so that this code
		// doesn't have to be so ugly!
		NodeList nodes = (NodeList) xpath.evaluate( "//fbapi:" + property, doc, XPathConstants.NODESET );
		List<String> values = new ArrayList<String>();
		for ( int i = 0; i < nodes.getLength(); i++ ) {
			values.add( nodes.item( i ).getTextContent() );
		}
		return values;
	}
	
	@Test
	public void testRenameObjects() throws Exception {
		IFacebookRestClient<Document> client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		//Cleanup from previous run
		cleanObjectType(client, "testrename_1234");
		cleanObjectType(client, "testrename_5678");
		
		client.data_createObjectType("testrename_1234");
		client.data_defineObjectProperty("testrename_1234", "property1", PropertyType.INTEGER);
		Document objectTypeQuery = client.data_getObjectType("testrename_1234");
		assertEquals("property1", xpath.evaluate("//fbapi:object_property_info/fbapi:name", objectTypeQuery));
		Document objectTypesQuery = client.data_getObjectTypes();
		assertEquals("1", xpath.evaluate("count(//fbapi:object_type_info[fbapi:name = 'testrename_1234'])", objectTypesQuery));
		
		client.data_renameObjectType("testrename_1234", "testrename_5678");
		client.data_renameObjectProperty("testrename_5678", "property1", "property2");
		
		objectTypeQuery = client.data_getObjectType("testrename_5678");
		assertEquals("property2", xpath.evaluate("//fbapi:object_property_info/fbapi:name", objectTypeQuery));
		
		client.data_undefineObjectProperty("testrename_5678", "property2");
		objectTypeQuery = client.data_getObjectType("testrename_5678");
		assertEquals("0", xpath.evaluate("count(//fbapi:name)", objectTypeQuery));
		
		//Cleanup
		cleanObjectType(client, "testrename_1234");
		cleanObjectType(client, "testrename_5678");
	}
	
	@Test
	public void testRenameAssociations() throws Exception {
		IFacebookRestClient<Document> client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		//Cleanup from previous run
		cleanAssociation(client, "testrename_abcd");
		cleanAssociation(client, "testrename_efgh");
		
		client.data_defineAssociation("testrename_abcd", AssociationType.ONE_WAY,
									  new AssociationInfo("user1"), new AssociationInfo("user2"),
									  null);
		
		Document associationQuery = client.data_getAssociationDefinition("testrename_abcd");
		
		client.data_renameAssociation("testrename_abcd", "testrename_efgh", null, "user3");
		associationQuery = client.data_getAssociationDefinition("testrename_efgh");
		
		assertEquals("user1", xpath.evaluate("//fbapi:assoc_info1_elt[1]", associationQuery));
		assertEquals("user2 has been replaced by user3",
				     "user3", xpath.evaluate("//fbapi:assoc_info2_elt[1]", associationQuery));
		
		client.data_undefineAssociation("testrename_efgh");
	}
	
	/**
	 * Removes the specified object type if it exists, doesn't complain if it doesn't exist.
	 * @param name
	 */
	private void cleanObjectType(IFacebookRestClient<?> client, String name) {
		try {
			client.data_dropObjectType(name);
		} catch(FacebookException ex) {
			if(ex.getCode() != 803) {
				fail("Error " + ex.getCode() + " returned when " +
				     "trying to clean up " + name + " object: " + ex.getMessage());
			}
		}
	}
	
	private void cleanAssociation(IFacebookRestClient<?> client, String associationName) {
		try {
			client.data_undefineAssociation(associationName);
		} catch(FacebookException ex) {
			if(ex.getCode() != 803) {
				fail("Error " + ex.getCode() + " returned when " +
				     "trying to clean up " + associationName + " association: " + ex.getMessage());
			}
		}
	}
}
