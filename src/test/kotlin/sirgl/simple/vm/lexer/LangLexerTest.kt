package sirgl.simple.vm.lexer

import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import java.nio.file.Paths

class LangLexerTest : LexerBaseTest() {
    override val relativePath: Path = Paths.get("lexer")

    @TestFactory
    fun runTests() = getAllTests()

}