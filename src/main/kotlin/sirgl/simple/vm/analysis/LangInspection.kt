package sirgl.simple.vm.analysis

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.CompilationError
import sirgl.simple.vm.driver.ErrorSink
import sirgl.simple.vm.roots.SourceFileSource
import sirgl.simple.vm.roots.SymbolSource

interface LangInspection {
    val problemHolder: ProblemHolder
    val visitor: LangVisitor
}

interface ProblemHolder {
    fun registerProblem(node: AstNode, description: String)
}

class ProblemHolderImpl(
    private val errorSink: ErrorSink,
    private val sourceFile: SourceFileSource
) : ProblemHolder {
    override fun registerProblem(node: AstNode, description: String) {
        errorSink.submitError(SemanticError(node, description, sourceFile))
    }
}

class SemanticError(
    private val node: AstNode,
    private val comment: String,
    override val symbolSource: SymbolSource?
) : CompilationError {
    override val text: String
        get() = "Error in ($node): $comment"

}