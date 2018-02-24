package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangIntLiteralExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangIntLiteralExprImpl(
        override val value: Int,
        lexeme: Lexeme
) : LangIntLiteralExpr, LangLeafExprImpl(lexeme) {
    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) = visitor.visitIntLiteralExpr(this)

    override val debugName = "IntLiteral"
}