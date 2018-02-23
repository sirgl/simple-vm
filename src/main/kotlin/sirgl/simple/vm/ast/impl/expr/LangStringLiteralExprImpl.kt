package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangStringLiteralExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangStringLiteralExprImpl(
        override val value: String,
        startOffset: Int,
        endOffset: Int
) : LangStringLiteralExpr, LangLeafExprImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    override fun toString() = "StringLiteral$rangeText(value: $value)"

    override fun accept(visitor: LangVisitor) = visitor.visitStringLiteralExpr(this)
}