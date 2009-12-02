package com.google.code.facebookapi;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.EnumSet;

import org.junit.Test;

public class Issue199IncorrectSignatureTest {

	@Test
	public void testUsersGetInfoWithZeroUserIds() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );
		client.users_getInfo( new ArrayList<Long>(), EnumSet.of( ProfileField.NAME ) );
	}

	@Test
	public void testDataGetObjectsWithZeroObjectsIds() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );
		try {
			client.data_getObjects( new ArrayList<Long>() );
			fail( "FacebookException expected" );
		}
		catch ( FacebookException ex ) {
			// This is the Facebook server complaining about the contents of the parameter.
			// It has every right to complain, we're passing an empty list in!
			ex.getMessage().contains( "Invalid parameter: object ids" );
		}
	}

	@Test
	public void testPhotosGetTagsWithZeroPhotoIds() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );
		client.photos_getTags( new ArrayList<String>() );
	}

}
