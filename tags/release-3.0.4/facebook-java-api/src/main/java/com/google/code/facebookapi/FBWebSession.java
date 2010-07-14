package com.google.code.facebookapi;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.ObjectUtils;

public class FBWebSession implements Serializable {

	private FBAppConf appConf;
	private String sessionKey;
	private Date sessionExpires;
	private Long userId;
	private String sessionSecret;
	private SortedMap<String,String> params;

	private boolean appUser;

	public FBWebSession( FBAppConf appConf ) {
		this.appConf = appConf;
		this.params = new TreeMap<String,String>();
	}

	public FBAppConf getAppConf() {
		return appConf;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey( String sessionKey ) {
		this.sessionKey = sessionKey;
	}

	public boolean isExpired() {
		return sessionKey == null || sessionExpires == null || sessionExpires.getTime() <= System.currentTimeMillis();
	}

	public Date getSessionExpires() {
		return sessionExpires;
	}

	public void setSessionExpires( Date sessionExpires ) {
		this.sessionExpires = sessionExpires;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId( Long userId ) {
		this.userId = userId;
	}

	public String getSessionSecret() {
		return sessionSecret;
	}

	public void setSessionSecret( String sessionSecret ) {
		this.sessionSecret = sessionSecret;
	}

	public SortedMap<String,String> getParams() {
		return params;
	}

	public void setParams( SortedMap<String,String> params ) {
		this.params = params;
	}

	public boolean isAppUser() {
		return appUser;
	}

	public void setAppUser( boolean appUser ) {
		this.appUser = appUser;
	}

	public boolean update( String sessionKey, Date sessionExpires, Long userId, String sessionSecret, Boolean appUser ) {
		boolean same = true;
		if ( sessionKey != null && !ObjectUtils.equals( this.sessionKey, sessionKey ) ) {
			this.sessionKey = sessionKey;
			same = false;
		}
		if ( sessionExpires != null && !ObjectUtils.equals( this.sessionExpires, sessionExpires ) ) {
			this.sessionExpires = sessionExpires;
			same = false;
		}
		if ( userId != null && !ObjectUtils.equals( this.userId, userId ) ) {
			this.userId = userId;
			same = false;
		}
		if ( sessionSecret != null && !ObjectUtils.equals( this.sessionSecret, sessionSecret ) ) {
			this.sessionSecret = sessionSecret;
			same = false;
		}
		if ( appUser != null && !ObjectUtils.equals( this.appUser, appUser ) ) {
			this.appUser = appUser;
			same = false;
		}
		return !same;
	}

}
