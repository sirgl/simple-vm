package sirgl.simple.vm.ast.impl.stmt

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangCatchClause
import sirgl.simple.vm.ast.stmt.LangTryStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangTryStmtImpl(
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val tryBlock: LangBlock,
        override val catchBlocks: List<LangCatchClause>
) : LangStmtImpl(startLexeme, endLexeme), LangTryStmt {
    override val debugName = "TryStmt"

    override val children = makeChildren()

    private fun makeChildren(): List<AstNode> {
        val nodes = mutableListOf<AstNode>()
        nodes.add(tryBlock)
        nodes.addAll(catchBlocks)
        return nodes
    }

    override fun accept(visitor: LangVisitor) {
        visitor.visitTryStmt(this)
    }
}