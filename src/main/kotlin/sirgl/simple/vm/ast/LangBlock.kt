package sirgl.simple.vm.ast

import sirgl.simple.vm.resolve.Scope

interface LangBlock : AstNode, Scope {
    override val parent: AstNode
    val stmts: List<LangStmt>
}