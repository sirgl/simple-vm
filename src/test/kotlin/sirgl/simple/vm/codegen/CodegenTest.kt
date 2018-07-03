package sirgl.simple.vm.codegen

import org.junit.jupiter.api.Test
import sirgl.simple.vm.MultiFileProjectTest
import sirgl.simple.vm.roots.FsSourceFileSource
import java.nio.file.Path


class CodegenTest : MultiFileProjectTest(TestCodegenOutputStrategy::class.java.classLoader.getResource("codegen").file) {
    override fun apply(sourcePaths: List<Path>): String {
        return runCompilerJobAndGetErrors(sourcePaths.map { FsSourceFileSource(it) })
    }

    @Test
    fun expr() = test("expr")
}