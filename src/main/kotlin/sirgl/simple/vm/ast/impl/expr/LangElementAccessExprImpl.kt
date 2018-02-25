package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangCallExpr
import sirgl.simple.vm.ast.expr.LangElementAccessExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangElementAccessExprImpl(
        last: Lexeme,
        override val arrayExpr: LangExpr,
        override val indexExpr: LangExpr
) : LangElementAccessExpr, LangExprImpl(arrayExpr.startOffset, last.endOffset, arrayExpr.startLine) {
    override fun accept(visitor: LangVisitor) {
        visitor.visitElementAccessExpr(this)
    }

    override val debugName = "ElementAccessExpr"

    override val children: List<AstNode> = listOf(arrayExpr, indexExpr)
}