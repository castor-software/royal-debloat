package se.kth.jbroom.debloat;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.objectweb.asm.*;

import java.io.*;
import java.util.Map;
import java.util.Set;

public class MethodDebloater {

    //--------------------------------/
    //-------- CLASS FIELD/S --------/
    //------------------------------/

    private String outputDirectory;
    private Map<String, Set<String>> usageAnalysis;

    private static final Logger LOGGER = LogManager.getLogger(MethodDebloater.class.getName());

    //--------------------------------/
    //-------- CONSTRUCTOR/S --------/
    //------------------------------/

    public MethodDebloater(String outputDirectory, Map<String, Set<String>> usageAnalysis) {
        this.outputDirectory = outputDirectory;
        this.usageAnalysis = usageAnalysis;
    }

    //--------------------------------/
    //------- PUBLIC METHOD/S -------/
    //------------------------------/

    public void removeUnusedMethods() throws IOException {
        for (Map.Entry<String, Set<String>> entry : usageAnalysis.entrySet()) {
            if (entry.getValue() != null) {
                removeMethod(entry.getKey().replace(".", "/"), entry.getValue());
            }
        }
    }

    //--------------------------------/
    //------ PRIVATE METHOD/S -------/
    //------------------------------/

    private void removeMethod(String clazz, Set usedMethods) throws IOException {
        FileInputStream in = new FileInputStream(new File(outputDirectory + "/" + clazz + ".class"));
        ClassReader cr = new ClassReader(in);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                if (usedMethods.contains(name + desc)) {
                    LOGGER.info("Removed unused method: " + name + desc + " in class ==> " + clazz);
                    return new MethodThrowerException(mv);
                    // return null;
                }
                return mv;
                // return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };
        cr.accept(cv, ClassReader.SKIP_DEBUG);

        byte[] code = cw.toByteArray();
        OutputStream fos = new FileOutputStream(outputDirectory + "/" + clazz.replace(".", "/") + ".class");
        fos.write(code);
        fos.close();
    }
}
