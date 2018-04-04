package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangPackageDecl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.driver.SourceFile
import sirgl.simple.vm.resolve.Scope

class LangFileImpl(
        var scope: Scope,
        override val packageDeclaration: LangPackageDecl?,
        override val classDecl: LangClass) : AstNodeImpl(
        packageDeclaration?.startOffset ?: classDecl.startOffset,
        classDecl.endOffset,
        packageDeclaration?.startLine ?: classDecl.startLine
), LangFile, Scope by scope {
    init {
        scope.element = this
    }

    override val parent: AstNode? = null
    override lateinit var sourceFile: SourceFile

    override fun accept(visitor: LangVisitor) {
        visitor.visitFile(this)
    }

    override val debugName = "File"

    override val children: List<AstNode> = makeChildren()
    private fun makeChildren() = when {
        packageDeclaration != null -> listOf(packageDeclaration, classDecl)
        else -> listOf(classDecl)
    }
}