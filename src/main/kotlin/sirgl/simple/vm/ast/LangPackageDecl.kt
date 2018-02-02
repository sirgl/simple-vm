package sirgl.simple.vm.ast

interface LangPackageDecl : AstNode {
    val declaredPackage: String
    override val parent: LangFile
}