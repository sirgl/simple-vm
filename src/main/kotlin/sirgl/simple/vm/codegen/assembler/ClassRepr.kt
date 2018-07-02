package sirgl.simple.vm.codegen.assembler

import java.io.DataOutputStream

class ClassRepr(
        val bytecodeVersion: Short,
        val parentClassDescriptor: CPDescriptor,
        val classDescriptor: CPDescriptor,
        val constantPool: ConstantPool,
        val methods: List<MethodWithBytecode>,
        val fields: List<CPDescriptor>
) {
    fun write(stream: DataOutputStream) {
        // General meta info
        stream.writeInt(MAGIC_VALUE)
        stream.writeShort(bytecodeVersion.toInt())

        // Class description
        stream.write(classDescriptor)
        stream.write(parentClassDescriptor)

        // Field descriptions
        stream.writeInt(fields.size)
        for (fieldDescr in fields) {
            stream.write(fieldDescr)
        }

        //Constant pool
        constantPool.write(stream)

        // Method descriptions
        stream.writeInt(methods.size)
        for (method in methods) {
            stream.write(method.descr)
            stream.write(method.bytecode)
        }
    }

    private fun DataOutputStream.write(desc: CPDescriptor) {
        writeShort(desc.toInt())
    }

    override fun toString(): String {
        return buildString {
            append("Bytecode version: ")
            append(bytecodeVersion.toInt())
            append("\n")

            append("Parent class: ")
            append(getQualifiedClassName(parentClassDescriptor))
            append("\n")
            append("Current class: ")
            append(getQualifiedClassName(classDescriptor))
            append("\n")

            if (fields.isNotEmpty()) {
                append("Fields:\n")
                for (field in fields) {
                    append("\t")
                    append(getVarStr(field))
                    append("\n")
                }
            }

            if (methods.isNotEmpty()) {
                append("Methods:\n")
                for (method in methods) {
                    val methodInfo = constantPool.resolveDescr(method.descr) as MethodInfo
                    append(methodInfo.name)
                    append(" : ")
                    val returnTypeStr = constantPool.resolveDescr(methodInfo.returnTypeDescr) as String
                    append(returnTypeStr)
                    append("(")
                    var first = true
                    for (parameterVarDescriptor in methodInfo.parameterVarDescriptors) {
                        if (!first) {
                            append(", ")
                            first = false
                        }
                        append(getVarStr(parameterVarDescriptor))
                    }
                    append(")")
                    var inlineOperandBytesLeft = 0
                    var inlineOperandBuilder = 0
                    var inlineOperandReady = false
                    for (byte in method.bytecode) {
                        if (inlineOperandBytesLeft > 0) {
                            inlineOperandBuilder = (inlineOperandBuilder shr 8) and byte.toInt()
                            inlineOperandBytesLeft--
                            if (inlineOperandBytesLeft == 0) {
                                inlineOperandReady = true
                            }
                            continue
                        }
                        append("\t")
                        if (inlineOperandReady) {
                            val opcode = byteToOpcode[byte.toInt()]
                            val inlineOperand = inlineOperandBuilder.toShort()
                            append(opcode.ordinal)
                            append(" ")
                            append(inlineOperand)
                        } else {
                            val opcode = byteToOpcode[byte.toInt()]
                            append(opcode.ordinal)
                        }
                        append("\n")
                    }
                }
            }

        }
    }

    private fun getVarStr(varDescr: CPDescriptor) : String {
        val varInfo = constantPool.resolveDescr(varDescr) as VarInfo
        val name = constantPool.resolveDescr(varInfo.nameDescriptor) as String
        val typeStr = constantPool.resolveDescr(varInfo.typeDescriptor) as String
        return "$name : $typeStr"
    }

    private fun getQualifiedClassName(descr: CPDescriptor): String {
        val classInfo = constantPool.resolveDescr(descr) as ClassInfo
        val packageStr = constantPool.resolveDescr(classInfo.packageDescriptor) as String
        return packageStr + "." + classInfo.name
    }
}