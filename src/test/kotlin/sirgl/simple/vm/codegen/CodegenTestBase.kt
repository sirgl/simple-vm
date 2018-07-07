package sirgl.simple.vm.codegen

import sirgl.simple.vm.analysis.ProblemHolderImpl
import sirgl.simple.vm.analysis.SemanticAnalysisPass
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.defaultInspections
import sirgl.simple.vm.driver.CompileJob
import sirgl.simple.vm.driver.phases.*
import sirgl.simple.vm.driver.phases.passes.SetupReferencesPass
import sirgl.simple.vm.driver.phases.passes.SetupTopLevelPass
import sirgl.simple.vm.roots.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.file.Paths


class TestCodegenOutputStrategy : CodegenOutputStrategy {
    val stream = ByteArrayOutputStream()

    override fun getOutputType() = OutputType.TEXT

    override fun getOutputStream(sourceFileSource: SourceFileSource): OutputStream? {
        val path = sourceFileSource.path ?: return null
        if (!path.last().toString().contains("__Target")) return null
        return stream
    }

}

fun runCompilerJobAndGetErrors(sources: List<FileSymbolSource>): String {
    val strategy = TestCodegenOutputStrategy()
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
                        SetupTopLevelPhase(),
                        SetupMethodRefernecesSymbolsPhase(),
                        AstBypassesPhase(
                                walker = SimpleWalker(),
                                passes = mutableListOf(
                                        SetupReferencesPass(),
                                        SemanticAnalysisPass(
                                                inspections = defaultInspections(ProblemHolderImpl(it.errorSink))
                                        )
                                )
                        ),
                        CodegenPhase()

                )
            },
            strategy
    )
    job.run()
    return strategy.stream.toString()
}