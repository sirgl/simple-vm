package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangStringLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangStringLiteralExprImpl(
        override val value: String,
        lexeme: Lexeme
) : LangStringLiteralExpr, LangLeafExprImpl(lexeme) {
    override fun accept(visitor: LangVisitor) = visitor.visitStringLiteralExpr(this)

    override val debugName = "StringLiteral"

    override fun toString() = super.toString() + " value: $value"
}