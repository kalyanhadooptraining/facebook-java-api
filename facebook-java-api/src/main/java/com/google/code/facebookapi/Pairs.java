package com.google.code.facebookapi;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

/**
 * Basic client taking care of rest call mechanics (signing, etc) to facebook. No api knowledge, nor response interpretation is planned.
 */
public class Pairs {

	protected static Log log = LogFactory.getLog( Pairs.class );

	protected static Pair<String,CharSequence> newPair( String name, Object value ) {
		return new Pair<String,CharSequence>( name, String.valueOf( value ) );
	}

	@Deprecated
	protected static Pair<String,CharSequence> newPair( String name, boolean value ) {
		return newPair10( name, value );
	}

	protected static Pair<String,CharSequence> newPair10( String name, boolean value ) {
		return newPair( name, value ? "1" : "0" );
	}

	protected static Pair<String,CharSequence> newPairTF( String name, boolean value ) {
		return newPair( name, value ? "true" : "false" );
	}

	protected static Pair<String,CharSequence> newPair( String name, CharSequence value ) {
		return new Pair<String,CharSequence>( name, value );
	}

	protected static Pair<String,CharSequence> newPair( String name, Long value ) {
		return new Pair<String,CharSequence>( name, Long.toString( value ) );
	}

	protected static Pair<String,CharSequence> newPair( String name, Integer value ) {
		return new Pair<String,CharSequence>( name, Integer.toString( value ) );
	}

	protected static boolean addParam( String name, Long value, Collection<Pair<String,CharSequence>> params ) {
		params.add( newPair( name, value ) );
		return true;
	}

	protected static boolean addParamIfNotBlank( String name, Long value, Collection<Pair<String,CharSequence>> params ) {
		if ( value != null ) {
			return addParam( name, value, params );
		}
		return false;
	}

	protected static boolean addParamSecondsIfNotBlank( String name, Date value, Collection<Pair<String,CharSequence>> params ) {
		if ( value != null ) {
			return addParam( name, value.getTime() / 1000, params );
		}
		return false;
	}

	protected static boolean addParamIfNotBlankZero( String name, Long value, Collection<Pair<String,CharSequence>> params ) {
		if ( value != null && value.longValue() != 0 ) {
			return addParam( name, value, params );
		}
		return false;
	}

	protected static boolean addParamDelimitIfNotBlankEmpty( String name, Iterable<?> value, Collection<Pair<String,CharSequence>> params ) {
		return addParamIfNotBlank( name, BasicClientHelper.delimit( value ), params );
	}

	protected static boolean addParam( String name, Object value, Collection<Pair<String,CharSequence>> params ) {
		params.add( newPair( name, value ) );
		return true;
	}

	protected static boolean addParam( String name, CharSequence value, Collection<Pair<String,CharSequence>> params ) {
		params.add( newPair( name, value ) );
		return true;
	}

	protected static boolean addParamIfNotBlank( String name, CharSequence value, Collection<Pair<String,CharSequence>> params ) {
		if ( ( value != null ) && ( !"".equals( value ) ) ) {
			params.add( newPair( name, value ) );
			return true;
		}
		return false;
	}

	protected static boolean addParamIfNotBlank( String name, JSONArray value, Collection<Pair<String,CharSequence>> params ) {
		if ( value != null ) {
			params.add( newPair( name, value ) );
			return true;
		}
		return false;
	}

	protected static boolean addParamJsonIfNotBlank( String name, ToJsonObject value, Collection<Pair<String,CharSequence>> params ) {
		if ( value != null ) {
			return addParam( name, value.toJson(), params );
		}
		return false;
	}

}
