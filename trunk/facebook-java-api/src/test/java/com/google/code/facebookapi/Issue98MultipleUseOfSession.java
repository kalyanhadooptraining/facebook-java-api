package com.google.code.facebookapi;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.junit.Test;

public class Issue98MultipleUseOfSession {

	@Test
	public void testMultipleUseOfSession() throws Exception {
		JUnitProperties properties = new JUnitProperties();
		String APP_KEY = properties.getAPIKEY();
		String APP_SECRET = properties.getSECRET();
		String EMAIL = properties.getEMAIL();
		String PASSWORD = properties.getPASS();

		FacebookJaxbRestClient client = new FacebookJaxbRestClient( APP_KEY, APP_SECRET );
		client.setIsDesktop( true );

		HttpClient http = new HttpClient();

		String token = client.auth_createToken();

		HttpClientParams params = new HttpClientParams();
		HttpState initialState = new HttpState();

		http.setParams( params );
		http.setState( initialState );

		GetMethod get = new GetMethod( "http://www.facebook.com/login.php?api_key=" + APP_KEY + "&v=1.0&auth_token=" + token );
		http.executeMethod( get );

		PostMethod post = new PostMethod( "http://www.facebook.com/login.php" );
		post.addParameter( "api_key", APP_KEY );
		post.addParameter( "v", "1.0" );
		post.addParameter( "auth_token", token );
		post.addParameter( "email", EMAIL );
		post.addParameter( "pass", PASSWORD );

		http.executeMethod( post );

		String sessionID = client.auth_getSession( token );

		// try 1
		try {
			Long uid = client.users_getLoggedInUser();
			System.out.println( "UserID: " + uid );

			client.events_get( uid, null, null, null );
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

		// try 2 with new client constructed with a session ID
		try {
			FacebookJaxbRestClient newClient = new FacebookJaxbRestClient( APP_KEY, APP_SECRET, sessionID );
			newClient.setIsDesktop( true );

			Long uid = newClient.users_getLoggedInUser();
			System.out.println( "New CLIENT UserID: " + uid );

			newClient.events_get( uid, null, null, null );
			System.out.println( newClient.getRawResponse() );
			System.out.println( "NEW CLIENT Events: " + newClient.getRawResponse() );
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
