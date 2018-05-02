package sirgl.simple.vm

import mu.KotlinLogging
import sirgl.simple.vm.common.AstCache
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.driver.AstBuilder
import sirgl.simple.vm.driver.ErrorSink
import sirgl.simple.vm.driver.GlobalScope
import sirgl.simple.vm.driver.phases.AstBuildingPhase
import sirgl.simple.vm.roots.FileSystemSymbolSourceProvider
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

private val log = KotlinLogging.logger {}

private val defaultPhases: List<CompilerPhase<*>> = listOf(
    AstBuildingPhase()
)

class LangCompiler(
    val configuration: Configuration,
    val phases: List<CompilerPhase<*>> = defaultPhases
) {
    val astCache = AstCache()
    val errorSink = ErrorSink()
    val globalScope = GlobalScope()

    fun run() {
        FileSystemSymbolSourceProvider(listOf(Paths.get(configuration.sourcePath)))

        val context = CompilerContext(
            AstBuilder(globalScope, errorSink, astCache),
            astCache,
            globalScope,
            configuration,
            errorSink,
            listOf()
        )

        runCompiler(context, phases)
    }

}

fun runCompiler(context: CompilerContext, phases: List<CompilerPhase<*>>) {
    for (phase in phases) {
        val phaseName = phase.descriptor.name
        val timeMillis = measureTimeMillis {
            phase.run(context)
        }
        // TODO handle errors in every phase
        println("Phase $phaseName finished in $timeMillis ms")
    }
}
