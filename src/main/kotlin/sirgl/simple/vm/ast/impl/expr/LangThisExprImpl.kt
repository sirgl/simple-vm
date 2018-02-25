package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangThisExpr
import sirgl.simple.vm.ast.ext.rangeText
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangThisExprImpl(
        lexeme: Lexeme
) : LangThisExpr, LangLeafExprImpl(lexeme.startOffset, lexeme.endOffset) {
    override fun toString() = "ThisExpr$rangeText"

    override fun accept(visitor: LangVisitor) = visitor.visitThisExpr(this)

    override val debugName = "ThisExpr"


}