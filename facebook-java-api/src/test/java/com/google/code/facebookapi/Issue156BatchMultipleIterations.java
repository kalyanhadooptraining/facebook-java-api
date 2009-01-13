package com.google.code.facebookapi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

/**
 * Ensure that multiple batches (> 20 results, which is the Facebook limit) work as expected.
 * 
 * @author david.j.boden
 */
public class Issue156BatchMultipleIterations {

	@Test
	public void testGetMultipleBatchIterations() throws FacebookException, IOException {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		long uid = client.users_getLoggedInUser();
		client.beginBatch();

		for ( int i = 0; i < 30; i++ ) {
			Object result = client.photos_getAlbums( uid );
			assertNull( result );
		}

		client.friends_get(); //This makes the total 31 calls

		List<? extends Object> batchResponse = client.executeBatch( false );

		assertEquals( 31, batchResponse.size() );
	}
}
