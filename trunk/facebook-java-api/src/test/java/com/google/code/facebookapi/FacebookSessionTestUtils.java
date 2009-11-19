package com.google.code.facebookapi;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookSessionTestUtils {

	protected static Logger logger = Logger.getLogger( FacebookSessionTestUtils.class );

	public static final String LOGIN_BASE_URL = "https://www.facebook.com/login.php";
	public static final String PERM_BASE_URL = "http://www.facebook.com/connect/prompt_permissions.php";

	public static JSONObject getValidSessionID( boolean generateSessionSecret ) throws FacebookException, HttpException, IOException, JSONException {
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

		Map<String,String> params = new HashMap<String,String>();
		params.put( "api_key", apikey );
		params.put( "v", "1.0" );
		params.put( "auth_token", auth_token );
		String queryString = BasicClientHelper.delimit( params.entrySet(), "&", "=", true ).toString();
		GetMethod get = new GetMethod( LOGIN_BASE_URL + "?" + queryString );
		http.executeMethod( get );

		// 'submit' login popup/window
		PostMethod post = new PostMethod( LOGIN_BASE_URL );
		post.addParameter( new NameValuePair( "login_attempt", "1" ) );
		post.addParameter( new NameValuePair( "version", "1.0" ) );
		post.addParameter( new NameValuePair( "auth_token", auth_token ) );
		post.addParameter( new NameValuePair( "api_key", apikey ) );
		// return_session=0/1, req_perms="", session_key_only=0/1
		// ??? lsd=DhB8X
		// ??? login_attempt=1
		post.addParameter( new NameValuePair( "email", properties.getEMAIL() ) );
		post.addParameter( new NameValuePair( "pass", properties.getPASS() ) );
		http.executeMethod( post );

		// assume success, and try to attain valid session now
		client.auth_getSession( auth_token, generateSessionSecret );
		String session_key = client.getCacheSessionKey();
		String session_secret = client.getCacheSessionSecret();
		JSONObject out = new JSONObject();
		out.put( "session_key", client.getCacheSessionKey() );
		out.put( "uid", client.getCacheUserId() );
		out.put( "expires", client.getCacheSessionExpires() / 1000 );
		out.put( "secret", client.getCacheSessionSecret() );
		return out;
	}

	public static <T extends IFacebookRestClient> T getSessionlessValidClient( Class<T> clientReturnType ) {
		return getSessionlessIFacebookRestClient( clientReturnType );
	}

	public static <T extends IFacebookRestClient> T getValidClient( Class<T> clientReturnType ) throws IOException, FacebookException, JSONException {
		return getValidClient( clientReturnType, false );
	}

	@SuppressWarnings("unchecked")
	public static <T extends IFacebookRestClient> T getValidClient( Class<T> clientReturnType, boolean generateSessionSecret ) throws IOException, FacebookException,
			JSONException {
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
			JSONObject session_info = FacebookSessionTestUtils.getValidSessionID( generateSessionSecret );
			session_key = session_info.getString( "session_key" );
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

	public static void requirePerm( Permission perm, IFacebookRestClient client ) throws FacebookException {
		if ( !client.users_hasAppPermission( perm ) ) {
			// create http client
			HttpClient http = new HttpClient();
			http.setParams( new HttpClientParams() );
			http.setState( new HttpState() );

			// 'open' login popup/window
			Map<String,String> params = new HashMap<String,String>();
			params.put( "api_key", client.getApiKey() );
			params.put( "v", "1.0" );
			params.put( "fbconnect", "true" );
			params.put( "extern", "1" );
			params.put( "ext_perm", perm.getName() );
			params.put( "next", "http://www.facebook.com/connect/login_success.html?xxRESULTTOKENxx" );
			String queryString = BasicClientHelper.delimit( params.entrySet(), "&", "=", true ).toString();
			String url = PERM_BASE_URL + "?" + queryString;
			throw new IllegalStateException( "Require extended permission " + perm.getName() + "; please visit: " + url );
		}
	}

}
