package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangSuperExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangSuperExprImpl(
        lexeme: Lexeme
) : LangSuperExpr, LangLeafExprImpl(lexeme.startOffset, lexeme.endOffset) {
    override fun toString() = "SuperExpr$rangeText"

    override fun accept(visitor: LangVisitor) = visitor.visitSuperExpr(this)

    override val debugName = "SuperExpr"
}