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
import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.facebookapi.JUnitProperties;

public class CaptureSessionFilter implements Filter {

	protected static Logger logger = Logger.getLogger( CaptureSessionFilter.class );

	@SuppressWarnings("unused")
	private FilterConfig config;

	private JUnitProperties junitProperties;

	public void init( FilterConfig filterConfig ) throws ServletException {
		this.config = filterConfig;
		this.junitProperties = new JUnitProperties();
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
		try {
			JSONObject session = junitProperties.loadSession( false );
			logger.debug( "loadSession: " + session );
		}
		catch ( JSONException ex ) {
			logger.error( "Trouble", ex );
		}
		String uid = request.getParameter( "fb_sig_user" );
		if ( junitProperties.getUID().equals( uid ) ) {
			try {
				JSONObject session = captureSession( request, junitProperties.getSECRET() );
				junitProperties.storeSession( session );
			}
			catch ( Exception ex ) {
				logger.error( "Trouble", ex );
				if ( ex instanceof RuntimeException ) {
					throw (RuntimeException) ex;
				}
				throw new RuntimeException( ex );
			}
		}
		chain.doFilter( request, response );
	}

	public static JSONObject captureSession( HttpServletRequest request, String secret ) throws JSONException {
		JSONObject out = new JSONObject();
		copy( "fb_sig_api_key", request, "api_key", out );
		out.put( "secret", secret );
		copy( "fb_sig_session_key", request, "session_key", out );
		copyLong( "fb_sig_user", request, "uid", out );
		copyLong( "fb_sig_expires", request, "expires", out );
		copy( "fb_sig_ss", request, "ss", out );
		copy( "fb_sig_app_id", request, "app_id", out );
		return out;
	}

	public static void copy( String rname, HttpServletRequest request, String jname, JSONObject json ) throws JSONException {
		String val = request.getParameter( rname );
		if ( val != null ) {
			json.put( jname, val );
		}
	}

	public static void copyLong( String rname, HttpServletRequest request, String jname, JSONObject json ) throws JSONException {
		String val = request.getParameter( rname );
		if ( val != null ) {
			json.put( jname, Long.parseLong( val ) );
		}
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
