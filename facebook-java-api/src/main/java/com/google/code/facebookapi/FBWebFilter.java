package com.google.code.facebookapi;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.BooleanUtils;

public class FBWebFilter implements Filter {

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
		doFilter( apiKey, secret, httpRequest, httpResponse );
		chain.doFilter( httpRequest, httpResponse );
	}

	public void doFilter( String apiKey, String secret, HttpServletRequest httpRequest, HttpServletResponse httpResponse ) throws IOException, ServletException {
		HttpSession httpSession = httpRequest.getSession();

		// MAINTAINING FBSESSION INFORMATION:
		// 3 sources: FBRequestParams, FBConnectCookies, sessionObj
		// Values can be in requestScope or sessionScope


		SortedMap<String,String> params = null;
		params = FacebookSignatureUtil.pulloutFbSigParams( getRequestParameterMap( httpRequest ) );
		params = FacebookSignatureUtil.getVerifiedParams( "fb_sig", params, secret );
		boolean validParams = ( params != null );
		if ( !validParams ) {
			params = new TreeMap<String,String>();
		}

		SortedMap<String,String> cookies = null;
		if ( !noCookies ) {
			cookies = pulloutFbConnectCookies( httpRequest.getCookies(), apiKey );
			cookies = FacebookSignatureUtil.getVerifiedParams( apiKey, cookies, secret );
		}
		boolean validCookies = ( cookies != null );
		if ( !validCookies ) {
			cookies = new TreeMap<String,String>();
		}

		FBWebSession session = (FBWebSession) httpSession.getAttribute( skey );
		if ( session == null ) {
			session = new FBWebSession( apiKey );
			httpSession.setAttribute( skey, session );
		}

		FBWebRequest request = new FBWebRequest( httpRequest, httpResponse, session, params, cookies, validParams || validCookies );

		boolean updateSession = false;
		if ( validParams ) {
			updateSession = updateSession || updateRequestSessionFromParams( params, request, session );
		}
		if ( validCookies ) {
			updateSession = updateSession || updateSessionFromCookies( apiKey, cookies, session );
		}
		if ( updateSession ) {
			httpSession.setAttribute( skey, session );
		}

		httpRequest.setAttribute( rkey, request );
		httpRequest.setAttribute( skey, session );

		// TODO: update cookies

		// TODO: MAINTAINING JSESSIONID COOKIE sync across FBML/BROWSER cookies
	}

	// ---- Helpers

	public boolean updateRequestSessionFromParams( SortedMap<String,String> params, FBWebRequest request, FBWebSession session ) {
		String sessionKey = session.getSessionKey();
		Long userId = session.getUserId();

		request.setInCanvas( getFbParamBoolean( FacebookParam.IN_CANVAS, params ) );
		request.setInIframe( getFbParamBoolean( FacebookParam.IN_IFRAME, params ) || !request.isInCanvas() );
		request.setInProfileTab( getFbParamBoolean( FacebookParam.IN_PROFILE_TAB, params ) );

		if ( !request.isInProfileTab() ) {
			sessionKey = getFbParam( FacebookParam.SESSION_KEY, params );
			userId = getFbParamLong( FacebookParam.USER, params );
			Long canvas_user = getFbParamLong( FacebookParam.CANVAS_USER, params );
			if ( canvas_user != null ) {
				userId = canvas_user;
			}
		} else {
			sessionKey = getFbParam( FacebookParam.PROFILE_SESSION_KEY, params );
			userId = getFbParamLong( FacebookParam.PROFILE_USER, params );
		}
		Date sessionExpires = getFbParamDate( FacebookParam.EXPIRES, params );
		String sessionSecret = getFbParam( FacebookParam.SS, params );
		boolean appUser = getFbParamBooleanN( FacebookParam.ADDED, params );

		return session.update( sessionKey, sessionExpires, userId, sessionSecret, appUser );
	}

	public boolean updateSessionFromCookies( String apiKey, SortedMap<String,String> cookies, FBWebSession session ) {
		String sessionKey = cookies.get( apiKey + "_session_key" );
		Date sessionExpires = toDate( cookies.get( apiKey + "_expires" ) );
		Long userId = toLong( cookies.get( apiKey + "_user" ) );
		String sessionSecret = cookies.get( apiKey + "_ss" );

		return session.update( sessionKey, sessionExpires, userId, sessionSecret, null );
	}

	@SuppressWarnings("unchecked")
	private static Map<String,String[]> getRequestParameterMap( HttpServletRequest request ) {
		return (Map<String,String[]>) request.getParameterMap();
	}

	public static SortedMap<String,String> pulloutFbConnectCookies( Cookie[] cookies, String apiKey ) {
		SortedMap<String,String> out = new TreeMap<String,String>();
		for ( Cookie cookie : cookies ) {
			String key = cookie.getName();
			if ( key.startsWith( apiKey ) ) {
				out.put( key, cookie.getValue() );
			}
		}
		return out;
	}

	// ---- Parameter Helpers

	public static String getFbParam( FacebookParam key, Map<String,String> params ) {
		return params.get( key.toString() );
	}

	public static Date getFbParamDate( FacebookParam key, Map<String,String> params ) {
		return toDate( getFbParam( key, params ) );
	}

	public static Date toDate( String t ) {
		return toDate( toLong( t ) );
	}

	public static Date toDate( Long l ) {
		if ( l != null ) {
			return new Date( l * 1000 );
		}
		return null;
	}

	public static Long toLong( String t ) {
		if ( t != null ) {
			return Long.parseLong( t );
		}
		return null;
	}

	public static Long getFbParamLong( FacebookParam key, Map<String,String> params ) {
		return toLong( getFbParam( key, params ) );
	}

	public static boolean getFbParamBoolean( FacebookParam key, Map<String,String> params ) {
		Long t = getFbParamLong( key, params );
		return t != null && t > 0;
	}

	public static Boolean getFbParamBooleanN( FacebookParam key, Map<String,String> params ) {
		Long t = getFbParamLong( key, params );
		if ( t != null ) {
			return t > 0;
		}
		return null;
	}

	public static boolean fbParamEquals( FacebookParam key, String val, Map<String,String> params ) {
		String param = getFbParam( key, params );
		return val.equals( param );
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
