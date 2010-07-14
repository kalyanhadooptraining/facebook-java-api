package com.google.code.facebookapi;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @see http://wiki.developers.facebook.com/index.php/Authorizing_Applications
 */
public class FbWebHelper {

	public static FBWebRequest attainFBWebRequest( FBAppConf appConf, HttpServletRequest httpRequest ) throws IOException, ServletException {
		return attainFBWebRequest( appConf, false, httpRequest );
	}

	public static FBWebRequest attainFBWebRequest( FBAppConf appConf, boolean ignoreCookies, HttpServletRequest httpRequest ) throws IOException, ServletException {
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
		if ( !ignoreCookies ) {
			cookies = pulloutFbConnectCookies( httpRequest.getCookies(), apiKey );
			cookies = FacebookSignatureUtil.getVerifiedParams( apiKey, cookies, secret );
		}
		boolean validCookies = ( cookies != null );

		// PREVIOUSLY STORED SESSION
		String skey = "fbsess_" + apiKey;
		FBWebSession session = (FBWebSession) httpSession.getAttribute( skey );
		if ( session == null ) {
			session = new FBWebSession( appConf );
			httpSession.setAttribute( skey, session );
		} else {
			// FIXME: do we have to fix/set up the deserialized session.appConf object
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

		// TODO: update cookies in http response
		// boolean updateCookies = !ignoreCookies && validParams && session.getSessionSecret() != null;
		// if ( updateCookies ) {
		// }

		return request;
	}

	public static FBWebSession attainFBWebSession( FBAppConf appConf, HttpServletRequest httpRequest ) throws IOException, ServletException {
		final String apiKey = appConf.getApiKey();
		final String secret = appConf.getSecret();
		Cookie[] hcookies = httpRequest.getCookies();
		SortedMap<String,String> cookies = null;
		cookies = pulloutFbConnectCookies( hcookies, apiKey );
		cookies = FacebookSignatureUtil.getVerifiedParams( apiKey, cookies, secret );
		if ( cookies != null ) {
			FBWebSession session = new FBWebSession( appConf );
			updateSessionFromCookies( cookies, session );
			return session;
		}
		return new FBWebSession( appConf );
	}

	public static Map<String,FBWebSession> attainFBWebSessions( FBAppConfs appConfs, HttpServletRequest httpRequest ) throws IOException, ServletException {
		Map<String,FBWebSession> out = new HashMap<String,FBWebSession>();
		{
			Cookie[] hcookies = httpRequest.getCookies();
			Map<String,SortedMap<String,String>> cookiesMap = pulloutFbConnectCookies( hcookies, appConfs );
			for ( Entry<String,SortedMap<String,String>> entry : cookiesMap.entrySet() ) {
				String apiKey = entry.getKey();
				SortedMap<String,String> cookies = entry.getValue();
				FBAppConf appConf = appConfs.getConfByApiKey( apiKey );
				cookies = FacebookSignatureUtil.getVerifiedParams( apiKey, cookies, appConf.getSecret() );
				if ( cookies != null ) {
					FBWebSession session = new FBWebSession( appConf );
					updateSessionFromCookies( cookies, session );
					out.put( apiKey, session );
				}
			}
		}
		{
			SortedMap<String,String> params = null;
			params = FacebookSignatureUtil.pulloutFbSigParams( getRequestParameterMap( httpRequest ) );
			String apiKey = params.get( "fb_sig_api_key" );
			FBAppConf appConf = appConfs.getConfByApiKey( apiKey );
			if ( appConf != null ) {
				params = FacebookSignatureUtil.getVerifiedParams( "fb_sig", params, appConf.getSecret() );
				if ( params != null ) {
					FBWebSession session = out.get( apiKey );
					if ( session == null ) {
						session = new FBWebSession( appConf );
					}
					updateSessionFromParams( params, session );
					out.put( apiKey, session );
				}
			}
		}
		return out;
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

	public static boolean updateSessionFromParams( SortedMap<String,String> params, FBWebSession session ) {
		if ( params == null || params.isEmpty() ) {
			return false;
		}

		String sessionKey = session.getSessionKey();
		Long userId = session.getUserId();

		boolean inProfileTab = getFbParamBoolean( FacebookParam.IN_PROFILE_TAB, params );
		if ( !inProfileTab ) {
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

	public static Map<String,SortedMap<String,String>> pulloutFbConnectCookies( Cookie[] cookies, FBAppConfs appConfs ) {
		Map<String,SortedMap<String,String>> out = new HashMap<String,SortedMap<String,String>>();
		Set<String> existsSet = new HashSet<String>();
		for ( Cookie cookie : cookies ) {
			String key = cookie.getName();
			String[] split = key.split( "_" );
			if ( split.length == 1 || split.length == 2 ) {
				String apiKey = split[0];
				if ( existsSet.contains( apiKey ) || appConfs.hasConfByApiKey( apiKey ) ) {
					existsSet.add( apiKey );
					SortedMap<String,String> vals = out.get( apiKey );
					if ( vals == null ) {
						vals = new TreeMap<String,String>();
						out.put( apiKey, vals );
					}
					vals.put( key, cookie.getValue() );
				}
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
