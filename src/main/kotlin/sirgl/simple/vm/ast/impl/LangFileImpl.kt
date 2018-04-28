package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.AstNode
import sirgl.simple.vm.ast.LangClass
import sirgl.simple.vm.ast.LangFile
import sirgl.simple.vm.ast.LangPackageDecl
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.resolve.Scope
import sirgl.simple.vm.resolve.Scoped
import sirgl.simple.vm.roots.SymbolSource

class LangFileImpl(
    override val packageDeclaration: LangPackageDecl?,
    override val classDecl: LangClass
) : AstNodeImpl(
    packageDeclaration?.startOffset ?: classDecl.startOffset,
    classDecl.endOffset,
    packageDeclaration?.startLine ?: classDecl.startLine
), LangFile, Scoped {
    override lateinit var scope: Scope
    override lateinit var symbolSource: SymbolSource
    override val parent: AstNode? = null

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