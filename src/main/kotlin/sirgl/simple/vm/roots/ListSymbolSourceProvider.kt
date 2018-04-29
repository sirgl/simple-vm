package sirgl.simple.vm.roots

class ListSymbolSourceProvider(val sources: List<FileSymbolSource>) : SymbolSourceProvider {
    override fun findSources() = sources.asSequence()
}