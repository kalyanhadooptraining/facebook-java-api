package com.google.code.facebookapi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.code.facebookapi.schema.User;
import com.google.code.facebookapi.schema.UsersGetInfoResponse;

public class Issue30InfoMusicListTest {

	@Test
	public void test_getUserMusic() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );
		long uid = client.users_getLoggedInUser();
		Set<CharSequence> fields = new HashSet<CharSequence>( Arrays.asList( "uid", "music" ) );
		UsersGetInfoResponse response = client.users_getInfo( Arrays.asList( uid ), fields );
		User user = response.getUser().get( 0 );
		System.out.println( "music:" + user.getMusic() );
		// System.out.println( "music-raw:" + client.getRawResponse() );
	}

}
