package sirgl.simple.vm.driver.phases

import sirgl.simple.vm.ast.bypass.SimpleWalker
import sirgl.simple.vm.common.CompilerContext
import sirgl.simple.vm.common.CompilerPhase
import sirgl.simple.vm.common.PhaseDescriptor
import sirgl.simple.vm.driver.phases.passes.SetupMethodReferencesPass
import sirgl.simple.vm.driver.phases.passes.SetupTopLevelPass
import sirgl.simple.vm.roots.SymbolSourceProvider

class SetupMethodRefernecesSymbolsPhase : CompilerPhase<SetupMethodRefernecesSymbolsPhase>() {
    override val descriptor = Companion

    override fun run(context: CompilerContext) {
        for (source in context.astCache.getAllSources()) {
            val path = source.sourceFileSource.path
            if (path != null) {
                println("Processing file $path")
            }
            val pass = SetupMethodReferencesPass()
            val walker = SimpleWalker()
            pass.doPostPass(source.file, walker)
        }
    }

    companion object : PhaseDescriptor<SetupMethodRefernecesSymbolsPhase>("Setup top level")
}