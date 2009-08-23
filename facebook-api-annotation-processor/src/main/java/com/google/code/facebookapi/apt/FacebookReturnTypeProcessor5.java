package com.google.code.facebookapi.apt;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.type.ReferenceType;
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.util.SimpleDeclarationVisitor;

public class FacebookReturnTypeProcessor5 implements AnnotationProcessor {
    
    PrintWriter outJAXB;
    PrintWriter outJSON;
    PrintWriter outXML;
    
    AnnotationProcessorEnvironment processingEnv;
    
    public FacebookReturnTypeProcessor5(AnnotationProcessorEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }
    
    class CopyConstructorVisitor extends SimpleDeclarationVisitor {
    	
    	String clientType;
    	PrintWriter out;
    	
    	CopyConstructorVisitor(String clientType, PrintWriter out) {
    		this.clientType = clientType;
    		this.out = out;
    	}
    	
    	@Override
    	public void visitConstructorDeclaration(ConstructorDeclaration e) {
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
    
    }

    private CharSequence modifiers(ConstructorDeclaration e) {
    	StringBuilder modifiers = new StringBuilder();
    	Collection<Modifier> modifierSet = e.getModifiers();
    	boolean isFirstModifier = true;
    	for(Modifier m : modifierSet) {
        	if(!isFirstModifier) {
        		modifiers.append(" ");
        	}
    		modifiers.append(m.toString());
    	}
    	
    	return modifiers;
    }
    
