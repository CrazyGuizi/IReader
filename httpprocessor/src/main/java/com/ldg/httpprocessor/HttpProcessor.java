package com.ldg.httpprocessor;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;


@AutoService(Process.class)
@SupportedAnnotationTypes("com.ldg.httpprocessor.HttpAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class HttpProcessor extends AbstractProcessor {

    public static final String HTTP_API_MANAGER = "HttpApiManager";

    private Filer mFiler;
    private Elements mElementUtils;
    private Messager mMessager;
    private Types mTypeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mTypeUtils = processingEnvironment.getTypeUtils();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
        System.out.println("李德贵");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(HttpAnnotation.class);

        //process会被调用三次，只有一次是可以处理InjectView注解的，原因不明
        if (elements.size() == 0) {
            return true;
        }

        Map<Element, List<Element>> elementMap = new HashMap<>();

        StringBuffer buffer = new StringBuffer();
        buffer.append("package com.ldg.common;\n")
                .append("import java.util.HashMap;\n");

        StringBuilder importPacket = new StringBuilder();
        StringBuilder putMap = new StringBuilder();


        //遍历所有被InjectView注释的元素
        for (Element element : elements) {
            //获取所在类的信息
            Element clazz = element.getEnclosingElement();


            //按类存入map中
            addElement(elementMap, clazz, element);
        }

        for (Map.Entry<Element, List<Element>> entry : elementMap.entrySet()) {
            //生成注入代码
            generateInjectorCode(importPacket, putMap, entry.getValue());
        }

        buffer.append(importPacket.toString());
        buffer.append("public class HttpApiManager {\n" +
                "    private HashMap<Integer, Object> mHashMap = new HashMap<>();\n" +
                "    public void init() {");
        buffer.append(putMap.toString());
        buffer.append("    }\n" +
                "}");

        generateCode(HTTP_API_MANAGER, buffer.toString());
        return true;
    }

    /**
     * 生成注入代码
     *
     * @param views 需要注入的成员变量
     */
    private void generateInjectorCode(StringBuilder packetBuffer, StringBuilder mapBuffer, List<Element> views) {
        for (Element element : views) {
            String className = element.getSimpleName().toString();
            packetBuffer.append("import " + element.asType().toString() + ";\n");
            int requestCode = element.getAnnotation(HttpAnnotation.class).requestCode();
            mapBuffer.append("mHashMap.put(" + requestCode + ", new " + className + "());\n");
        }
    }

    private void addElement(Map<Element, List<Element>> map, Element clazz, Element field) {
        List<Element> list = map.get(clazz);
        if (list == null) {
            list = new ArrayList<>();
            map.put(clazz, list);
        }
        list.add(field);
    }

    private void generateCode(String className, String code) {
        try {
            JavaFileObject file = mFiler.createSourceFile(className);
            Writer writer = file.openWriter();
            writer.write(code);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
