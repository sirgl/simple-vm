package sirgl.simple.vm.scope

interface Scope {
    val parentScope: Scope
    fun register(symbol: Symbol)
    fun resolve(name: String): Sequence<Symbol>
    fun getErrors()
}