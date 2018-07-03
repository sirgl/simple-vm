package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.type.LangType
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets


/**
 * Descriptor of entry from Constant Pool
 */
typealias CPDescriptor = Short

data class ClassInfo(val packageDescriptor: CPDescriptor, val name: String)
data class VarInfo(val typeDescriptor: CPDescriptor, val nameDescriptor: CPDescriptor)
data class MethodInfo(val name: String, val returnTypeDescr: CPDescriptor, val parameterVarDescriptors: List<CPDescriptor>)


enum class CpLabel(val idx: Byte) {
    Str(0),
    Int(1),
    Bool(2),
    Method(3),
    Var(4),
    Type(5),
    ClassRef(6);
}

// It would be good if all entities from CP could be represented in Generalized Key -> CPDescriptor
// Using it we could avoid many hash maps and order list
class ConstantPool(
        // TODO use here primitive map
        private val classes: MutableMap<ClassInfo, CPDescriptor> = hashMapOf(),
        private val ints: MutableMap<Int, CPDescriptor> = hashMapOf(),
        private val strings: MutableMap<String, CPDescriptor> = hashMapOf(),
        private val vars: MutableMap<VarInfo, CPDescriptor> = hashMapOf(),
        private val methods: MutableMap<MethodInfo, CPDescriptor> = hashMapOf()
) {
    private val orderList = mutableListOf<Any>()


    private var currentDescriptor: CPDescriptor = 0

    fun addClass(packageName: String, name: String): CPDescriptor {
        val packageDescriptor = addString(packageName)
        return addClass(packageDescriptor, name)
    }

    fun addClass(packageDescriptor: CPDescriptor, name: String): CPDescriptor {
        return getDescrOrAddValue(ClassInfo(packageDescriptor, name), classes)
    }

    fun addInt(num: Int): CPDescriptor = getDescrOrAddValue(num, ints)

    private fun <K: Any> ConstantPool.getDescrOrAddValue(value: K, map: MutableMap<K, CPDescriptor>): CPDescriptor {
        return map.computeIfAbsent(value) {
            val numDescr = currentDescriptor
            orderList.add(value)
            map[value] = numDescr
            currentDescriptor++
            numDescr
        }
    }

    fun addString(str: String): CPDescriptor = getDescrOrAddValue(str, strings)

    fun addChar(ch: Byte): CPDescriptor = getDescrOrAddValue(ch.toInt(), ints)

    fun addType(type: LangType) = getDescrOrAddValue(type.signature, strings)

    fun addVar(typeDescriptor: CPDescriptor, nameDescriptor: CPDescriptor): CPDescriptor {
        return getDescrOrAddValue(VarInfo(typeDescriptor, nameDescriptor), vars)
    }

    fun addMethod(name: String, returnTypeDescr: CPDescriptor, parameterVarDescriptors: List<CPDescriptor>): CPDescriptor {
        return getDescrOrAddValue(MethodInfo(name, returnTypeDescr, parameterVarDescriptors), methods)
    }

    fun write(stream: DataOutputStream) {
        for (entry in orderList) {
            when (entry) {
                is String -> {
                    stream.writeLabel(CpLabel.Str)
                    stream.writeString(entry)
                }
                is ClassInfo -> {
                    val name = entry.name
                    stream.writeLabel(CpLabel.ClassRef)
                    stream.writeString(name)
                    stream.writeDescr(entry.packageDescriptor)
                }
                is Int -> {
                    stream.writeLabel(CpLabel.Int)
                    stream.writeInt(entry)
                }
                is VarInfo -> {
                    stream.writeLabel(CpLabel.Var)
                    stream.writeDescr(entry.typeDescriptor)
                    stream.writeDescr(entry.nameDescriptor)
                }
                is MethodInfo -> {
                    stream.writeLabel(CpLabel.Method)
                    stream.writeString(entry.name)
                    stream.writeDescr(entry.returnTypeDescr)
                    for (parameterType in entry.parameterVarDescriptors) {
                        stream.writeDescr(parameterType)
                    }
                }
            }
        }
    }

    // TODO label it
    private fun DataOutputStream.writeLabel(label: CpLabel) {
        writeByte(label.idx.toInt())
    }

    private fun DataOutputStream.writeDescr(desc: CPDescriptor) {
        writeShort(desc.toInt())
    }


    private fun DataOutputStream.writeString(str: String) {
        val byteArray = str.toByteArray(StandardCharsets.US_ASCII)
        writeInt(byteArray.size)
        write(byteArray)
    }

    fun resolveDescr(descr: CPDescriptor)  = orderList[descr.toInt()] // Probably more safe way should be supported


    companion object {
//        fun read(): ConstantPool {
//
//        }
    }
}