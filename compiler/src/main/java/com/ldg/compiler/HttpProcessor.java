package com.ldg.compiler;

import com.google.auto.service.AutoService;
import com.ldg.annotation.HttpAnnotation;
import com.ldg.compiler.utils.Constants;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedAnnotationTypes(Constants.HTTP_ANNOTATION_NAME)
public class HttpProcessor extends AbstractProcessor {

    String mModuleName;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mModuleName = processingEnvironment.getOptions().get(Constants.MODULE_NAME);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(HttpAnnotation.class);
        if (elements == null || elements.isEmpty()) {
            return true;
        }
        Set<TypeElement> typeElements = new HashSet<>();
        for (Element element : elements) {
            if (element.getKind().isClass() && validateClass((TypeElement) element)) {
                typeElements.add((TypeElement) element);
            }
        }

        if (mModuleName != null) {
            String finalModuleName = "HttpApiImpl_" + mModuleName
                    .replace(".", "_")
                    .replace("-", "_");
            generateHttpApi(finalModuleName, typeElements);
        }
        return true;
    }

    private boolean validateClass(TypeElement element) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.ABSTRACT)) {
            return false;
        }

        return true;
    }


    private void generateHttpApi(String finalModuleName, Set<TypeElement> elements) {
        // Map<Integer, Class<?>> map
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(Integer.class),
                ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class)));
        ParameterSpec mapParameter = ParameterSpec.builder(mapTypeName, "map").build();

        MethodSpec.Builder methodHandler = MethodSpec.methodBuilder(Constants.HTTP_ANNOTATION_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameter);

        Map<Integer, String> record = new HashMap<>();
        for (TypeElement element : elements) {
            HttpAnnotation httpAnnotation = element.getAnnotation(HttpAnnotation.class);
            int requestCode = httpAnnotation.requestCode();
            if (record.containsKey(requestCode)) {
                throw new RuntimeException("the requestCode " + requestCode + " of "
                        + record.get(requestCode) + " is exit");
            } else {
                methodHandler.addStatement("map.put($L,$T.class)", requestCode, ClassName.get(element));
                record.put(requestCode, element.getQualifiedName().toString());
            }
        }

        TypeElement interfaceType = processingEnv.getElementUtils()
                .getTypeElement(Constants.HTTP_API_INTERFACE_NAME);

        TypeSpec type = TypeSpec.classBuilder(finalModuleName)
                .addSuperinterface(ClassName.get(interfaceType))
                .addMethod(methodHandler.build())
                .addModifiers(Modifier.PUBLIC)
                .build();

        try {
            JavaFile.builder(Constants.COMPILER_HTTP_PACKET, type).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}