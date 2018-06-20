package sirgl.simple.vm.codegen

import sirgl.simple.vm.codegen.assembler.Instruction
import sirgl.simple.vm.codegen.assembler.Label
import sirgl.simple.vm.type.*
import sun.nio.cs.US_ASCII
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer

val LANG_BYTECODE_VERSION: Short = 0
val MAGIC_VALUE: Int = 0x0B1B2B3B

class LangClassWriter(
        val className: String,
        val parentClassName: String,
        val compilationUnitName: String,
        val constantPool: ConstantPool,
        val version: Short = LANG_BYTECODE_VERSION
) {
    val localMethodData = mutableListOf<LocalMethodData>()

    fun addMethod(method: LocalMethodData) {
        localMethodData.add(method)
    }

    fun writeTo(stream: OutputStream) {
        val outputStream = DataOutputStream(BufferedOutputStream(stream))
        outputStream.writeInt(MAGIC_VALUE)
        outputStream.write(constantPool.toByteArray())
        for (data in localMethodData) {

        }

    }
}

//TODO
//const val OP_ADD
class MethodWriter {
    val instructions = mutableListOf<Instruction>()
    var position: Int = 0

    lateinit var name: String

    fun emit(insn: Instruction) {
        position += insn.size
        if (position > 65535) {
            throw UnsupportedOperationException("")
        }
        instructions.add(insn)
    }

    fun label() = Label(position) // probably here I should remember this position (pos in insn -> pos in code)

    fun toArray(): ByteArray {
        val buffer = ByteBuffer.allocate(65536)
        for (instruction in instructions) {
            instruction.serialize(buffer)
        }
        val length = buffer.position()
        return buffer.array().sliceArray(0..length + 1)
    }
}

/*  ----------  */
// Types of CP entries

const val CLASS_REF_ENTRY_TYPE: Byte = 0
const val METHOD_REF_ENTRY_TYPE: Byte = 1
const val STRING_ENTRY_TYPE: Byte = 2
const val INT_ENTRY_TYPE: Byte = 3


// Consts for types

const val CLASS_TYPE: Byte = 0
const val I32_TYPE: Byte = 0
const val I8_TYPE: Byte = 0
const val BOOL_TYPE: Byte = 0

/**
 * Types of entries in CP:
 * Class reference
 * Method reference (with link to class reference)
 * String literals
 * Integer literals
 *
 */
class ConstantPool { // High level pool, not using indices
    val classRefs = mutableSetOf<String>()
    val methodNameToRef = mutableMapOf<String, MethodBaseInfo>()
    val strings = mutableSetOf<String>()
    val ints = mutableSetOf<Int>() // TODO primitive set
    var position = 0


    fun addClassReference(className: String): Int {
        classRefs.add(className)
        position++
        return position - 1
    }

    fun addMethodReference(
            methodName: String,
            className: String,
            parameters: List<ParameterInfo>,
            returnType: LangType
    ): Int {
        classRefs.add(className)
        addClassTypeIfMissing(returnType)
        for (parameter in parameters) {
            val type = parameter.type
            addClassTypeIfMissing(type)
        }
        methodNameToRef[methodName] = MethodBaseInfo(methodName, parameters, className)
        position++
        return position - 1
    }

    private fun addClassTypeIfMissing(returnType: LangType): Int {
        if (returnType is ClassType) {
//            classRefs.add(returnType.classSignature.qualifiedName) // TODO
        }
        position++
        return position - 1
    }


    fun addString(str: String): Int {
        strings.add(str)
        position++
        return position - 1
    }

    fun addInt(value: Int): Int {
        ints.add(value)
        position++
        return position - 1
    }

    private fun computeClassRefsByteCount(): Int {
        return classRefs.sumBy { it.length + 2 + 1 } // two bytes for length, one byte for type
    }

    private fun computeStringsByteCount(): Int {
        return strings.sumBy { it.length + 2 + 1 } // two bytes for length, one byte for type
    }

    private fun computeIntsByteCount(): Int {
        return ints.sumBy { it + 1 } // one byte for type
    }

    private fun computeMethodRefByteCount(): Int {
        var size = 0
        for (methodInfo in methodNameToRef.values) {
            size += 1 // For type
            size += 2 + methodInfo.methodName.length // length and name
            size += 2 // class reference
            size += 1 // parameters count
            for (parameter in methodInfo.parameters) {
                size += 2 + parameter.name.length // length and parameter name
                size += 1 // type of parameter type
                when (parameter.type) {
                    is ClassType -> size += 2 // Index of class reference
                }
            }
        }
        return size
    }

    private fun computeByteCount(): Int {
        return computeClassRefsByteCount() + computeStringsByteCount() +
                computeIntsByteCount() + computeMethodRefByteCount()
    }

    // TODO check endiannes, probably should use DataOutputStream also
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(computeByteCount())
        val classToIndex = mutableMapOf<String, Short>()

        for ((index, classRef) in classRefs.withIndex()) {
            buffer.put(CLASS_REF_ENTRY_TYPE)
            writeString(buffer, classRef)
            classToIndex[classRef] = index.toShort()
        }
        for (string in strings) {
            buffer.put(STRING_ENTRY_TYPE)
            writeString(buffer, string)
        }

        for (int in ints) {
            buffer.put(INT_ENTRY_TYPE)
            buffer.putInt(int)
        }

        for (methodInfo in methodNameToRef.values) {
            buffer.put(METHOD_REF_ENTRY_TYPE)
            writeString(buffer, methodInfo.methodName)
            buffer.putShort(classToIndex[methodInfo.className]!!)
            val parameters = methodInfo.parameters
            buffer.put(parameters.size.toByte())
            for (parameter in parameters) {
                val type = parameter.type
                when (type) {
                    is I32Type -> buffer.put(I32_TYPE)
                    is I8Type -> buffer.put(I8_TYPE)
                    is BoolType -> buffer.put(BOOL_TYPE)
                    is ClassType -> {
                        buffer.put(CLASS_TYPE)
//                        buffer.putShort(classToIndex[type.classSignature.qualifiedName]!!) // TODO!
                    }
                    else -> throw UnsupportedOperationException("Type not supported as method param: ${type.name}")
                }
                writeString(buffer, parameter.name)
            }
        }
        return buffer.array()
    }

    fun writeString(buffer: ByteBuffer, str: String) {
        buffer.putShort(str.length.toShort()) // TODO check earlier, that string fits 64k
        buffer.put(str.toByteArray(US_ASCII()))
    }
}

class MethodBaseInfo(
        val methodName: String,
        val parameters: List<ParameterInfo>,
        val className: String
)

class ParameterInfo(
        val name: String,
        val type: LangType
)

class LocalMethodData(
        val name: String,
        val parameters: List<ParameterInfo>,
        val maxStack: Int,
        val bytecode: ByteArray
) {
    fun serialize(out: DataOutputStream) {
        out.writeShort(name.length)
        for (parameter in parameters) {
            val type = parameter.type
            when (type) {
                is I32Type -> out.writeByte(I32_TYPE.toInt())
                is I8Type -> out.writeByte(I8_TYPE.toInt())
                is BoolType -> out.writeByte(BOOL_TYPE.toInt())
            // TODO
//                is ClassType -> {
//                    out.writeByte(CLASS_TYPE.toInt())
//                    out.writeShort(classToIndex[type.classSignature.qualifiedName]!!)
//                }
                else -> throw UnsupportedOperationException("Type not supported as method param: ${type.name}")
            }
            // TODO write name
//            out.(buffer, parameter.name)
        }
    }
}