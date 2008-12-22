package com.google.code.facebookapi;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;

import com.google.code.facebookapi.schema.FriendsGetAppUsersResponse;
import com.google.code.facebookapi.schema.FriendsGetResponse;

public class Issue140FriendsGetJSONTest {

	@Test
	public void testFriendsGetBatchJSON() throws Exception {
		IFacebookRestClient<Object> client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		client.beginBatch();

		Object result = client.friends_get();
		assertNull( result );

		result = client.friends_get();
		assertNull( result );

		List<? extends Object> results = client.executeBatch( false );
		assertEquals( 2, results.size() );
		for ( Object r : results ) {
			System.out.println( "Result: " + r );
			assertNotNull( r );
		}
	}

	@Test
	public void testFriendsGetBatchJAXB() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		client.beginBatch();

		Object result = client.friends_get();
		assertNull( result );

		result = client.friends_get();
		assertNull( result );

		List<? extends Object> results = client.executeBatch( false );
		assertEquals( 2, results.size() );
		for ( Object r : results ) {
			assertNotNull( r );
			FriendsGetResponse fgr = (FriendsGetResponse) r;
			List<Long> uids = fgr.getUid();
			assertTrue( uids.size() > 0 );
		}
	}

	@Test
	public void testFriendsGetBatchXML() throws Exception {
		IFacebookRestClient<Document> client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );

		client.beginBatch();

		Document result = client.friends_get();
		assertNull( result );

		result = client.friends_get();
		assertNull( result );

		List<? extends Object> results = client.executeBatch( false );
		assertEquals( 2, results.size() );
		for ( Object r : results ) {
			System.out.println( "Result: " + r );
			assertNotNull( r );
		}
	}

	@Test
	public void testGetAppUsers() throws Exception {
		FacebookXmlRestClient client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		FacebookXmlRestClient.initJaxbSupport();

		client.friends_getAppUsers();
		FriendsGetAppUsersResponse fbResponse = (FriendsGetAppUsersResponse) client.getResponsePOJO();

		assertNotNull( fbResponse );
	}

}
