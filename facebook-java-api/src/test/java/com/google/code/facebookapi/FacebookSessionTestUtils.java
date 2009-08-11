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
import org.w3c.dom.Document;

public class FacebookSessionTestUtils {
	
	public static String lastTokenUsed;

	public static String getValidSessionID() throws IOException, FacebookException {
		JUnitProperties properties = new JUnitProperties();
		IFacebookRestClient<Document> client = new FacebookXmlRestClient( properties.getAPIKEY(), properties.getSECRET() );
		String token = client.auth_createToken();

		HttpClient http = new HttpClient();
        http.setParams(new HttpClientParams());
        http.setState(new HttpState());

        final String LOGIN = "http://www.facebook.com/login.php";
        
        GetMethod get = new GetMethod(LOGIN + "?api_key=" + properties.getAPIKEY() + "&v=1.0&auth_token=" + token );

        http.executeMethod(get);

        PostMethod post = new PostMethod(LOGIN);
        post.addParameter(new NameValuePair("api_key", properties.getAPIKEY()));
        post.addParameter(new NameValuePair("v", "1.0"));
        post.addParameter(new NameValuePair("auth_token", token));
        post.addParameter(new NameValuePair("email", properties.getEMAIL()));
        post.addParameter(new NameValuePair("pass", properties.getPASS()));

        http.executeMethod(post);

		String sessionID = client.auth_getSession( token );
		lastTokenUsed = token;
		return sessionID;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IFacebookRestClient> T getSessionlessValidClient( Class<T> clientReturnType ) {
		return getSessionlessIFacebookRestClient( clientReturnType );
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
			JUnitProperties properties = new JUnitProperties();
			Constructor<T> clientConstructor = clientReturnType.getConstructor( String.class, String.class, String.class );
			return clientConstructor.newInstance( properties.getAPIKEY(), properties.getSECRET(), sessionID );
		}
		catch ( Exception ex ) {
			throw new RuntimeException( "Couldn't create relevant IFacebookRestClient using reflection", ex );
		}
	}
	
	@SuppressWarnings("unchecked")
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
