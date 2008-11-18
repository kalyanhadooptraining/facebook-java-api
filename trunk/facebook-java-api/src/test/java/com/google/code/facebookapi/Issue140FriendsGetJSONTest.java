package com.google.code.facebookapi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

public class Issue140FriendsGetJSONTest {

	@Test
	public void testFriendsGetJSON() throws Exception {
		IFacebookRestClient<Object> client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		client.beginBatch();

		Object result = client.friends_get();
		assertNull( result );

		result = client.friends_get();
		assertNull( result );

		List<? extends Object> results = client.executeBatch( false );

		for ( Object r : results ) {
			System.out.println( "Result: " + r );
			assertNotNull( r );
		}
	}

}
