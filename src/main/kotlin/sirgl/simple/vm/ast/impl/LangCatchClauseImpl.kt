package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangCatchClause
import sirgl.simple.vm.ast.LangParameter
import sirgl.simple.vm.ast.stmt.LangTryStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangCatchClauseImpl(
    startLexeme: Lexeme,
    endLexeme: Lexeme,
    override val parameter: LangParameter,
    override val block: LangBlock
) : AstNodeImpl(startLexeme, endLexeme), LangCatchClause {
    override fun accept(visitor: LangVisitor) {
        visitor.visitCatchClause(this)
    }

    override lateinit var parent: LangTryStmt

    override val debugName = "CatchClause"

    override val children: List<AstNode> = listOf(parameter, block)
}