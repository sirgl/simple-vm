package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangNullExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.type.LangType
import sirgl.simple.vm.type.NullType

class LangNullExprImpl(
        startOffset: Int,
        endOffset: Int
) : LangNullExpr, LangLeafExprImpl(startOffset, endOffset) {
    override lateinit var parent: AstNode

    override var type: LangType = NullType

    override fun accept(visitor: LangVisitor) = visitor.visitNullExpr(this)

    override val debugName = "NullExpr"
}