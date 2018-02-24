package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangBoolLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangBoolLiteralExprImpl(
        override val value: Boolean,
        lexeme: Lexeme
) : LangBoolLiteralExpr, LangLeafExprImpl(lexeme) {
    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) = visitor.visitBoolLiteralExpr(this)

    override val debugName = "BoolLiteral"

    override fun toString() = super.toString() + " value: $value"
}