package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangAssignExpr
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangAssignExprImpl(
        startOffset: Int,
        endOffset: Int,
        override val leftRef: LangReferenceExpr,
        override val rightValue: LangExpr
) : LangAssignExpr, LangExprImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) {
        visitor.visitAssignExpr(this)
    }

    override val debugName = "BinaryExpr"

    override val children = listOf(leftRef, rightValue)

}