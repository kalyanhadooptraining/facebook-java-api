package com.google.code.facebookapi;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.zip.CRC32;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility for managing Facebook-specific parameters, specifically those related to session/login aspects.
 */
public final class FacebookSignatureUtil {

	protected static Log log = LogFactory.getLog( FacebookSignatureUtil.class );

	public static SortedMap<String,String> pulloutFbSigParams( Map<String,String[]> reqParams ) {
		SortedMap<String,String> result = new TreeMap<String,String>();
		for ( Map.Entry<String,String[]> entry : reqParams.entrySet() ) {
			String key = entry.getKey();
			String[] values = entry.getValue();
			if ( values.length > 0 && FacebookParam.isInNamespace( key ) ) {
				result.put( key, values[0] );
			}
		}
		return result;
	}

	/**
	 * Verifies that a signature received matches the expected value. Removes FacebookParam.SIGNATURE from params if present.
	 * 
	 * @param params
	 *            a map of parameters and their values, such as one obtained from extractFacebookNamespaceParams; expected to contain the signature as the
	 *            FacebookParam.SIGNATURE parameter
	 * @param secret
	 *            the developers 'secret' API key
	 * @return a boolean indicating whether the calculated signature matched the expected signature
	 */
	public static boolean verifySignature( SortedMap<String,String> params, String secret ) {
		if ( params == null || params.isEmpty() ) {
			return false;
		}
		String sig = params.remove( FacebookParam.SIGNATURE.toString() );
		if ( sig != null ) {
			return verifySignature( params, secret, sig );
		}
		return false;
	}

	/**
	 * Verifies that a signature received matches the expected value.
	 * 
	 * @param params
	 *            a map of parameters and their values, such as one obtained from extractFacebookNamespaceParams
	 * @param secret
	 *            the developers 'secret' API key
	 * @param expected
	 *            the expected resulting value of computing the MD5 sum of the 'sig' params and the 'secret' key
	 * @return a boolean indicating whether the calculated signature matched the expected signature
	 */
	public static boolean verifySignature( SortedMap<String,String> params, String secret, String expected ) {
		if ( params == null || params.isEmpty() ) {
			return false;
		}
		return StringUtils.equals( expected, generateSignature( params, secret ) );
	}

	/**
	 * Converts a Map of key-value pairs into the form expected by generateSignature
	 * 
	 * @param entries
	 *            a collection of Map.Entry's, such as can be obtained using myMap.entrySet()
	 * @return a List suitable for being passed to generateSignature
	 */
	public static List<String> convert( Collection<Map.Entry<String,String>> entries ) {
		List<String> result = new ArrayList<String>( entries.size() );
		for ( Map.Entry<String,String> entry : entries ) {
			result.add( FacebookParam.stripSignaturePrefix( entry.getKey() ) + "=" + ( ( entry.getValue() == null ) ? "" : entry.getValue() ) );
		}
		return result;
	}

	public static StringBuilder generateBaseString( SortedMap<String,String> params ) {
		StringBuilder sb = new StringBuilder();
		for ( Entry<String,String> entry : params.entrySet() ) {
			String key = entry.getKey();
			if ( FacebookParam.isInNamespace( key ) && !FacebookParam.isSignature( key ) ) {
				sb.append( FacebookParam.stripSignaturePrefix( key ) );
				sb.append( "=" );
				String value = entry.getValue();
				sb.append( ( value == null ) ? "" : value );
			}
		}
		return sb;
	}

	public static String generateSignature( SortedMap<String,String> params, String secret ) {
		StringBuilder sb = generateBaseString( params );
		sb.append( secret );
		return generateMD5( sb.toString() );
	}

	public static String generateMD5( String value ) {
		try {
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			byte[] bytes;
			try {
				bytes = value.getBytes( "UTF-8" );
			}
			catch ( UnsupportedEncodingException e1 ) {
				bytes = value.getBytes();
			}
			StringBuilder result = new StringBuilder();
			for ( byte b : md.digest( bytes ) ) {
				result.append( Integer.toHexString( ( b & 0xf0 ) >>> 4 ) );
				result.append( Integer.toHexString( b & 0x0f ) );
			}
			return result.toString();
		}
		catch ( NoSuchAlgorithmException ex ) {
			throw new RuntimeException( ex );
		}
	}

	/**
	 * <ol>
	 * <li>Normalize the email address. Trim leading and trailing whitespace, and convert all characters to lowercase.</li>
	 * <li>Compute the CRC32 value for the normalized email address and use the unsigned integer representation of this value. (Note that some implementations return
	 * signed integers, in which case you will need to convert that result to an unsigned integer.)</li>
	 * <li>Compute the MD5 value for the normalized email address and use the hex representation of this value (using lowercase for A through F).</li>
	 * <li>Combine these two value with an underscore.</li>
	 * </ol>
	 * For example, the address mary@example.com converts to 4228600737_c96da02bba97aedfd26136e980ae3761.
	 * 
	 * @param email
	 * @return email_hash
	 * @see IFacebookRestClient#connect_registerUsers(Collection)
	 */
	public static String generateEmailHash( String email ) {
		email = email.trim().toLowerCase();
		CRC32 crc = new CRC32();
		crc.update( email.getBytes() );
		String md5 = generateMD5( email );
		return crc.getValue() + "_" + md5;
	}

}
