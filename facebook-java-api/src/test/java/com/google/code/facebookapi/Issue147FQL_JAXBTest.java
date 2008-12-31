package com.google.code.facebookapi;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.w3c.dom.Element;

import com.google.code.facebookapi.schema.FqlQueryResponse;

public class Issue147FQL_JAXBTest {

	@Test
	public void testFQLJAXB() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		FqlQueryResponse fqr = (FqlQueryResponse) client.fql_query( "select first_name, last_name from user where uid = " + client.users_getLoggedInUser() );

		List<Object> results = fqr.getResults();

		assertEquals( 1, results.size() );

		Element result0 = (Element) results.get( 0 );
		XMLTestUtils.print( result0 );
	}
}
