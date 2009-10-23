package com.google.code.facebookapi.apt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class FacebookReturnTypeProcessorFactory5 implements AnnotationProcessorFactory {

    // Process any set of annotations
    private static final Collection<String> supportedAnnotations
        = Collections.unmodifiableCollection(Collections.singletonList("com.google.code.facebookapi.FacebookReturnType"));

    // No supported options
    private static final Collection<String> supportedOptions = Collections.emptySet();

    public Collection<String> supportedAnnotationTypes() {
        return supportedAnnotations;
    }

    public Collection<String> supportedOptions() {
        return supportedOptions;
    }

    public AnnotationProcessor getProcessorFor(
            Set<AnnotationTypeDeclaration> atds,
            AnnotationProcessorEnvironment env) {
        return new FacebookReturnTypeProcessor5(env);
    }
}

