package sirgl.simple.vm

import mu.KotlinLogging
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.driver.AstBuilder
import sirgl.simple.vm.driver.ErrorSink
import sirgl.simple.vm.driver.ResolveCache
import sirgl.simple.vm.driver.phases.AstBuildingPhase
import sirgl.simple.vm.driver.phases.DiscoveryPhase
import kotlin.system.measureTimeMillis

private val log = KotlinLogging.logger {}

private val defaultPhases = listOf(
        DiscoveryPhase(),
        AstBuildingPhase()
)

class LangCompiler(
        val configuration: Configuration,
        val phases: List<CompilerPhase> = defaultPhases
) {
    val astCache = ResolveCache()
    val errorSink = ErrorSink()

    fun run() {
        val context = CompilerContext(AstBuilder(astCache, errorSink), astCache, configuration, errorSink)


        runCompiler(context, phases)
    }

}

fun runCompiler(context: CompilerContext, phases: List<CompilerPhase>) {
    for (phase in phases) {
        val phaseName = phase.name
        val timeMillis = measureTimeMillis {
            phase.run(context)
        }
        // TODO handle errors in every phase
        println("Phase $phaseName finished in $timeMillis ms")
    }
}
