package sirgl.simple.vm

import sirgl.simple.vm.analysis.LangInspection
import sirgl.simple.vm.analysis.ProblemHolderImpl
import sirgl.simple.vm.analysis.SemanticAnalysisPass
import sirgl.simple.vm.analysis.inspections.ResolveInspection
import sirgl.simple.vm.analysis.inspections.ScopeInspection
import sirgl.simple.vm.analysis.inspections.SemanticAnalysisInspection
import sirgl.simple.vm.analysis.inspections.TypeCheckInspection
import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.codegen.CodegenPass
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.driver.CompileJob
import sirgl.simple.vm.driver.phases.*
import sirgl.simple.vm.driver.phases.passes.SetupReferencesPass
import sirgl.simple.vm.roots.FileSystemSymbolSourceProvider
import java.nio.file.Paths

fun buildDefaultPipeline(context: CompilerContext): List<CompilerPhase<*>> {
    val problemHolder = ProblemHolderImpl(context.errorSink)
    val inspections = defaultInspections(problemHolder)
    return listOf(
            // TODO stdlib
            AstBuildingPhase(),
            CommonTypesSetupPhase(),
            SetupTopLevelPhase(),
            SetupMethodRefernecesSymbolsPhase(),
            AstBypassesPhase(
                    walker = SimpleWalker(),
                    passes = mutableListOf(
                            SetupReferencesPass(),
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
        val compileJob = CompileJob(mutableListOf(sourceProvider), buildPipeline)
        compileJob.run()
    }
}
