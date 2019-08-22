package gr.gousiosg.javacg.utils;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;

public interface IReporter {

    void visitINVOKEVIRTUAL(ConstantPoolGen cp, MethodGen m, JavaClass jc, INVOKEVIRTUAL i);

    void visitINVOKEINTERFACE(ConstantPoolGen cp, MethodGen m, JavaClass jc, INVOKEINTERFACE i);

    void visitINVOKESPECIAL(ConstantPoolGen cp, MethodGen m, JavaClass jc, INVOKESPECIAL i);

    void visitINVOKEDYNAMIC(ConstantPoolGen cp, MethodGen m, JavaClass jc, INVOKEDYNAMIC i);

    void visitINVOKESTATIC(ConstantPoolGen cp, MethodGen m, JavaClass jc, INVOKESTATIC i);

    void visitConstantPool(JavaClass jc, Constant constant, ConstantPool constantPool);
}