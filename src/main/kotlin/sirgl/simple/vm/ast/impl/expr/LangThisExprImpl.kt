package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangThisExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangThisExprImpl(
        startOffset: Int,
        endOffset: Int
) : LangThisExpr, LangExprImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    override fun toString() = "ThisExpr$rangeText"

    override fun accept(visitor: LangVisitor) = visitor.visitThisExpr(this)
}