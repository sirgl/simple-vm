package sirgl.simple.vm.ast

import sirgl.simple.vm.resolve.Scoped
import sirgl.simple.vm.roots.SymbolSource

interface LangFile : AstNode, Scoped {
    val classDecl: LangClass
    val packageDeclaration: LangPackageDecl?
    val symbolSource: SymbolSource
//    val path: Path
    // TODO think about error handling
}