package sirgl.simple.vm.common

class Phases(private val phases: MutableList<CompilerPhase<*>> = mutableListOf()) {
    fun addToTail(phase: CompilerPhase<*>) {
        phases.add(phase)
    }

    fun addAfter(phase: CompilerPhase<*>, descriptor: PhaseDescriptor<*>) {
        phases.add(phases.indexOfFirst { it.descriptor == descriptor } + 1, phase)
    }
}