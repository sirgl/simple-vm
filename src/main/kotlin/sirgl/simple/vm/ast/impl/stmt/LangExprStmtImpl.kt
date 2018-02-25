package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.stmt.LangExprStmt
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangExprStmtImpl(
        startOffset: Int,
        endOffset: Int,
        line: Int,
        override val expr: LangExpr
) : LangStmtImpl(startOffset, endOffset, line), LangExprStmt {
    override val debugName = "ExprStmt"

    override val children = listOf(expr)

    override fun accept(visitor: LangVisitor) {
        visitor.visitExprStmt(this)
    }
}