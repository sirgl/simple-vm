package sirgl.simple.vm.ast.impl.expr

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangExpr
import sirgl.simple.vm.ast.expr.LangCallExpr
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.type.ClassType
import sirgl.simple.vm.type.LangType
import sirgl.simple.vm.type.MethodReferenceType
import sirgl.simple.vm.type.UnknownType

class LangCallExprImpl(
    last: Lexeme,
    override val caller: LangExpr,
    override val arguments: List<LangExpr>
) : LangCallExpr, LangExprImpl(caller.startOffset, last.endOffset, caller.startLine) {
    override val type: LangType by lazy {
        val callerType = caller.type
        when (callerType) {
            is MethodReferenceType -> callerType.methodSymbol.returnType
            is ClassType -> callerType.classSymbol.type
            else -> UnknownType
        }
    }

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