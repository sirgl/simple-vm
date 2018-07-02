package sirgl.simple.vm.codegen

private const val DEFAULT_SIZE = 512

class ByteList(startingSize: Int = DEFAULT_SIZE) {
    private var buffer = ByteArray(startingSize)
    private var size = 0

    private fun realloc() {
        buffer = buffer.copyOf(buffer.size * 2)
    }

    fun addBytes(bytes: ByteArray) {
        if (size + bytes.size > buffer.size) {
            realloc()
        }
        System.arraycopy(bytes, 0, buffer, size, bytes.size)
        size += bytes.size
    }
}