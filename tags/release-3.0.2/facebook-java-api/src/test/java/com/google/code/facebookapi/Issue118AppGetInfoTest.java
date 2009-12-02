package com.google.code.facebookapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import com.google.code.facebookapi.schema.AppInfo;

public class Issue118AppGetInfoTest {

	private static final String stuffAPIKEY_ExistsInDirectory = "16cce4f13025b2bb4517b3890c4f68b9";
	private static final String stuffDevAPIKEY_NotInDirectory = "985c882028dc9bd8438fc349792fdc6b";

	@Test
	public void testAppInfo() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		// Stuff application
		AppInfo appInfo = client.application_getPublicInfoByApiKey( stuffAPIKEY_ExistsInDirectory );

		assertNotNull( appInfo.getAppId() );
		assertNotNull( appInfo.getApiKey() );
		assertNotNull( appInfo.getCanvasName() );
		assertNotNull( appInfo.getCompanyName() );
		assertNotNull( appInfo.getIconUrl() );
		assertNotNull( appInfo.getLogoUrl() );
		assertNotNull( appInfo.getDescription() );
		assertTrue( appInfo.getDailyActiveUsers() > -1 );
		assertTrue( appInfo.getWeeklyActiveUsers() > -1 );
		assertTrue( appInfo.getMonthlyActiveUsers() > -1 );

		// Now lookup by application id
		appInfo = client.application_getPublicInfoById( appInfo.getAppId() );

		assertEquals( stuffAPIKEY_ExistsInDirectory, appInfo.getApiKey() );
	}

	/**
	 * This test tries to get the public info for an application that exists in facebook but isn't published in the application directory. We expect a failure
	 * (FacebookException).
	 */
	@Test
	@Ignore
	public void testAppNotInDirectoryFails() throws Exception {
		IFacebookRestClient<Object> client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		try {
			client.application_getPublicInfoByApiKey( stuffDevAPIKEY_NotInDirectory );
			fail( "Seaching for an app which isn't in the public directory should have thrown an error 900." );
		}
		catch ( FacebookException ex ) {
			assertEquals( 900, ex.getCode() );
		}

		// Raw response is:
		/*
		 * <?xml version="1.0" encoding="UTF-8"?> <error_response xmlns="http://api.facebook.com/1.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 * xsi:schemaLocation ="http://api.facebook.com/1.0/ http://api.facebook.com/1.0/facebook.xsd" > <error_code>900</error_code> <error_msg>No such application
		 * exists.</error_msg> <request_args list="true"> <arg><key>api_key</key> <value>985c882028dc9bd8438fc349792fdc6b</value></arg> <arg><key>application_api_key
		 * </key><value>985c882028dc9bd8438fc349792fdc6b</value></arg> <arg><key>call_id</key> <value>1227046450038</value> </arg> <arg><key>format</key>
		 * <value>xml</value> </arg> <arg> <key>method</key><value>facebook.application.getPublicInfo</value> </arg>
		 * <arg><key>session_key</key><value>acf0ea397c61da3eef0f6d02-536286910 </value> </arg> <arg><key>sig</key> <value>13a49ac16477fb59a40183831c44cbfd</value> </arg>
		 * <arg> <key>v</key> <value>1.0</value> </arg> </request_args> </error_response>
		 */
	}

	@Test
	@Ignore
	public void testRubbishAPIKeyFails() throws Exception {
		IFacebookRestClient<Object> client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		try {
			client.application_getPublicInfoByApiKey( "123456789" );
			fail( "Seaching for a junk API KEY should have thrown an error 101." );
		}
		catch ( FacebookException ex ) {
			// assertEquals(101, ex.getCode());
			// WARNING, THE Facebook Documentation is incorrect.
			// We get an error 900 not a 101.
			// http://wiki.developers.facebook.com/index.php/Application.getPublicInfo
			/*
			 * Supposed to be: 101 The API key submitted is not associated with any known application. 104 Incorrect signature. 900 No such application exists.
			 */
		}
	}

}
