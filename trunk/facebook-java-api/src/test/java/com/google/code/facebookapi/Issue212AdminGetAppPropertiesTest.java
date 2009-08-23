package com.google.code.facebookapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


public class Issue212AdminGetAppPropertiesTest {
	@Test
	public void testStreamGet() throws IOException, FacebookException {
		IFacebookRestClient<Object> client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		List<ApplicationProperty> properties = new ArrayList<ApplicationProperty>();
		properties.add( ApplicationProperty.APPLICATION_NAME );
		properties.add( ApplicationProperty.CALLBACK_URL );
		properties.add( ApplicationProperty.CANVAS_NAME );

		ApplicationPropertySet result = client.admin_getAppPropertiesAsSet( properties );

		Assert.assertNotNull( result );

		System.out.println( result.toJsonString() );
	}
}
