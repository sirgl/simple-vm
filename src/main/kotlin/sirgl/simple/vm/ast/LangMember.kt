package sirgl.simple.vm.ast

interface LangMember : AstNode {
    override val parent: LangClass
    val enclosingClass: LangClass
}