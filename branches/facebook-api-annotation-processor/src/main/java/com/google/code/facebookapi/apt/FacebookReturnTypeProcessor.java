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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("com.google.code.facebookapi.FacebookReturnType")
public class FacebookReturnTypeProcessor extends AbstractProcessor {
    
    PrintWriter outJAXB;
    PrintWriter outJSON;
    PrintWriter outXML;
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        
        DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ");
        String now = isoDateFormat.format(new Date());
        
        try {
            Elements eltUtils = processingEnv.getElementUtils();
            JavaFileObject jaxbJava = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookJaxbRestClientExtended",
            		                                                            eltUtils.getTypeElement("com.google.code.facebookapi.IFacebookRestClient"),
                                                                                eltUtils.getTypeElement("com.google.code.facebookapi.FacebookJaxbRestClient"));
            Writer jaxbJavaWriter = jaxbJava.openWriter();
            outJAXB = new PrintWriter(jaxbJavaWriter);
            
            JavaFileObject jsonJava = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookJsonRestClientExtended",
                    															eltUtils.getTypeElement("com.google.code.facebookapi.IFacebookRestClient"),
                    															eltUtils.getTypeElement("com.google.code.facebookapi.FacebookJsonRestClient"));
            Writer jsonJavaWriter = jsonJava.openWriter();
            outJSON = new PrintWriter(jsonJavaWriter);
            
            JavaFileObject xmlJava = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookXmlRestClientExtended",
																			   eltUtils.getTypeElement("com.google.code.facebookapi.IFacebookRestClient"),
																			   eltUtils.getTypeElement("com.google.code.facebookapi.FacebookXmlRestClient"));
            Writer xmlJavaWriter = xmlJava.openWriter();
            outXML = new PrintWriter(xmlJavaWriter);
            
            writeHeader(outJAXB, "Jaxb", now);
            writeHeader(outJSON, "Json", now);
            writeHeader(outXML, "Xml", now);
            
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void writeHeader(PrintWriter out, String classNamePart, String now) {
        out.println("package com.google.code.facebookapi;");
        out.println();
        out.println("import javax.annotation.Generated;");
        out.println();

        out.println("@Generated(value=\"com.google.code.facebookapi.apt.FacebookReturnTypeProcessor\", date=\"" + now + "\")");
        out.println("public class Facebook" + classNamePart + "RestClientExtended extends Facebook" + classNamePart + "RestClient {");
        out.println();
    }
	
	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {		
	    AnnotationVisitor visitor = new AnnotationVisitor();
	    
	    for(TypeElement element : annotations) {
	        //We know this is the right type, this processor only supports one annotation type
	        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(element);
	        
            for(Element annotatedElement : elements) {
                visitor.scan(annotatedElement, outJAXB);
                //out.println("Element " + annotatedElement.getKind() + " " + annotatedElement.accept(visitor, out));
            }
	        
	    }
		
		if(roundEnv.processingOver()) {
		    outJAXB.println("}");
		    outJAXB.flush();
		    outJAXB.close();
		    
		    outJSON.println("}");
		    outJSON.flush();
		    outJSON.close();
		    
		    outXML.println("}");
		    outXML.flush();
		    outXML.close();
		}
		
		return true;

	}
	
	class AnnotationVisitor extends ElementScanner6<Void, PrintWriter> {
		
		
	    @SuppressWarnings("unchecked")
        @Override
	    public Void visitExecutable(ExecutableElement e, PrintWriter outJAXB) {
	        
	    	//These two lines are required to ensure that the
	    	//object tree actually bothers to parse the method
	    	//parameters. Otherwise they're empty for every method!
	        TypeElement el = (TypeElement)e.getEnclosingElement();
	        List<? extends Element> methods = el.getEnclosedElements();
	    	
	    	//Get JAXB and JSON return types - default to Object
	    	String jaxbReturnType = "Object";
	    	String jsonReturnType = "Object";
	    	String xmlReturnType = "org.w3c.dom.Document";
	    	
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
	    	methodCode.append("%RETURNTYPE%");
	    	methodCode.append(" ");
	    	methodCode.append(e.getSimpleName()).append("( ");
	    	
	    	StringBuilder paramListCode = new StringBuilder();
	    	
	    	boolean isFirstParam = true;
	    	List<? extends VariableElement> parameters = e.getParameters();
	        for(VariableElement param : parameters) {
	        	if(!isFirstParam) {
	        		methodCode.append(", ");
	        		paramListCode.append(", ");
	        	}
	        	TypeMirror paramType = param.asType();
	        	
	        	methodCode.append(paramType.toString());
	        	methodCode.append(" ");
	            methodCode.append(param.toString());
	            
	            paramListCode.append(param.toString());
	            
	            isFirstParam = false;
	        }
	    	
	    	methodCode.append(" ) ");
	    	
	    	List<? extends TypeMirror> thrownTypes = e.getThrownTypes();
	    	boolean isFirstType = true;
	    	for(TypeMirror type : thrownTypes) {
	    		if(isFirstType) {
	    			methodCode.append("throws ");
	    		} else {
	    			methodCode.append(", ");
	    		}
	    		methodCode.append(type.toString());
	    		isFirstType = false;
	    	}
	    	
	    	methodCode.append(" {");

	        outJAXB.println(methodCode.toString().replace("%RETURNTYPE%", jaxbReturnType));
	        outJAXB.println("        client.setResponseFormat(\"xml\");");
	        outJAXB.println("        Object rawResponse = client." + e.getSimpleName() + "( " + paramListCode + " );");
	        outJAXB.println("        return (" + jaxbReturnType + ")parseCallResult( rawResponse );");
	        outJAXB.println("    }");
	        outJAXB.println();
	        
	        outJSON.println(methodCode.toString().replace("%RETURNTYPE%", jsonReturnType));
	        outJSON.println("        client.setResponseFormat(\"json\");");
	        outJSON.println("        Object rawResponse = client." + e.getSimpleName() + "( " + paramListCode + " );");
	        outJSON.println("        return (" + jsonReturnType + ")parseCallResult( rawResponse );");
	        outJSON.println("    }");
	        outJSON.println();
	        
	        outXML.println(methodCode.toString().replace("%RETURNTYPE%", xmlReturnType));
	        outXML.println("        client.setResponseFormat(\"xml\");");
	        outXML.println("        Object rawResponse = client." + e.getSimpleName() + "( " + paramListCode + " );");
	        outXML.println("        return (" + xmlReturnType + ")parseCallResult( rawResponse );");
	        outXML.println("    }");
	        outXML.println();
	        
	        return null;
	    }
	}

}
