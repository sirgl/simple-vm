package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.ast.BinaryOperatorType
import sirgl.simple.vm.ast.expr.PrefixOperatorType
import java.nio.ByteBuffer

abstract class Instruction {
    abstract fun serialize(buffer: ByteBuffer)
    abstract val size: Int
}

val byteToOpcode: Array<Opcode> = Opcode.values()

open class SingleByteInstruction(val opcode: Opcode) : Instruction() {
    override fun serialize(buffer: ByteBuffer) {
        buffer.put(opcode.ordinal.toByte())
    }

    override val size: Int = 1

    val opByte: Byte
        get() = opcode.ordinal.toByte()
}

abstract class InlinedOperandInstruction(val opcode: Opcode) : Instruction() {
    override fun serialize(buffer: ByteBuffer) {
        buffer.put(opcode.ordinal.toByte())
        buffer.putShort(inlineOp)
    }

    abstract val inlineOp: Short

    override val size: Int = 3

    val opByte: Byte
        get() = opcode.ordinal.toByte()
}

private fun binopToOpcode(operatorType: BinaryOperatorType) = when (operatorType) {
    BinaryOperatorType.Plus -> Opcode.ADD
    BinaryOperatorType.Minus -> Opcode.SUB
    BinaryOperatorType.Asterisk -> Opcode.MUL
    BinaryOperatorType.Div -> Opcode.DIV
    BinaryOperatorType.Percent -> Opcode.REM
    BinaryOperatorType.Lt -> Opcode.ILT
    BinaryOperatorType.Le -> Opcode.ILE
    BinaryOperatorType.Gt -> Opcode.IGT
    BinaryOperatorType.Ge -> Opcode.IGE
    BinaryOperatorType.Eq -> Opcode.IEQ
}

class BinopInstruction(binaryOperatorType: BinaryOperatorType) :
        SingleByteInstruction(binopToOpcode(binaryOperatorType))


private fun unopToOpcode(prefixOperatorType: PrefixOperatorType) = when (prefixOperatorType) {
    PrefixOperatorType.Plus -> Opcode.NOOP
    PrefixOperatorType.Minus -> Opcode.NEG
    PrefixOperatorType.Inversion -> Opcode.INV
}

class UnaryInstruction(
        prefixOperatorType: PrefixOperatorType
) : SingleByteInstruction(unopToOpcode(prefixOperatorType))

class Label(val position: Int)


abstract class ControlInstruction(
        var label: Label?,
        code: Opcode
) : InlinedOperandInstruction(code) {
    override val inlineOp: Short
        get() = label?.position?.toShort() ?: 0
}

class GotoInstruction(label: Label?) : ControlInstruction(label, Opcode.GOTO)
class IfTrueInstruction(label: Label?) : ControlInstruction(label, Opcode.IF_TRUE)
class IfFalseInstruction(label: Label?) : ControlInstruction(label, Opcode.IF_FALSE)
class IfNullInstruction(label: Label?) : ControlInstruction(label, Opcode.IF_NULL)
class IfNotNullInstruction(label: Label?) : ControlInstruction(label, Opcode.IF_NOT_NULL)

class IloadConstInstruction(val index: Short) : InlinedOperandInstruction(Opcode.ILOAD_CONST) {
    override val inlineOp: Short
        get() = index
}
class LoadTrueInstruction : SingleByteInstruction(Opcode.LOAD_TRUE)
class LoadFalseInstruction : SingleByteInstruction(Opcode.LOAD_FALSE)
class LoadNullInstruction : SingleByteInstruction(Opcode.LOAD_NULL)

class ConvertCharToIntInstruction : SingleByteInstruction(Opcode.C2I)
class ConvertIntToCharInstruction : SingleByteInstruction(Opcode.I2C)

class StoreIntInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.ISTORE) {
    override val inlineOp: Short
        get() = slot
}

class StoreCharInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.CSTORE) {
    override val inlineOp: Short
        get() = slot
}
class StoreBoolInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.BSTORE) {
    override val inlineOp: Short
        get() = slot
}
class StoreReferenceInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.RSTORE) {
    override val inlineOp: Short
        get() = slot
}

class PopInstruction() : SingleByteInstruction(Opcode.POP)

class TypecheckInstruction(val cpEntry: Short) : InlinedOperandInstruction(Opcode.TYPECHECK) {
    override val inlineOp: Short
        get() = cpEntry
}

class ReturnInstruction : SingleByteInstruction(Opcode.RETURN)
class NoopInstruction : SingleByteInstruction(Opcode.NOOP)