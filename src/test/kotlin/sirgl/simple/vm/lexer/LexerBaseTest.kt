package sirgl.simple.vm.lexer

import org.junit.jupiter.api.TestFactory
import sirgl.simple.vm.FileTestCase
import java.nio.file.Path
import java.nio.file.Paths

class LexerBaseTest : FileTestCase<String>() {
    override val relativePath: Path = Paths.get("lexer")
    private val lexer = LangLexerHandwrittenImpl()

    override fun applyAction(text: String): String {
        return lexer.tokenize(text).joinToString("\n")
    }

    @TestFactory fun runTests() = getAllTests()
}