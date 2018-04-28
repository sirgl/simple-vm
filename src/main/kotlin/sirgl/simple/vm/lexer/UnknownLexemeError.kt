package sirgl.simple.vm.lexer

import sirgl.simple.vm.driver.CompilationError
import sirgl.simple.vm.driver.SourceFile


class UnknownLexemeError(
    val lexeme: Lexeme,
    override val sourceFile: SourceFile?
) : CompilationError {
    override val text = "Unknown lexeme: $lexeme"
}