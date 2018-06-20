package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.ext.getSymbolSource
import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.type.LangType

class ParameterSymbolImpl(
        override val name: String,
        override val symbolSource: SymbolSource,
        override val type: LangType,
        parameter: LangParameter
) : ParameterSymbol {
    override val offset: Int = parameter.endOffset
}

fun LangParameter.toSymbol(): ParameterSymbol =
        ParameterSymbolImpl(name, getSymbolSource(), type, this)