package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.stmt.LangContinueStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangContinueStmtImpl(
        lexeme: Lexeme
) : LangStmtImpl(lexeme, lexeme), LangContinueStmt {
    override val debugName = "ContinueStmt"

    override val children = emptyList<AstNode>()

    override fun accept(visitor: LangVisitor) {
        visitor.visitContinueStmt(this)
    }
}