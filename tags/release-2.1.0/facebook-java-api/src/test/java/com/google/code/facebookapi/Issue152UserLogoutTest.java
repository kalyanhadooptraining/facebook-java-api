package com.google.code.facebookapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class Issue152UserLogoutTest {

	@Test
	public void testUserLogoutFacebookException() throws FacebookException, IOException  {
		FacebookXmlRestClient client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		
		client.auth_expireSession();
		
		try {
			client.auth_getSession( FacebookSessionTestUtils.lastTokenUsed );
			fail("Session is expired, getSession should fail");
		} catch( FacebookException ex ) {
			//Success
			assertEquals(ErrorCode.SESSION_INVALID, ex.getCode());
			System.out.println("Logout success, FacebookException thrown");
		}
		
		
		
	}
	
}
