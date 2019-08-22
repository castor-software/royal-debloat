package gr.gousiosg.javacg.utils.impl

import gr.gousiosg.javacg.utils.IReporter
import org.apache.bcel.classfile.Constant
import org.apache.bcel.classfile.ConstantPool
import org.apache.bcel.classfile.JavaClass
import org.apache.bcel.generic.*

class DotGraphReporter: BasePrintBasedReporter(){
    override fun close() {
        println("}")//To change body of created functions use File | Settings | File Templates.
    }


    init{
        println("Digraph cg {")
    }


    val map: HashMap<String, Int> = HashMap()

    val set = HashSet<String>()

    override fun report(cp: ConstantPoolGen?, m: MethodGen?, jc: JavaClass?, invokationType: String, type:String, methodName: String, args: String){

        val aNode = jc?.getClassName()?:"UNKNOWN"
        val bNode = type


        if(!map.containsKey(aNode)) {
            map.put(aNode, map.size + 1)
            println("%s [label=\"%s\"];".format(map[aNode], aNode))
        }

        if(!map.containsKey(bNode)) {
            map.put(bNode, map.size + 1)
            println("%s [label=\"%s\"];".format(map[bNode],bNode))
        }

        println("%s -> %s;".format(map.get(aNode), map.get(bNode)))
    }

    override fun visitConstantPool(jc: JavaClass?, constant: Constant?, constantPool: ConstantPool?) {

    }
}