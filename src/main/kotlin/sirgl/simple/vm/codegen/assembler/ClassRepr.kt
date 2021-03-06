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
            if (method.bytecode != null) {
                stream.writeByte(1) // modifier list byte
                stream.writeInt(method.bytecode.size)
                stream.write(method.bytecode)
            } else {
                stream.writeByte(0) // modifier list byte
            }
        }
    }

    private fun DataOutputStream.write(desc: CPDescriptor) {
        writeShort(desc.toInt())
    }

    override fun toString() = buildString {
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
                append((constantPool.resolve(field) as? VariableCPEntry)?.getPresentableContent(constantPool)
                        ?: "Unknown field")
                append("\n")
            }
        }

        if (methods.isNotEmpty()) {
            append("Methods:\n")
            for (method in methods) {
                appendMethod(method)
                append("\n")
            }
        }
    }

    private fun StringBuilder.appendMethod(method: MethodWithBytecode) {
        val methodHeader = (constantPool.resolve(method.descr) as? MethodCPEntry)?.toStringWithoutPackage(constantPool)
                ?: "<Unknown method>"
        append(methodHeader)
        append("\n")
        appendMethodBody(method)
    }

    fun createInstructionIndex(bytecode: ByteArray): IntArray {
        val size = bytecode.size
        val indices = IntArray(size)
        var currentInstruction = 0
        var i = 0
        while (i < size) {
            val b = bytecode[i]
            val opcode = Opcode.values()[b.toInt()]
            indices[i] = currentInstruction
            if (opcode.hasInlineOperand) {
                indices[i + 1] = currentInstruction
                indices[i + 2] = currentInstruction
                i += 2
            }
            i++
            currentInstruction++
        }
        return indices
    }

    fun StringBuilder.appendMethodBody(method: MethodWithBytecode) {
        val bytecode = method.bytecode
        if (bytecode != null) {
            val instructionIndex = createInstructionIndex(bytecode)
            var i = 0
            val bytecodeLength = bytecode.size
            var currentInstructionNumber = 0
            while (i < bytecodeLength) {
                val b = bytecode[i]
                val opcode = Opcode.values()[b.toInt()]
                append(currentInstructionNumber).append("\t").append(opcode)
                if (opcode.hasInlineOperand) {
                    append(" ")
                    val operand = (bytecode[i + 1].toInt() or (bytecode[i + 2].toInt() shl 8)).toShort()
                    i += 2
                    when (opcode.inlineOerandType) {
                        InlineOperandType.NoInlineOperand -> throw IllegalStateException()
                        InlineOperandType.ConstantPoolEntry -> {
                            append("#$operand ->  ")
                            append(constantPool.resolve(operand)?.getPresentableContent(constantPool)
                                    ?: "<Unknown constant>")
                        }
                        InlineOperandType.Label -> append(instructionIndex[operand.toInt()])
                        InlineOperandType.VariableSlot -> append(operand.toInt())
                    }
                }
                append("\n")
                i++
                currentInstructionNumber++
            }
        }
    }

    private fun getQualifiedClassName(descr: CPDescriptor): String {
        return (constantPool.resolve(descr) as? ClassCPEntry)?.getPresentableContent(constantPool) ?: "<Unknown class>"
    }
}