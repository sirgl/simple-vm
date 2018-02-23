package sirgl.simple.vm.ast

import sirgl.simple.vm.scope.Scope

interface LangClass : AstNode, Scope {
    override val parent: LangFile
    val simpleName: String
    val qualifiedName: String
    val parentClassName: String?
    val members: List<LangMember>
}