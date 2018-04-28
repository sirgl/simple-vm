package sirgl.simple.vm.ast

interface LangReferenceElement : AstNode {
    val qualifier: LangReferenceElement?
    val name: String
    val fullName: String
}

fun LangReferenceElement.getFullName(): String {
    return qualifier?.getFullName() ?: ""+name
}