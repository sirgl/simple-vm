package sirgl.simple.vm.ast

import sirgl.simple.vm.scope.Scope

interface LangMethod : LangMember, Scope {
    val name: String
    val parameters: Array<LangParameter>
    val block: LangBlock
}