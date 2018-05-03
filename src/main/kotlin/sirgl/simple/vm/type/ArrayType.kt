package sirgl.simple.vm.type

class ArrayType(val elementType: LangType) : LangType {
    override val name: String = "$elementType[]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ArrayType) return false

        if (elementType != other.elementType) return false

        return true
    }

    override fun hashCode(): Int {
        return elementType.hashCode()
    }


}