package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.roots.SymbolSource
import sirgl.simple.vm.roots.SymbolSourceType
import sirgl.simple.vm.type.I32Type
import sirgl.simple.vm.type.LangType
import java.nio.file.Path

object LengthSymbolImpl : LengthSymbol {
    override val type: LangType
        get() = I32Type

    override val name: String
        get() = "length"

    override val symbolSource: SymbolSource = object : SymbolSource {
        override val path: Path? = null
        override val type: SymbolSourceType = SymbolSourceType.Compiled
    }
}