package sirgl.simple.vm.common

interface Phase {
    fun run(context: CompilerContext)
    val phaseDescriptor: PhaseDescriptor
}