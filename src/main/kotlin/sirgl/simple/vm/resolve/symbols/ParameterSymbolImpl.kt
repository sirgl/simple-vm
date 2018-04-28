package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.type.LangType

class ParameterSymbolImpl(
    override val name: String,
    override val symbolSource: SymbolSource,
    override val type: LangType
) : ParameterSymbol