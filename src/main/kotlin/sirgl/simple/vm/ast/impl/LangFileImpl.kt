package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangPackageDecl
import sirgl.simple.vm.ast.visitor.LangVisitor

class LangFileImpl(
        override val packageDeclaration: LangPackageDecl?,
        override val classDecl: LangClass) : AstNodeImpl(
        packageDeclaration?.startOffset ?: classDecl.startOffset,
        classDecl.endOffset
), LangFile {
    override val parent: AstNode? = null

    override fun accept(visitor: LangVisitor) {
        visitor.visitFile(this)
    }

    override val debugName = "File"
}