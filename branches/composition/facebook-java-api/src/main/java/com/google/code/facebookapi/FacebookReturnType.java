package com.google.code.facebookapi;

/**
 * Used in the IFacebookRestClient interface to tie together
 * the method call (e.g. friends_get) and its return type.
 * 
 * Facebook provides a human readable description of the API
 * and an XSD schema of the response formats. Without a WADL
 * descriptor for the service, these two concepts aren't tied
 * together. So, the Java API needs to tie the requests and
 * expected response types together using this annotation.
 * 
 * The information specified helps to produce annotation-generated
 * files FacebookXXXRestClient.java for Xml, Json and Jaxb. This
 * annotation allows all the return types to be specified in
 * IFacebookRestClient rather than scattered across the concrete
 * adapter .java files.
 * 
 * @author david.j.boden
 */
public @interface FacebookReturnType {
	Class<?> JSON();
	Class<?> JAXB();
}
