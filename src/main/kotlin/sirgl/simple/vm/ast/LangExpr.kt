package sirgl.simple.vm.ast

import sirgl.simple.vm.type.LangType

interface LangExpr : AstNode {
    var promoteToType: LangType?
    val type: LangType
    override val parent: AstNode
}