package com.google.code.facebookapi;

import java.util.Map;
import java.util.SortedMap;

/**
 * Represents a bactched Facebook API request.
 */
// package-level access intentional (at least for now)
class BatchQuery {

	private IFacebookMethod method;
	private SortedMap<String,String> params;

	public BatchQuery( IFacebookMethod method, SortedMap<String,String> params ) {
		this.method = method;
		this.params = params;
	}

	public IFacebookMethod getMethod() {
		return method;
	}

	protected void setMethod( FacebookMethod method ) {
		this.method = method;
	}

	public Map<String,String> getParams() {
		return params;
	}

	protected void setParams( SortedMap<String,String> params ) {
		this.params = params;
	}

}
