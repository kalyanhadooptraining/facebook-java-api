package com.google.code.facebookapi;

import org.junit.Ignore;
import org.junit.Test;

import com.Ostermiller.util.Browser;


public class Issue161FriendsGetSessionless {

	@Test
	@Ignore
	public void testFriendsGetSessionless() throws Exception {

		JUnitProperties props = new JUnitProperties();

		String FB_APP_API_KEY = props.getAPIKEY();
		String FB_APP_SECRET = props.getSECRET();

		FacebookJsonRestClient client = new FacebookJsonRestClient( FB_APP_API_KEY, FB_APP_SECRET );
		client.setIsDesktop( true );

		String token = client.auth_createToken();
		System.out.println( "Token: " + token );

		String session = facebookLogin( client, token );
		System.out.println( "Session: " + session );

		Long userId = client.users_getLoggedInUser();
		System.out.println( "User ID: " + userId );

		System.out.println( "Friends: " + client.friends_get( userId ) );
		
		System.out.println( "Friends: " + client.friends_get());
	}


	private static String facebookLogin( IFacebookRestClient<?> client, String token ) throws Exception {
		JUnitProperties props = new JUnitProperties();
		String FB_APP_API_KEY = props.getAPIKEY();
		Browser.init();
		Browser.displayURL( "https://login.facebook.com/login.php?api_key=" + FB_APP_API_KEY + "&v=1.0&popup=1&auth_token=" + token );

		return client.auth_getSession( token );
	}

}
