package sirgl.simple.vm.inspections

import sirgl.simple.vm.FileTestCase
import sirgl.simple.vm.analysis.ProblemHolderImpl
import sirgl.simple.vm.analysis.SemanticAnalysisPass
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.codegen.CodegenPass
import sirgl.simple.vm.defaultInspections
import sirgl.simple.vm.driver.CompileJob
import sirgl.simple.vm.driver.phases.AstBuildingPhase
import sirgl.simple.vm.driver.phases.AstBypassesPhase
import sirgl.simple.vm.driver.phases.passes.SetupPass
import sirgl.simple.vm.roots.InMemorySourceFileSource
import sirgl.simple.vm.roots.ListSymbolSourceProvider


abstract class SemanticAnalysisTestBase : FileTestCase<String>() {
    override fun applyAction(text: String): String {
        val job = CompileJob(listOf(
            ListSymbolSourceProvider(
                listOf(
                    InMemorySourceFileSource(text)
                )
            )
        ), {
            listOf(
                AstBuildingPhase(),
                AstBypassesPhase(
                    walker = SimpleWalker(),
                    passes = mutableListOf(
                        SetupPass(),
                        SemanticAnalysisPass(
                            inspections = defaultInspections(ProblemHolderImpl(it.errorSink))
                        ),
                        CodegenPass()
                    )
                )
            )
        })
        job.run()
        return job.errorSink.errors.joinToString("\n") { it.text }
    }

}