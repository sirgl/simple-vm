package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.driver.GlobalScope
import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.type.ClassType

class ClassSymbolImpl(
        override val name: String,
        override val symbolSource: SymbolSource,
        override val members: Map<String, MemberSymbol>,
        override val packageSymbol: PackageSymbol,
        override val imports: List<PackageSymbol>,
        val membersMultidefs: MutableMap<String, MutableSet<Symbol>>
) : ClassSymbol {
    override val type: ClassType by lazy {
        val classType = ClassType(simpleName)
        classType.classSymbol = this
        classType
    }
    override val simpleName: String
        get() = name

    override val qualifiedName: String by lazy {
        val packageName = packageSymbol.name
        if (packageName == ".") name else "$packageName.$name"
    }

    override var parentClassSymbol: ClassSymbol? = null

    override fun resolve(name: String, referenceOffset: Int?) = members[name]
            ?: parentClassSymbol?.resolve(name, referenceOffset)
            ?: packageSymbol.resolve(name, referenceOffset)
            ?: resolveByImports(name, referenceOffset)

    private fun resolveByImports(name: String, referenceOffset: Int?) : Symbol? {
        for (import in imports) {
            val symbol = import.resolve(name, referenceOffset)
            if (symbol != null) return symbol
        }
        return null
    }

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

fun LangClass.toSymbol(globalScope: GlobalScope): ClassSymbol {
    val file = this.parent
    val packageSymbol = file.packageDeclaration?.toSymbol(globalScope) ?: globalScope.root
    val importSymbols = file.imports.map { it.toSymbol(globalScope) }.toMutableList()
    importSymbols.add(globalScope.findOrCreatePackageSymbol("lang"))

    val memberSymbols = mutableMapOf<String, MemberSymbol>()
    val membersMultidefs = mutableMapOf<String, MutableSet<Symbol>>()
    for (method in methods) {
        val methodSymbol = method.toSymbol()
        val methodName = method.name
        addHandlingProbableDuplication(memberSymbols, methodName, methodSymbol, membersMultidefs)
    }
    for (constructor: LangConstructor in constructors) {
        val methodSymbol = constructor.toSymbol()
        val methodName = constructorName
        addHandlingProbableDuplication(memberSymbols, methodName, methodSymbol, membersMultidefs)
    }
    for (field in fields) {
        val fieldSymbol = field.toSymbol()
        val fieldName = field.name
        addHandlingProbableDuplication(memberSymbols, fieldName, fieldSymbol, membersMultidefs)
    }
    val classSymbol = ClassSymbolImpl(simpleName, file.symbolSource, memberSymbols, packageSymbol, importSymbols, membersMultidefs)
    for (method in methods) {
        (memberSymbols[method.name] as MethodSymbolImpl).enclosingClass = classSymbol
    }
    for (field in fields) {
        (memberSymbols[field.name] as FieldSymbolImpl).enclosingClass = classSymbol
    }
    for (constructor in constructors) {
        val methodSymbolImpl = memberSymbols[constructorName] as MethodSymbolImpl
        (methodSymbolImpl.returnType as ClassType).classSymbol = classSymbol
        methodSymbolImpl.enclosingClass = classSymbol

    }
    return classSymbol
}

private fun addHandlingProbableDuplication(
        memberSymbols: MutableMap<String, MemberSymbol>,
        methodName: String,
        methodSymbol: MemberSymbol,
        membersMultidefs: MutableMap<String, MutableSet<Symbol>>
) {
    val previous = memberSymbols.put(methodName, methodSymbol)
    if (previous != null) {
        val duplicatingSymbols = membersMultidefs[methodName] ?: mutableSetOf()
        duplicatingSymbols.add(previous)
        duplicatingSymbols.add(methodSymbol)
        membersMultidefs[methodName] = duplicatingSymbols
    }
}