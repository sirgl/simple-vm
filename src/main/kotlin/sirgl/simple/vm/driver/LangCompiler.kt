package sirgl.simple.vm.driver

import mu.KotlinLogging
import sirgl.simple.vm.Configuration
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.driver.phases.AstBuildingPhase
import sirgl.simple.vm.driver.phases.DiscoveryPhase
import sirgl.simple.vm.lexer.HandwrittenLangLexer
import sirgl.simple.vm.lexer.LangLexer
import sirgl.simple.vm.parser.HandwrittenLangParser
import sirgl.simple.vm.parser.LangParser
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
    val lexer: LangLexer = HandwrittenLangLexer()
    val parser: LangParser = HandwrittenLangParser()
    val astCache = AstCache()
    val errorSink = ErrorSink()

    fun run() {
        val context = CompilerContext(AstBuilder(astCache, errorSink), astCache, configuration)


        for (phase in phases) {
            val phaseName = phase.name
            val timeMillis = measureTimeMillis {
                phase.run(context)
            }
            println("Phase $phaseName finished in $timeMillis ms")
        }
    }
}