package com.google.code.facebookapi;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.facebookapi.schema.UsersGetInfoResponse;

public class Issue157UsersGetInfo {

	@Test
	public void testUsersGetInfo() throws FacebookException, IOException {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );
		client.setIsDesktop( true );

		long logged = client.users_getLoggedInUser();
		List<Long> list_u = Collections.singletonList( logged );
		UsersGetInfoResponse response = client.users_getInfo( list_u,
				EnumSet.of( ProfileField.NAME,
						    ProfileField.PIC_SMALL,
						    ProfileField.STATUS,
						    ProfileField.PIC_BIG ) );

		String name = response.getUser().get( 0 ).getName();

		Assert.assertNotNull( name );
		Assert.assertNotSame( "", name );
	}
}
