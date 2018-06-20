package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.expr.LangStringLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.common.CommonClassTypes
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.ClassType

class LangStringLiteralExprImpl(
        override val value: String,
        lexeme: Lexeme
) : LangStringLiteralExpr, LangLeafExprImpl(lexeme) {
    override val type: ClassType
        get() = CommonClassTypes.LANG_STRING

    override fun accept(visitor: LangVisitor) = visitor.visitStringLiteralExpr(this)

    override val debugName = "StringLiteral"

    override fun toString() = super.toString() + " value: $value"
}