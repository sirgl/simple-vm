package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangParenExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

class LangParenExprImpl(
        lParen: Lexeme,
        rParen: Lexeme,
        override val expr: LangExpr
) : LangParenExpr, LangExprImpl(lParen, rParen) {
    override val type: LangType
        get() = expr.type

    override fun accept(visitor: LangVisitor) {
        visitor.visitParenExpr(this)
    }

    override val debugName = "ParenExpr"

    override val children: List<AstNode> = listOf(expr)
}