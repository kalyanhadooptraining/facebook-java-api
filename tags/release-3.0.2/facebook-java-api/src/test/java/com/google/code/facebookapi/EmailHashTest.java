package com.google.code.facebookapi;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class EmailHashTest {

	@Test
	public void test_emailHash() {
		String email = "mary@example.com";
		String expectedHash = "4228600737_c96da02bba97aedfd26136e980ae3761";
		String hash = FacebookSignatureUtil.generateEmailHash( email );
		assertEquals( expectedHash, hash );
	}

}
