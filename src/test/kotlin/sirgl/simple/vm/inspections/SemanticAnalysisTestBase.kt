package sirgl.simple.vm.inspections

import sirgl.simple.vm.FileTestCase
import sirgl.simple.vm.analysis.ProblemHolderImpl
import sirgl.simple.vm.analysis.SemanticAnalysisPass
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.defaultInspections
import sirgl.simple.vm.driver.CompileJob
import sirgl.simple.vm.driver.phases.AstBuildingPhase
import sirgl.simple.vm.driver.phases.AstBypassesPhase
import sirgl.simple.vm.driver.phases.CommonTypesSetupPhase
import sirgl.simple.vm.driver.phases.StdLibInjectionPhase
import sirgl.simple.vm.driver.phases.passes.SetupPass
import sirgl.simple.vm.roots.FileSymbolSource
import sirgl.simple.vm.roots.FileSystemSymbolSourceProvider
import sirgl.simple.vm.roots.InMemorySourceFileSource
import sirgl.simple.vm.roots.ListSymbolSourceProvider
import java.nio.file.Paths


abstract class SemanticAnalysisTestBase : FileTestCase<String>() {
    override fun applyAction(text: String): String {
        return runCompilerJobAndGetErrors(
                listOf(
                        InMemorySourceFileSource(text)
                )
        )
    }
}

fun runCompilerJobAndGetErrors(sources: List<FileSymbolSource>): String {
    val job = CompileJob(
            mutableListOf(
                    ListSymbolSourceProvider(sources)
            ),
            {
                listOf(
                        StdLibInjectionPhase(
                                FileSystemSymbolSourceProvider(
                                        listOf(
                                                Paths.get(
                                                        InMemorySourceFileSource::class.java.classLoader.getResource(
                                                                "stdlib"
                                                        ).file
                                                )
                                        )
                                )
                        ),
                        AstBuildingPhase(),
                        CommonTypesSetupPhase(),
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