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

	private FBAppConf appConf;
	private boolean ignoreCookies;

	public void init( FilterConfig filterConfig ) throws ServletException {
		if ( appConf == null ) {
			String appId = filterConfig.getInitParameter( "appId" );
			String apiKey = filterConfig.getInitParameter( "apiKey" );
			String secret = filterConfig.getInitParameter( "secret" );
			appConf = new FBAppConfBean( appId, apiKey, secret );
		}
		ignoreCookies = BooleanUtils.toBoolean( filterConfig.getInitParameter( "ignoreCookies" ) );
		init();
	}

	public void init( FBAppConf appConf, boolean noCookies ) {
		this.appConf = appConf;
		this.ignoreCookies = noCookies;
		init();
	}

	public void init() {
		// empty
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
		String rkey = "fbreq";
		FBWebRequest request = FbWebHelper.attainFBWebRequest( appConf, ignoreCookies, httpRequest );
		httpRequest.setAttribute( rkey, request );
		chain.doFilter( httpRequest, httpResponse );
	}

	// ---- Getters

	public FBAppConf getAppConf() {
		return appConf;
	}

	public void setAppConf( FBAppConf appConf ) {
		this.appConf = appConf;
	}

	public boolean isIgnoreCookies() {
		return ignoreCookies;
	}

	public void setIgnoreCookies( boolean noCookies ) {
		this.ignoreCookies = noCookies;
	}

}
