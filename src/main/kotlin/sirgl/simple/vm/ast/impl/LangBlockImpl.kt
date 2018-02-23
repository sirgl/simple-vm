package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.scope.Scope

class LangBlockImpl(
        val scope: Scope,
        override val statements: Array<LangStmt>,
        val lBrace: Lexeme,
        val rBrace: Lexeme
) : AstNodeImpl(lBrace.startOffset, rBrace.endOffset), LangBlock, Scope by scope {
    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) {
        visitor.visitBlock(this)
    }

    override val debugName = "Block"
}