    private CharSequence throwClause(ConstructorDeclaration e) {
    	StringBuilder throwClause = new StringBuilder();
    	Collection<ReferenceType> thrownTypes = e.getThrownTypes();
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
    
    private CharSequence throwClause(MethodDeclaration e) {
        StringBuilder throwClause = new StringBuilder();
        Collection<ReferenceType> thrownTypes = e.getThrownTypes();
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
    
    private CharSequence parametersIncludingTypes(ConstructorDeclaration e) {
    	StringBuilder methodCode = new StringBuilder();
    	
    	boolean isFirstParam = true;
    	Collection<ParameterDeclaration> parameters = e.getParameters();
        for(ParameterDeclaration param : parameters) {
        	if(!isFirstParam) {
        		methodCode.append(", ");
        	}
        	TypeMirror paramType = param.getType();
        	
        	methodCode.append(paramType.toString());
        	methodCode.append(" ");
        	String paramName = param.toString();
        	//For some reason, the name is "int myVar" if it's a primative type
            //Get rid of the "int" bit.
        	if(paramName.contains(" ")) {
                paramName = paramName.substring(paramName.indexOf(' ') + 1);
            }
            methodCode.append(paramName);
            
            isFirstParam = false;
        }
        
        return methodCode;
    }
    
    private CharSequence parametersIncludingTypes(MethodDeclaration e) {
        StringBuilder methodCode = new StringBuilder();
        
        boolean isFirstParam = true;
        Collection<ParameterDeclaration> parameters = e.getParameters();
        for(ParameterDeclaration param : parameters) {
            if(!isFirstParam) {
                methodCode.append(", ");
            }
            TypeMirror paramType = param.getType();
            
            methodCode.append(paramType.toString());
            methodCode.append(" ");
            String paramName = param.toString();
            //For some reason, the name is "int myVar" if it's a primative type
            //Get rid of the "int" bit.
            if(paramName.contains(" ")) {
                paramName = paramName.substring(paramName.indexOf(' ') + 1);
            }
            methodCode.append(paramName);
            
            isFirstParam = false;
        }
        
        return methodCode;
    }
    
    private CharSequence parametersExcludingTypes(ConstructorDeclaration e) {	
    	StringBuilder paramListCode = new StringBuilder();
    	
    	boolean isFirstParam = true;
    	Collection<ParameterDeclaration> parameters = e.getParameters();
        for(ParameterDeclaration param : parameters) {
        	if(!isFirstParam) {
        		paramListCode.append(", ");
        	}
        	
            String paramName = param.toString();
            //For some reason, the name is "int myVar" if it's a primative type
            //Get rid of the "int" bit.
            if(paramName.contains(" ")) {
                paramName = paramName.substring(paramName.indexOf(' ') + 1);
            }
            paramListCode.append(paramName);
            
            isFirstParam = false;
        }
        
        return paramListCode;
    }
    
    private CharSequence parametersExcludingTypes(MethodDeclaration e) {   
        StringBuilder paramListCode = new StringBuilder();
        
        boolean isFirstParam = true;
        Collection<ParameterDeclaration> parameters = e.getParameters();
        for(ParameterDeclaration param : parameters) {
            if(!isFirstParam) {
                paramListCode.append(", ");
            }
            
            String paramName = param.toString();
            //For some reason, the name is "int myVar" if it's a primative type
            //Get rid of the "int" bit.
            if(paramName.contains(" ")) {
                paramName = paramName.substring(paramName.indexOf(' ') + 1);
            }
            paramListCode.append(paramName);
            
            isFirstParam = false;
        }
        
        return paramListCode;
    }
    
    private void writeHeader(PrintWriter out, String classNamePart, String now) {
        out.println("package com.google.code.facebookapi;");
        out.println();
        //out.println("import javax.annotation.Generated;");
        //out.println();

        //out.println("@Generated(value=\"com.google.code.facebookapi.apt.FacebookReturnTypeProcessor\", date=\"" + now + "\")");
        if(classNamePart.equals("Jaxb")) {
        	out.println("@SuppressWarnings(\"unchecked\")");
        }
        out.println("public class Facebook" + classNamePart + "RestClient extends Facebook" + classNamePart + "RestClientBase {");
        out.println();
    }
	
    public void process() {
	
        DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ");
        String now = isoDateFormat.format(new Date());

        try {
            outJAXB = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookJaxbRestClient");
            outJSON = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookJsonRestClient");
            outXML = processingEnv.getFiler().createSourceFile("com.google.code.facebookapi.FacebookXmlRestClient");
        } catch(IOException ex) {
            System.out.println("Ignoring second attempt to process annotations");
            return;
        }
        
        writeHeader(outJAXB, "Jaxb", now);
        writeHeader(outJSON, "Json", now);
        writeHeader(outXML, "Xml", now);
        
        CopyConstructorVisitor copyConstructorsJaxb = new CopyConstructorVisitor("Jaxb", outJAXB);
        CopyConstructorVisitor copyConstructorsJson = new CopyConstructorVisitor("Json", outJSON);
        CopyConstructorVisitor copyConstructorsXml = new CopyConstructorVisitor("Xml", outXML);
        
        ClassDeclaration facebookJaxbRestClientBase = (ClassDeclaration)processingEnv.getTypeDeclaration("com.google.code.facebookapi.FacebookJaxbRestClientBase");
        for(ConstructorDeclaration cd : facebookJaxbRestClientBase.getConstructors()) {
            cd.accept(copyConstructorsJaxb);
        }

        ClassDeclaration facebookJsonRestClientBase = (ClassDeclaration)processingEnv.getTypeDeclaration("com.google.code.facebookapi.FacebookJsonRestClientBase");
        for(ConstructorDeclaration cd : facebookJsonRestClientBase.getConstructors()) {
            cd.accept(copyConstructorsJson);
        }
        
        ClassDeclaration facebookXmlRestClientBase = (ClassDeclaration)processingEnv.getTypeDeclaration("com.google.code.facebookapi.FacebookXmlRestClientBase");
        for(ConstructorDeclaration cd : facebookXmlRestClientBase.getConstructors()) {
            cd.accept(copyConstructorsXml);
        }
        
        
	    AnnotationVisitor visitor = new AnnotationVisitor();
	    
	    Collection<Declaration> elements = processingEnv.getDeclarationsAnnotatedWith((AnnotationTypeDeclaration)processingEnv.getTypeDeclaration("com.google.code.facebookapi.FacebookReturnType"));
	    
	    for(Declaration element : elements) {
	        element.accept(visitor);
	    }
		
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
	
	/**
	 * These two lines are required to ensure that the
	 * object tree actually bothers to parse the method
	 * parameters. Otherwise they're empty for every method!
	 * @param e
	 */
	private void shakeEnclosingElementMethods(MethodDeclaration e) {
        e.getDeclaringType().getMethods();
	}
	
	class AnnotationVisitor extends SimpleDeclarationVisitor {
		
	    @Override
	    public void visitMethodDeclaration(MethodDeclaration e) {    	
        	shakeEnclosingElementMethods(e);
        	
	    	//Get JAXB and JSON return types - default to Object
	    	String jaxbReturnType = "Object";
	    	String jsonReturnType = "Object";
	    	String xmlReturnType = "org.w3c.dom.Document";
	    	
	        Collection<AnnotationMirror> annotations = e.getAnnotationMirrors();
	        AnnotationMirror firstAnnotation = annotations.iterator().next();
	        Map<AnnotationTypeElementDeclaration, AnnotationValue> annotationParams = firstAnnotation.getElementValues();
	        boolean jaxbAlreadySet = false;
	        for(AnnotationTypeElementDeclaration key : annotationParams.keySet()) {
	        	if(key.getSimpleName().contentEquals("JAXBList")) {
	        		if(annotationParams.get(key) != null) {
	        			jaxbReturnType = "java.util.List<" + stripDotClass(annotationParams.get(key).toString()) + ">";
	        			jaxbAlreadySet = true;
	        		}	        		
	        	}
	            else if(!jaxbAlreadySet && key.getSimpleName().contentEquals("JAXB")) {
	        		if(annotationParams.get(key) != null) {
	        			jaxbReturnType = stripDotClass(annotationParams.get(key).toString());
	        		}
	        	} else if(key.getSimpleName().contentEquals("JSON")) {
	        		if(annotationParams.get(key) != null) {
	        		    jsonReturnType = stripDotClass(annotationParams.get(key).toString());
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
	    }
	}

	
	String stripDotClass(String input) {
	    if(!input.endsWith(".class")) {
	        return input;
	    } else {
	        return input.substring(0, input.length() - 6);
	    }
	}
}
