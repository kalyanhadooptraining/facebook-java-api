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
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("com.google.code.facebookapi.FacebookReturnType")
public class FacebookReturnTypeProcessor extends AbstractProcessor {
    
    protected PrintWriter outJAXB;
    protected PrintWriter outJSON;
    protected PrintWriter outXML;
    
    private void initWritableFiles() {
        
        DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ");
        String now = isoDateFormat.format(new Date());
        
        try {       	
            Elements eltUtils = processingEnv.getElementUtils();

            CopyConstructorVisitor copyConstructors = new CopyConstructorVisitor();
            
            TypeElement facebookJaxbRestClientBase = eltUtils.getTypeElement("com.google.code.facebookapi.FacebookJaxbRestClientBase");
            if(facebookJaxbRestClientBase != null) {
	            JavaFileObject jaxbJava = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookJaxbRestClient",
	            		                                                            eltUtils.getTypeElement("com.google.code.facebookapi.IFacebookRestClient"),
	                                                                                eltUtils.getTypeElement("com.google.code.facebookapi.FacebookJaxbRestClientBase"));
	            Writer jaxbJavaWriter = jaxbJava.openWriter();
	            outJAXB = new PrintWriter(jaxbJavaWriter);
	            
	            writeHeader(outJAXB, "Jaxb", now);
	            facebookJaxbRestClientBase.accept(copyConstructors, new Tuple<String, PrintWriter>("Jaxb", outJAXB));
            }
            
            TypeElement facebookJsonRestClientBase = eltUtils.getTypeElement("com.google.code.facebookapi.FacebookJsonRestClientBase");
            if(facebookJaxbRestClientBase != null) {
	            JavaFileObject jsonJava = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookJsonRestClient",
	                    															eltUtils.getTypeElement("com.google.code.facebookapi.IFacebookRestClient"),
	                    															eltUtils.getTypeElement("com.google.code.facebookapi.FacebookJsonRestClientBase"));
	            Writer jsonJavaWriter = jsonJava.openWriter();
	            outJSON = new PrintWriter(jsonJavaWriter);
	            
	            writeHeader(outJSON, "Json", now);
	            facebookJsonRestClientBase.accept(copyConstructors, new Tuple<String, PrintWriter>("Json", outJSON));
            }
	            
            TypeElement facebookXmlRestClientBase = eltUtils.getTypeElement("com.google.code.facebookapi.FacebookXmlRestClientBase");
            if(facebookXmlRestClientBase != null) {            
	            JavaFileObject xmlJava = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookXmlRestClient",
																				   eltUtils.getTypeElement("com.google.code.facebookapi.IFacebookRestClient"),
																				   eltUtils.getTypeElement("com.google.code.facebookapi.FacebookXmlRestClientBase"));
	            Writer xmlJavaWriter = xmlJava.openWriter();
	            outXML = new PrintWriter(xmlJavaWriter);
            
