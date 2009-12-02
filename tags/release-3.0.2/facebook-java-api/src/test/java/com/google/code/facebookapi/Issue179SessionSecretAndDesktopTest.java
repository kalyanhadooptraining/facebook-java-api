package com.google.code.facebookapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.Test;

public class Issue179SessionSecretAndDesktopTest {

	@Test
	public void testCreateSessionSecretAndUseIt() throws Exception {
		JSONObject session_info = FacebookSessionTestUtils.attainSession();
		String apikey = session_info.getString( "api_key" );
		String sessionKey = session_info.getString( "session_key" );
		String sessionSecret = session_info.getString( "ss" );

		// restrictedClient is simulating construction of the client on the
		// desktop app using the session secret instead of the real secret.
		FacebookJsonRestClient restrictedClient = new FacebookJsonRestClient( apikey, sessionSecret, sessionKey, true );

		assertEquals( "Session Secret ending in __ should have been auto-detected", true, restrictedClient.isDesktop() );

		restrictedClient.friends_get();

		// For some methods, you will get a failure if you're using a
		// generated session secret, for security reasons
		try {
			restrictedClient.admin_getAppPropertiesMap( Arrays.asList( ApplicationProperty.APP_ID, ApplicationProperty.APPLICATION_NAME ) );
			fail( "Restricted Client shouldn't be able to call admin_getAppProperties" );
		}
		catch ( FacebookException ex ) {
			assertEquals( ErrorCode.API_EC_SESSION_SECRET_NOT_ALLOWED, ex.getCode() );
		}
	}

}
