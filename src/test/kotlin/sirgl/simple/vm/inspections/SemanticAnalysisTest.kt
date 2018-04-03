package sirgl.simple.vm.inspections

import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import java.nio.file.Paths

class SemanticAnalysisTest : SemanticAnalysisTestBase() {
    override val relativePath: Path = Paths.get("semantics")

    @TestFactory
    fun runTests() = getAllTests()
}