package sirgl.simple.vm.driver

import sirgl.simple.vm.codegen.CodegenOutputStrategy
import sirgl.simple.vm.codegen.DefaultCodegenStrategy
import sirgl.simple.vm.common.AstCache
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.roots.SymbolSourceProvider
import kotlin.system.measureTimeMillis

class CompileJob(
        val sourceProviders: MutableList<SymbolSourceProvider>,
        val buildPipeline: (context: CompilerContext) -> List<CompilerPhase<*>>,
        val codegenOutputStrategy: CodegenOutputStrategy = DefaultCodegenStrategy()
) {
    val astCache = AstCache()
    val errorSink = ErrorSink()
    val globalScope = GlobalScope()

    // Would be nice to return some meaningful result
    fun run() {
        val context = CompilerContext(
                AstBuilder(globalScope, errorSink, astCache),
                astCache,
                globalScope,
                errorSink,
                sourceProviders,
                codegenOutputStrategy
        )
        val phases = buildPipeline(context)
        run(context, phases)
    }

    private fun run(context: CompilerContext, phases: List<CompilerPhase<*>>) = runCompiler(context, phases)

}

fun runCompiler(context: CompilerContext, phases: List<CompilerPhase<*>>) {
    for (phase in phases) {
        val phaseName = phase.descriptor.name
        val timeMillis = measureTimeMillis {
            phase.run(context)
        }
        // TODO handle errors in every phase
        println("Phase '$phaseName' finished in $timeMillis ms")
        if (context.errorSink.hasErrors) {
            println("Aborting, due to errors:")
            println(context.errorSink.errors.joinToString("\n"))
            return
        }
    }
}