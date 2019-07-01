package se.kth.jbroom.debloat;

import org.objectweb.asm.*;

import java.io.*;
import java.util.Map;
import java.util.Set;

public class MethodDebloater {

    private String outputDirectory;
    Map<String, Set<String>> usageAnalysis;

    public MethodDebloater(String outputDirectory, Map<String, Set<String>> usageAnalysis) {
        this.outputDirectory = outputDirectory;
        this.usageAnalysis = usageAnalysis;
    }

    public void removeUnusedMethods() throws IOException {

        for (Map.Entry<String, Set<String>> entry : usageAnalysis.entrySet()) {
            if (entry.getValue() != null) {
                removeMethod(entry.getKey().replace(".", "/"), entry.getValue());

            }
        }
    }

    public void removeMethod(String clazz, Set usedMethods) throws IOException {

        FileInputStream in = new FileInputStream(new File(outputDirectory + "/" + clazz + ".class"));
        ClassReader cr = new ClassReader(in);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                System.out.println("Visiting method:  " + name + signature);

                if (usedMethods.contains(name + signature)) {
                    System.out.println("Removed unused method: " + name + " in class " + clazz);
                    return null;
                }
                return super.visitMethod(access, name, desc, signature, exceptions);
            }

        };
        cr.accept(cv, ClassReader.SKIP_DEBUG);

        byte[] code = cw.toByteArray();
        OutputStream fos = new FileOutputStream(outputDirectory + "/" + clazz.replace(".", "/") + ".class");
        fos.write(code);
        fos.close();
    }
}
