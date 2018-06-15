package sirgl.simple.vm.inspections

import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

class SemanticAnalysisTest : SemanticAnalysisTestBase() {
    override val relativePath: Path = Paths.get("semantics")

    @Test
    fun calls() = runSingle("calls")

    @Test
    fun reference() = runSingle("reference")

    @Test
    fun resolve() = runSingle("resolve")

    @Test
    fun scope() = runSingle("scope")

    @Test
    fun typecheck() = runSingle("typecheck")

    @Test
    fun methods() = runSingle("methods")

    @Test
    fun breakContinue() = runSingle("breakContinue")
}