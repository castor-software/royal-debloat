package gr.gousiosg.javacg.utils.impl

import gr.gousiosg.javacg.utils.IReporter
import org.apache.bcel.classfile.Constant
import org.apache.bcel.classfile.ConstantPool
import org.apache.bcel.classfile.JavaClass
import org.apache.bcel.generic.*

class StdoutReporter: BasePrintBasedReporter() {
    override fun close() {

    }

    override fun report(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, invokationType: String, type:String, methodName: String, args: String){
        println("M:" + jc?.getClassName() + ":" + m?.name + "(" + m?.argumentTypes?.joinToString(",") + ")"
                + " " + "(%s)%s:%s(%s)".format(invokationType, type, methodName, args))
    }

}