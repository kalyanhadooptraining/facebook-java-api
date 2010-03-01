package com.google.code.facebookapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A FacebookRestClient that uses the JSON result format. This means results from calls to the Facebook API are returned as <a href="http://www.json.org/">JSON</a> and
 * transformed into Java <code>Object</code>'s.
 */
public abstract class FacebookJsonRestClientBase extends SpecificReturnTypeAdapter<Object> {

	protected static Log log = LogFactory.getLog( FacebookJsonRestClientBase.class );

	public FacebookJsonRestClientBase( ExtensibleClient client ) {
		super( "json", client );
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 */
	public FacebookJsonRestClientBase( String apiKey, String secret ) {
		this( new ExtensibleClient( "json", apiKey, secret ) );
	}

	/**
	 * Constructor.
	 * 
	 * @param apiKey
	 *            your Facebook API key
	 * @param secret
	 *            your 'secret' Facebook key
	 * @param sessionKey
	 *            the session-id to use
	 */
	public FacebookJsonRestClientBase( String apiKey, String secret, String sessionKey ) {
		this( new ExtensibleClient( "json", apiKey, secret, sessionKey ) );
	}

	public FacebookJsonRestClientBase( String apiKey, String secret, String sessionKey, boolean sessionSecret ) {
		this( new ExtensibleClient( "json", apiKey, secret, sessionKey, sessionSecret ) );
	}

	@SuppressWarnings("unchecked")
	protected <T> T parseCallResult( Class<T> type, Object rawResponse ) throws FacebookException {
		log.debug( "Facebook response:  " + rawResponse );
		Object out = JsonHelper.parseCallResult( rawResponse );
		if ( type == JSONArray.class && out instanceof JSONObject ) {
			JSONArray arr = new JSONArray();
			JSONObject json = (JSONObject) out;
			if ( json.length() > 0 ) {
				arr.put( json );
			}
			out = arr;
		}
		return (T) out;
	}


	/**
	 * Executes a batch of queries. You define the queries to execute by calling 'beginBatch' and then invoking the desired API methods that you want to execute as part
	 * of your batch as normal. Invoking this method will then execute the API calls you made in the interim as a single batch query.
	 * 
	 * @param serial
	 *            set to true, and your batch queries will always execute serially, in the same order in which your specified them. If set to false, the Facebook API
	 *            server may execute your queries in parallel and/or out of order in order to improve performance.
	 * 
	 * @return a list containing the results of the batch execution. The list will be ordered such that the first element corresponds to the result of the first query in
	 *         the batch, and the second element corresponds to the result of the second query, and so on. The types of the objects in the list will match the type
	 *         normally returned by the API call being invoked (so calling users_getLoggedInUser as part of a batch will place a Long in the list, and calling friends_get
	 *         will place a Document in the list, etc.).
	 * 
	 *         The list may be empty, it will never be null.
	 * 
	 * @throws FacebookException
	 * @throws IOException
	 */
	public List<? extends Object> executeBatch( boolean serial ) throws FacebookException {
		List<String> clientResults = client.executeBatch( serial );

		List<Object> result = new ArrayList<Object>();

		for ( String clientResult : clientResults ) {
			JSONArray doc;
			try {
				// Must ensure that JSONArray(String) constructor
				// is called. JSONArray(Object) behaves differently.
				doc = new JSONArray( clientResult );
			}
			catch ( JSONException ex ) {
				throw new RuntimeException( "Error parsing client result", ex );
			}
			for ( int count = 0; count < doc.length(); count++ ) {
				try {
					String response = (String) doc.get( count );
					Object responseObject = JsonHelper.parseCallResult( response );
					result.add( responseObject );
				}
				catch ( Exception ignored ) {
					result.add( null );
				}
			}
		}
		return result;
	}

}
