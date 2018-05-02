package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.Scoped
import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.type.LangType

interface Symbol {
    val name: String
    val symbolSource: SymbolSource
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

interface ParameterSymbol : VarSymbol

interface LocalVarSymbol : VarSymbol

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
}

interface PackageSymbol : ScopedSymbol

