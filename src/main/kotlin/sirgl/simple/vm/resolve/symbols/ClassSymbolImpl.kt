package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.driver.GlobalScope
import sirgl.simple.vm.roots.SymbolSource

class ClassSymbolImpl(
    override val name: String,
    override val symbolSource: SymbolSource,
    override val members: Map<String, MemberSymbol>,
    override val packageSymbol: PackageSymbol,
    override val imports: List<PackageSymbol>
) : ClassSymbol {
    override val simpleName: String
        get() = name

    override val qualifiedName: String by lazy {
        val packageName = packageSymbol.name
        packageName
    }

    override var parentClassSymbol: ClassSymbol? = null

    override fun resolve(name: String) = members[name]
            ?: parentClassSymbol?.resolve(name)
            ?: packageSymbol.resolve(name)

    override fun register(symbol: Symbol, node: AstNode?) =
        throw UnsupportedOperationException("It should be done at the beginning")

    override fun getMultipleDeclarations() =
        throw UnsupportedOperationException("It should be done at the beginning")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassSymbolImpl) return false

        if (name != other.name) return false
        if (packageSymbol != other.packageSymbol) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + packageSymbol.hashCode()
        return result
    }


}

fun LangClass.toSymbol(globalScope: GlobalScope) : ClassSymbol {
    val file = this.parent
    val packageSymbol = file.packageDeclaration?.toSymbol(globalScope) ?: globalScope.root
    val importSymbols = file.imports.map { it.toSymbol(globalScope) }

    val memberSymbols = mutableMapOf<String, MemberSymbol>()
    for (method in methods) {
        memberSymbols[method.name] = method.toSymbol()
    }
    for (field in fields) {
        memberSymbols[field.name] = field.toSymbol()
    }
    return ClassSymbolImpl(simpleName, file.symbolSource, memberSymbols, packageSymbol, importSymbols)
}