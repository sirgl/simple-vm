package sirgl.simple.vm

import sirgl.simple.vm.analysis.ProblemHolderImpl
import sirgl.simple.vm.analysis.TypeCheckInspection
import sirgl.simple.vm.analysis.SemanticAnalysisPass
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.codegen.CodegenPass
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.driver.CompileJob
import sirgl.simple.vm.driver.phases.AstBuildingPhase
import sirgl.simple.vm.driver.phases.MainPhase
import sirgl.simple.vm.driver.phases.passes.SetupPass
import sirgl.simple.vm.roots.FileSystemSymbolSourceProvider
import java.nio.file.Paths

fun buildDefaultPipeline(context: CompilerContext) : List<CompilerPhase<*>> = listOf(
    AstBuildingPhase(),
    MainPhase(
        walker = SimpleWalker(),
        passes = mutableListOf(
            SetupPass(),
            SemanticAnalysisPass(
                inspections = mutableListOf(
                    TypeCheckInspection(ProblemHolderImpl(context.errorSink))
                )
            ),
            CodegenPass()
        )
    )
)

class LangCompiler(
    val configuration: Configuration,
    val buildPipeline: (context: CompilerContext) -> List<CompilerPhase<*>>
) {
    // Would be nice to return some meaningful result
    fun run() {
        val sourceProvider = FileSystemSymbolSourceProvider(listOf(Paths.get(configuration.sourcePath)))
        val compileJob = CompileJob(listOf(sourceProvider), buildPipeline)
        compileJob.run()
    }
}
