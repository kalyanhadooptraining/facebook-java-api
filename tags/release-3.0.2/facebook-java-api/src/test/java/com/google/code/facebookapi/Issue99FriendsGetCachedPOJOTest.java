package com.google.code.facebookapi;

import static org.junit.Assert.assertNotNull;

import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import com.google.code.facebookapi.schema.FriendsGetResponse;
import com.google.code.facebookapi.schema.User;
import com.google.code.facebookapi.schema.UsersGetInfoResponse;

public class Issue99FriendsGetCachedPOJOTest {

	@Test
	public void testFriendsGetCachedReturnsPOJO() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		// keep track of the logged in user id
		Long userId = client.users_getLoggedInUser();
		assertNotNull( userId );

		// Get friends list
		client.friends_get();
		FriendsGetResponse response = (FriendsGetResponse) client.getResponsePOJO();
		List<Long> friends = response.getUid();

		// Go fetch the information for the user list of user ids
		client.users_getInfo( friends, EnumSet.of( ProfileField.NAME ) );

		UsersGetInfoResponse userResponse = (UsersGetInfoResponse) client.getResponsePOJO();

		// Print out the user information
		List<User> users = userResponse.getUser();
		for ( User user : users ) {
			assertNotNull( user.getName() );
		}
	}

}
