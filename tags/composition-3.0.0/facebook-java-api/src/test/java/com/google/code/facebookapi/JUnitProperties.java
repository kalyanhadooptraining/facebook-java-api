package com.google.code.facebookapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JUnitProperties {
	
	Properties properties;
	
	public JUnitProperties() {
		properties = new Properties();
		InputStream propertiesInputStream = getClass().getResourceAsStream( "/junit.properties" );
		if( propertiesInputStream == null ) {
			throw new RuntimeException( "Could not locate junit.properties on the root directory of the classpath" );
		}
		try {
			properties.load( propertiesInputStream );
		} catch( IOException ex ) {
			throw new RuntimeException( "Located junit.properties but could not load from it", ex );
		}
		
		if( getAPIKEY() == null || getSECRET() == null || getEMAIL() == null || getPASS() == null ) {
			throw new RuntimeException( "junit.properties must contain values for APIKEY, SECRET, EMAIL and PASS (your Facebook password)" );
		}
	}
	
	public String getAPIKEY() {
		return properties.getProperty( "APIKEY" );
	}
	
	public String getSECRET() {
		return properties.getProperty( "SECRET" );
	}
	
	public String getEMAIL() {
		return properties.getProperty( "EMAIL" );
	}
	
	public String getPASS() {
		return properties.getProperty( "PASS" );
	}
	
}