package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.ext.getParentOfClass

interface LangMember : AstNode {
    val enclosingClass: LangClass
        get() = getParentOfClass()
}