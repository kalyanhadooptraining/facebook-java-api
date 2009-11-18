package com.google.code.facebookapi;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.prefs.Preferences;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

public class FacebookSessionTestUtils {

	public static final String LOGIN_BASE_URL = "http://www.facebook.com/login.php";

	public static String[] getValidSessionID( boolean generateSessionSecret ) throws IOException, FacebookException {
		JUnitProperties properties = new JUnitProperties();

		String apikey = properties.getAPIKEY();
		String secret = properties.getSECRET();
		if ( generateSessionSecret ) {
			apikey = properties.getDESKTOP_APIKEY();
			secret = properties.getDESKTOP_SECRET();
		}

		// attain auth_token
		FacebookXmlRestClient client = new FacebookXmlRestClient( apikey, secret );
		String auth_token = client.auth_createToken();

		// create http client
		HttpClient http = new HttpClient();
		http.setParams( new HttpClientParams() );
		http.setState( new HttpState() );


		// 'open' login popup/window
		GetMethod get = new GetMethod( LOGIN_BASE_URL + "?api_key=" + apikey + "&v=1.0&auth_token=" + auth_token );
		http.executeMethod( get );

		// 'submit' login popup/window
		PostMethod post = new PostMethod( LOGIN_BASE_URL );
		post.addParameter( new NameValuePair( "api_key", apikey ) );
		post.addParameter( new NameValuePair( "v", "1.0" ) );
		post.addParameter( new NameValuePair( "auth_token", auth_token ) );
		post.addParameter( new NameValuePair( "email", properties.getEMAIL() ) );
		post.addParameter( new NameValuePair( "pass", properties.getPASS() ) );
		http.executeMethod( post );

		// assume success, and try to attain valid session now
		client.auth_getSession( auth_token, generateSessionSecret );
		String session_key = client.getCacheSessionKey();
		String session_secret = client.getCacheSessionSecret();
		return new String[] { session_key, session_secret };
	}

	public static <T extends IFacebookRestClient> T getSessionlessValidClient( Class<T> clientReturnType ) {
		return getSessionlessIFacebookRestClient( clientReturnType );
	}

	public static <T extends IFacebookRestClient> T getValidClient( Class<T> clientReturnType ) throws IOException, FacebookException {
		return getValidClient( clientReturnType, false );
	}

	@SuppressWarnings("unchecked")
	public static <T extends IFacebookRestClient> T getValidClient( Class<T> clientReturnType, boolean generateSessionSecret ) throws IOException, FacebookException {
		final String SESSION_PREFERENCE = "/com/google/code/facebookapi/test/sessionID";

		IFacebookRestClient<T> client = null;

		Preferences prefs = Preferences.userRoot();

		String session_key = prefs.get( SESSION_PREFERENCE, null );
		if ( session_key != null ) {
			try {
				client = getIFacebookRestClient( clientReturnType, session_key );
				// Test out the session ID
				client.friends_get();
			}
			catch ( FacebookException ex ) {
				prefs.remove( SESSION_PREFERENCE );
				client = null;
				System.out.println( "Session ID is out of date; generate a new one" );
			}
		}

		if ( client == null ) {
			String[] session_info = FacebookSessionTestUtils.getValidSessionID( generateSessionSecret );
			session_key = session_info[0];
			prefs.put( SESSION_PREFERENCE, session_key );
			client = getIFacebookRestClient( clientReturnType, session_key );
		}

		return (T) client;
	}

	private static <T extends IFacebookRestClient> T getIFacebookRestClient( Class<T> clientReturnType, String sessionID ) {
		try {
			JUnitProperties properties = new JUnitProperties();
			Constructor<T> clientConstructor = clientReturnType.getConstructor( String.class, String.class, String.class );
			return clientConstructor.newInstance( properties.getAPIKEY(), properties.getSECRET(), sessionID );
		}
		catch ( Exception ex ) {
			throw new RuntimeException( "Couldn't create relevant IFacebookRestClient using reflection", ex );
		}
	}

	private static <T extends IFacebookRestClient> T getSessionlessIFacebookRestClient( Class<T> clientReturnType ) {
		try {
			JUnitProperties properties = new JUnitProperties();
			Constructor<T> clientConstructor = clientReturnType.getConstructor( String.class, String.class );
			return clientConstructor.newInstance( properties.getAPIKEY(), properties.getSECRET() );
		}
		catch ( Exception ex ) {
			throw new RuntimeException( "Couldn't create relevant IFacebookRestClient using reflection", ex );
		}
	}

}
