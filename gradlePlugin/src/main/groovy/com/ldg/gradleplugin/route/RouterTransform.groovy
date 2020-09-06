package com.ldg.gradleplugin.route

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.google.common.collect.ImmutableList
import jdk.internal.org.objectweb.asm.ClassReader
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.objectweb.asm.Opcodes

import java.util.jar.JarFile

public class RouterTransform extends Transform {
    public static final String APT_CLASS = "com/ldg/router/AptHub.class"
    public static final String APT_ROUTER_TABLE_PACKAGE = "com/ldg/apt/router"
    public static final String ROUTER_TABLE = "com/ldg/router/IRouteTable"

    public static File registerTargetClassFile

    public Map<String, List<RouterRecord>> mRecordMap = [:]
    static List<RouterRecord> mCurModuleRecords

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

    List<RouterRecord> getRecords(String projectName) {
        def records = mRecordMap.get(projectName)
        if (records == null) {
            mRecordMap[projectName] = ImmutableList.of(
                    new RouterRecord(ROUTER_TABLE)
            )
        }

        return mRecordMap[projectName]
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        println ' --------- RouterTransform begin ---------'

        mCurModuleRecords = getRecords(mProject.name)

        def incremental = transformInvocation.incremental
        if (!incremental) {
            transformInvocation.outputProvider.deleteAll()
        }

        transformInvocation.inputs.each {
            TransformInput input ->
                if (!input.jarInputs.empty) {
                    input.jarInputs.each {
                        JarInput jarInput ->
                            File dest = getJarDestFile(transformInvocation, jarInput)
                            if (incremental) {
                                switch (jarInput.status) {
                                    case Status.NOTCHANGED:
                                        break
                                    case Status.ADDED:
                                    case Status.CHANGED:
                                        transformJar(jarInput, dest)
                                        break
                                    case Status.REMOVED:
                                        dest.deleteOnExit()
                                        break
                                }
                            } else {
                                transformJar(jarInput, dest)
                            }
                    }
                }


                if (!input.directoryInputs.empty) {
                    input.directoryInputs.each {
                        def dest = transformInvocation.outputProvider.getContentLocation(
                                it.name,
                                it.contentTypes,
                                it.scopes,
                                Format.DIRECTORY
                        )

                        if (incremental) {
                            def srcDirPath = it.file.absolutePath
                            def destDirPath = dest.absolutePath
                            it.changedFiles.entrySet().each {
                                { File inputFile, Status status ->
                                    def destFilePath = inputFile.absolutePath.replace(srcDirPath, destDirPath)
                                    def destFile = new File(destFilePath)
                                    switch (status) {
                                        case Status.NOTCHANGED:
                                            break
                                        case Status.ADDED:
                                        case Status.CHANGED:
                                            try {
                                                org.apache.commons.io.FileUtils.touch(destFile)
                                            } catch (IOException e) {
                                            }
                                            transformSingleFile(inputFile, destFile)
                                            break

                                        case Status.REMOVED:
                                            org.apache.commons.io.FileUtils.deleteQuietly(destFile)
                                            break
                                    }
                                }
                            }
                        } else {
                            transformDir(it, dest)
                        }
                    }
                }
        }

        if (registerTargetClassFile) {
            println "注入RouterTable处理类：aptHub handle" + registerTargetClassFile.absolutePath
            RouterInject.handle(registerTargetClassFile, mCurModuleRecords)
        } else {
            println "没找到aptHub"
        }
    }

    private File getJarDestFile(TransformInvocation transformInvocation, JarInput jarInput) {
        def destName = jarInput.name
        if (destName.endsWith(".jar")) {
            destName = "${destName.substring(0, destName.length() - 4)}_${DigestUtils.md5Hex(jarInput.file.absolutePath)}"
        }

        return transformInvocation.outputProvider.getContentLocation(
                destName,
                jarInput.contentTypes,
                jarInput.scopes,
                Format.JAR
        )
    }

    void transformJar(JarInput jarInput, File destFile) {

        def shouldScan = excludeJar.each {
            if (jarInput.name.contains(it)) {
                return false
            }
            return true
        }

        if (shouldScan) {
            println '可以扫描的jar包名: ' + jarInput.name + "\t路径:" + jarInput.file.absolutePath

            scanJar(jarInput.file, destFile)
        }
        FileUtils.copyFile(jarInput.file, destFile)
    }

    void scanJar(File src, File dest) {
        if (src && src.exists()) {
            def jarFile = new JarFile(src)
            def entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                def element = entries.nextElement()
                if (element.name == APT_CLASS) {
                    registerTargetClassFile = dest
                } else if (element.name.startsWith(APT_ROUTER_TABLE_PACKAGE)) {
                    def inputStream = jarFile.getInputStream(element)
                    inputStream.withCloseable {
                        visitClass(it)
                    }
                }
            }
            jarFile.close()
        }
    }

    void transformSingleFile(File inputFile, File destFile) {
        if (inputFile.isFile() && shouldScan(inputFile)) {
            println '扫描文件：' + inputFile.absolutePath
            visitClass(new FileInputStream(inputFile))
        }

        FileUtils.copyFile(inputFile, destFile)
    }

    void transformDir(DirectoryInput directoryInput, File dest) {
        directoryInput.file.eachFileRecurse {
            File file ->
                if (file.isFile() && shouldScan(file)) {
                    println '扫描文件：' + file.absolutePath
                    visitClass(new FileInputStream(file))
                }
        }

        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    boolean shouldScan(File inputFile) {
        return inputFile.absolutePath.replaceAll("\\\\", "/").contains(APT_ROUTER_TABLE_PACKAGE)
    }

    Set<String> excludeJar = ["com.android.support", "android.arch.", "androidx."]

    private void visitClass(InputStream it) {
        ClassReader reader = new ClassReader(it)

        RouterVisitor visitor = new RouterVisitor()
        reader.accept(visitor, 0)
        it.close()
    }

    public static class RouterVisitor extends jdk.internal.org.objectweb.asm.ClassVisitor {
        RouterVisitor() {
            super(Opcodes.ASM5)
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            if (interfaces != null) {
                mCurModuleRecords.each {
                    RouterRecord record ->
                        interfaces.each {
                            if (it == record.templateName) {
                                record.aptClass.add(name)
                            }
                        }
                }
            }
        }
    }

}