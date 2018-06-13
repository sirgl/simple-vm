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
import sirgl.simple.vm.driver.phases.StdLibInjectionPhase
import sirgl.simple.vm.driver.phases.SymbolInjectionPhase
import sirgl.simple.vm.driver.phases.passes.SetupPass
import sirgl.simple.vm.roots.FileSystemSymbolSourceProvider
import sirgl.simple.vm.roots.InMemorySourceFileSource
import sirgl.simple.vm.roots.ListSymbolSourceProvider
import java.nio.file.Paths


abstract class SemanticAnalysisTestBase : FileTestCase<String>() {
    override fun applyAction(text: String): String {
        val job = CompileJob(
            mutableListOf(
            ListSymbolSourceProvider(
                listOf(
                    InMemorySourceFileSource(text)
                )
            )
        ), {
            listOf(
                StdLibInjectionPhase(FileSystemSymbolSourceProvider(listOf(Paths.get(InMemorySourceFileSource::class.java.classLoader.getResource("stdlib").file)))),
                AstBuildingPhase(),
                SymbolInjectionPhase(),
                AstBypassesPhase(
                    walker = SimpleWalker(),
                    passes = mutableListOf(
                        SetupPass(),
                        SemanticAnalysisPass(
                            inspections = defaultInspections(ProblemHolderImpl(it.errorSink))
                        )
                    )
                )

            )
        })
        job.run()
        return job.errorSink.errors.joinToString("\n") { it.text }
    }

}