package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.stmt.LangExprStmt
import sirgl.simple.vm.lexer.Lexeme

class LangExprStmtImpl(
        startOffset: Int,
        endOffset: Int,
        override val expr: LangExpr
) : LangStmtImpl(startOffset, endOffset), LangExprStmt {
    override val debugName = "ExprStmt"

    override val children = listOf(expr)
}