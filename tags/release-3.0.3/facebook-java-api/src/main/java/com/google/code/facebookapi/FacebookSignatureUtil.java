package com.google.code.facebookapi;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
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
		SortedMap<String,String> out = new TreeMap<String,String>();
		for ( Entry<String,String[]> entry : reqParams.entrySet() ) {
			String key = entry.getKey();
			String[] values = entry.getValue();
			if ( values.length > 0 && key.startsWith( "fb_sig" ) ) {
				out.put( key, values[0] );
			}
		}
		return out;
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
		return ( getVerifiedParams( "fb_sig", params, secret ) != null );
	}

	public static SortedMap<String,String> getVerifiedParams( String prefix, SortedMap<String,String> map, String secret ) {
		SortedMap<String,String> out = new TreeMap<String,String>();
		String prefix2 = prefix + "_";
		String sig = null;
		for ( Entry<String,String> entry : map.entrySet() ) {
			String key = entry.getKey();
			if ( key.equals( prefix ) ) {
				sig = entry.getValue();
			} else if ( key.startsWith( prefix2 ) ) {
				key = key.substring( prefix2.length() );
				out.put( key, entry.getValue() );
			}
		}
		String sigGen = generateSignature( out, secret );
		if ( StringUtils.equals( sig, sigGen ) ) {
			return out;
		}
		return null;
	}

	public static String generateSignature( SortedMap<String,String> params, String secret ) {
		StringBuilder sb = generateBaseString( params );
		sb.append( secret );
		return generateMD5( sb.toString() );
	}

	public static StringBuilder generateBaseString( SortedMap<String,String> params ) {
		StringBuilder sb = new StringBuilder();
		for ( Entry<String,String> entry : params.entrySet() ) {
			String key = entry.getKey();
			sb.append( key );
			sb.append( "=" );
			String value = entry.getValue();
			sb.append( ( value == null ) ? "" : value );
		}
		return sb;
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
