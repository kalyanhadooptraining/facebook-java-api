package com.google.code.facebookapi;

import static junit.framework.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class TestJSON {

	@Test
	public void testJSON() throws JSONException {
		boolean bool = (Boolean) JsonHelper.jsonToJavaValue( "true" );
		assertEquals( true, bool );
		int myint = (Integer) JsonHelper.jsonToJavaValue( "5" );
		assertEquals( 5, myint );
		double mydouble = (Double) JsonHelper.jsonToJavaValue( "5.555" );
		assertEquals( 5.555, mydouble );
		String mystring = (String) JsonHelper.jsonToJavaValue( "\"mystring\"" );
		assertEquals( "mystring", mystring );
		long mylong = (Long) JsonHelper.jsonToJavaValue( Long.toString( Long.MAX_VALUE ) );
		assertEquals( mylong, Long.MAX_VALUE );
		JSONArray myArray = (JSONArray) JsonHelper.jsonToJavaValue( "   [3, 5, 6]  " );
		assertEquals( 3, myArray.get( 0 ) );
		assertEquals( 5, myArray.get( 1 ) );
		assertEquals( 6, myArray.get( 2 ) );
		JSONObject myObject = (JSONObject) JsonHelper.jsonToJavaValue( " {abc:123}" );
		assertEquals( 123, myObject.get( "abc" ) );
	}

}
