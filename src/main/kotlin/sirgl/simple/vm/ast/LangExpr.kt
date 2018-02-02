package sirgl.simple.vm.ast

import sirgl.simple.vm.type.LangType

interface LangExpr : AstNode {
    val promoteToType: LangType?
    val type: LangType
}