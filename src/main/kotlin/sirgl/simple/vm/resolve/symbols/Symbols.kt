package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.constructorName
import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.Scoped
import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.type.ClassType
import sirgl.simple.vm.type.LangType

interface Symbol {
    val name: String
    val symbolSource: SymbolSource
}

interface LocalSymbol {
    val offset: Int
}

interface VarSymbol : Symbol {
    val type: LangType
}

interface MemberSymbol : Symbol {
    val enclosingClass: ClassSymbol
}

interface ScopedSymbol : Scope, Symbol, Scoped {
    override val scope: Scope
        get() = this
}

interface FieldSymbol : VarSymbol, MemberSymbol

interface LengthSymbol : VarSymbol

interface ParameterSymbol : VarSymbol, LocalSymbol

interface LocalVarSymbol : VarSymbol, LocalSymbol

interface MethodSymbol : Symbol, MemberSymbol {
    val returnType: LangType
    val parameters: List<ParameterSymbol>
}

interface ClassSymbol : ScopedSymbol {
    val imports: List<PackageSymbol>
    val members: Map<String, MemberSymbol>
    val simpleName: String
    val packageSymbol: PackageSymbol // Do I really need this?
    // Null only for Object
    val parentClassSymbol: ClassSymbol?
    val qualifiedName: String

    val type: ClassType
}

val ClassSymbol.constructor
    get() = members[constructorName] as? MethodSymbol

interface PackageSymbol : ScopedSymbol

fun ClassSymbol.findParentSymbol(predicate: (ClassSymbol) -> Boolean): ClassSymbol? {
    var current: ClassSymbol? = this.parentClassSymbol
    while (current != null) {
        if (predicate(current)) return current
        current = current.parentClassSymbol
    }
    return null
}

