package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.stmt.LangReturnStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangReturnStmtImpl(
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val expression: LangExpr? = null
) : LangStmtImpl(startLexeme, endLexeme), LangReturnStmt {
    override val debugName = "ReturnStmt"

    override val children = makeChildren()

    private fun makeChildren() = when (expression) {
        null -> emptyList()
        else -> listOf(expression)
    }

    override fun accept(visitor: LangVisitor) {
        visitor.visitReturnStmt(this)
    }
}