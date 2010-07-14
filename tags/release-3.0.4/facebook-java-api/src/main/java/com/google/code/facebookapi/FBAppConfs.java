package com.google.code.facebookapi;

public interface FBAppConfs {

	public boolean hasConfByApiKey( String apiKey );

	public FBAppConf getConfByApiKey( String apiKey );

	public FBAppConf getConfByAppId( String appId );

}
