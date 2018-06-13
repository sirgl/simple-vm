package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.common.PhaseDescriptor
import sirgl.simple.vm.roots.SymbolSourceProvider

class StdLibInjectionPhase(val stdLibSourceProvider: SymbolSourceProvider) : CompilerPhase<StdLibInjectionPhase>() {
    override val descriptor = Companion

    override fun run(context: CompilerContext) {
        context.sourceProviders.add(stdLibSourceProvider)
    }

    companion object : PhaseDescriptor<StdLibInjectionPhase>("Stdlib injection")
}