package sirgl.simple.vm.scope

class ScopeImpl : Scope {
    override fun getErrors() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override lateinit var parentScope: Scope

    override fun register(symbol: Symbol) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resolve(name: String): Sequence<Symbol> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}