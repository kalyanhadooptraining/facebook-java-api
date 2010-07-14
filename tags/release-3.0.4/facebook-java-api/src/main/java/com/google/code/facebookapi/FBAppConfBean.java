package com.google.code.facebookapi;

public class FBAppConfBean implements FBAppConf {

	private String appId;
	private String apiKey;
	private String secret;

	public FBAppConfBean( String appId, String apiKey, String secret ) {
		this.appId = appId;
		this.apiKey = apiKey;
		this.secret = secret;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId( String appId ) {
		this.appId = appId;
	}

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

}
