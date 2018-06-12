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
    override val imports: List<PackageSymbol>,
    val membersMultidefs: MutableMap<String, MutableSet<Symbol>>
) : ClassSymbol {
    override val simpleName: String
        get() = name

    override val qualifiedName: String by lazy {
        val packageName = packageSymbol.name
        packageName
    }

    override var parentClassSymbol: ClassSymbol? = null

    override fun resolve(name: String, referenceOffset: Int?) = members[name]
            ?: parentClassSymbol?.resolve(name, referenceOffset)
            ?: packageSymbol.resolve(name, referenceOffset)

    override fun register(symbol: Symbol, node: AstNode?) =
        throw UnsupportedOperationException("It should be done at the beginning")

    override fun getMultipleDeclarations() = membersMultidefs

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
    val membersMultidefs = mutableMapOf<String, MutableSet<Symbol>>()
    for (method in methods) {
        val methodSymbol = method.toSymbol()
        val methodName = method.name
        val previous = memberSymbols.put(methodName, methodSymbol)
        if (previous != null) {
            val duplicatingSymbols = membersMultidefs[methodName] ?: mutableSetOf()
            duplicatingSymbols.add(previous)
            duplicatingSymbols.add(methodSymbol)
            membersMultidefs[methodName] = duplicatingSymbols
        }
    }
    for (field in fields) {
        val fieldSymbol = field.toSymbol()
        val fieldName = field.name
        val previous = memberSymbols.put(fieldName, fieldSymbol)
        if (previous != null) {
            val duplicatingSymbols = membersMultidefs[fieldName] ?: mutableSetOf()
            duplicatingSymbols.add(previous)
            duplicatingSymbols.add(fieldSymbol)
            membersMultidefs[fieldName] = duplicatingSymbols
        }
    }
    val classSymbol = ClassSymbolImpl(simpleName, file.symbolSource, memberSymbols, packageSymbol, importSymbols, membersMultidefs)
    for (method in methods) {
        (memberSymbols[method.name] as MethodSymbolImpl).enclosingClass = classSymbol
    }
    for (field in fields) {
        (memberSymbols[field.name] as FieldSymbolImpl).enclosingClass = classSymbol
    }
    return classSymbol
}