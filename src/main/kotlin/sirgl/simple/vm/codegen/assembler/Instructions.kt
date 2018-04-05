package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.ast.BinaryOperatorType
import sirgl.simple.vm.ast.expr.PrefixOperatorType
import java.nio.ByteBuffer

abstract class Instruction {
    abstract fun serialize(buffer: ByteBuffer)
    abstract val size: Int
}

const val OP_ADD = 0
const val OP_MUL = 1
const val OP_SUB = 2
const val OP_DIV = 3
const val OP_REM = 4

const val OP_ILT = 5
const val OP_ILE = 6
const val OP_IGT = 7
const val OP_IGE = 8
const val OP_IEQ = 9



const val OP_NOOP = 10

const val OP_NEG = 11 // -x
const val OP_INV = 12 // !x


const val OP_GOTO = 13
const val OP_IF_TRUE = 14
const val OP_IF_FALSE = 15
const val OP_IF_NULL = 16
const val OP_IF_NOT_NULL = 17

open class SingleByteInstruction(val opcode: Int) : Instruction() {
    override fun serialize(buffer: ByteBuffer) {
        buffer.put(opcode.toByte())
    }
    override val size: Int = 1
}

open class InlinedOperandInstruction(val opcode: Int, val inlineOp: Short) : Instruction() {
    override fun serialize(buffer: ByteBuffer) {
        buffer.put(opcode.toByte())
        buffer.putShort(inlineOp)
    }

    override val size: Int = 3

}

private fun binopToOpcode(operatorType: BinaryOperatorType) = when (operatorType) {
    BinaryOperatorType.Plus -> OP_ADD
    BinaryOperatorType.Minus -> OP_SUB
    BinaryOperatorType.Asterisk -> OP_MUL
    BinaryOperatorType.Div -> OP_DIV
    BinaryOperatorType.Percent -> OP_REM
    BinaryOperatorType.Lt -> OP_ILT
    BinaryOperatorType.Le -> OP_ILE
    BinaryOperatorType.Gt -> OP_IGT
    BinaryOperatorType.Ge -> OP_IGE
    BinaryOperatorType.Eq -> OP_IEQ
}

class BinopInstruction(binaryOperatorType: BinaryOperatorType) : SingleByteInstruction(binopToOpcode(binaryOperatorType))


private fun unopToOpcode(prefixOperatorType: PrefixOperatorType) = when (prefixOperatorType) {
    PrefixOperatorType.Plus -> OP_NOOP
    PrefixOperatorType.Minus -> OP_NEG
    PrefixOperatorType.Inversion -> OP_INV
}

class UnaryInstruction(
        prefixOperatorType: PrefixOperatorType
) : SingleByteInstruction(unopToOpcode(prefixOperatorType))

class Label(val position: Int)

abstract class ControlInstruction(
        val label: Label,
        val code: Int
) : InlinedOperandInstruction(code, label.position.toShort())

class GotoInstruction(label: Label) : ControlInstruction(label, OP_GOTO)
class IfTrueInstruction(label: Label) : ControlInstruction(label, OP_IF_TRUE)
class IfFalseInstruction(label: Label) : ControlInstruction(label, OP_IF_FALSE)
class IfNullInstruction(label: Label) : ControlInstruction(label, OP_IF_NULL)
class IfNotNullInstruction(label: Label) : ControlInstruction(label, OP_IF_NOT_NULL)
