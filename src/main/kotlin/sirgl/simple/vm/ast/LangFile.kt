package sirgl.simple.vm.ast

interface LangFile : AstNode {
    val classDecl: LangClass
    val packageDeclaration: LangPackageDecl?

//    val path: Path
    // TODO think about error handling
}