package sirgl.simple.vm.resolve.symbols

import sirgl.simple.vm.roots.InternalSymbolSource
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

    override val symbolSource: SymbolSource = InternalSymbolSource
}