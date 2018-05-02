package sirgl.simple.vm.ast

interface LangImport : AstNode {
    val referenceElement: LangReferenceElement
    override val parent: LangFile
}