package com.google.code.facebookapi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class Issue175LinksPosting {

	@Test
	@Ignore
	public void testPostLink() throws FacebookException, IOException  {
		FacebookXmlRestClient client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		
		Long postID = client.links_post( client.users_getLoggedInUser(), "http://www.google.com", "Testing that the link posting feature correctly posts a link to google" );
		
		assertNotNull( postID );
		assertTrue( postID > 0 );
	}
	
}