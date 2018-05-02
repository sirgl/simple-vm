package sirgl.simple.vm.common

abstract class CompilerPhase<T> {
    abstract val descriptor: PhaseDescriptor<T>

    abstract fun run(context: CompilerContext)
}