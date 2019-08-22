package gr.gousiosg.javacg.utils.impl

import org.apache.bcel.classfile.Constant
import org.apache.bcel.classfile.ConstantPool
import org.apache.bcel.classfile.JavaClass
import org.apache.bcel.generic.ConstantPoolGen
import org.apache.bcel.generic.MethodGen
import java.io.File

class Neo4JReporter: BasePrintBasedReporter(){
    override fun close() {
    }


    val ma = HashMap<String, Int>();

    private fun addNode(repr: String):Int
    {
        if(!ma.containsKey(repr)){
            ma[repr] = ma.size + 1;

            println("CREATE (m%s:METHOD {repr: \"%s\"} )".format(ma[repr], repr))
        }

        return ma[repr]?:-1
    }
    override fun report(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, invokationType: String, type: String, methodName: String, args: String) {
        val aNode = jc?.getClassName() + ":" + m?.name + "(" + m?.argumentTypes?.joinToString(",") + ")"
        val bNode = "%s:%s(%s);".format(invokationType, type, methodName, args)

        var m1 = addNode(aNode)
        var m2 = addNode(bNode)

        println(("CREATE (m%s)-[:CALLS]->(m%s)").format(m1,m2))


    }

    override fun visitConstantPool(jc: JavaClass?, constant: Constant?, constantPool: ConstantPool?) {

    }

}