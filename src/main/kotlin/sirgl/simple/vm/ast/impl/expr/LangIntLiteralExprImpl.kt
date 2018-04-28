package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangIntLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.I32Type

class LangIntLiteralExprImpl(
    override val value: Int,
    lexeme: Lexeme
) : LangIntLiteralExpr, LangLeafExprImpl(lexeme) {
    override val type = I32Type

    override fun accept(visitor: LangVisitor) = visitor.visitIntLiteralExpr(this)

    override val debugName = "IntLiteral"

    override fun toString() = super.toString() + " value: $value"
}