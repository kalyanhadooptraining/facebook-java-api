package com.google.code.facebookapi;

import java.io.Serializable;

public interface FBAppConf extends Serializable {

	public String getAppId();

	public String getApiKey();

	public String getSecret();

}
