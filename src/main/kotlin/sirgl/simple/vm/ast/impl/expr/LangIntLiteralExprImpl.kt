package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangIntLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangIntLiteralExprImpl(
        override val value: Int,
        lexeme: Lexeme
) : LangIntLiteralExpr, LangLeafExprImpl(lexeme) {
    override fun accept(visitor: LangVisitor) = visitor.visitIntLiteralExpr(this)

    override val debugName = "IntLiteral"

    override fun toString() = super.toString() + " value: $value"
}