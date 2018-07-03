package sirgl.simple.vm.codegen

import kotlin.experimental.and


private const val BUFFER_SIZE = 64 * 1024

class BytecodeBuffer {
    private val buffer = ByteArray(BUFFER_SIZE)
    var position = 0

    fun finish() : ByteArray {
        val bytes = buffer.copyOf(position)
        clear()
        return bytes
    }

    fun emit(instruction: Byte) {
        if (position >= BUFFER_SIZE) {
            throw IllegalStateException("Buffer is full")
        }
        buffer[position] = instruction
        position++
    }

    fun emit(instruction: Byte, inlineOperand: Short) {
        if (position + 2 >= BUFFER_SIZE) {
            throw IllegalStateException("Buffer is full")
        }
        buffer[position] = instruction
        buffer[position + 1] = (inlineOperand and 0xff).toByte()
        buffer[position + 2] = (inlineOperand.toInt() shr 8 and 0xff).toByte()
        position += 3
    }

    fun clear() {
        position = 0
    }
}