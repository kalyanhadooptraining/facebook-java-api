package com.google.code.facebookapi;

import junit.framework.Assert;

import org.junit.Test;

public class Issue98MultipleUseOfSessionTest {

	@Test
	public void testMultipleUseOfSession() throws Exception {
		// try 1
		FacebookJaxbRestClient clientA = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );
		Long uidA = clientA.users_getLoggedInUser();
		clientA.events_get( uidA, null, null, null );

		// try 2 with new client constructed with a session ID
		FacebookJaxbRestClient clientB = new FacebookJaxbRestClient( clientA.getApiKey(), clientA.getSecret(), clientA.getCacheSessionKey() );
		Long uidB = clientB.users_getLoggedInUser();
		clientB.events_get( uidB, null, null, null );

		Assert.assertEquals( uidA, uidB );
		// Assert.assertEquals( eventsA, eventsB );
	}

}
