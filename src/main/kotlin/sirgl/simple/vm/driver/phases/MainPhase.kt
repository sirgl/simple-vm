package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.ast.bypass.AstWalker
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.common.PhaseDescriptor

class MainPhase(
    val walker: AstWalker,
    private val passes: MutableList<AstPass> = mutableListOf()
) : CompilerPhase<MainPhase>() {

    fun addPass(astPass: AstPass) {
        passes.add(astPass)
    }

    override fun run(context: CompilerContext) {
        for (source in context.astCache.getAllSources()) {
            println("Processing file ${source.sourceFileSource.path}")
            for (pass in passes) {
                pass.doPass(source.file, walker)
            }
        }
    }

    override val descriptor = Companion

    companion object : PhaseDescriptor<MainPhase>("Main phase")
}