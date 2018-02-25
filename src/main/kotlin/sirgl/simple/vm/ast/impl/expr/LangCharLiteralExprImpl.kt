package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangCharLiteralExpr
import sirgl.simple.vm.ast.expr.LangStringLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangCharLiteralExprImpl(
        override val value: Byte,
        lexeme: Lexeme
) : LangCharLiteralExpr, LangLeafExprImpl(lexeme) {
    override fun accept(visitor: LangVisitor) = visitor.visitCharLiteralExpr(this)

    override val debugName = "CharLiteral"

    override fun toString() = super.toString() + " value: $value"
}