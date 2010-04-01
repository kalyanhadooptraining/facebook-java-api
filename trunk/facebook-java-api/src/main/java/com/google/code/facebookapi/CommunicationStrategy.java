package com.google.code.facebookapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.SortedMap;

/**
 * Interface for encapsulating network communication logic.
 */
public interface CommunicationStrategy {

	/**
	 * Sends a post request to the specified URL.
	 * 
	 * @param serverUrl
	 *            Target server URL.
	 * @param params
	 *            Parameters to include in POST body.
	 * @return String response.
	 * @throws IOException
	 *             Thrown on any communication-related error.
	 */
	public String postRequest( URL serverUrl, SortedMap<String,String> params ) throws IOException;

	/**
	 * Helper function for posting a request that includes raw file data, such as file upload.
	 * 
	 * @param serverUrl
	 *            Target server URL.
	 * @param params
	 *            request parameters (not including the file)
	 * @param fileName
	 * @param fileStream
	 * @return an InputStream with the request response
	 */
	public String postRequest( URL serverUrl, SortedMap<String,String> params, String fileName, InputStream fileStream ) throws IOException;

	public int getConnectionTimeout();

	public void setConnectionTimeout( int connectTimeout );

	public int getReadTimeout();

	public void setReadTimeout( int readTimeout );

}
