/*
 +---------------------------------------------------------------------------+
 | Facebook Development Platform Java Client                                 |
 +---------------------------------------------------------------------------+
 | Copyright (c) 2007 Facebook, Inc.                                         |
 | All rights reserved.                                                      |
 |                                                                           |
 | Redistribution and use in source and binary forms, with or without        |
 | modification, are permitted provided that the following conditions        |
 | are met:                                                                  |
 |                                                                           |
 | 1. Redistributions of source code must retain the above copyright         |
 |    notice, this list of conditions and the following disclaimer.          |
 | 2. Redistributions in binary form must reproduce the above copyright      |
 |    notice, this list of conditions and the following disclaimer in the    |
 |    documentation and/or other materials provided with the distribution.   |
 |                                                                           |
 | THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR      |
 | IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES |
 | OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.   |
 | IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,          |
 | INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT  |
 | NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, |
 | DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY     |
 | THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT       |
 | (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF  |
 | THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.         |
 +---------------------------------------------------------------------------+
 | For help with this library, contact developers-help@facebook.com          |
 +---------------------------------------------------------------------------+
 */

package com.facebook.api;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;

/**
 * A FacebookRestClient that uses the XML result format. This means results from calls to the Facebook API are returned as XML and transformed into instances of Document.
 * 
 * Allocate an instance of this class to make Facebook API requests. *
 * 
 * @deprecated this is provided for legacy support only. Please use FacebookXmlRestClient instead if you want to use the Facebook Platform XML API.
 */
@Deprecated
public class FacebookRestClient extends FacebookXmlRestClient implements IFacebookRestClient<Document> {

	public FacebookRestClient( String apiKey, String secret ) {
		super( apiKey, secret );
	}

	public FacebookRestClient( String apiKey, String secret, int connectionTimeout ) {
		super( apiKey, secret, connectionTimeout );
	}

	public FacebookRestClient( String apiKey, String secret, String sessionKey ) {
		super( apiKey, secret, sessionKey );
	}

	public FacebookRestClient( String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		super( apiKey, secret, sessionKey, connectionTimeout );
	}

	public FacebookRestClient( String serverAddr, String apiKey, String secret, String sessionKey ) throws MalformedURLException {
		super( serverAddr, apiKey, secret, sessionKey );
	}

	public FacebookRestClient( String serverAddr, String apiKey, String secret, String sessionKey, int connectionTimeout ) throws MalformedURLException {
		super( serverAddr, apiKey, secret, sessionKey, connectionTimeout );
	}

	public FacebookRestClient( URL serverUrl, String apiKey, String secret, String sessionKey ) {
		super( serverUrl, apiKey, secret, sessionKey );
	}

	public FacebookRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout ) {
		super( serverUrl, apiKey, secret, sessionKey, connectionTimeout, -1 );
	}

	public FacebookRestClient( URL serverUrl, String apiKey, String secret, String sessionKey, int connectionTimeout, int readTimeout ) {
		super( serverUrl, apiKey, secret, sessionKey, connectionTimeout, readTimeout );
	}

}
