package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangCharLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.I8Type
import sirgl.simple.vm.type.LangType

class LangCharLiteralExprImpl(
        override val value: Byte,
        lexeme: Lexeme
) : LangCharLiteralExpr, LangLeafExprImpl(lexeme) {
    override val type: LangType = I8Type

    override fun accept(visitor: LangVisitor) = visitor.visitCharLiteralExpr(this)

    override val debugName = "CharLiteral"

    override fun toString() = super.toString() + " value: $value"
}