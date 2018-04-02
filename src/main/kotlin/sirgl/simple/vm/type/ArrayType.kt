package sirgl.simple.vm.type

class ArrayType(val elementType: LangType) : LangType {
    override val name: String = "$elementType[]"
}