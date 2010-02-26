package com.google.code.facebookapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @see http://wiki.developers.facebook.com/index.php/Authorizing_Applications
 */
public class FbWebHelper {

	public static FBWebRequest attainFBWebRequest( FBAppConf appConf, String skey, HttpServletRequest httpRequest ) throws IOException, ServletException {
		boolean noCookies = false;
		return attainFBWebRequest( appConf, noCookies, skey, httpRequest );
	}

	public static FBWebRequest attainFBWebRequest( FBAppConf appConf, boolean noCookies, String skey, HttpServletRequest httpRequest ) throws IOException,
			ServletException {
		HttpSession httpSession = httpRequest.getSession();
		String apiKey = appConf.getApiKey();
		String secret = appConf.getSecret();

		// MAINTAINING FBSESSION INFORMATION:
		// 3 sources: FBRequestParams, FBConnectCookies, sessionObj
		// Values can be in requestScope or sessionScope

		// FB REQUEST PARAMS (canvas/fbml/iframe)
		SortedMap<String,String> params = null;
		params = FacebookSignatureUtil.pulloutFbSigParams( getRequestParameterMap( httpRequest ) );
		params = FacebookSignatureUtil.getVerifiedParams( "fb_sig", params, secret );
		boolean validParams = ( params != null );

		// FB CONNECT COOKIES
		SortedMap<String,String> cookies = null;
		if ( !noCookies ) {
			cookies = pulloutFbConnectCookies( httpRequest.getCookies(), apiKey );
			cookies = FacebookSignatureUtil.getVerifiedParams( apiKey, cookies, secret );
		}
		boolean validCookies = ( cookies != null );

		// PREVIOUSLY STORED SESSION
		FBWebSession session = (FBWebSession) httpSession.getAttribute( skey );
		if ( session == null ) {
			session = new FBWebSession( appConf );
			httpSession.setAttribute( skey, session );
		}

		// if validParams, validCookies, validSession:: make sure apiKey matches all around

		FBWebRequest request = new FBWebRequest( appConf, session, params, cookies, validParams || validCookies );

		boolean updateSession = false;
		if ( validParams ) {
			updateSession = updateSession || updateRequestSessionFromParams( params, request, session );
		}
		if ( validCookies ) {
			updateSession = updateSession || updateSessionFromCookies( cookies, session );
		}
		if ( updateSession ) {
			httpSession.setAttribute( skey, session );
		}

		boolean updateCookies = !noCookies && validParams && !validCookies;
		if ( updateCookies ) {
			// TODO: update cookies in http response
		}

		return request;
	}

	private static final String SUFF_SESSION_KEY = "_session_key";
	private static final int SUFF_SESSION_KEY_LENGTH = SUFF_SESSION_KEY.length();

	public static List<FBWebSession> attainFBWebSessions( FBAppConfs appConfs, HttpServletRequest httpRequest ) throws IOException, ServletException {
		List<FBWebSession> out = new ArrayList<FBWebSession>();
		Cookie[] hcookies = httpRequest.getCookies();
		for ( Cookie cookie : hcookies ) {
			final String name = cookie.getName();
			if ( name.endsWith( SUFF_SESSION_KEY ) ) {
				// looks to be possible fb connect cookie
				String apiKey = name.substring( 0, name.length() - SUFF_SESSION_KEY_LENGTH );
				FBAppConf appConf = appConfs.getConfByApiKey( apiKey );
				if ( appConf != null ) {
					SortedMap<String,String> cookies = null;
					cookies = pulloutFbConnectCookies( hcookies, apiKey );
					cookies = FacebookSignatureUtil.getVerifiedParams( apiKey, cookies, appConf.getSecret() );
					if ( cookies != null ) {
						FBWebSession session = new FBWebSession( appConf );
						updateSessionFromCookies( cookies, session );
						out.add( session );
					}
				}
			}
		}
		return null;
	}

	// ---- Helpers

	public static boolean updateRequestSessionFromParams( SortedMap<String,String> params, FBWebRequest request, FBWebSession session ) {
		if ( params == null || params.isEmpty() ) {
			return false;
		}
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
		Date sessionExpires = getFbParamExpiresDate( FacebookParam.EXPIRES, params );
		String sessionSecret = getFbParam( FacebookParam.SS, params );
		boolean appUser = getFbParamBooleanN( FacebookParam.ADDED, params );

		return session.update( sessionKey, sessionExpires, userId, sessionSecret, appUser );
	}

	public static boolean updateSessionFromCookies( SortedMap<String,String> cookies, FBWebSession session ) {
		if ( cookies == null || cookies.isEmpty() ) {
			return false;
		}
		String apiKey = session.getAppConf().getApiKey();
		String sessionKey = cookies.get( apiKey + "_session_key" );
		Date sessionExpires = toExpiresDate( cookies.get( apiKey + "_expires" ) );
		Long userId = toLong( cookies.get( apiKey + "_user" ) );
		String sessionSecret = cookies.get( apiKey + "_ss" );

		return session.update( sessionKey, sessionExpires, userId, sessionSecret, true );
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
		if ( params != null ) {
			return params.get( key.toString() );
		}
		return null;
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

	public static Date getFbParamExpiresDate( FacebookParam key, Map<String,String> params ) {
		return toExpiresDate( getFbParam( key, params ) );
	}

	public static Date toExpiresDate( String t ) {
		return toExpiresDate( toLong( t ) );
	}

	public static Date toExpiresDate( Long l ) {
		if ( l != null ) {
			if ( l <= 0 ) {
				return new Date( Long.MAX_VALUE );
			}
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

}
