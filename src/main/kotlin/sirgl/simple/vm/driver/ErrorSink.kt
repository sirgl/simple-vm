package sirgl.simple.vm.driver

import sirgl.simple.vm.roots.SymbolSource

class ErrorSink {
    val errors = mutableListOf<CompilationError>()

    fun submitError(error: CompilationError) {
        synchronized(this) {
            errors.add(error)
        }
    }

    val hasErrors: Boolean
        get() = errors.isNotEmpty()
}

interface CompilationError {
    val text: String
    val symbolSource: SymbolSource?
}