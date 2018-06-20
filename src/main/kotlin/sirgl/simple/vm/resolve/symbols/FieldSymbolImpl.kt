package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.LangField
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.type.LangType

class FieldSymbolImpl(
        override val name: String,
        override val symbolSource: SymbolSource,
        override val type: LangType
) : FieldSymbol {
    override lateinit var enclosingClass: ClassSymbol
}

fun LangField.toSymbol(): FieldSymbol = FieldSymbolImpl(name, getSymbolSource(), type)