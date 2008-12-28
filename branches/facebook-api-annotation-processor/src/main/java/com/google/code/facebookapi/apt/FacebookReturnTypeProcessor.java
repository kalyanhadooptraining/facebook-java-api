package com.google.code.facebookapi.apt;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("com.google.code.facebookapi.FacebookReturnType")
public class FacebookReturnTypeProcessor extends AbstractProcessor {
	
	private static boolean addedAlready = false;
	
	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		System.out.println("Processed annotation" + annotations);
		
		if(!addedAlready) {
			try {
				Elements eltUtils = processingEnv.getElementUtils();
				JavaFileObject xmlJava = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookXmlRestClientExtended",
						                                                           eltUtils.getTypeElement("com.google.code.facebookapi.FacebookXmlRestClient"));
				Writer xmlJavaWriter = xmlJava.openWriter();
				PrintWriter out = new PrintWriter(xmlJavaWriter);
				
				out.println("package com.google.code.facebookapi;");
				out.println();
				out.println("import javax.annotation.Generated;");
				out.println();
				DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.mmm-zzzz");
				String now = isoDateFormat.format(new Date());
				out.println("@Generated(value=\"com.google.code.facebookapi.apt.FacebookReturnTypeProcessor\" date=\"" + now + "\"");
				out.println("public class FacebookXmlRestClientExtended extends FacebookXmlRestClient {");
				out.println("}");
							
				out.flush();
				out.close();
				
				addedAlready = true;
				return true;
				
			} catch(IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		
		return true;
	}

}
