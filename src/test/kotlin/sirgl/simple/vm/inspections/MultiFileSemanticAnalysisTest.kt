package sirgl.simple.vm.inspections

import org.junit.Test
import sirgl.simple.vm.MultiFileProjectTest
import sirgl.simple.vm.roots.InMemorySourceFileSource
import java.nio.file.Path

class MultiFileSemanticAnalysisTest : MultiFileProjectTest(MultiFileSemanticAnalysisTest::class.java.classLoader.getResource("multifile").file) {
    override fun apply(sourcePaths: List<Path>): String {
        return runCompilerJobAndGetErrors(sourcePaths.map { InMemorySourceFileSource(it.toFile().readText()) })
    }

    @Test
    fun testX() = test("asdasd")
}