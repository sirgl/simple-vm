package sirgl.simple.vm.lexer

import sirgl.simple.vm.FileTestCase

abstract class LexerBaseTest : FileTestCase<String>() {
    private val lexer = HandwrittenLangLexer()

    override fun applyAction(text: String): String {
        return lexer.tokenize(text).joinToString("\n")
    }

}