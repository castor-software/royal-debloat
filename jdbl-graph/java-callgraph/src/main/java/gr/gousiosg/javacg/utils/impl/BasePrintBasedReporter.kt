package gr.gousiosg.javacg.utils.impl

import gr.gousiosg.javacg.utils.IReporter
import org.apache.bcel.classfile.Constant
import org.apache.bcel.classfile.ConstantPool
import org.apache.bcel.classfile.JavaClass
import org.apache.bcel.generic.*
import java.io.Closeable


abstract class BasePrintBasedReporter: Closeable, IReporter{


    abstract fun report(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, invokationType: String, type:String, methodName: String, args: String)



    override fun visitINVOKESTATIC(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, i: INVOKESTATIC?) {
        report(cp, m, jc, "S",
                i?.getReferenceType(cp).toString(),
                i?.getMethodName(cp).toString(),
                i?.getArgumentTypes(cp)?.map { it.toString() }?.joinToString(",") ?: "")
    }


    override fun visitINVOKEVIRTUAL(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, i: INVOKEVIRTUAL?) {
        report(cp, m, jc, "M",
                i?.getReferenceType(cp).toString(),
                i?.getMethodName(cp).toString(),
                i?.getArgumentTypes(cp)?.map { it.toString() }?.joinToString(",") ?: "")
    }

    override fun visitINVOKEINTERFACE(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, i: INVOKEINTERFACE?) {
        report(cp, m, jc, "I",
                i?.getReferenceType(cp).toString(),
                i?.getMethodName(cp).toString(),
                i?.getArgumentTypes(cp)?.map { it.toString() }?.joinToString(",") ?: "")
    }

    override fun visitINVOKESPECIAL(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, i: INVOKESPECIAL?) {
        report(cp, m, jc, "O",
                i?.getType(cp).toString(),
                i?.getMethodName(cp).toString(),
                i?.getArgumentTypes(cp)?.map { it.toString() }?.joinToString(",") ?: "")
    }

    override fun visitINVOKEDYNAMIC(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, i: INVOKEDYNAMIC?) {
        report(cp, m, jc, "D",
                i?.getType(cp).toString(),
                i?.getMethodName(cp).toString(),
                i?.getArgumentTypes(cp)?.map { it.toString() }?.joinToString(",") ?: "")
    }

    override fun visitConstantPool(jc: JavaClass?, constant: Constant?, constantPool: ConstantPool?) {



        println("C:" + jc?.className + " %s".format(constantPool?.constantToString(constant)))
    }

}