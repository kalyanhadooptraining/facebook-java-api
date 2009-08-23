package com.google.code.facebookapi;

import java.io.InputStream;

import org.junit.Test;
import org.w3c.dom.Document;

public class Issue23MimeEncodingTest {

	@Test
	public void test_mimeEncodingUtf8() throws Exception {
		IFacebookRestClient<Document> client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		String fileName = "FaceBook-128x128.png";
		InputStream fileStream = getClass().getClassLoader().getResourceAsStream( fileName );
		String caption = "test_caption \u0103";
		// String caption = "test_caption";
		client.photos_upload( null, caption, null, fileName, fileStream );
	}

}
