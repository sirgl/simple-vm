package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangBlock
import sirgl.simple.vm.ast.LangStmt
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.lexer.Lexeme
import sirgl.simple.vm.resolve.LocalScope
import sirgl.simple.vm.resolve.Scope

class LangBlockImpl(
    override val stmts: List<LangStmt>,
    val lBrace: Lexeme,
    val rBrace: Lexeme
) : AstNodeImpl(lBrace, rBrace), LangBlock {
    override val scope: Scope = LocalScope(this)


    override lateinit var parent: AstNode

    override fun accept(visitor: LangVisitor) {
        visitor.visitBlock(this)
    }

    override val debugName = "Block"

    override val children: List<AstNode> = stmts
}