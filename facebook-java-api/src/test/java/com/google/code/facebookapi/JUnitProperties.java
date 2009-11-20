package com.google.code.facebookapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class JUnitProperties {

	protected static Logger logger = Logger.getLogger( JUnitProperties.class );

	private Properties properties = new Properties();

	public String getAPIKEY() {
		return getProperty( "APIKEY" );
	}

	public String getSECRET() {
		return getProperty( "SECRET" );
	}

	public String getUID() {
		return getProperty( "UID" );
	}

	@SuppressWarnings("unchecked")
	public void clearSessions() throws IOException {
		Set<String> set = new HashSet( properties.keySet() );
		for ( String key : set ) {
			if ( key.startsWith( "sess_" ) ) {
				properties.remove( key );
			}
		}
		storeProperties();
	}

	public JSONObject loadSession() throws JSONException {
		return loadSession( getUID(), getAPIKEY() );
	}

	public static String key( String uid, String apikey ) {
		return "sess_" + uid + "_" + apikey;
	}

	public JSONObject loadSession( String uid, String apikey ) throws JSONException {
		String key = key( uid, apikey );
		String str = properties.getProperty( key );
		if ( str != null ) {
			JSONObject out = new JSONObject( str );
			long expires = out.getLong( "expires" );
			if ( expires == 0 || expires * 1000 < System.currentTimeMillis() ) {
				return out;
			}
		}
		return null;
	}

	public void storeSession( JSONObject session ) throws JSONException, IOException {
		String apikey = session.getString( "api_key" );
		String uid = session.getString( "uid" );
		String key = key( uid, apikey );
		session.put( "pref_key", key );
		properties.put( key, session.toString() );
		storeProperties();
	}

	public void loadProperties() throws IOException {
		logger.debug( "loadProperties()" );
		File file = getPropertiesFile();
		FileInputStream in = new FileInputStream( file );
		properties.clear();
		properties.load( in );
	}

	public void storeProperties() throws IOException {
		logger.debug( "storeProperties()" );
		File file = getPropertiesFile();
		FileOutputStream out = new FileOutputStream( file );
		properties.store( out, null );
	}

	public JUnitProperties() {
		try {
			loadProperties();
		}
		catch ( IOException ex ) {
			throw new RuntimeException( ex );
		}
	}

	public static File getPropertiesFile() {
		File userhome = new File( System.getProperty( "user.home" ) );
		File junitprop = new File( userhome, ".fbja-junit.properties" );
		return junitprop;
	}

	public String getProperty( String name ) {
		return loadProperty( name, properties );
	}

	public static String loadProperty( String name, Properties properties ) {
		String out = properties.getProperty( name );
		if ( out == null ) {
			// "junit.properties must contain values for:
			// APIKEY, SECRET, UID
			throw new RuntimeException( String.format( "junit.properties missing value for %s", name ) );
		}
		return out;
	}

}
