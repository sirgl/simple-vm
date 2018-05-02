package sirgl.simple.vm.driver

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.symbols.*
import sirgl.simple.vm.roots.InternalSymbolSource

/**
 * Global scope, not thread safe
 */
class GlobalScope : Scope {
    private val symbols = mutableMapOf<String, Symbol>()
    private val multipleDeclaredNames by lazy { mutableMapOf<String, MutableSet<Symbol>>() }
    private var wasMultipleDefinitions = false

    val root = PackageSymbolImpl(".", InternalSymbolSource)

    fun addSourceFile(file: LangFile) {
        val packageDecl = file.packageDeclaration
        val classDecl = file.classDecl
        val classSymbol = classDecl.symbol
        if (packageDecl == null) {
            root.register(classSymbol, classDecl)
        } else {
            val packageSymbol = findOrCreatePackageSymbol(packageDecl.referenceElement.fullName)
            packageSymbol.register(classSymbol, classDecl)
        }
    }

    fun findOrCreatePackageSymbol(fullPackageName: String): PackageSymbol {
        val packageElements = fullPackageName.split(".")
        var currentNode: PackageSymbol = root
        for (packageElement in packageElements) {
            val childPackage = currentNode.resolve(packageElement) as? PackageSymbol
            currentNode = if (childPackage == null) {
                val packageSymbol = PackageSymbolImpl(packageElement, InternalSymbolSource)
                currentNode.register(packageSymbol, null)
                packageSymbol
            } else {
                childPackage
            }
        }
        return currentNode
    }

    override fun resolve(name: String) = symbols[name]

    override fun register(symbol: Symbol, node: AstNode?) {
        val name = symbol.name
        val previousValue = symbols.put(name, symbol)
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
}