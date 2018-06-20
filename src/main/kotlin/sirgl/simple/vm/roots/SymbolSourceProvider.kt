package sirgl.simple.vm.roots

interface SymbolSourceProvider {
    fun findSources(): Sequence<FileSymbolSource>
}