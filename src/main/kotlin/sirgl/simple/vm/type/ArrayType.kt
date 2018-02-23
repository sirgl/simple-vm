package sirgl.simple.vm.type

class ArrayType(val type: LangType) : LangType {
    override val name: String = "$type[]"
}