package sirgl.simple.vm.common

abstract class CompilerPhase {
    abstract val name: String

    abstract fun run(context: CompilerContext)
}