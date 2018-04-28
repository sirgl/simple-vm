package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.roots.SymbolSource

class ClassSymbolImpl(
    override val name: String,
    override val symbolSource: SymbolSource,
    override val members: Map<String, MemberSymbol>,
    override val packageSymbol: PackageSymbol
) : ClassSymbol {
    override val simpleName: String
        get() = name
    override var parentClassSymbol: ClassSymbol? = null

    override fun resolve(name: String) = members[name]
            ?: parentClassSymbol?.resolve(name)
            ?: packageSymbol.resolve(name)

    override fun register(symbol: Symbol, node: AstNode?) =
        throw UnsupportedOperationException("It should be done at the beginning")

    override fun getMultipleDeclarations() =
        throw UnsupportedOperationException("It should be done at the beginning")
}