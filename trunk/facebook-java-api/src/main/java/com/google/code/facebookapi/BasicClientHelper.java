package com.google.code.facebookapi;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basic client taking care of rest call mechanics (signing, etc) to facebook. No api knowledge, nor response interpretation is planned.
 */
public class BasicClientHelper {

	protected static Log log = LogFactory.getLog( BasicClientHelper.class );

	public static String toString( InputStream data ) throws IOException {
		Reader in = new BufferedReader( new InputStreamReader( data, "UTF-8" ) );
		StringBuilder buffer = new StringBuilder();
		char[] buf = new char[1000];
		int l = 0;
		while ( l >= 0 ) {
			buffer.append( buf, 0, l );
			l = in.read( buf );
		}
		return buffer.toString();
	}


	public static CharSequence delimit( Iterable<?> iterable ) {
		if ( iterable == null ) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		boolean empty = true;
		boolean notFirst = false;
		for ( Object item : iterable ) {
			if ( notFirst ) {
				buffer.append( "," );
			} else {
				empty = false;
				notFirst = true;
			}
			buffer.append( item.toString() );
		}
		if ( empty ) {
			return null;
		}
		return buffer;
	}

	public static String encode( CharSequence target ) {
		if ( target == null ) {
			return "";
		}
		String result = target.toString();
		try {
			result = URLEncoder.encode( result, "UTF8" );
		}
		catch ( UnsupportedEncodingException ex ) {
			throw runtimeException( ex );
		}
		return result;
	}

	public static String getResponse( InputStream data ) throws IOException {
		Reader in = new BufferedReader( new InputStreamReader( data, "UTF-8" ) );
		StringBuilder buffer = new StringBuilder();
		char[] buf = new char[1000];
		int l = 0;
		while ( l >= 0 ) {
			buffer.append( buf, 0, l );
			l = in.read( buf );
		}
		return buffer.toString();
	}

	public static CharSequence delimit( Collection<Map.Entry<String,String>> entries, String delimiter, String equals, boolean doEncode ) {
		if ( entries == null || entries.isEmpty() ) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		boolean notFirst = false;
		for ( Map.Entry<String,String> entry : entries ) {
			if ( notFirst ) {
				buffer.append( delimiter );
			} else {
				notFirst = true;
			}
			CharSequence value = entry.getValue();
			buffer.append( entry.getKey() ).append( equals ).append( doEncode ? encode( value ) : value );
		}
		return buffer;
	}

	public static void disconnect( HttpURLConnection conn ) {
		if ( conn != null ) {
			conn.disconnect();
		}
	}

	public static void close( Closeable c ) {
		if ( c != null ) {
			try {
				c.close();
			}
			catch ( IOException ex ) {
				log.warn( "Trouble closing connection", ex );
			}
		}
	}

	public static RuntimeException runtimeException( Exception ex ) {
		if ( ! ( ex instanceof RuntimeException ) ) {
			return new RuntimeException( ex );
		}
		return (RuntimeException) ex;
	}

	protected static String toString( CharSequence cs ) {
		return cs == null ? null : cs.toString();
	}

}
