package sirgl.simple.vm.ast

import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.resolve.Scope

interface LangFile : AstNode, Scope {
    val classDecl: LangClass
    val packageDeclaration: LangPackageDecl?
    val sourceFile: SourceFile
//    val path: Path
    // TODO think about error handling
}