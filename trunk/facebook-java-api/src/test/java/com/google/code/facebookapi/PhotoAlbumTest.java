package com.google.code.facebookapi;

import org.junit.Test;
import org.w3c.dom.Document;

public class PhotoAlbumTest {

	@Test
	public void test_PhotoAlbumList() throws Exception {
		IFacebookRestClient<Document> client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );

		Long userId = client.users_getLoggedInUser();
		Document doc = client.photos_getAlbums( userId );
	}

}
