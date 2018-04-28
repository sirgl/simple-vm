package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.CompilationError
import sirgl.simple.vm.driver.ErrorSink
import sirgl.simple.vm.driver.SourceFile

interface LangInspection {
    val errorHolder: ErrorHolder
    val visitor: LangVisitor
}

interface ErrorHolder {
    fun registerProblem(node: AstNode, description: String)
}

class ErrorHolderImpl(
    private val errorSink: ErrorSink,
    private val sourceFile: SourceFile
) : ErrorHolder {
    override fun registerProblem(node: AstNode, description: String) {
        errorSink.submitError(SemanticError(node, description, sourceFile))
    }
}

class SemanticError(
    private val node: AstNode,
    private val comment: String,
    override val sourceFile: SourceFile?
) : CompilationError {
    override val text: String
        get() = "Error in ($node): $comment"

}