	            writeHeader(outXML, "Xml", now);
	            facebookXmlRestClientBase.accept(copyConstructors, new Tuple<String, PrintWriter>("Xml", outXML));
            }
            
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    class CopyConstructorVisitor extends ElementScanner6<Void, Tuple<String, PrintWriter>> {
    	@Override
    	public Void visitExecutable(ExecutableElement e, Tuple<String, PrintWriter> tuple) {
    		String clientType = tuple.a;
    		PrintWriter out = tuple.b;
    		if(e.getKind() == ElementKind.CONSTRUCTOR) {
    			out.print("    ");
    			out.print(modifiers(e));
    			out.print(" ");
    			out.print("Facebook"); out.print(clientType); out.print("RestClient");
    			out.print("( ");
    			out.print(parametersIncludingTypes(e));
    			out.print(" ) ");
    			out.print(throwClause(e));
    			out.println(" {");
    			out.print("        super( ");
    			out.print(parametersExcludingTypes(e));
    			out.println(" );");
    			out.println("    }");
    			out.println();
    		}
    		return null;
    	}
    }
    
    class Tuple<A, B> {
    	public A a;
    	public B b;
    	public Tuple(A a, B b) {
    		this.a = a;
    		this.b = b;
    	}
    }
    
    private CharSequence modifiers(ExecutableElement e) {
    	StringBuilder modifiers = new StringBuilder();
    	Set<Modifier> modifierSet = e.getModifiers();
    	boolean isFirstModifier = true;
    	for(Modifier m : modifierSet) {
        	if(!isFirstModifier) {
        		modifiers.append(" ");
        	}
    		modifiers.append(m.toString());
    	}
    	
    	return modifiers;
    }
    
    private CharSequence throwClause(ExecutableElement e) {
    	StringBuilder throwClause = new StringBuilder();
    	List<? extends TypeMirror> thrownTypes = e.getThrownTypes();
    	boolean isFirstThrows = true;
    	for(TypeMirror t : thrownTypes) {
        	if(isFirstThrows) {
        		throwClause.append("throws ");
        	} else {
        		throwClause.append(", ");
        	}
        	throwClause.append(t.toString());
    	}
    	
    	return throwClause;
    }
    
    private CharSequence parametersIncludingTypes(ExecutableElement e) {
    	StringBuilder methodCode = new StringBuilder();
    	
    	boolean isFirstParam = true;
    	List<? extends VariableElement> parameters = e.getParameters();
        for(VariableElement param : parameters) {
        	if(!isFirstParam) {
        		methodCode.append(", ");
        	}
        	TypeMirror paramType = param.asType();
        	
        	methodCode.append(paramType.toString());
        	methodCode.append(" ");
            methodCode.append(param.toString());
            
            isFirstParam = false;
        }
        
        return methodCode;
    }
    
    private CharSequence parametersExcludingTypes(ExecutableElement e) {	
    	StringBuilder paramListCode = new StringBuilder();
    	
    	boolean isFirstParam = true;
    	List<? extends VariableElement> parameters = e.getParameters();
        for(VariableElement param : parameters) {
        	if(!isFirstParam) {
        		paramListCode.append(", ");
        	}
            
            paramListCode.append(param.toString());
            
            isFirstParam = false;
        }
        
        return paramListCode;
    }
    
    private void writeHeader(PrintWriter out, String classNamePart, String now) {
        out.println("package com.google.code.facebookapi;");
        out.println();
        out.println("import javax.annotation.Generated;");
        out.println();

        out.println("@Generated(value=\"com.google.code.facebookapi.apt.FacebookReturnTypeProcessor\", date=\"" + now + "\")");
        if(classNamePart.equals("Jaxb")) {
        	out.println("@SuppressWarnings(\"unchecked\")");
        }
        out.println("public class Facebook" + classNamePart + "RestClient extends Facebook" + classNamePart + "RestClientBase {");
        out.println();
    }
	
    protected boolean initWritableFilesCompleted = false;
    
	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		if(!initWritableFilesCompleted) {
			//Can't do this by overriding init(). That would result in the
			//files being created during both the source and test compile
			//phases because init() is called regardless of whether there's
			//an annotation match.
			initWritableFiles();
			initWritableFilesCompleted = true;
		}
		
	    AnnotationVisitor visitor = new AnnotationVisitor();
	    
	    for(TypeElement element : annotations) {
	        //We know this is the right type, this processor only supports one annotation type
	        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(element);
	        
	        visitor.scan(elements, outJAXB);
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
	
	/**
	 * These two lines are required to ensure that the
	 * object tree actually bothers to parse the method
	 * parameters. Otherwise they're empty for every method!
	 * @param e
	 */
	private void shakeEnclosingElementMethods(Element e) {
        TypeElement el = (TypeElement)e.getEnclosingElement();
        el.getEnclosedElements();
	}
	
	class AnnotationVisitor extends ElementScanner6<Void, PrintWriter> {
		
        @Override
	    public Void visitExecutable(ExecutableElement e, PrintWriter outJAXB) {
	    	
        	shakeEnclosingElementMethods(e);
        	
	    	//Get JAXB and JSON return types - default to Object
	    	String jaxbReturnType = "Object";
	    	String jsonReturnType = "Object";
	    	String xmlReturnType = "org.w3c.dom.Document";
	    	
	        List<? extends AnnotationMirror> annotations = e.getAnnotationMirrors();
	        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationParams = annotations.get(0).getElementValues();
	        boolean jaxbAlreadySet = false;
	        for(ExecutableElement key : annotationParams.keySet()) {
	        	if(key.getSimpleName().contentEquals("JAXBList")) {
	        		if(annotationParams.get(key) != null) {
	        			jaxbReturnType = "java.util.List<" + removeDotClass(annotationParams.get(key).toString()) + ">";
	        			jaxbAlreadySet = true;
	        		}	        		
	        	}
	            else if(!jaxbAlreadySet && key.getSimpleName().contentEquals("JAXB")) {
	        		if(annotationParams.get(key) != null) {
	        			jaxbReturnType = removeDotClass(annotationParams.get(key).toString());
	        		}
	        	} else if(key.getSimpleName().contentEquals("JSON")) {
	        		if(annotationParams.get(key) != null) {
	        		    jsonReturnType = removeDotClass(annotationParams.get(key).toString());
	        		}
	        	}
	        }
	    	
	    	StringBuilder methodCode = new StringBuilder();
	    	
	    	if(e.getAnnotation(Deprecated.class) != null) {
	    		methodCode.append("    @Deprecated").append(System.getProperty("line.separator"));
	    	}
	    	
	    	methodCode.append("    public ");
	    	methodCode.append("%RETURNTYPE%");
	    	methodCode.append(" ");
	    	methodCode.append(e.getSimpleName()).append("( ");
	    	methodCode.append(parametersIncludingTypes(e));
	    	methodCode.append(" ) ");
	    	methodCode.append(throwClause(e));    	
	    	methodCode.append(" {");
	    	
	    	CharSequence paramListCode = parametersExcludingTypes(e);

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
	
	private String removeDotClass(String input) {
		if(input.endsWith(".class")) {
			return input.substring(0, input.length() - 6);
		} else {
			return input;
		}
	}

}
