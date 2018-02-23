package sirgl.simple.vm.parser

import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import java.nio.file.Paths

class LangParserTest : ParserBaseTest() {
    override val relativePath: Path = Paths.get("parser")

    @TestFactory
    fun runTests() = getAllTests()

}