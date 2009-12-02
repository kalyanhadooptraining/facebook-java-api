package com.google.code.facebookapi;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FacebookApiUrls {

	protected static Log log = LogFactory.getLog( FacebookApiUrls.class );

	public static final String FB_SERVER = "api.facebook.com/restserver.php";
	public static final String SERVER_ADDR = "http://" + FB_SERVER;
	public static final String HTTPS_SERVER_ADDR = "https://" + FB_SERVER;
	public static final URL SERVER_URL;
	public static final URL HTTPS_SERVER_URL;
	public static URL DEFAULT_SERVER_URL = null;
	public static URL DEFAULT_HTTPS_SERVER_URL = null;
	static {
		SERVER_URL = toURL( SERVER_ADDR );
		HTTPS_SERVER_URL = toURL( HTTPS_SERVER_ADDR );
		DEFAULT_SERVER_URL = SERVER_URL;
		DEFAULT_HTTPS_SERVER_URL = HTTPS_SERVER_URL;
	}

	public static URL toURL( String url ) {
		try {
			return new URL( url );
		}
		catch ( MalformedURLException ex ) {
			log.error( "MalformedURLException: " + ex.getMessage(), ex );
		}
		return null;
	}

	public static URL getDefaultServerUrl() {
		return DEFAULT_SERVER_URL;
	}

	public static void setDefaultServerUrl( URL newUrl ) {
		DEFAULT_SERVER_URL = newUrl;
	}

	public static URL getDefaultHttpsServerUrl() {
		return DEFAULT_HTTPS_SERVER_URL;
	}

	public static void setDefaultHttpsServerUrl( URL newUrl ) {
		DEFAULT_HTTPS_SERVER_URL = newUrl;
	}

}
