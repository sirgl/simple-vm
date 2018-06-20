package sirgl.simple.vm.type

import sirgl.simple.vm.resolve.symbols.MethodSymbol

class MethodReferenceType(
        override val name: String
) : LangType {
    lateinit var methodSymbol: MethodSymbol
}