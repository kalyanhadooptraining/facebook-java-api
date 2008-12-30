package com.google.code.facebookapi.apt;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("com.google.code.facebookapi.FacebookReturnType")
public class FacebookReturnTypeProcessor extends AbstractProcessor {
    
    PrintWriter out;
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        try {
            Elements eltUtils = processingEnv.getElementUtils();
            JavaFileObject jaxbJava = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookJaxbRestClientExtended",
            		                                                           eltUtils.getTypeElement("com.google.code.facebookapi.IFacebookRestClient"),
                                                                               eltUtils.getTypeElement("com.google.code.facebookapi.FacebookJaxbRestClient"));
            Writer jaxbJavaWriter = jaxbJava.openWriter();
            out = new PrintWriter(jaxbJavaWriter);
            
            out.println("package com.google.code.facebookapi;");
            out.println();
            out.println("import javax.annotation.Generated;");
            out.println();
            DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ");
            String now = isoDateFormat.format(new Date());
            out.println("@Generated(value=\"com.google.code.facebookapi.apt.FacebookReturnTypeProcessor\", date=\"" + now + "\")");
            out.println("public class FacebookJaxbRestClientExtended extends FacebookJaxbRestClient {");
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
	
	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		System.out.println("Processed annotation" + annotations);
				
	    AnnotationVisitor visitor = new AnnotationVisitor();
	    
	    for(TypeElement element : annotations) {
	        //We know this is the right type, this processor only supports one annotation type
	        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(element);
	        
            for(Element annotatedElement : elements) {
                visitor.scan(annotatedElement, out);
                //out.println("Element " + annotatedElement.getKind() + " " + annotatedElement.accept(visitor, out));
            }
	        
	    }
		
		if(roundEnv.processingOver()) {
		    out.println("}");
		    out.flush();
		    out.close();
		}
		
		return true;

	}
	
	class AnnotationVisitor extends ElementScanner6<Void, PrintWriter> {
		
		
	    @Override
	    public Void visitExecutable(ExecutableElement e, PrintWriter out) {
	    	
	    	processingEnv.getElementUtils().printElements(out, e);
	    	
	    	//Get JAXB and JSON return types - default to Object
	    	String jaxbReturnType = "Object";
	    	String jsonReturnType = "Object";
	    	
	        List<? extends AnnotationMirror> annotations = e.getAnnotationMirrors();
	        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationParams = annotations.get(0).getElementValues();
	        for(ExecutableElement key : annotationParams.keySet()) {
	        	if(key.getSimpleName().contentEquals("JAXB")) {
	        		if(annotationParams.get(key) != null) {
	        			jaxbReturnType = annotationParams.get(key).toString();
	        		}
	        	} else if(key.getSimpleName().contentEquals("JSON")) {
	        		if(annotationParams.get(key) != null) {
	        		    jsonReturnType = annotationParams.get(key).toString();
	        		}
	        	}
	        }
	    	
	    	StringBuilder methodCode = new StringBuilder();
	    	methodCode.append("    public ");
	    	methodCode.append(jaxbReturnType);
	    	methodCode.append(" ");
	    	methodCode.append(e.getSimpleName()).append("(");
	    	
	    	List<? extends VariableElement> parameters = e.getParameters();
	        for(VariableElement param : parameters) {
	            methodCode.append(param.toString() + ", ");
	        }
	        
	    	List<? extends TypeParameterElement> typeParameters = e.getTypeParameters();
	        for(TypeParameterElement param : typeParameters) {
	            methodCode.append(param.toString() + ", ");
	        }
	    	
	    	methodCode.append(") {");

	        out.println(methodCode);
	        out.println("        client.setResponseFormat(\"xml\");");
	        out.println("        Object rawResponse = client." + e.getSimpleName() + "();");
	        out.println("        return (" + jaxbReturnType + ")parseCallResult( rawResponse );");
	        out.println("    }");
	        return null;
	    }
	}

}
