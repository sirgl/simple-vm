package sirgl.simple.vm.resolve

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.ext.findParentOfClass
import sirgl.simple.vm.resolve.symbols.LocalSymbol
import sirgl.simple.vm.resolve.symbols.Symbol

class LocalScope(var element: AstNode) : Scope {

    private var parentScope: Scope? = null
    private val localSymbols = mutableMapOf<String, Symbol>()
    private val multipleDeclaredNames by lazy { mutableMapOf<String, MutableSet<Symbol>>() }
    private var wasMultipleDefinitions = false

    override fun resolve(name: String, referenceOffset: Int?): Symbol? {
        val symbol = localSymbols[name]
        return if (symbol == null || (referenceOffset != null && symbol is LocalSymbol && symbol.offset - 1 > referenceOffset)) {
            if (parentScope == null) {
                parentScope = findParentScope()
            }
            parentScope?.resolve(name, referenceOffset)
        } else {
            symbol
        }
    }

    override fun register(symbol: Symbol, node: AstNode?) {
        val name = symbol.name
        val previousValue = localSymbols.put(name, symbol)
        if (previousValue != null) {
            wasMultipleDefinitions = true
            val duplicatingList = multipleDeclaredNames[name] ?: mutableSetOf()
            duplicatingList.add(symbol)
            duplicatingList.add(previousValue)
        }
    }

    override fun getMultipleDeclarations(): Map<String, Set<Symbol>> = if (wasMultipleDefinitions) {
        multipleDeclaredNames
    } else {
        emptyMap()
    }

    private fun findParentScope() = element.findParentOfClass<Scoped>()?.scope
}