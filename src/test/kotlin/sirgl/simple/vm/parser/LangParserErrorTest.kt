package sirgl.simple.vm.parser

import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import java.nio.file.Paths

class LangParserErrorTest : ParserBaseTest() {
    override val failExpected = true

    override val relativePath: Path = Paths.get("parser/errors")

    @TestFactory
    fun runTests() = getAllTests()

}