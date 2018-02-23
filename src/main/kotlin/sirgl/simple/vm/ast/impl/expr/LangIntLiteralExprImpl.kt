package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangIntLiteralExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangIntLiteralExprImpl(
        override val value: Int,
        startOffset: Int,
        endOffset: Int
) : LangIntLiteralExpr, LangLeafExprImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    override fun toString() = "IntLiteral$rangeText(value: $value)"

    override fun accept(visitor: LangVisitor) = visitor.visitIntLiteralExpr(this)

    override val debugName = "IntLiteral"
}