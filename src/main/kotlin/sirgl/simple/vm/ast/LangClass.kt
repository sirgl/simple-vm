package sirgl.simple.vm.ast

interface LangClass : AstNode {
    override val parent: LangFile
    val simpleName: String
    val qualifiedName: String
}