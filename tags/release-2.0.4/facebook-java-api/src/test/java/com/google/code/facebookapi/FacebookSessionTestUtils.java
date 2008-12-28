package com.google.code.facebookapi;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;

import org.w3c.dom.Document;

import com.Ostermiller.util.Browser;

public class FacebookSessionTestUtils {

	private static String APIKEY = System.getProperty( "APIKEY" );
	private static String SECRET = System.getProperty( "SECRET" );
	
	public static String lastTokenUsed;

	public static String getValidSessionID() throws IOException, FacebookException {
		IFacebookRestClient<Document> client = new FacebookXmlRestClient( APIKEY, SECRET );
		String token = client.auth_createToken();

		try {
			URI hitURL = new URI( "http://api.new.facebook.com/login.php?api_key=" + APIKEY + "&v=1.0&auth_token=" + token );
			// With Java 6, we could use the Desktop API to open the browser
			// Desktop.getDesktop().browse( hitURL );
			// For Java 5, we're using a 3rd party utility.
			Browser.init();
			Browser.displayURL( hitURL.toString() );
			Thread.sleep( 2000 ); // Give the token registration a 2 second headstart
		}
		catch ( InterruptedException ex ) {
			throw new RuntimeException( "InterruptedException while waiting for browser to open" );
		}
		catch ( URISyntaxException ex ) {
			throw new RuntimeException( "Invalid Facebook URI", ex );
		}

		String sessionID = client.auth_getSession( token );
		lastTokenUsed = token;
		return sessionID;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IFacebookRestClient> T getValidClient( Class<T> clientReturnType ) throws IOException, FacebookException {
		final String SESSION_PREFERENCE = "/com/google/code/facebookapi/test/sessionID";

		IFacebookRestClient<T> client = null;

		String sessionID = Preferences.userRoot().get( SESSION_PREFERENCE, null );
		if ( sessionID != null ) {
			try {
				client = getIFacebookRestClient( clientReturnType, sessionID );
				// Test out the session ID
				client.friends_get();
			}
			catch ( FacebookException ex ) {
				Preferences.userRoot().remove( SESSION_PREFERENCE );
				client = null;
				System.out.println( "Session ID is out of date; generate a new one" );
			}
		}

		if ( client == null ) {
			sessionID = FacebookSessionTestUtils.getValidSessionID();
			Preferences.userRoot().put( SESSION_PREFERENCE, sessionID );
			client = getIFacebookRestClient( clientReturnType, sessionID );
		}

		return (T) client;
	}

	@SuppressWarnings("unchecked")
	private static <T extends IFacebookRestClient> T getIFacebookRestClient( Class<T> clientReturnType, String sessionID ) {
		try {
			Constructor<T> clientConstructor = clientReturnType.getConstructor( String.class, String.class, String.class );
			return clientConstructor.newInstance( APIKEY, SECRET, sessionID );
		}
		catch ( Exception ex ) {
			throw new RuntimeException( "Couldn't create relevant IFacebookRestClient using reflection", ex );
		}
	}

}
