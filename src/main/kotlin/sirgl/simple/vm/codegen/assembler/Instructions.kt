package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.ast.BinaryOperatorType
import sirgl.simple.vm.ast.expr.PrefixOperatorType
import java.nio.ByteBuffer

abstract class Instruction {
    abstract fun serialize(buffer: ByteBuffer)
    abstract val size: Int
}

enum class Opcode(val hasInlineOperand: Boolean) {
    // integer arithmetic
    ADD(false),
    MUL(false),
    SUB(false),
    DIV(false),
    REM(false),

    // integer comparison
    ILT(false),
    ILE(false),
    IGT(false),
    IGE(false),
    IEQ(false),

    // noop
    NOOP(false),

    // integer unary
    NEG(false),
    INV(false),

    // control flow
    GOTO(true),
    IF_TRUE(true),
    IF_FALSE(true),
    IF_NULL(true),
    IF_NOT_NULL(true),

    // constant loading
    ILOAD_CONST(true), // int
    CLOAD_CONST(true), // char
    SLOAD_CONST(true), // string
    LOAD_TRUE(false),
    LOAD_FALSE(false),
    LOAD_ONE(false),
    LOAD_ZERO(false),
    LOAD_NULL(false),

    // discarding method result in ExprStmt
    POP(false),

    // conversion
    I2C(false),
    C2I(false),

    // store
    ISTORE(true), // int
    CSTORE(true), // char
    BSTORE(true), // boolean
    RSTORE(true), // reference

    // typecheck
    TYPECHECK(true)

    // TODO calls
    // TODO Array operations
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

open class InlinedOperandInstruction(val opcode: Opcode, val inlineOp: Short) : Instruction() {
    override fun serialize(buffer: ByteBuffer) {
        buffer.put(opcode.ordinal.toByte())
        buffer.putShort(inlineOp)
    }

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
) : InlinedOperandInstruction(code, label?.position?.toShort() ?: 0)

class GotoInstruction(label: Label?) : ControlInstruction(label, Opcode.GOTO)
class IfTrueInstruction(label: Label?) : ControlInstruction(label, Opcode.IF_TRUE)
class IfFalseInstruction(label: Label?) : ControlInstruction(label, Opcode.IF_FALSE)
class IfNullInstruction(label: Label?) : ControlInstruction(label, Opcode.IF_NULL)
class IfNotNullInstruction(label: Label?) : ControlInstruction(label, Opcode.IF_NOT_NULL)

class IloadConstInstruction(index: Short) : InlinedOperandInstruction(Opcode.ILOAD_CONST, index)
class LoadTrueInstruction : SingleByteInstruction(Opcode.LOAD_TRUE)
class LoadFalseInstruction : SingleByteInstruction(Opcode.LOAD_FALSE)
class LoadNullInstruction : SingleByteInstruction(Opcode.LOAD_NULL)

class ConvertCharToIntInstruction : SingleByteInstruction(Opcode.C2I)
class ConvertIntToCharInstruction : SingleByteInstruction(Opcode.I2C)

class StoreIntInstruction(slot: Short) : InlinedOperandInstruction(Opcode.ISTORE, slot)
class StoreCharInstruction(slot: Short) : InlinedOperandInstruction(Opcode.CSTORE, slot)
class StoreBoolInstruction(slot: Short) : InlinedOperandInstruction(Opcode.BSTORE, slot)
class StoreReferenceInstruction(slot: Short) : InlinedOperandInstruction(Opcode.RSTORE, slot)

class PopInstruction() : SingleByteInstruction(Opcode.POP)

class TypecheckInstruction(cpEntry: Short) : InlinedOperandInstruction(Opcode.TYPECHECK, cpEntry)