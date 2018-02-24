package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.stmt.LangIfStmt
import sirgl.simple.vm.ast.stmt.LangReturnStmt
import sirgl.simple.vm.lexer.Lexeme

class LangIfStmtImpl(
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val condition: LangExpr,
        override val thenBlock: LangBlock,
        override val elseBlock: LangBlock?
) : LangStmtImpl(startLexeme, endLexeme), LangIfStmt {
    override val debugName = "ReturnStmt"

    override val children = makeChildren()

    private fun makeChildren() = when (elseBlock) {
        null -> listOf(condition, thenBlock)
        else -> listOf(condition, thenBlock, elseBlock)
    }
}