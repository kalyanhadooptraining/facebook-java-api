package com.google.code.facebookapi.apt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
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

@SuppressWarnings("restriction")
public class FacebookReturnTypeProcessor5 implements AnnotationProcessor {

	private AnnotationProcessorEnvironment processingEnv;

	public FacebookReturnTypeProcessor5( AnnotationProcessorEnvironment processingEnv ) {
		this.processingEnv = processingEnv;
	}



	public static class CopyConstructorVisitor extends SimpleDeclarationVisitor {

		private String clientType;
		private PrintWriter out;

		public CopyConstructorVisitor( String clientType, PrintWriter out ) {
			this.clientType = clientType;
			this.out = out;
		}

		@Override
		public void visitConstructorDeclaration( ConstructorDeclaration e ) {
			out.print( "    " );
			out.print( modifiers( e ) );
			out.print( " " );
			out.print( "Facebook" );
			out.print( clientType );
			out.print( "RestClient" );
			out.print( "( " );
			out.print( parametersIncludingTypes( e ) );
			out.print( " ) " );
			out.print( throwClause( e ) );
			out.println( " {" );
			out.print( "        super( " );
			out.print( parametersExcludingTypes( e ) );
			out.println( " );" );
			out.println( "    }" );
			out.println();
		}

	}

	private static CharSequence modifiers( ConstructorDeclaration e ) {
		StringBuilder modifiers = new StringBuilder();
		Collection<Modifier> modifierSet = e.getModifiers();
		boolean isFirstModifier = true;
		for ( Modifier m : modifierSet ) {
			if ( !isFirstModifier ) {
				modifiers.append( " " );
			}
			modifiers.append( m.toString() );
		}

		return modifiers;
	}

	private static CharSequence throwClause( ConstructorDeclaration e ) {
		StringBuilder throwClause = new StringBuilder();
		Collection<ReferenceType> thrownTypes = e.getThrownTypes();
		boolean isFirstThrows = true;
		for ( TypeMirror t : thrownTypes ) {
			if ( isFirstThrows ) {
				throwClause.append( "throws " );
			} else {
				throwClause.append( ", " );
			}
			throwClause.append( t.toString() );
		}

		return throwClause;
	}

	private static CharSequence throwClause( MethodDeclaration e ) {
		StringBuilder throwClause = new StringBuilder();
		Collection<ReferenceType> thrownTypes = e.getThrownTypes();
		boolean isFirstThrows = true;
		for ( TypeMirror t : thrownTypes ) {
			if ( isFirstThrows ) {
				throwClause.append( "throws " );
			} else {
				throwClause.append( ", " );
			}
			throwClause.append( t.toString() );
		}

		return throwClause;
	}

	private static CharSequence parametersIncludingTypes( ConstructorDeclaration e ) {
		StringBuilder methodCode = new StringBuilder();

		boolean isFirstParam = true;
		Collection<ParameterDeclaration> parameters = e.getParameters();
		for ( ParameterDeclaration param : parameters ) {
			if ( !isFirstParam ) {
				methodCode.append( ", " );
			}
			TypeMirror paramType = param.getType();

			methodCode.append( paramType.toString() );
			methodCode.append( " " );
			String paramName = param.toString();
			// For some reason, the name is "int myVar" if it's a primative type
			// Get rid of the "int" bit.
			if ( paramName.contains( " " ) ) {
				paramName = paramName.substring( paramName.indexOf( ' ' ) + 1 );
			}
			methodCode.append( paramName );

			isFirstParam = false;
		}

		return methodCode;
	}

	private static CharSequence parametersIncludingTypes( MethodDeclaration e ) {
		StringBuilder methodCode = new StringBuilder();

		boolean isFirstParam = true;
		Collection<ParameterDeclaration> parameters = e.getParameters();
		for ( ParameterDeclaration param : parameters ) {
			if ( !isFirstParam ) {
				methodCode.append( ", " );
			}
			TypeMirror paramType = param.getType();

			methodCode.append( paramType.toString() );
			methodCode.append( " " );
			String paramName = param.toString();
			// For some reason, the name is "int myVar" if it's a primative type
			// Get rid of the "int" bit.
			if ( paramName.contains( " " ) ) {
				paramName = paramName.substring( paramName.indexOf( ' ' ) + 1 );
			}
			methodCode.append( paramName );

			isFirstParam = false;
		}

		return methodCode;
	}

