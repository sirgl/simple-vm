package sirgl.simple.vm.ast

interface LangPackageDecl : AstNode {
    val referenceElement: LangReferenceElement
    override val parent: LangFile
}