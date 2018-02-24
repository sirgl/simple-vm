package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.stmt.LangWhileStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangWhileStmtImpl(
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val condition: LangExpr,
        override val block: LangBlock

) : LangStmtImpl(startLexeme, endLexeme), LangWhileStmt {
    override fun accept(visitor: LangVisitor) {
        visitor.visitWhileStmt(this)
    }

    override val debugName = "WhileStmt"

    override val children = makeChildren()

    private fun makeChildren() = listOf(condition, block)
}