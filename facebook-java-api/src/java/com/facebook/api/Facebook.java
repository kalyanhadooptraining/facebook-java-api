package com.facebook.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @deprecated we are renaming the class to FacebookWebappHelper, please use that for now.
 */
@Deprecated
public class Facebook extends FacebookWebappHelper {

	public Facebook( HttpServletRequest request, HttpServletResponse response, String apiKey, String secret ) {
		super( request, response, apiKey, secret );
	}

}
