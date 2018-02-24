package sirgl.simple.vm.parser

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import java.nio.file.Paths

class LangParserCorrectTest : ParserBaseTest() {
    override val failExpected = false
    override val relativePath: Path = Paths.get("parser/correct")

    @Test fun testClass() = runSingle("class")
    @Test fun testExpr() = runSingle("expr")
    @Test fun testField() = runSingle("field")
    @Test fun testStmt() = runSingle("stmts")
    @Test fun testTry() = runSingle("try")
}