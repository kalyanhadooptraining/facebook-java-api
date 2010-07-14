package com.google.code.facebookapi;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;

public class Issue140FriendsGetJSONTest {

	@Test
	public void testFriendsGetBatchJSON() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

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
			System.out.println( "Result: " + r );
			assertNotNull( r );
		}
	}

	@Test
	public void testFriendsGetBatchXML() throws Exception {
		FacebookXmlRestClient client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );

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

}
