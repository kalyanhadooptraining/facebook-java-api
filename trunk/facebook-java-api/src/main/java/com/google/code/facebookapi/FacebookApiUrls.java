package com.google.code.facebookapi;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FacebookApiUrls {

	protected static Log log = LogFactory.getLog( FacebookApiUrls.class );

	public static URL SERVER_URL = null;
	public static URL HTTPS_SERVER_URL = null;
	static {
		try {
			SERVER_URL = new URL( IFacebookRestClient.SERVER_ADDR );
			HTTPS_SERVER_URL = new URL( IFacebookRestClient.HTTPS_SERVER_ADDR );
		}
		catch ( MalformedURLException ex ) {
			log.error( "MalformedURLException: " + ex.getMessage(), ex );
		}
	}

	public static URL getDefaultServerUrl() {
		return SERVER_URL;
	}

	public static void setDefaultServerUrl( URL newUrl ) {
		SERVER_URL = newUrl;
	}

	public static URL getDefaultHttpsServerUrl() {
		return HTTPS_SERVER_URL;
	}

	public static void setDefaultHttpsServerUrl( URL newUrl ) {
		HTTPS_SERVER_URL = newUrl;
	}

}
