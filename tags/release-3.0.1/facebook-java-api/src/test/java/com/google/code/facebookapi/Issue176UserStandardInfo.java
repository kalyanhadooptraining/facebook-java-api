package com.google.code.facebookapi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.code.facebookapi.schema.User;
import com.google.code.facebookapi.schema.UsersGetStandardInfoResponse;

public class Issue176UserStandardInfo {

	@Test
	public void testGetStandardInfo() throws FacebookException, IOException  {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );
		
		List<Long> userIds = Collections.singletonList( client.users_getLoggedInUser() );
		List<ProfileField> profileFields = new ArrayList<ProfileField>();
		
		profileFields.add( ProfileField.NAME );
		
		UsersGetStandardInfoResponse response = client.users_getStandardInfo( userIds, profileFields );
		
		User user = response.getStandardUserInfo().get( 0 );
		
		assertNotNull( user.getName() );
		assertNotSame( "", user.getName().trim() );
	}
	
}