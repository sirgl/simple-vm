package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.BinaryOperatorType.*
import sirgl.simple.vm.ast.LangBinaryOperator
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangBinaryExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.type.BoolType
import sirgl.simple.vm.type.I32Type
import sirgl.simple.vm.type.LangType

class LangBinaryExprImpl(
        override val left: LangExpr,
        override val right: LangExpr,
        override val operator: LangBinaryOperator,
        startOffset: Int,
        endOffset: Int,
        line: Int
) : LangBinaryExpr, LangExprImpl(startOffset, endOffset, line) {
    override val type: LangType by lazy {
        when (operator.typeBinary) {
            Eq, Gt, Ge, Le, Lt -> BoolType
            else -> I32Type
        }
    }

    override fun accept(visitor: LangVisitor) = visitor.visitBinaryExpr(this)

    override val debugName = "BinaryExpr"

    override val children = listOf(left, operator, right)

}