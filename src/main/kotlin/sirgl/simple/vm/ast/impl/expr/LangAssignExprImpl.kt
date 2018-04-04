package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangAssignExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.type.LangType

class LangAssignExprImpl(
        startOffset: Int,
        endOffset: Int,
        line: Int,
        override val leftRef: LangExpr,
        override val rightValue: LangExpr
) : LangAssignExpr, LangExprImpl(startOffset, endOffset, line) {
    override val type: LangType by lazy { rightValue.type }

    override fun accept(visitor: LangVisitor) {
        visitor.visitAssignExpr(this)
    }

    override val debugName = "BinaryExpr"

    override val children = listOf(leftRef, rightValue)

}