package sirgl.simple.vm

import sirgl.simple.vm.analysis.*
import sirgl.simple.vm.analysis.inspections.ResolveInspection
import sirgl.simple.vm.analysis.inspections.ScopeInspection
import sirgl.simple.vm.analysis.inspections.SemanticAnalysisInspection
import sirgl.simple.vm.analysis.inspections.TypeCheckInspection
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.codegen.CodegenPass
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.driver.CompileJob
import sirgl.simple.vm.driver.phases.AstBuildingPhase
import sirgl.simple.vm.driver.phases.AstBypassesPhase
import sirgl.simple.vm.driver.phases.passes.SetupPass
import sirgl.simple.vm.roots.FileSystemSymbolSourceProvider
import java.nio.file.Paths

fun buildDefaultPipeline(context: CompilerContext) : List<CompilerPhase<*>> {
    val problemHolder = ProblemHolderImpl(context.errorSink)
    val inspections = defaultInspections(problemHolder)
    return listOf(
        AstBuildingPhase(),
        AstBypassesPhase(
            walker = SimpleWalker(),
            passes = mutableListOf(
                SetupPass(),
                SemanticAnalysisPass(
                    inspections = inspections
                ),
                CodegenPass()
            )
        )
    )
}

fun defaultInspections(problemHolder: ProblemHolderImpl): MutableList<LangInspection> = mutableListOf(
    TypeCheckInspection(problemHolder),
    ScopeInspection(problemHolder),
    ResolveInspection(problemHolder),
    SemanticAnalysisInspection(problemHolder)
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
