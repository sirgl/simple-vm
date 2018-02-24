package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.expr.LangReferenceExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangReferenceExprImpl(
        lexeme: Lexeme,
        override val name: String
) : LangReferenceExpr, LangLeafExprImpl(lexeme) {
    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) = visitor.visitReferenceExpr(this)

    override val debugName = "ReferenceExpr"
}