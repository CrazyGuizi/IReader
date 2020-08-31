package com.ldg.gradleplugin.route

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassWriter
import org.gradle.api.Project

public class RouterTransform extends Transform {

    Project mProject

    RouterTransform(Project project) {
        mProject = project
        println "这里是:" + mProject.name
    }

    // 获取Transform的名字
    @Override
    public String getName() {
        return RouterTransform.class.getName();
    }

    // CONTENT_CLASS：编译后的字节码文件（jar 包或目录）
    // CONTENT_RESOURCES：标准的 Java 资源
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    // 作用范围：
    // 1. PROJECT：只处理当前项目
    // 2. SUB_PROJECTS：只处理子项目
    // 3. PROJECT_LOCAL_DEPS：只处理当前项目的本地依赖,例如 jar, aar
    // 4. EXTERNAL_LIBRARIES：只处理外部的依赖库
    // 5. PROVIDED_ONLY：只处理本地或远程以 provided 形式引入的依赖库
    // 6. TESTED_CODE：只处理测试代码
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    // 是否支持增量更新
    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                org.apache.commons.io.FileUtils.copyFile(jarInput.getFile(), dest);
            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                //FileUtils.copyDirectory(directoryInput.getFile(), dest)
                transformDir(directoryInput.getFile(), dest);
            }
        }
    }

    private static void transformDir(File input, File dest) throws IOException {
        if (dest.exists()) {
            org.apache.commons.io.FileUtils.forceDelete(dest);
        }
        org.apache.commons.io.FileUtils.forceMkdir(dest);
        String srcDirPath = input.getAbsolutePath();
        String destDirPath = dest.getAbsolutePath();
        for (File file : input.listFiles()) {
            String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath);
            File destFile = new File(destFilePath);
            if (file.isDirectory()) {
                transformDir(file, destFile);
            } else if (file.isFile()) {
                org.apache.commons.io.FileUtils.touch(destFile);
                weave(file.getAbsolutePath(), destFile.getAbsolutePath());
            }
        }
    }

    private static void weave(String inputPath, String outputPath) {
        try {
            FileInputStream is = new FileInputStream(inputPath);
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//            AsmClassAdapter adapter = new AsmClassAdapter(cw);
//            cr.accept(adapter, ClassReader.EXPAND_FRAMES);
            FileOutputStream fos = new FileOutputStream(outputPath);
            fos.write(cw.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}