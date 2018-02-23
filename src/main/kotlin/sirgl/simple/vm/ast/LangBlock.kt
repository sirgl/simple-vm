package sirgl.simple.vm.ast

import sirgl.simple.vm.scope.Scope

interface LangBlock : AstNode, Scope {
    override val parent: AstNode
    val statements: Array<LangStmt>
}