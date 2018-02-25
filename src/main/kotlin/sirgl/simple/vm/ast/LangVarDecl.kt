package sirgl.simple.vm.ast

import sirgl.simple.vm.type.LangType

interface LangVarDecl {
    val name: String
    val type: LangType
    val initializer: LangExpr?
}