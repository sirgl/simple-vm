package sirgl.simple.vm.parser

import sirgl.simple.vm.driver.CompilationError
import sirgl.simple.vm.roots.SymbolSource

class ParseError(
    override val text: String,
    override val symbolSource: SymbolSource?
) : CompilationError