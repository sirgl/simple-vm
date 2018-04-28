package sirgl.simple.vm.ast

import sirgl.simple.vm.resolve.Scoped

interface LangBlock : AstNode, Scoped {
    override val parent: AstNode
    val stmts: List<LangStmt>
}