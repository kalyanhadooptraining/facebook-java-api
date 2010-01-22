package com.google.code.facebookapi;

import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FBWebRequest {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private FBWebSession session;

	private boolean valid;
	private SortedMap<String,String> params;
	private SortedMap<String,String> cookies;

	private boolean inCanvas;
	private boolean inIframe;
	private boolean inProfileTab;

	public FBWebRequest( HttpServletRequest request, HttpServletResponse response, FBWebSession session, SortedMap<String,String> params, SortedMap<String,String> cookies ) {
		this.request = request;
		this.response = response;
		this.session = session;
		this.params = params;
		this.cookies = cookies;
		this.valid = params != null && cookies != null && !params.isEmpty() && !cookies.isEmpty();
	}

	public FBWebRequest( HttpServletRequest request, HttpServletResponse response, FBWebSession session, SortedMap<String,String> params,
			SortedMap<String,String> cookies, boolean valid ) {
		this.request = request;
		this.response = response;
		this.session = session;
		this.params = params;
		this.cookies = cookies;
		this.valid = valid;
	}

	public SortedMap<String,String> getParams() {
		return params;
	}

	public void setParams( SortedMap<String,String> params ) {
		this.params = params;
	}

	public SortedMap<String,String> getCookies() {
		return cookies;
	}

	public void setCookies( SortedMap<String,String> cookies ) {
		this.cookies = cookies;
	}

	public boolean isInCanvas() {
		return inCanvas;
	}

	public void setInCanvas( boolean inCanvas ) {
		this.inCanvas = inCanvas;
	}

	public boolean isInIframe() {
		return inIframe;
	}

	public void setInIframe( boolean inIframe ) {
		this.inIframe = inIframe;
	}

	public boolean isInProfileTab() {
		return inProfileTab;
	}

	public void setInProfileTab( boolean inProfileTab ) {
		this.inProfileTab = inProfileTab;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public FBWebSession getSession() {
		return session;
	}

	public boolean isValid() {
		return valid;
	}

}
