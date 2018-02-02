package sirgl.simple.vm.ast

import sirgl.simple.vm.ast.visitor.LangVisitor

interface AstNode {
    val children: List<AstNode>
    val parent: AstNode?
    val startOffset: Int
    val endOffset: Int
    fun accept(visitor: LangVisitor)
}