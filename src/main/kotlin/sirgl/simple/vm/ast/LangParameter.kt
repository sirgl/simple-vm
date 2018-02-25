package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.support.LangVarDecl

interface LangParameter : LangVarDecl, AstNode {
    override val parent: AstNode
}