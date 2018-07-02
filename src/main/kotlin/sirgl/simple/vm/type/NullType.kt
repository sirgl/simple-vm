package sirgl.simple.vm.type

object NullType : LangType {
    override val signature: String
        get() = throw UnsupportedOperationException()
    override val name = "null"
}