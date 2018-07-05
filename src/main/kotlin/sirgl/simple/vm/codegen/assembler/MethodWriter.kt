package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.codegen.BytecodeBuffer

/**
 * Warning! Not thread safe, using static buffer
 */
class MethodWriter(val classWriter: ClassWriter) {
    private val instructions = mutableListOf<Instruction>()
    private var position = 0
    val breakGotos = mutableListOf<GotoInstruction>()
    val continueGotos = mutableListOf<GotoInstruction>()


    fun emit(insn: Instruction) {
        instructions.add(insn)
        position += when (insn) {
            is SingleByteInstruction -> 1
            is InlinedOperandInstruction -> 3
            else -> throw UnsupportedOperationException("Instruction not supported: $insn")
        }
    }

    fun labelCurrent() = Label(position)

    // Always available as last instruction is NOOP always
    fun labelNext() = Label(position + 1)

    fun getBytecode(): ByteArray {
        for (insn in instructions) {
            when (insn) {
                is SingleByteInstruction -> buffer.emit(insn.opByte)
                is InlinedOperandInstruction -> buffer.emit(insn.opByte, insn.inlineOp)
                else -> throw UnsupportedOperationException("Instruction not supported: $insn")
            }
        }
        return buffer.finish()
    }

    companion object {
        private val buffer = BytecodeBuffer()
    }
}