package com.ldg.gradleplugin.route

import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

public class RouterInject {

    private static List<RouterRecord> mRecords;
    public static final String APT_CLASS = "com/ldg/router/AptHub"
    public static final String STATIC_ROUTER_TABLE_FILED = "sRouteTables"
    public static final String HANDLE_METHOD_NAME = "handleTable"

    public static void handle(File registerClass, List<RouterRecord> records) {
        // todo 注入routeTable
        mRecords = records;
        if (registerClass != null && registerClass.exists()
                && records != null && !records.empty) {
            if (registerClass.name.endsWith(".jar")) {
                def optJar = new File(registerClass.getParent(), registerClass.name + ".opt")
                optJar.deleteOnExit()

                def jarFile = new JarFile(registerClass)

                JarOutputStream jos = new JarOutputStream(new FileOutputStream(optJar))
                def entries = jarFile.entries()
                while (entries.hasMoreElements()) {
                    def element = entries.nextElement()
                    jos.putNextEntry(new ZipEntry(element.name))
                    jarFile.getInputStream(element).withCloseable { is ->
                        if (element.name == RouterTransform.APT_CLASS) {
                            def modifyBytes = modifyBytes(is)
                            jos.write(modifyBytes)
                        } else {
                            jos.write(IOUtils.toByteArray(is))
                        }
                        jos.closeEntry()
                    }
                }
                jos.close()
                jarFile.close()
                registerClass.delete()
                optJar.renameTo(registerClass)
            }
        }
    }

    public static byte[] modifyBytes(InputStream inputStream) {
        inputStream.withCloseable { is ->
            ClassReader cr = new ClassReader(is)
            ClassWriter cw = new ClassWriter(cr, 0)
            ClassVisitor cv = new AptVisitor(cw)
            cr.accept(cv, 0)
            def array = cw.toByteArray()
            println "输出结果" + array
            return array
        }
    }

    private static class AptVisitor extends ClassVisitor {

        AptVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv)
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            def methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
            if (name == '<clinit>') {
                methodVisitor = new ClinitVisitor(methodVisitor)
            }
            return methodVisitor
        }
    }

    private static class ClinitVisitor extends MethodVisitor {

        ClinitVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv)
        }

//        mv.visitTypeInsn(NEW, "com/ldg/apt/router/RouteTable_app");
//        mv.visitInsn(DUP);
//        mv.visitMethodInsn(INVOKESPECIAL, "com/ldg/apt/router/RouteTable_app", "<init>", "()V", false);
//        mv.visitFieldInsn(GETSTATIC, "com/ldg/ireader/Apt", "sRouteTables", "Ljava/util/Map;");
//        mv.visitMethodInsn(INVOKEVIRTUAL, "com/ldg/apt/router/RouteTable_app", "handleTable", "(Ljava/util/Map;)V", false);

        @Override
        void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                mRecords.each { record ->
                    record.aptClass.each { aClassName ->
                        println "注入类：" + aClassName
                        mv.visitTypeInsn(Opcodes.NEW, aClassName)
                        mv.visitInsn(Opcodes.DUP)
                        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, aClassName, "<init>", "()V", false)
                        mv.visitFieldInsn(Opcodes.GETSTATIC, APT_CLASS, STATIC_ROUTER_TABLE_FILED, "Ljava/util/Map;")
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, aClassName, HANDLE_METHOD_NAME, "(Ljava/util/Map;)V", false)
                    }
                }
            }
            super.visitInsn(opcode)
        }
    }
}