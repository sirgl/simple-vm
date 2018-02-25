package sirgl.simple.vm.ast

interface LangParameter : LangVarDecl, AstNode {
    override val parent: AstNode
}