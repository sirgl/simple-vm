package sirgl.simple.vm.ast

import sirgl.simple.vm.roots.SourceFileSource

interface LangFile : AstNode {
    val classDecl: LangClass
    val packageDeclaration: LangPackageDecl?
    val symbolSource: SourceFileSource
    val imports: List<LangImport>
}