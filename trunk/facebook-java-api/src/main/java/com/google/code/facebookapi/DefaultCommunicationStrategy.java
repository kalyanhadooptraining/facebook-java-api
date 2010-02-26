package com.google.code.facebookapi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CommunicationStrategy implementation that uses raw Java-API sockets.
 */
public class DefaultCommunicationStrategy implements CommunicationStrategy {

	protected static Log log = LogFactory.getLog( DefaultCommunicationStrategy.class );

	private static final String ENCODING = "UTF-8";

	protected static final String CRLF = "\r\n";
	protected static final String PREF = "--";
	protected static final int UPLOAD_BUFFER_SIZE = 1024;

	private int connectionTimeout = -1;
	private int readTimeout = -1;

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout( int connectionTimeout ) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout( int readTimeout ) {
		this.readTimeout = readTimeout;
	}

	public DefaultCommunicationStrategy() {
		// empty
	}

	public DefaultCommunicationStrategy( int connectionTimeout, int readTimeout ) {
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
	}

	public String sendPostRequest( URL serverUrl, Map<String,String> params ) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		InputStream in = null;
		try {
			conn = (HttpURLConnection) serverUrl.openConnection();
			if ( connectionTimeout != -1 ) {
				conn.setConnectTimeout( connectionTimeout );
			}
			if ( readTimeout != -1 ) {
				conn.setReadTimeout( readTimeout );
			}
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-type", "application/x-www-form-urlencoded" );
			conn.setDoOutput( true );
			conn.connect();
			out = conn.getOutputStream();
			CharSequence paramString = ( null == params ) ? "" : BasicClientHelper.delimit( params.entrySet(), "&", "=", true );
			out.write( paramString.toString().getBytes( ENCODING ) );
			in = conn.getInputStream();
			return BasicClientHelper.toString( in );
		}
		finally {
			BasicClientHelper.close( in );
			BasicClientHelper.close( out );
			BasicClientHelper.disconnect( conn );
		}
	}

	public String postFileRequest( URL serverUrl, Map<String,String> params, String fileName, InputStream fileStream ) throws IOException {
		HttpURLConnection con = null;
		OutputStream urlOut = null;
		InputStream in = null;
		try {
			String boundary = Long.toString( System.currentTimeMillis(), 16 );
			con = (HttpURLConnection) serverUrl.openConnection();
			if ( connectionTimeout != -1 ) {
				con.setConnectTimeout( connectionTimeout );
			}
			if ( readTimeout != -1 ) {
				con.setReadTimeout( readTimeout );
			}
			con.setDoInput( true );
			con.setDoOutput( true );
			con.setUseCaches( false );
			con.setRequestProperty( "Content-Type", "multipart/form-data; boundary=" + boundary );
			con.setRequestProperty( "MIME-version", "1.0" );

			urlOut = con.getOutputStream();
			DataOutputStream out = new DataOutputStream( urlOut );

			for ( Map.Entry<String,String> entry : params.entrySet() ) {
				out.writeBytes( PREF + boundary + CRLF );

				out.writeBytes( "Content-Type: text/plain;charset=utf-8" + CRLF );
				// out.writeBytes( "Content-Transfer-Encoding: application/x-www-form-urlencoded" + CRLF );

				// out.writeBytes( "Content-Type: text/plain;charset=utf-8" + CRLF );
				// out.writeBytes( "Content-Transfer-Encoding: quoted-printable" + CRLF );

				out.writeBytes( "Content-disposition: form-data; name=\"" + entry.getKey() + "\"" + CRLF );
				out.writeBytes( CRLF );
				byte[] valueBytes = entry.getValue().toString().getBytes( "UTF-8" );
				out.write( valueBytes );
				out.writeBytes( CRLF );
			}

			out.writeBytes( PREF + boundary + CRLF );
			out.writeBytes( "Content-Type: image" + CRLF );
			out.writeBytes( "Content-disposition: form-data; filename=\"" + fileName + "\"" + CRLF );
			// out.writeBytes("Content-Transfer-Encoding: binary" + CRLF); // not necessary

			// Write the file
			out.writeBytes( CRLF );
			byte buf[] = new byte[UPLOAD_BUFFER_SIZE];
			int len = 0;
			while ( len >= 0 ) {
				out.write( buf, 0, len );
				len = fileStream.read( buf );
			}

			out.writeBytes( CRLF + PREF + boundary + PREF + CRLF );
			out.flush();
			in = con.getInputStream();
			return BasicClientHelper.toString( in );
		}
		finally {
			BasicClientHelper.close( urlOut );
			BasicClientHelper.close( in );
			BasicClientHelper.disconnect( con );
		}
	}

}
