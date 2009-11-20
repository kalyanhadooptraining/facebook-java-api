package com.google.code.facebookapi;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

@Deprecated
public class SessionPrefs {

	protected static Logger logger = Logger.getLogger( SessionPrefs.class );

	public static final String PREFS_NODE = "/com/google/code/facebookapi";
	public static final String PREFIX_TEST_SESSION = "tsess:";

	public static final Preferences userRoot = Preferences.userRoot();
	public static final Preferences prefs = userRoot.node( PREFS_NODE );
	public static final JUnitProperties junitProperties = new JUnitProperties();

	public static void clearSessions() throws BackingStoreException {
		for ( String key : prefs.keys() ) {
			if ( key.startsWith( PREFIX_TEST_SESSION ) ) {
				prefs.remove( key );
			}
		}
		prefs.flush();
	}

	public static JSONObject loadSession( boolean desktop ) throws JSONException {
		// FIXME: integrate uid into key somehow
		String apikey = desktop ? junitProperties.getDESKTOP_APIKEY() : junitProperties.getAPIKEY();
		String uid = junitProperties.getUID();
		String key = PREFIX_TEST_SESSION + uid + ":" + apikey;
		String val = prefs.get( key, null );
		if ( val != null ) {
			JSONObject out = new JSONObject( val );
			long exp = out.getLong( "expires" );
			if ( exp == 0 || exp * 1000 < System.currentTimeMillis() ) {
				return out;
			}
			// FIXME: should we validate the session_key even more?
		}
		return null;
	}

	public static void storeSession( JSONObject session ) throws JSONException, BackingStoreException {
		// FIXME: integrate uid into key somehow
		String apikey = session.getString( "api_key" );
		String uid = session.getString( "uid" );
		String key = PREFIX_TEST_SESSION + uid + ":" + apikey;
		session.put( "pref_key", key );
		logger.debug( "storeSession: " + session );
		String val = session.toString();
		prefs.put( key, val );
		prefs.flush();
		if ( !val.equals( prefs.get( key, null ) ) ) {
			logger.error( "Trouble saving session" );
		}
	}

}
