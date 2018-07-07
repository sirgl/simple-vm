package sirgl.simple.vm.codegen.assembler

import sirgl.simple.vm.codegen.BytecodeBuffer
import sirgl.simple.vm.resolve.symbols.LocalVarSymbol
import sirgl.simple.vm.resolve.symbols.ParameterSymbol
import sirgl.simple.vm.resolve.symbols.VarSymbol

/**
 * Warning! Not thread safe, using static buffer
 */
class MethodWriter(val classWriter: ClassWriter, isInstanceMethod: Boolean) {
    private val instructions = mutableListOf<Instruction>()
    private var position = 0
    val breakGotos = mutableListOf<GotoInstruction>()
    val continueGotos = mutableListOf<GotoInstruction>()
    private var currentSlot: Short = if (isInstanceMethod) 1 else 0
    private val variables = mutableMapOf<VarSymbol, Short>()

    fun addParameter(parameterSymbol: ParameterSymbol) {
        variables.computeIfAbsent(parameterSymbol) {
            val newSlot = currentSlot
            currentSlot++
            newSlot
        }
    }

    fun getVariableSlot(varSymbol: VarSymbol)  = variables[varSymbol]!!

    fun addLocalVariable(localVarSymbol: LocalVarSymbol) : Short {
        return variables.computeIfAbsent(localVarSymbol) {
            val newSlot = currentSlot
            currentSlot++
            newSlot
        }
    }

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