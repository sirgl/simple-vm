package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.roots.SymbolSource

class PackageSymbolImpl(
    override val name: String,
    override val symbolSource: SymbolSource
) : PackageSymbol {
    val symbols = mutableMapOf<String, Symbol>()
    val duplicatingDeclaration by lazy { mutableMapOf<String, MutableSet<Symbol>>() }
    var wasMultipleDefinitions = false

    override fun resolve(name: String) = symbols[name]

    override fun register(symbol: Symbol, node: AstNode?) {
        val name = symbol.name
        val previousValue = symbols.put(name, symbol)
        if (previousValue != null) {
            wasMultipleDefinitions = true
            val duplicatingList = duplicatingDeclaration[name] ?: mutableSetOf()
            duplicatingList.add(symbol)
            duplicatingList.add(previousValue)
        }
    }

    override fun getMultipleDeclarations(): Map<String, Set<Symbol>> = if (wasMultipleDefinitions) {
        duplicatingDeclaration
    } else {
        emptyMap()
    }
}