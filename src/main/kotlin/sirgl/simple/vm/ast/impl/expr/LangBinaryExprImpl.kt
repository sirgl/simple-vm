package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBinaryOperator
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangBinaryExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangBinaryExprImpl(
        override val left: LangExpr,
        override val right: LangExpr,
        override val operator: LangBinaryOperator,
        startOffset: Int,
        endOffset: Int
) : LangBinaryExpr, LangExprImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) = visitor.visitBinaryExpr(this)

    override val debugName = "BinaryExpr"

    override val children = listOf(left, operator, right)

}