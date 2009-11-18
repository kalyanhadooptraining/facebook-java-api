package com.google.code.facebookapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class Issue152UserLogoutTest {

	@Test
	public void testUserLogoutFacebookException() throws FacebookException, IOException {
		FacebookXmlRestClient client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		client.auth_expireSession();
		try {
			// clear cached user id
			client.setCacheSession( client.getCacheSessionKey(), null, client.getCacheSessionExpires() );
			// attempt to get user id from facebook
			client.users_getLoggedInUser();
			// should not have a valid session key
			fail( "Session is expired, getLoggedInUser should fail" );
		}
		catch ( FacebookException ex ) {
			assertEquals( ErrorCode.SESSION_INVALID, ex.getCode() );
		}
	}

}
