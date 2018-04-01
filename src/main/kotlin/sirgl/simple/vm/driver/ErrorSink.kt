package sirgl.simple.vm.driver


class ErrorSink {
    private val errors = mutableListOf<CompilationError>()
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
    val sourceFile: SourceFile?
}