package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.ast.BinaryOperatorType
import sirgl.simple.vm.ast.expr.PrefixOperatorType
import java.nio.ByteBuffer

abstract class Instruction {
    abstract fun serialize(buffer: ByteBuffer)
    abstract val size: Int
}

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

class StoreIntInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.ISTORE_SLOT) {
    override val inlineOp: Short
        get() = slot
}

class StoreBoolInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.BSTORE_SLOT) {
    override val inlineOp: Short
        get() = slot
}

class StoreReferenceInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.RSTORE_SLOT) {
    override val inlineOp: Short
        get() = slot
}

class LoadIntInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.ILOAD_SLOT) {
    override val inlineOp: Short
        get() = slot
}

class LoadBoolInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.BLOAD_SLOT) {
    override val inlineOp: Short
        get() = slot
}

class LoadReferenceInstruction(val slot: Short) : InlinedOperandInstruction(Opcode.RLOAD_SLOT) {
    override val inlineOp: Short
        get() = slot
}

class StoreFieldIntInstruction(val descriptor: CPDescriptor) : InlinedOperandInstruction(Opcode.ISTORE_FIELD) {
    override val inlineOp: CPDescriptor
        get() = descriptor
}

class StoreFieldBoolInstruction(val descriptor: CPDescriptor) : InlinedOperandInstruction(Opcode.BSTORE_FIELD) {
    override val inlineOp: CPDescriptor
        get() = descriptor
}

class StoreFieldReferenceInstruction(val descriptor: CPDescriptor) : InlinedOperandInstruction(Opcode.RSTORE_FIELD) {
    override val inlineOp: CPDescriptor
        get() = descriptor
}

class LoadFieldIntInstruction(val descriptor: CPDescriptor) : InlinedOperandInstruction(Opcode.ILOAD_FIELD) {
    override val inlineOp: CPDescriptor
        get() = descriptor
}

class LoadFieldBoolInstruction(val descriptor: CPDescriptor) : InlinedOperandInstruction(Opcode.BLOAD_FIELD) {
    override val inlineOp: CPDescriptor
        get() = descriptor
}

class LoadFieldReferenceInstruction(val descriptor: CPDescriptor) : InlinedOperandInstruction(Opcode.RLOAD_FIELD) {
    override val inlineOp: CPDescriptor
        get() = descriptor
}

class PopInstruction : SingleByteInstruction(Opcode.POP)

class TypecheckInstruction(val cpEntry: Short) : InlinedOperandInstruction(Opcode.TYPECHECK) {
    override val inlineOp: Short
        get() = cpEntry
}

class CallVirtualInstruction(val cpEntry: Short) : InlinedOperandInstruction(Opcode.CALL_VIRTUAL) {
    override val inlineOp: Short
        get() = cpEntry
}

class ReturnInstruction : SingleByteInstruction(Opcode.RETURN)
class NoopInstruction : SingleByteInstruction(Opcode.NOOP)