package com.ldg.httpprocessor;

import com.google.auto.service.AutoService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


@AutoService(Process.class)
@SupportedAnnotationTypes("com.ldg.httpprocessor.HttpAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class HttpProcessor extends AbstractProcessor {

    public static final String HTTP_API_SERVICE_IMPL = "HttpApiServiceImpl_";
    public static String MODULE_NAME = "";

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
        MODULE_NAME = processingEnvironment.getOptions().get("module");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(HttpAnnotation.class);

        StringBuffer buffer = new StringBuffer();
        buffer.append("package com.ldg.common;\n")
                .append("import com.ldg.common.http.api.IHttpApi;\n")
                .append("import com.ldg.common.http.api.HttpApiManager;")
                .append("import java.util.HashMap;\n");

        StringBuilder importPacket = new StringBuilder();
        StringBuilder putMap = new StringBuilder();


        //遍历所有注释的元素
        for (Element element : elements) {
            generateInjectorCode(importPacket, putMap, element);
            mMessager.printMessage(Diagnostic.Kind.WARNING, "纯纯粹粹");
        }

        buffer.append(importPacket.toString());
        buffer.append("public class " + HTTP_API_SERVICE_IMPL + MODULE_NAME + " implements IHttpApi {\n" +
                "\n" +
                "    @Override\n" +
                "    public void addApi() {\n");
        buffer.append(putMap.toString());
        buffer.append("    }\n" +
                "}");

        generateCode(HTTP_API_SERVICE_IMPL + MODULE_NAME, buffer.toString());
        return true;
    }

    /**
     * 生成注入代码
     */
    private void generateInjectorCode(StringBuilder packetBuffer, StringBuilder mapBuffer, Element element) {
        String className = element.getSimpleName().toString();
        packetBuffer.append("import " + element.asType().toString() + ";\n");
        int requestCode = element.getAnnotation(HttpAnnotation.class).requestCode();
        mapBuffer.append("HttpApiManager.addApi(" + requestCode + ", new " + className + "());\n");
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
