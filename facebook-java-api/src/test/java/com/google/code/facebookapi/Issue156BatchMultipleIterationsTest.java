package com.google.code.facebookapi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.util.List;

import org.json.JSONArray;
import org.junit.Test;

/**
 * Ensure that multiple batches (> 20 results, which is the Facebook limit) work as expected.
 * 
 * @author david.j.boden
 */
public class Issue156BatchMultipleIterationsTest {

	@Test
	public void testGetMultipleBatchIterations() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		long uid = client.users_getLoggedInUser();
		client.beginBatch();

		for ( int i = 0; i < 30; i++ ) {
			JSONArray result = client.photos_getAlbums( uid );
			assertNull( result );
		}

		client.friends_get(); // This makes the total 31 calls

		List<? extends Object> batchResponse = client.executeBatch( false );

		assertEquals( 31, batchResponse.size() );
	}

}
