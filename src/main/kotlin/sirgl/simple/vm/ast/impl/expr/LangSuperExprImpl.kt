package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangSuperExpr
import sirgl.simple.vm.ast.expr.LangThisExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangSuperExprImpl(
        startOffset: Int,
        endOffset: Int
) : LangSuperExpr, LangLeafExprImpl(startOffset, endOffset) {
    override fun toString() = "SuperExpr$rangeText"

    override fun accept(visitor: LangVisitor) = visitor.visitSuperExpr(this)
}