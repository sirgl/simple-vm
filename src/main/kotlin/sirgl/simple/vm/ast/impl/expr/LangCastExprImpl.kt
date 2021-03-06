package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.LangTypeElement
import sirgl.simple.vm.ast.expr.LangCastExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

class LangCastExprImpl(
        last: Lexeme,
        override val expr: LangExpr,
        override val targetTypeElement: LangTypeElement
) : LangCastExpr, LangExprImpl(expr.startOffset, last.endOffset, expr.startLine) {
    override val type: LangType = targetType

    override val targetType: LangType
        get() = targetTypeElement.type

    override fun accept(visitor: LangVisitor) {
        visitor.visitCastExpr(this)
    }

    override val debugName = "CastExpr"

    override fun toString() = super.toString() + " type: ${targetType.name}"

    override val children: List<AstNode> = listOf(expr)
}