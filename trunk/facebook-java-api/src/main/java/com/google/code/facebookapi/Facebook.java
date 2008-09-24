package com.google.code.facebookapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

/**
 * Utility class to handle authorization and authentication of requests. Objects of this class are meant to be created for every request. They are stateless and are not
 * supposed to be kept in the session.
 * 
 * @deprecated deprecated, renaming to FacebookWebappHelper; this implementation also uses deprecated FacebookRestClient
 */
@Deprecated
public class Facebook extends FacebookWebappHelper<Document> {

	public Facebook( HttpServletRequest request, HttpServletResponse response, String apiKey, String secret ) {
		super( request, response, apiKey, secret, new FacebookRestClient( apiKey, secret ) );
	}

}
