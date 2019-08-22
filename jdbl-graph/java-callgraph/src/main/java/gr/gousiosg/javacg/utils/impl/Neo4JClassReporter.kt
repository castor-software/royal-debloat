package gr.gousiosg.javacg.utils.impl

import org.apache.bcel.classfile.Constant
import org.apache.bcel.classfile.ConstantPool
import org.apache.bcel.classfile.JavaClass
import org.apache.bcel.generic.ConstantPoolGen
import org.apache.bcel.generic.MethodGen
import java.io.File

class Neo4JClassReporter: BasePrintBasedReporter(){
    override fun close() {
    }


    val ma = HashMap<String, Int>()
    val set = HashSet<String>()

    private fun addNode(repr: String):Int
    {
        if(!ma.containsKey(repr)){
            ma[repr] = ma.size + 1;

            println("CREATE (m%s:METHOD {repr: \"%s\"} )".format(ma[repr], repr))
        }

        return ma[repr]?:-1
    }
    override fun report(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, invokationType: String, type: String, methodName: String, args: String) {
        val aNode = jc?.getClassName()?:"UNKNOWN"
        val bNode = "%s".format(type)

        var m1 = addNode(aNode)
        var m2 = addNode(bNode)

        if(!set.contains("%s%s".format(m1, m2))){
            println(("CREATE (m%s)-[:CALLS]->(m%s)").format(m1,m2))

            set.add("%s%s".format(m1, m2))
        }


    }

    override fun visitConstantPool(jc: JavaClass?, constant: Constant?, constantPool: ConstantPool?) {

    }

}