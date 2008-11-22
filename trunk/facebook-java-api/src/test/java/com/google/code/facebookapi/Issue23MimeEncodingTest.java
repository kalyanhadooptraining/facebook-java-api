package com.google.code.facebookapi;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

public class Issue23MimeEncodingTest {

	@Test
	@Ignore
	public void test_mimeEncodingUtf8() throws Exception {
		IFacebookRestClient<Document> client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );

		// not quite ready
		File photo = null;
		String caption = "";
		client.photos_upload( photo, caption );
	}

}
