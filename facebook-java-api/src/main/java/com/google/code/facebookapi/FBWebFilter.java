package com.google.code.facebookapi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;

public class FBWebFilter implements Filter {

	// TODO: update cookies; make sure cookies are good and match request?
	// should this be knee jerk? or asked for by user

	// TODO: MAINTAINING JSESSIONID COOKIE sync across FBML/BROWSER cookies
	// jsessionid has to be same on fbml and browser


	private String apiKey;
	private String secret;
	private boolean noCookies;
	private boolean multiApp;
	private String skey;
	private String rkey;

	public void init( FilterConfig filterConfig ) throws ServletException {
		if ( apiKey == null ) {
			apiKey = filterConfig.getInitParameter( "apiKey" );
		}
		if ( secret == null ) {
			secret = filterConfig.getInitParameter( "secret" );
		}
		noCookies = BooleanUtils.toBoolean( filterConfig.getInitParameter( "noCookies" ) );
		multiApp = BooleanUtils.toBoolean( filterConfig.getInitParameter( "multiApp" ) );
		init();
	}

	public void init( String apiKey, String secret, boolean noCookies, boolean multiApp ) {
		this.apiKey = apiKey;
		this.secret = secret;
		this.noCookies = noCookies;
		this.multiApp = multiApp;
		init();
	}

	public void init() {
		if ( !multiApp ) {
			skey = "fbses";
			rkey = "fbreq";
		} else {
			skey = "fbses:" + apiKey;
			rkey = "fbreq:" + apiKey;
		}
	}

	public void destroy() {
		// empty
	}

	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
		if ( request instanceof HttpServletRequest && response instanceof HttpServletResponse ) {
			doFilter( (HttpServletRequest) request, (HttpServletResponse) response, chain );
		} else {
			chain.doFilter( request, response );
		}
	}

	public void doFilter( HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain ) throws IOException, ServletException {
		FbWebHelper.attainFBWebRequest( apiKey, secret, noCookies, rkey, skey, httpRequest );
		chain.doFilter( httpRequest, httpResponse );
	}

	// ---- Getters

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey( String apiKey ) {
		this.apiKey = apiKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret( String secret ) {
		this.secret = secret;
	}

	public boolean isNoCookies() {
		return noCookies;
	}

	public void setNoCookies( boolean noCookies ) {
		this.noCookies = noCookies;
	}

	public boolean isMultiApp() {
		return multiApp;
	}

	public void setMultiApp( boolean multiApp ) {
		this.multiApp = multiApp;
	}

}
