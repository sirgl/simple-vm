package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangBoolLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.BoolType

class LangBoolLiteralExprImpl(
    override val value: Boolean,
    lexeme: Lexeme
) : LangBoolLiteralExpr, LangLeafExprImpl(lexeme) {
    override val type = BoolType

    override fun accept(visitor: LangVisitor) = visitor.visitBoolLiteralExpr(this)

    override val debugName = "BoolLiteral"

    override fun toString() = super.toString() + " value: $value"
}