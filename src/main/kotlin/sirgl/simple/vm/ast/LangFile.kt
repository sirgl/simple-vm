package sirgl.simple.vm.ast

import sirgl.simple.vm.resolve.Scoped
import sirgl.simple.vm.roots.SourceFileSource
import sirgl.simple.vm.roots.SymbolSource

interface LangFile : AstNode {
    val classDecl: LangClass
    val packageDeclaration: LangPackageDecl?
    val symbolSource: SourceFileSource
    val imports: List<LangImport>
}