package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.ast.bypass.AstWalker
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.common.PhaseDescriptor

class AstBypassesPhase(
        val walker: AstWalker,
        private val passes: MutableList<AstPass> = mutableListOf()
) : CompilerPhase<AstBypassesPhase>() {

    fun addPass(astPass: AstPass) {
        passes.add(astPass)
    }

    override fun run(context: CompilerContext) {
        for (source in context.astCache.getAllSources()) {
            val path = source.sourceFileSource.path
            if (path != null) {
                println("Processing file $path")
            }
            for (pass in passes) {
                println("Running pass: ${pass.name}")
                pass.doPass(source.file, walker)
            }
        }
    }

    override val descriptor = Companion

    companion object : PhaseDescriptor<AstBypassesPhase>("AST bypasses")
}