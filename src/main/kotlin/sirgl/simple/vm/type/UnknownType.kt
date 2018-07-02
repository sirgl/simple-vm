package sirgl.simple.vm.type

object UnknownType : LangType {
    override val signature: String
        get() = throw UnsupportedOperationException()
    override val name = "<unknown>"
}