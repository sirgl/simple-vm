package sirgl.simple.vm.ast

import java.nio.file.Path

interface LangFile: AstNode {
    val classDecl: LangClass
    val packageDeclaration: LangPackageDecl?

//    val path: Path
    // TODO think about error handling
}