	private static CharSequence parametersExcludingTypes( ConstructorDeclaration e ) {
		StringBuilder paramListCode = new StringBuilder();

		boolean isFirstParam = true;
		Collection<ParameterDeclaration> parameters = e.getParameters();
		for ( ParameterDeclaration param : parameters ) {
			if ( !isFirstParam ) {
				paramListCode.append( ", " );
			}

			String paramName = param.toString();
			// For some reason, the name is "int myVar" if it's a primative type
			// Get rid of the "int" bit.
			if ( paramName.contains( " " ) ) {
				paramName = paramName.substring( paramName.indexOf( ' ' ) + 1 );
			}
			paramListCode.append( paramName );

			isFirstParam = false;
		}

		return paramListCode;
	}

	private static CharSequence parametersExcludingTypes( MethodDeclaration e ) {
		StringBuilder paramListCode = new StringBuilder();

		boolean isFirstParam = true;
		Collection<ParameterDeclaration> parameters = e.getParameters();
		for ( ParameterDeclaration param : parameters ) {
			if ( !isFirstParam ) {
				paramListCode.append( ", " );
			}

			String paramName = param.toString();
			// For some reason, the name is "int myVar" if it's a primative type
			// Get rid of the "int" bit.
			if ( paramName.contains( " " ) ) {
				paramName = paramName.substring( paramName.indexOf( ' ' ) + 1 );
			}
			paramListCode.append( paramName );

			isFirstParam = false;
		}

		return paramListCode;
	}

	public void process() {
		PrintWriter outJAXB = openClassFile( "Jaxb" );
		PrintWriter outJSON = openClassFile( "Json" );
		PrintWriter outXML = openClassFile( "Xml" );

		if ( outJAXB == null && outJSON == null && outXML == null ) {
			return;
		}

		final AnnotationTypeDeclaration annotationType = (AnnotationTypeDeclaration) processingEnv.getTypeDeclaration( "com.google.code.facebookapi.FacebookReturnType" );
		Collection<Declaration> elements = processingEnv.getDeclarationsAnnotatedWith( annotationType );

		AnnotationVisitor visitor = new AnnotationVisitor( outJAXB, outJSON, outXML );
		for ( Declaration element : elements ) {
			element.accept( visitor );
		}

		closeClassFile( outJAXB );
		closeClassFile( outJSON );
		closeClassFile( outXML );
	}

	private PrintWriter openClassFile( String type ) {
		final String codepackage = "com.google.code.facebookapi";
		final String mainClassName = "Facebook" + type + "RestClient";
		final String baseClassName = mainClassName + "Base";
		final String mainClass = codepackage + "." + mainClassName;
		final String baseClass = codepackage + "." + baseClassName;
		try {
			PrintWriter out = processingEnv.getFiler().createSourceFile( mainClass );
			out.println( String.format( "package %s;", codepackage ) );
			out.println();
			if ( type.equals( "Jaxb" ) ) {
				out.println( "@SuppressWarnings(\"unchecked\")" );
			}
			out.println( String.format( "public class %s extends %s {", mainClassName, baseClassName ) );
			out.println();
			CopyConstructorVisitor copyConstructors = new CopyConstructorVisitor( type, out );
			ClassDeclaration clientBase = (ClassDeclaration) processingEnv.getTypeDeclaration( baseClass );
			for ( ConstructorDeclaration cd : clientBase.getConstructors() ) {
				cd.accept( copyConstructors );
			}
			return out;
		}
		catch ( IOException ex ) {
			System.err.println( "Ignoring IOException during: " + type + "; " + ex );
			return null;
		}
	}

