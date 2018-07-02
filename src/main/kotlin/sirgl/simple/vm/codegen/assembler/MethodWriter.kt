package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.codegen.BytecodeBuffer

/**
 * Warning! Not thread safe, using static buffer
 */
class MethodWriter(val classWriter: ClassWriter) {
    fun emit(insn: Instruction) {
        when (insn) {
            is SingleByteInstruction -> buffer.emit(insn.opByte)
            is InlinedOperandInstruction -> buffer.emit(insn.opByte, insn.inlineOp)
            else -> throw UnsupportedOperationException("Instruction not supported: $insn")
        }
    }

    fun labelCurrent() = Label(buffer.position)

    // Always available as last instruction is NOOP always
    fun labelNext() = Label(buffer.position + 1)

    fun getBytecode() : ByteArray = buffer.finish()

    companion object {
        private val buffer = BytecodeBuffer()
    }
}