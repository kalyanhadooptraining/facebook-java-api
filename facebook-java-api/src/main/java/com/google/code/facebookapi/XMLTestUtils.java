package com.google.code.facebookapi;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

public class XMLTestUtils {

	protected static Log log = LogFactory.getLog( XMLTestUtils.class );

	public static void print( Node dom ) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer t = factory.newTransformer();
			StringWriter sw = new StringWriter();
			t.transform( new DOMSource( dom ), new StreamResult( sw ) );
			System.out.println( sw.toString() );
		}
		catch ( Exception ex ) {
			System.out.println( "Could not print XML document" );
		}
	}

	public static void debug( String message, Node dom ) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer t = factory.newTransformer();
			StringWriter sw = new StringWriter();
			t.transform( new DOMSource( dom ), new StreamResult( sw ) );
			log.debug( message + sw.toString() );
		}
		catch ( Exception ex ) {
			log.warn( message + ": Coult not print XML document", ex );
		}
	}

	public static void error( String message, Node dom ) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer t = factory.newTransformer();
			StringWriter sw = new StringWriter();
			t.transform( new DOMSource( dom ), new StreamResult( sw ) );
			log.error( message + sw.toString() );
		}
		catch ( Exception ex ) {
			log.error( message + ": Coult not print XML document", ex );
		}
	}

}
