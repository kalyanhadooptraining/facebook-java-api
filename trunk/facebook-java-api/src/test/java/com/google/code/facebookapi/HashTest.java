package com.google.code.facebookapi;

import static junit.framework.Assert.assertEquals;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.junit.Test;

public class HashTest {

	protected static Logger logger = Logger.getLogger( HashTest.class );

	@Test
	public void test_emailHash() {
		String email = "mary@example.com";
		String expectedHash = "4228600737_c96da02bba97aedfd26136e980ae3761";
		String hash = FacebookSignatureUtil.generateEmailHash( email );
		assertEquals( expectedHash, hash );
	}

	@Test
	public void test_baseString() {
		SortedMap<String,String> params = new TreeMap<String,String>();
		params.put( "fields", "FIELDS" );
		params.put( "fb_sig_apikey", "APIKEY" );
		params.put( "fb_sig_session_key", "SESSIONKEY" );
		StringBuilder baseString = FacebookSignatureUtil.generateBaseString( params );
		logger.debug( baseString.toString() );
	}

}
