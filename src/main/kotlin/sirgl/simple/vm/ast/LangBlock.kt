package sirgl.simple.vm.ast

import sirgl.simple.vm.scope.Scope

interface LangBlock : AstNode, Scope {
    val statements: Array<LangStmt>
}