package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.type.LangType
import java.io.DataOutputStream


/**
 * Descriptor of entry from Constant Pool
 */
typealias CPDescriptor = Short


class ConstantPool {
    // Pretty inefficient thing
    private val entries = hashMapOf<ConstantPoolEntry, CPDescriptor>()
    private val orderList = mutableListOf<ConstantPoolEntry>()


    private var currentDescriptor: CPDescriptor = 0

    fun addClass(packageName: String, simpleName: String): CPDescriptor {
        val packageDescriptor = addString(packageName)
        val simpleNameDescriptor = addString(simpleName)
        return addClass(simpleNameDescriptor, packageDescriptor)
    }

    fun addClass(simpleNameDescriptor: CPDescriptor, packageDescriptor: CPDescriptor): CPDescriptor {
        return getDescrOrAddValue(ClassCPEntry(simpleNameDescriptor, packageDescriptor))
    }

    fun addInt(num: Int): CPDescriptor = getDescrOrAddValue(IntCPEntry(num))

    fun addString(str: String): CPDescriptor = getDescrOrAddValue(StringCPEntry(str))

    fun getDescrOrAddValue(entry: ConstantPoolEntry): CPDescriptor {
        return entries.computeIfAbsent(entry) {
            val numDescr = currentDescriptor
            orderList.add(entry)
            entries[entry] = numDescr
            currentDescriptor++
            numDescr
        }
    }

    fun addChar(ch: Byte): CPDescriptor = getDescrOrAddValue(IntCPEntry(ch.toInt()))

    fun addType(type: LangType) = getDescrOrAddValue(StringCPEntry(type.signature))

    fun addVar(typeSignatureDescriptor: CPDescriptor, nameDescriptor: CPDescriptor): CPDescriptor {
        return getDescrOrAddValue(VariableCPEntry(typeSignatureDescriptor, nameDescriptor))
    }

    fun addMethod(
            classDescriptor: CPDescriptor,
            nameDescriptor: CPDescriptor,
            returnTypeDescr: CPDescriptor,
            parameterVarDescriptors: List<CPDescriptor>
    ): CPDescriptor {
        return getDescrOrAddValue(MethodCPEntry(
                classDescriptor,
                nameDescriptor,
                returnTypeDescr,
                parameterVarDescriptors
        ))
    }

    fun write(stream: DataOutputStream) {
        stream.writeInt(orderList.size)
        for (entry in orderList) {
            stream.writeLabel(entry.label)
            entry.writeContent(stream)
        }
    }

    fun resolve(descr: CPDescriptor) = orderList.getOrNull(descr.toInt())

    companion object {
//        fun read(): ConstantPool {
//
//        }
    }
}

private fun DataOutputStream.writeLabel(label: CpLabel) {
    writeByte(label.idx.toInt())
}


// TODO label it
