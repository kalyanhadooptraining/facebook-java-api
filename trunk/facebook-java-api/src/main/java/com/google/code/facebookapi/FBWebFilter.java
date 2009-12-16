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

public class FBWebFilter implements Filter {

	private String apiKey;
	private String secret;

	public void init( FilterConfig filterConfig ) throws ServletException {
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

	public void doFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain ) throws IOException, ServletException {
		// MAINTAINING FBSESSION INFORMATION:
		// 3 sources: FBRequestParams, FBConnectCookies, sessionObj
		// Values can be in requestScope or sessionScope

		// MAINTAINING JSESSIONID COOKIE sync across FBML/BROWSER cookies
	}

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

}
