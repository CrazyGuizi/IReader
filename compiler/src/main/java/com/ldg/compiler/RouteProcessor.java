package com.ldg.compiler;

import com.google.auto.service.AutoService;
import com.ldg.annotation.Route;
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
import java.util.logging.Logger;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * created by gui 2020/8/9
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes(Constants.ROUTE_ANNOTATION)
public class RouteProcessor extends AbstractProcessor {

    private String mModuleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mModuleName = processingEnvironment.getOptions().get(Constants.MODULE_NAME);
        System.out.println("编译modelName：" + mModuleName);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (elements == null || elements.isEmpty()) {
            return true;
        }

        Set<TypeElement> elementSet = new HashSet<>();
        for (Element element : elements) {
            if (element.getKind().isClass() && invalidateClass((TypeElement) element)) {
                elementSet.add((TypeElement) element);
            }
        }

        if (mModuleName != null && mModuleName.length() > 0) {
            String moduleName = mModuleName.replace(".", "_").replace("-", "_");
            mModuleName = moduleName;
            generateRouteTable(elementSet);
        }

        return true;
    }

    private void generateRouteTable(Set<TypeElement> elementSet) {
        if (elementSet == null || elementSet.isEmpty()) {
            return;
        }

        // Map<String, Class<?>> map
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(Object.class)));
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "map").build();

        MethodSpec.Builder methodHandle = MethodSpec.methodBuilder("handleTable")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameterSpec);


        Map<String, String> pathMaps = new HashMap<>();

        for (TypeElement typeElement : elementSet) {
            Route route = typeElement.getAnnotation(Route.class);
            String[] values = route.value();
            for (String value : values) {
                if (value == null && value.length() <= 0) {
                    throw new IllegalArgumentException("the route path value of" + typeElement.getSimpleName() +
                            " is invalidate");
                }

                if (pathMaps.containsKey(value)) {
                    throw new IllegalArgumentException("the route path " + value + "is already exit!");
                }

                methodHandle.addStatement("map.put($S, $T.class)", value, ClassName.get(typeElement));
                pathMaps.put(value, typeElement.getQualifiedName().toString());
            }
        }

        TypeElement interfaceType = processingEnv.getElementUtils().getTypeElement(Constants.ROUTE_TABLE_NAME);
        TypeSpec type = TypeSpec.classBuilder(Constants.ROUTE_TABLE + "_" + mModuleName)
                .addSuperinterface(ClassName.get(interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodHandle.build())
                .build();
        try {
            JavaFile.builder(Constants.COMPILE_ROUTE_PACKET, type).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean invalidateClass(TypeElement element) {
        if (element != null) {
            if (!isSubtype(element, Constants.ACTIVITY)
                    && !isSubtype(element, Constants.ANDROID_X_FRAGMENT)) {
                Logger.getLogger("the" + element.getSimpleName() + " is not a Activity or Fragment ");
                return false;
            }

            Set<Modifier> modifiers = element.getModifiers();
            if (modifiers.contains(Modifier.ABSTRACT)) {
                Logger.getLogger("the" + element.getSimpleName() + " must not be abstract");
                return false;
            }
        }
        return true;
    }

    private boolean isSubtype(TypeElement element, String type) {
        return processingEnv.getTypeUtils().isSubtype(element.asType(),
                processingEnv.getElementUtils().getTypeElement(type).asType());
    }

}
