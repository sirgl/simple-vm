package sirgl.simple.vm.resolve

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.resolve.symbols.Symbol

interface Scope {
    fun resolve(name: String): Symbol?

    fun register(symbol: Symbol, node: AstNode?)

    fun getMultipleDeclarations(): Map<String, Set<Symbol>>
}