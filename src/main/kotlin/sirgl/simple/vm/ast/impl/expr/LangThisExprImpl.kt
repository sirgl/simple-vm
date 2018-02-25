package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangThisExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangThisExprImpl(
        startOffset: Int,
        endOffset: Int
) : LangThisExpr, LangLeafExprImpl(startOffset, endOffset) {
    override fun toString() = "ThisExpr$rangeText"

    override fun accept(visitor: LangVisitor) = visitor.visitThisExpr(this)

    override val debugName = "ThisExpr"


}