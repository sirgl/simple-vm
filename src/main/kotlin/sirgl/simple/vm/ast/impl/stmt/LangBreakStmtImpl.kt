package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.stmt.LangBreakStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangBreakStmtImpl(
        startLexeme: Lexeme,
        endLexeme: Lexeme
) : LangStmtImpl(startLexeme, endLexeme), LangBreakStmt {
    override val debugName = "BreakStmt"

    override val children = emptyList<AstNode>()

    override fun accept(visitor: LangVisitor) {
        visitor.visitBreakStmt(this)
    }
}