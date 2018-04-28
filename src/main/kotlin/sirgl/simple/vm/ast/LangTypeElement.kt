package sirgl.simple.vm.ast

import sirgl.simple.vm.type.LangType

interface LangTypeElement : AstNode {
    val type: LangType
    val reference: LangReferenceElement?
    val sort: LangTypeElementSort
    val coreType: LangTypeElement?
}