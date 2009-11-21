package com.google.code.facebookapi;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.facebookapi.schema.UsersGetInfoResponse;

public class Issue157UsersGetInfoTest {

	@Test
	public void testUsersGetInfo() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		long logged = client.users_getLoggedInUser();
		List<Long> users = Collections.singletonList( logged );
		EnumSet<ProfileField> fields = EnumSet.allOf( ProfileField.class );
		UsersGetInfoResponse response = client.users_getInfo( users, fields );

		String name = response.getUser().get( 0 ).getName();

		Assert.assertNotNull( name );
		Assert.assertNotSame( "", name );
	}

}
