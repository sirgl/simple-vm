package sirgl.simple.vm.ast.impl

import sirgl.simple.vm.ast.*
import sirgl.simple.vm.ast.visitor.LangVisitor
import sirgl.simple.vm.roots.SourceFileSource

class LangFileImpl(
    override val packageDeclaration: LangPackageDecl?,
    override val classDecl: LangClass,
    override val imports: List<LangImport>
) : AstNodeImpl(
    packageDeclaration?.startOffset ?: classDecl.startOffset,
    classDecl.endOffset,
    packageDeclaration?.startLine ?: classDecl.startLine
), LangFile {
    override lateinit var symbolSource: SourceFileSource
    override val parent: AstNode? = null

    override fun accept(visitor: LangVisitor) {
        visitor.visitFile(this)
    }

    override val debugName = "File"

    override val children: List<AstNode> by lazy { makeChildren() }

    private fun makeChildren(): List<AstNode> {
        val nodes = mutableListOf<AstNode>()
        if (packageDeclaration != null) {
            nodes.add(packageDeclaration)
        }
        nodes.addAll(imports)
        nodes.add(classDecl)
        return nodes
    }
}