	private void closeClassFile( PrintWriter out ) {
		if ( out == null ) {
			return;
		}
		out.println( "}" );
		out.flush();
		out.close();
	}



	public static class AnnotationVisitor extends SimpleDeclarationVisitor {

		private PrintWriter outJAXB;
		private PrintWriter outJSON;
		private PrintWriter outXML;

		public AnnotationVisitor( PrintWriter outJAXB, PrintWriter outJSON, PrintWriter outXML ) {
			this.outJAXB = outJAXB;
			this.outJSON = outJSON;
			this.outXML = outXML;
		}

		/**
		 * These two lines are required to ensure that the object tree actually bothers to parse the method parameters. Otherwise they're empty for every method!
		 */
		private static void shakeEnclosingElementMethods( MethodDeclaration e ) {
			e.getDeclaringType().getMethods();
		}

		@Override
		public void visitMethodDeclaration( MethodDeclaration e ) {
			shakeEnclosingElementMethods( e );

			// Get JAXB and JSON return types - default to Object
			String jaxbReturnType = "Object";
			String jsonReturnType = "Object";
			String xmlReturnType = "org.w3c.dom.Document";

			Collection<AnnotationMirror> annotations = e.getAnnotationMirrors();
			AnnotationMirror firstAnnotation = annotations.iterator().next();
			Map<AnnotationTypeElementDeclaration,AnnotationValue> annotationParams = firstAnnotation.getElementValues();
			boolean jaxbAlreadySet = false;
			for ( AnnotationTypeElementDeclaration key : annotationParams.keySet() ) {
				String name = key.getSimpleName();
				String val = annotationParams.get( key ).toString();
				if ( name.contentEquals( "JAXBList" ) ) {
					if ( annotationParams.get( key ) != null ) {
						jaxbReturnType = "java.util.List<" + stripDotClass( val ) + ">";
						jaxbAlreadySet = true;
					}
				} else if ( !jaxbAlreadySet && name.contentEquals( "JAXB" ) ) {
					if ( annotationParams.get( key ) != null ) {
						jaxbReturnType = stripDotClass( val );
					}
				} else if ( name.contentEquals( "JSON" ) ) {
					if ( annotationParams.get( key ) != null ) {
						jsonReturnType = stripDotClass( val );
					}
				}
			}

			boolean deprecated = e.getAnnotation( Deprecated.class ) != null;

			String methName = e.getSimpleName();

			String methSig = "    public %s %s( %s ) %s {";
			methSig = String.format( methSig, "%RETURNTYPE%", methName, parametersIncludingTypes( e ), throwClause( e ) );

			String methCall = "        Object rawResponse = client.%s( %s );";
			methCall = String.format( methCall, methName, parametersExcludingTypes( e ) );

			String methRet = "        return (%s)parseCallResult( rawResponse );";
			methRet = String.format( methRet, "%RETURNTYPE%" );

			String methRet2 = "        return parseCallResult( %s.class, rawResponse );";
			methRet2 = String.format( methRet2, "%RETURNTYPE%" );

			printMethod( outJAXB, jaxbReturnType, deprecated, methSig, methCall, methRet );
			printMethod( outJSON, jsonReturnType, deprecated, methSig, methCall, methRet2 );
			printMethod( outXML, xmlReturnType, deprecated, methSig, methCall, methRet );
		}

		public static void printMethod( PrintWriter out, String returnType, boolean deprecated, String methSig, String methCall, String methRet ) {
			if ( out == null ) {
				return;
			}
			if ( deprecated ) {
				out.println( "    @Deprecated" );
			}
			out.println( methSig.replace( "%RETURNTYPE%", returnType ) );
			out.println( methCall );
			out.println( methRet.replace( "%RETURNTYPE%", returnType ) );
			out.println( "    }" );
			out.println();
		}

		public static String stripDotClass( String input ) {
			if ( !input.endsWith( ".class" ) ) {
				return input;
			} else {
				return input.substring( 0, input.length() - 6 );
			}
		}

	}

}
