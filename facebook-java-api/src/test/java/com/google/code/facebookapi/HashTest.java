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
	public void test_Sig() {
		SortedMap<String,String> paramsA = new TreeMap<String,String>();
		paramsA.put( "aA", "aA" );
		paramsA.put( "bB", "bB" );
		paramsA.put( "cC", "cC" );

		String expectedBaseString = "aA=aAbB=bBcC=cC";
		StringBuilder baseString = FacebookSignatureUtil.generateBaseString( paramsA );
		assertEquals( expectedBaseString, baseString.toString() );

		String secret = "secret";
		String expectedSignature = "9376fd922ce506221cc1b3892ddca0b9";
		String sig = FacebookSignatureUtil.generateSignature( paramsA, secret );
		assertEquals( expectedSignature, sig );
	}

	@Test
	public void test_FbSig() {
		SortedMap<String,String> paramsA = new TreeMap<String,String>();
		paramsA.put( "aA", "aA" );
		paramsA.put( "bB", "bB" );
		paramsA.put( "cC", "cC" );

		String secret = "secret";
		SortedMap<String,String> paramsB = new TreeMap<String,String>();
		paramsB.put( "fb_sig_aA", "aA" );
		paramsB.put( "fb_sig_bB", "bB" );
		paramsB.put( "fb_sig_cC", "cC" );
		paramsB.put( "fb_sig", "9376fd922ce506221cc1b3892ddca0b9" );

		SortedMap<String,String> paramsC = FacebookSignatureUtil.getVerifiedParams( "fb_sig", paramsB, secret );

		assertEquals( paramsA, paramsC );
	}

}
