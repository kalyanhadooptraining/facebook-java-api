package com.google.code.facebookapi;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		
		
		/*****
		 * Enable DEBUG / FINEST level logging for the whole package
		 */
		//Could have just picked a single class, but this will deal with all our classes.
		Logger packageLogger = Logger.getLogger( "com.google.code.facebookapi" );
		packageLogger.setLevel( Level.FINEST );
		ConsoleHandler console = new ConsoleHandler();
		//Just because the logger's level is set doesn't mean that you'll
		//see the messages on the console. The handler can filter messages
		//out. So, let's set to FINEST / ALL level so that doesn't happen.
		console.setLevel( Level.ALL );
		packageLogger.addHandler( console );
		Log extensibleClientLog4j = LogFactory.getLog( ExtensibleClient.class );
		extensibleClientLog4j.debug( "Debug logging has been enabled by configuring the underlying logging provider." );
		/*****
		 * END Enable DEBUG / FINEST level logging for the whole package
		 */
		

		FacebookJsonRestClient client = new FacebookJsonRestClient( FB_APP_API_KEY, FB_APP_SECRET );

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
