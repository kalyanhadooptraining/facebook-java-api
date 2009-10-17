package com.google.code.facebookapi;

import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonHelper {

	public static JSONObject toJson( Map<String,String> map ) {
		return new JSONObject( map );
	}

	public static JSONArray toJsonListOfStrings( Collection<String> list ) {
		return new JSONArray( list );
	}

	public static JSONArray toJsonListOfMaps( Collection<Map<String,String>> listOfMaps ) {
		JSONArray out = new JSONArray();
		for ( Map<String,String> map : listOfMaps ) {
			out.put( toJson( map ) );
		}
		return out;
	}

}
