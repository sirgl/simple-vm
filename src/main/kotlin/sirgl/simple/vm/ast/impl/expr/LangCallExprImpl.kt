package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangCallExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.LangType

class LangCallExprImpl(
    last: Lexeme,
    override val caller: LangExpr,
    override val arguments: List<LangExpr>
) : LangCallExpr, LangExprImpl(caller.startOffset, last.endOffset, caller.startLine) {
    //    override val type: LangType by lazy { (caller.type as? ClassType)?.classSignature?.toType() ?: UnknownType }
    override val type: LangType by lazy { TODO() }

    override fun accept(visitor: LangVisitor) {
        visitor.visitCallExpr(this)
    }

    override val debugName = "CallExpr"

    override val children: List<AstNode> = makeChildren()

    private fun makeChildren(): List<AstNode> {
        val nodes = mutableListOf<AstNode>(caller)
        nodes.addAll(arguments)
        return nodes
    }
}