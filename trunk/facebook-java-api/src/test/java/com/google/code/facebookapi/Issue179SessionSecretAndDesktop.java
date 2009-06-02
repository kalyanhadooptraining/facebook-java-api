package com.google.code.facebookapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;


public class Issue179SessionSecretAndDesktop {

	@Test
	public void testCreateSessionSecretAndUseIt() throws FacebookException, IOException {
		JUnitProperties props = new JUnitProperties();

		String sessionKey = FacebookSessionTestUtils.getValidSessionID( true );
		
		//Just checking...
		assertEquals(sessionKey, FacebookSessionTestUtils.lastClientUsed.getCacheSessionKey());
		
		//Here's the generated session secret, ready to pass to your desktop app client
		String sessionSecret = FacebookSessionTestUtils.lastClientUsed.getSessionSecret();
		
		System.out.println(sessionSecret);
		
		//restrictedClient is simulating construction of the client on the
		//desktop app using the session secret instead of the real secret.
		FacebookJsonRestClient restrictedClient = new FacebookJsonRestClient(props.getDESKTOP_APIKEY(), sessionSecret, sessionKey);

		assertEquals( "Session Secret ending in __ should have been auto-detected",
				      true, restrictedClient.isDesktop() );
		
		restrictedClient.friends_get();
		
		//For some methods, you will get a failure if you're using a
		//generated session secret, for security reasons
		try {
			restrictedClient.fbml_setRefHandle("abc", "123");
			fail("Restricted Client shouldn't be able to call fbml_setRefHandle");
		} catch(FacebookException ex) {
			assertEquals((int)ErrorCode.GEN_PERMISSIONS_ERROR, ex.getCode());
		}
	}
	
}
