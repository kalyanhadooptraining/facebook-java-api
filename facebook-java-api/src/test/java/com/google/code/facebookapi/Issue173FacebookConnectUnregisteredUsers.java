package com.google.code.facebookapi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Issue173FacebookConnectUnregisteredUsers {

	@Test
	public void testFacebookConnectUnregisteredUsers() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		int count = client.connect_getUnconnectedFriendsCount();
		assertTrue( "Count is returned successfully", count >= 0 );
	}
}
