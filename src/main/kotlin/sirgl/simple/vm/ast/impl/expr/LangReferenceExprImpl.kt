package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangReferenceExprImpl(
        nameLexeme: Lexeme,
        override val name: String,
        override val qualifier: LangExpr? = null
) : LangReferenceExpr, LangExprImpl(nameLexeme.startOffset, qualifier?.endOffset ?: nameLexeme.endOffset) {
    override fun accept(visitor: LangVisitor) = visitor.visitReferenceExpr(this)

    override val debugName = "ReferenceExpr"

    override fun toString() = super.toString() + " name: $name"

    override val children: List<AstNode> = when (qualifier) {
        null -> emptyList()
        else -> listOf(qualifier)
    }
}