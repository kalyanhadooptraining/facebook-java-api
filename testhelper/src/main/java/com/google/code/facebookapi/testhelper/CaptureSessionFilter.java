package com.google.code.facebookapi.testhelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class CaptureSessionFilter implements Filter {

	protected static Logger logger = Logger.getLogger( CaptureSessionFilter.class );

	private FilterConfig config;

	public void init( FilterConfig filterConfig ) throws ServletException {
		this.config = filterConfig;
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

	@SuppressWarnings("unchecked")
	public void doFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain ) throws IOException, ServletException {
		logger.debug( "doFilter(): " + request.getRequestURL() + " :\n" + printMap( request.getParameterMap() ) );
		chain.doFilter( request, response );
	}

	public static String printMap( Map<String,String[]> map ) {
		StringBuilder sb = new StringBuilder();
		for ( Entry<String,String[]> entry : map.entrySet() ) {
			sb.append( entry.getKey() + " : " + Arrays.asList( entry.getValue() ) );
			sb.append( "\n" );
		}
		return sb.toString();
	}

}
