package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase

class AstBuildingPhase : CompilerPhase() {
    override val name = "Ast Building"

    override fun run(context: CompilerContext) {
        for (source in context.sourceFiles) {
            context.astBuilder.submit(source)
        }
        context.resolveCache.complete()
    }

}