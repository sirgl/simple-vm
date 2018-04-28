package sirgl.simple.vm.inspections

import sirgl.simple.vm.Configuration
import sirgl.simple.vm.FileTestCase
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.driver.AstBuilder
import sirgl.simple.vm.driver.ErrorSink
import sirgl.simple.vm.driver.ResolveCache
import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.driver.phases.AstBuildingPhase
import sirgl.simple.vm.driver.phases.SemanticAnalysisPhase
import sirgl.simple.vm.driver.phases.SetupPhase
import sirgl.simple.vm.runCompiler

abstract class SemanticAnalysisTestBase : FileTestCase<String>() {


    override fun applyAction(text: String): String {
        val resolveCache = ResolveCache()

        val errorSink = ErrorSink()

        val astBuilder = AstBuilder(resolveCache, errorSink)
        runCompiler(
            CompilerContext(
                astBuilder,
                resolveCache,
                Configuration("", "Main"),
                sourceFiles = listOf(
                    SourceFile("__DUMMY__.lang", { text.byteInputStream() })
                ),
                errorSink = errorSink
            ),
            phases = listOf(
                AstBuildingPhase(),
                SetupPhase(),
                SemanticAnalysisPhase()
            )
        )
        return errorSink.errors.joinToString("\n") { it.text }
    }

}