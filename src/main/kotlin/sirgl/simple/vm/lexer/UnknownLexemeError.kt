package sirgl.simple.vm.lexer

import sirgl.simple.vm.driver.CompilationError
import sirgl.simple.vm.roots.FileSymbolSource


class UnknownLexemeError(
    val lexeme: Lexeme,
    override val symbolSource: FileSymbolSource
) : CompilationError {
    override val text = "Unknown lexeme: $lexeme"
}