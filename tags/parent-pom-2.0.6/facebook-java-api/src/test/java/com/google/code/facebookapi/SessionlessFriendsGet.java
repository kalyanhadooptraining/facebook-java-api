package com.google.code.facebookapi;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.json.JSONArray;
import org.junit.Ignore;
import org.junit.Test;


public class SessionlessFriendsGet {

	@Test
	@Ignore
	public void testFriendsGetWithoutSession() throws FacebookException {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getSessionlessValidClient( FacebookJsonRestClient.class );

		try {
			client.friends_get();
			fail("Shouldn't be able to use base friends_get() without a session");
		} catch(FacebookException ex) {
			//Ignore
		}
		
		long uid = client.users_getLoggedInUser();
		JSONArray friends = (JSONArray)client.friends_get(uid);
		
		assertTrue( friends.length() > 0 );
	}
}
