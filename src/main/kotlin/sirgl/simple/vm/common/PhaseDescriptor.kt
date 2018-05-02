package sirgl.simple.vm.common

open class PhaseDescriptor<T>(val name: String) {
    fun get() : T {
        TODO()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhaseDescriptor<*>) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}