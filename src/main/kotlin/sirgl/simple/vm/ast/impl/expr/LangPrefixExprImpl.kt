package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangPrefixExpr
import sirgl.simple.vm.ast.expr.PrefixOperatorType
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme

class LangPrefixExprImpl(
        startLexeme: Lexeme,
        endLexeme: Lexeme,
        override val expr: LangExpr,
        override val prefixOperatorType: PrefixOperatorType
) : LangExprImpl(startLexeme.startOffset, endLexeme.endOffset, startLexeme.line), LangPrefixExpr {

    override fun accept(visitor: LangVisitor) {
        visitor.visitPrefixExpr(this)
    }

    override val debugName = "PrefixExpr"

    override val children = listOf<AstNode>(expr)

    override fun toString() = super.toString() + " op: ${prefixOperatorType.representation}"
}
