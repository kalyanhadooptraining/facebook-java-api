package com.google.code.facebookapi;

import static com.google.code.facebookapi.FacebookJsonRestClientBase.jsonToJavaValue;
import static junit.framework.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class TestJSON {
	
	@SuppressWarnings("unused")
	@Test
	public void testJSON() throws JSONException {
		boolean bool = (Boolean)jsonToJavaValue( "true" );
		int myint = (Integer)jsonToJavaValue( "5" );
		double mydouble = (Double)jsonToJavaValue( "5.555" );
		String mystring = (String)jsonToJavaValue( "\"mystring\"" );
		long mylong = (Long)jsonToJavaValue( Long.toString( Long.MAX_VALUE ) );
		JSONArray myArray = (JSONArray)jsonToJavaValue( "   [3, 5, 6]  " );
		assertEquals( 3, myArray.get( 0 ) );
		JSONObject myObject = (JSONObject)jsonToJavaValue( " {abc:123}" );
		assertEquals( 123, myObject.get( "abc" ) );
	}
	
}