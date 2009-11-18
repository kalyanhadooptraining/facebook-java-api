package com.google.code.facebookapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class JUnitProperties {

	protected static Logger logger = Logger.getLogger( JUnitProperties.class );

	private String apikey;
	private String secret;
	private String apikeyDesktop;
	private String secretDesktop;
	private String email;
	private String pass;

	public String getAPIKEY() {
		return apikey;
	}

	public String getSECRET() {
		return secret;
	}

	public String getDESKTOP_APIKEY() {
		return apikeyDesktop;
	}

	public String getDESKTOP_SECRET() {
		return secretDesktop;
	}

	public String getEMAIL() {
		return email;
	}

	public String getPASS() {
		return pass;
	}

	public JUnitProperties() {
		Properties properties = loadProperties();
		apikey = loadProperty( "APIKEY", properties );
		secret = loadProperty( "SECRET", properties );
		apikeyDesktop = loadProperty( "DESKTOP_APIKEY", properties );
		secretDesktop = loadProperty( "DESKTOP_SECRET", properties );
		email = loadProperty( "USER", properties );
		pass = loadProperty( "PASS", properties );
	}

	public static InputStream loadPropertiesStream() {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream( "/junit.properties" );
	}

	public static InputStream loadPropertiesStream2() {
		File userhome = new File( System.getProperty( "user.home" ) );
		File junitprop = new File( userhome, ".fbja-junit.properties" );
		try {
			return new FileInputStream( junitprop );
		}
		catch ( FileNotFoundException ex ) {
			throw new RuntimeException( "File not found: " + junitprop, ex );
		}
	}

	public static Properties loadProperties() {
		InputStream stream = loadPropertiesStream2();
		if ( stream == null ) {
			throw new RuntimeException( "Could not locate junit.properties on the root directory of the classpath" );
		}
		Properties out = new Properties();
		try {
			out.load( stream );
		}
		catch ( IOException ex ) {
			throw new RuntimeException( "Located junit.properties but could not load from it", ex );
		}
		try {
			stream.close();
		}
		catch ( IOException ex ) {
			logger.warn( "Trouble closing stream", ex );
		}
		return out;
	}

	public static String loadProperty( String name, Properties properties ) {
		String out = properties.getProperty( name );
		if ( out == null ) {
			// "junit.properties must contain values for:
			// APIKEY, SECRET
			// DESKTOP_APIKEY, DESKTOP_SECRET (for testing 'desktop mode' applications)
			// EMAIL and PASS (your Facebook password)
			throw new RuntimeException( String.format( "junit.properties missing value for %s", name ) );
		}
		return out;
	}

}
