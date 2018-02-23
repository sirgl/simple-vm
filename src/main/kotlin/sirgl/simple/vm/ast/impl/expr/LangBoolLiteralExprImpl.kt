package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangBoolLiteralExpr
import sirgl.simple.vm.ast.expr.LangIntLiteralExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangBoolLiteralExprImpl(
        override val value: Boolean,
        startOffset: Int,
        endOffset: Int
) : LangBoolLiteralExpr, LangExprImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    override fun toString() = "BoolLiteral$rangeText(value: $value)"

    override fun accept(visitor: LangVisitor) = visitor.visitBoolLiteralExpr(this)
}