package com.facebook.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

public class FacebookWebRequest<T> {

	protected static Log log = LogFactory.getLog( FacebookWebRequest.class );

	private HttpServletRequest request;
	private String apiKey;
	private String secret;
	private IFacebookRestClient<T> apiClient;
	private boolean valid;
	private Map<String,String> fbParams;

	private String sessionKey;
	private Long userId;
	private Long sessionExpires;

	private boolean appAdded;
	private boolean inCanvas;
	private boolean inIframe;
	private boolean inProfile;


	public static FacebookWebRequest<Document> newInstanceXml( HttpServletRequest request, String apiKey, String secret ) {
		return new FacebookWebRequest<Document>( request, apiKey, secret, new FacebookXmlRestClient( apiKey, secret ) );
	}

	public static FacebookWebRequest<Object> newInstanceJson( HttpServletRequest request, String apiKey, String secret ) {
		return new FacebookWebRequest<Object>( request, apiKey, secret, new FacebookJsonRestClient( apiKey, secret ) );
	}

	public static FacebookWebRequest<Object> newInstanceJaxb( HttpServletRequest request, String apiKey, String secret ) {
		return new FacebookWebRequest<Object>( request, apiKey, secret, new FacebookJaxbRestClient( apiKey, secret ) );
	}

	protected FacebookWebRequest( HttpServletRequest request, String apiKey, String secret, IFacebookRestClient<T> apiClient ) {
		this.request = request;
		this.apiKey = apiKey;
		this.secret = secret;
		this.apiClient = apiClient;
		this.fbParams = FacebookSignatureUtil.pulloutFbSigParams( request.getParameterMap() );
		this.valid = FacebookSignatureUtil.verifySignature( fbParams, secret );
		if ( valid ) {
			{
				// caching of session key / logged in user
				sessionKey = getFbParam( FacebookParam.SESSION_KEY );
				userId = getFbParamLong( FacebookParam.USER );
				sessionExpires = getFbParamLong( FacebookParam.EXPIRES );
				if ( sessionKey != null && userId != null && sessionExpires != null ) {
					apiClient.setCacheSession( sessionKey, userId, sessionExpires );
				}
			}
			{
				// caching of friends
				String friends = getFbParam( FacebookParam.FRIENDS );
				if ( friends != null && !friends.equals( "" ) ) {
					List<Long> friendsList = new ArrayList<Long>();
					for ( String friend : friends.split( "," ) ) {
						friendsList.add( Long.parseLong( friend ) );
					}
					apiClient.setCacheFriendsList( friendsList );
				}
			}
			{
				// caching of the "added" value
				String addedS = getFbParam( FacebookParam.ADDED );
				if ( addedS != null ) {
					this.appAdded = true;
					apiClient.setCacheAppAdded( addedS.equals( "1" ) );
				}
			}
			{
				// other values from the request;
				inCanvas = fbParamEquals( FacebookParam.IN_CANVAS, "1" );
				inIframe = fbParamEquals( FacebookParam.IN_IFRAME, "1" ) || !inCanvas;
				inProfile = fbParamEquals( FacebookParam.IN_PROFILE, "1" );
			}
		}
	}

	// ---- Parameter Helpers

	public String getFbParam( String key ) {
		return fbParams.get( key );
	}

	public Long getFbParamLong( String key ) {
		String t = getFbParam( key );
		if ( t != null ) {
			return Long.parseLong( t );
		}
		return null;
	}

	public boolean fbParamEquals( String key, String val ) {
		String param = getFbParam( key );
		return key.equals( param );
	}

	public String getFbParam( FacebookParam key ) {
		return fbParams.get( key.toString() );
	}

	public Long getFbParamLong( FacebookParam key ) {
		String t = getFbParam( key );
		if ( t != null ) {
			return Long.parseLong( t );
		}
		return null;
	}

	public boolean fbParamEquals( FacebookParam key, String val ) {
		String param = getFbParam( key );
		return key.equals( param );
	}

	// ---- Getters

	public boolean isLoggedIn() {
		return sessionKey != null && userId != null;
	}

	public IFacebookRestClient<T> getApiClient() {
		return apiClient;
	}

	public String getApiKey() {
		return apiKey;
	}

	public boolean isAppAdded() {
		return appAdded;
	}

	public Map<String,String> getFbParams() {
		return fbParams;
	}

	public boolean isInCanvas() {
		return inCanvas;
	}

	public boolean isInIframe() {
		return inIframe;
	}

	public boolean isInProfile() {
		return inProfile;
	}

	public Long getSessionExpires() {
		return sessionExpires;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public Long getUserId() {
		return userId;
	}

	public boolean isValid() {
		return valid;
	}

}
