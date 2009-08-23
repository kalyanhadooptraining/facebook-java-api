package com.google.code.facebookapi;

import static org.junit.Assert.assertTrue;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * For this test to work, the owner of the API KEY in use when running the JUnit
 * test must have photos *of themself*.
 * 
 * If you're running the test as yourself, that's probably the case anyway. If
 * you're running it as a batch test user, ensure that at least one photo is
 * posted for that fake user.
 * 
 * @author david.j.boden
 */
public class Issue41PhotosGetSubject {
	@Test
	public void testGetPhotosBySubject() throws Exception {
		FacebookXmlRestClient client = FacebookSessionTestUtils
				.getValidClient(FacebookXmlRestClient.class);

		long apiUserId = client.users_getLoggedInUser();

		Document result = client.photos_get(apiUserId);

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(new FacebookNamespaceContext());

		String numOfPhotos = xpath.evaluate("count(//fbapi:photo)", result);

		assertTrue(Integer.parseInt(numOfPhotos) > 0);
	}
}
