package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangElementAccessExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.ArrayType
import sirgl.simple.vm.type.LangType
import sirgl.simple.vm.type.UnknownType

class LangElementAccessExprImpl(
    last: Lexeme,
    override val arrayExpr: LangExpr,
    override val indexExpr: LangExpr
) : LangElementAccessExpr, LangExprImpl(arrayExpr.startOffset, last.endOffset, arrayExpr.startLine) {
    override val type: LangType by lazy {
        (arrayExpr.type as? ArrayType)?.elementType ?: UnknownType
    }

    override fun accept(visitor: LangVisitor) {
        visitor.visitElementAccessExpr(this)
    }

    override val debugName = "ElementAccessExpr"

    override val children: List<AstNode> = listOf(arrayExpr, indexExpr)
}