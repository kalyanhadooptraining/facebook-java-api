package com.google.code.facebookapi;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
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
	public static final String PREFS_SESSIONS_NODE = "/com/google/code/facebookapi/test_sessions";

	public static final JUnitProperties junitProperties = new JUnitProperties();

	public static void clearSessions() throws BackingStoreException {
		Preferences root = Preferences.userRoot();
		Preferences prefs = root.node( PREFS_SESSIONS_NODE );
		for ( String key : prefs.keys() ) {
			prefs.remove( key );
		}
		prefs.flush();
	}

	public static JSONObject attainSession( boolean generateSessionSecret ) throws FacebookException, HttpException, IOException, JSONException, BackingStoreException {
		Preferences root = Preferences.userRoot();
		Preferences prefs = root.node( PREFS_SESSIONS_NODE );
		prefs.flush();
		String apikey = junitProperties.getAPIKEY();
		String key = "session:" + generateSessionSecret + ":" + apikey;
		String val = prefs.get( key, null );
		if ( val != null ) {
			JSONObject out = new JSONObject( val );
			long exp = out.getLong( "expires" );
			if ( exp * 1000 < System.currentTimeMillis() ) {
				return out;
			}
			// FIXME: should we validate the session_key even more?
		}
		JSONObject out = attainSessionRaw( generateSessionSecret );
		prefs.put( key, out.toString() );
		prefs.flush();
		return out;
	}

	public static JSONObject attainSessionRaw( boolean generateSessionSecret ) throws FacebookException, HttpException, IOException, JSONException {
		String apikey = junitProperties.getAPIKEY();
		String secret = junitProperties.getSECRET();
		if ( generateSessionSecret ) {
			apikey = junitProperties.getDESKTOP_APIKEY();
			secret = junitProperties.getDESKTOP_SECRET();
		}

		// attain auth_token
		FacebookXmlRestClient client = new FacebookXmlRestClient( apikey, secret );
		String auth_token = client.auth_createToken();

		// create http client
		HttpClient http = new HttpClient();
		http.setParams( new HttpClientParams() );
		http.setState( new HttpState() );

		{
			// 'open' login popup/window
			Map<String,String> params = new HashMap<String,String>();
			params.put( "api_key", apikey );
			params.put( "v", "1.0" );
			params.put( "auth_token", auth_token );
			String queryString = BasicClientHelper.delimit( params.entrySet(), "&", "=", true ).toString();
			GetMethod get = new GetMethod( LOGIN_BASE_URL + "?" + queryString );
			http.executeMethod( get );
			logger.debug( "uri: " + get.getURI() );
		}

		{
			// 'submit' login popup/window
			PostMethod post = new PostMethod( LOGIN_BASE_URL );
			post.addParameter( new NameValuePair( "login_attempt", "1" ) );
			post.addParameter( new NameValuePair( "version", "1.0" ) );
			post.addParameter( new NameValuePair( "auth_token", auth_token ) );
			post.addParameter( new NameValuePair( "api_key", apikey ) );
			// return_session=0/1, req_perms="", session_key_only=0/1
			// ??? lsd=DhB8X
			// ??? login_attempt=1
			post.addParameter( new NameValuePair( "email", junitProperties.getEMAIL() ) );
			post.addParameter( new NameValuePair( "pass", junitProperties.getPASS() ) );
			http.executeMethod( post );
		}

		{
			// assume success, and try to attain valid session now
			client.auth_getSession( auth_token, generateSessionSecret );
			JSONObject out = new JSONObject();
			out.put( "api_key", apikey );
			out.put( "session_key", client.getCacheSessionKey() );
			out.put( "uid", client.getCacheUserId() );
			out.put( "expires", client.getCacheSessionExpires() );
			if ( generateSessionSecret ) {
				out.put( "secret", client.getCacheSessionSecret() );
			} else {
				out.put( "secret", secret );
			}
			return out;
		}
	}

	public static <T extends IFacebookRestClient> T getSessionlessValidClient( Class<T> clientReturnType ) {
		return getSessionlessIFacebookRestClient( clientReturnType );
	}

	public static <T extends IFacebookRestClient> T getValidClient( Class<T> clientReturnType ) throws IOException, FacebookException, JSONException {
		return getValidClient( clientReturnType, false );
	}

	public static <T extends IFacebookRestClient> T getValidClient( Class<T> clientReturnType, boolean generateSessionSecret ) {
		try {
			JSONObject session_info = attainSession( generateSessionSecret );
			return createRestClient( clientReturnType, session_info );
		}
		catch ( Exception ex ) {
			throw BasicClientHelper.runtimeException( ex );
		}
	}

	private static <T extends IFacebookRestClient> T createRestClient( Class<T> clientReturnType, JSONObject session_info ) throws JSONException {
		String apikey = session_info.getString( "api_key" );
		String secret = session_info.getString( "secret" );
		String sessionkey = session_info.getString( "session_key" );
		return createRestClient( clientReturnType, apikey, secret, sessionkey );
	}

	private static <T extends IFacebookRestClient> T createRestClient( Class<T> clientReturnType, String apikey, String secret, String sessionkey ) {
		try {
			Constructor<T> clientConstructor = clientReturnType.getConstructor( String.class, String.class, String.class );
			return clientConstructor.newInstance( apikey, secret, sessionkey );
		}
		catch ( Exception ex ) {
			throw new RuntimeException( "Couldn't create relevant IFacebookRestClient using reflection", ex );
		}
	}

	private static <T extends IFacebookRestClient> T getSessionlessIFacebookRestClient( Class<T> clientReturnType ) {
		try {
			Constructor<T> clientConstructor = clientReturnType.getConstructor( String.class, String.class );
			return clientConstructor.newInstance( junitProperties.getAPIKEY(), junitProperties.getSECRET() );
		}
		catch ( Exception ex ) {
			throw new RuntimeException( "Couldn't create relevant IFacebookRestClient using reflection", ex );
		}
	}

	public static <T extends IFacebookRestClient> void requirePerm( Permission perm, T client ) throws FacebookException {
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
