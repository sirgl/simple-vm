package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.common.PhaseDescriptor
import sirgl.simple.vm.roots.SourceFileSource

class AstBuildingPhase : CompilerPhase<AstBuildingPhase>() {
    override val descriptor: PhaseDescriptor<AstBuildingPhase> = Companion

    override fun run(context: CompilerContext) {
        for (sourceProvider in context.sourceProviders) {
            for (fileSymbolSource in sourceProvider.findSources()) {
                // TODO here I need to handle not only source files
                if (fileSymbolSource is SourceFileSource) {
                    context.astBuilder.submit(fileSymbolSource)
                }
            }
        }
    }

    companion object : PhaseDescriptor<AstBuildingPhase>("AST building")
}