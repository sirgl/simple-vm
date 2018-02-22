package sirgl.simple.vm.lexer

import org.junit.jupiter.api.TestFactory
import sirgl.simple.vm.FileTestCase
import java.nio.file.Path
import java.nio.file.Paths

abstract class LexerBaseTest : FileTestCase<String>() {
    private val lexer = HandwrittenLangLexer()

    override fun applyAction(text: String): String {
        return lexer.tokenize(text).joinToString("\n")
    }

}