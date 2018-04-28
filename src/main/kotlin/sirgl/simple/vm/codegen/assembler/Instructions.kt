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

const val OP_ILOAD_CONST = 18
const val OP_CLOAD_CONST = 19
const val OP_SLOAD_CONST = 20
const val OP_LOAD_TRUE = 21
const val OP_LOAD_FALSE = 22
const val OP_LOAD_ONE = 23
const val OP_LOAD_ZERO = 24
const val OP_LOAD_NULL = 25

const val OP_DROP = 26 // for discarding method result in ExprStmt

const val OP_I2C = 27
const val OP_C2I = 28

// Store
const val OP_ISTORE = 29
const val OP_CSTORE = 30
const val OP_BSTORE = 31
const val OP_RSTORE = 32

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

class BinopInstruction(binaryOperatorType: BinaryOperatorType) :
    SingleByteInstruction(binopToOpcode(binaryOperatorType))


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
    var label: Label?,
    code: Int
) : InlinedOperandInstruction(code, label?.position?.toShort() ?: 0)

class GotoInstruction(label: Label?) : ControlInstruction(label, OP_GOTO)
class IfTrueInstruction(label: Label?) : ControlInstruction(label, OP_IF_TRUE)
class IfFalseInstruction(label: Label?) : ControlInstruction(label, OP_IF_FALSE)
class IfNullInstruction(label: Label?) : ControlInstruction(label, OP_IF_NULL)
class IfNotNullInstruction(label: Label?) : ControlInstruction(label, OP_IF_NOT_NULL)

class IloadConstInstruction(index: Short) : InlinedOperandInstruction(OP_ILOAD_CONST, index)
class LoadTrueInstruction : SingleByteInstruction(OP_LOAD_TRUE)
class LoadFalseInstruction : SingleByteInstruction(OP_LOAD_FALSE)
class LoadNullInstruction : SingleByteInstruction(OP_LOAD_NULL)

class ConvertCharToIntInstruction : SingleByteInstruction(OP_C2I)
class ConvertIntToCharInstruction : SingleByteInstruction(OP_I2C)

class StoreIntInstruction(slot: Short) : InlinedOperandInstruction(OP_ISTORE, slot)
class StoreCharInstruction(slot: Short) : InlinedOperandInstruction(OP_CSTORE, slot)
class StoreBoolInstruction(slot: Short) : InlinedOperandInstruction(OP_BSTORE, slot)
class StoreReferenceInstruction(slot: Short) : InlinedOperandInstruction(OP_RSTORE, slot)